package com.rwtcompany.onlinevegitableshopapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.rwtcompany.onlinevegitableshopapp.R
import com.rwtcompany.onlinevegitableshopapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var controller:NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentHomeBinding.inflate(inflater)

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
}