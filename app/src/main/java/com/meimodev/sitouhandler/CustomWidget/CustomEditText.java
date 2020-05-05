/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.CustomWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomEditText extends TextInputEditText {

    private static final String TAG = "CustomEdtText";
    private Context context;

    CountDownTimer countdownToClearFocus = new CountDownTimer(1500, 1000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            CustomEditText.this.clearFocus();
        }
    };

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
        addOnFocusEscape();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        this.context = context;
        addOnFocusEscape();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        this.context = context;
        addOnFocusEscape();
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        String customFont = a.getString(R.styleable.CustomEditText_customEditTextFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface;

        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
//            Log.e(TAG, " Unable to load typeface: " + e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }

    public void setAsThousandSeparator() {
        this.addTextChangedListener(new ThousandSeparatorTextWatcher(this));
    }

    public void setAsDatePicker(FragmentManager fragmentManager) {
        this.setFocusable(false);
        this.setShowSoftInputOnFocus(false);
        this.setOnFocusChangeListener((view, b) -> displayDatePickerDialog(fragmentManager, b));
        this.setOnClickListener(view -> displayDatePickerDialog(fragmentManager, true));
        this.clearFocus();

    }

    public void setAsTimePicker(FragmentManager fragmentManager) {
        this.setFocusable(false);
        this.setShowSoftInputOnFocus(false);
        this.setOnFocusChangeListener((view, b) -> displayTimePickerDialog(fragmentManager, b));
        this.setOnClickListener(view -> displayTimePickerDialog(fragmentManager, true));
        this.clearFocus();
    }

    public void setAsNoLeadingZero() {
        final EditText thisET = this;
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    thisET.setError("Angka tidak bisa diawali dengan 0");

                }
                else {
                    thisET.setError(null);
                }

//                thisET.clearFocus();
            }
        });
    }

    void displayDatePickerDialog(FragmentManager fragmentManager, boolean set) {
        if (set) {
            Calendar now = Calendar.getInstance();
            Locale locale = new Locale("in", "ID");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", locale);

            com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        this.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            try {
                datePickerDialog.setOkColor(getResources().getColor(android.R.color.white));
                datePickerDialog.setCancelColor(getResources().getColor(android.R.color.white));
                datePickerDialog.setCancelText("BATAL");
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
                datePickerDialog.setLocale(locale);
                datePickerDialog.show(fragmentManager, null);
            } catch (NullPointerException e) {
                Log.e(TAG, "displayDatePickerDialog: null refrence on FragmentManager", e);
            }
        }
    }

    void displayTimePickerDialog(FragmentManager fragmentManager, boolean set) {

        if (set) {
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((view, hourOfDay, minute, second) -> {
                this.setText(Constant.formatTime(hourOfDay, minute, second, false));
            }, true);
            try {
                timePickerDialog.setCancelText("BATAL");
                timePickerDialog.setOkColor(getResources().getColor(android.R.color.white));
                timePickerDialog.setCancelColor(getResources().getColor(android.R.color.white));
                timePickerDialog.show(fragmentManager, null);
            } catch (NullPointerException e) {
                Log.e(TAG, "displayTimePickerDialog: null refrence on FragmentManager", e);
            }

        }
    }

    private void addOnFocusEscape() {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                countdownToClearFocus.start();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countdownToClearFocus.cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                countdownToClearFocus.start();
            }
        });
    }

}

