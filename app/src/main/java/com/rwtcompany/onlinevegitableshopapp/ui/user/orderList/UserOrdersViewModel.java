package com.rwtcompany.onlinevegitableshopapp.ui.user.orderList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.UserOrder;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

import java.util.List;

public class UserOrdersViewModel extends ViewModel {
    LiveData<List<UserOrder>> list;
    public UserOrdersViewModel(){
        Repository repository = Repository.getRepository();
        list= repository.getUserOrders();
    }
}
