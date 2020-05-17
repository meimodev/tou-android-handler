/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.databinding.ActivityNewsDetailBinding;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

public class ActivityNewsDetail extends AppCompatActivity {

    private ActivityNewsDetailBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.colorPrimary);

        String title = getIntent().getStringExtra("TITLE");
        if (!StringUtils.isEmpty(title)){
            b.textViewTitle.setText(Html.fromHtml(title));
        }

        String description = getIntent().getStringExtra("DESC");
        if (!StringUtils.isEmpty(description)){
            b.textViewDescription.setText(Html.fromHtml(description));
        }

        String imgUrl = getIntent().getStringExtra("IMG");
        if (!StringUtils.isEmpty(imgUrl)){
            Picasso.get().load(imgUrl).into(b.imageViewMain);
        } else{
            b.imageViewMain.setImageResource(R.drawable.ic_logo_tou_system);
        }

    }
}
