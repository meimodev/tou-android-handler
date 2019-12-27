package com.meimodev.sitouhandler.Dashboard.NavFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.meimodev.sitouhandler.IssueDetail.IssueDetail;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavFragment_AuthHistory extends Fragment implements View.OnClickListener {

    private Context ctx;

    @BindView(R.id.btn_detail)
    Button btnDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nav_fragment_auth_history, container, false);
        ctx = rootView.getContext();
        ButterKnife.bind(this, rootView);

        btnDetail.setOnClickListener(this);

//        close the fab menu
        rootView.findViewById(R.id.layout_main).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        ctx.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));

        switch (view.getId()) {
            case R.id.btn_detail:
                startActivity(new Intent(ctx, IssueDetail.class));
                break;
        }
    }
}
