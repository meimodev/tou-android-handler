/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Html;
import android.util.Log;
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

    private static final String TAG = "Fragment_User_Home_News";
    private ArrayList<Fragment_User_Home_News_RecyclerModel> items;
    private Context context;

    Fragment_User_Home_News_RecyclerAdapter(ArrayList<Fragment_User_Home_News_RecyclerModel> items, Context context) {
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

        if (model.getImageUrl().isEmpty()) {
//            Glide.with(context).asBitmap().load(R.drawable.ic_logo_tou).into(ivImage);
           ivImage.setImageResource(R.drawable.ic_logo_tou_system);

        }
        else {
//            Glide.with(context).asBitmap().load(model.getImageUrl()).into(ivImage);
            Picasso.get().load(model.getImageUrl()).into(ivImage);
        }

        if (!model.getTime().isEmpty()) {
            tvTime.setText(model.getTime());
        } else {
            tvTime.setVisibility(View.INVISIBLE);
        }

        if (!model.getTitle().isEmpty()) {
            tvTitle.setText(Html.fromHtml(model.getTitle()));
        }

        View.OnClickListener listener = view -> {
            Intent intent = new Intent(context, ActivityNewsDetail.class);
            intent.putExtra("TITLE", model.getTitle());
            intent.putExtra("DESC", model.getDescription());
            if (!model.getImageUrl().isEmpty()) {
                intent.putExtra("IMG", model.getImageUrl());
            }
            context.startActivity(intent);
        };
        cvPlaceHolder.setOnClickListener(listener);

    }

    @BindView(R.id.recyclerItem_time)
    TextView tvTime;
    @BindView(R.id.recyclerItem_image)
    ImageView ivImage;
    @BindView(R.id.recyclerItem_title)
    TextView tvTitle;
    @BindView(R.id.recyclerItem_placeHolder)
    CardView cvPlaceHolder;

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
