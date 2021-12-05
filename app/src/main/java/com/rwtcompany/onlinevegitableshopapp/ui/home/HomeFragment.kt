package com.rwtcompany.onlinevegitableshopapp.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rwtcompany.onlinevegitableshopapp.R
import com.rwtcompany.onlinevegitableshopapp.databinding.FragmentHomeBinding
import com.rwtcompany.onlinevegitableshopapp.ui.user.address.UserAddressActivity
import com.rwtcompany.onlinevegitableshopapp.ui.user.home.UserHomePageActivity


class HomeFragment : Fragment() {

    companion object{
        var RC_SIGN_IN =1
    }

    private lateinit var binding:FragmentHomeBinding
    private lateinit var controller:NavController
    private lateinit var viewModel: HomeFragmentViewModel

    private  lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentHomeBinding.inflate(inflater)
        viewModel= ViewModelProvider(this)[HomeFragmentViewModel::class.java]
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1021043518874-6fuj6bo01b3vvr6dut2cuj6q5lu4t3hu.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        //Remove already sign in account so that
        //dialog will again pop up and ask user
        // to choose a new google account
        googleSignInClient.signOut()

        binding.signInButton.setOnClickListener {
            signIn()
        }

        binding.btnCreateNewAccount.setOnClickListener {
            controller.navigate(R.id.action_homeFragment_to_signUpFragment)
        }
        binding.btnLogin.setOnClickListener {
            controller.navigate(R.id.action_homeFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        controller=Navigation.findNavController(binding.root)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        print("Starting intent")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(context,"OOps something went wrong!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        viewModel.signInWithCredential(credential).observe(viewLifecycleOwner){
            if (it.isSuccessful) {
                Toast.makeText(context, "Signed In successfully...", Toast.LENGTH_LONG).show()
                //Save user data to DB
                viewModel.saveNewUserData()
                //go to user end
                startActivity(Intent(activity, UserHomePageActivity::class.java))
                activity?.finish()
            } else
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
        }
    }
}