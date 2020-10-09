package com.rwtcompany.onlinevegitableshopapp.ui.auth.forgotpassword

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rwtcompany.onlinevegitableshopapp.databinding.ForgotPasswordFragmentBinding

class ForgotPasswordFragment : Fragment() {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var binding: ForgotPasswordFragmentBinding
    private lateinit var dialog:ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = ForgotPasswordFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        dialog= ProgressDialog(context)
        dialog.setMessage("Processing...")
        dialog.setCancelable(false)

        binding.btnSendResetLink.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty())
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_LONG).show()
            else {
                sendPasswordResetLink(email);
            }
        }
        return binding.root
    }

    private fun sendPasswordResetLink(email: String) {
        dialog.show()
        viewModel.sendPasswordResetLink(email).observe(viewLifecycleOwner, {
            dialog.dismiss()
            if (it.isSuccessful)
                Toast.makeText(context, "Password Reset Link send to your email...", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        })
    }

}