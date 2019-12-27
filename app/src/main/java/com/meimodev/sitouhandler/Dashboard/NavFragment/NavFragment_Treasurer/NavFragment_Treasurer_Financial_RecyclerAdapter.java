package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.WebViewActivity;

import java.util.ArrayList;

public class NavFragment_Treasurer_Financial_RecyclerAdapter extends RecyclerView.Adapter<NavFragment_Treasurer_Financial_RecyclerAdapter.MyViewHolder> {

    private ArrayList<NavFragment_Treasurer_Financial_RecyclerModel> items;
    private Context context;

    public NavFragment_Treasurer_Financial_RecyclerAdapter(ArrayList<NavFragment_Treasurer_Financial_RecyclerModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item_financial_report, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavFragment_Treasurer_Financial_RecyclerModel model = items.get(position);

        if (model.isBKU()) {
            holder.tvType.setText("BKU");
            holder.tvDate.setText(model.getMonth());
            holder.tvYear.setText(model.getYear());
        } else if (model.isEval()) {
            holder.tvType.setText("EVALUASI");
            holder.tvDate.setText(String.format("%s\n%s", model.getSemester(), model.getMonth()));
        }

        holder.cvHolder.setOnClickListener(view -> {
            context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
//            Toast.makeText(context, "Start WEB VIEW Activity", Toast.LENGTH_SHORT).show();

        context.startActivity(new Intent(context, WebViewActivity.class));
        });
        if (position == items.size() - 1) {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 250);
            holder.cvHolder.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvHolder;
        TextView tvDate, tvYear, tvType;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cvHolder = itemView.findViewById(R.id.recyclerItem_itemHolder);
            tvDate = itemView.findViewById(R.id.recyclerItem_date);
            tvYear = itemView.findViewById(R.id.recyclerItem_year);
            tvType = itemView.findViewById(R.id.recyclerItem_textType);
        }
    }
}
