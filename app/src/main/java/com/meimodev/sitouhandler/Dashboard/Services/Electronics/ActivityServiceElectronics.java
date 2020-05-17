/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Electronics;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.databinding.ActivityServiceElectronicsBinding;
import com.meimodev.sitouhandler.databinding.ActivityServiceGroceriesBinding;
import com.squareup.picasso.Picasso;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceElectronics extends AppCompatActivity {

    private static final String TAG = "ActivityServiceElect";

    private ActivityServiceElectronicsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceElectronicsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Picasso.get().load(R.drawable.electronics_teaser).into(b.imageViewMain);

        changeStatusColor(this, R.color.colorPrimary);

    }
}
