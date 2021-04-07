package com.example.allamvizsgateszt2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.example.allamvizsgateszt2.API.ApiRepository
import com.example.allamvizsgateszt2.API.ApiViewModel
import com.example.allamvizsgateszt2.API.ApiViewModelFactory
import com.example.allamvizsgateszt2.API.UserUpload
import com.example.allamvizsgateszt2.Database.MDMDatabaseApp
import com.example.allamvizsgateszt2.Database.User
import com.example.allamvizsgateszt2.Database.UserViewModel
import com.example.allamvizsgateszt2.Database.UserViewModelFactory
import com.example.allamvizsgateszt2.MainActivity.Data.getSHA512
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext
import android.util.Log


class RegisterFragment: Fragment(), CoroutineScope {
    private lateinit var apiViewModel: ApiViewModel
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as MDMDatabaseApp).repository)
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val repository = ApiRepository()
        val factory = ApiViewModelFactory(repository)
        apiViewModel=ViewModelProvider(requireActivity(), factory).get(ApiViewModel::class.java)
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val regButton=view.findViewById<Button>(R.id.buttonRegister)
        regButton.isEnabled=false
        view.findViewById<Button>(R.id.buttonRegisterBack).setOnClickListener {
            view.findNavController().navigate(R.id.action_registerFragment_to_FirstFragment)
        }
        val name=view.findViewById<EditText>(R.id.editTextTextPersonName)
        val pw=view.findViewById<EditText>(R.id.editTextTextPassword)
        val email=view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        name.addTextChangedListener {
            regButton.isEnabled = name.text.isNotBlank() && pw.text.isNotBlank() && correctFormatMail(email.text.toString())
        }

        pw.addTextChangedListener {
            regButton.isEnabled = pw.text.isNotBlank() && name.text.isNotBlank()&& correctFormatMail(email.text.toString())
        }
        email.addTextChangedListener {
            regButton.isEnabled = pw.text.isNotBlank() && name.text.isNotBlank()&& correctFormatMail(email.text.toString())
        }

        regButton.setOnClickListener {
            launch {
                setLayoutWaiting(true)
                val userUpload=UserUpload(0, name.text.toString(), email.text.toString(), getSHA512(pw.text.toString()))
                try{
                    val uploadResp=apiViewModel.createUser(userUpload)
                    var check: Boolean = false
                    Log.d("cerr", "1")
                    if(uploadResp.isSuccessful && uploadResp.body()!=null){
                        check=uploadResp.body()!!
                        Log.d("cerr", "2")
                        if(check){
                            MainActivity.setAsUser(name.text.toString())
                            MainActivity.setAsPw(getSHA512(pw.text.toString()))
                            MainActivity.setLogin(true)
                            userViewModel.updateAll()
                            userViewModel.insert(User(0, name.text.toString(), getSHA512(pw.text.toString()), true))
                            if(view.findNavController().currentDestination?.id == R.id.registerFragment)
                                view.findNavController().navigate(R.id.action_registerFragment_to_SecondFragment)

                        }
                        else{
                            Toast.makeText(context, "Failed to create", Toast.LENGTH_SHORT).show()
                            Log.d("cerr", "3")
                            setLayoutWaiting(false)
                        }
                    }
                    else{
                        Toast.makeText(context, "Failed to create", Toast.LENGTH_SHORT).show()
                        Log.d("cerr", "3")
                        setLayoutWaiting(false)
                    }
                }catch(e: Exception){
                    Log.d("cerr", e.toString())
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                    setLayoutWaiting(false)
                }

            }
        }
    }

    private fun setLayoutWaiting(value: Boolean){
        if(value){
            view?.findViewById<ProgressBar>(R.id.registerProgress)?.visibility= View.VISIBLE
            view?.findViewById<TextView>(R.id.registerText)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPersonName)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextEmailAddress)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPassword)?.visibility= View.INVISIBLE
            view?.findViewById<Button>(R.id.buttonRegister)?.visibility= View.INVISIBLE
            view?.findViewById<Button>(R.id.buttonRegisterBack)?.visibility= View.INVISIBLE
        }
        else{
            view?.findViewById<ProgressBar>(R.id.registerProgress)?.visibility= View.INVISIBLE
            view?.findViewById<TextView>(R.id.registerText)?.visibility= View.VISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPersonName)?.visibility= View.VISIBLE
            view?.findViewById<EditText>(R.id.editTextTextEmailAddress)?.visibility= View.VISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPassword)?.visibility= View.VISIBLE
            view?.findViewById<Button>(R.id.buttonRegister)?.visibility= View.VISIBLE
            view?.findViewById<Button>(R.id.buttonRegisterBack)?.visibility= View.VISIBLE
        }
    }


    private fun correctFormatMail(email: String): Boolean{
        val reg=Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        return reg.matches(email)
    }

}