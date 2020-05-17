/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.Validator;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_UserData extends Fragment {
    private static final String TAG = "Fragment_UserData";
    private Context context;
    private View rootView;

    @BindView(R.id.textInputLayout_firstName)
    TextInputLayout tilFirstName;
    @BindView(R.id.editText_firstName)
    CustomEditText etFirstName;
    @BindView(R.id.textInputLayout_middleName)
    TextInputLayout tilMiddleName;
    @BindView(R.id.textInputLayout_lastName)
    TextInputLayout tilLastName;
    @BindView(R.id.textInputLayout_dob)
    TextInputLayout tilDob;
    @BindView(R.id.editText_dob)
    CustomEditText etDob;

    @BindView(R.id.radioButton_male)
    RadioButton rbMale;
    @BindView(R.id.radioButton_female)
    RadioButton rbFemale;
    @BindView(R.id.radioGroup_sex)
    RadioGroup rgSex;

    private Validator validator ;
    private ViewPager2 viewPager2;

    private String sex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_apply_member_user_data, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        validator = new Validator(context);

//        spinnerSex.setAdapter(
//                new ArrayAdapter<>(
//                        context,
//                        R.layout.resoruce_dropdown_popup_large,
//                        new String[]{"Laki-laki", "Perempuan"}
//                )
//        );

        etDob.setAsDatePicker(getFragmentManager());

        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            sex = ((RadioButton) rgSex.findViewById(checkedId)).getText().toString();
        });


        if (ApplyMember.USER_ID != 0) {
            tilFirstName.getEditText().setText(ApplyMember.USER_FIRST_NAME);
            tilMiddleName.getEditText().setText(ApplyMember.USER_MIDDLE_NAME);
            tilLastName.getEditText().setText(ApplyMember.USER_LAST_NAME);
            tilDob.getEditText().setText(ApplyMember.USER_DATE_OF_BIRTH);
//            spinnerSex.setSelectedIndex(ApplyMember.USER_SEX.contains("laki") ? 0 : 1);
            if (ApplyMember.USER_SEX.contains("laki")){
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
        }



        viewPager2 = getActivity().findViewById(R.id.viewPager);

        return rootView;
    }


    @Override
    public void onPause() {
        tilFirstName.setError(validator.validateName(tilFirstName));
        String safeMiddleName = tilMiddleName.getEditText().getText().toString();

        safeMiddleName =
                safeMiddleName.contains(" ")
                        ? StringUtils.replace(safeMiddleName, " ", "XXX")
                        : safeMiddleName;
        String middleName = validator.validateName(safeMiddleName);
        tilMiddleName.setError(
                !StringUtils.isEmpty(middleName) ||
                         middleName.contains("melebihi")
                        || middleName.contains("kosong")
                        ? null : middleName
        );
        tilLastName.setError(validator.validateName(tilLastName));

        //set view pager item to this view page index
        if (tilFirstName.getError() != null
                || tilMiddleName.getError() != null
                || tilLastName.getError() != null) {
            viewPager2.setCurrentItem(1, true);
        }
        else {
            ApplyMember.USER_FIRST_NAME = tilFirstName.getEditText().getText().toString();
            ApplyMember.USER_MIDDLE_NAME = safeMiddleName;
            ApplyMember.USER_LAST_NAME = tilLastName.getEditText().getText().toString();
            ApplyMember.USER_DATE_OF_BIRTH = tilDob.getEditText().getText().toString();
            ApplyMember.USER_SEX = sex;
        }
        super.onPause();
    }
}
