package com.rwtcompany.onlinevegitableshopapp.ui.user.cart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityCartBinding;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.model.DeliveryDetails;

import java.util.ArrayList;


public class CartActivity extends AppCompatActivity implements CustomAdapter.OnClickListener {
    private ActivityCartBinding binding;
    private CartViewModel viewModel;

    private CustomAdapter adapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Added Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        //Retrieving and saving data from UserHomePageActivity Intent
        Intent intent=getIntent();
        viewModel.items=(ArrayList< CartItem>)intent.getSerializableExtra("items");
        viewModel.totalCost = intent.getIntExtra("totalCost",0);

        viewModel.orderExtraCharges.observe(this,orderExtraCharges -> {
            //Show Delivery charge alert
            showAlert("Delivery Charge "+getResources().getString(R.string.rs)+orderExtraCharges.getDeliveryCharge());
            setUpTotalCost(viewModel.totalCost,Integer.parseInt(orderExtraCharges.getDeliveryCharge()));
        });
        
        binding.cartRecyclerView.setHasFixedSize(true);

        dialog = new Dialog(this);

        setUpTotalCost(viewModel.totalCost,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        adapter = new CustomAdapter(viewModel.items, this);
        binding.cartRecyclerView.setAdapter(adapter);
    }

    private void setUpTotalCost(int totalCost,int deliveryCharge){
        binding.tvTotalCostCart.setText(getResources().getString(R.string.rs)+totalCost);
        binding.tvDeliveryCharge.setText(getResources().getString(R.string.rs)+deliveryCharge);
    }

    private void showAlert(String message){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    public void order(View view) {
        int minOrderPrice=Integer.parseInt(viewModel.orderExtraCharges.getValue().getMinOrderPrice());
        if (viewModel.totalCost < minOrderPrice) {
            showAlert("Min "+getResources().getString(R.string.rs) + minOrderPrice + " order required");
        } else {
            if (viewModel.userDetails.getValue().getAddress()!=null) {
                dialog.setContentView(R.layout.retrieve_address);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                TextView tvRetrieveAddress = dialog.findViewById(R.id.tvRetriveAddress);
                TextView tvRetrievePhone = dialog.findViewById(R.id.tvRetrievePhone);
                TextView tvRetrieveName = dialog.findViewById(R.id.tvRetrieveName);
                final EditText etRequested = dialog.findViewById(R.id.etRequestedTime);
                tvRetrieveAddress.append("Address:");
                tvRetrieveAddress.append(viewModel.userDetails.getValue().getAddress());
                tvRetrievePhone.append("Phone:");
                tvRetrievePhone.append(viewModel.userDetails.getValue().getNumber());
                tvRetrieveName.append("Name:");
                tvRetrieveName.append(viewModel.userDetails.getValue().getName());

                Button btnSelectIt = dialog.findViewById(R.id.btnSelectIt);
                Button btnAddNew = dialog.findViewById(R.id.btnAddNew);

                btnAddNew.setOnClickListener(v -> addAddress());
                btnSelectIt.setOnClickListener(v -> {
                    String requestedTime = etRequested.getText().toString().trim();
                    if (requestedTime.isEmpty()) {
                        Toast.makeText(CartActivity.this, "Please enter request date and time please", Toast.LENGTH_LONG).show();
                    } else {
                        viewModel.placeOrder(requestedTime);

                        orderSuccessfullyPlaced();
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

        btnConfirmAddress.setOnClickListener(v -> {
            String address = etHouseNumber.getText().toString().trim() + " " + etColony.getText().toString().trim() + " "
                    + etLocality.getText().toString().trim();
            address = address.trim();
            String number = etMobileNumber.getText().toString().trim();
            String name = etUserName.getText().toString().trim();
            String requestTime = etRequestTime.getText().toString().trim();
            if (address.isEmpty() || number.isEmpty() || requestTime.isEmpty() || name.isEmpty())
                Toast.makeText(CartActivity.this, "enter name,address,mobile number and delivery date and time please", Toast.LENGTH_LONG).show();
            else {
                DeliveryDetails deliveryDetails=new DeliveryDetails(address,name,number);
                viewModel.saveAddress(deliveryDetails);
                //update delivery details
                viewModel.userDetails.getValue().setAddress(address);
                viewModel.userDetails.getValue().setName(name);
                viewModel.userDetails.getValue().setNumber(number);
                viewModel.placeOrder(requestTime);

                orderSuccessfullyPlaced();
            }

        });
        btnCancelAddress.setOnClickListener(v -> dialog.dismiss());
    }

    private void orderSuccessfullyPlaced(){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void increaseItemQuantity(int index) {
        viewModel.increaseItemQuantity(index);
        adapter.notifyDataSetChanged();
        setUpTotalCost(viewModel.totalCost,Integer.parseInt(viewModel.orderExtraCharges.getValue().getDeliveryCharge()));
    }

    @Override
    public void decrementItemQuantity(int index) {
        viewModel.decrementItemQuantity(index);
        adapter.notifyDataSetChanged();
        setUpTotalCost(viewModel.totalCost,Integer.parseInt(viewModel.orderExtraCharges.getValue().getDeliveryCharge()));
        if(viewModel.items.isEmpty()){
            setResult(RESULT_OK);
            finish();
        }
    }
}

