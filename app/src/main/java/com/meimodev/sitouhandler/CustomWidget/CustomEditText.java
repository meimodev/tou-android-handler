package com.meimodev.sitouhandler.CustomWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.util.Calendar;

public class CustomEditText extends AppCompatEditText {

    private static final String TAG = "CustomEdtText";
    private Context context;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        this.context = context;

    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        this.context = context;
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
//            Log.e(TAG, "Unable to load typeface: " + e.getMessage());
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
        this.setOnFocusChangeListener((view, b) -> displayDatePickerDialog(fragmentManager, b));
        this.setOnClickListener(view -> displayDatePickerDialog(fragmentManager, true));
        this.clearFocus();

    }

    public void setAsTimePicker(FragmentManager fragmentManager) {
        this.setFocusable(false);
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

                } else
                    thisET.setError(null);

//                thisET.clearFocus();
            }
        });
    }

    public void setAsEmailInput() {
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
                if (editable.toString().contains("@")
                        && editable.toString().contains(".")
                        && editable.toString().length() > 3) {
                    thisET.setError(null);
                } else {
                    thisET.setError("E-mail belum valild");
                    thisET.requestFocus();
                }

            }
        });
    }

    void displayDatePickerDialog(FragmentManager fragmentManager, boolean set) {
        if (set) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    (view1, year, monthOfYear, dayOfMonth) -> this.setText(Constant.formatDate(dayOfMonth, monthOfYear, year)),
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            try {
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
                timePickerDialog.show(fragmentManager, null);
            } catch (NullPointerException e) {
                Log.e(TAG, "displayTimePickerDialog: null refrence on FragmentManager", e);
            }

        }
    }
}

