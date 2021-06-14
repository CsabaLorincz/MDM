package com.mdm.app.extension

import android.app.Activity
import android.content.pm.PackageInfo
import com.mdm.app.activities.MDMActivity

public fun Activity.allowed(packageInfo: PackageInfo):Boolean{
    val packageManager=(this as MDMActivity).packageManager
    val name=packageInfo.applicationInfo.loadLabel(packageManager).toString()
    if(MDMActivity.applications.applications.contains(name))
        return false
    return true
}