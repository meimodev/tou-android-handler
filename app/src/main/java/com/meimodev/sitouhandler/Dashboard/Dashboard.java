package com.meimodev.sitouhandler.Dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.meimodev.sitouhandler.BuildConfig;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif.NavFragment_Chief_ManageServiceArea;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Member_Home;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Home;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Member_Issue;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.NavFragment_Member_Setting;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym.NavFragment_PntSym_manageMemberData;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Priest.NavFragment_Priest_SeeServiceArea;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary.NavFragment_Secretary_Papers;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer.NavFragment_Treasurer_Financial;
import com.meimodev.sitouhandler.Issue.Issue;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.SignIn.SignIn;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.meimodev.sitouhandler.Constant.*;
import static com.meimodev.sitouhandler.SharedPrefManager.*;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";

    private static Dashboard instance;

    private DrawerLayout drawer;

    private FloatingActionMenu floatingActionMenu;

    private NavigationView navigationView;

//    BroadcastReceiver brContentInFragmentIsClicked = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (Objects.requireNonNull(intent.getAction()).contentEquals(ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED)) {
//                if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
//            }
//        }
//    };
    private boolean isCollapse = false;
    public static final String ACTION_TOGGLE_COLLAPSE_HEADER = "toggle_header";
    BroadcastReceiver brToggleHeaderCollapse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(ACTION_TOGGLE_COLLAPSE_HEADER)) {

                isCollapse = !isCollapse;
                Guideline guideline = findViewById(R.id.guide);
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
                lp.guidePercent = isCollapse ? 0.10f : 0.30f;
                guideline.setLayoutParams(lp);

                int visibility = isCollapse ? View.GONE : View.VISIBLE;
                LinearLayout llGuide = findViewById(R.id.layout_guide);
                llGuide.setVisibility(visibility);
//                ViewGroup.LayoutParams params = llGuide.getLayoutParams();
//                int collapseHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
//                int expanseHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
//                params.height = isCollapse ? collapseHeight : expanseHeight;
//                llGuide.setLayoutParams(params);

                TextView tvUserName = findViewById(R.id.textView_userName);
                tvUserName.setVisibility(visibility);
                TextView tvChurchPosition = findViewById(R.id.textView_churchPosition);
                tvChurchPosition.setVisibility(visibility);
                TextView tvColumn = findViewById(R.id.textView_column);
                tvColumn.setVisibility(visibility);
                TextView tvChurchAndVillage = findViewById(R.id.textView_churchNameAndVillage);
                tvChurchAndVillage.setVisibility(visibility);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;

//        Binding Auth Token to Retrofit
        RetrofitClient.resetRetrofitClient();
        RetrofitClient.getInstance(((String) load(Dashboard.this, KEY_USER_ACCESS_TOKEN))).getApiServices();

//        Sending this account refreshed FCM Token to server
        sendFCMTokenToServer(Dashboard.this);

        handleAccountNotificationSubscription();

//        init Toolbar And navigation drawer
        setupToolbarAndNavigationDrawer();

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
                .commit();
    }


    @Override
    public void onBackPressed() {
//       Check if Floating Action Menu open then close it
        if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
        else {
//           Check if drawer open then close it
            if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
            else super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // On Stop & Resume
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStop() {
//        unregisterReceiver(brContentInFragmentIsClicked);
//        unregisterReceiver(brUnauthenticatedSignIn);
        unregisterReceiver(brToggleHeaderCollapse);
//        unregisterReceiver(brFetchHome);
        super.onStop();
    }

    @Override
    protected void onResume() {
//        registerReceiver(brUnauthenticatedSignIn, new IntentFilter(ACTION_CONTENT_USER_UNATHENTICATED));
        registerReceiver(brToggleHeaderCollapse, new IntentFilter(ACTION_TOGGLE_COLLAPSE_HEADER));
//        registerReceiver(brContentInFragmentIsClicked, new IntentFilter(ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
//        registerReceiver(brFetchHome, new IntentFilter(Constant.ACTION_REFETCH_MEMBER_HOME));
        super.onResume();
    }

    ///////////////////////////////////////////////////////////////////////////
    // HELPER
    ///////////////////////////////////////////////////////////////////////////

//    BroadcastReceiver brUnauthenticatedSignIn = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (Objects.requireNonNull(intent.getAction()).contentEquals(ACTION_CONTENT_USER_UNATHENTICATED))
//
//
//        }
//    };

    private void handleAccountNotificationSubscription() {
        // subscribe & unsubscribe the related notification topics

        Integer member_id = ((Integer) load(this, KEY_MEMBER_ID));
        if (member_id != null && member_id != 0 && load(this, KEY_MEMBER_BIPRA) != null) {

            FirebaseMessaging fcm = FirebaseMessaging.getInstance();
            //if have church membership than subscribe to gmim member
            fcm.subscribeToTopic(NOTIFICATION_TOPIC_GMIM_MEMBER);
            Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to GMIM Member");

            Integer churchId = ((Integer) load(this, KEY_CHURCH_ID));
            String churchTopic = NOTIFICATION_TOPIC_CHURCH + "_" + churchId;
            // subscribe to church topic
            fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH);
            Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH);

            String churchPosition = ((String) load(this, KEY_CHURCH_POSITION));
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

                } else if (churchPosition.contentEquals(ACCOUNT_TYPE_SECRETARY)) {
                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Chief-Secretary: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S);

                    fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);
                    Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church Executives Secretary-Treasurer: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T);

                } else if (churchPosition.contentEquals(ACCOUNT_TYPE_TREASURER)) {
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

            String bipra = ((String) load(this, KEY_MEMBER_BIPRA));
            Integer columnId = ((Integer) load(this, KEY_COLUMN_ID));
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

            } else if (bipra.contentEquals(BIPRA_WKI)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_WKI);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church WKI: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_WKI);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_WKI);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column WKI: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_WKI);

            } else if (bipra.contentEquals(BIPRA_PEMUDA)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_YOUTH);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church YOUTH: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_YOUTH);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_YOUTH);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Column YOUTH: " + columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_YOUTH);

            } else if (bipra.contentEquals(BIPRA_REMAJA)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church TEENS: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_TEENS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church TEENS: " + columnTopic + "_" + NOTIFICATION_TOPIC_CHURCH_TEENS);

            } else if (bipra.contentEquals(BIPRA_ANAK)) {
                fcm.subscribeToTopic(churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church KIDS: " + churchTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);

                fcm.subscribeToTopic(columnTopic + "_" + NOTIFICATION_TOPIC_COLUMN_KIDS);
                Log.e(TAG, "handleAccountNotificationSubscription: Subscribed to Church KIDS: " + columnTopic + "_" + NOTIFICATION_TOPIC_CHURCH_KIDS);

            }


        } else {
            Log.e(TAG, "handleAccountNotificationSubscription: User not a church member or invalid sign in data, do re-sign in");
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, SignIn.class));
            finishAffinity();
        }


    }



    private void setupNavDrawerItemsBasedOnAccountType() {
        if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_CHIEF)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(1).getSubMenu().setGroupVisible(R.id.nav_group_chief, true);
        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_SECRETARY)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(2).getSubMenu().setGroupVisible(R.id.nav_group_secretary, true);

        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_TREASURER)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(3).getSubMenu().setGroupVisible(R.id.nav_group_treasurer, true);

        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_PRIEST)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(4).getSubMenu().setGroupVisible(R.id.nav_group_priest, true);

        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_PENATUA)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(5).getSubMenu().setGroupVisible(R.id.nav_group_penatua, true);

        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_SYAMAS)) {
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(6).getSubMenu().setGroupVisible(R.id.nav_group_syamas, true);

        } else if (ACCOUNT_TYPE.contains(ACCOUNT_TYPE_MEMBER)) {
            navigationView.setCheckedItem(R.id.nav_home);

        }
    }



//    public static Dashboard getInstance() {
//        return instance;
//    }
//    public void clearApplicationData() {
//        File cacheDirectory = getCacheDir();
//        File applicationDirectory = new File(cacheDirectory.getParent());
//        if (applicationDirectory.exists()) {
//            String[] fileNames = applicationDirectory.list();
//            for (String fileName : fileNames) {
//                if (!fileName.equals("lib")) {
//                    deleteFile(new File(applicationDirectory, fileName));
//                }
//            }
//        }
//    }
//    public static boolean deleteFile(File file) {
//        boolean deletedAll = true;
//        if (file != null) {
//            if (file.isDirectory()) {
//                String[] children = file.list();
//                for (int i = 0; i < children.length; i++) {
//                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
//                }
//            } else {
//                deletedAll = file.delete();
//            }
//        }
//
//        return deletedAll;
//    }

    private void setupFloatingActionMenuAndButtons() {
        //        setup Floating Action Menu & Buttons
        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        FloatingActionButton fab = new FloatingActionButton(Dashboard.this);
        fab.setLabelText("Ajuan Pengeluaran");
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setImageResource(R.drawable.ic_menu_send);
        fab.setOnClickListener(v -> {
            if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            startActivity(new Intent(Dashboard.this, Issue.class).putExtra("ISSUE_TYPE", ISSUE_TYPE_OUTCOME));
        });
        floatingActionMenu.addMenuButton(fab);

        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        fab = new FloatingActionButton(Dashboard.this);
        fab.setLabelText("Ajuan Pemasukan");
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setImageResource(R.drawable.ic_menu_send);
        fab.setOnClickListener(v -> {
            if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            startActivity(new Intent(Dashboard.this, Issue.class).putExtra("ISSUE_TYPE", ISSUE_TYPE_INCOME));
        });
        floatingActionMenu.addMenuButton(fab);

        fab = new FloatingActionButton(Dashboard.this);
        fab.setLabelText("Ajuan Surat");
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setImageResource(R.drawable.ic_menu_send);
        fab.setOnClickListener(v -> {
            if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            startActivity(new Intent(Dashboard.this, Issue.class).putExtra("ISSUE_TYPE", ISSUE_TYPE_PAPERS));
        });
        floatingActionMenu.addMenuButton(fab);

        fab = new FloatingActionButton(Dashboard.this);
        fab.setLabelText("Ajuan Ibadah");
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setImageResource(R.drawable.ic_menu_send);
        fab.setOnClickListener(v -> {
            if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            startActivity(new Intent(Dashboard.this, Issue.class).putExtra("ISSUE_TYPE", ISSUE_TYPE_SERVICE));
        });
        floatingActionMenu.addMenuButton(fab);

        floatingActionMenu.setClosedOnTouchOutside(true);
    }

    private void setupToolbarAndNavigationDrawer() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
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

                //===================================================== CHIEF fragment =====================================================
                case R.id.nav_chief_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_chief_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_chief_manageServiceArea:
                    fragment = new NavFragment_Chief_ManageServiceArea();
                    break;

                //===================================================== SECRETARY fragment =====================================================
                case R.id.nav_secretary_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_secretary_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_secretary_papers:
                    fragment = new NavFragment_Secretary_Papers();
                    break;

                //===================================================== TREASURER fragment =====================================================
                case R.id.nav_treasurer_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_treasurer_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
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
                case R.id.nav_priest_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_priest_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_priest_serviceArea:
                    fragment = new NavFragment_Priest_SeeServiceArea();
                    break;

                //===================================================== PENATUA fragment =====================================================
                case R.id.nav_penatua_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_penatua_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_penatua_manageMemberData:
                    fragment = new NavFragment_PntSym_manageMemberData();
                    break;

                //===================================================== SYAMAS fragment =====================================================
                case R.id.nav_syamas_home:
                    fragment = new NavFragment_Member_Home();
                    break;
                case R.id.nav_syamas_issue:
                    fragment = new NavFragment_Member_Issue();
                    break;
                case R.id.nav_syamas_manageMemberData:
                    fragment = new NavFragment_PntSym_manageMemberData();
                    break;

                //===================================================== MEMBER fragment =====================================================

                case R.id.nav_home:
                    fragment = new Fragment_User_Home();
                    break;
                case R.id.nav_setting:
                    fragment = new NavFragment_Member_Setting();
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

    void setupBackgroundHeader() {
        SharedPrefManager spm = SharedPrefManager.getInstance(Dashboard.this);
        cvImportantDates.setVisibility(View.GONE);
        cvImportantDates.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContent, new Fragment_ImportantDates())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
            if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
        });

        tvUserName.setText(((String) spm.loadUserData(KEY_USER_FULL_NAME)));

        StringBuilder position = new StringBuilder();
        try {
            JSONArray arrayPosition = new JSONArray ((String) spm.loadUserData(KEY_CHURCH_POSITION));
            for (int i = 0; i < arrayPosition.length(); i++) {
                int lastIndex = arrayPosition.length()-1;
                String pos = (String) arrayPosition.get(i);
                position= i==lastIndex? position.append(pos) : position.append(pos).append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvChurchPosition.setText(position);

        tvColumn.setText(((String) spm.loadUserData(KEY_COLUMN_NAME_INDEX)));

        String string = spm.loadUserData(KEY_CHURCH_NAME) + ", " + (spm.loadUserData(KEY_CHURCH_VILLAGE));
        tvChurchNameAndVillage.setText(string);

    }

}
