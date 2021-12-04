package com.rwtcompany.onlinevegitableshopapp.ui.admin.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItemWithKey;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.editItem.AdminItemEditActivity;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.AdminPageViewHolder> {
    private List<AdminItemWithKey> items;

    public MyAdapter(List<AdminItemWithKey> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdminPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_home_custom_layout, parent, false);
        return new AdminPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPageViewHolder holder, int position) {
        if (items.get(position).getAdminItem().getName() != null)
            holder.setName(items.get(position).getAdminItem().getName().substring(0, 1).toUpperCase() + items.get(position).getAdminItem().getName().substring(1));
        if (items.get(position).getAdminItem().getUnit().equals("out of stock"))
            holder.setPrice("Not available");
        else {
            String price = holder.ivItem.getResources().getString(R.string.rs) + items.get(position).getAdminItem().getPrice() + " / " + items.get(position).getAdminItem().getUnit();
            holder.setPrice(price);
        }

        holder.setIvItem(items.get(position).getAdminItem().getImageUrl());


        holder.mView.setOnLongClickListener(v -> {
            Intent intent = new Intent(holder.mView.getContext(), AdminItemEditActivity.class);
            intent.putExtra("name", items.get(position).getAdminItem().getName());
            intent.putExtra("price", items.get(position).getAdminItem().getPrice());
            intent.putExtra("imageUrl", items.get(position).getAdminItem().getImageUrl());
            intent.putExtra("unit", items.get(position).getAdminItem().getUnit());
            intent.putExtra("key", items.get(position).getKey());
            holder.mView.getContext().startActivity(intent);
            return true;
        });
    }


    public static class AdminPageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvName, tvPrice;
        ImageView ivItem;

        public AdminPageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvName = mView.findViewById(R.id.tvName);
            tvPrice = mView.findViewById(R.id.tvPrice);
            ivItem = mView.findViewById(R.id.ivItem);
        }

        public void setName(String name) {
            tvName.setText(name);
        }

        public void setPrice(String price) {
            tvPrice.setText(price);
        }

        public void setIvItem(String url) {
            Glide.with(mView.getContext()).load(url).into(ivItem);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}
