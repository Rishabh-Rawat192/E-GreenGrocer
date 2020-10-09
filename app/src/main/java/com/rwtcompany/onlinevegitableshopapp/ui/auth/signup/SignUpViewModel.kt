package com.rwtcompany.onlinevegitableshopapp.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rwtcompany.onlinevegitableshopapp.model.AdminMetaData
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted
import com.rwtcompany.onlinevegitableshopapp.repository.Repository

class SignUpViewModel : ViewModel() {
    private val repository=Repository.getRepository()

    fun signUp(email: String, password: String):LiveData<TaskCompleted>{
        return repository.signUp(email, password)
    }

    fun getAdminMetaData(): LiveData<AdminMetaData> {
        return repository.adminMetaData;
    }

    fun logout() {
        repository.logout()
    }

    fun updateAdminMetaData(email: String, token: String?):LiveData<TaskCompleted>{
        return repository.updateAdminMetaData(AdminMetaData(null, null, null, email, token))
    }

    fun saveNewUserData():LiveData<TaskCompleted>{
        return repository.saveNewUserData()
    }

}