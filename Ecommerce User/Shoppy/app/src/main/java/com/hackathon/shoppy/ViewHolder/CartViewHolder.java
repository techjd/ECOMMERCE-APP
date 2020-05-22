package com.hackathon.shoppy.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView pname,pprice ;
    public ImageView pimage;
    public TextView quantity;
    ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        pname = itemView.findViewById(R.id.pname);
        pprice = itemView.findViewById(R.id.pprice);
        quantity = itemView.findViewById(R.id.qty);
        pimage = itemView.findViewById(R.id.pimage);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
