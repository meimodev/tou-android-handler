package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.MemberOperationDialog;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.meimodev.sitouhandler.Constant.*;

public class NavFragment_PntSym_manageMemberData extends Fragment {

    private static final String TAG = "NavFragment_PntSym_mana";

    private View rootView;
    private Context context;

    @BindView(R.id.layout_header)
    CardView cvHeader;

    @BindView(R.id.textView_churchName)
    TextView tvChurchName;
    @BindView(R.id.textView_column)
    TextView tvColumn;

    @BindView(R.id.textView_fathers)
    TextView tvBIPRA_fathers;
    @BindView(R.id.textView_mothers)
    TextView tvBIPRA_mothers;
    @BindView(R.id.textView_youths)
    TextView tvBIPRA_youths;
    @BindView(R.id.textView_teens)
    TextView tvBIPRA_teens;
    @BindView(R.id.textView_kids)
    TextView tvBIPRA_kids;
    @BindView(R.id.textView_total)
    TextView tvBIPRA_total;

    @BindView(R.id.textView_baptize)
    TextView tvBaptize;
    @BindView(R.id.textView_sidi)
    TextView tvSidi;
    @BindView(R.id.textView_married)
    TextView tvMarried;

    @BindView(R.id.cardView_view)
    CardView cvView;
    @BindView(R.id.cardView_add)
    CardView cvAdd;
    @BindView(R.id.cardView_edit)
    CardView cvEdit;
    @BindView(R.id.cardView_delete)
    CardView cvDelete;

    private MemberOperationDialog memberOperationDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_fragment_pnt_sym_manage_member_data, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        memberOperationDialog = new MemberOperationDialog(context);

        return rootView;
    }

    private void fetchData() {
        IssueRequestHandler req = new IssueRequestHandler(rootView);
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getColumnOverview(
                Guru.getInt(KEY_MEMBER_ID, 0)
        ));
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) {
                JSONObject data = res.getData();

                try {
                    //        Setup Header Views
                    tvChurchName.setText(String.format("%s, %s", data.getString("church_name"), data.getString("church_village")));
                    String columnNameIndex = data.getString("column_name_index");
                    tvColumn.setText(columnNameIndex);

                    tvBIPRA_fathers.setText(data.getString("PKB_count"));
                    tvBIPRA_mothers.setText(data.getString("WKI_count"));
                    tvBIPRA_youths.setText(data.getString("pemuda_count"));
                    tvBIPRA_teens.setText(data.getString("remaja_count"));
                    tvBIPRA_kids.setText(data.getString("anak_count"));
                    tvBIPRA_total.setText(data.getString("total_count"));

                    tvBaptize.setText(data.getString("baptize_count"));
                    tvSidi.setText(data.getString("sidi_count"));
                    tvMarried.setText(data.getString("married_count"));

                    String link = data.getString("column_detail_link");

                    cvView.setOnClickListener(view -> {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("title", "Data " + columnNameIndex);
                        intent.putExtra("destination_url", link);
                        startActivity(intent);
                    });

                    cvAdd.setOnClickListener(view -> {
                        Intent i = new Intent(context, PntSym_InputForm.class);
                        i.putExtra("type", PntSym_InputForm.ADD_MEMBER);
                        startActivity(i);
                    });
                    cvEdit.setOnClickListener(view -> {
                        Intent i = new Intent(context, PntSym_InputForm.class);
                        i.putExtra("type", PntSym_InputForm.EDIT_MEMBER);
                        startActivity(i);
                    });
                    cvDelete.setOnClickListener(view -> {
                        Intent i = new Intent(context, PntSym_InputForm.class);
                        i.putExtra("type", PntSym_InputForm.DELETE_MEMBER);
                        startActivity(i);
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: JSON error = " + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
    }

    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }

}
