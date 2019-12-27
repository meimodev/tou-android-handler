package com.meimodev.sitouhandler.CustomWidget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MemberOperationDialog extends AlertDialog.Builder {
    private static final String TAG = "MemberOperationDialog";
    private Context context;
    private AlertDialog alertDialog;

    private OnMemberOperationDialogOperationListener.AddMemberListener addMemberListener;

    public MemberOperationDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MemberOperationDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

//    public void setAsAddMemberDialog(@NonNull ViewGroup containerView, @NonNull FragmentManager fragmentManager,
//                                     String church_village, String column,
//                                     OnMemberOperationDialogOperationListener.AddMemberListener addMemberListener
//    ) {
//        View view = LayoutInflater.from(context).inflate(R.layout.resource_add_member, containerView, false);
//
//        TextView tvTitle = view.findViewById(R.id.textView_title);
//        tvTitle.setText("Tambah Anggota Baru");
//        TextView tvChurchVillageName = view.findViewById(R.id.textView_churchName);
//        tvChurchVillageName.setText(church_village);
//        TextView tvColumn = view.findViewById(R.id.textView_column);
//        tvColumn.setText(column);
//
//        CustomEditText etFirstName = view.findViewById(R.id.editText_firstName);
//        CustomEditText etMiddleName = view.findViewById(R.id.editText_middleName);
//        CustomEditText etLastName = view.findViewById(R.id.editText_lastName);
//
//        LinearLayout llDegreeHolder = view.findViewById(R.id.layout_degreeCardHolder);
//        Button btnAddDegree = view.findViewById(R.id.button_addDegree);
//        ArrayList<View> degreeViews = new ArrayList<>();
//        String[] strDegree = {"spinner degree 1", "spinner degree 2", "spinner degree 3"};
//
//        btnAddDegree.setOnClickListener(view1 -> {
//            View v = LayoutInflater.from(context).inflate(R.layout.resource_add_member_data, llDegreeHolder, false);
//
//            CardView cvHolder = v.findViewById(R.id.layout_item);
//            MaterialSpinner spinnerDegree = v.findViewById(R.id.spinner_degree);
//            spinnerDegree.setItems(strDegree);
//            cvHolder.setOnClickListener(view2 -> spinnerDegree.expand());
//            Button btnDel = v.findViewById(R.id.button_delete);
//            btnDel.setVisibility(View.GONE);
////            TextView tvDegree = v.findViewById(R.id.textView_degree);
//            String str = "Gelar Akademik " + degreeViews.size();
//            tvDegree.setText(str);
//
//            degreeViews.add(v);
//
//            llDegreeHolder.removeAllViews();
//            for (View keyView : degreeViews) {
//                llDegreeHolder.addView(keyView);
//            }
//        });
//
//        MaterialSpinner spinnerSex = view.findViewById(R.id.spinner_sex);
//        String[] sexStrings = {"Laki-laki", "Perempuan"};
//        spinnerSex.setItems(sexStrings);
//        LinearLayout llSexHolder = view.findViewById(R.id.layout_sexHolder);
//        llSexHolder.setOnClickListener(view1 -> spinnerSex.expand());
//
//        CustomEditText etBOD = view.findViewById(R.id.editText_BOD);
//        etBOD.setAsDatePicker(fragmentManager);
//
//        Switch switchBaptize = view.findViewById(R.id.switch_baptize);
//        LinearLayout llBaptize = view.findViewById(R.id.layout_baptize);
//        switchBaptize.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b) llBaptize.setVisibility(View.VISIBLE);
//            else llBaptize.setVisibility(View.GONE);
//        });
//        Switch switchSidi = view.findViewById(R.id.switch_sidi);
//        LinearLayout llSidi = view.findViewById(R.id.layout_sidi);
//        switchSidi.setOnCheckedChangeListener(((compoundButton, b) -> {
//            if (b) llSidi.setVisibility(View.VISIBLE);
//            else llSidi.setVisibility(View.GONE);
//        }));
//        Switch switchMarried = view.findViewById(R.id.switch_married);
//        LinearLayout llMarried = view.findViewById(R.id.layout_married);
//        switchMarried.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b) llMarried.setVisibility(View.VISIBLE);
//            else llMarried.setVisibility(View.GONE);
//        });
//
//        this.setPositiveButton("Tambah Anggota Baru", (dialogInterface, i) -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("first_name", etFirstName.getText().toString().trim());
//            bundle.putString("middle_name", etMiddleName.getText().toString().trim());
//            bundle.putString("last_name", etLastName.getText().toString().trim());
//
//            ArrayList<String> degrees = new ArrayList<>();
//            for (View v : degreeViews) {
//                MaterialSpinner spinner = v.findViewById(R.id.spinner_degree);
//                degrees.add(spinner.getItems().get(spinner.getSelectedIndex()).toString());
//            }
//            bundle.putStringArrayList("degrees", degrees);
//
//            bundle.putString("sex", spinnerSex.getItems().get(spinnerSex.getSelectedIndex()).toString());
//            bundle.putString("BOD", etBOD.getText().toString());
//
//            bundle.putBoolean("baptize", switchBaptize.isChecked());
//            if (switchBaptize.isChecked())
//                bundle.putString(
//                        "baptize_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_baptizeLetter)).getText().toString()
//                );
//            bundle.putBoolean("sidi", switchSidi.isChecked());
//            if (switchSidi.isChecked())
//                bundle.putString(
//                        "sidi_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_sidiLetter)).getText().toString()
//                );
//            bundle.putBoolean("married", switchMarried.isChecked());
//            if (switchMarried.isChecked())
//                bundle.putString(
//                        "married_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_marriedLetter)).getText().toString()
//                );
//
//            addMemberListener.addMember(bundle);
//        });
//
//        this.setView(view);
//        this.create();
//
//    }

//    public void setAsEditMemberDialog(@NonNull ViewGroup containerView, @NonNull FragmentManager fragmentManager,
//                                      String church_village, String column,
//                                      String firstName, String middleName, String lastName,
//                                      ArrayList<String> selectedDegrees, String sex, String DOB,
//                                      boolean isBaptize, boolean isSidi, boolean isMarried,
//                                      String baptizeLetter, String sidiLetter, String marriedLetter,
//                                      OnMemberOperationDialogOperationListener.EditMemberListener editMemberListener
//    ) {
//        View view = LayoutInflater.from(context).inflate(R.layout.resource_add_member, containerView, false);
//
//        TextView tvTitle = view.findViewById(R.id.textView_title);
//        tvTitle.setText("Ubah Data Anggota");
//        TextView tvChurchVillageName = view.findViewById(R.id.textView_churchName);
//        tvChurchVillageName.setText(church_village);
//        TextView tvColumn = view.findViewById(R.id.textView_column);
//        tvColumn.setText(column);
//
//        CustomEditText etFirstName = view.findViewById(R.id.editText_firstName);
//        etFirstName.setText(firstName);
//        CustomEditText etMiddleName = view.findViewById(R.id.editText_middleName);
//        etMiddleName.setText(middleName);
//        CustomEditText etLastName = view.findViewById(R.id.editText_lastName);
//        etLastName.setText(lastName);
//
////        setup degrees
//        LinearLayout llDegreeHolder = view.findViewById(R.id.layout_degreeCardHolder);
//        Button btnAddDegree = view.findViewById(R.id.button_addDegree);
//        ArrayList<View> degreeViews = new ArrayList<>();
//        ArrayList<String> availableDegrees = new ArrayList<>();
//        availableDegrees.add("degree 1");
//        availableDegrees.add("degree 2");
//        availableDegrees.add("degree 3");
//        availableDegrees.add("degree 4");
//        availableDegrees.add("degree 5");
//
//        for (String degree : selectedDegrees) {
//            View v = LayoutInflater.from(context).inflate(R.layout.resource_add_member_data, llDegreeHolder, false);
//
//            CardView cvHolder = v.findViewById(R.id.layout_item);
//            MaterialSpinner spinnerDegree = v.findViewById(R.id.spinner_degree);
//            spinnerDegree.setItems(availableDegrees);
//
//            for (int i = 0; i < selectedDegrees.size(); i++) {
//                if (degree.contentEquals(availableDegrees.get(i))) {
//                    spinnerDegree.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            cvHolder.setOnClickListener(view2 -> spinnerDegree.expand());
//            Button btnDel = v.findViewById(R.id.button_delete);
//            btnDel.setVisibility(View.GONE);
////            TextView tvDegree = v.findViewById(R.id.textView_degree);
//            String str = "Gelar Akademik " + degreeViews.size();
////            tvDegree.setText(str);
//
//            degreeViews.add(v);
//
//            llDegreeHolder.removeAllViews();
//            for (View keyView : degreeViews) {
//                llDegreeHolder.addView(keyView);
//            }
//        }
//
//        btnAddDegree.setOnClickListener(view1 -> {
//            View v = LayoutInflater.from(context).inflate(R.layout.resource_add_member_data, llDegreeHolder, false);
//
//            CardView cvHolder = v.findViewById(R.id.layout_item);
//            MaterialSpinner spinnerDegree = v.findViewById(R.id.spinner_degree);
//            spinnerDegree.setItems(availableDegrees);
//            cvHolder.setOnClickListener(view2 -> spinnerDegree.expand());
//            Button btnDel = v.findViewById(R.id.button_delete);
//            btnDel.setVisibility(View.GONE);
//            TextView tvDegree = v.findViewById(R.id.textView_degree);
//            String str = "Gelar Akademik " + degreeViews.size();
//            tvDegree.setText(str);
//
//            degreeViews.add(v);
//
//            llDegreeHolder.removeAllViews();
//            for (View keyView : degreeViews) {
//                llDegreeHolder.addView(keyView);
//            }
//        });
//
//        MaterialSpinner spinnerSex = view.findViewById(R.id.spinner_sex);
//        String[] sexStrings = {"Laki-laki", "Perempuan"};
//        spinnerSex.setItems(sexStrings);
//
//        for (int i = 0; i < sexStrings.length; i++) {
//            if (sex.contentEquals(sexStrings[i]))
//                spinnerSex.setSelectedIndex(i);
//        }
//
//        LinearLayout llSexHolder = view.findViewById(R.id.layout_sexHolder);
//        llSexHolder.setOnClickListener(view1 -> spinnerSex.expand());
//
//        CustomEditText etDOB = view.findViewById(R.id.editText_BOD);
//        etDOB.setText(DOB);
//        etDOB.setAsDatePicker(fragmentManager);
//
//        Switch switchBaptize = view.findViewById(R.id.switch_baptize);
//        LinearLayout llBaptize = view.findViewById(R.id.layout_baptize);
//        switchBaptize.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b) llBaptize.setVisibility(View.VISIBLE);
//            else llBaptize.setVisibility(View.GONE);
//        });
//        if (isBaptize) {
//            switchBaptize.setChecked(true);
//            ((CustomEditText) view.findViewById(R.id.editText_baptizeLetter)).setText(baptizeLetter);
//        }
//
//        Switch switchSidi = view.findViewById(R.id.switch_sidi);
//        LinearLayout llSidi = view.findViewById(R.id.layout_sidi);
//        switchSidi.setOnCheckedChangeListener(((compoundButton, b) -> {
//            if (b) llSidi.setVisibility(View.VISIBLE);
//            else llSidi.setVisibility(View.GONE);
//        }));
//        if (isSidi) {
//            switchBaptize.setChecked(true);
//            ((CustomEditText) view.findViewById(R.id.editText_sidiLetter)).setText(sidiLetter);
//        }
//
//        Switch switchMarried = view.findViewById(R.id.switch_married);
//        LinearLayout llMarried = view.findViewById(R.id.layout_married);
//        switchMarried.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b) llMarried.setVisibility(View.VISIBLE);
//            else llMarried.setVisibility(View.GONE);
//        });
//        if (isMarried) {
//            switchBaptize.setChecked(true);
//            ((CustomEditText) view.findViewById(R.id.editText_marriedLetter)).setText(marriedLetter);
//        }
//
//        this.setPositiveButton("Ubah Data Anggota", (dialogInterface, i) -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("first_name", etFirstName.getText().toString().trim());
//            bundle.putString("middle_name", etMiddleName.getText().toString().trim());
//            bundle.putString("last_name", etLastName.getText().toString().trim());
//
//            ArrayList<String> selectedDegreesToFragment = new ArrayList<>();
//            for (View v : degreeViews) {
//                MaterialSpinner spinner = v.findViewById(R.id.spinner_degree);
//                selectedDegreesToFragment.add(spinner.getItems().get(spinner.getSelectedIndex()).toString());
//            }
//            bundle.putStringArrayList("degrees", selectedDegreesToFragment);
//
//            bundle.putString("sex", spinnerSex.getItems().get(spinnerSex.getSelectedIndex()).toString());
//            bundle.putString("BOD", etDOB.getText().toString());
//
//            bundle.putBoolean("baptize", switchBaptize.isChecked());
//            if (switchBaptize.isChecked())
//                bundle.putString(
//                        "baptize_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_baptizeLetter)).getText().toString()
//                );
//            bundle.putBoolean("sidi", switchSidi.isChecked());
//            if (switchSidi.isChecked())
//                bundle.putString(
//                        "sidi_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_sidiLetter)).getText().toString()
//                );
//            bundle.putBoolean("married", switchMarried.isChecked());
//            if (switchMarried.isChecked())
//                bundle.putString(
//                        "married_letter",
//                        ((CustomEditText) view.findViewById(R.id.editText_marriedLetter)).getText().toString()
//                );
//
//            editMemberListener.editMember(bundle);
//        });
//
//        this.setView(view);
//        this.create();
//
//    }

//    public void setAsDeleteMemberDialog(@NonNull ViewGroup containerView,
//                                        OnMemberOperationDialogOperationListener.DeleteMemberListener deleteMemberListener
//    ) {
//        View view = LayoutInflater.from(context).inflate(R.layout.resource_delete_member_data, containerView, false);
//
//        CustomEditText etName = view.findViewById(R.id.editText_name);
//        TextView tvLoading = view.findViewById(R.id.textView_loading);
//
//        CardView cvInfoPlaceHolder = view.findViewById(R.id.cardView_infoService);
//
//        CountDownTimer countdownToFetchData = new CountDownTimer(1500, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                Log.e(TAG, "onFinish: Fetch Data");
//                tvLoading.setVisibility(View.GONE);
//            }
//        };
//
//        etName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                countdownToFetchData.cancel();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                cvInfoPlaceHolder.setVisibility(View.VISIBLE);
//                countdownToFetchData.start();
//            }
//        });
//
//        this.setPositiveButton("Hapus Data Anggota", (dialogInterface, i) -> {
//            Bundle bundle = new Bundle();
//            deleteMemberListener.deleteMember(bundle);
//        });
//
//        this.setView(view);
//        this.create();
//    }
}
