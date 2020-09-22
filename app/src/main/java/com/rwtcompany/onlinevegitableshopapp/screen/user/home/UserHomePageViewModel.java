package com.rwtcompany.onlinevegitableshopapp.screen.user.home;

import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.CartItem;

import java.util.ArrayList;

public class UserHomePageViewModel extends ViewModel {
    ArrayList<CartItem> items;
    int totalCost;
    public UserHomePageViewModel(){
        items = new ArrayList<>();
    }
    void addItem(CartItem item){
        items.add(item);
        totalCost+=Integer.parseInt(item.getPrice());
    }
    void removeItem(CartItem item){
        items.remove(item);
        totalCost-=Integer.parseInt(item.getPrice());
    }
    void orderPlaced(){
        items.clear();
        totalCost=0;
    }
}
