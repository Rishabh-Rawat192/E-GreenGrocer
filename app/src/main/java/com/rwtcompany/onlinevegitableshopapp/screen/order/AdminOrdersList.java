package com.rwtcompany.onlinevegitableshopapp.screen.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminOrdersListBinding;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrdersList;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminOrdersList extends AppCompatActivity implements UserOrdersListAdapter.OnRowListener {
    ActivityAdminOrdersListBinding binding;

    RecyclerView.Adapter adapter;
    ArrayList<UserOrdersList> list;
    HashMap<String,String> orderIdAndUuid;

    ProgressDialog dialog;

    private DatabaseReference orderUsersIdReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Orders Received");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("getting data...");
        dialog.setCancelable(false);

        list = new ArrayList<>();
        orderIdAndUuid=new HashMap<>();
        adapter = new UserOrdersListAdapter(this, list);
        binding.adminOrderRecyclerView.setHasFixedSize(true);
        binding.adminOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.adminOrderRecyclerView.setAdapter(adapter);

        orderUsersIdReference= FirebaseDatabase.getInstance().getReference().child("orders");


    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        orderUsersIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                list.clear();
                for(DataSnapshot users:dataSnapshot.getChildren())
                {
                    String uuid=users.getKey();
                    for(DataSnapshot userOrders:users.getChildren())
                    {
                        if (userOrders.hasChild("orderStatus")&&userOrders.hasChild("deliveryCharge")
                                &&userOrders.hasChild("total")) {
                            String id=userOrders.getKey();
                            String orderStatus=userOrders.child("orderStatus").getValue().toString();
                            int deliveryCharge = Integer.parseInt(userOrders.child("deliveryCharge").getValue().toString());

                            String total = String.valueOf(Integer.parseInt(userOrders.child("total").getValue().toString()) + deliveryCharge);
                            list.add(new UserOrdersList(id, total, orderStatus));
                            orderIdAndUuid.put(id,uuid);
                        }
                    }
                }
                if(list.isEmpty())
                    Toast.makeText(AdminOrdersList.this,"NO ORDERS RECEIVED!!!",Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(AdminOrdersList.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void positionClick(int i) {
        String orderId=list.get(i).getOrderId();
        String uuid = orderIdAndUuid.get(orderId);
        Intent intent=new Intent(AdminOrdersList.this,AdminOrdersDescription.class);
        intent.putExtra("uuid",uuid);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
