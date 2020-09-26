package com.rwtcompany.onlinevegitableshopapp.ui.user.orderDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

public class UserOrderDescriptionViewModel extends ViewModel {
    LiveData<OrderDetails> orderDetails;
    private Repository repository;

    private String orderId;
    public UserOrderDescriptionViewModel(String orderId) {
        this.orderId=orderId;
        repository = Repository.getRepository();
        orderDetails = repository.getOrderDetails(orderId);
    }
    public void removeOrder(String uid){
        repository.removeOrder(orderId,uid);
    }
}
