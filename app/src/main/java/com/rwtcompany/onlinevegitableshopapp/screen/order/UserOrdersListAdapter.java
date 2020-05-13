package com.rwtcompany.onlinevegitableshopapp.screen.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrdersList;

import java.util.ArrayList;

public class UserOrdersListAdapter extends RecyclerView.Adapter<UserOrdersListAdapter.MyViewHolder> {
    ArrayList<UserOrdersList> list;
    Context context;
    OnRowListener onRowListener;


    public interface OnRowListener
    {
        void positionClick(int i);
    }
    public UserOrdersListAdapter(Context context,ArrayList<UserOrdersList> list) {
        this.context=context;
        this.list = list;
        this.onRowListener=(OnRowListener)context;
    }

    @NonNull
    @Override
    public UserOrdersListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_order_list_custom_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrdersListAdapter.MyViewHolder holder, final int position) {
        String orderId=list.get(position).getOrderId();
        String data=orderId.substring(0,2)+"/"+orderId.substring(2,4)+"/"+orderId.substring(4,8)+"\n"
                +orderId.substring(8,10)+":"+orderId.substring(10,12)+":"+orderId.substring(12,14);
        holder.tvOrderId.setText(data);

        holder.tvOrderStatus.setText(list.get(position).getOrderStatus());
        holder.tvTotalPrice.setText("Rs:"+list.get(position).getTotalCost());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRowListener.positionClick(position);
            }
        });

    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView tvOrderId,tvTotalPrice,tvOrderStatus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvTotalPrice=view.findViewById(R.id.tvTotatPrice);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatusListUser);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
