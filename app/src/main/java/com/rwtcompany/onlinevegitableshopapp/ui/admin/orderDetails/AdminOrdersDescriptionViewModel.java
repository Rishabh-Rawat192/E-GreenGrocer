package com.rwtcompany.onlinevegitableshopapp.ui.admin.orderDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

public class AdminOrdersDescriptionViewModel extends ViewModel {
    LiveData<OrderDetails> orderDetails;
    private String orderId,uid;
    private Repository repository;
    public AdminOrdersDescriptionViewModel(String orderId,String uid){
        this.orderId=orderId;
        this.uid=uid;
        repository=Repository.getRepository();
        orderDetails = repository.getOrderDetails(orderId, uid);
    }
    public void removeOrder(){
        repository.removeOrder(this.orderId, this.uid);
    }
    public void updateOrderStatus(String orderStatus){
        repository.updateOrderStatus(orderStatus, orderId, uid);
    }
}
