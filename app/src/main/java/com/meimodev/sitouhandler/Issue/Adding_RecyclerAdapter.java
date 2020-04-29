/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Issue;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.meimodev.sitouhandler.Constant.*;

public class Adding_RecyclerAdapter extends RecyclerView.Adapter<Adding_RecyclerAdapter.MyViewHolder> {

    private final String TAG = "Adding_RecyclerAdapter:";
    private Context ctx;
    private ArrayList<Adding_RecyclerModel> items;

    private int layoutType;

    @BindView(R.id.cardView_main)
    CardView cvMain;

    @BindView(R.id.linearLayout_unregistered)
    LinearLayout llUnregistered;
    @BindView(R.id.textView_unregisteredChurch)
    TextView tvUnregisteredChurch;
    @BindView(R.id.button_unregisteredName)
    Button btnUnregistered;
    @BindView(R.id.textView_unregisteredName)
    TextView tvUnregistered;

    @BindView(R.id.button_del)
    ImageButton btnDelete;

    @BindView(R.id.textView_category)
    TextView tvCategory;
    @BindView(R.id.imageView_pic)
    ImageView ivMain;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_birthDate)
    TextView tvDate;
    @BindView(R.id.textView_kolom)
    TextView tvKolom;


    @BindView(R.id.cardView_baptis)
    CardView cvBaptis;
    @BindView(R.id.cardView_sidi)
    CardView cvSidi;
    @BindView(R.id.cardView_nikah)
    CardView cvNikah;


    public static final int RECYCLER_LAYOUT_ADDING_DETAILS = 5;
    public static final int RECYCLER_LAYOUT_ADDING_NAMESANDPRIEST = 6;

    public Adding_RecyclerAdapter(Context ctx, ArrayList<Adding_RecyclerModel> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public Adding_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.recycler_item_adding, parent, false);


        ButterKnife.bind(this, view);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Adding_RecyclerAdapter.MyViewHolder holder, int position) {
        Adding_RecyclerModel model = items.get(holder.getAdapterPosition());
        Intent intent = new Intent(Adding.ACTION_SELECT_ITEM_RECYCLER_VIEW);
        holder.setIsRecyclable(false);

        if (ctx.getClass().getSimpleName().equals(Adding.class.getSimpleName())) {
            btnDelete.setVisibility(View.GONE);
        }
        else {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(view -> {
                deleteItem(model.getId());
                onRecyclerItemDeleteListener.delete();
            });
        }

        ArrayList<Adding_RecyclerModel> staticReference = new ArrayList<>();
        if (model.isUnregistered()) {
            llUnregistered.setVisibility(View.VISIBLE);
            String churchName = Guru.getString(KEY_CHURCH_NAME, null);
            String churchVillage = Guru.getString(KEY_CHURCH_KELURAHAN, null);
            tvUnregistered.setText(model.getName());
            tvUnregisteredChurch.setText(String.format("Jemaat '%s', %s", churchName.toUpperCase(), churchVillage));

            if (Adding.OPERATION_TYPE == Adding.OPERATION_ADD_NAME_REGISTERED_ONLY) {
                btnUnregistered.setVisibility(View.GONE);
            }

            btnUnregistered.setOnClickListener(view ->
                    Constant.displayDialog(
                            ctx,
                            "Perhatian !",
                            "Anda menggunakan nama yang tidak terdaftar sistem"
                                    + System.lineSeparator()
                                    + "Jemaat " + churchName + ", " + churchVillage
                                    + System.lineSeparator()
                                    + "Pastikan NAMA tersebut sesuai",
                            (dialog, which) -> {
                                if (isModelAlreadySelected(model.getId(), staticReference)) {
                                    ctx.sendBroadcast(new Intent(Adding.ACTION_ALREADY_SELECTED_RECYCLER_VIEW));
                                }
                                else {
                                    intent.putExtra("unregistered", true);
                                    intent.putExtra("model.name", model.getName());
                                    ctx.sendBroadcast(intent);
                                }
                            }
                    ));
        }
        else {
            cvMain.setOnClickListener(view -> {
//                Check if current model already added in selectedModelArray
                if (isModelAlreadySelected(model.getId(), staticReference)) {
                    ctx.sendBroadcast(new Intent(Adding.ACTION_ALREADY_SELECTED_RECYCLER_VIEW));
                }
                else {
                    intent.putExtra("model.id", model.getId());
                    intent.putExtra("model.name", model.getName());
                    intent.putExtra("model.bod", model.getBirthDate());
                    intent.putExtra("model.kol", model.getKolom());
                    intent.putExtra("model.img", model.getImageUrl());
                    intent.putExtra("model.cat", model.getCategory());
                    intent.putExtra("model.bap", model.isBaptis());
                    intent.putExtra("model.sid", model.isSidi());
                    intent.putExtra("model.nik", model.isNikah());
                    intent.putExtra("model.cathead", model.isCategoryHead());
                    ctx.sendBroadcast(intent);
                }

            });

            if (model.getImageUrl() != null) {
                if (model.getImageUrl().contains("/")) {
                    Picasso.get().load(model.getImageUrl()).resize(50, 50).centerCrop().into(ivMain);
                }
            }

            if (model.getName() != null) {
                tvName.setText(model.getName());
            }

            if (model.getBirthDate() != null) {
                tvDate.setText(model.getBirthDate());
            }

            if (model.getKolom() != null) {
                tvKolom.setText(model.getKolom());
            }

            if (model.isBaptis()) {
                cvBaptis.setVisibility(View.VISIBLE);
                cvBaptis.setEnabled(true);
            }
            if (model.isSidi()) {
                cvSidi.setVisibility(View.VISIBLE);
                cvSidi.setEnabled(true);
            }
            if (model.isNikah()) {
                cvNikah.setVisibility(View.VISIBLE);
                cvNikah.setEnabled(true);
            }
            if (model.isCategoryHead()) {
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(model.getCategory());
            }
        }

    }

    private boolean isModelAlreadySelected(int modelId, ArrayList<Adding_RecyclerModel> modelArrayList) {
        if (modelArrayList != null) {
            for (Adding_RecyclerModel model : modelArrayList) {
                if (model.getId() == modelId) return true;
            }
            return false;
        }
        else {
            return false;
        }
    }

    private OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener;

    public void setOnRecyclerItemDeleteListener(OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener) {
        this.onRecyclerItemDeleteListener = onRecyclerItemDeleteListener;
    }


    public void setItems(ArrayList<Adding_RecyclerModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void deleteItem(int modelId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == modelId) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void addItem(Adding_RecyclerModel model) {
        items.add(model);
        notifyItemInserted(getItemCount());
    }

    public ArrayList<Adding_RecyclerModel> getItems() {
        return items;
    }


}
