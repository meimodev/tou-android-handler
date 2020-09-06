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

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter grayscale = new ColorMatrixColorFilter(matrix);

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

            Picasso.get().load(model.getImageUrl()).fit().into(pHolder.imageView);
            if (!model.isAvailable()) {

                pHolder.imageView.setColorFilter(grayscale);
                pHolder.tvNote.setText(model.getIsAvailableMessage());
                pHolder.cvAdd.setClickable(false);
                pHolder.cvAdd.setCardBackgroundColor(context.getResources().getColor(R.color.disabled_background));
                return;
            }
            pHolder.cvAdd.setOnClickListener(v -> {
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
            Picasso.get().load(model.getImageUrl()).fit().transform(new CropCircleTransformation()).into(pHolder.imageView);
            pHolder.tvName.setVisibility(View.INVISIBLE);
            pHolder.tvPrice.setText(model.getName());
            pHolder.tvPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pHolder.cvAdd.setVisibility(View.GONE);
            pHolder.cvMain.setClickable(true);


            if (!model.isVendorOpen()) {
                pHolder.imageView.setColorFilter(grayscale);
                pHolder.cvMain.setClickable(false);
                pHolder.tvPrice.setText(String.format("%s (Tutup)", model.getName()));
            }

            pHolder.tvNote.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pHolder.tvNote.setVisibility(View.VISIBLE);
            pHolder.tvNote.setText(String.format("%s - %s", model.getOpenHour(), model.getCloseHour()));

            pHolder.cvMain.setOnClickListener(v -> {
                Intent i = new Intent(ActivityServiceGroceries.KEY_VENDOR_PRODUCTS);
                i.putExtra("VENDOR_NAME", model.getName());
                context.sendBroadcast(i);
            });
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolderProduct extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvNote;
        ImageView imageView;
        CardView cvAdd, cvMain;

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
