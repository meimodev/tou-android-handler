/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.ContentValues.TAG;

public class ActivityServiceGroceries_Product_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<ActivityServiceGroceries.HelperModelProduct> products;

    private OnRecyclerItemOperationListener.ItemClickListener clickListener;

    ActivityServiceGroceries_Product_RecyclerAdapter(
            Context context,
            ArrayList<ActivityServiceGroceries.HelperModelProduct> products,
            OnRecyclerItemOperationListener.ItemClickListener clickListener
    ) {
        this.clickListener = clickListener;
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderProduct(LayoutInflater.from(context).inflate(
                R.layout.resource_list_item_groceries_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);


        ActivityServiceGroceries.HelperModelProduct model = products.get(position);

        ViewHolderProduct pHolder = (ViewHolderProduct) holder;
        pHolder.setIsRecyclable(false);

        pHolder.tvNote.setVisibility(View.INVISIBLE);

        if (!model.isVendor()) {

            pHolder.tvName.setText(model.getName());
            String price = Constant.convertNumberToCurrency(model.getPrice()) + " / " + model.getUnit();
            pHolder.tvPrice.setText(price);
            pHolder.tvNote.setVisibility(View.VISIBLE);
            pHolder.tvNote.setText(model.getVendorName());

            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.ic_logo_tou_system).into(pHolder.imageView);

            if (!model.isAvailable()) {
                pHolder.cvMain.setClickable(false);
                Constant.applyGrayscaleToImage( pHolder.imageView);
                pHolder.tvNote.setText(model.getIsAvailableMessage());
                pHolder.tvNote.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                pHolder.cvAdd.setClickable(false);
                pHolder.cvAdd.setCardBackgroundColor(context.getResources().getColor(R.color.disabled_background));
                pHolder.cvMain.setOnClickListener(v -> Toast.makeText(context, "Produk ini sedang tidak tersedia -_-", Toast.LENGTH_LONG).show());
                return;
            }

            pHolder.cvMain.setOnClickListener(v -> {
                Bundle data = new Bundle();
                data.putInt("ID", model.getId());
                data.putString("NAME", model.getName());
                data.putInt("PRICE", model.getPrice());
                data.putString("UNIT", model.getUnit());
                data.putInt("POS", position);
                data.putInt("VENDOR_ID", model.getVendorId());
                data.putString("VENDOR_NAME", model.getVendorName());
                clickListener.itemClick(data);
            });
        }
        else {

            int imagePadding = 45;
            pHolder.imageView.setPaddingRelative(imagePadding, imagePadding ,imagePadding ,imagePadding);

            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.ic_logo_tou_system).transform(new CropCircleTransformation()).into(pHolder.imageView);
            pHolder.tvName.setVisibility(View.GONE);
            pHolder.tvPrice.setText(model.getName());
            pHolder.tvPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pHolder.cvAdd.setVisibility(View.GONE);
            pHolder.cvMain.setClickable(true);


            if (!model.isVendorOpen()) {
                Constant.applyGrayscaleToImage(pHolder.imageView);
                pHolder.cvMain.setClickable(false);
                pHolder.tvPrice.setText(String.format("%s (Tutup)", model.getName()));
            }

            pHolder.tvNote.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pHolder.tvNote.setVisibility(View.VISIBLE);
            pHolder.tvNote.setText(String.format("%s - %s", model.getOpenHour(), model.getCloseHour()));

            pHolder.cvMain.setOnClickListener(v -> {
                Intent i = new Intent(ActivityServiceGroceries.KEY_VENDOR_PRODUCTS);
                i.putExtra("VENDOR_NAME", model.getName());
                i.putExtra("VENDOR_ID", model.getVendorId());
                context.sendBroadcast(i);
            });
        }

        if (products.size() % 2 == 0) {
            if (position == products.size() - 1 || position == products.size() - 2) {
                applyBottomMargin(pHolder);
            }
        }
        else {
            if (position == products.size() - 1) {
                applyBottomMargin(pHolder);
            }
        }


    }

    private void applyBottomMargin(ViewHolderProduct pHolder) {
        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );

        ViewGroup.MarginLayoutParams currParam = ((ViewGroup.MarginLayoutParams) pHolder.cvMain.getLayoutParams());

        params.setMargins(currParam.leftMargin, currParam.topMargin, currParam.rightMargin, 275);
        pHolder.cvMain.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolderProduct extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvNote;
        ImageView imageView;
        CardView cvAdd;
        MaterialCardView cvMain;

        ViewHolderProduct(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.recyclerItem_name);
            tvPrice = itemView.findViewById(R.id.recyclerItem_price);
            tvNote = itemView.findViewById(R.id.recyclerItem_note);
            cvAdd = itemView.findViewById(R.id.recyclerItem_layout_add);
            cvMain = itemView.findViewById(R.id.layout_main);

            imageView = itemView.findViewById(R.id.imageView);

        }

    }


}
