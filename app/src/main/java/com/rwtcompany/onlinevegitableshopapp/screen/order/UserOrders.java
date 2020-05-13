package com.rwtcompany.onlinevegitableshopapp.screen.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserOrdersBinding;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrdersList;

import java.util.ArrayList;

public class UserOrders extends AppCompatActivity implements UserOrdersListAdapter.OnRowListener {
    ActivityUserOrdersBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference myOrderReference;

    ArrayList<UserOrdersList> list;
    RecyclerView.Adapter adapter;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Your Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("getting data...");
        dialog.setCancelable(false);
        dialog.show();

        binding.userOrdersRecyclerView.setHasFixedSize(true);
        binding.userOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new UserOrdersListAdapter(this, list);
        binding.userOrdersRecyclerView.setAdapter(adapter);

        mAuth=FirebaseAuth.getInstance();
        myOrderReference= FirebaseDatabase.getInstance().getReference().child("orders").child(mAuth.getUid());


    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        myOrderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                dialog.dismiss();
                list.clear();
                if(dataSnapshot.getChildrenCount()==0)
                {
                    Toast.makeText(UserOrders.this,"No items added to cart...",Toast.LENGTH_LONG).show();
                }
                else
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        String totalCost="";
                        String orderStatus="";
                        if(snapshot.hasChild("total")&&snapshot.hasChild("orderStatus")
                                &&snapshot.hasChild("deliveryCharge"))
                        {
                            int total;
                            int deliveryCharge = Integer.parseInt(snapshot.child("deliveryCharge").getValue().toString());
                            totalCost = snapshot.child("total").getValue().toString();
                            total = deliveryCharge + Integer.parseInt(totalCost);
                            totalCost = String.valueOf(total);

                            orderStatus = snapshot.child("orderStatus").getValue().toString();
                            list.add(new UserOrdersList(id, totalCost, orderStatus));
                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(UserOrders.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void positionClick(int i) {
        Intent intent = new Intent(UserOrders.this, UserOrderDescriptionPage.class);
        intent.putExtra("orderId", list.get(i).getOrderId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
