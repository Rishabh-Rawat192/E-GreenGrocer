package com.rwtcompany.onlinevegitableshopapp.ui.admin.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItemWithKey;
import com.rwtcompany.onlinevegitableshopapp.model.AdminMetaData;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

import java.util.List;

public class AdminHomeViewModel extends ViewModel {
    LiveData<AdminMetaData> adminMetaData;
    LiveData<List<AdminItemWithKey>> items;
    private Repository repository;

    public AdminHomeViewModel() {
        repository = Repository.getRepository();
        adminMetaData = repository.getAdminMetaData();
        items = repository.getAllItems();
    }

    public void updateAdminMetaData(AdminMetaData adminMetaData) {
        repository.updateAdminMetaData(adminMetaData);
    }
}
