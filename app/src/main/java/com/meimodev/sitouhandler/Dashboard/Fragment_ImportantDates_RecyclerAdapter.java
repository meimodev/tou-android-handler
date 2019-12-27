package com.meimodev.sitouhandler.Dashboard;

import android.content.Context;
import android.content.Intent;
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

public class Fragment_ImportantDates_RecyclerAdapter extends RecyclerView.Adapter<Fragment_ImportantDates_RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Fragment_ImportantDates_RecyclerModel> items;

    private OnRecyclerItemOperationListener.AcceptItemListener clickListener;

    Fragment_ImportantDates_RecyclerAdapter(Context context, ArrayList<Fragment_ImportantDates_RecyclerModel> items,
                                            OnRecyclerItemOperationListener.AcceptItemListener clickListener) {
        this.context = context;
        this.items = items;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item_important_dates, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Fragment_ImportantDates_RecyclerModel model = items.get(position);

        holder.cardViewHolder.setOnClickListener(view -> {
            context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
            Bundle bundle = new Bundle();

            bundle.putString("priest_category",model.getPriestModel().getCategory());
            bundle.putInt("priest_ID",model.getPriestModel().getBasicMemberModels().getID());
            bundle.putString("priest_name",model.getPriestModel().getBasicMemberModels().getFullName());
            bundle.putString("priest_DOB",model.getPriestModel().getBasicMemberModels().getDOB());
            bundle.putString("priest_column",model.getPriestModel().getBasicMemberModels().getColumn());
            bundle.putString("priest_baptize",model.getPriestModel().getBasicMemberModels().getBaptizeLetterEntry());
            bundle.putString("priest_sidi",model.getPriestModel().getBasicMemberModels().getSidiLetterEntry());
            bundle.putString("priest_married",model.getPriestModel().getBasicMemberModels().getMarriageLetterEntry());

            bundle.putString("issuedBy_name",model.getIssuedByModel().getFullName());
            bundle.putStringArrayList("issuedBy_position",model.getIssuedByModel().getAssignedPositions());
            bundle.putString("issuedBy_column",model.getIssuedByModel().getColumn());

            bundle.putString("issue", model.getIssue());
            bundle.putString("place",model.getPlace());
            bundle.putString("date",model.getDate() + " "+model.getMonth() + " 2019");
            bundle.putString("time",model.getTime());
            bundle.putString("place_column",model.getPlace_column());

            clickListener.acceptItem(bundle);
        });
        String string = model.getDate() + "\n" + model.getMonth();
        holder.tvDate.setText(string);
        holder.tvIssue.setText(model.getIssue());
        holder.tvTime.setText(model.getTime());
        holder.tvIssuedBy.setText(model.getIssuedByModel().getFullName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvIssue, tvTime, tvIssuedBy;
        CardView cardViewHolder;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.recyclerItem_date);
            tvIssue = itemView.findViewById(R.id.recyclerItem_issueKey);
            tvTime = itemView.findViewById(R.id.recyclerItem_time);
            tvIssuedBy = itemView.findViewById(R.id.recyclerItem_issuedBy);

            cardViewHolder = itemView.findViewById(R.id.recyclerItem_itemHolder);
        }

    }
}
