/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2021. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignIn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.meimodev.sitouhandler.databinding.ActivityLoggerBinding;

import java.util.ArrayList;

public class Logger extends AppCompatActivity {

    public static ArrayList<String> loggers = new ArrayList<>();


    private ArrayAdapter<String> adapterLog;
    private ActivityLoggerBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoggerBinding.inflate(getLayoutInflater());

        setContentView(b.getRoot());
        loggers.add("Init Log ..................");
        adapterLog = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                loggers
                );

        b.listView.setAdapter(adapterLog);

    }

    @Override
    protected void onStop() {
        loggers.clear();
        super.onStop();
    }


}