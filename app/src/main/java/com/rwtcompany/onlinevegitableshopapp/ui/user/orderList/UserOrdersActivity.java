package com.rwtcompany.onlinevegitableshopapp.ui.user.orderList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserOrdersBinding;
import com.rwtcompany.onlinevegitableshopapp.ui.user.orderDetails.UserOrderDescriptionActivity;

public class UserOrdersActivity extends AppCompatActivity implements UserOrdersListAdapter.OnRowListener {
    private ActivityUserOrdersBinding binding;
    private UserOrdersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Your Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(UserOrdersViewModel.class);
        binding.userOrdersRecyclerView.setHasFixedSize(true);

        viewModel.list.observe(this,userOrdersLists -> {
            UserOrdersListAdapter adapter = new UserOrdersListAdapter(this, userOrdersLists);
            binding.userOrdersRecyclerView.setAdapter(adapter);
            if (userOrdersLists.isEmpty())
                Toast.makeText(this,"No Orders so far...",Toast.LENGTH_LONG).show();
        });
    }


    @Override
    public void positionClick(int i) {
        Intent intent = new Intent(UserOrdersActivity.this, UserOrderDescriptionActivity.class);
        intent.putExtra("orderId", viewModel.list.getValue().get(i).getOrderId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
