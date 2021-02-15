package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.shuhart.stickyheader.StickyAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SectionVendorProductsAdapter extends StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder> {

    private ArrayList<SectionVendorProducts> sectionArrayList;
    private OnRecyclerItemOperationListener.ItemClickListener itemClickListener;

    Context context;


    private static final int LAYOUT_HEADER = 0;
    private static final int LAYOUT_CHILD = 1;

    public SectionVendorProductsAdapter(
            ArrayList<SectionVendorProducts> sectionArrayList,
            Context context,
            OnRecyclerItemOperationListener.ItemClickListener itemClickListener) {
        this.sectionArrayList = sectionArrayList;
        this.sectionArrayList.add(new VendorDetail_Product_Child_Model(
                0, "name", "image",
                "0", "unit", false, ""
        ));
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == LAYOUT_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.recycler_item_sticky_header, parent, false));
        } else {
            return new ItemViewHolder(inflater.inflate(R.layout.recycler_item_sticky_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        SectionVendorProducts model = sectionArrayList.get(position);

        if (sectionArrayList.get(position).isHeader()) {
            ((HeaderViewHolder) holder).textView.setText(model.getName());
        } else {
            ItemViewHolder h = (ItemViewHolder) holder;

            h.tvName.setText(model.getName());
            String priceDisplay = Constant.convertNumberToCurrency(model.getPrice()) /*+ " / " + model.getUnit()*/;
            h.tvPrice.setText(priceDisplay);

            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.ic_sss_logo_icon).into(h.ivImage);


            if (!model.isAvailable()) {
                h.tvDesc.setText(model.isAvailableMessage());
                h.tvDesc.setTextColor(context.getResources().getColor(R.color.colorAccent4End));
                Constant.applyGrayscaleToImage(h.ivImage);
                Constant.applyGrayscaleToImage(h.ivAddIcon);

                h.layout.setBackgroundColor(context.getResources().getColor(R.color.backGround_fragment));
                h.layout.setClickable(false);

            } else {
                h.layout.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", model.getId());
                    bundle.putString("NAME", model.getName());
                    bundle.putInt("PRICE", Integer.parseInt(model.getPrice()));
                    bundle.putString("UNIT", model.getUnit());
                    bundle.putInt("POS", position);
                    bundle.putInt("VENDOR_ID", model.getVendorId());
                    bundle.putString("VENDOR_NAME", model.getVendorName());
                    itemClickListener.itemClick(bundle);
                });
            }

            if (position == (sectionArrayList.size() - 1)) {
                //apply bottom margin to the last item
                h.layout.setVisibility(View.INVISIBLE);
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (sectionArrayList.get(position).isHeader()) {
            return LAYOUT_HEADER;
        } else
            return LAYOUT_CHILD;
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {

        return sectionArrayList.get(itemPosition).sectionPosition();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int headerPosition) {
        holder.setIsRecyclable(false);
        ((HeaderViewHolder) holder).textView.setText(sectionArrayList.get(headerPosition).getName());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, LAYOUT_HEADER);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerItem_headerText);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDesc;
        ImageView ivImage, ivAddIcon;
        MaterialCardView cvImageHolder;
        ConstraintLayout layout;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.recyclerItem_name);
            tvPrice = itemView.findViewById(R.id.recyclerItem_price);
            tvDesc = itemView.findViewById(R.id.recyclerItem_desc);

            ivImage = itemView.findViewById(R.id.recyclerItem_image);

            layout = itemView.findViewById(R.id.recyclerItem_layout);
            ivAddIcon = itemView.findViewById(R.id.recyclerItem_iconAdd);

            cvImageHolder = itemView.findViewById(R.id.recyclerItem_imageHolder);

        }
    }


}
