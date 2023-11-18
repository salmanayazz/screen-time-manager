package com.example.screentimemanager.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object Util {
    fun getApplicationsList (context: Context): List<ApplicationInfo>{

        /**
         * Returns a list of non-system applications installed on the user's phone.
         *
         * @param context The context of the application or activity.
         * @return A list of ApplicationInfo objects representing user-installed applications.
         */

        //TODO : this function is time consuming , it needs to be done inside a coroutines NOT UI threads
        val appsList = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val mutableList:MutableList<ApplicationInfo>  = mutableListOf()
        for (app in appsList){
            if (app.flags and  ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = app.loadLabel(context.packageManager).toString()
                if(appName.trim().isNotEmpty()){
                    mutableList.add(app)
                }
            }
        }
        return mutableList
    }
}