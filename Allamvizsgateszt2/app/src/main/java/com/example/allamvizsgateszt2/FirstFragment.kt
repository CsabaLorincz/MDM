package com.example.allamvizsgateszt2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.allamvizsgateszt2.API.*
import com.example.allamvizsgateszt2.Database.*
import com.example.allamvizsgateszt2.MainActivity.Data.getSHA512
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import java.lang.reflect.Array.get
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), CoroutineScope {
    private lateinit var apiViewModel: ApiViewModel
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as MDMDatabaseApp).repository)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_first, container, false)
        val repository = ApiRepository()
        val factory = ApiViewModelFactory(repository)
        apiViewModel=ViewModelProvider(requireActivity(), factory).get(ApiViewModel::class.java)


        return view
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d("akarmiiiii", "asdasd")
        super.onViewCreated(view, savedInstanceState)
        val loginB = view.findViewById<Button>(R.id.loginButton)
        loginB.isEnabled = false

        setLayoutWaiting(true)
        userViewModel.getCurrent()
        setLayoutWaiting(false)
        val continueButton=view.findViewById<Button>(R.id.continueButton)
        continueButton.isEnabled = !(MainActivity.user=="" || MainActivity.pw=="")
        if(continueButton.isEnabled){
            val str="Continue as ${MainActivity.user}"
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
                setLayoutWaiting(true)
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
                            setLayoutWaiting(false)
                        }
                        else{
                            MainActivity.setData(appsList)
                            MainActivity.setLogin(true)
                            MainActivity.setAsUser(localName)
                            MainActivity.setAsPw(localPw)


                            userViewModel.updateAllBut(name.text.toString())


                            val names = userViewModel.getAll()
                            //Toast.makeText(context, names, Toast.LENGTH_SHORT).show()

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
                        setLayoutWaiting(false)
                    }

                }catch(e: java.lang.Exception){
                    name.text.clear()
                    pw.text.clear()
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                    setLayoutWaiting(false)
                }

            }
        }

        continueButton.setOnClickListener {
            launch{
                setLayoutWaiting(true)
                try{
                    val appsResponse=apiViewModel.getApps(MainActivity.user, MainActivity.pw)
                    lateinit var appsList: Applications
                    if(appsResponse.isSuccessful && appsResponse.body()!=null){
                        appsList=Applications(appsResponse.body()!!.toMutableList())

                        MainActivity.setData(appsList)
                        MainActivity.setLogin(false)
                        MainActivity.setAsUser(MainActivity.user)
                        MainActivity.setAsPw(MainActivity.pw)




                        val databaseAppList=userViewModel.getUserApps(MainActivity.user)
                        for(i in databaseAppList){
                            if(!appsList.applications.contains(i)){
                                userViewModel.deleteUserApps(MainActivity.user, i)
                            }
                        }
                        for(i in appsList.applications){
                            if(!databaseAppList.contains(i)){
                                userViewModel.insertUserApp(UserApps(0, MainActivity.user, i))
                            }
                        }
                        if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                            view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

                    }
                    else{
                        MainActivity.setLogin(false)
                        MainActivity.setAsUser(MainActivity.user)
                        MainActivity.setAsPw(MainActivity.pw)
                        val databaseAppList=userViewModel.getUserApps(MainActivity.user)
                        MainActivity.setData(Applications(databaseAppList.toMutableList()))
                        if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                            view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                }catch(e:Exception){
                    MainActivity.setLogin(false)
                    MainActivity.setAsUser(MainActivity.user)
                    MainActivity.setAsPw(MainActivity.pw)
                    val databaseAppList=userViewModel.getUserApps(MainActivity.user)
                    MainActivity.setData(Applications(databaseAppList.toMutableList()))
                    if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                        view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }

            }


            }
        }



    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()



    private fun setLayoutWaiting(value: Boolean){
        if(value){
            view?.findViewById<ProgressBar>(R.id.progressBarLogin)?.visibility=VISIBLE
            view?.findViewById<TextView>(R.id.textView)?.visibility=INVISIBLE
            view?.findViewById<EditText>(R.id.editName)?.visibility=INVISIBLE
            view?.findViewById<EditText>(R.id.editPassword)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.continueButton)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.registerButton)?.visibility=INVISIBLE
            view?.findViewById<Button>(R.id.loginButton)?.visibility=INVISIBLE
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
    }

}
/*

loginB.setOnClickListener {
            Log.d("step", "0")
            val localName=name.text.toString()
            val localPw=getSHA512(pw.text.toString())
            setLayoutWaiting(true)
            Log.d("step", "00")


            launch {
                    try {


                        apiViewModel.getApps("'${localName}'", "'${localPw}'")
                        Log.d("step", "1")


                    } catch (e: Exception) {
                        Log.d("sikersad", e.toString())
                        Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                        setLayoutWaiting(false)
                    }
                }//

            lateinit var appList: Applications
            Log.d("step", "2")
            apiViewModel.applications.observe(requireActivity()) { applications ->
                appList = applications
                Log.d("step", "3")

                if (applications.applications.size == 1 && applications.applications[0] == "Login Error") {
                    name.text.clear()
                    pw.text.clear()
                    Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT).show()
                    setLayoutWaiting(false)
                } else {
                    MainActivity.setData(appList)
                    MainActivity.setLogin(true)
                    MainActivity.setAsUser(localName)
                    MainActivity.setAsPw(localPw)


                    userViewModel.updateAllBut(name.text.toString())


                    val names = userViewModel.getAll()
                    //Toast.makeText(context, names, Toast.LENGTH_SHORT).show()

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
                        if(!appList.applications.contains(i)){
                            userViewModel.deleteUserApps(localName, i)
                        }
                    }
                    for(i in appList.applications){
                        if(!databaseAppList.contains(i)){
                            userViewModel.insertUserApp(UserApps(0, localName, i))
                        }
                    }
                    if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                        view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }

            }
            if(MainActivity.user==localName && MainActivity.pw==localPw){

                val l=userViewModel.getUserApps(localName)
                MainActivity.setData(Applications(l))
                MainActivity.setLogin(true)

                if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                    view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
            else{
                setLayoutWaiting(false)
            }

        }

 */

/*

continueButton.setOnClickListener {
            launch {
                setLayoutWaiting(true)
                MainActivity.setLogin(false)
                try {
                    val MUser=MainActivity.user
                    val MPw=MainActivity.pw
                    apiViewModel.getApps("'${MUser}'", "'${MPw}'")
                    Log.d("as error", MainActivity.user)
                    Log.d("step", "1")
                    lateinit var appList: Applications
                    Log.d("step", "2")
                    apiViewModel.applications.observe(requireActivity(), Observer { applications ->
                        if(applications!=null){
                            appList = applications
                            Log.d("step", "3")
                            MainActivity.setData(appList)
                            val databaseAppList=userViewModel.getUserApps(MUser)
                            for(i in databaseAppList){
                                if(!appList.applications.contains(i)){
                                    userViewModel.deleteUserApps(MainActivity.user, i)
                                }
                            }
                            for(i in appList.applications){
                                if(!databaseAppList.contains(i)){
                                    userViewModel.insertUserApp(UserApps(0, MUser, i))
                                }
                            }

                            if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                                view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)


                        }

                    })
                    Log.d("step", "vege")
                    //Log.d("step", "vege2")
                    if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                    view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

                }catch(e: java.lang.Exception){
                    Log.d("important log", e.toString())
                    val apps=userViewModel.getUserApps(MainActivity.user)
                    if(!apps.isNullOrEmpty()){
                        val applications=Applications(apps)
                        MainActivity.setData(applications)
                    }

                    if(view.findNavController().currentDestination?.id == R.id.FirstFragment)
                        view.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)


                }
            }
        }




 */