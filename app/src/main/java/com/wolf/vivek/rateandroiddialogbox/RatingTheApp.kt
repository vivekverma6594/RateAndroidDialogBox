package com.wolf.vivek.rateandroiddialogbox

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

/**
 * Created by ${VivekVerma} on 03/03/18.
 */

object RatingTheApp {

    private const val DAYS_UNTIL_PROMPT = 0
    private const val LAUNCHES_UNTIL_PROMPT = 1

    fun onCreate(mContext: Context) {

        val sharedPref = mContext.getSharedPreferences(Constants.appRatingSP, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        if (sharedPref.getBoolean(Constants.KEY_OPT_OUT, false)) {
            return
        }

        var installDate = sharedPref.getLong(Constants.KEY_INSTALL_DATE, 0)
        if (installDate == 0L) {
            installDate = System.currentTimeMillis()
            editor.putLong(Constants.KEY_INSTALL_DATE, installDate)
        }

        var launchTimes = sharedPref.getLong(Constants.KEY_LAUNCH_TIMES, 0)
        launchTimes++
        editor.putLong(Constants.KEY_LAUNCH_TIMES, launchTimes)

        if (launchTimes >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= installDate + DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showRateDialog(mContext, editor)
            }
        }
        editor.apply()
    }

    private fun showRateDialog(mContext: Context, editor: SharedPreferences.Editor?) {
        val rateDialog = AlertDialog.Builder(mContext)
        rateDialog.setTitle("Rate Word Power!")
        rateDialog.setMessage("Would you like to rate this application?")
        rateDialog.setCancelable(false)

        rateDialog.setPositiveButton("Rate Now", {dialog, which ->
            val url = "market://details?id=" + mContext.packageName
            try {
                mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: android.content.ActivityNotFoundException) {
                mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())))
            }
            setOptOut(mContext, true)
        })
        rateDialog.setNegativeButton("No", {dialog, which ->
            setOptOut(mContext, true)
        })
        rateDialog.setNeutralButton("Later", {dialog, which ->
            val sharedPref = mContext.getSharedPreferences(Constants.appRatingSP, Context.MODE_PRIVATE)
            val neutralEditor = sharedPref.edit()
            neutralEditor.putLong(Constants.KEY_LAUNCH_TIMES, 0)
            neutralEditor.putLong(Constants.KEY_INSTALL_DATE, 0)
            neutralEditor.apply()
        })
        rateDialog.create().show()
    }

    private fun setOptOut(context: Context, optOut: Boolean) {
        val pref = context.getSharedPreferences(Constants.appRatingSP, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Constants.KEY_OPT_OUT, optOut)
        editor.apply()
    }

}