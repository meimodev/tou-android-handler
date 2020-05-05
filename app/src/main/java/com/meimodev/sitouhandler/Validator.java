/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.CustomWidget.CustomButtonAdd;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Validator extends Constant {
    private static final String TAG = "Validator";
    public static final String MESSAGE_ERROR_IS_EMPTY = "tidak boleh kosong";
    public static final String MESSAGE_ERROR_IS_LEADING_ZERO = "tidak boleh dimulai dengan '0'";
    public static final String MESSAGE_ERROR_IS_INVALID = "tidak valid";

    private Context context;

//    public Validator() {
//    }

    public Validator(Context context) {
        this.context = context;
    }

    public String validateEmail(String email) {

        String[] allowedChar = new String[]{"@", ".", "_", "-"};
        String[] allowedCharReplacement = new String[]{"", "", "", ""};

        if (!StringUtils.isEmpty(validateEmpty(email))) {
            return "E-mail " + validateEmpty(email);
        }

        if (!StringUtils.isAsciiPrintable(email)) {
            return "silahkan gunakan karakter ASCII";
        }

        if (StringUtils.startsWithAny(email, allowedChar)) {
            return "harus diawali dengan huruf";
        }

        if (email.length() < 5) {
            return "silahkan gunakan minimal 5 karakter";
        }

        int countAtSymbol = StringUtils.countMatches(email, "@");
        if (countAtSymbol != 1) {
            return "silahkan gunakan 1 karakter @";
        }

        if (!StringUtils.isAlphanumeric(
                StringUtils.replaceEach(
                        email,
                        allowedChar,
                        allowedCharReplacement))) {
            return "karakter yang di dukung @ . _ -";
        }

        return null;
    }

    public String validateEmail(EditText editText) {
        return validateEmail(editText.getText().toString());
    }

    public String validateEmail(TextInputLayout textInputLayout) {
        String result = validateEmail(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }

    public String validatePhone(String phone) {

        /*
         * phone rules
         * length > 6
         * start with 0
         * all numeric
         *
         * */

        if (!StringUtils.isEmpty(validateEmpty(phone))) {
            return "Nomor Telepon " + validateEmpty(phone);
        }

        if (!StringUtils.isAsciiPrintable(phone)) {
            return "silahkan gunakan karakter ASCII";
        }

        if (!StringUtils.startsWith(phone, "0")) {
            return "harus diawali dengan 0";
        }

        if (phone.length() < 7) {
            return "silahkan gunakan minimal 7 karakter";
        }

        if (!StringUtils.isNumeric(phone)) {
            return "karakter yang di dukung 0 hingga 9";
        }

        return null;
    }

    public String validatePhone(EditText editText) {
        return validatePhone(editText.getText().toString());
    }

    public String validatePhone(TextInputLayout textInputLayout) {
        String result = validatePhone(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }

    public String validatePassword(String pass) {

        /*
         * password rules
         * not empty
         * length > 8
         *
         * */

        if (!StringUtils.isEmpty(validateEmpty(pass))) {
            return "Password " + validateEmpty(pass);
        }

        if (!StringUtils.isAsciiPrintable(pass)) {
            return "silahkan gunakan karakter ASCII";
        }

        if (pass.length() < 7) {
            return "harus melebihi 7 karakter";
        }

        return null;
    }

    public String validatePassword(EditText editText) {
        return validatePassword(editText.getText().toString());
    }

    public String validatePassword(TextInputLayout textInputLayout) {
        String result = validatePassword(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }

    public String validateName(String name) {

        /*
         * Name rules
         * not empty
         * alpha only
         * length > 1
         * */

        if (!StringUtils.isEmpty(validateEmpty(name))) {
            return "Nama " + validateEmpty(name);
        }

        if (!StringUtils.isAsciiPrintable(name)) {
            return "silahkan gunakan karakter ASCII";
        }

        if (name.length() < 2) {
            return "harus melebihi 1 karakter";
        }

        if (!StringUtils.isAlpha(name)) {
            return "karakter yang di dukung a-z A-Z";
        }

        return null;
    }

    public String validateName(EditText editText) {
        return validateName(editText.getText().toString());
    }

    public String validateName(TextInputLayout textInputLayout) {
        String result = validateName(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }

    public String validateSex(String name) {

        /*
         * sex
         * Laki-laki & Perempuan only
         * not empty
         * */

        if (!StringUtils.isEmpty(validateEmpty(name))) {
            return "Jenis Kelamin " + validateEmpty(name);
        }

        if (!StringUtils.isAsciiPrintable(name)) {
            return "silahkan gunakan karakter ASCII";
        }

        if (!name.contentEquals("Laki-laki") && !name.contentEquals("Perempuan")) {
            return "pilih 'Laki-laki' atau 'Perempuan'";
        }

        return null;
    }

    public String validateSex(EditText editText) {
        return validateSex(editText.getText().toString());
    }

    public String validateSex(TextInputLayout textInputLayout) {
        String result = validateSex(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }

    public String validateEmpty(String text) {
        return StringUtils.isEmpty(text) ? "tidak boleh kosong" : null;
    }

    public String validateEmpty(EditText editText) {
        return validateEmpty(editText.getText().toString());
    }

    public String validateEmpty(TextInputLayout textInputLayout) {
        String result = validateEmpty(Objects.requireNonNull(textInputLayout.getEditText()));
        if (result != null) {
            displayErrorMessage(context, result);
            textInputLayout.setError(result);
        }
        else {
            textInputLayout.setError(null);
        }
        return result;
    }


    public Map<String, String> validateInput(
            String keyIssue,
            @Nullable ArrayList<CustomButtonAdd> customButtonAdds,
            @Nullable ArrayList<CustomEditText> customEditTexts,
            @Nullable ArrayList<MaterialSpinner> spinners,
            @Nullable ArrayList<RadioGroup> radioGroups
    ) {
        Map<String, String> map = new HashMap<>();

        switch (keyIssue) {
            ///////////////////////////////////////////     Handle  OUTCOME     ///////////////////////////////////////////
            case KEY_OUTCOME_CENTRALIZE:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah" + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(radioGroups)) {
                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(0))) {
                        return makeMap(true, "Silahkan memilih salah satu tipe sentralisasi");
                    }
                }
                break;
            case KEY_OUTCOME_PAYCHECK:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah" + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if ((isSafe(spinners))) {
                    if (validateSpinner_isItemNotSelected(spinners.get(2))) {
                        return makeMap(true, "Silahkan pilih jenis gaji / tunjangan yang akan diajukan");
                    }
                    if (validateSpinner_isItemNotSelected(spinners.get(0))) {
                        return makeMap(true, "Silahkan pilih staff yang akan diajukan");
                    }
                }
                break;
            case KEY_OUTCOME_PENGADAAN:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                    if (validateSpinner_isItemNotSelected(spinners.get(3))) {
                        return makeMap(true, "Silahkan pilih jenis pengadaan yang akan diajukan");
                    }
                }
                break;
            case KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(1))) {
                        return makeMap(true, "Silahkan pilih nama biaya yang akan diajukan");
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(2))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
                break;
            case KEY_OUTCOME_RAPAT_SIDANG_KONVEN:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }

                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(4))) {
                        return makeMap(true, "Silahkan pilih Jenis Rapat / Sidang / Konven yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(5))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(5))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }

                break;
            case KEY_OUTCOME_DIAKONIA_BESASISWA:
                // Amount
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                // Detail
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(7))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(7))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
                // Button Add Name
//                if (isSafe(customButtonAdds)) {
//                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0)))
//                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
//                }
                // Radio Group
                if (isSafe(radioGroups)) {
                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(2))) {
                        return makeMap(true, "Silahkan pilih jenis " + keyIssue + " yang akan diajukan");
                    }
                }
                break;
            case KEY_OUTCOME_PEMBEKALAN_PELATIHAN:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(4))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(4))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
                if (isSafe(radioGroups)) {
                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(4))) {
                        return makeMap(true, "Silahkan pilih jenis " + keyIssue + " yang akan diajukan");
                    }
                }

                break;
            case KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(6))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(6))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(5))) {
                        return makeMap(true, "Silahkan pilih Jenis Subsidi yang akan diajukan");
                    }
                }

                break;
            case KEY_OUTCOME_OTHER:

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(3))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(3))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
//                if (isSafe(radioGroups)) {
//                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(3)))
//                        return makeMap(true, "Silahkan pilih jenis pengeluaran lainnya yang akan diajukan");
//                }
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(6))) {
                        return makeMap(true, "Silahkan pilih Jenis Biaya yang akan diajukan");
                    }
                }

                break;
            case KEY_OUTCOME_OTHER_NO_ACCOUNT:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(1))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }
                break;
            ///////////////////////////////////////////     Handle  INCOME     ///////////////////////////////////////////
            case KEY_INCOME_PERSEMBAHAN_IBADAH:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "ID Ibadah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(1))) {
                        return makeMap(true, "ID Ibadah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }
                break;
            case KEY_INCOME_SAMPUL_SYUKUR:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }

                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(0))) {
                        return makeMap(true, "Silahkan pilih Jenis Sampul Syukur yang akan diajukan");
                    }
                }

                break;
            case KEY_INCOME_LAINNYA:
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Catatan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(2))) {
                        return makeMap(true, "Catatan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }

                if (isSafe(radioGroups)) {
                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(0))) {
                        return makeMap(true, "Silahkan pilih jenis pemasukan lainnya");
                    }
                }

                break;
            case KEY_INCOME_LAINNYA_NO_ACCOUNT:

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isLeadingZero(customEditTexts.get(0))) {
                        return makeMap(true, "Jumlah " + MESSAGE_ERROR_IS_LEADING_ZERO);
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(3))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isTooShort(customEditTexts.get(3))) {
                        return makeMap(true, "Detil Penggunaan " + MESSAGE_ERROR_IS_INVALID);
                    }
                }

                break;
            ///////////////////////////////////////////     Handle  PAPERS     ///////////////////////////////////////////
            case KEY_PAPERS_VALIDATE_MEMBERS:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan ");
                    }
                }
                break;
            case KEY_PAPERS_CREDENTIAL:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan ");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Tujuan " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Tanggal " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Waktu " + MESSAGE_ERROR_IS_EMPTY);
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(3))) {
                        return makeMap(true, "Tempat " + MESSAGE_ERROR_IS_EMPTY);
                    }
                }

                break;
            case KEY_PAPERS_BAPTIZE:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                    if (customButtonAdds.get(0).getSelectedList().size() <= 3) {
                        return makeMap(true, "Nama yang diajukan tidak cukup, minimal ajukan 4 nama berurutan:" + "\n- 1 Yang Dibatis" + "\n- 1 Ayah" + "\n- 1 Ibu" + "\n- 1 Saksi");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(4))) {
                        return makeMap(true, "Silahkan pilih Tanggal baptisan");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama Pendeta yang meneguhkan");
                    }
                }

                break;
            case KEY_PAPERS_SIDI:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(4))) {
                        return makeMap(true, "Silahkan pilih Tanggal sidi");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama Pendeta yang meneguhkan");
                    }
                }


                break;
            case KEY_PAPERS_MARRIED:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                    if (customButtonAdds.get(0).getSelectedList().size() < 6) {
                        return makeMap(true, "Nama yang diajukan tidak cukup, minimal ajukan 6 nama berurutan:" + "\n- 1 Suami" + "\n- 1 Ayah Suami" + "\n- 1 Ibu Suami" + "\n- 1 Istri" + "\n- 1 Ayah Istri" + "\n- 1 Ibu Istri");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(4))) {
                        return makeMap(true, "Silahkan pilih Tanggal pernikahan");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama Pendeta yang meneguhkan");
                    }
                }

                break;

            ///////////////////////////////////////////     Handle  SERVICE     ///////////////////////////////////////////
            case KEY_SERVICE_KOLOM:

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }


                break;
            case KEY_SERVICE_BIPRA:

                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(3))) {
                        return makeMap(true, "Silahkan pilih Jangkauan Ibadah");
                    }
                    if (validateSpinner_isItemNotSelected(spinners.get(0))) {
                        return makeMap(true, "Silahkan pilih BIPRA yang bersangkutan");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }

                break;
            case KEY_SERVICE_HUT:

                if (isSafe(radioGroups)) {
                    if (validateRadioGroup_isNotSelectedAnything(radioGroups.get(0))) {
                        return makeMap(true, "Silahkan pilih jenis HUT");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }
                break;
            case KEY_SERVICE_PEMAKAMAN:
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }
                break;
            case KEY_SERVICE_PERINGATAN:

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih @aktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }
                break;
            case KEY_SERVICE_KELUARGA:
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(4))) {
                        return makeMap(true, "Silahkan Pilih Jenis Ibadah Keluarga");
                    }
                }
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }
                break;
            case KEY_SERVICE_HARI_RAYA:
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(5))) {
                        return makeMap(true, "Silahkan Pilih Jenis Hari Raya");
                    }
                }
                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
//                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
//                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
//                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }
                break;
            case KEY_SERVICE_SPECIAL:

                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(6))) {
                        return makeMap(true, "Silahkan Pilih Jenis Ibadah Khusus");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
//                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
//                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
//                    }
                }

                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
                }

                break;
            case KEY_SERVICE_LAIN:
                if (isSafe(spinners)) {
                    if (validateSpinner_isItemNotSelected(spinners.get(7))) {
                        return makeMap(true, "Silahkan Pilih Jenis Ibadah Lainnya");
                    }
                }

                if (isSafe(customButtonAdds)) {
                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(0))) {
                        return makeMap(true, "Silahkan pilih Khadim yang akan diajukan");
                    }
//                    if (validateButton_isNotSelectingAnything(customButtonAdds.get(1))) {
//                        return makeMap(true, "Silahkan pilih Nama yang akan diajukan");
//                    }
                }
                if (isSafe(customEditTexts)) {
                    if (validateEditText_isEmpty(customEditTexts.get(0))) {
                        return makeMap(true, "Silahkan pilih Tanggal pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(1))) {
                        return makeMap(true, "Silahkan pilih Waktu pelaksanaan");
                    }
                    if (validateEditText_isEmpty(customEditTexts.get(2))) {
                        return makeMap(true, "Silahkan pilih Tempat pelaksanaan");
                    }
//                    if (validateEditText_isEmpty(customEditTexts.get(3))) {
//                        return makeMap(true, "Silahkan masukan catatan mengenai ibadah");
//                    }
                }
                break;
        }
        map.put("result", "OK");
        map.put("message", "Data is Valid");
        return map;
    }

    public boolean validateButton_isNotSelectingAnything(CustomButtonAdd customButtonAdd) {
        return customButtonAdd.getSelectedList() == null || customButtonAdd.getSelectedList().isEmpty();
    }

    public boolean validateButton_isNotContainAnyView(CustomButtonAdd customButtonAdd) {
        return customButtonAdd.getSelectedView() == null || customButtonAdd.getSelectedView().isEmpty();
    }

    public boolean validateSpinner_isItemNotSelected(MaterialSpinner materialSpinner) {
        return materialSpinner.getItems().get(materialSpinner.getSelectedIndex()).toString().contains("Pilih");
    }

    public boolean validateRadioGroup_isNotSelectedAnything(RadioGroup radioGroup) {
        return radioGroup.getCheckedRadioButtonId() == 0 || radioGroup.getCheckedRadioButtonId() == -1;
    }

    public boolean validateEditText_isEmpty(CustomEditText et) {
        return et.getText().toString().length() == 0;
    }

    public boolean validateEditText_isLeadingZero(CustomEditText et) {
        return et.getText().toString().startsWith("0");
    }

    public boolean validateEditText_isTooShort(CustomEditText et) {
        return et.getText().toString().length() <= 3;
    }

    public boolean validateEditText_isInvalidLetterEntry(CustomEditText et) {
        String h = et.getText().toString();
        if (h.isEmpty()) {
            Log.e(TAG, "validateEditText_isInvalidLetterEntry: empty string");
            return true;
        }
        if (h.length() < 7) {
            Log.e(TAG, "validateEditText_isInvalidLetterEntry: string < 7");
            return true;
        }
        if (!h.contains("/")) {
            Log.e(TAG, "validateEditText_isInvalidLetterEntry: not contain /");
            return true;
        }
        if (!h.contains(".")) {
            Log.e(TAG, "validateEditText_isInvalidLetterEntry: not contain .");
            return true;
        }
        return false;
    }

    private boolean isSafe(ArrayList arrayList) {
        return arrayList != null && !arrayList.isEmpty();
    }

    private Map<String, String> makeMap(boolean error, String message) {
        Map<String, String> map = new HashMap<>();
        if (error) {
            map.put("result", "ERROR");
        }
        else {
            map.put("result", "OK");
        }
        map.put("message", message);
        return map;
    }

    public void displayErrorMessage(Context context, String message) {
        Constant.displayDialog(
                context,
                "Pengajuan belum valid!",
                message,
                (dialog, which) -> {
                }
        );
    }

    public void displaySuccessMessage(Context context) {
        Constant.displayDialog(
                context,
                "OK, Pengajuan Valid",
                "Sedang dalam proses Pengajuan",
                true,
                (dialog, which) -> {
                },
                null,
                dialog -> context.sendBroadcast(new Intent(Constant.ACTION_ACTIVITY_FINISH))
        );

    }

    public static class MaskWatcher implements TextWatcher {
        private boolean isRunning = false;
        private boolean isDeleting = false;
        private final String mask;

        public MaskWatcher(String mask) {
            this.mask = mask;
        }

//        public static MaskWatcher buildCpf() {
//            return new MaskWatcher("###.###.###-##");
//        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            isDeleting = count > after;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (isRunning || isDeleting) {
                return;
            }
            isRunning = true;

            int editableLength = editable.length();
            if (editableLength < mask.length()) {
                if (mask.charAt(editableLength) != '#') {
                    editable.append(mask.charAt(editableLength));
                }
                else if (mask.charAt(editableLength - 1) != '#') {
                    editable.insert(editableLength - 1, mask, editableLength - 1, editableLength);
                }
            }

            isRunning = false;
        }
    }

}
