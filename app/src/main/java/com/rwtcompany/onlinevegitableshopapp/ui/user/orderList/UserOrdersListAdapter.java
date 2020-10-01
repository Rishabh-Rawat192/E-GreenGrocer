package com.rwtcompany.onlinevegitableshopapp.ui.user.orderList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.model.UserOrder;

import java.util.List;

public class UserOrdersListAdapter extends RecyclerView.Adapter<UserOrdersListAdapter.MyViewHolder> {
    private List<UserOrder> list;
    private Context context;
    private OnRowListener onRowListener;

    public interface OnRowListener
    {
        void positionClick(int i);
    }
    public UserOrdersListAdapter(Context context,List<UserOrder> list) {
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
        holder.tvTotalPrice.setText(context.getResources().getString(R.string.rs)+list.get(position).getTotalCost());
        holder.view.setOnClickListener(v -> onRowListener.positionClick(position));

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
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
