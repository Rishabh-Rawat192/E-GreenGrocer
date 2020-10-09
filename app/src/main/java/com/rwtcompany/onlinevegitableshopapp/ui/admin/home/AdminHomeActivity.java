package com.rwtcompany.onlinevegitableshopapp.ui.admin.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminHomeBinding;
import com.rwtcompany.onlinevegitableshopapp.model.AdminMetaData;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.addItem.AdminAddItemActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.MainActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.orderList.AdminOrdersActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private ActivityAdminHomeBinding binding;
    private AdminHomeViewModel viewModel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Your Items");
        mAuth=FirebaseAuth.getInstance();

        setSupportActionBar(binding.toolbar);

        viewModel =new ViewModelProvider(this).get(AdminHomeViewModel.class);

        binding.adminRecyclerView.hasFixedSize();

        //Navigation view header components
        final TextView tvDeliveryCharge=binding.navigation.getHeaderView(0).findViewById(R.id.tvDeliveryCharge);
        final TextView tvMinOrderCharge=binding.navigation.getHeaderView(0).findViewById(R.id.tvMinOrderCharge);
        Button btnChangeDeliveryCharge = binding.navigation.getHeaderView(0).findViewById(R.id.btnChangeDeliveryCharge);
        Button btnChangeMinOrderCharge = binding.navigation.getHeaderView(0).findViewById(R.id.btnChangeMinOrderCharge);

        viewModel.adminMetaData.observe(this,adminMetaData -> {
            if(adminMetaData!=null){
                String deliveryCharge="Delivery Charge : "+getResources().getString(R.string.rs)+adminMetaData.getDeliveryCharge();
                tvDeliveryCharge.setText(deliveryCharge);
                String minOrderCharge="Min Order Charge : "+getResources().getString(R.string.rs)+adminMetaData.getMinOrderPrice();
                tvMinOrderCharge.setText(minOrderCharge);
            }
        });

        viewModel.items.observe(this,adminItems -> {
            if(adminItems!=null){
                MyAdapter adapter = new MyAdapter(adminItems);
                binding.adminRecyclerView.setAdapter(adapter);
            }else {
                Toast.makeText(this, "Something went wrong...",Toast.LENGTH_LONG).show();
            }
        });

        btnChangeDeliveryCharge.setOnClickListener(v -> {
            final Dialog deliveryPriceDialog = new Dialog(AdminHomeActivity.this);
            deliveryPriceDialog.setContentView(R.layout.admin_delivery_price_layout);
            deliveryPriceDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            deliveryPriceDialog.show();
            final EditText etDeliveryPrice = deliveryPriceDialog.findViewById(R.id.etDeliveryPrice);
            final Button btnSubmitDeliveryPrice = deliveryPriceDialog.findViewById(R.id.btnSubmitDeliveryPrice);
            btnSubmitDeliveryPrice.setOnClickListener(v12 -> {
                String price=etDeliveryPrice.getText().toString();
                if(price.isEmpty())
                {
                    Toast.makeText(AdminHomeActivity.this,"enter delivery price please",Toast.LENGTH_LONG).show();
                }
                else
                {
                    viewModel.updateAdminMetaData(new AdminMetaData(null,price,null,null,null));
                    Toast.makeText(AdminHomeActivity.this,"price set!!",Toast.LENGTH_LONG).show();
                    deliveryPriceDialog.dismiss();
                }
            });
        });

        btnChangeMinOrderCharge.setOnClickListener(v -> {
            final Dialog orderCostDialog=new Dialog(AdminHomeActivity.this);
            orderCostDialog.setContentView(R.layout.change_min_order_price_custom_layout);
            orderCostDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            final EditText etMinOrderPrice = orderCostDialog.findViewById(R.id.etMinOrderPrice);
            Button btnMinOrderPrice = orderCostDialog.findViewById(R.id.btnMinOrderPrice);
            btnMinOrderPrice.setOnClickListener(v1 -> {
                String price=etMinOrderPrice.getText().toString();
                if(price.isEmpty())
                {
                    Toast.makeText(AdminHomeActivity.this,"enter price please",Toast.LENGTH_LONG).show();
                }
                else {
                    viewModel.updateAdminMetaData(new AdminMetaData(price,null,null,null,null));
                    Toast.makeText(AdminHomeActivity.this,"price set!!",Toast.LENGTH_LONG).show();
                    orderCostDialog.dismiss();
                }
            });
            orderCostDialog.show();
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_homepage_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.changeAdminPin:
                    final Dialog pinDialog = new Dialog(this);
                    pinDialog.setContentView(R.layout.admin_pin_change_layout);
                    pinDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    pinDialog.show();
                    final EditText etOldPin = pinDialog.findViewById(R.id.etOldPin);
                    final EditText etNewPin = pinDialog.findViewById(R.id.etNewPin);
                    Button btnPinCancel = pinDialog.findViewById(R.id.btnPinCancel);
                    Button btnPinConfirm = pinDialog.findViewById(R.id.btnPinConfirm);
                    btnPinCancel.setOnClickListener(v -> pinDialog.dismiss());
                    btnPinConfirm.setOnClickListener(v -> {
                        String oldPin=etOldPin.getText().toString().trim();
                        String newPin=etNewPin.getText().toString().trim();
                        if (oldPin.isEmpty()||newPin.isEmpty()){
                            Toast.makeText(AdminHomeActivity.this,"please enter old and new pin...",Toast.LENGTH_LONG).show();
                        }
                        else if(!oldPin.equals(viewModel.adminMetaData.getValue().getPin()))
                        {
                            Toast.makeText(AdminHomeActivity.this,"incorrect old pin...",Toast.LENGTH_LONG).show();
                        }
                        else {
                            viewModel.updateAdminMetaData(new AdminMetaData(null,null,newPin,null,null));
                            Toast.makeText(AdminHomeActivity.this,"new pin set successfully...",Toast.LENGTH_LONG).show();
                            pinDialog.dismiss();
                        }
                    });

                    break;
                case R.id.addItem:
                    startActivity(new Intent(AdminHomeActivity.this, AdminAddItemActivity.class));
                    break;
                case R.id.logout:
                    mAuth.signOut();
                    removeFCMToken();
                    startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.adminOrders:
                    startActivity(new Intent(AdminHomeActivity.this, AdminOrdersActivity.class));
                    break;
                case android.R.id.home:
                    binding.drawer.openDrawer(GravityCompat.START);
            }

        return super.onOptionsItemSelected(item);
    }

    private void removeFCMToken(){
        viewModel.updateAdminMetaData(new AdminMetaData(null,null,null,null,""));
    }

}
