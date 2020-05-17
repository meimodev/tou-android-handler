/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Gas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.databinding.ActivityServiceGasBinding;
import com.meimodev.sitouhandler.databinding.ActivityServiceGroceriesBinding;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceGas extends AppCompatActivity {

    private static final String TAG = "ActivityServiceGas";

    private ActivityServiceGasBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceGasBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        changeStatusColor(this, R.color.colorPrimary);

    }
}
