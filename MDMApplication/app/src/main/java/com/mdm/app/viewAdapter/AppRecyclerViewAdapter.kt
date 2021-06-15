package com.mdm.app.viewAdapter


import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mdm.app.API.*
import com.mdm.app.Database.UserApps
import com.mdm.app.Database.UserViewModel
import com.mdm.app.activities.MDMActivity
import com.mdm.app.activities.MDMActivity.Data.applications
import com.mdm.app.activities.MDMActivity.Data.isLoggedInAdmin
import com.mdm.app.activities.MDMActivity.Data.user
import com.mdm.app.R
import com.mdm.app.extension.allowed
import com.mdm.app.extension.setLayoutWaiting
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class AppRecyclerViewAdapter : RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder>(),  CoroutineScope, Filterable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    lateinit var c: FragmentActivity
    var cisInit=false
    lateinit var view: View
    var adminAccess=false
    lateinit var parentView: View
    lateinit var apiViewModel: ApiViewModel
    lateinit var userViewModel: UserViewModel
    var allAppsLoc: MutableList<PackageInfo> = mutableListOf()
    var filteredApps: MutableList<PackageInfo> = mutableListOf()

    var filteredAppsLocal: MutableList<PackageInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        if(cisInit) {
            holder.appName.text = filteredApps[position].applicationInfo.loadLabel(c.packageManager).toString()
            holder.statButton.isEnabled = adminAccess
            holder.allowed=c.allowed(filteredApps[position])
            if(holder.allowed) {
                holder.statButton.setImageResource(android.R.drawable.ic_input_add)

            } else {
                holder.statButton.setImageResource(android.R.drawable.ic_delete)
            }
            Glide.with(holder.itemView.context)
                    .load(filteredApps[position].applicationInfo.loadIcon(c.packageManager))
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(holder.icon).view

            holder.statButton.setOnClickListener {
                launch {
                    try{
                        parentView.setLayoutWaiting(true)
                        var bV = if(holder.allowed){
                            apiViewModel.createPolicy(MDMActivity.user, MDMActivity.pw, holder.appName.text.toString())
                        }else{
                            apiViewModel.deletePolicy(MDMActivity.user, MDMActivity.pw, holder.appName.text.toString())
                        }
                        if(bV.isSuccessful && bV.body()!=null){
                            var b=bV.body()!!
                            if(b){
                                if(holder.allowed){
                                    holder.allowed=false
                                    holder.statButton.setImageResource(android.R.drawable.ic_delete)
                                    applications.applications.add(holder.appName.text.toString())
                                    userViewModel.insertUserApp(UserApps(0, user, holder.appName.text.toString()))
                                }
                                else{
                                    holder.allowed=true
                                    holder.statButton.setImageResource(android.R.drawable.ic_input_add)
                                    applications.applications.remove(holder.appName.text.toString())
                                    userViewModel.deleteUserApps(user, holder.appName.text.toString())
                                }
                            }
                            else{
                                Toast.makeText(c.applicationContext, "Task unsuccessful", Toast.LENGTH_SHORT).show()
                            }

                        }
                        else{
                            Toast.makeText(c.applicationContext, "Task unsuccessful", Toast.LENGTH_SHORT).show()
                        }
                        parentView.setLayoutWaiting(false)
                    }catch(e: Exception){
                        Toast.makeText(c.applicationContext, "Failed to connect", Toast.LENGTH_SHORT).show()
                        parentView.setLayoutWaiting(false)
                    }
                }

            }
        }

    }

    override fun getItemCount(): Int = smallest(filteredApps.size, MDMActivity.pgVal*MDMActivity.pageNum)

    fun smallest(a: Int, b: Int):Int
    {
        if(a>b)
            return b
        return a
    }

    inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView=view.findViewById(R.id.app_name)
        val statButton: ImageButton=view.findViewById(R.id.statusButton)
        val icon: ImageView=view.findViewById(R.id.icon)
        var allowed: Boolean=true
    }

    fun setActivity(a: FragmentActivity, uvm: UserViewModel){
        c=a
        cisInit=true
        val repository = ApiRepository()
        val factory = ApiViewModelFactory(repository)
        apiViewModel= ViewModelProvider(c, factory).get(ApiViewModel::class.java)
        userViewModel=uvm
        notifyDataSetChanged()
    }
    fun setParView(v: View){
        parentView=v
        notifyDataSetChanged()
    }
    fun setData(admin: Boolean, apps: MutableList<PackageInfo>){
        adminAccess = admin
        allAppsLoc.clear()
        allAppsLoc.addAll(MDMActivity.getAllApps())
        filteredApps.clear()
        filteredApps.addAll(apps)
        removeThis()
        notifyDataSetChanged()
    }
    public fun setNotify(){
        notifyDataSetChanged()
    }

    private fun getAllNotAllowed():MutableList<PackageInfo>{
        val list= mutableListOf<PackageInfo>()
        if(cisInit)
        for(i in allAppsLoc){
            if(!c.allowed(i)){
                list.add(i)
            }
        }
        return list
    }


    private fun removeThis(){
        if(cisInit)
        for(i in filteredApps){
            if(i.applicationInfo.loadLabel((c as MDMActivity).packageManager).toString()=="MDMApp"){
                filteredApps.remove(i)
                return
            }

        }
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                filteredAppsLocal= mutableListOf<PackageInfo>()

                if(MDMActivity.filterFlag==0){

                    val datalist= mutableListOf<PackageInfo>()
                    if(MDMActivity.checked){
                        datalist.addAll(getAllNotAllowed())
                    }else{
                        datalist.addAll(allAppsLoc)
                    }
                    filteredAppsLocal.addAll(datalist)
                }else

                //searchBar
                if(MDMActivity.filterFlag==1) {
                    if (constraint.isNullOrBlank()) {
                        val datalist= mutableListOf<PackageInfo>()
                        if(MDMActivity.checked){
                            datalist.addAll(getAllNotAllowed())
                        }else{
                            datalist.addAll(allAppsLoc)
                        }
                        filteredAppsLocal.addAll(datalist)
                    } else {
                        val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                        val datalist= mutableListOf<PackageInfo>()
                        if(MDMActivity.checked){
                            datalist.addAll(getAllNotAllowed())
                        }else{
                            datalist.addAll(allAppsLoc)
                        }
                        for (item in 0..datalist.size-1) {
                            if (datalist[item].applicationInfo.loadLabel(c.packageManager).toString().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                                filteredAppsLocal.add(datalist[item])
                            }
                        }
                    }

                }else

                //restricted only
                if(MDMActivity.filterFlag==2){
                    filteredAppsLocal.addAll(getAllNotAllowed())
                }

                return filterResults.also {
                    it.values = filteredAppsLocal
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if(filteredAppsLocal.isNullOrEmpty()) {
                    setData(isLoggedInAdmin, filteredApps)
                }
                else {
                    setData(isLoggedInAdmin, filteredAppsLocal)
                }
                notifyDataSetChanged()
            }
        }
    }
}