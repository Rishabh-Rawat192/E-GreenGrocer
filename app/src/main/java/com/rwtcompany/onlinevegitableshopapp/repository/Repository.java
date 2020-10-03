package com.rwtcompany.onlinevegitableshopapp.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItemWithKey;
import com.rwtcompany.onlinevegitableshopapp.model.AdminMetaData;
import com.rwtcompany.onlinevegitableshopapp.model.AdminOrder;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderExtraCharges;
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted;
import com.rwtcompany.onlinevegitableshopapp.model.UserDetails;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrder;
import com.rwtcompany.onlinevegitableshopapp.repository.remote.RemoteRepository;

import java.util.List;

public class Repository {
    private static Repository repository = null;

    private RemoteRepository remoteRepository;

    private Repository() {
        remoteRepository = new RemoteRepository();
    }

    public static Repository getRepository() {
        if (repository == null)
            repository = new Repository();
        return repository;
    }

    public void placeOrder(List<CartItem> items, OrderDetails orderDetails) {
        remoteRepository.placeOrder(items, orderDetails);
    }

    public LiveData<TaskCompleted> saveUserAddress(DeliveryDetails details) {
        return remoteRepository.saveUserAddress(details);
    }

    public LiveData<UserDetails> getUserDetails() {
        return remoteRepository.getUserDetails();
    }

    public LiveData<OrderExtraCharges> getOrderExtraCharge() {
        return remoteRepository.getOrderExtraCharge();
    }

    public LiveData<List<UserOrder>> getUserOrders() {
        return remoteRepository.getUserOrders();
    }

    public LiveData<List<AdminOrder>> getAllOrders() {
        return remoteRepository.getAllOrders();
    }

    public LiveData<OrderDetails> getOrderDetails(String orderId) {
        return remoteRepository.getOrderDetails(orderId);
    }

    public LiveData<OrderDetails> getOrderDetails(String orderId, String uuid) {
        return remoteRepository.getOrderDetails(orderId, uuid);
    }

    public LiveData<TaskCompleted> removeOrder(String orderId, String uid) {
        return remoteRepository.removeOrder(orderId, uid);
    }

    public LiveData<TaskCompleted> updateOrderStatus(String orderStatus, String orderId, String uid) {
        return remoteRepository.updateOrderStatus(orderStatus, orderId, uid);
    }

    public LiveData<AdminMetaData> getAdminMetaData() {
        return remoteRepository.getAdminMetaData();
    }

    public LiveData<TaskCompleted> updateAdminMetaData(AdminMetaData adminMetaData) {
        return remoteRepository.updateAdminMetaData(adminMetaData);
    }

    public LiveData<TaskCompleted> addNewProduct(AdminItem item, Uri imageUri) {
        return remoteRepository.addNewProduct(item, imageUri);
    }

    public LiveData<TaskCompleted> updateProduct(AdminItemWithKey item, Uri imageUri) {
        return remoteRepository.updateProduct(item, imageUri);
    }

    public LiveData<List<AdminItemWithKey>> getAllItems() {
        return remoteRepository.getAllItems();
    }

}
