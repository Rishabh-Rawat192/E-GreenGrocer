package com.rwtcompany.onlinevegitableshopapp.ui.user.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserHomePageBinding;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;
import com.rwtcompany.onlinevegitableshopapp.ui.MainActivity;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.ui.user.orderList.UserOrdersActivity;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.ui.user.cart.CartActivity;

public class UserHomePageActivity extends AppCompatActivity {
    static int HOME_TO_CART_INTENT_CODE = 1;

    private ActivityUserHomePageBinding binding;
    private UserHomePageViewModel viewModel;

    private Query query;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private FirebaseRecyclerAdapter<AdminItem, HomePageViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(UserHomePageViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        query = FirebaseDatabase.getInstance().getReference().child("items").orderByChild("available").equalTo(true);

        binding.userHomeRecyclerView.setHasFixedSize(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("getting data...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        dialog.show();
        FirebaseRecyclerOptions<AdminItem> options =
                new FirebaseRecyclerOptions.Builder<AdminItem>()
                        .setQuery(query, AdminItem.class)
                        .setLifecycleOwner(this)
                        .build();
        adapter = new FirebaseRecyclerAdapter<AdminItem, HomePageViewHolder>(options) {

            @NonNull
            @Override
            public HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_home_custom_layout, parent, false);
                return new HomePageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final HomePageViewHolder holder, final int position, @NonNull final AdminItem model) {
                dialog.dismiss();
                holder.setPrice(getResources().getString(R.string.rs) + model.getPrice() + "/" + model.getUnit());

                if(model.getName()!=null)
                    holder.setName(model.getName().substring(0,1).toUpperCase()+model.getName().substring(1));
                Glide.with(UserHomePageActivity.this).load(model.getImageUrl()).into(holder.ivUserHomeImage);
                
//              Update button
                updateItemButton(new CartItem(model.getName(), model.getImageUrl(), model.getPrice(), model.getUnit(), "", String.valueOf(model.getPrice())), holder);

                holder.btnAddOrRemoveItem.setOnClickListener(v -> {

                    switch (model.getUnit()) {
                        case "100 gram":
                            updateUIAfterAddOrRemove(100, position, holder, model);
                            break;
                        case "250 gram":
                            updateUIAfterAddOrRemove(250, position, holder, model);
                            break;
                        case "500 gram":
                            updateUIAfterAddOrRemove(500, position, holder, model);
                            break;
                        case "1 kg": case "1 piece":
                            updateUIAfterAddOrRemove(1, position, holder, model);
                            break;
                        case "6 piece":
                            updateUIAfterAddOrRemove(6, position, holder, model);
                            break;
                        case "12 piece":
                            updateUIAfterAddOrRemove(12, position, holder, model);
                            break;
                        case "50 gram":
                            updateUIAfterAddOrRemove(50, position, holder, model);
                            break;
                    }
                });
            }
        };

        binding.userHomeRecyclerView.setAdapter(adapter);
    }

    private void updateUIAfterAddOrRemove(int quantity, int position, HomePageViewHolder holder, AdminItem model) {
        CartItem item = new CartItem(model.getName(), model.getImageUrl(), model.getPrice(), model.getUnit(), String.valueOf(quantity), String.valueOf(model.getPrice()));
        if (!viewModel.items.contains(item))
            viewModel.addItem(item);
        else
            viewModel.removeItem(item);
        updateItemButton(item, holder);
        if (viewModel.items.isEmpty())
            binding.btnUserHomeGoToCart.setVisibility(View.GONE);
        else
            binding.btnUserHomeGoToCart.setVisibility(View.VISIBLE);
    }

    private void updateItemButton(CartItem item, HomePageViewHolder holder) {
        if (viewModel.items.contains(item)) {
            holder.btnAddOrRemoveItem.setText("REMOVE");
            holder.btnAddOrRemoveItem.setTextColor(Color.RED);
        } else {
            holder.btnAddOrRemoveItem.setText("ADD");
            holder.btnAddOrRemoveItem.setTextColor(Color.BLACK);
        }
    }

    public static class HomePageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView ivUserHomeImage;
        TextView tvUserHomeName, tvUserHomePrice;
        Button btnAddOrRemoveItem;

        public HomePageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            ivUserHomeImage = mView.findViewById(R.id.ivUserHomeImage);
            tvUserHomeName = mView.findViewById(R.id.tvUserHomeName);
            tvUserHomePrice = mView.findViewById(R.id.tvUserHomePrice);
            btnAddOrRemoveItem = mView.findViewById(R.id.btnAddOrRemoveItem);
        }

        public void setName(String name) {
            tvUserHomeName.setText(name);
        }

        public void setPrice(String price) {
            tvUserHomePrice.setText(price);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_homepage_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userLogout) {
            mAuth.signOut();
            startActivity(new Intent(UserHomePageActivity.this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.userOrdersBasket) {
            startActivity(new Intent(UserHomePageActivity.this, UserOrdersActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("totalCost", viewModel.totalCost);
        intent.putExtra("items", viewModel.items);
        startActivityForResult(intent, HOME_TO_CART_INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HOME_TO_CART_INTENT_CODE && resultCode == RESULT_OK) {
            viewModel.orderPlaced();
            binding.btnUserHomeGoToCart.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }
}
