package com.example.allamvizsgateszt2

import android.app.ActivityOptions
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.allamvizsgateszt2.API.ApiRepository
import com.example.allamvizsgateszt2.API.ApiViewModel
import com.example.allamvizsgateszt2.API.ApiViewModelFactory
import com.example.allamvizsgateszt2.API.Applications
import com.example.allamvizsgateszt2.Database.MDMDatabaseApp
import com.example.allamvizsgateszt2.Database.UserViewModel
import com.example.allamvizsgateszt2.Database.UserViewModelFactory
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    var deviceManager: DevicePolicyManager? = null
    var compName: ComponentName? = null

    val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as MDMDatabaseApp).repository)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestPermission()
        Log.d("Startalma", "startalma")
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        compName = ComponentName(this, AdminManager::class.java)
        deviceManager = getSystemService(
                Context.DEVICE_POLICY_SERVICE
        ) as DevicePolicyManager
        //val arr=arrayOf("")
        //deviceManager!!.setLockTaskPackages(compName!!, arr)
        val list = packageManager.getInstalledPackages(0)
        setAll(list)
        Log.d("elsolog", "123")
        if(!deviceManager!!.isAdminActive(compName!!)){

            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            startActivityForResult(intent, 0)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
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
            Data.applications=applications
            for(i in applications.applications)
                Log.d("siker:)", i)
        }

        lateinit var allApps: MutableList<PackageInfo>
        fun setAll(applications: MutableList<PackageInfo> ){
            allApps=applications
        }

        var isLoggedInAdmin=false
        fun setLogin(b: Boolean){
            isLoggedInAdmin=b
        }
        var user=""
        fun setAsUser(name: String){
            user=name
            Log.d("as errorName", name)
        }
        var pw=""
        fun setAsPw(pass: String){
            pw=pass
            Log.d("as errorPW", pass)
        }
        fun getSHA512(str:String):String{
            val md: MessageDigest = MessageDigest.getInstance("SHA-512")
            val messageDigest = md.digest(str.toByteArray())

            val no = BigInteger(1, messageDigest)

            var hashtext: String = no.toString(16)

            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }

            return hashtext
        }
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.packageName)
                )
                startActivityForResult(intent, 232)
            } else {
                //Permission Granted-System will work
            }
        }
    }
}