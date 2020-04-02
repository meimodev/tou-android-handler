/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.MemberOperationDialog;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.WebViewActivity;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    private Fetch fetch;

    private BroadcastReceiver brPermissionGranted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Dashboard.ACTION_REQUEST_PERMISSION_GRANTED.contentEquals(action)) {
                fetchData();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_fragment_pnt_sym_manage_member_data, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        Constant.verifyStoragePermissions(getActivity());
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            fetchData();
        }
//        memberOperationDialog = new MemberOperationDialog(context);

        return rootView;
    }

    @Override
    public void onResume() {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            fetchData();
        }
        context.registerReceiver(brPermissionGranted, new IntentFilter(Dashboard.ACTION_REQUEST_PERMISSION_GRANTED));
        super.onResume();
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(brPermissionGranted);
        super.onPause();
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
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                JSONObject data = res.getData();
                proceed(data);
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
    }

    private void proceed(JSONObject data) throws JSONException {

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

        cvView.setOnClickListener(view -> downloadColumnData());

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
    }

    private void downloadColumnData() {

        cvView.setEnabled(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddLLyy", Locale.getDefault());
        String date = dateFormat.format(new Date());

        String fileName = date + "-"
                + Objects.requireNonNull(
                Guru.getString(KEY_COLUMN_NAME_INDEX, "")).replace(" ", "-")
                + ".pdf";

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        String url = URL_DOWNLOAD_REPORT_COLUMN + Guru.getInt(KEY_MEMBER_ID, 0);

        final Request request = new Request(url, "/storage/emulated/0/Download/" + fileName);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        Snackbar.make(rootView, "Sedang mengunduh / download\nSilahkan menunggu sesaat", Snackbar.LENGTH_INDEFINITE)
                .setAction("BATALKAN", v -> {
                    fetch.cancelAll();
                    cvView.setEnabled(true);
                    cvView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent3End));
                })
                .show();
        cvView.setEnabled(false);
        cvView.setCardBackgroundColor(getResources().getColor(R.color.disabled_background));

        fetch.addListener(new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onQueued(@NotNull Download download, boolean bool) {

            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {
                Log.e(TAG, "onCompleted: download is waiting network");
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                Log.e(TAG, "onCompleted: download completed");

                cvView.setEnabled(true);
                cvView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent3End));
                Snackbar.make(rootView, "Selesai mengunduh file: " + fileName, Snackbar.LENGTH_INDEFINITE)
                        .setAction("LIHAT FILE", v -> {
                            cvView.setEnabled(true);
                            cvView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent3End));
                            context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        })
                        .show();
            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {

            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
                Log.e(TAG, "onStarted: download started");

            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
                Log.e(TAG, "onProgress: download progress long l = " + l + " long l1 = " + l1);
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {
            }

            @Override
            public void onCancelled(@NotNull Download download) {
                Log.e(TAG, "onCancelled: download cancelled");
                Snackbar.make(rootView, "Unduhan / Download dibatalkan", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        });

        fetch.enqueue(
                request,
                result -> {

//                    Constant.displayDialog(
//                            context,
//                            "Perhatian !",
//                            "Sedang mengunduh / Download, Silahkan menunggu beberapa saat"
//                    );
//
//                    Snackbar.make(rootView, "Sedang mengunduh / download\nSilahkan menunggu sesaat", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("BATALKAN", v -> fetch.cancelAll())
//                            .show();

                },
                result -> {
                    Log.e(TAG, "onError: yup an error :D :", result.getThrowable());
                    cvView.setEnabled(true);
                    Constant.displayDialog(
                            context,
                            "Perhatian !",
                            "Terjadi kesalahan silahkan coba lagi",
                            true,
                            (dialog, which) -> {
                            },
                            null
                    );
                }
        );
    }


}
