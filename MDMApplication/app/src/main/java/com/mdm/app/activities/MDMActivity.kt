package com.mdm.app.activities

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import com.mdm.app.API.Applications
import com.mdm.app.R
import com.mdm.app.receivers.AdminManager



class MDMActivity : AppCompatActivity() {
    var deviceManager: DevicePolicyManager? = null
    var compName: ComponentName? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestPermission()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        compName = ComponentName(this, AdminManager::class.java)
        deviceManager = getSystemService(
                Context.DEVICE_POLICY_SERVICE
        ) as DevicePolicyManager
        val list = packageManager.getInstalledPackages(0)
        setAll(list)
        if(!deviceManager!!.isAdminActive(compName!!)){
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            startActivityForResult(intent, 0)
        }

    }

    fun getComp():ComponentName?{
        return compName
    }

    fun getManager():DevicePolicyManager?{
        return deviceManager
    }


    companion object Data{
        var applications: Applications = Applications(mutableListOf())
        fun setData(applications: Applications){
            Data.applications = applications
        }

        private lateinit var allApps: MutableList<PackageInfo>
        fun setAll(applications: MutableList<PackageInfo> ){
            allApps = applications
        }
        fun getAllApps(): MutableList<PackageInfo>{
            return allApps
        }

        var isLoggedInAdmin=false
        fun setLogin(b: Boolean){
            isLoggedInAdmin =b
        }
        var user=""
        fun setAsUser(name: String){
            user =name
        }

        var pw=""
        fun setAsPw(pass: String){
            pw =pass
        }

        var filterFlag=0
        var checked=false
        var allowRegister=false
        var pageNum=1
        val pgVal=15
        var ownerState=false
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.packageName)
                )
                startActivityForResult(intent, 232)
            }
        }
    }
}