package com.hackathon.shoppy.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    ArrayList<Product> list;
    public SearchAdapter(ArrayList<Product> list){
        this.list=list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_suggestion,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.with(holder.itemView.getContext())
                .load(list.get(position).getImage1())
                .into(holder.product_image);
        holder.product_name.setText(list.get(position).getName());
        holder.product_price.setText(list.get(position).getPrice());
        holder.product_id.setText(list.get(position).getPid());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView product_image;
        TextView product_name,product_price,product_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.pimage);
            product_name = itemView.findViewById(R.id.pname);
            product_price = itemView.findViewById(R.id.pprice);
            product_id =itemView.findViewById(R.id.pid);
        }
    }
}
