package com.example.allamvizsgateszt2

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allamvizsgateszt2.API.Applications
import com.example.allamvizsgateszt2.Database.MDMDatabaseApp
import com.example.allamvizsgateszt2.Database.UserViewModel
import com.example.allamvizsgateszt2.Database.UserViewModelFactory
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private lateinit var appList: RecyclerView
    lateinit var appAdapter: AppRecyclerViewAdapter
    private lateinit var apps: Applications
    var deviceManager: DevicePolicyManager? = null
    var compName: ComponentName? = null
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as MDMDatabaseApp).repository)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        apps=MainActivity.applications
        appAdapter = AppRecyclerViewAdapter()
        appAdapter.setData(MainActivity.isLoggedInAdmin)
        appAdapter.setActivity(requireActivity(), userViewModel)
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        appList=view.findViewById(R.id.recycler)

        deviceManager=(activity as MainActivity).getManager()
        compName=(activity as MainActivity).getComp()



        val APP_PACKAGES = ArrayList<String>()
        for (i in MainActivity.allApps.indices) {
            val packageInfo = MainActivity.allApps[i]
            if(allowed(packageInfo))
                APP_PACKAGES.add(packageInfo.applicationInfo.packageName)
            Log.d("Igen", packageInfo.applicationInfo.packageName)

        }


        if(deviceManager!!.isDeviceOwnerApp("")) {
            Log.d("ASD", compName.toString())
            Log.d("ASD", deviceManager.toString())
            if (deviceManager!!.isAdminActive(compName!!)) {
                Log.d("ASD jo", compName.toString())
                Log.d("ASD jo", deviceManager.toString())
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    deviceManager!!.setLockTaskPackages(compName!!, APP_PACKAGES.toTypedArray())
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    deviceManager!!.setLockTaskFeatures(compName!!, DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                            DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW)

                    Log.d("true", "ttt")
                }
            }
        }
        else{
            Log.d("ASD sad", compName.toString())
            Log.d("ASD sad", deviceManager.toString())
            if(deviceManager!!.isAdminActive(compName!!)){
                Log.d("ASD maybe not sad?", "asdasd")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    deviceManager!!.setLockTaskPackages(compName!!, APP_PACKAGES.toTypedArray())
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    deviceManager!!.setLockTaskFeatures(compName!!, DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                            DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW)
                    //deviceManager!!.setLockTaskFeatures(compName!!, LOCK_TASK_FEATURE_OVERVIEW)
                    Log.d("true", "ttt")
                }
            }
        }



        with(appList) {
            appList.adapter = appAdapter
            appList.layoutManager = LinearLayoutManager(activity)
            appList.setHasFixedSize(true)

        }
        val endB=view.findViewById<Button>(R.id.endButton)
        if(MainActivity.isLoggedInAdmin){
            endB.visibility=View.VISIBLE
        }
        else{
            endB.visibility=View.INVISIBLE
        }
        endB.setOnClickListener {
            val arr=arrayOf("")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                deviceManager!!.setLockTaskPackages(compName!!, arr)
            }
            (activity as MainActivity).finish()
            exitProcess(0)

        }
        val nameStr="Welcome " + MainActivity.user
        view.findViewById<TextView>(R.id.yourNameView).text=nameStr

        view.findViewById<Button>(R.id.backButton).setOnClickListener {
            view.findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appAdapter.setParView(view)
    }

    private fun allowed(packageInfo: PackageInfo):Boolean{
        var packageManager=(activity as MainActivity).packageManager
        var name=packageInfo.applicationInfo.loadLabel(packageManager).toString()
        if(MainActivity.applications.applications.contains(name))
            return false
        return true
    }

}