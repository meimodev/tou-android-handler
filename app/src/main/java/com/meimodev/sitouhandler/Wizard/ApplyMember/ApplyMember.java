package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class ApplyMember extends FragmentActivity {

    private static final String TAG = "ApplyMember";

    private ArrayList<Fragment> fragments;

    private ViewPager2 viewPager;

    private FragmentStateAdapter pagerAdapter;

    public static int USER_ID = 0;
    public static String USER_FIRST_NAME = "";
    public static String USER_MIDDLE_NAME = "";
    public static String USER_LAST_NAME = "";
    public static String USER_DEGREE_PRE = "";
    public static String USER_DEGREE_POST = "";
    public static String USER_DATE_OF_BIRTH = "";
    public static String USER_SEX = "";

    public static String STATUS = "";
    public static boolean APPLICABLE = false;
    public static Integer CHURCH_ID = null;
    public static String COLUMN_INDEX = null;
    public static String BAPTIS = "";
    public static String SIDI = "";
    public static String NIKAH = "";

    public static ArrayList<ApplyMemberRegisteredChurchHelper> REGISTERED_CHURCH_LIST;

    @BindView(R.id.btn_fragment)
    Button btnNext;
    @BindView(R.id.spring_dots_indicator)
    SpringDotsIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_member);
        ButterKnife.bind(this);
        Constant.changeStatusColor(this, R.color.colorPrimary);

        registerReceiver(brReplaceWithFinish, new IntentFilter(ACTION_REPLACE_WITH_FINISH));
        registerReceiver(brSendDataToServer, new IntentFilter(ACTION_SEND_DATA_TO_SERVER));

        fetchData();


        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != fragments.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
//            View layout = findViewById(R.id.layout_main);
//            layout.setVisibility(View.GONE);
        });

    }

    private void fetchData() {

        Call call = RetrofitClient.getInstance(null).getApiServices().getApplyMemberPrep(
                Guru.getInt(Constant.KEY_USER_ID, 0)
        );
        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                proceed(res);
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        req.enqueue(call);
    }

    private void proceed(APIWrapper res) throws JSONException {

        //set views
        fragments = new ArrayList<>();
        fragments.add(new Fragment_Intro());
        fragments.add(new Fragment_UserData());
        fragments.add(new Fragment_MemberData());
        fragments.add(new Fragment_Designated());
//        fragments.add(new Fragment_Finish());
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager2(viewPager);

        REGISTERED_CHURCH_LIST = new ArrayList<>();

        JSONObject obj = res.getData();
        JSONObject user = obj.getJSONObject("user");

        USER_ID = user.getInt("id");
        USER_FIRST_NAME = user.getString("first_name");
        USER_MIDDLE_NAME = user.getString("middle_name");
        USER_LAST_NAME = user.getString("last_name");
        USER_DATE_OF_BIRTH = user.getString("date_of_birth");
        USER_SEX = user.getString("sex");


        if (obj.isNull("member_confirm")) {
            // applicable
            APPLICABLE = true;
            JSONArray churches = obj.getJSONArray("churches");
            for (int i = 0; i < churches.length(); i++) {
                JSONObject church = churches.getJSONObject(i);
                String cName = church.getString("church_name");
                int count = church.getInt("count_column");
                int churchId = church.getInt("church_id");
                REGISTERED_CHURCH_LIST.add(new ApplyMemberRegisteredChurchHelper(
                        cName, count, churchId
                ));
            }
        }
        else {
            APPLICABLE = false;
            JSONObject memberConfirm = obj.getJSONObject("member_confirm");
            String status = memberConfirm.getString("status");
            String message;
            if (status.contentEquals("UNCONFIRMED")) {
                message = "Permintaan telah terkirim dan sedang diproses. Silahkan menunggu konfirmasi dari Jemaat Tujuan anda ";
            }
            else {
                message = "Permintaan anda telah ditolak. Anda dapat mengajukan pengajuan Anggota Jemaat kembali setelah 24 Jam semenjak permintaan anda ditolak";
            }
            Constant.displayDialog(
                    this,
                    "Perhatian!",
                    message,
                    false,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> finish());
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager == null) {
            finish();
        }
        else {
            if (viewPager.getCurrentItem() == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                super.onBackPressed();
            }
            else {
                // Otherwise, select the previous step.
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        }

    }

    public static final String ACTION_REPLACE_WITH_FINISH = "action_replace";
    public BroadcastReceiver brReplaceWithFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(ACTION_REPLACE_WITH_FINISH)) {
                replaceWithFinishFragment();
            }
        }
    };
    public static final String ACTION_SEND_DATA_TO_SERVER = "action_send";
    public BroadcastReceiver brSendDataToServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(ACTION_SEND_DATA_TO_SERVER)) {
                sendDataToServer();
            }
        }
    };

    private void replaceWithFinishFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_main, new Fragment_Finish())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Button btnFragment = findViewById(R.id.btn_fragment);
        btnFragment.setVisibility(View.GONE);
        if (viewPager != null) viewPager.setCurrentItem(0);
        btnFragment.setText("OK");
        btnFragment.setOnClickListener(v -> finish());
    }

    private void sendDataToServer() {
//        View layoutMain = findViewById(R.id.layout_main);
        Call call = RetrofitClient.getInstance(null).getApiServices().setApplyMember(
                ApplyMember.USER_ID,
                ApplyMember.USER_FIRST_NAME,
                ApplyMember.USER_MIDDLE_NAME,
                ApplyMember.USER_LAST_NAME,
                ApplyMember.USER_SEX,
                ApplyMember.USER_DATE_OF_BIRTH,
                ApplyMember.CHURCH_ID,
                ApplyMember.COLUMN_INDEX,
                ApplyMember.BAPTIS,
                ApplyMember.SIDI,
                ApplyMember.NIKAH
        );
        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
//                if (layoutMain.getVisibility() == View.VISIBLE) {
//                    layoutMain.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                replaceWithFinishFragment();
//                if (layoutMain.getVisibility() != View.VISIBLE) {
//                    layoutMain.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onRetry() {
                sendDataToServer();
            }
        });
        req.enqueue(call);

//        Log.e(TAG, "==============================================================");
//        Log.e(TAG, "User ID: " + ApplyMember.USER_ID);
//        Log.e(TAG, "first name: " + ApplyMember.USER_FIRST_NAME);
//        Log.e(TAG, "middle name: " + ApplyMember.USER_MIDDLE_NAME);
//        Log.e(TAG, "last name: " + ApplyMember.USER_LAST_NAME);
//        Log.e(TAG, "user sex: " + ApplyMember.USER_SEX);
//        Log.e(TAG, "date of birth: " + ApplyMember.USER_DATE_OF_BIRTH);
//        Log.e(TAG, "church id: " + ApplyMember.CHURCH_ID);
//        Log.e(TAG, "column index: " + ApplyMember.COLUMN_INDEX);
//        Log.e(TAG, "baptis: " + ApplyMember.BAPTIS);
//        Log.e(TAG, "sidi: " + ApplyMember.SIDI);
//        Log.e(TAG, "nikah: " + ApplyMember.NIKAH);
//        Log.e(TAG, "==============================================================");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(brReplaceWithFinish);
        unregisterReceiver(brSendDataToServer);
        super.onDestroy();
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    public class ApplyMemberRegisteredChurchHelper {

        private String churchName;
        private int countColumn;
        private int churchId;

        public ApplyMemberRegisteredChurchHelper(String churchName, int countColumn, int churchId) {
            this.churchName = churchName;
            this.countColumn = countColumn;
            this.churchId = churchId;
        }

        public String getChurchName() {
            return churchName;
        }

        public int getCountColumn() {
            return countColumn;
        }

        public int getChurchId() {
            return churchId;
        }
    }
}
