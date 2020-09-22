package com.rwtcompany.onlinevegitableshopapp.screen.user.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderExtraCharges;
import com.rwtcompany.onlinevegitableshopapp.model.UserDetails;
import com.rwtcompany.onlinevegitableshopapp.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CartViewModel extends ViewModel {
    ArrayList<CartItem> items;
    int totalCost;
    LiveData<UserDetails> userDetails;
    LiveData<OrderExtraCharges> orderExtraCharges;
    private Repository repository;
    public CartViewModel(){
        repository=Repository.getRepository();
        userDetails=repository.getUserDetails();
        orderExtraCharges=repository.getOrderExtraCharge();
    }
    void saveAddress(DeliveryDetails deliveryDetails){
        repository.saveUserAddress(deliveryDetails);
    }
    void placeOrder(String requestTime){
        //Get current time and use it as orderId
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String orderId = formatter.format(date);
        OrderDetails orderDetails = new OrderDetails(userDetails.getValue().getDeliveryDetails(), orderExtraCharges.getValue().getDeliveryCharge(), orderId, "pending...", requestTime, String.valueOf(this.totalCost));
        repository.placeOrder(items,orderDetails);
    }
    void increaseItemQuantity(int index){
        CartItem item = items.get(index);
        int price = Integer.parseInt(item.getPrice());
        int cost=Integer.parseInt(item.getCost())+price;
        //Getting integer of per unit price
        int unitModulus= Integer.parseInt(item.getUnit().substring(0, item.getUnit().indexOf(" ")));
        int quantity = Integer.parseInt(item.getQuantity()) + unitModulus;
        items.get(index).setQuantity(String.valueOf(quantity));
        items.get(index).setCost(String.valueOf(cost));
        totalCost+=price;
    }
    void decrementItemQuantity(int index){
        CartItem item = items.get(index);
        int price=Integer.parseInt(item.getPrice());
        int cost=Integer.parseInt(item.getCost())-price;
        //Getting integer of per unit price
        int unitModulus= Integer.parseInt(item.getUnit().substring(0, item.getUnit().indexOf(" ")));
        int quantity = Integer.parseInt(item.getQuantity())-unitModulus;
        if(cost==0)
            items.remove(index);
        else{
            items.get(index).setQuantity(String.valueOf(quantity));
            items.get(index).setCost(String.valueOf(cost));
        }
        totalCost-=price;
    }
}
