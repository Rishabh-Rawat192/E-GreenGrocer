package com.rwtcompany.onlinevegitableshopapp.screen.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminOrdersDescriptionBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.R;

public class AdminOrdersDescription extends AppCompatActivity {
    ActivityAdminOrdersDescriptionBinding binding;

    private DatabaseReference orderReference;
    private DatabaseReference itemsReference;

    ProgressDialog dialog;

    String deliveryCharge;

    String orderId,uuid;
    String number="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        orderId=intent.getStringExtra("orderId");
        uuid=intent.getStringExtra("uuid");

        orderReference = FirebaseDatabase.getInstance().getReference().child("orders").child(uuid).child(orderId);
        itemsReference=orderReference.child("items");

        binding.userOrderDescriptionRecyclerView.setHasFixedSize(true);
        binding.userOrderDescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog = new ProgressDialog(this);
        dialog.setMessage("loading data....");
        dialog.setCancelable(false);
        dialog.show();


        orderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("address")&&dataSnapshot.hasChild("name"))
                {
                    number=dataSnapshot.child("number").getValue().toString();
                    String address="Name:"+dataSnapshot.child("name").getValue().toString()
                            + "\nAddress:"+dataSnapshot.child("address").getValue().toString()
                            +"\nNumber:"+number;

                    String cost="Rs:"+dataSnapshot.child("total").getValue().toString();
                    deliveryCharge=dataSnapshot.child("deliveryCharge").getValue().toString();
                    if(!deliveryCharge.equals("0"))
                        cost+="\n+"+deliveryCharge;

                    String orderStatus=dataSnapshot.child("orderStatus").getValue().toString();
                    if (!orderStatus.equals("pending...")) {
                        binding.etEstimateDeliveryTime.setText(orderStatus);
                    }
                    String requestedDeliveryTime="Time Requested: "+dataSnapshot.child("requestTime").getValue().toString();
                    binding.tvRequestedDeliveryTime.setText(requestedDeliveryTime);
                    binding.tvAdminOrderAddress.setText(address);
                    binding.tvAdminTotalOrderCost.setText(cost);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(AdminOrdersDescription.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        dialog.show();
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(itemsReference, CartItem.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<CartItem, UserOrderDescriptionPage.MyViewHolder>(options) {
            @NonNull
            @Override
            public UserOrderDescriptionPage.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_description_custom_layout, parent, false);
                return new UserOrderDescriptionPage.MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserOrderDescriptionPage.MyViewHolder holder, int position, @NonNull CartItem model) {
                dialog.dismiss();
                Glide.with(AdminOrdersDescription.this).load(model.getImageUrl()).into(holder.ivOrderedItem);
                holder.setName(model.getName());
                holder.setPrice(model.getPrice()+"/"+model.getUnit());
                holder.setCost(model.getCost());

                String quantity=model.getQuantity();
                if (model.getUnit().contains("gram")) {
                    quantity+="gram";
                } else if (model.getUnit().contains("kg")) {
                    quantity+="kg";
                } else if (model.getUnit().contains("piece")) {
                    quantity += "piece";
                }
                holder.setQuantity(quantity);
            }
        };
        binding.userOrderDescriptionRecyclerView.setAdapter(adapter);
    }


    public void onCancel(View view) {
        dialog.show();
        orderReference.removeValue();
        dialog.dismiss();
        finish();
    }

    public void onDelivered(View view) {
        dialog.show();
        orderReference.removeValue();
        dialog.dismiss();
        finish();
    }

    public void onOk(View view) {
        String data=binding.etEstimateDeliveryTime.getText().toString().trim();
        if(data.isEmpty())
        {
            Toast.makeText(AdminOrdersDescription.this,"enter delivery date and time please...",Toast.LENGTH_LONG).show();
        }
        else {
            dialog.show();
            orderReference.child("orderStatus").setValue(data);
            Toast.makeText(AdminOrdersDescription.this,"Date and time set as entered....",Toast.LENGTH_LONG).show();
            dialog.dismiss();
            finish();
        }

    }

    public void callUser(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number)));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
