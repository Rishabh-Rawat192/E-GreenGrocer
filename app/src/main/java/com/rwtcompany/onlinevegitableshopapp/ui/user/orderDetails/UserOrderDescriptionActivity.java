package com.rwtcompany.onlinevegitableshopapp.ui.user.orderDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserOrderDescriptionBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.R;

public class UserOrderDescriptionActivity extends AppCompatActivity {
    private ActivityUserOrderDescriptionBinding binding;
    private UserOrderDescriptionViewModel viewModel;

    private ProgressDialog dialog;

    private DatabaseReference orderedItemsReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOrderDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        String orderId = intent.getStringExtra("orderId");

        viewModel = new ViewModelProvider(this, new MyViewModelFactory(orderId)).get(UserOrderDescriptionViewModel.class);
        viewModel.orderDetails.observe(this,orderDetails -> {
            String address="Address:-"+orderDetails.getAddress()+"\n"+"Phone:-"+orderDetails.getNumber();
            binding.tvOrderAddress.setText(address);
            String total="Rs:"+orderDetails.getTotal();
            String deliveryCharge=orderDetails.getDeliveryCharge();
            if(!deliveryCharge.equals("0"))
                total+="\n+"+deliveryCharge;
            binding.tvTotalOrderCost.setText(total);
            String orderStatus = "Delivery:" + orderDetails.getOrderStatus();
            binding.tvOrderStatus.setText(orderStatus);
        });

        dialog=new ProgressDialog(this);
        dialog.setMessage("Canceling....");
        dialog.setCancelable(false);

        binding.userOrderDescriptionRecyclerView.setHasFixedSize(true);

        orderedItemsReference = FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid()).child(orderId).child("items");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        dialog.show();
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(orderedItemsReference, CartItem.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<CartItem,MyViewHolder> adapter = new FirebaseRecyclerAdapter<CartItem, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_description_custom_layout, parent, false);
                return new UserOrderDescriptionActivity.MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull CartItem model) {
                dialog.dismiss();
                Glide.with(UserOrderDescriptionActivity.this).load(model.getImageUrl()).into(holder.ivOrderedItem);
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
        public ImageView ivOrderedItem;
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
        viewModel.removeOrder(mAuth.getUid());
        dialog.dismiss();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}