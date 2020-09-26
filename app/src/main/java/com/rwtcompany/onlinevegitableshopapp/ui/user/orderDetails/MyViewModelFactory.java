package com.rwtcompany.onlinevegitableshopapp.ui.user.orderDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private String orderId;

    public MyViewModelFactory( String orderId) {
        this.orderId=orderId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(String.class).newInstance(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't create an instance of "+modelClass,e);
        }
    }
}
