package com.rwtcompany.onlinevegitableshopapp.ui.admin.orderList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.AdminOrder;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

import java.util.List;

public class AdminOrdersViewModel extends ViewModel {
    LiveData<List<AdminOrder>> orders;
    public AdminOrdersViewModel(){
        Repository repository = Repository.getRepository();
        orders=repository.getAllOrders();
    }
}
