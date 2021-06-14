package com.mdm.app.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mdm.app.API.*
import com.mdm.app.Database.*
import com.mdm.app.R
import com.mdm.app.activities.MDMActivity
import com.mdm.app.activities.MDMActivity.Data.allowRegister
import com.mdm.app.extension.getSHA512
import com.mdm.app.extension.hideKeyboard
import com.mdm.app.extension.setLayoutWaiting
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class LoginFragment : Fragment(), CoroutineScope {
    private lateinit var apiViewModel: ApiViewModel
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as MDMDatabaseApp).repository)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_login, container, false)
        val repository = ApiRepository()
        val factory = ApiViewModelFactory(repository)
        apiViewModel=ViewModelProvider(requireActivity(), factory).get(ApiViewModel::class.java)
        return view
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginB = view.findViewById<Button>(R.id.loginButton)
        loginB.isEnabled = false

        view.setLayoutWaiting(true)
        userViewModel.getCurrent()
        view.setLayoutWaiting(false)
        val continueButton=view.findViewById<Button>(R.id.continueButton)
        continueButton.isEnabled = !(MDMActivity.user=="" || MDMActivity.pw=="")
        if(continueButton.isEnabled){
            val str="Continue as ${MDMActivity.user}"
            continueButton.text=str
        }
        val name = view.findViewById<EditText>(R.id.editName)
        val pw = view.findViewById<EditText>(R.id.editPassword)

        name.addTextChangedListener {
            loginB.isEnabled = name.text.isNotBlank() && pw.text.isNotBlank()
        }

        pw.addTextChangedListener {
            loginB.isEnabled = pw.text.isNotBlank() && name.text.isNotBlank()
        }


        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                view.findNavController().navigate(R.id.action_FirstFragment_to_registerFragment)
        }

        loginB.setOnClickListener {
            launch {
                view.setLayoutWaiting(true)
                val localName=name.text.toString()
                val localPw=getSHA512(pw.text.toString())
                try{
                    val appsResponse=apiViewModel.getApps(localName, localPw)
                    lateinit var appsList: Applications
                    if(appsResponse.isSuccessful && appsResponse.body()!=null){
                        appsList=Applications(appsResponse.body()!!.toMutableList())
                        if(appsList.applications.size==1 && appsList.applications[0]=="Login Error"){
                            name.text.clear()
                            pw.text.clear()
                            Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT).show()
                            view.setLayoutWaiting(false)
                        }
                        else{
                            MDMActivity.setData(appsList)
                            MDMActivity.setLogin(true)
                            MDMActivity.setAsUser(localName)
                            MDMActivity.setAsPw(localPw)


                            userViewModel.updateAllBut(name.text.toString())


                            val names = userViewModel.getAll()

                            if (names!!.contains(localName)) {
                                userViewModel.update(localName)
                            } else {
                                userViewModel.insert(
                                        User(
                                                0,
                                                localName,
                                                localPw,
                                                true
                                        )
                                )
                            }
                            val databaseAppList=userViewModel.getUserApps(localName)
                            for(i in databaseAppList){
                                if(!appsList.applications.contains(i)){
                                    userViewModel.deleteUserApps(localName, i)
                                }
                            }
                            for(i in appsList.applications){
                                if(!databaseAppList.contains(i)){
                                    userViewModel.insertUserApp(UserApps(0, localName, i))
                                }
                            }
                            if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                                view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

                        }
                    }
                    else{
                        name.text.clear()
                        pw.text.clear()
                        Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                        view.setLayoutWaiting(false)
                    }

                }catch(e: java.lang.Exception){
                    name.text.clear()
                    pw.text.clear()
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                    view.setLayoutWaiting(false)
                }

            }
        }

        continueButton.setOnClickListener {
            launch{
                view.setLayoutWaiting(true)
                try{
                    val appsResponse=apiViewModel.getApps(MDMActivity.user, MDMActivity.pw)
                    lateinit var appsList: Applications
                    if(appsResponse.isSuccessful && appsResponse.body()!=null){
                        appsList=Applications(appsResponse.body()!!.toMutableList())

                        MDMActivity.setData(appsList)
                        MDMActivity.setLogin(false)
                        MDMActivity.setAsUser(MDMActivity.user)
                        MDMActivity.setAsPw(MDMActivity.pw)

                        val databaseAppList=userViewModel.getUserApps(MDMActivity.user)
                        for(i in databaseAppList){
                            if(!appsList.applications.contains(i)){
                                userViewModel.deleteUserApps(MDMActivity.user, i)
                            }
                        }
                        for(i in appsList.applications){
                            if(!databaseAppList.contains(i)){
                                userViewModel.insertUserApp(UserApps(0, MDMActivity.user, i))
                            }
                        }
                        if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                            view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

                    }
                    else{
                        MDMActivity.setLogin(false)
                        MDMActivity.setAsUser(MDMActivity.user)
                        MDMActivity.setAsPw(MDMActivity.pw)
                        val databaseAppList=userViewModel.getUserApps(MDMActivity.user)
                        MDMActivity.setData(Applications(databaseAppList.toMutableList()))
                        if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                            view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                }catch(e:Exception){
                    MDMActivity.setLogin(false)
                    MDMActivity.setAsUser(MDMActivity.user)
                    MDMActivity.setAsPw(MDMActivity.pw)
                    val databaseAppList=userViewModel.getUserApps(MDMActivity.user)
                    MDMActivity.setData(Applications(databaseAppList.toMutableList()))
                    if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                        view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }

                }
            }
            if(continueButton.isEnabled){
                view.findViewById<Button>(R.id.registerButton).isEnabled= allowRegister
            }
            else{
                view.findViewById<Button>(R.id.registerButton).isEnabled= true
            }
        }



    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()



    /*private fun setLayoutWaiting(value: Boolean){
        if(value){
            view?.findViewById<ProgressBar>(R.id.progressBarLogin)?.visibility=VISIBLE
            view?.findViewById<TextView>(R.id.textView)?.visibility=INVISIBLE
            view?.findViewById<EditText>(R.id.editName)?.visibility=INVISIBLE
            view?.findViewById<EditText>(R.id.editPassword)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.continueButton)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.registerButton)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.loginButton)?.visibility=INVISIBLE
            view?.hideKeyboard()
        }
        else{
            view?.findViewById<ProgressBar>(R.id.progressBarLogin)?.visibility=INVISIBLE
            view?.findViewById<TextView>(R.id.textView)?.visibility=VISIBLE
            view?.findViewById<EditText>(R.id.editName)?.visibility=VISIBLE
            view?.findViewById<EditText>(R.id.editPassword)?.visibility=VISIBLE
            view?.findViewById<Button>(R.id.continueButton)?.visibility=VISIBLE
            view?.findViewById<Button>(R.id.registerButton)?.visibility=VISIBLE
            view?.findViewById<Button>(R.id.loginButton)?.visibility=VISIBLE
        }
    }*/

}
