package com.rwtcompany.onlinevegitableshopapp.screen.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserOrderDescriptionPageBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.R;

public class UserOrderDescriptionPage extends AppCompatActivity {
    ActivityUserOrderDescriptionPageBinding binding;

    ProgressDialog dialog;

    private DatabaseReference orderedItemsReference;
    private DatabaseReference orderReference;
    private FirebaseAuth mAuth;

    String deliveryCharge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOrderDescriptionPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog=new ProgressDialog(this);
        dialog.setMessage("Canceling....");
        dialog.setCancelable(false);

        binding.userOrderDescriptionRecyclerView.setHasFixedSize(true);
        binding.userOrderDescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent=getIntent();
        String orderId = intent.getStringExtra("orderId");

        orderReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId);

        dialog.show();


        orderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                String address="Address:-"+dataSnapshot.child("address").getValue()+"\n"+"Phone:-"+dataSnapshot.child("number").getValue();
                binding.tvOrderAddress.setText(address);
                String total="Rs:"+dataSnapshot.child("total").getValue();
                deliveryCharge=dataSnapshot.child("deliveryCharge").getValue().toString();
                if(!deliveryCharge.equals("0"))
                    total+="\n+"+deliveryCharge;
                binding.tvTotalOrderCost.setText(total);
                String orderStatus = "Delivery:" + dataSnapshot.child("orderStatus").getValue();
                binding.tvOrderStatus.setText(orderStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(UserOrderDescriptionPage.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        orderedItemsReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId).child("items");
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(orderedItemsReference, CartItem.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<CartItem, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_description_custom_layout, parent, false);
                return new UserOrderDescriptionPage.MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull CartItem model) {
                dialog.dismiss();
                Glide.with(UserOrderDescriptionPage.this).load(model.getImageUrl()).into(holder.ivOrderedItem);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ImageView ivOrderedItem;
        TextView tvOrderedItemName,tvOrderedItemPrice,tvOrderedItemQuantity,tvOrderedItemCost;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            ivOrderedItem = mView.findViewById(R.id.ivOrderedItem);
            tvOrderedItemName = mView.findViewById(R.id.tvOrederedItemName);
            tvOrderedItemPrice = mView.findViewById(R.id.tvOrderedItemPrice);
            tvOrderedItemQuantity = mView.findViewById(R.id.tvOrderedItemQuantity);
            tvOrderedItemCost = mView.findViewById(R.id.tvOrderedItemCost);
        }
        public void setName(String name)
        {
            tvOrderedItemName.setText(name);
        }
        public void setPrice(String price)
        {
            String p="Rs:"+price;
            tvOrderedItemPrice.setText(p);
        }
        public void setQuantity(String quantity)
        {
            String s="Quantity:"+quantity;
            tvOrderedItemQuantity.setText(s);
        }
        public void setCost(String cost)
        {
            String c="Rs:"+cost;
            tvOrderedItemCost.setText(c);
        }
    }

    public void onCancel(View view) {

        dialog.show();
        orderReference.removeValue();
        dialog.dismiss();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
