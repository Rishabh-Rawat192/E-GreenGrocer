package com.rwtcompany.onlinevegitableshopapp.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderExtraCharges;
import com.rwtcompany.onlinevegitableshopapp.model.UserDetails;
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

    public void saveUserAddress(DeliveryDetails details) {
        remoteRepository.saveUserAddress(details);
    }

    public LiveData<UserDetails> getUserDetails() {
        return remoteRepository.getUserDetails();
    }

    public LiveData<OrderExtraCharges> getOrderExtraCharge() {
        return remoteRepository.getOrderExtraCharge();
    }

}
