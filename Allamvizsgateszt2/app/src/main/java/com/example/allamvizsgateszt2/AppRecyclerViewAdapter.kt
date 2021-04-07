package com.example.allamvizsgateszt2


import android.content.pm.PackageInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allamvizsgateszt2.API.*
import com.example.allamvizsgateszt2.Database.MDMDatabaseApp
import com.example.allamvizsgateszt2.Database.UserApps
import com.example.allamvizsgateszt2.Database.UserViewModel
import com.example.allamvizsgateszt2.Database.UserViewModelFactory
import com.example.allamvizsgateszt2.FirstFragment
import com.example.allamvizsgateszt2.MainActivity
import com.example.allamvizsgateszt2.MainActivity.Data.allApps
import com.example.allamvizsgateszt2.MainActivity.Data.applications
import com.example.allamvizsgateszt2.MainActivity.Data.user
import com.example.allamvizsgateszt2.R
import kotlinx.coroutines.*
import org.w3c.dom.Text
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class AppRecyclerViewAdapter : RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder>(),  CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    lateinit var c: FragmentActivity
    var cisInit=false
    lateinit var view: View
    var adminAccess=false
    lateinit var parentView: View
    lateinit var apiViewModel: ApiViewModel
    lateinit var userViewModel: UserViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        if(cisInit) {
            holder.appName.text = allApps[position].applicationInfo.loadLabel(c.packageManager).toString()
            holder.statButton.isEnabled = adminAccess
            holder.allowed=allowed(allApps[position])
            if(holder.allowed) {
                holder.statButton.setImageResource(android.R.drawable.ic_input_add)

            } else {
                holder.statButton.setImageResource(android.R.drawable.ic_delete)
            }
            Glide.with(holder.itemView.context)
                    .load(allApps[position].applicationInfo.loadIcon(c.packageManager))
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(holder.icon).view

            holder.statButton.setOnClickListener {
                launch {
                    try{
                        setLayoutWaiting(true)
                        var bV = if(holder.allowed){
                            apiViewModel.createPolicy(MainActivity.user, MainActivity.pw, holder.appName.text.toString())
                        }else{
                            apiViewModel.deletePolicy(MainActivity.user, MainActivity.pw, holder.appName.text.toString())
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
                        setLayoutWaiting(false)
                    }catch(e: Exception){
                        Log.d("holder exc", e.toString())
                        Toast.makeText(c.applicationContext, "Failed to connect", Toast.LENGTH_SHORT).show()
                        setLayoutWaiting(false)
                    }
                }

            }
        }

    }

    override fun getItemCount(): Int = allApps.size

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
        Log.d("zzz", cisInit.toString())
        Log.d("zzz", c.toString())
        notifyDataSetChanged()
    }
    fun setParView(v: View){
        parentView=v
        notifyDataSetChanged()
    }
    fun setData(admin: Boolean){
        adminAccess = admin
    }
    private fun allowed(packageInfo: PackageInfo):Boolean{
        val packageManager=(c as MainActivity).packageManager
        val name=packageInfo.applicationInfo.loadLabel(packageManager).toString()
        if(applications.applications.contains(name))
            return false
        return true
    }

    private fun setLayoutWaiting(value: Boolean){
        if(value){
            parentView?.findViewById<ProgressBar>(R.id.ProgressBarView)?.visibility= View.VISIBLE
            parentView?.findViewById<Button>(R.id.backButton)?.visibility= View.INVISIBLE
            parentView?.findViewById<Button>(R.id.endButton)?.visibility= View.INVISIBLE
            parentView?.findViewById<TextView>(R.id.yourNameView)?.visibility= View.INVISIBLE
            parentView?.findViewById<RecyclerView>(R.id.recycler)?.visibility= View.INVISIBLE
        }
        else{
            parentView?.findViewById<ProgressBar>(R.id.ProgressBarView)?.visibility= View.INVISIBLE
            parentView?.findViewById<Button>(R.id.backButton)?.visibility= View.VISIBLE
            parentView?.findViewById<Button>(R.id.endButton)?.visibility= View.VISIBLE
            parentView?.findViewById<TextView>(R.id.yourNameView)?.visibility= View.VISIBLE
            parentView?.findViewById<RecyclerView>(R.id.recycler)?.visibility= View.VISIBLE
        }

    }
}