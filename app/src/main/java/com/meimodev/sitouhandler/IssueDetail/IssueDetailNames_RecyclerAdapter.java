package com.meimodev.sitouhandler.IssueDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Models.BasicMember_Model;
import com.meimodev.sitouhandler.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class IssueDetailNames_RecyclerAdapter extends RecyclerView.Adapter<IssueDetailNames_RecyclerAdapter.MyViewHolder> {

    private ArrayList<IssueDetailNames_RecyclerModel> items;
    private Context context;

    public IssueDetailNames_RecyclerAdapter(ArrayList<IssueDetailNames_RecyclerModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item_adding, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BasicMember_Model model = items.get(position).getBasicMemberModels();

        if (items.get(position).isUnregistered()) {
            holder.llUnregistered.setVisibility(View.VISIBLE);
            holder.llUnregistered.setVisibility(View.VISIBLE);
            holder.tvUnregisteredName.setText(model.getFullName());
            holder.btnUnregistered.setVisibility(View.GONE);
        } else {
            if (model.getImageURL() != null)
                Picasso.get().load(model.getImageURL()).resize(50, 50).centerCrop().into(holder.ivImage);

            if (model.getFullName() != null)
                holder.tvName.setText(model.getFullName());

            if (model.getDOB() != null)
                holder.tvBOD.setText(model.getDOB());

            if (model.getColumn() != null)
                holder.tvColoumn.setText(model.getColumn());

            if (!model.getBaptizeLetterEntry().isEmpty()) {
                holder.cvBaptize.setVisibility(View.VISIBLE);
                holder.cvBaptize.setEnabled(true);
            }
            if (!model.getSidiLetterEntry().isEmpty()) {
                holder.cvSidi.setVisibility(View.VISIBLE);
                holder.cvSidi.setEnabled(true);
            }
            if (!model.getMarriageLetterEntry().isEmpty()) {
                holder.cvMarriage.setVisibility(View.VISIBLE);
                holder.cvMarriage.setEnabled(true);
            }
            if (items.get(position).isCategoryHead()) {
                holder.tvCategory.setVisibility(View.VISIBLE);
                holder.tvCategory.setText(items.get(position).getCategory());
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBOD, tvColoumn, tvCategory, tvUnregisteredName;
        ImageView ivImage;
        LinearLayout llUnregistered;
        Button btnUnregistered;
        CardView cvBaptize, cvSidi, cvMarriage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_name);
            tvBOD = itemView.findViewById(R.id.textView_birthDate);
            tvColoumn = itemView.findViewById(R.id.textView_kolom);
            tvCategory = itemView.findViewById(R.id.textView_category);
            tvUnregisteredName = itemView.findViewById(R.id.textView_unregisteredName);
            ivImage = itemView.findViewById(R.id.imageView_pic);
            llUnregistered = itemView.findViewById(R.id.linearLayout_unregistered);
            btnUnregistered = itemView.findViewById(R.id.button_unregisteredName);

            cvBaptize = itemView.findViewById(R.id.cardView_baptis);
            cvSidi = itemView.findViewById(R.id.cardView_sidi);
            cvMarriage = itemView.findViewById(R.id.cardView_nikah);
        }
    }
}
