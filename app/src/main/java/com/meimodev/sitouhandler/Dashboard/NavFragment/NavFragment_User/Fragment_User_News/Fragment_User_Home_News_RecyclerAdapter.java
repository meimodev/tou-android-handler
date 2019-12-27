package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_User_Home_News_RecyclerAdapter extends RecyclerView.Adapter<Fragment_User_Home_News_RecyclerAdapter.MyViewHolder> {

    private ArrayList<Fragment_User_Home_News_RecyclerModel> items;
    private Context context;

    public Fragment_User_Home_News_RecyclerAdapter(ArrayList<Fragment_User_Home_News_RecyclerModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item_member_home, parent, false);
        ButterKnife.bind(this, v);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Fragment_User_Home_News_RecyclerModel model = items.get(position);

        if (!model.getImageUrl().isEmpty())
            Picasso.with(context).load(model.getImageUrl()).fit().into(ivImage);

        if (!model.getTitle().isEmpty()) {
            tvTitle.setText(model.getTitle());
        }

        if (!model.getTime().isEmpty()) {
            tvTime.setText(model.getTime());
        }

        if (!model.getDescription().isEmpty()) {
            tvDescription.setText(model.getDescription());
        }

        View.OnClickListener listener = view -> {
            if (model.getToActivityIntent() != null) {
                context.startActivity(model.getToActivityIntent());
            } else {
                Toast.makeText(context, "NO INTENT from model", Toast.LENGTH_SHORT).show();
            }
        };
        cvPlaceHolder.setOnClickListener(listener);

    }

    @BindView(R.id.recyclerItem_description)
    TextView tvDescription;
    @BindView(R.id.recyclerItem_title)
    TextView tvTitle;
    @BindView(R.id.recyclerItem_time)
    TextView tvTime;
    @BindView(R.id.recyclerItem_image)
    ImageView ivImage;
    @BindView(R.id.recyclerItem_placeHolder)
    CardView cvPlaceHolder;

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
