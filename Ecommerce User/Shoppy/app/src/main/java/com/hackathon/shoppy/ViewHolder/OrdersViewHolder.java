package com.hackathon.shoppy.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.R;

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView pname,pdate ;
    public ImageView pimage;
    ItemClickListener itemClickListener;

    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        pname = itemView.findViewById(R.id.product_name);
        pdate = itemView.findViewById(R.id.order_date);
        pimage = itemView.findViewById(R.id.product_image);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
