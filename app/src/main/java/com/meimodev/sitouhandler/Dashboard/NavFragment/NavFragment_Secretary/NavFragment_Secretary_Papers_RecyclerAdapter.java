package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

public class NavFragment_Secretary_Papers_RecyclerAdapter extends RecyclerView.Adapter<NavFragment_Secretary_Papers_RecyclerAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<NavFragment_Secretary_Papers_RecyclerModel> items;

    private OnRecyclerItemOperationListener.ItemClickListener itemClickListener;

    NavFragment_Secretary_Papers_RecyclerAdapter(Context context, ArrayList<NavFragment_Secretary_Papers_RecyclerModel> items, OnRecyclerItemOperationListener.ItemClickListener itemClickListener) {
        this.context = context;
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item_secretary_letters, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavFragment_Secretary_Papers_RecyclerModel model = items.get(position);

        String typeHolder = model.getLetterType().contentEquals(Constant.KEY_PAPERS_VALIDATE_MEMBERS) ? "Ket. ANGGOTA" : model.getLetterType();
        holder.tvType.setText(typeHolder);
        holder.tvEntry.setText(model.getLetterEntry());

        holder.cvHolder.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("ISSUE_ID", model.getId());
            itemClickListener.itemClick(bundle);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvEntry;
        CardView cvHolder;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.recyclerItem_textType);
            tvEntry = itemView.findViewById(R.id.recyclerItem_entryNumber);
            cvHolder = itemView.findViewById(R.id.recyclerItem_itemHolder);
        }
    }
}
