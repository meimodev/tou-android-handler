/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.databinding.ActivityServiceGroceriesBinding;
import com.squareup.picasso.Picasso;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceGroceries extends AppCompatActivity {

    private static final String TAG = "ActivityServiceGrocerie";

    private ActivityServiceGroceriesBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceGroceriesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Picasso.get().load(R.drawable.groceries_teaser).into(b.imageViewMain);

        changeStatusColor(this, R.color.colorPrimary);

    }
}
