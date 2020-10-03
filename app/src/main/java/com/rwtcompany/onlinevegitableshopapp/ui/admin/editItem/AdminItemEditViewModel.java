package com.rwtcompany.onlinevegitableshopapp.ui.admin.editItem;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItemWithKey;
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

public class AdminItemEditViewModel extends ViewModel {
    private AdminItemWithKey item;
    private Uri imageUri;
    private Repository repository;

    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public AdminItemEditViewModel(){
        repository = Repository.getRepository();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public AdminItemWithKey getItem() {
        return item;
    }

    public void setItem(AdminItemWithKey item) {
        this.item = item;
    }

    public LiveData<TaskCompleted> updateItem(){
        return repository.updateProduct(item, imageUri);
    }
}
