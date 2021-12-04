package com.rwtcompany.onlinevegitableshopapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted
import com.rwtcompany.onlinevegitableshopapp.repository.Repository

class HomeFragmentViewModel:ViewModel() {
    private val repository= Repository.getRepository()

    fun signInWithCredential(credential:AuthCredential): LiveData<TaskCompleted> {
        return repository.signInWithCredential(credential)
    }

    fun saveNewUserData():LiveData<TaskCompleted>{
        return repository.saveNewUserData()
    }
}