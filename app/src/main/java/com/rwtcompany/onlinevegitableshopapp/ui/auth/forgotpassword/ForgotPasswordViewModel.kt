package com.rwtcompany.onlinevegitableshopapp.ui.auth.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted
import com.rwtcompany.onlinevegitableshopapp.repository.Repository

class ForgotPasswordViewModel : ViewModel() {
    private val repository = Repository.getRepository()

    fun sendPasswordResetLink(email: String): LiveData<TaskCompleted> {
        return repository.sendPasswordResetLink(email)
    }
}