package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_Model;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_RecyclerAdapter;
import com.meimodev.sitouhandler.Helper.APIError;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meimodev.sitouhandler.Constant.toggleSort;

public class NavFragment_Member_Home extends Fragment implements View.OnClickListener {
    private static final String TAG = "NavFragment_Member_Home";
    private View rootView;
    private Context context;

    @BindView(R.id.recyclerView_notifications)
    RecyclerView rvNotifications;

    @BindView(R.id.textView_sortAll)
    TextView tvSortAll;
    @BindView(R.id.textView_sortConfirmed)
    TextView tvSortConfirmed;
    @BindView(R.id.textView_sortUnconfirmed)
    TextView tvSortUnConfirmed;

    @BindView(R.id.layout_sort)
    CardView llSort;

    @BindView(R.id.textView_dataNotFound)TextView tvDataNotFound;

    private ArrayList<Notification_Model> recyclerItems;
    private ArrayList<Notification_Model> recyclerItemsDefault;
    private Notification_RecyclerAdapter recyclerAdapter;

    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_member_home, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        progress = Constant.makeProgressCircle(rootView);

        if (getActivity() != null)
            getActivity().findViewById(R.id.cardView_importantDates).setEnabled(true);

        llSort.setVisibility(View.GONE);
        rvNotifications.setHasFixedSize(false);
        rvNotifications.setItemAnimator(new DefaultItemAnimator());
        rvNotifications.setLayoutManager(new LinearLayoutManager(context));

        tvSortAll.setOnClickListener(this);
        tvSortConfirmed.setOnClickListener(this);
        tvSortUnConfirmed.setOnClickListener(this);


        return rootView;
    }


    private void setupRecyclerView() {

        recyclerAdapter = new Notification_RecyclerAdapter(
                context,
                recyclerItems,
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "ACCEPT " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                },
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "DENIED " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                }
        );
        recyclerItemsDefault = new ArrayList<>(recyclerItems);
        rvNotifications.setAdapter(recyclerAdapter);


        if (progress.getVisibility() == View.VISIBLE) {
            progress.setVisibility(View.INVISIBLE);
        }
        if (llSort.getVisibility() != View.VISIBLE) {
            llSort.setVisibility(View.VISIBLE);
        }
        if (rvNotifications.getVisibility() != View.VISIBLE)
            rvNotifications.setVisibility(View.VISIBLE);

        //        Set Default Sort
        tvSortAll.callOnClick();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_sortAll:

                toggleSort(context, tvSortAll, true);
                toggleSort(context, tvSortConfirmed, false);
                toggleSort(context, tvSortUnConfirmed, false);
                Snackbar.make(rootView, "menampilkan SEMUA data", Snackbar.LENGTH_LONG).show();
                resetRecyclerView(recyclerItemsDefault);
                break;
            case R.id.textView_sortConfirmed:
                recyclerAdapter.notifyDataSetChanged();
                toggleSort(context, tvSortAll, false);
                toggleSort(context, tvSortConfirmed, true);
                toggleSort(context, tvSortUnConfirmed, false);
                Snackbar.make(rootView, "sortir data yang SUDAH DIKONFIRMASI", Snackbar.LENGTH_LONG).show();
                Collections.sort(recyclerItems);
                Collections.reverse(recyclerItems);
                resetRecyclerView(recyclerItems);
                break;
            case R.id.textView_sortUnconfirmed:
                toggleSort(context, tvSortAll, false);
                toggleSort(context, tvSortConfirmed, false);
                toggleSort(context, tvSortUnConfirmed, true);
                Snackbar.make(rootView, "sortir data yang BELUM DIKONFIRMASI", Snackbar.LENGTH_LONG).show();
                Collections.sort(recyclerItems);
                resetRecyclerView(recyclerItems);
                break;

        }
    }

    private void fetchData() {
        if (progress.getVisibility() != View.VISIBLE) {
            progress.setVisibility(View.VISIBLE);
        }
        if (rvNotifications.getVisibility() == View.VISIBLE)
            rvNotifications.setVisibility(View.INVISIBLE);

        ApiServices api = RetrofitClient.getInstance(SharedPrefManager.load(context, SharedPrefManager.KEY_ACCESS_TOKEN).toString()).getApiServices();
        Call<ReadJSONNavFragmentMemberHome> call = api.getMemberHome(((int) SharedPrefManager.getInstance(context).loadUserData(SharedPrefManager.KEY_MEMBER_ID)));
        Callback<ReadJSONNavFragmentMemberHome> callback;
        callback = new Callback<ReadJSONNavFragmentMemberHome>() {

            @Override
            public void onResponse(Call<ReadJSONNavFragmentMemberHome> call, Response<ReadJSONNavFragmentMemberHome> response) {
                if (response.isSuccessful()) {

                    ReadJSONNavFragmentMemberHome res = response.body();

                    if (!res.isError()) {

                        // proceed

                        recyclerItems = new ArrayList<>();
                        recyclerItemsDefault = new ArrayList<>();
                        if (res.getData().size() <= 0){tvDataNotFound.setVisibility(View.VISIBLE);}
                        for (ReadJSONNavFragmentMemberHome.Data data : res.getData()) {
                            recyclerItems.add(new Notification_Model(
                                    data.getIssueId(),
                                    data.getAuthId(),
                                    data.getAuthStatus(),
                                    data.getIssueKey(),
                                    data.getAuthOn(),
                                    data.getIssuedByMemberName(),
                                    data.getIssuedByMemberColumn()
                            ));
                        }

                        setupRecyclerView();

                        if (progress.getVisibility() == View.VISIBLE)
                            progress.setVisibility(View.INVISIBLE);


                        Log.e(TAG, "onResponse: response return success proceeding");
                    } else {

                        Constant.displayDialog(
                                context,
                                null,
                                res.getMessage(),
                                true,
                                (dialogInterface, i) -> {
                                },
                                null
                        );
                        Log.e(TAG, "onResponse: response return success but error");
                    }

                } else {
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ReadJSONNavFragmentMemberHome> call, Throwable t) {
                Log.e(TAG, "onRetry: ", t);
                progress.setVisibility(View.GONE);
                llSort.setVisibility(View.GONE);
                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    fetchData();

                });
            }
        };
        call.enqueue(callback);


    }

    private void sendData(int authorizeId, int authorizeKey) {

        if (progress.getVisibility() != View.VISIBLE) {
            progress.setVisibility(View.VISIBLE);
        }
        if (rvNotifications.getVisibility() == View.VISIBLE)
            rvNotifications.setVisibility(View.INVISIBLE);

        ApiServices api = RetrofitClient.getInstance(SharedPrefManager.load(context, SharedPrefManager.KEY_ACCESS_TOKEN).toString()).getApiServices();
        Call<ResponseBody> call = null;
        switch (authorizeKey) {
            case Constant.AUTHORIZATION_STATUS_CODE_ACCEPTED:
                call = api.authorizeIssue(authorizeId, Constant.AUTHORIZATION_STATUS_ACCEPTED);
                break;
            case Constant.AUTHORIZATION_STATUS_CODE_REJECTED:
                call = api.authorizeIssue(authorizeId, Constant.AUTHORIZATION_STATUS_REJECTED);
                break;
        }

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(response.body());

                    if (!res.isError()) {

                        // proceed
                        fetchData();

                        Constant.displayDialog(
                                context,
                                null,
                                res.getMessage(),
                                true,
                                (dialogInterface, i) -> {
                                },
                                null
                        );

                        Log.e(TAG, "onResponse: response return success proceeding");
                        Log.e(TAG, "onResponse: response return message: " + res.getMessage());

                    } else {

                        Constant.displayDialog(
                                context,
                                null,
                                res.getMessage(),
                                true,
                                (dialogInterface, i) -> {
                                },
                                null
                        );
                        Log.e(TAG, "onResponse: response return success but error");
                        Log.e(TAG, "onResponse: response return message: " + res.getMessage());
                    }

                } else {
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: ", t);
                progress.setVisibility(View.GONE);
                llSort.setVisibility(View.GONE);
                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    sendData(authorizeId, authorizeKey);
                });
            }
        };
        call.enqueue(callback);


    }

    private void resetRecyclerView(ArrayList<Notification_Model> items) {
        rvNotifications.setVisibility(View.GONE);
        recyclerAdapter = new Notification_RecyclerAdapter(
                context,
                items,
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "ACCEPT " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                },
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "DENIED " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                }
        );
        rvNotifications.setVisibility(View.VISIBLE);
        rvNotifications.setAdapter(recyclerAdapter);
    }


    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }
}
