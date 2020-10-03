package com.rwtcompany.onlinevegitableshopapp.ui.admin.addItem;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

public class AdminAddItemViewModel extends ViewModel {
    private Repository repository;
    private Uri imageUri;

    public AdminAddItemViewModel() {
        repository = Repository.getRepository();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public LiveData<TaskCompleted> addNewProduct(AdminItem item) {
        return repository.addNewProduct(item, imageUri);
    }

    public void productAdded(){
        setImageUri(null);
    }
}
