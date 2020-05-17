/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Cookies;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.databinding.ActivityServiceCookiesBinding;
import com.meimodev.sitouhandler.databinding.ActivityServiceGasBinding;
import com.squareup.picasso.Picasso;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceCookies extends AppCompatActivity {

    private static final String TAG = "ActivityServiceCookies";

    private ActivityServiceCookiesBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceCookiesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Picasso.get().load(R.drawable.cookies_teaser).into(b.imageViewMain);

        changeStatusColor(this, R.color.colorPrimary);

    }
}
