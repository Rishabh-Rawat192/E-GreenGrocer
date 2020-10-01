package com.rwtcompany.onlinevegitableshopapp.ui.admin.home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminHomePageBinding;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.addItem.AdminAddItem;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.editItem.AdminItemEdit;
import com.rwtcompany.onlinevegitableshopapp.ui.login.MainActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.orderList.AdminOrdersActivity;

public class AdminHomePage extends AppCompatActivity {

    ActivityAdminHomePageBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference adminReference;

    private String adminPin;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Your Items");
        mAuth=FirebaseAuth.getInstance();

        setSupportActionBar(binding.toolbar);

        binding.adminRecyclerView.hasFixedSize();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
        adminReference = FirebaseDatabase.getInstance().getReference().child("admin");
        dialog = new ProgressDialog(this);
        dialog.setMessage("getting data...");
        dialog.setCancelable(false);
        dialog.show();

        final TextView tvDeliveryCharge=binding.navigation.getHeaderView(0).findViewById(R.id.tvDeliveryCharge);
        final TextView tvMinOrderCharge=binding.navigation.getHeaderView(0).findViewById(R.id.tvMinOrderCharge);
        Button btnChangeDeliveryCharge = binding.navigation.getHeaderView(0).findViewById(R.id.btnChangeDeliveryCharge);
        Button btnChangeMinOrderCharge = binding.navigation.getHeaderView(0).findViewById(R.id.btnChangeMinOrderCharge);

        adminReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        adminPin=dataSnapshot.child("pin").getValue().toString();

                        String deliveryCharge="Delivery Charge : "+getResources().getString(R.string.rs)+dataSnapshot.child("price").getValue().toString();
                        tvDeliveryCharge.setText(deliveryCharge);

                        String minOrderCharge="Min Order Charge : "+getResources().getString(R.string.rs)+dataSnapshot.child("minOrderPrice").getValue().toString();
                        tvMinOrderCharge.setText(minOrderCharge);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(AdminHomePage.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        btnChangeDeliveryCharge.setOnClickListener(v -> {
            final Dialog deliveryPriceDialog = new Dialog(AdminHomePage.this);
            deliveryPriceDialog.setContentView(R.layout.admin_delivery_price_layout);
            deliveryPriceDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            deliveryPriceDialog.show();
            final EditText etDeliveryPrice = deliveryPriceDialog.findViewById(R.id.etDeliveryPrice);
            final Button btnSubmitDeliveryPrice = deliveryPriceDialog.findViewById(R.id.btnSubmitDeliveryPrice);
            btnSubmitDeliveryPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String price=etDeliveryPrice.getText().toString();
                    if(price.isEmpty())
                    {
                        Toast.makeText(AdminHomePage.this,"enter delivery price please",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference().child("admin").child("price").setValue(price);
                        Toast.makeText(AdminHomePage.this,"price set!!",Toast.LENGTH_LONG).show();
                        deliveryPriceDialog.dismiss();
                    }
                }
            });
        });

        btnChangeMinOrderCharge.setOnClickListener(v -> {
            final Dialog orderCostDialog=new Dialog(AdminHomePage.this);
            orderCostDialog.setContentView(R.layout.change_min_order_price_custom_layout);
            orderCostDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            final EditText etMinOrderPrice = orderCostDialog.findViewById(R.id.etMinOrderPrice);
            Button btnMinOrderPrice = orderCostDialog.findViewById(R.id.btnMinOrderPrice);
            btnMinOrderPrice.setOnClickListener(v1 -> {
                String price=etMinOrderPrice.getText().toString();
                if(price.isEmpty())
                {
                    Toast.makeText(AdminHomePage.this,"enter price please",Toast.LENGTH_LONG).show();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("admin").child("minOrderPrice").setValue(price);
                    Toast.makeText(AdminHomePage.this,"price set!!",Toast.LENGTH_LONG).show();
                    orderCostDialog.dismiss();
                }
            });
            orderCostDialog.show();
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        dialog.show();
        Query baseQuery=databaseReference;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        DatabasePagingOptions<AdminItem> options = new DatabasePagingOptions.Builder<AdminItem>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, AdminItem.class)
                .build();

        FirebaseRecyclerPagingAdapter<AdminItem,AdminPageViewHolder> adapter = new FirebaseRecyclerPagingAdapter<AdminItem, AdminPageViewHolder>(options)
        {

            @NonNull
            @Override
            public AdminPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_home_custom_layout, parent, false);
                return new AdminPageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdminPageViewHolder holder, int position, @NonNull final AdminItem model) {
                dialog.dismiss();
                holder.setName(model.getName());
                if(model.getUnit().equals("out of stock"))
                    holder.setPrice("Not available");
                else
                {
                    String price=getResources().getString(R.string.rs)+model.getPrice()+" / "+model.getUnit();
                    holder.setPrice(price);
                }

                Glide.with(AdminHomePage.this).load(model.getImageUrl()).into(holder.ivItem);

                holder.mView.setOnLongClickListener(v -> {
                    Intent intent=new Intent(AdminHomePage.this, AdminItemEdit.class);
                    intent.putExtra("name",model.getName());
                    intent.putExtra("price", model.getPrice());
                    intent.putExtra("imageUrl", model.getImageUrl());
                    startActivity(intent);
                    return true;
                });
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
//                        Log.i("Loading state","initial");
//                        break;
                    case LOADING_MORE:
//                        Log.i("Loading state","more");
//                        break;
                    case LOADED:
//                        Log.i("Loading state","loaded");
//                        break;
                    case ERROR:
//                        Toast.makeText(AdminHomePage.this,"OOPS... Something Went Wrong!..",Toast.LENGTH_LONG).show();
//                        break;
                }
            }
        };

        binding.adminRecyclerView.setAdapter(adapter);
    }

    public static class AdminPageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvName,tvPrice;
        ImageView ivItem;

        public AdminPageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            tvName = mView.findViewById(R.id.tvName);
            tvPrice = mView.findViewById(R.id.tvPrice);
            ivItem = mView.findViewById(R.id.ivItem);

        }
        public void setName(String name)
        {
            tvName.setText(name);
        }
        public void setPrice(String price)
        {
            tvPrice.setText(price);
        }
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
                            Toast.makeText(AdminHomePage.this,"please enter old and new pin...",Toast.LENGTH_LONG).show();
                        }
                        else if(!oldPin.equals(adminPin))
                        {
                            Toast.makeText(AdminHomePage.this,"incorrect old pin...",Toast.LENGTH_LONG).show();
                        }
                        else {
                            adminReference.child("pin").setValue(newPin);
                            Toast.makeText(AdminHomePage.this,"new pin set successfully...",Toast.LENGTH_LONG).show();
                            pinDialog.dismiss();
                        }
                    });

                    break;
                case R.id.addItem:
                    startActivity(new Intent(AdminHomePage.this, AdminAddItem.class));
                    break;
                case R.id.logout:
                    mAuth.signOut();
                    removeFCMToken();
                    startActivity(new Intent(AdminHomePage.this, MainActivity.class));
                    finish();
                    break;
                case R.id.adminOrders:
                    startActivity(new Intent(AdminHomePage.this, AdminOrdersActivity.class));
                    break;
                case android.R.id.home:
                    binding.drawer.openDrawer(GravityCompat.START);
            }

        return super.onOptionsItemSelected(item);
    }

    private void removeFCMToken(){
        adminReference.child("token").setValue("");
    }

}
