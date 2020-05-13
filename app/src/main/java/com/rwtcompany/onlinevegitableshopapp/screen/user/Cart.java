package com.rwtcompany.onlinevegitableshopapp.screen.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityCartBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItems;
import com.rwtcompany.onlinevegitableshopapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.rwtcompany.onlinevegitableshopapp.screen.user.UserHomePage.addedOnCart;
import static com.rwtcompany.onlinevegitableshopapp.screen.user.UserHomePage.minOrderPrice;
import static com.rwtcompany.onlinevegitableshopapp.screen.user.UserHomePage.nameCost;

public class Cart extends AppCompatActivity {
    ActivityCartBinding binding;

    Dialog dialog;

    boolean orderPlaced = false;

    private DatabaseReference cartReference;
    private DatabaseReference totalCostReference;

    private DatabaseReference ordersReference;
    private DatabaseReference orderTotalCostReference;
    private DatabaseReference orderIdReference;
    private DatabaseReference currentOrderReference;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;


    String address;
    String number;
    String name;
    String deliveryCharge = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Added Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        cartReference = FirebaseDatabase.getInstance().getReference().child("cart").child(mAuth.getUid()).child("items");
        totalCostReference = FirebaseDatabase.getInstance().getReference().child("cart").child(mAuth.getUid()).child("totalCost");

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String orderId = formatter.format(date);

        ordersReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId).child("items");
        orderTotalCostReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId).child("total");
        orderIdReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId).child("orderId");

        orderIdReference.setValue(orderId);

        currentOrderReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId);

        binding.cartRecyclerView.setHasFixedSize(true);
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting things ready...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("address") && dataSnapshot.hasChild("number") && dataSnapshot.hasChild("name")) {
                    address = dataSnapshot.child("address").getValue().toString();
                    number = dataSnapshot.child("number").getValue().toString();
                    name = dataSnapshot.child("name").getValue().toString();
                } else {
                    address = "";
                    number = "";
                    name = "";
                }

                FirebaseDatabase.getInstance().getReference().child("admin").child("price").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        deliveryCharge = dataSnapshot.getValue().toString();
                        String price = "Delivery Charge->" + "Rs:" + deliveryCharge;
                        String tvCost = "Rs:" + sumCost() + "+" + deliveryCharge;
                        binding.tvTotalCostCart.setText(tvCost);
                        if (!UserHomePage.shownDeliveryDialog) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                            builder.setTitle(price);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.show();
                            UserHomePage.shownDeliveryDialog = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersReference.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
        FirebaseRecyclerOptions<CartItems> options =
                new FirebaseRecyclerOptions.Builder<CartItems>()
                        .setQuery(cartReference, CartItems.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<CartItems, UserHomePage.HomePageViewHolder>(options) {
            @NonNull
            @Override
            public UserHomePage.HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_home_cutom_layout, parent, false);
                return new UserHomePage.HomePageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UserHomePage.HomePageViewHolder holder, int position, @NonNull final CartItems model) {
                progressDialog.dismiss();
                holder.setPrice("Rs:" + model.getPrice() + "/" + model.getUnit());

                if (model.getUnit().contains("gram"))
                    holder.setUnit("gram");
                else if (model.getUnit().contains("kg")) {
                    holder.setUnit("kg");
                } else if (model.getUnit().contains("piece"))
                    holder.setUnit("piece");

                holder.userHomeTopLayout.setVisibility(View.VISIBLE);

                holder.setQuantity(String.valueOf(addedOnCart.get(model.getName())));
                holder.setItemCost(String.valueOf(nameCost.get(model.getName())));

                String tvCost = "Rs:" + sumCost() + "+" + deliveryCharge;
                binding.tvTotalCostCart.setText(tvCost);

                holder.setName(model.getName());
                Glide.with(Cart.this).load(model.getImageUrl()).into(holder.ivUserHomeImage);


                ordersReference.child(model.getName()).child("name").setValue(model.getName());
                ordersReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                ordersReference.child(model.getName()).child("price").setValue(model.getPrice());
                ordersReference.child(model.getName()).child("unit").setValue(model.getUnit());
                ordersReference.child(model.getName()).child("quantity").setValue(String.valueOf(addedOnCart.get(model.getName())));
                ordersReference.child(model.getName()).child("cost").setValue(String.valueOf(nameCost.get(model.getName())));

                orderTotalCostReference.setValue(String.valueOf(sumCost()));


                holder.btnUserHomeRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GETTING INTEGER PART FROM RATE OF ITEM
                        String rateQuantity = model.getUnit();
                        if (rateQuantity.contains(" ")) {
                            rateQuantity = rateQuantity.substring(0, rateQuantity.indexOf(" "));
                        }
                        int c = 0;
                        int newQuantity = 0;
                        if (model.getUnit().equals("100 gram") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 100);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("250 gram") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 250);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("500 gram") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 500);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("1 kg") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 1);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("1 piece") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 1);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("6 piece") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 6);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("12 piece") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 12);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("50 gram") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) - 50);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        }

                        if (newQuantity == 0) {
                            addedOnCart.remove(model.getName());
                            nameCost.remove(model.getName());
                            cartReference.child(model.getName()).removeValue();

                            ordersReference.child(model.getName()).removeValue();

                            orderTotalCostReference.setValue(String.valueOf(sumCost()));
                        } else {
                            nameCost.put(model.getName(), c);
                            cartReference.child(model.getName()).child("name").setValue(model.getName());
                            cartReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            cartReference.child(model.getName()).child("price").setValue(model.getPrice());
                            cartReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            cartReference.child(model.getName()).child("quantity").setValue(String.valueOf(newQuantity));
                            cartReference.child(model.getName()).child("cost").setValue(String.valueOf(c));

                            ordersReference.child(model.getName()).child("name").setValue(model.getName());
                            ordersReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            ordersReference.child(model.getName()).child("price").setValue(model.getPrice());
                            ordersReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            ordersReference.child(model.getName()).child("quantity").setValue(String.valueOf(newQuantity));
                            ordersReference.child(model.getName()).child("cost").setValue(String.valueOf(c));


                        }
                        String totalCost = String.valueOf(sumCost());
                        orderTotalCostReference.setValue(totalCost);
                        totalCostReference.setValue(totalCost);

                        if (addedOnCart.isEmpty()) {
                            totalCostReference.removeValue();
                            orderTotalCostReference.removeValue();
                            binding.btnOrder.setVisibility(View.GONE);
                            UserHomePage.shownDeliveryDialog = false;
                            finish();
                        }

                        String tvCost = "Rs:" + totalCost + "+" + deliveryCharge;
                        binding.tvTotalCostCart.setText(tvCost);
                    }
                });
                holder.btnUserHomeAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GETTING INTEGER PART FROM RATE OF ITEM
                        String rateQuantity = model.getUnit();
                        if (rateQuantity.contains(" ")) {
                            rateQuantity = rateQuantity.substring(0, rateQuantity.indexOf(" "));
                        }

                        int c = 0;
                        int newQuantity = 0;
                        if (model.getUnit().equals("100 gram")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 100);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("250 gram")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 250);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("500 gram")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 500);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("1 kg")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 1);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setItemCost(String.valueOf(c));
                            holder.setQuantity(String.valueOf(newQuantity));
                        } else if (model.getUnit().equals("1 piece")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 1);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("6 piece")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 6);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("12 piece")) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 12);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        } else if (model.getUnit().equals("50 gram") && addedOnCart.get(model.getName()) > 0) {
                            addedOnCart.put(model.getName(), addedOnCart.get(model.getName()) + 50);
                            newQuantity = addedOnCart.get(model.getName());
                            c = newQuantity * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
                            holder.setQuantity(String.valueOf(newQuantity));
                            holder.setItemCost(String.valueOf(c));
                        }

                        if (newQuantity != 0) {
                            nameCost.put(model.getName(), c);
                            cartReference.child(model.getName()).child("name").setValue(model.getName());
                            cartReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            cartReference.child(model.getName()).child("price").setValue(model.getPrice());
                            cartReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            cartReference.child(model.getName()).child("quantity").setValue(String.valueOf(newQuantity));
                            cartReference.child(model.getName()).child("cost").setValue(String.valueOf(c));

                            ordersReference.child(model.getName()).child("name").setValue(model.getName());
                            ordersReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            ordersReference.child(model.getName()).child("price").setValue(model.getPrice());
                            ordersReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            ordersReference.child(model.getName()).child("quantity").setValue(String.valueOf(newQuantity));
                            ordersReference.child(model.getName()).child("cost").setValue(String.valueOf(c));

                        }
                        String totalCost = String.valueOf(sumCost());
                        totalCostReference.setValue(totalCost);
                        orderTotalCostReference.setValue(totalCost);

                        String tvCost = "Rs:" + totalCost + "+" + deliveryCharge;
                        binding.tvTotalCostCart.setText(tvCost);

                        binding.btnOrder.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        binding.cartRecyclerView.setAdapter(adapter);
    }

    int sumCost() {
        int totalCost = 0;
        for (String i : nameCost.keySet()) {
            totalCost += nameCost.get(i);
        }

        return totalCost;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!orderPlaced)
            currentOrderReference.removeValue();
    }

    public void order(View view) {

        if (sumCost() < minOrderPrice) {
            Toast.makeText(this, "Min Rs->" + minOrderPrice + " order required", Toast.LENGTH_LONG).show();
        } else {
            if (!address.isEmpty()) {
                dialog.setContentView(R.layout.retrieve_address);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                TextView tvRetrieveAddress = dialog.findViewById(R.id.tvRetriveAddress);
                TextView tvRetrievePhone = dialog.findViewById(R.id.tvRetrievePhone);
                TextView tvRetrieveName = dialog.findViewById(R.id.tvRetrieveName);
                final EditText etRequested = dialog.findViewById(R.id.etRequestedTime);
                tvRetrieveAddress.setText("Address:" + address);
                tvRetrievePhone.setText("Phone:" + number);
                tvRetrieveName.setText("Name:" + name);

                Button btnSelectIt = dialog.findViewById(R.id.btnSelectIt);
                Button btnAddNew = dialog.findViewById(R.id.btnAddNew);

                btnAddNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addAddress();
                    }
                });
                btnSelectIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String requestedTime = etRequested.getText().toString().trim();
                        if (requestedTime.isEmpty()) {
                            Toast.makeText(Cart.this, "Please enter request date and time please", Toast.LENGTH_LONG).show();
                        } else {
                            currentOrderReference.child("address").setValue(address);
                            currentOrderReference.child("number").setValue(number);
                            currentOrderReference.child("name").setValue(name);
                            currentOrderReference.child("deliveryCharge").setValue(deliveryCharge);

                            currentOrderReference.child("orderStatus").setValue("pending...");
                            currentOrderReference.child("requestTime").setValue(requestedTime);

                            emptyCart();
                            orderPlaced = true;
                            dialog.dismiss();
                            finish();
                        }
                    }
                });


            } else {
                addAddress();
            }
        }

    }

    void addAddress() {
        dialog.setContentView(R.layout.add_address);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        Button btnConfirmAddress = dialog.findViewById(R.id.btnConfirmAddress);
        Button btnCancelAddress = dialog.findViewById(R.id.btnCancelAddress);
        final EditText etUserName = dialog.findViewById(R.id.etUserName);
        final EditText etHouseNumber = dialog.findViewById(R.id.etHouseNumber);
        final EditText etColony = dialog.findViewById(R.id.etColony);
        final EditText etLocality = dialog.findViewById(R.id.etLocality);
        final EditText etMobileNumber = dialog.findViewById(R.id.etMoblieNumber);
        final EditText etRequestTime = dialog.findViewById(R.id.etRequestTime);

        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = etHouseNumber.getText().toString().trim() + " " + etColony.getText().toString().trim() + " "
                        + etLocality.getText().toString().trim();
                address = address.trim();
                number = etMobileNumber.getText().toString().trim();
                name = etUserName.getText().toString().trim();
                String requestTime = etRequestTime.getText().toString().trim();
                if (address.isEmpty() || number.isEmpty() || requestTime.isEmpty() || name.isEmpty() || requestTime.isEmpty())
                    Toast.makeText(Cart.this, "enter name,address,mobile number and delivery date and time please", Toast.LENGTH_LONG).show();
                else {
                    userReference.child("address").setValue(address);
                    userReference.child("number").setValue(number);
                    userReference.child("name").setValue(name);

                    currentOrderReference.child("address").setValue(address);
                    currentOrderReference.child("number").setValue(number);
                    currentOrderReference.child("name").setValue(name);
                    currentOrderReference.child("deliveryCharge").setValue(deliveryCharge);

                    currentOrderReference.child("orderStatus").setValue("pending...");
                    currentOrderReference.child("requestTime").setValue(requestTime);

                    emptyCart();
                    orderPlaced = true;
                    dialog.dismiss();
                    finish();
                }

            }
        });
        btnCancelAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void emptyCart() {
        Toast.makeText(Cart.this, "ORDER PLACED SUCCESSFULLY..", Toast.LENGTH_LONG).show();
        addedOnCart.clear();
        nameCost.clear();
        cartReference.removeValue();
        totalCostReference.removeValue();
        UserHomePage.shownDeliveryDialog = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

