package com.rwtcompany.onlinevegitableshopapp.ui.auth.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.rwtcompany.onlinevegitableshopapp.R
import com.rwtcompany.onlinevegitableshopapp.databinding.LoginFragmentBinding
import com.rwtcompany.onlinevegitableshopapp.ui.admin.home.AdminHomeActivity
import com.rwtcompany.onlinevegitableshopapp.ui.user.home.UserHomePageActivity

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var dialog:ProgressDialog
    private lateinit var controller:NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setHasOptionsMenu(true)

        dialog= ProgressDialog(context)
        dialog.setMessage("Getting you in")
        dialog.setCancelable(false)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val pin = binding.etAdminPin.text.toString().trim()
            if (email.isEmpty() || password.isEmpty())
                Toast.makeText(context, "Please enter email and password!", Toast.LENGTH_LONG).show()
            else {
                dialog.show()
                login(email, password, if (pin.isEmpty()) null else pin)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            controller.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        controller=Navigation.findNavController(binding.root)
    }

    private fun login(email: String, password: String, pin: String?) {
        //Normal user login
        if (pin == null) {
            viewModel.login(email, password).observe(viewLifecycleOwner) {
                dialog.dismiss()
                if (it.isSuccessful) {
                    Toast.makeText(context, "Logged in successfully...", Toast.LENGTH_LONG).show()
                    //go to user end
                    startActivity(Intent(activity, UserHomePageActivity::class.java))
                    activity?.finish()
                } else
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        } else {
            this.adminLogin(email, password, pin)
        }
    }

    private fun adminLogin(email: String, password: String, pin: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                //check admin pin
                viewModel.getAdminMetaData().observe(viewLifecycleOwner) { adminMetaData ->
                    when {
                        adminMetaData == null -> {
                            dialog.dismiss()
                            Toast.makeText(activity, "OOPs something went wrong...", Toast.LENGTH_LONG).show()
                            viewModel.logout()
                        }
                        //Pin matches
                        adminMetaData.pin == pin -> {
                            updateAdminMetaData(email)
                        }
                        else -> {
                            dialog.dismiss()
                            Toast.makeText(activity, "PIN is incorrect please try again...", Toast.LENGTH_LONG).show()
                            viewModel.logout()
                        }
                    }
                }
            } else{
                dialog.dismiss()
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateAdminMetaData(email: String) {
        //Get token for notification and go to next activity
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task: Task<InstanceIdResult> ->
                    dialog.dismiss()
                    var token: String? = null
                    if (!task.isSuccessful) {
                        Log.i("Main", task.exception.toString())
                        Toast.makeText(activity, "Notification service is not activated. Please logout and then login again later.", Toast.LENGTH_LONG).show()
                    } else {
                        token = task.result?.token
                    }
                    viewModel.updateAdminMetaData(email, token)
                    Toast.makeText(context, "Logged in successfully...", Toast.LENGTH_LONG).show()
                    startActivity(Intent(activity, AdminHomeActivity::class.java))
                    activity?.finish()
                }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.auth_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.admin)
            binding.etAdminPin.visibility = View.VISIBLE
        return super.onOptionsItemSelected(item)
    }

}