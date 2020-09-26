package com.rwtcompany.onlinevegitableshopapp.ui.admin.orderDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private String orderId;
    private String uuid;

    public MyViewModelFactory(String orderId, String uuid) {
        this.orderId = orderId;
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try{
            return modelClass.getConstructor(String.class,String.class).newInstance(orderId,uuid);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Can't create an instance of "+modelClass,e);
        }
    }
}
