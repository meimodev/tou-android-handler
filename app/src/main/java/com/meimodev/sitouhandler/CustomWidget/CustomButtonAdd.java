package com.meimodev.sitouhandler.CustomWidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomButtonAdd extends AppCompatButton {

    private static final String TAG = "CustomButtonAdd : ";

    public CustomButtonAdd(Context context) {
        super(context);
        selectedView = new ArrayList<>();
    }

    public CustomButtonAdd(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomButtonAdd(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomButtonAdd);
        String customFont = a.getString(R.styleable.CustomButtonAdd_customButtonFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
//            Log.e(TAG, "Unable to load typeface: " + e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }

    private LayoutInflater inflater;
    private LinearLayout parentView;
    private ViewGroup rootViewGroup;
    private OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener;
    private int capacity;

    public void setAsAddingButton(ViewGroup rootViewGroup, LayoutInflater inflater,
                                  LinearLayout parentView, int capacity,
                                  OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener) {
        this.inflater = inflater;
        this.capacity = capacity;
        this.rootViewGroup = rootViewGroup;
        this.parentView = parentView;
        this.onRecyclerItemDeleteListener = onRecyclerItemDeleteListener;
        selectedList = new ArrayList<>();
    }

    private ArrayList<Adding_RecyclerModel> selectedList;

    public void addSelected(Adding_RecyclerModel addingModel) {
        if (parentView != null && parentView.getVisibility() == View.GONE)
            parentView.setVisibility(VISIBLE);
        if (inflater != null) {

            //check if model to add is already exist
            boolean alreadyExist = false;
            for (Adding_RecyclerModel a : selectedList) {
                if (a.getId() == addingModel.getId() && a.getName().contentEquals(addingModel.getName())) {
                    alreadyExist = true;
                    break;
                }
            }
            if (alreadyExist) {

                Constant.displayDialog(
                        getContext(),
                        "Perhatian !",
                        "Tidak bisa menambahkan nama.\nNama: " + addingModel.getName() + " sudah ada dalam daftar nama ini",
                        true,
                        null,
                        null
                );

                return;
            }

            View v = inflater.inflate(R.layout.recycler_item_adding, rootViewGroup, false);

            Button btnDelete = v.findViewById(R.id.button_del);
            btnDelete.setVisibility(VISIBLE);
            btnDelete.setOnClickListener(view -> {
                parentView.removeView(v);
                selectedList.remove(addingModel);
                if (this.getVisibility() == View.GONE) this.setVisibility(VISIBLE);
                if (selectedList.size() == 0) parentView.setVisibility(GONE);
                onRecyclerItemDeleteListener.delete();
            });

            if (addingModel.isUnregistered()) {
                LinearLayout llUnregistered = v.findViewById(R.id.linearLayout_unregistered);
                llUnregistered.setVisibility(View.VISIBLE);

                TextView tvUnregisteredName = v.findViewById(R.id.textView_unregisteredName);
                tvUnregisteredName.setText(addingModel.getName());

                TextView tvUnregisteredChurch = v.findViewById(R.id.textView_unregisteredChurch);
                String churchName = SharedPrefManager.load(getContext(), SharedPrefManager.KEY_CHURCH_NAME).toString();
                String churchVillage = SharedPrefManager.load(getContext(), SharedPrefManager.KEY_CHURCH_VILLAGE).toString();

                tvUnregisteredChurch.setText(String.format("%s, %s", churchName, churchVillage));

                Button btnUnregistered = v.findViewById(R.id.button_unregisteredName);
                btnUnregistered.setVisibility(View.GONE);

                if (addingModel.getCategory() != null && !addingModel.getCategory().isEmpty()) {
                    TextView tvCategory = v.findViewById(R.id.textView_category);
                    tvCategory.setVisibility(View.VISIBLE);
                    tvCategory.setText(addingModel.getCategory());
                }

            } else {

                if (addingModel.getImageUrl() != null) {
                    ImageView ivMain = v.findViewById(R.id.imageView_pic);
                    if (addingModel.getImageUrl().contains("/"))
                        Picasso.get().load(addingModel.getImageUrl()).resize(50, 50).centerCrop().into(ivMain);
                }

                if (addingModel.getName() != null) {
                    TextView tvName = v.findViewById(R.id.textView_name);
                    tvName.setText(addingModel.getName());
                }

                if (addingModel.getBirthDate() != null) {
                    TextView tvDate = v.findViewById(R.id.textView_birthDate);
                    tvDate.setText(addingModel.getBirthDate());
                }

                if (addingModel.getKolom() != null) {
                    TextView tvKolom = v.findViewById(R.id.textView_kolom);
                    tvKolom.setText(addingModel.getKolom());
                }

                if (addingModel.isBaptis()) {
                    CardView cvBaptis = v.findViewById(R.id.cardView_baptis);
                    cvBaptis.setVisibility(View.VISIBLE);
                    cvBaptis.setEnabled(true);
                }

                if (addingModel.isSidi()) {
                    CardView cvSidi = v.findViewById(R.id.cardView_sidi);
                    cvSidi.setVisibility(View.VISIBLE);
                    cvSidi.setEnabled(true);
                }

                if (addingModel.isNikah()) {
                    CardView cvNikah = v.findViewById(R.id.cardView_nikah);
                    cvNikah.setVisibility(View.VISIBLE);
                    cvNikah.setEnabled(true);
                }

                if (addingModel.isCategoryHead()) {
                    TextView tvCategory = v.findViewById(R.id.textView_category);
                    tvCategory.setVisibility(View.VISIBLE);
                    tvCategory.setText(addingModel.getCategory());
                }
            }

            selectedList.add(addingModel);
            parentView.addView(v);

            if (selectedList.size() == capacity) this.setVisibility(GONE);
        } else {
            Log.e(TAG, "addSelected: inflater is NULL, make sure to call 'setAsAddingButton' method");
        }


    }

    public ArrayList<Adding_RecyclerModel> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(ArrayList<Adding_RecyclerModel> selectedList) {
        this.selectedList = selectedList;
    }

    private ArrayList<View> selectedView;

    public ArrayList<View> getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(ArrayList<View> selectedView) {
        this.selectedView = selectedView;
    }

    public void addSelectedView(View view) {
        selectedView.add(view);
    }
}
