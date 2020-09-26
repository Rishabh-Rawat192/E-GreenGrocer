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
import com.rwtcompany.onlinevegitableshopapp.model.AdminOrder;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderDetails;
import com.rwtcompany.onlinevegitableshopapp.model.OrderExtraCharges;
import com.rwtcompany.onlinevegitableshopapp.model.TaskCompleted;
import com.rwtcompany.onlinevegitableshopapp.model.UserDetails;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RemoteRepository {
    DatabaseReference mRef;
    FirebaseAuth mAuth;

    public RemoteRepository() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void placeOrder(List<CartItem> items, OrderDetails orderDetails) {
        DatabaseReference ordersReference = mRef.child("orders").child(mAuth.getUid()).child(orderDetails.getOrderId());
        //Saving order meta data
        ordersReference.setValue(orderDetails);
        //Saving items
        for (CartItem item : items)
            ordersReference.child("items").child(item.getName()).setValue(item);
    }

    public LiveData<TaskCompleted> saveUserAddress(DeliveryDetails details) {
        DatabaseReference userReference = mRef.child("users").child(mAuth.getUid());
        MutableLiveData<TaskCompleted> isSuccessful = new MutableLiveData<>();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("address", details.getAddress());
        updatedData.put("number", details.getNumber());
        updatedData.put("name", details.getName());

        userReference.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isSuccessful.setValue(new TaskCompleted(true, null));
            else
                isSuccessful.setValue(new TaskCompleted(false, task.getException().getMessage()));
        });
        return isSuccessful;
    }

    public LiveData<UserDetails> getUserDetails() {
        DatabaseReference userReference = mRef.child("users").child(mAuth.getUid());
        MutableLiveData<UserDetails> details = new MutableLiveData<>();
        //Getting users saved details
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                details.setValue(userDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                details.setValue(new UserDetails("", "", "", ""));
            }
        });
        return details;
    }

    public LiveData<OrderExtraCharges> getOrderExtraCharge() {
        DatabaseReference adminRef = mRef.child("admin");
        MutableLiveData<OrderExtraCharges> orderExtraCharges = new MutableLiveData<>();
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String minOrderPrice = "";
                String deliveryCharge = "";
                if (dataSnapshot.hasChild("minOrderPrice"))
                    minOrderPrice = dataSnapshot.child("minOrderPrice").getValue(String.class);
                if (dataSnapshot.hasChild("price"))
                    deliveryCharge = dataSnapshot.child("price").getValue().toString();
                orderExtraCharges.setValue(new OrderExtraCharges(minOrderPrice, deliveryCharge));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                orderExtraCharges.setValue(new OrderExtraCharges("", ""));
            }
        });
        return orderExtraCharges;
    }

    public LiveData<List<UserOrder>> getUserOrders() {
        DatabaseReference userOrdersReference = mRef.child("orders").child(mAuth.getUid());
        MutableLiveData<List<UserOrder>> list = new MutableLiveData<>();

        userOrdersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                List<UserOrder> ls = new ArrayList<>();
                //If there are any orders by this user
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        String totalCost = "";
                        String orderStatus = "";
                        if (snapshot.hasChild("total") && snapshot.hasChild("orderStatus")
                                && snapshot.hasChild("deliveryCharge")) {
                            int total;
                            int deliveryCharge = Integer.parseInt(snapshot.child("deliveryCharge").getValue().toString());
                            totalCost = snapshot.child("total").getValue().toString();
                            total = deliveryCharge + Integer.parseInt(totalCost);
                            totalCost = String.valueOf(total);

                            orderStatus = snapshot.child("orderStatus").getValue().toString();
                            ls.add(new UserOrder(id, totalCost, orderStatus));
                        }
                    }
                }
                list.setValue(ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                List<UserOrder> ls = new ArrayList<>();
                list.setValue(ls);
            }
        });

        return list;
    }

    public LiveData<List<AdminOrder>> getAllOrders() {
        DatabaseReference orderRef = mRef.child("orders");
        MutableLiveData<List<AdminOrder>> orders = new MutableLiveData<>();
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AdminOrder> list = new ArrayList<>();
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    String uuid = users.getKey();
                    for (DataSnapshot userOrders : users.getChildren()) {
                        if (userOrders.hasChild("orderStatus") && userOrders.hasChild("deliveryCharge")
                                && userOrders.hasChild("total")) {
                            String id = userOrders.getKey();
                            String orderStatus = userOrders.child("orderStatus").getValue().toString();
                            int deliveryCharge = Integer.parseInt(userOrders.child("deliveryCharge").getValue().toString());

                            String total = String.valueOf(Integer.parseInt(userOrders.child("total").getValue().toString()) + deliveryCharge);
                            list.add(new AdminOrder(id, total, orderStatus, uuid));
                        }
                    }
                }
                if (list.isEmpty())
                    orders.setValue(null);
                else
                    orders.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                orders.setValue(null);
            }
        });

        return orders;
    }

    public LiveData<OrderDetails> getOrderDetails(String orderId) {
        return this.getOrderDetails(orderId, mAuth.getUid());
    }

    public LiveData<OrderDetails> getOrderDetails(String orderId, String uid) {
        DatabaseReference orderReference = mRef.child("orders").child(uid).child(orderId);
        MutableLiveData<OrderDetails> data = new MutableLiveData<>();
        orderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("address") && dataSnapshot.hasChild("name")) {
                    OrderDetails orderDetails = dataSnapshot.getValue(OrderDetails.class);
                    data.setValue(orderDetails);
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<TaskCompleted> removeOrder(String orderId, String uid) {
        DatabaseReference orderRef = mRef.child("orders").child(uid).child(orderId);
        MutableLiveData<TaskCompleted> isDeleted = new MutableLiveData<>();
        orderRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isDeleted.setValue(new TaskCompleted(true, null));
            else
                isDeleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
        });
        return isDeleted;
    }


    public LiveData<TaskCompleted> updateOrderStatus(String orderStatus, String orderId, String uid) {
        DatabaseReference orderStatusRef = mRef.child("orders").child(uid).child(orderId).child("orderStatus");
        MutableLiveData<TaskCompleted> isNewOrderStatusSaved = new MutableLiveData<>();
        orderStatusRef.setValue(orderStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isNewOrderStatusSaved.setValue(new TaskCompleted(true,null));
            else
                isNewOrderStatusSaved.setValue(new TaskCompleted(false, Objects.requireNonNull(task.getException()).getMessage()));
        });
        return isNewOrderStatusSaved;
    }
}
