package com.rwtcompany.onlinevegitableshopapp.screen.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserHomePageBinding;
import com.rwtcompany.onlinevegitableshopapp.screen.login.MainActivity;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.screen.order.UserOrders;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItems;

import java.util.HashMap;

public class UserHomePage extends AppCompatActivity {
    ActivityUserHomePageBinding binding;

    static  boolean shownDeliveryDialog=false;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference cartReference;
    private DatabaseReference totalCostReference;
    Query query;

    ProgressDialog dialog;

    static HashMap<String,Integer> addedOnCart;
    static HashMap<String,Integer> nameCost;

    int [] quantities;
    int [] costs;

    static int minOrderPrice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.userHomeRecyclerView.setHasFixedSize(true);
        binding.userHomeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("getting data...");
        dialog.setCancelable(false);
        dialog.show();

        addedOnCart = new HashMap<>();
        nameCost = new HashMap<>();

        cartReference=FirebaseDatabase.getInstance().getReference().child("cart").child(mAuth.getUid()).child("items");
        totalCostReference = FirebaseDatabase.getInstance().getReference().child("cart").child(mAuth.getUid()).child("totalCost");
        cartReference.removeValue();
        totalCostReference.removeValue();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
        databaseReference.keepSynced(true);

        query = databaseReference.orderByChild("available").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quantities =new int[(int)dataSnapshot.getChildrenCount()];
                costs = new int[(int) dataSnapshot.getChildrenCount()];

                FirebaseDatabase.getInstance().getReference().child("admin").child("minOrderPrice").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog.dismiss();
                        minOrderPrice = Integer.parseInt(dataSnapshot.getValue().toString());
                        //Setting recyclerVew as now quantities,costs and minOrderPrice available
                        setUpRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(UserHomePage.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserHomePage.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(addedOnCart.isEmpty())
        {
            binding.btnUserHomeGoToCart.setVisibility(View.GONE);
            binding.tvTotalCostCart.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView(){
        dialog.show();
        FirebaseRecyclerOptions<AdminItems> options =
                new FirebaseRecyclerOptions.Builder<AdminItems>()
                        .setQuery(query, AdminItems.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<AdminItems,HomePageViewHolder>(options)
        {

            @NonNull
            @Override
            public HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_home_cutom_layout, parent, false);
                return new HomePageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final HomePageViewHolder holder, final int position, @NonNull final AdminItems model) {
                dialog.dismiss();
                holder.setPrice("Rs:" + model.getPrice() + "/" + model.getUnit());

                if (model.getUnit().contains("gram"))
                    holder.setUnit("gram");
                else if (model.getUnit().contains("kg")) {
                    holder.setUnit("kg");
                } else if (model.getUnit().contains("piece"))
                    holder.setUnit("piece");

                if (addedOnCart.containsKey(model.getName())) {
                    quantities[position] = addedOnCart.get(model.getName());
                    costs[position] = nameCost.get(model.getName());
                    holder.userHomeTopLayout.setVisibility(View.VISIBLE);

                } else {
                    quantities[position] = 0;
                    costs[position] = 0;
                    holder.userHomeTopLayout.setVisibility(View.GONE);
                }


                holder.setQuantity(String.valueOf(quantities[position]));
                holder.setItemCost(String.valueOf(costs[position]));
                binding.tvTotalCostCart.setText(String.valueOf(sumCost()));

                holder.setName(model.getName());
                Glide.with(UserHomePage.this).load(model.getImageUrl()).into(holder.ivUserHomeImage);

                holder.btnUserHomeRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GETTING INTEGER PART FROM RATE OF ITEM
                        String rateQuantity = model.getUnit();
                        if (rateQuantity.contains(" ")) {
                            rateQuantity = rateQuantity.substring(0, rateQuantity.indexOf(" "));
                        }

                        if (model.getUnit().equals("100 gram") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(100,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("250 gram") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(250,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("500 gram") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(500,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("1 kg") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(1,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("1 piece") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(1,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("6 piece") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(6,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("12 piece") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(12,position,holder,"remove",model,rateQuantity);
                        } else if (model.getUnit().equals("50 gram") && quantities[position] > 0) {
                            updateUIAfterAddOrRemove(50,position,holder,"remove",model,rateQuantity);
                        }

                        binding.tvTotalCostCart.setText(String.valueOf(sumCost()));

                        if (quantities[position] == 0) {
                            addedOnCart.remove(model.getName());
                            nameCost.remove(model.getName());
                            cartReference.child(model.getName()).removeValue();
                            holder.userHomeTopLayout.setVisibility(View.GONE);
                        } else {
                            addedOnCart.put(model.getName(), quantities[position]);
                            nameCost.put(model.getName(), costs[position]);
                            cartReference.child(model.getName()).child("name").setValue(model.getName());
                            cartReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            cartReference.child(model.getName()).child("price").setValue(model.getPrice());
                            cartReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            cartReference.child(model.getName()).child("quantity").setValue(String.valueOf(quantities[position]));
                            cartReference.child(model.getName()).child("cost").setValue(String.valueOf(costs[position]));

                            totalCostReference.setValue(String.valueOf(sumCost()));
                        }

                        if (addedOnCart.isEmpty()) {
                            binding.btnUserHomeGoToCart.setVisibility(View.GONE);
                            binding.tvTotalCostCart.setVisibility(View.GONE);
                        }

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

                        if (model.getUnit().equals("100 gram")) {
                            updateUIAfterAddOrRemove(100,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("250 gram")) {
                            updateUIAfterAddOrRemove(250,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("500 gram")) {
                            updateUIAfterAddOrRemove(500,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("1 kg")) {
                            updateUIAfterAddOrRemove(1,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("1 piece")) {
                            updateUIAfterAddOrRemove(1,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("6 piece")) {
                            updateUIAfterAddOrRemove(6,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("12 piece")) {
                            updateUIAfterAddOrRemove(12,position,holder,"add",model,rateQuantity);
                        } else if (model.getUnit().equals("50 gram")) {
                            updateUIAfterAddOrRemove(50,position,holder,"add",model,rateQuantity);
                        }

                        binding.tvTotalCostCart.setText(String.valueOf(sumCost()));

                        if (quantities[position] != 0) {
                            addedOnCart.put(model.getName(), quantities[position]);
                            nameCost.put(model.getName(), costs[position]);
                            cartReference.child(model.getName()).child("name").setValue(model.getName());
                            cartReference.child(model.getName()).child("imageUrl").setValue(model.getImageUrl());
                            cartReference.child(model.getName()).child("price").setValue(model.getPrice());
                            cartReference.child(model.getName()).child("unit").setValue(model.getUnit());
                            cartReference.child(model.getName()).child("quantity").setValue(String.valueOf(quantities[position]));
                            cartReference.child(model.getName()).child("cost").setValue(String.valueOf(costs[position]));

                            totalCostReference.setValue(String.valueOf(sumCost()));

                            holder.userHomeTopLayout.setVisibility(View.VISIBLE);
                            binding.btnUserHomeGoToCart.setVisibility(View.VISIBLE);
                            binding.tvTotalCostCart.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        };

        binding.userHomeRecyclerView.setAdapter(adapter);
    }

    private void updateUIAfterAddOrRemove(int quantity, int position, HomePageViewHolder holder, String todo,AdminItems model,String rateQuantity) {
        if(todo.equals("remove")){
            quantities[position] -= quantity;
        }
        else{
            quantities[position] += quantity;
        }
        costs[position] = quantities[position] * Integer.parseInt(model.getPrice()) / Integer.parseInt(rateQuantity);
        holder.setItemCost(String.valueOf(costs[position]));
        holder.setQuantity(String.valueOf(quantities[position]));
    }


    public static class HomePageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView ivUserHomeImage;
        TextView tvUserHomeName,tvUserHomePrice,tvUserHomeQuantity,tvUserHomeUnit,tvItemCost;
        Button btnUserHomeAdd,btnUserHomeRemove;

        LinearLayout userHomeTopLayout;

        public HomePageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            ivUserHomeImage = mView.findViewById(R.id.ivUserHomeImage);
            tvUserHomeName = mView.findViewById(R.id.tvUserHomeName);
            tvUserHomePrice = mView.findViewById(R.id.tvUserHomePrice);
            tvUserHomeQuantity = mView.findViewById(R.id.tvUserHomeQuantity);
            btnUserHomeAdd = mView.findViewById(R.id.btnUserHomeAdd);
            btnUserHomeRemove = mView.findViewById(R.id.btnUserHomeRemove);
            tvUserHomeUnit = mView.findViewById(R.id.tvUerHomeUnit);
            tvItemCost = mView.findViewById(R.id.tvItemCost);

            userHomeTopLayout = mView.findViewById(R.id.userHomeTopLayout);

        }
        public void setName(String name)
        {
            tvUserHomeName.setText(name);
        }
        public void setPrice(String price)
        {
            tvUserHomePrice.setText(price);
        }
        public void setQuantity(String quantity)
        {
            tvUserHomeQuantity.setText(quantity);
        }
        public void setUnit(String unit)
        {
            tvUserHomeUnit.setText(unit);
        }
        public void setItemCost(String cost)
        {
            tvItemCost.setText(cost);
        }
    }
    int sumCost()
    {
        int totalCost=0;
        for(int i=0;i<costs.length;i++)
            totalCost+=costs[i];
        return totalCost;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_homepage_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.userLogout) {
            mAuth.signOut();
            startActivity(new Intent(UserHomePage.this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.userOrdersBasket) {
            startActivity(new Intent(UserHomePage.this, UserOrders.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToCart(View view) {
            startActivity(new Intent(UserHomePage.this, Cart.class));
    }
}
