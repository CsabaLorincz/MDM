package com.mdm.app.fragments

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mdm.app.API.Applications
import com.mdm.app.Database.MDMDatabaseApp
import com.mdm.app.Database.UserViewModel
import com.mdm.app.Database.UserViewModelFactory
import com.mdm.app.R
import com.mdm.app.activities.MDMActivity
import com.mdm.app.activities.MDMActivity.Data.allowRegister
import com.mdm.app.activities.MDMActivity.Data.checked
import com.mdm.app.activities.MDMActivity.Data.filterFlag
import com.mdm.app.activities.MDMActivity.Data.isLoggedInAdmin
import com.mdm.app.activities.MDMActivity.Data.pageNum
import com.mdm.app.extension.allowed
import com.mdm.app.viewAdapter.AppRecyclerViewAdapter
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AppListFragment : Fragment() {
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
        apps= MDMActivity.applications
        appAdapter = AppRecyclerViewAdapter()
        appAdapter.setActivity(requireActivity(), userViewModel)
        appAdapter.setData(MDMActivity.isLoggedInAdmin, MDMActivity.getAllApps())
        val view = inflater.inflate(R.layout.fragment_applist, container, false)
        appList=view.findViewById(R.id.recycler)

        deviceManager=(activity as MDMActivity).getManager()
        compName=(activity as MDMActivity).getComp()



        val APP_PACKAGES = ArrayList<String>()
        for (i in MDMActivity.getAllApps().indices) {
            val packageInfo = MDMActivity.getAllApps()[i]
            if((activity as MDMActivity).allowed(packageInfo))
                APP_PACKAGES.add(packageInfo.applicationInfo.packageName)
        }


        if(deviceManager!!.isDeviceOwnerApp("com.mdm.app")) {
            if (deviceManager!!.isAdminActive(compName!!)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    deviceManager!!.setLockTaskPackages(compName!!, APP_PACKAGES.toTypedArray())
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    deviceManager!!.setLockTaskFeatures(compName!!, DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                            DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW or DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO)
                }
            }
        }
        else{
            if(deviceManager!!.isAdminActive(compName!!)){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    deviceManager!!.setLockTaskPackages(compName!!, APP_PACKAGES.toTypedArray())
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    deviceManager!!.setLockTaskFeatures(compName!!, DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                            DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW or DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO)
                }
            }
        }



        with(appList) {
            appList.adapter = appAdapter
            appList.layoutManager = LinearLayoutManager(activity)
            appList.setHasFixedSize(true)

        }
        val endB=view.findViewById<Button>(R.id.endButton)
        if(MDMActivity.isLoggedInAdmin){
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
            (activity as MDMActivity).finish()
            exitProcess(0)

        }
        val nameStr="Welcome " + MDMActivity.user
        view.findViewById<TextView>(R.id.yourNameView).text=nameStr

        view.findViewById<Button>(R.id.backButton).setOnClickListener {
            pageNum=1
            view.findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val searchBar = view.findViewById<SearchView>(R.id.searchView)
        searchBar.setOnCloseListener {
            filterFlag=0
            appAdapter.filter.filter("query.toString()")
            true
        }
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if(query.isNullOrEmpty()) {
                    false
                } else {
                    filterFlag=1
                    appAdapter.filter.filter(query.toString())
                    true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return if(newText.isNullOrEmpty()) {
                    false
                } else {
                    filterFlag=1
                    appAdapter.filter.filter(newText.toString())
                    true
                }
            }
        })

        val check=view.findViewById<CheckedTextView>(R.id.check)
        check.setOnClickListener {
            checked=!checked
            if(checked){
                check.text="Show all"
                filterFlag=2
                appAdapter.filter.filter("newText.toString()")
                true
            }
            else{
                check.text="Show only restricted"
                filterFlag=0
                appAdapter.filter.filter("newText.toString()")
                true
            }
        }
        val mode=view.findViewById<CheckBox>(R.id.checkMode)
        if(isLoggedInAdmin){
            mode.visibility=View.VISIBLE
        }
        else{
            mode.visibility=View.INVISIBLE
        }
        mode.isChecked= allowRegister

        mode.setOnClickListener{
            allowRegister = !allowRegister
        }

        appList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)){
                    Log.d("pgnum", pageNum.toString())
                    pageNum+=1
                    appAdapter.setNotify()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appAdapter.setParView(view)
    }

    /*private fun allowed(packageInfo: PackageInfo):Boolean{
        val packageManager=(activity as MDMActivity).packageManager
        val name=packageInfo.applicationInfo.loadLabel(packageManager).toString()
        if(MDMActivity.applications.applications.contains(name))
            return false
        return true
    }*/

}