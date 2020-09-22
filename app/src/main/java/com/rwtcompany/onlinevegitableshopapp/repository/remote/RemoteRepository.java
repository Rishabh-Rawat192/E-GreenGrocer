package com.rwtcompany.onlinevegitableshopapp.repository.remote;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderExtraCharges;
import com.rwtcompany.onlinevegitableshopapp.model.UserDetails;

import java.util.List;

public class RemoteRepository {
    DatabaseReference mRef;
    FirebaseAuth mAuth;

    public RemoteRepository(){
        mRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
    }

    public void placeOrder(List<CartItem> items, OrderDetails orderDetails){
        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderDetails.getOrderId());
        //Saving items
        for(CartItem item:items)
            ordersReference.child("items").child(item.getName()).setValue(item);
        //Saving order meta data
        ordersReference.child("address").setValue(orderDetails.getDeliveryDetails().getAddress());
        ordersReference.child("deliveryCharge").setValue(orderDetails.getDeliveryCharge());
        ordersReference.child("name").setValue(orderDetails.getDeliveryDetails().getName());
        ordersReference.child("number").setValue(orderDetails.getDeliveryDetails().getNumber());
        ordersReference.child("orderId").setValue(orderDetails.getOrderId());
        ordersReference.child("orderStatus").setValue(orderDetails.getOrderStatus());
        ordersReference.child("requestTime").setValue(orderDetails.getRequestTime());
        ordersReference.child("total").setValue(orderDetails.getTotal());
    }

    public void saveUserAddress(DeliveryDetails details){
        DatabaseReference userReference=mRef.child("users").child(mAuth.getUid());
        userReference.child("address").setValue(details.getAddress());
        userReference.child("number").setValue(details.getNumber());
        userReference.child("name").setValue(details.getName());
    }

    public LiveData<UserDetails> getUserDetails(){
        DatabaseReference userReference=mRef.child("users").child(mAuth.getUid());
        MutableLiveData<UserDetails> details = new MutableLiveData<>();
        //Getting users saved details
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String address="";
                String number="";
                String name="";
                String email="";
                if (dataSnapshot.hasChild("address"))
                    address = dataSnapshot.child("address").getValue().toString();
                if (dataSnapshot.hasChild("number"))
                    number = dataSnapshot.child("number").getValue().toString();
                if (dataSnapshot.hasChild("name"))
                    name = dataSnapshot.child("name").getValue().toString();
                if (dataSnapshot.hasChild("email"))
                    email = dataSnapshot.child("email").getValue().toString();
                details.setValue(new UserDetails(new DeliveryDetails(address,name,number),email));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                details.setValue(new UserDetails(new DeliveryDetails("","",""),""));
            }
        });
        return details;
    }

    public LiveData<OrderExtraCharges> getOrderExtraCharge(){
        DatabaseReference adminRef=mRef.child("admin");
        MutableLiveData<OrderExtraCharges> orderExtraCharges = new MutableLiveData<>();
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String minOrderPrice="";
                String deliveryCharge="";
                if(dataSnapshot.hasChild("minOrderPrice"))
                    minOrderPrice=dataSnapshot.child("minOrderPrice").getValue(String.class);
                if(dataSnapshot.hasChild("price"))
                    deliveryCharge=dataSnapshot.child("price").getValue().toString();
                orderExtraCharges.setValue(new OrderExtraCharges(minOrderPrice,deliveryCharge));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                orderExtraCharges.setValue(new OrderExtraCharges("",""));
            }
        });
        return orderExtraCharges;
    }
}
