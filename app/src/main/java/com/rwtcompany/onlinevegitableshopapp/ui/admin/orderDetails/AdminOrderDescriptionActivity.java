package com.rwtcompany.onlinevegitableshopapp.ui.admin.orderDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminOrderDescriptionBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.ui.user.orderDetails.UserOrderDescriptionActivity;

public class AdminOrderDescriptionActivity extends AppCompatActivity {
    private ActivityAdminOrderDescriptionBinding binding;
    private AdminOrdersDescriptionViewModel viewModel;

    private DatabaseReference itemsReference;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrderDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        String uuid = intent.getStringExtra("uuid");

        viewModel = new ViewModelProvider(this, new MyViewModelFactory(orderId, uuid)).get(AdminOrdersDescriptionViewModel.class);
        viewModel.orderDetails.observe(this, orderDetails -> {
            if (orderDetails != null) {
                String address = "Name:" + orderDetails.getName()
                        + "\nAddress:" + orderDetails.getAddress()
                        + "\nNumber:" + orderDetails.getNumber();

                String cost = "Rs:" + orderDetails.getTotal();
                String deliveryCharge = orderDetails.getDeliveryCharge();
                if (!deliveryCharge.equals("0"))
                    cost += "\n+" + deliveryCharge;

                String orderStatus = orderDetails.getOrderStatus();
                if (!orderStatus.equals("pending...")) {
                    binding.etEstimateDeliveryTime.setText(orderStatus);
                }
                String requestedDeliveryTime = "Time Requested: " + orderDetails.getRequestTime();

                binding.tvRequestedDeliveryTime.setText(requestedDeliveryTime);
                binding.tvAdminOrderAddress.setText(address);
                binding.tvAdminTotalOrderCost.setText(cost);
            } else {
                Toast.makeText(AdminOrderDescriptionActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
            }
        });

        itemsReference = FirebaseDatabase.getInstance().getReference().child("orders").child(uuid).child(orderId).child("items");

        binding.userOrderDescriptionRecyclerView.setHasFixedSize(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("loading data....");
        dialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        dialog.show();
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(itemsReference, CartItem.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<CartItem, UserOrderDescriptionActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<CartItem, UserOrderDescriptionActivity.MyViewHolder>(options) {
            @NonNull
            @Override
            public UserOrderDescriptionActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_description_custom_layout, parent, false);
                return new UserOrderDescriptionActivity.MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserOrderDescriptionActivity.MyViewHolder holder, int position, @NonNull CartItem model) {
                dialog.dismiss();
                Glide.with(AdminOrderDescriptionActivity.this).load(model.getImageUrl()).into(holder.ivOrderedItem);
                if (model.getName() != null)
                    holder.setName(model.getName().substring(0, 1).toUpperCase() + model.getName().substring(1));
                holder.setPrice(model.getPrice() + "/" + model.getUnit());
                holder.setCost(model.getCost());

                String quantity = model.getQuantity();
                if (model.getUnit().contains("gram")) {
                    quantity += "gram";
                } else if (model.getUnit().contains("kg")) {
                    quantity += "kg";
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
        viewModel.removeOrder();
        dialog.dismiss();
        finish();
    }

    public void onDelivered(View view) {
        dialog.show();
        viewModel.removeOrder();
        dialog.dismiss();
        finish();
    }

    public void onOk(View view) {
        String data = binding.etEstimateDeliveryTime.getText().toString().trim();
        if (data.isEmpty()) {
            Toast.makeText(AdminOrderDescriptionActivity.this, "enter delivery date and time please...", Toast.LENGTH_LONG).show();
        } else {
            dialog.show();
            viewModel.updateOrderStatus(data);
            Toast.makeText(AdminOrderDescriptionActivity.this, "Date and time set as entered....", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            finish();
        }
    }

    public void callUser(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + viewModel.orderDetails.getValue().getNumber())));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
