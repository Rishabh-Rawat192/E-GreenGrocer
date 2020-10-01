package com.rwtcompany.onlinevegitableshopapp.ui.user.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.model.CartItem;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.HomePageViewHolder> {
    ArrayList<CartItem> items;
    Context context;
    OnClickListener listener;
    interface OnClickListener{
        void increaseItemQuantity(int index);
        void decrementItemQuantity(int index);
    }
    CustomAdapter(ArrayList<CartItem> items, Context context){
        this.items=items;
        this.context=context;
        listener=(OnClickListener)context;
    }
    @NonNull
    @Override
    public CustomAdapter.HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cart_custom_layout, parent, false);
        return new CustomAdapter.HomePageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.HomePageViewHolder holder, int position) {
        holder.setPrice(context.getResources().getString(R.string.rs) + items.get(position).getPrice() + "/" + items.get(position).getUnit());

        if (items.get(position).getUnit().contains("gram"))
            holder.setUnit("gram");
        else if (items.get(position).getUnit().contains("kg")) {
            holder.setUnit("kg");
        } else if (items.get(position).getUnit().contains("piece"))
            holder.setUnit("piece");

        holder.setItemCost(items.get(position).getCost());
        holder.setQuantity(items.get(position).getQuantity());
        holder.setName(items.get(position).getName());
        Glide.with(context).load(items.get(position).getImageUrl()).into(holder.ivUserHomeImage);

        holder.btnAdd.setOnClickListener(v ->listener.increaseItemQuantity(position));
        holder.btnRemove.setOnClickListener(v-> listener.decrementItemQuantity(position));
    }
    public static class HomePageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView ivUserHomeImage;
        TextView tvUserHomeName,tvUserHomePrice,tvUserHomeQuantity,tvUserHomeUnit,tvItemCost;
        ImageButton btnAdd, btnRemove;


        public HomePageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            ivUserHomeImage = mView.findViewById(R.id.ivUserHomeImage);
            tvUserHomeName = mView.findViewById(R.id.tvUserHomeName);
            tvUserHomePrice = mView.findViewById(R.id.tvUserHomePrice);
            tvUserHomeQuantity = mView.findViewById(R.id.tvUserHomeQuantity);
            btnAdd = mView.findViewById(R.id.btn_add);
            btnRemove = mView.findViewById(R.id.btn_remove);
            tvUserHomeUnit = mView.findViewById(R.id.tvUerHomeUnit);
            tvItemCost = mView.findViewById(R.id.tvItemCost);

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


    @Override
    public int getItemCount() {
        return items.size();
    }


}
