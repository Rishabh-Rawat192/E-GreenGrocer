package com.rwtcompany.onlinevegitableshopapp.ui.admin.orderList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminOrdersBinding;
import com.rwtcompany.onlinevegitableshopapp.model.AdminOrder;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrder;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.orderDetails.AdminOrderDescriptionActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.user.orderList.UserOrdersListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity implements UserOrdersListAdapter.OnRowListener {
    private ActivityAdminOrdersBinding binding;
    private AdminOrdersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Orders Received");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.adminOrderRecyclerView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this).get(AdminOrdersViewModel.class);
        viewModel.orders.observe(this,adminOrdersLists -> {
            List<UserOrder> list = new ArrayList<>();
            if(adminOrdersLists==null)
                Toast.makeText(AdminOrdersActivity.this,"NO orders found!!",Toast.LENGTH_LONG).show();
            else{
                for(AdminOrder item: adminOrdersLists)
                    list.add(new UserOrder(item.getOrderId(), item.getTotalCost(), item.getOrderStatus()));
            }
            UserOrdersListAdapter adapter=new UserOrdersListAdapter(this,list);
            binding.adminOrderRecyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void positionClick(int i) {
        String orderId=viewModel.orders.getValue().get(i).getOrderId();
        String uuid = viewModel.orders.getValue().get(i).getUuid();
        Intent intent=new Intent(AdminOrdersActivity.this, AdminOrderDescriptionActivity.class);
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
