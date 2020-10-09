package com.rwtcompany.onlinevegitableshopapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.core.Repo
import com.rwtcompany.onlinevegitableshopapp.model.AdminMetaData
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted
import com.rwtcompany.onlinevegitableshopapp.repository.Repository

class LoginViewModel : ViewModel() {
    private val repository = Repository.getRepository()

    fun login(email: String, password: String): LiveData<TaskCompleted> {
        return repository.login(email, password)
    }

    fun getAdminMetaData(): LiveData<AdminMetaData> {
        return repository.adminMetaData;
    }

    fun logout() {
        repository.logout()
    }

    fun updateAdminMetaData(email: String,token:String?):LiveData<TaskCompleted>{
        return repository.updateAdminMetaData(AdminMetaData(null,null,null,email,token))
    }
}