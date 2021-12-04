package com.rwtcompany.onlinevegitableshopapp.repository.remote;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteRepository {
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    private static String GENERAL_ERROR_MESSAGE = "OOps something went wrong...";

    public RemoteRepository() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        //Update mAuth when state changes
        new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth=firebaseAuth;
            }
        };
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
            else if (task.getException() != null)
                isDeleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isDeleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });
        return isDeleted;
    }


    public LiveData<TaskCompleted> updateOrderStatus(String orderStatus, String orderId, String uid) {
        DatabaseReference orderStatusRef = mRef.child("orders").child(uid).child(orderId).child("orderStatus");
        MutableLiveData<TaskCompleted> isNewOrderStatusSaved = new MutableLiveData<>();
        orderStatusRef.setValue(orderStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isNewOrderStatusSaved.setValue(new TaskCompleted(true, null));
            else if (task.getException() != null)
                isNewOrderStatusSaved.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isNewOrderStatusSaved.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });
        return isNewOrderStatusSaved;
    }

    public LiveData<AdminMetaData> getAdminMetaData() {
        DatabaseReference adminRef = mRef.child("admin");
        MutableLiveData<AdminMetaData> data = new MutableLiveData<>();

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String minOrderPrice = dataSnapshot.child("minOrderPrice").getValue(String.class);
                String pin = dataSnapshot.child("pin").getValue(String.class);
                String deliveryCharge = dataSnapshot.child("price").getValue(String.class);
                String token = dataSnapshot.child("token").getValue(String.class);
                String email = dataSnapshot.child("user").getValue(String.class);

                data.setValue(new AdminMetaData(minOrderPrice, deliveryCharge, pin, email, token));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                data.setValue(null);
            }
        });

        return data;

    }

    public LiveData<TaskCompleted> updateAdminMetaData(AdminMetaData adminMetaData) {
        DatabaseReference adminRef = mRef.child("admin");
        MutableLiveData<TaskCompleted> isComplete = new MutableLiveData<>();

        HashMap<String, Object> updatedData = new HashMap<>();
        if (adminMetaData.getMinOrderPrice() != null)
            updatedData.put("minOrderPrice", adminMetaData.getMinOrderPrice());
        if (adminMetaData.getPin() != null)
            updatedData.put("pin", adminMetaData.getPin());
        if (adminMetaData.getDeliveryCharge() != null)
            updatedData.put("price", adminMetaData.getDeliveryCharge());
        if (adminMetaData.getToken() != null)
            updatedData.put("token", adminMetaData.getToken());
        if (adminMetaData.getEmail() != null)
            updatedData.put("user", adminMetaData.getEmail());

        adminRef.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isComplete.setValue(new TaskCompleted(true, null));
            else if (task.getException() != null)
                isComplete.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isComplete.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });

        return isComplete;
    }

    public LiveData<TaskCompleted> addNewProduct(AdminItem item, Uri imageUri) {
        DatabaseReference itemRef = mRef.child("items").push();
        MutableLiveData<TaskCompleted> isTaskCompleted = new MutableLiveData<>();

        String imageName = String.valueOf(System.currentTimeMillis());
        final StorageReference ref = mStorage.child("images").child(imageName);
        UploadTask uploadTask = ref.putFile(imageUri);

        //Uploading image
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String imageUrl = downloadUri.toString();

                item.setImageUrl(imageUrl);
                //Saving item details in DB
                itemRef.setValue(item);
                itemRef.child("imageName").setValue(imageName);

                if (item.getUnit().equals("out of stock"))
                    itemRef.child("available").setValue(false);
                else
                    itemRef.child("available").setValue(true);

                isTaskCompleted.setValue(new TaskCompleted(true, null));
            } else {
                if (task.getException() != null)
                    isTaskCompleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
                else
                    isTaskCompleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
                //Delete uploaded image
                ref.delete();
            }
        });

        return isTaskCompleted;
    }

    public LiveData<TaskCompleted> updateProduct(AdminItemWithKey item, Uri imageUri) {
        DatabaseReference itemRef = mRef.child("items").child(item.getKey());
        MutableLiveData<TaskCompleted> isTaskCompleted = new MutableLiveData<>();

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("name", item.getAdminItem().getName());
        newData.put("price", item.getAdminItem().getPrice());
        newData.put("unit", item.getAdminItem().getUnit());

        if (item.getAdminItem().getUnit().equals("out of stock"))
            newData.put("available", false);
        else
            newData.put("available", true);

        //if image not to be updated
        if (imageUri == null) {
            itemRef.updateChildren(newData).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    isTaskCompleted.setValue(new TaskCompleted(true, null));
                else if (task.getException() != null)
                    isTaskCompleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
            });
        } else {
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StorageReference oldImageRef = mStorage.child("images");
                    //key as imageName
                    String oldImageName = item.getKey();
                    String newImageName = String.valueOf(System.currentTimeMillis());

                    //Delete old image and use newImageName if imageName doesn't exists
                    if (!dataSnapshot.hasChild("imageName")) {
                        oldImageRef.child(oldImageName).delete();
                    }
                    //Use old image name if imageName exists
                    else
                        newImageName = dataSnapshot.child("imageName").getValue(String.class);

                    newData.put("imageName", newImageName);

                    final StorageReference ref = mStorage.child("images").child(newImageName);
                    UploadTask uploadTask = ref.putFile(imageUri);

                    //Uploading newImage
                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imageUrl = downloadUri.toString();

                            newData.put("imageUrl", imageUrl);

                            //Updating item details in DB
                            itemRef.updateChildren(newData).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    isTaskCompleted.setValue(new TaskCompleted(true, null));
                                } else if (task1.getException() != null)
                                    isTaskCompleted.setValue(new TaskCompleted(false, task1.getException().getMessage()));
                                else
                                    isTaskCompleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
                            });

                        } else {
                            if (task.getException() != null)
                                isTaskCompleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
                            else
                                isTaskCompleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
                            //Delete uploaded image
                            ref.delete();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isTaskCompleted.setValue(new TaskCompleted(false, databaseError.getMessage()));
                }
            });

        }

        return isTaskCompleted;
    }

    public LiveData<List<AdminItemWithKey>> getAllItems() {
        DatabaseReference itemRef = mRef.child("items");
        MutableLiveData<List<AdminItemWithKey>> items = new MutableLiveData<>();

        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AdminItemWithKey> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(new AdminItemWithKey(snapshot.getValue(AdminItem.class), snapshot.getKey()));
                }
                items.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                items.setValue(null);
            }
        });

        return items;
    }

    ///////////////////////////////////////
    //////////Authentication/////////////
    //////////////////////////////////////
    public LiveData<TaskCompleted> login(String email, String password) {
        MutableLiveData<TaskCompleted> isLoggedIn = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isLoggedIn.setValue(new TaskCompleted(true, null));
            else if(task.getException()!=null)
                isLoggedIn.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isLoggedIn.setValue(new TaskCompleted(false,GENERAL_ERROR_MESSAGE));
        });
        return isLoggedIn;
    }

    public void logout(){
        mAuth.signOut();
    }

    public LiveData<TaskCompleted> signUp(String email, String password) {
        MutableLiveData<TaskCompleted> isSignedUp = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isSignedUp.setValue(new TaskCompleted(true, null));
            else if(task.getException()!=null)
                isSignedUp.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isSignedUp.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });

        return isSignedUp;
    }

    public LiveData<TaskCompleted> signInWithCredential(AuthCredential credential){
        MutableLiveData<TaskCompleted> isSignIn=new MutableLiveData<>();
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isSignIn.setValue(new TaskCompleted(true, null));
            else if(task.getException()!=null)
                isSignIn.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isSignIn.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });

        return isSignIn;
    }

    public LiveData<TaskCompleted> saveNewUserData(){
        MutableLiveData<TaskCompleted> isTaskCompleted = new MutableLiveData<>();
        mRef.child("users").child(mAuth.getUid()).child("email").setValue(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isTaskCompleted.setValue(new TaskCompleted(true, null));
            else if(task.getException()!=null)
                isTaskCompleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isTaskCompleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });

        return isTaskCompleted;
    }

    public LiveData<TaskCompleted> sendPasswordResetLink(String email){
        MutableLiveData<TaskCompleted> isTaskCompleted = new MutableLiveData<>();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                isTaskCompleted.setValue(new TaskCompleted(true, null));
            else if(task.getException()!=null)
                isTaskCompleted.setValue(new TaskCompleted(false, task.getException().getMessage()));
            else
                isTaskCompleted.setValue(new TaskCompleted(false, GENERAL_ERROR_MESSAGE));
        });

        return isTaskCompleted;
    }
}
