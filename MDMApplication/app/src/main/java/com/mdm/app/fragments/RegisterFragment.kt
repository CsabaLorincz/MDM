package com.mdm.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mdm.app.Database.MDMDatabaseApp
import com.mdm.app.Database.User
import com.mdm.app.Database.UserViewModel
import com.mdm.app.Database.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import com.mdm.app.API.*
import com.mdm.app.R
import com.mdm.app.activities.MDMActivity
import com.mdm.app.extension.getSHA512
import com.mdm.app.extension.setLayoutWaiting


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
                view.setLayoutWaiting(true)
                val userUpload=UserUpload(0, name.text.toString(), email.text.toString(), getSHA512(pw.text.toString()))
                try{
                    val uploadResp=apiViewModel.createUser(userUpload)
                    var check: Boolean = false
                    if(uploadResp.isSuccessful && uploadResp.body()!=null){
                        check=uploadResp.body()!!
                        if(check){
                            MDMActivity.setAsUser(name.text.toString())
                            MDMActivity.setAsPw(getSHA512(pw.text.toString()))
                            MDMActivity.setLogin(true)
                            MDMActivity.setData(Applications(mutableListOf()))
                            userViewModel.updateAll()
                            userViewModel.insert(User(0, name.text.toString(), getSHA512(pw.text.toString()), true))
                            if(view.findNavController().currentDestination?.id == R.id.registerFragment)
                                view.findNavController().navigate(R.id.action_registerFragment_to_SecondFragment)

                        }
                        else{
                            Toast.makeText(context, "Failed to create", Toast.LENGTH_SHORT).show()
                            view.setLayoutWaiting(false)
                        }
                    }
                    else{
                        Toast.makeText(context, "Failed to create", Toast.LENGTH_SHORT).show()
                        view.setLayoutWaiting(false)
                    }
                }catch(e: Exception){
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                    view.setLayoutWaiting(false)
                }

            }
        }
    }

    /*private fun setLayoutWaiting(value: Boolean){
        if(value){
            view?.findViewById<ProgressBar>(R.id.registerProgress)?.visibility= View.VISIBLE
            view?.findViewById<TextView>(R.id.registerText)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPersonName)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextEmailAddress)?.visibility= View.INVISIBLE
            view?.findViewById<EditText>(R.id.editTextTextPassword)?.visibility= View.INVISIBLE
            view?.findViewById<Button>(R.id.buttonRegister)?.visibility= View.INVISIBLE
            view?.findViewById<Button>(R.id.buttonRegisterBack)?.visibility= View.INVISIBLE
            view?.hideKeyboard()
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
    }*/

    private fun correctFormatMail(email: String): Boolean{
        val reg=Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        return reg.matches(email)
    }

}