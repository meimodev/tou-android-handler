/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.squti.guru.Guru;
import com.google.android.material.navigation.NavigationView;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Home;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.NavFragmentSundayServiceIncomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NavFragment_SundayServiceIncome extends Fragment {

    private NavFragmentSundayServiceIncomeBinding b;
    private View rootView;

    private IssueRequestHandler req;

    private SundayServiceIncome_RecyclerAdapter recyclerAdapter;
    private ArrayList<SundayServiceIncomeHelper> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        b = NavFragmentSundayServiceIncomeBinding.inflate(inflater, container, false);
        rootView = b.getRoot();

        rootView.getContext().sendBroadcast(new Intent(Dashboard.ACTION_HEADER_COLLAPSE));
        req = new IssueRequestHandler(rootView);

        b.recyclerViewIncomes.setHasFixedSize(true);
        b.recyclerViewIncomes.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewIncomes.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        recyclerAdapter = new SundayServiceIncome_RecyclerAdapter(getContext(), list);
        b.recyclerViewIncomes.setAdapter(recyclerAdapter);

        fetchData();
        b.layoutFetch.setVisibility(View.GONE);
        b.layoutFetch.setOnClickListener(v -> fetchData());

        return rootView;
    }

    private void fetchData() {
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (res.getData() == null){
                    Constant.displayDialog(
                            rootView.getContext(),
                            "Perhatian!",
                            res.getMessage(),
                            false,
                            (dialog, which) -> {},
                            null,
                            dialog -> navFragmentBackToHome()
                    );
                } else resetRecyclerView(res.getData());
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getSundayIncome(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0)
        ));
    }

    private void navFragmentBackToHome() {
         NavigationView navigationView;
        navigationView =  getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, new Fragment_User_Home())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    private void resetRecyclerView(JSONObject data) throws JSONException {
        b.layoutFetch.setVisibility(View.VISIBLE);

        String str = "Tanggal Akses:"
                .concat(System.lineSeparator())
                .concat(data.getString("date").replace("\\n", System.lineSeparator()));
        b.textViewLastFetch.setText(str);

        JSONArray issues = data.getJSONArray("issues");
        list.clear();
        for (int i = 0; i < issues.length(); i++) {
            JSONObject issue = issues.getJSONObject(i);
            list.add(new SundayServiceIncomeHelper(
                    issue.getInt("issue_id"),
                    issue.getInt("financial_id"),
                    issue.getInt("income_id"),
                    issue.getString("description"),
                    issue.getString("amount")
            ));
        }
        recyclerAdapter.notifyDataSetChanged();

    }

    public static class SundayServiceIncomeHelper {
        private int issueId, financialId, incomeId;
        private String description, amount;

        public SundayServiceIncomeHelper(int issueId, int financialId, int incomeId, String description, String amount) {
            this.issueId = issueId;
            this.financialId = financialId;
            this.incomeId = incomeId;
            this.description = description;
            this.amount = amount;
        }

        public int getIssueId() {
            return issueId;
        }

        public void setIssueId(int issueId) {
            this.issueId = issueId;
        }

        public int getFinancialId() {
            return financialId;
        }

        public void setFinancialId(int financialId) {
            this.financialId = financialId;
        }

        public int getIncomeId() {
            return incomeId;
        }

        public void setIncomeId(int incomeId) {
            this.incomeId = incomeId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

}
