package com.meimodev.sitouhandler.Dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.IssueDetail.IssueDetailNames_RecyclerAdapter;
import com.meimodev.sitouhandler.IssueDetail.IssueDetailNames_RecyclerModel;
import com.meimodev.sitouhandler.Models.BasicMember_Model;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ImportantDates extends Fragment implements View.OnClickListener {

    private Context context;
    private View rootView;
    @BindView(R.id.recyclerView_importantDates)
    RecyclerView rvImportantDates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_important_dates, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        if (getActivity() != null)
            getActivity().findViewById(R.id.cardView_importantDates).setEnabled(false);

        setupRecyclerView(container);
        rootView.setOnClickListener(this);

        return rootView;
    }

    private void setupRecyclerView(ViewGroup container) {
        rvImportantDates.setHasFixedSize(false);
        rvImportantDates.setItemAnimator(new DefaultItemAnimator());
        rvImportantDates.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<Fragment_ImportantDates_RecyclerModel> items = new ArrayList<>();

        ArrayList<String> issuedByPositions = new ArrayList<>();
        issuedByPositions.add("Penatua Kolom 4");
        issuedByPositions.add("Sekertaris Jemaat");
        issuedByPositions.add("Panitia HRG 33223");

        items.add(new Fragment_ImportantDates_RecyclerModel(
                99,
                Constant.KEY_SERVICE_BIPRA,
                "Pukul 18.00",
                "22",
                "Jan",
                "Kel. Jhon Mambao",
                "Kolom 99",
//                Priest in charge
                new IssueDetailNames_RecyclerModel(
                        new BasicMember_Model(
                                10,
                                "jhon",
                                "Oprotas",
                                "89 September 2421",
                                2,
                                "Jssffs",
                                "Kolom 2",
                                "dasdasfs",
                                "r24234d",
                                "afsf3q4"
                        ),
                        "",
                        false
                ),
//                Issued By
                new BasicMember_Model(
                        998,
                        "Mumbo",
                        "Jumbo",
                        "01 Agustus ",
                        issuedByPositions
                )
        ));
        items.add(new Fragment_ImportantDates_RecyclerModel(
                6675,
                Constant.KEY_SERVICE_HUT,
                "Pukul 01.00",
                "90",
                "Des",
                "Kel. Mamabomgo",
                "Kolom 5664",
                //                Priest in charge
                new IssueDetailNames_RecyclerModel(
                        new BasicMember_Model(
                                10,
                                "jhon",
                                "Ukulele",
                                "22 January 2999",
                                2,
                                "Jssffs",
                                "Kolom 2",
                                "dasdasfs",
                                "r24234d",
                                "afsf3q4"
                        ),
                        "",
                        false
                ),
//                Issued By
                new BasicMember_Model(
                        998,
                        "Mumbo",
                        "Jumbo",
                        "01 Agustus ",
                        issuedByPositions
                )
        ));


        items.add(new Fragment_ImportantDates_RecyclerModel(
                98976,
                Constant.KEY_SERVICE_PERINGATAN,
                "Pukul 12.00",
                "55",
                "Feb",
                "Kel. Kongkali-kong",
                "Kolom 19",
//                Priest in charge
                new IssueDetailNames_RecyclerModel(
                        new BasicMember_Model(
                                10,
                                "jhon",
                                "mamba",
                                "208 February 2999",
                                2,
                                "Jssffs",
                                "Kolom 2",
                                "dasdasfs",
                                "r24234d",
                                "afsf3q4"
                        ),
                        "",
                        false
                ),
//                Issued By
                new BasicMember_Model(
                        998,
                        "Mumbo",
                        "Jumbo",
                        "01 Agustus 2020",
                        issuedByPositions
                )
        ));


        Fragment_ImportantDates_RecyclerAdapter adapter = new Fragment_ImportantDates_RecyclerAdapter(context, items, data -> {
//            Dialog important Date Detail
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            View view = getLayoutInflater().inflate(R.layout.dialog_datedetail, container, false);
            TextView tvTitle = view.findViewById(R.id.textView_title);
            tvTitle.setText(data.getString("issue"));
            TextView tvPlace = view.findViewById(R.id.textView_place);
            tvPlace.setText(data.getString("place"));
            TextView tvDate = view.findViewById(R.id.textView_date);
            tvDate.setText(data.getString("date"));
            TextView tvTime = view.findViewById(R.id.textView_time);
            tvTime.setText(data.getString("time"));
            TextView tvIssuedByName = view.findViewById(R.id.textView_issuedByName);
            tvIssuedByName.setText(data.getString("issuedBy_name"));
            TextView tvIssuedByCol = view.findViewById(R.id.textView_issuedByColumn);
            tvIssuedByCol.setText(data.getString("issuedBy_column"));

            ArrayList<String> positions = data.getStringArrayList("issuedBy_position");
            if (positions != null) {
                TextView tvIssuedByPos = view.findViewById(R.id.textView_issuedByPosition);
                String position = "";
                for (int i = 0; i < positions.size(); i++) {
                    String pos = positions.get(i);
                    if (i == positions.size() - 1)
                        position = position.concat(pos);
                    else position = position.concat(pos).concat("\n");
                }
                tvIssuedByPos.setText(position);
            }

            RecyclerView rvDialog = view.findViewById(R.id.recyclerView_names);
            ArrayList<IssueDetailNames_RecyclerModel> dialogItems = new ArrayList<>();
            dialogItems.add(
                    new IssueDetailNames_RecyclerModel(
                            new BasicMember_Model(
                                    data.getInt("priest_ID")
                                    , "" + data.getString("priest_name")
                                    , ""
                                    , "" + data.getString("priest_DOB")
                                    , 0
                                    , ""
                                    , "" + data.getString("priest_column")
                                    , "" + data.getString("priest_baptize")
                                    , "" + data.getString("priest_sidi")
                                    , "" + data.getString("priest_married")
                            )
                            , "Khadim : "
                            , false
                    ));
            IssueDetailNames_RecyclerAdapter dialogAdapter = new IssueDetailNames_RecyclerAdapter(dialogItems, context);
            rvDialog.setVisibility(View.VISIBLE);
            rvDialog.setHasFixedSize(true);
            rvDialog.setItemAnimator(new DefaultItemAnimator());
            rvDialog.setLayoutManager(new LinearLayoutManager(context));
            rvDialog.setAdapter(dialogAdapter);


            builder.setView(view);
            builder.create().show();


        });
        rvImportantDates.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));

//        switch (view.getId()) {
//            case R.id.cardView:
//                context.startActivity(new Intent(context, DateDetail.class));
//                break;
//        }

    }

    @Override
    public void onDetach() {
        if (getActivity() != null)
            getActivity().findViewById(R.id.cardView_importantDates).setEnabled(true);
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (getActivity() != null)
            getActivity().findViewById(R.id.cardView_importantDates).setEnabled(true);
        super.onDestroyView();
    }
}
