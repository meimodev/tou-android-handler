/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

public class SundayServiceIncome_RecyclerAdapter extends
        RecyclerView.Adapter<SundayServiceIncome_RecyclerAdapter.MyViewHolder> {

    private static final String TAG = "SundayServiceIncome_RA";
    private Context ctx;
    private ArrayList<NavFragment_SundayServiceIncome.SundayServiceIncomeHelper> items;


    SundayServiceIncome_RecyclerAdapter(Context ctx,
                                        ArrayList<NavFragment_SundayServiceIncome.SundayServiceIncomeHelper> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public SundayServiceIncome_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.resource_list_item_sunday_income, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvAmount;
        CheckBox checkBox;
        CardView cvMain;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.textView_description);
            tvAmount = itemView.findViewById(R.id.textView_amount);
            checkBox = itemView.findViewById(R.id.checkbox);
            cvMain = itemView.findViewById(R.id.cardView_main);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SundayServiceIncome_RecyclerAdapter.MyViewHolder h, int position) {
        NavFragment_SundayServiceIncome.SundayServiceIncomeHelper model = items.get(position);

        h.tvDesc.setText(model.getDescription());
        h.tvAmount.setText(model.getAmount());

        ColorStateList defCard = h.cvMain.getCardBackgroundColor();
        ColorStateList defDesc = h.tvDesc.getTextColors();
        ColorStateList defAmount = h.tvAmount.getTextColors();

        h.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                h.cvMain.setCardBackgroundColor(ctx.getResources().getColor(R.color.disabled_background));
                h.tvDesc.setTextColor(ctx.getResources().getColor(R.color.secondaryText));
                h.tvAmount.setTextColor(ctx.getResources().getColor(R.color.secondaryText));
            } else {
                h.cvMain.setCardBackgroundColor(defCard);
                h.tvDesc.setTextColor(defDesc);
                h.tvAmount.setTextColor(defAmount);
            }
        });

        h.cvMain.setOnClickListener(v -> h.checkBox.toggle());
    }

}

