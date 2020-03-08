package com.meimodev.sitouhandler.Issue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.FragmentIncome.Fragment_Income;
import com.meimodev.sitouhandler.Issue.FragmentOutcome.Fragment_Outcome;
import com.meimodev.sitouhandler.Issue.FragmentPapers.Fragment_Papers;
import com.meimodev.sitouhandler.Issue.FragmentServices.Fragment_Services;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Issue extends AppCompatActivity {

    private static final String TAG = "Issue";

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.backButton)
    ImageButton btnBack;
    @BindView(R.id.fragmentContainer)
    ConstraintLayout fragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        ButterKnife.bind(this);
        Constant.changeStatusColor(this, R.color.colorPrimary);

        ArrayList<String> arrayListString = new ArrayList<>();
        ArrayList<String> holder = new ArrayList<>();

        MaterialSpinner.OnItemSelectedListener<String> listener = null;

        int switchKey = getIntent().getIntExtra("ISSUE_TYPE", 0);
        switch (switchKey) {
            case 0:
                Log.e(TAG, "onCreate: Shit switchKey value cannot be 0!");
                break;

            case Constant.ISSUE_TYPE_OUTCOME:
                holder.add("Pilih Jenis Pengeluaran");
//                holder.add("cari data dari laporan evaluasi per semester");
                holder.add(Constant.KEY_OUTCOME_CENTRALIZE);
                holder.add(Constant.KEY_OUTCOME_PAYCHECK);
                holder.add(Constant.KEY_OUTCOME_PENGADAAN);
                holder.add(Constant.KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN);
                holder.add(Constant.KEY_OUTCOME_RAPAT_SIDANG_KONVEN);
                holder.add(Constant.KEY_OUTCOME_DIAKONIA_BESASISWA);
                holder.add(Constant.KEY_OUTCOME_PEMBEKALAN_PELATIHAN);
                holder.add(Constant.KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN);
                holder.add(Constant.KEY_OUTCOME_OTHER);
                holder.add(Constant.KEY_OUTCOME_OTHER_NO_ACCOUNT);

                arrayListString.addAll(holder);

                listener = (view, position, id, item) -> {
                    if (holder.get(0).contains("Pilih")) {
                        animate();
                        holder.remove(0);
                        spinner.setItems(Objects.requireNonNull(holder));
                        spinner.setSelectedIndex(position - 1);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_OUTCOME, item);

                    Fragment fragment = new Fragment_Outcome();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainer.getId(), fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                };
                break;

            case Constant.ISSUE_TYPE_PAPERS:
                holder.add("Pilih Jenis Surat");
                holder.add(Constant.KEY_PAPERS_VALIDATE_MEMBERS);
                holder.add(Constant.KEY_PAPERS_CREDENTIAL);
                holder.add(Constant.KEY_PAPERS_BAPTIZE);
                holder.add(Constant.KEY_PAPERS_SIDI);
                holder.add(Constant.KEY_PAPERS_MARRIED);

                arrayListString.addAll(holder);

                listener = (view, position, id, item) -> {
                    if (holder.get(0).contains("Pilih")) {
                        animate();
                        holder.remove(0);
                        spinner.setItems(Objects.requireNonNull(holder));
                        spinner.setSelectedIndex(position - 1);
                    }


                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_PAPERS, item);

                    Fragment fragment = new Fragment_Papers();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainer.getId(), fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                };
                break;
            case Constant.ISSUE_TYPE_INCOME:
                holder.add("Pilih Jenis Pemasukan");

//                holder.add(" cari data dari laporan evaluasi per semester");
                holder.add(Constant.KEY_INCOME_PERSEMBAHAN_IBADAH);
                holder.add(Constant.KEY_INCOME_SAMPUL_SYUKUR);
                holder.add(Constant.KEY_INCOME_LAINNYA);
                holder.add(Constant.KEY_INCOME_LAINNYA_NO_ACCOUNT);
                arrayListString.addAll(holder);

                listener = (view, position, id, item) -> {
                    if (holder.get(0).contains("Pilih")) {
                        animate();
                        holder.remove(0);
                        spinner.setItems(Objects.requireNonNull(holder));
                        spinner.setSelectedIndex(position - 1);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_INCOME, item);

                    Fragment fragment = new Fragment_Income();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainer.getId(), fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();

                };
                break;

            case Constant.ISSUE_TYPE_SERVICE: //exclusive limited to pelsus kolom
                holder.add("Pilih Jenis Ibadah");
                holder.add(Constant.KEY_SERVICE_KOLOM);
                holder.add(Constant.KEY_SERVICE_BIPRA);
                holder.add(Constant.KEY_SERVICE_HUT);
                holder.add(Constant.KEY_SERVICE_PEMAKAMAN);
//                holder.add(Constant.KEY_SERVICE_PERINGATAN);
                holder.add(Constant.KEY_SERVICE_KELUARGA);
                holder.add(Constant.KEY_SERVICE_HARI_RAYA);
                holder.add(Constant.KEY_SERVICE_SPECIAL);

                holder.add(Constant.KEY_SERVICE_LAIN);
                holder.add(Constant.KEY_SERVICE_SPECIAL_IBADAH_MINGGU);

                arrayListString.addAll(holder);

                listener = (view, position, id, item) -> {

                    if (holder.get(0).contains("Pilih")) {
                        animate();
                        holder.remove(0);
                        spinner.setItems(Objects.requireNonNull(holder));
                        spinner.setSelectedIndex(position - 1);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_SERVICE, item);

                    Fragment fragment = new Fragment_Services();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainer.getId(), fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                };
                break;
        }

        spinner.setItems(Objects.requireNonNull(arrayListString));
        spinner.setOnItemSelectedListener(listener);

        registerReceiver(brFinishActivity, new IntentFilter(Constant.ACTION_ACTIVITY_FINISH));
    }

    private void animate() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_issue_desired);

        ChangeBounds transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(1200);

        TransitionManager.beginDelayedTransition(findViewById(R.id.layout_main), transition);
        constraintSet.applyTo(findViewById(R.id.layout_main));

        btnBack.setVisibility(View.VISIBLE);
        fragmentContainer.setBackgroundColor(getResources().getColor(R.color.backGround_fragment));
    }

    @OnClick(R.id.backButton)
    void onClickBack() {
        finish();
    }

    BroadcastReceiver brFinishActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().contentEquals(Constant.ACTION_ACTIVITY_FINISH)) {
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(brFinishActivity, new IntentFilter(Constant.ACTION_ACTIVITY_FINISH));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(brFinishActivity);
        super.onDestroy();
    }
}
