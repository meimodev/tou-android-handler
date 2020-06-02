/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.squti.guru.Guru;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.meimodev.sitouhandler.BuildConfig;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Account_Setting;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Member_Home;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Member_Issue;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym.NavFragment_PntSym_manageMemberData;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary.NavFragment_Secretary_Papers;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_SundayServiceIncome;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer.NavFragment_Treasurer_Financial;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Home;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityOrderDetail;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityServiceLocation;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Issue;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.Wizard.ApplyMember.ApplyMember;
import com.meimodev.sitouhandler.Wizard.DuplicateCheck.DuplicateCheck;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_CHIEF;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA_ANAK;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA_PKB;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA_REMAJA;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA_WKI;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PENATUA_YOUTH;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_PRIEST;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_SECRETARY;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_SYAMAS;
import static com.meimodev.sitouhandler.Constant.ACCOUNT_TYPE_TREASURER;
import static com.meimodev.sitouhandler.Constant.BIPRA_ANAK;
import static com.meimodev.sitouhandler.Constant.BIPRA_PEMUDA;
import static com.meimodev.sitouhandler.Constant.BIPRA_PKB;
import static com.meimodev.sitouhandler.Constant.BIPRA_REMAJA;
import static com.meimodev.sitouhandler.Constant.BIPRA_WKI;
import static com.meimodev.sitouhandler.Constant.ISSUE_TYPE_INCOME;
import static com.meimodev.sitouhandler.Constant.ISSUE_TYPE_OUTCOME;
import static com.meimodev.sitouhandler.Constant.ISSUE_TYPE_PAPERS;
import static com.meimodev.sitouhandler.Constant.ISSUE_TYPE_SERVICE;
import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_COLUMN_COUNT;
import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_ID;
import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_KELURAHAN;
import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_NAME;
import static com.meimodev.sitouhandler.Constant.KEY_COLUMN_ID;
import static com.meimodev.sitouhandler.Constant.KEY_COLUMN_NAME_INDEX;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_BIPRA;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_CHURCH_POSITION;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_DUPLICATE_CHECK;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_ID;
import static com.meimodev.sitouhandler.Constant.KEY_USER_FULL_NAME;
import static com.meimodev.sitouhandler.Constant.KEY_USER_ID;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_EXECUTIVES;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_KIDS;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_PELSUS;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_PKB;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_TEENS;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_WKI;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_CHURCH_YOUTH;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN_KIDS;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN_PKB;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN_TEENS;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN_WKI;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_COLUMN_YOUTH;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_GMIM_MEMBER;
import static com.meimodev.sitouhandler.Constant.NOTIFICATION_TOPIC_USER;
import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";

    private DrawerLayout drawer;

    private SpeedDialView speedDialView;

    private NavigationView navigationView;

    public static boolean IS_HEADER_COLLAPSE = false;

    public static final String ACTION_HEADER_COLLAPSE = "collapse_header";
    public static final String ACTION_HEADER_EXPAND = "expand_header";
    BroadcastReceiver brToggleHeaderCollapse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(ACTION_HEADER_COLLAPSE)) {
                IS_HEADER_COLLAPSE = true;
            }
            if (intent.getAction().equalsIgnoreCase(ACTION_HEADER_EXPAND)) {
                IS_HEADER_COLLAPSE = false;
            }

            Guideline guideline = findViewById(R.id.guide);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            lp.guidePercent = IS_HEADER_COLLAPSE ? 0.10f : 0.30f;
            guideline.setLayoutParams(lp);

            int visibility = IS_HEADER_COLLAPSE ? View.GONE : View.VISIBLE;

            LinearLayout llGuide = findViewById(R.id.layout_guide);
            llGuide.setVisibility(visibility);
//                llGuide.setLayoutParams(params);
            TextView tvTitle = findViewById(R.id.textView_title);
            tvTitle.setVisibility(IS_HEADER_COLLAPSE ? View.VISIBLE : View.GONE);

            if (Guru.getInt(KEY_MEMBER_ID, 0) == 0) {
                if (!IS_HEADER_COLLAPSE) {
                    if (llApplyMembership.getVisibility() != View.VISIBLE) {
                        llApplyMembership.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    if (llApplyMembership.getVisibility() == View.VISIBLE) {
                        llApplyMembership.setVisibility(View.GONE);
                    }
                }
            }

            TextView tvUserName = findViewById(R.id.textView_userName);
            tvUserName.setVisibility(visibility);
            TextView tvChurchPosition = findViewById(R.id.textView_churchPosition);
            tvChurchPosition.setVisibility(visibility);
            TextView tvColumn = findViewById(R.id.textView_column);
            tvColumn.setVisibility(visibility);
            TextView tvChurchAndVillage = findViewById(R.id.textView_churchNameAndVillage);
            tvChurchAndVillage.setVisibility(visibility);
        }
    };
    public static final String ACTION_BOTTOM_NAV_HOME = "bottomNavHome";
    BroadcastReceiver brActivateBottomNavHome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(ACTION_BOTTOM_NAV_HOME)) {
                bottomNavigationView.setSelectedItemId(R.id.bottomNavBar_news);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        changeStatusColor(this, R.color.colorPrimary);


//        Binding Auth Token to Retrofit
        RetrofitClient.reBuiltRetrofitClient();
//        RetrofitClient.getInstance(Guru.getString(KEY_USER_ACCESS_TOKEN, null)).getApiServices();

//        Sending this account refreshed FCM Token to server
        sendFCMTokenToServer();

        fetchFreshChurchData();

        handleAccountNotificationSubscription();

//        init Toolbar And navigation drawer
        setupToolbarAndNavigationDrawer();

        setupBottomNavBar();

//        Setup Floating Action Menu
        setupFloatingActionMenuAndButtons();

//        Setup BackgroundHeader
        setupBackgroundHeader();

//        set items on nav drawer & default item selected
        setupNavDrawerItemsBasedOnAccountType();

//        transact default checked navigation menu fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, new Fragment_User_Home())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();

        initDuplicateCheck();
    }

    @Override
    public void onBackPressed() {
//       Check if Floating Action Menu open then close it
        if (speedDialView.isOpen()) {
            speedDialView.close(true);
            return;
        }
//           Check if drawer open then close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        //check if fragment
        if (bottomNavigationView.getSelectedItemId() == R.id.bottomNavBar_news) {
            finish();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {
//        unregisterReceiver(brContentInFragmentIsClicked);
//        unregisterReceiver(brUnauthenticatedSignIn);
        unregisterReceiver(brToggleHeaderCollapse);
        unregisterReceiver(brActivateBottomNavHome);
//        unregisterReceiver(brFetchHome);
        super.onStop();
    }

    @Override
    protected void onResume() {
//        registerReceiver(brUnauthenticatedSignIn, new IntentFilter(ACTION_CONTENT_USER_UNATHENTICATED));
        registerReceiver(brToggleHeaderCollapse, new IntentFilter(ACTION_HEADER_EXPAND));
        registerReceiver(brToggleHeaderCollapse, new IntentFilter(ACTION_HEADER_COLLAPSE));
        registerReceiver(brActivateBottomNavHome, new IntentFilter(ACTION_BOTTOM_NAV_HOME));
//        registerReceiver(brContentInFragmentIsClicked, new IntentFilter(ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
//        registerReceiver(brFetchHome, new IntentFilter(Constant.ACTION_REFETCH_MEMBER_HOME));
        super.onResume();
    }

    private void handleAccountNotificationSubscription() {
        // subscribe & unsubscribe the related notification topics

        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        fcm.subscribeToTopic(NOTIFICATION_TOPIC_USER);
        Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to USER");

        int member_id = Guru.getInt(KEY_MEMBER_ID, 0);
        if (member_id != 0 && Guru.getString(KEY_MEMBER_BIPRA, null) != null) {

            //if have church membership than subscribe to gmim member
            fcm.subscribeToTopic(NOTIFICATION_TOPIC_GMIM_MEMBER);
            Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to GMIM Member");

            int churchId = Guru.getInt(KEY_CHURCH_ID, 0);
            String churchTopic = NOTIFICATION_TOPIC_CHURCH + "_" + churchId;
            // subscribe to church topic
            fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH);
            Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH);

            String churchPosition = Guru.getString(KEY_MEMBER_CHURCH_POSITION, null);
            // check if church_executives than subscribe to church executive topics
            if (churchPosition.contentEquals(ACCOUNT_TYPE_CHIEF)
                    || churchPosition.contentEquals(ACCOUNT_TYPE_SECRETARY)
                    || churchPosition.contentEquals(ACCOUNT_TYPE_TREASURER)) {

                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES);

                // also subscribe to pelsus topic
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PELSUS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Pelsus: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PELSUS);

                if (churchPosition.contentEquals(ACCOUNT_TYPE_CHIEF)) {
                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Chief-Secretary: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);

                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Chief-Treasurer: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T);

                }
                else if (churchPosition.contentEquals(ACCOUNT_TYPE_SECRETARY)) {
                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Chief-Secretary: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);

                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Secretary-Treasurer: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);

                }
                else if (churchPosition.contentEquals(ACCOUNT_TYPE_TREASURER)) {
                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Chief-Treasurer: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T);

                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Secretary-Treasurer: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);

                }
            }

            // check if pelsus than subs to pelsus topic
            if (churchPosition.contains(ACCOUNT_TYPE_PENATUA) || churchPosition.contains(ACCOUNT_TYPE_SYAMAS)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PELSUS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Pelsus: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PELSUS);
            }

            String bipra = Guru.getString(KEY_MEMBER_BIPRA, null);
            int columnId = Guru.getInt(KEY_COLUMN_ID, 0);
            String columnTopic = churchTopic + "_" + NOTIFICATION_TOPIC_COLUMN + "_" + columnId;
            // check column than subs to correspond column inside that church
            fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN);
            Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN);

            // check BIPRA church status and subscribe to correspond BIPRA church topics
            // check BIPRA column status and subscribe to correspond BIPRA column topics
            if (bipra.contentEquals(BIPRA_PKB)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PKB);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church PKB: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_PKB);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_PKB);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column PKB: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_PKB);

            }
            else if (bipra.contentEquals(BIPRA_WKI)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_WKI);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church WKI: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_WKI);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_WKI);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column WKI: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_WKI);

            }
            else if (bipra.contentEquals(BIPRA_PEMUDA)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_YOUTH);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church YOUTH: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_YOUTH);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_YOUTH);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column YOUTH: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_YOUTH);

            }
            else if (bipra.contentEquals(BIPRA_REMAJA)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church TEENS: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_TEENS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church TEENS: " + columnTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);

            }
            else if (bipra.contentEquals(BIPRA_ANAK)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church KIDS: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_KIDS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church KIDS: " + columnTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);

            }


        }
        else {
            Log.e(TAG, "handleAccountNotificationSubscription: User not a church member ");


        }


    }

    private void setupNavDrawerItemsBasedOnAccountType() {

        String memberPosition = Guru.getString(KEY_MEMBER_CHURCH_POSITION, null);
        Log.e(TAG, "setupNavDrawerItemsBasedOnAccountType: memberPositions " + memberPosition);

        boolean showSundayIncome = false;
        boolean enableAuthorize = false;
        boolean enableIssuing = false;

        navigationView.setCheckedItem(R.id.nav_home);

        if (memberPosition.contains(ACCOUNT_TYPE_CHIEF)) {
            navigationView.getMenu().findItem(R.id.nav_chief).getSubMenu().setGroupVisible(R.id.nav_group_chief, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_SECRETARY)) {
            navigationView.getMenu().findItem(R.id.nav_secretary).getSubMenu().setGroupVisible(R.id.nav_group_secretary, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_TREASURER)) {
            navigationView.getMenu().findItem(R.id.nav_treasurer).getSubMenu().setGroupVisible(R.id.nav_group_treasurer, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PRIEST)) {
            navigationView.getMenu().findItem(R.id.nav_priest).getSubMenu().setGroupVisible(R.id.nav_group_priest, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA)) {
            navigationView.getMenu().findItem(R.id.nav_penatua).getSubMenu().setGroupVisible(R.id.nav_group_penatua, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_SYAMAS)) {
            navigationView.getMenu().findItem(R.id.nav_syamas).getSubMenu().setGroupVisible(R.id.nav_group_syamas, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA_PKB)) {
            navigationView.getMenu().findItem(R.id.nav_penatua_pkb).getSubMenu().setGroupVisible(R.id.nav_group_penatua_pkb, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA_WKI)) {
            navigationView.getMenu().findItem(R.id.nav_penatua_pkb).getSubMenu().setGroupVisible(R.id.nav_group_penatua_wki, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA_YOUTH)) {
            navigationView.getMenu().findItem(R.id.nav_penatua_pemuda).getSubMenu().setGroupVisible(R.id.nav_group_penatua_pemuda, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA_REMAJA)) {
            navigationView.getMenu().findItem(R.id.nav_penatua_remaja).getSubMenu().setGroupVisible(R.id.nav_group_penatua_remaja, true);
            speedDialView.setVisibility(View.VISIBLE);
            enableIssuing = true;
            enableAuthorize = true;
            showSundayIncome = true;
        }

        if (memberPosition.contains(ACCOUNT_TYPE_PENATUA_ANAK)) {
            navigationView.getMenu().findItem(R.id.nav_penatua_anak).getSubMenu().setGroupVisible(R.id.nav_group_penatua_anak, true);
            speedDialView.setVisibility(View.VISIBLE);
            showSundayIncome = true;
            enableIssuing = true;
            enableAuthorize = true;
        }
        SubMenu navSubMenu = navigationView.getMenu().findItem(R.id.nav_menu).getSubMenu();
        navSubMenu.findItem(R.id.nav_authorize).setVisible(enableAuthorize);
        navSubMenu.findItem(R.id.nav_issue).setVisible(enableIssuing);

        navSubMenu.findItem(R.id.nav_sundayIncome).setVisible(showSundayIncome);
    }

    private void setupFloatingActionMenuAndButtons() {
        //        setup Floating Action Menu & Buttons
        speedDialView = findViewById(R.id.speedDial);
        speedDialView.setUseReverseAnimationOnClose(true);
        speedDialView.setMainFabClosedIconColor(Color.WHITE);
        speedDialView.setMainFabOpenedIconColor(Color.WHITE);
        speedDialView.setExpansionMode(SpeedDialView.ExpansionMode.BOTTOM);

        SpeedDialActionItem item;

        item = new SpeedDialActionItem.Builder(ISSUE_TYPE_SERVICE, R.drawable.ic_cross)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent4End, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.mdtp_white, getTheme()))
                .setLabel("Ajuan Ibadah")
                .setLabelColor(Color.WHITE)
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .setLabelClickable(true)
                .create();
        speedDialView.addActionItem(item);


        item = new SpeedDialActionItem.Builder(ISSUE_TYPE_PAPERS, R.drawable.ic_papers)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent4End, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.mdtp_white, getTheme()))
                .setLabel("Ajuan Surat")
                .setLabelColor(Color.WHITE)
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .setLabelClickable(true)
                .create();
        speedDialView.addActionItem(item);

        item = new SpeedDialActionItem.Builder(ISSUE_TYPE_INCOME, R.drawable.ic_arrow_expand_down)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent4End, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.mdtp_white, getTheme()))
                .setLabel("Ajuan Pemasukkan")
                .setLabelColor(Color.WHITE)
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .setLabelClickable(true)
                .create();
        speedDialView.addActionItem(item);


        item = new SpeedDialActionItem.Builder(ISSUE_TYPE_OUTCOME, R.drawable.ic_arrow_expand_up)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent4End, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.mdtp_white, getTheme()))
                .setLabel("Ajuan Pengeluaran")
                .setLabelColor(Color.WHITE)
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .setLabelClickable(true)
                .create();
        speedDialView.addActionItem(item);


        speedDialView.setOnActionSelectedListener(actionItem -> {
            if (speedDialView.isOpen()) speedDialView.close(true);
            startActivity(new Intent(Dashboard.this, Issue.class).putExtra("ISSUE_TYPE", actionItem.getId()));
            return true;
        });

        speedDialView.setVisibility(View.GONE);

    }

    private void setupToolbarAndNavigationDrawer() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (speedDialView.isOpen()) speedDialView.close(true);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        TextView tvAppName = navigationView.getHeaderView(0).findViewById(R.id.textView_appName);
        TextView tvAppVersion = navigationView.getHeaderView(0).findViewById(R.id.textView_appVersion);
        TextView tvReleasedDate = navigationView.getHeaderView(0).findViewById(R.id.textView_appReleasedDate);

        tvAppName.setText(getApplicationInfo().loadLabel(getPackageManager()).toString());

        String string = "v." + BuildConfig.VERSION_CODE;
        tvAppVersion.setText(string);

        Locale locale = new Locale("in", "ID");
        SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");

        Date date = null;
        try {
            date = format.parse(BuildConfig.BUILD_DATE_STAMP);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat stringFormat = new SimpleDateFormat("MMM dd ''yy", Locale.ENGLISH);
        string = "Released On " + stringFormat.format(date);
        tvReleasedDate.setText(string);
//        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {

            // Transact fragment according to selected navigation item id
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_signOut) {
                Constant.signOut(Dashboard.this);
                return true;
            }

            switch (item.getItemId()) {
                case R.id.nav_authorize:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_sundayIncome:
                    fragment = new NavFragment_SundayServiceIncome();
                    break;

                //===================================================== CHIEF fragment =====================================================
//                case R.id.nav_chief_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_chief_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
//                case R.id.nav_chief_manageServiceArea:
//                    fragment = new NavFragment_Chief_ManageServiceArea();
//                    break;

                //===================================================== SECRETARY fragment =====================================================
//                case R.id.nav_secretary_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_secretary_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
                case R.id.nav_secretary_papers:
                    fragment = new NavFragment_Secretary_Papers();
                    break;

                //===================================================== TREASURER fragment =====================================================
//                case R.id.nav_treasurer_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_treasurer_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
                case R.id.nav_treasurer_financial:
                    fragment = new NavFragment_Treasurer_Financial();
                    break;
//                    case R.id.nav_treasurer_alter_budget:
////                        navigationView.setCheckedItem(R.id.nav_treasurer_home);
////                        fragment = new NavFragment_Member_Home();
//                        // TODO: 2019-08-03 DELAYED the Alter Budget Module!!!
//                        startActivity(new Intent(Dashboard.this, NavActivity_Treasurer_Budget.class));
//                        break;
//                    default:
//                        fragment = new NavFragment_Member_Home();

                //===================================================== PRIEST fragment =====================================================
//                case R.id.nav_priest_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_priest_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
//                case R.id.nav_priest_serviceArea:
//                    fragment = new NavFragment_Priest_SeeServiceArea();
//                    break;

                //===================================================== PENATUA fragment =====================================================
//                case R.id.nav_penatua_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_penatua_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
                case R.id.nav_penatua_manageMemberData:
                    fragment = new NavFragment_PntSym_manageMemberData();
                    break;

                //===================================================== SYAMAS fragment =====================================================
//                case R.id.nav_syamas_home:
//                    fragment = new NavFragment_Member_Home();
//                    break;
//                case R.id.nav_syamas_issue:
//                    fragment = new NavFragment_Member_Issue();
//                    break;
                case R.id.nav_syamas_manageMemberData:
                    fragment = new NavFragment_PntSym_manageMemberData();
                    break;

                //===================================================== MEMBER fragment =====================================================

                case R.id.nav_home:
                    fragment = new Fragment_User_Home();
                    break;
                case R.id.nav_setting:
                    fragment = new NavFragment_Account_Setting();
                    break;
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContent, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .addToBackStack(null)
                        .commit();
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void initDuplicateCheck() {
        int dc = Guru.getInt(KEY_MEMBER_DUPLICATE_CHECK, 0);
        int memberId = Guru.getInt(KEY_MEMBER_ID, 0);
        Log.e(TAG, "initDuplicateCheck: duplicate check = " + dc);
        if (dc == 0 && memberId != 0) {
            startActivity(new Intent(this, DuplicateCheck.class));
        }
    }

    private void sendFCMTokenToServer() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "sendFCMTokenToServer: getInstanceId/FCM_TOKEN failed: ", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    int userId = Guru.getInt(KEY_USER_ID, 0);
                    if (userId == 0) {
                        Log.e(TAG, "sendFCMTokenToServer: USER ID cannot be 0");
                        return;
                    }

                    Log.e(TAG, "sendFCMTokenToServer: new Token Issued -> sending to Server ...");
                    IssueRequestHandler req = new IssueRequestHandler(Dashboard.this);
                    req.setContext(Dashboard.this);
                    req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().setFCMToken(
                            userId, token
                    ));
                });
    }

    private void fetchFreshChurchData() {
        int churchId = Guru.getInt(KEY_CHURCH_ID, 0);
        if (churchId == 0) return;

        //fetch church data in dashboard so the data always fresh

        IssueRequestHandler req = new IssueRequestHandler(Dashboard.this);
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                JSONArray columns = res.getData().getJSONArray("columns");
                Guru.putInt(KEY_CHURCH_COLUMN_COUNT, columns.length());
            }

            @Override
            public void onRetry() {
                fetchFreshChurchData();
            }
        });
        req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().findChurchById(churchId));

    }

    @BindView(R.id.bottomNavBar)
    BottomNavigationView bottomNavigationView;

    private void setupBottomNavBar() {

        if (Guru.getInt(KEY_MEMBER_ID, 0) == 0) {
            bottomNavigationView.getMenu().findItem(R.id.bottomNavBar_warta).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.bottomNavBar_tatacara).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.bottomNavBar_kidung).setVisible(false);
        }

        bottomNavigationView.setVisibility(View.GONE);
    }

    @BindView(R.id.textView_userName)
    TextView tvUserName;
    @BindView(R.id.textView_churchPosition)
    TextView tvChurchPosition;
    @BindView(R.id.textView_column)
    TextView tvColumn;
    @BindView(R.id.textView_churchNameAndVillage)
    TextView tvChurchNameAndVillage;
    @BindView(R.id.cardView_importantDates)
    CardView cvImportantDates;
    @BindView(R.id.textView_title)
    TextView tvTitle;

    @BindView(R.id.layout_infoHolder)
    LinearLayout llInfoHolder;
    @BindView(R.id.layout_applyMembership)
    LinearLayout llApplyMembership;
    @BindView(R.id.button_apply)
    Button btnApply;

    @BindView(R.id.layout_fancyBackground)
    ConstraintLayout layoutFancyBackground;

    private void setupBackgroundHeader() {

        layoutFancyBackground.setOnClickListener(v -> {
            if (IS_HEADER_COLLAPSE) {
                sendBroadcast(new Intent(ACTION_HEADER_EXPAND));
            }
            else {
                sendBroadcast(new Intent(ACTION_HEADER_COLLAPSE));
            }
        });

        cvImportantDates.setVisibility(View.GONE);
        cvImportantDates.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContent, new Fragment_ImportantDates())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
            if (speedDialView.isOpen()) speedDialView.close(true);
        });

        tvTitle.setText("TOU-System");
        if (Guru.getInt(KEY_MEMBER_ID, 0) == 0) {
            llInfoHolder.setVisibility(View.GONE);
            llApplyMembership.setVisibility(View.VISIBLE);

            btnApply.setOnClickListener(v -> {
                startActivity(new Intent(this, ApplyMember.class));
            });


            return;
        }
        else {
            llInfoHolder.setVisibility(View.VISIBLE);
            llApplyMembership.setVisibility(View.GONE);
        }

        tvUserName.setText(Guru.getString(KEY_USER_FULL_NAME, null));

        String position = Guru.getString(KEY_MEMBER_CHURCH_POSITION, null).replace("\\n", System.lineSeparator());
        Log.e(TAG, "setupBackgroundHeader: position " + position);
        tvChurchPosition.setText(position);

        String column = Guru.getString(KEY_COLUMN_NAME_INDEX, null);
        if (column != null) {
            tvColumn.setVisibility(View.VISIBLE);
            tvColumn.setText(column);
        }
        else {
            tvColumn.setVisibility(View.GONE);
        }

        String churchName = Guru.getString(KEY_CHURCH_NAME, null);
        if (churchName != null) {
            String churchVillage = Guru.getString(KEY_CHURCH_KELURAHAN, null);
            String string = churchName + ", " + churchVillage;
            tvChurchNameAndVillage.setText(string);
        }
        else {
            tvChurchNameAndVillage.setVisibility(View.GONE);
        }


    }

    public static final String ACTION_REQUEST_PERMISSION_GRANTED = "ACTION_REQUEST_PERMISSION_GRANTED";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: CALLED !");
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendBroadcast(new Intent(ACTION_REQUEST_PERMISSION_GRANTED));
        }
        else {
            Constant.displayDialog(
                    this,
                    "Perhatian !",
                    "Fitur ini mengharuskan anda memberi izin kepada aplikasi untuk mengakses sistem operasi handphone anda",
                    false,
                    (dialog, which) -> {
                        NavigationView nav = findViewById(R.id.nav_view);
                        nav.setCheckedItem(R.id.nav_home);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContent, new Fragment_User_Home())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();

                    }, null
            );
        }
    }
}
