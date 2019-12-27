package com.meimodev.sitouhandler.IssueDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.WebViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueDetail extends AppCompatActivity {

    public String TAG = "IssueDetail : ";

    @BindView(R.id.layout_namesHolder)
    LinearLayout llNames;

    @BindView(R.id.linearLayout_notationPlaceholder)
    LinearLayout llNotationPlaceHolder;

    @BindView(R.id.textView_letterNumberEntry)
    TextView tvLetterNumberEntry;
    @BindView(R.id.textView_serviceEntryId)
    TextView tvServiceEntryId;
    @BindView(R.id.linearLayout_serviceEntryId)
    LinearLayout llServiceEntryId;

    @BindView(R.id.textView_keyIssue)
    TextView tvKeyIssue;

    @BindView(R.id.textView_issuedDate)
    TextView tvIssuedDate;
    @BindView(R.id.cardView_acceptRejectBadge)
    CardView cvAcceptReject;
    @BindView(R.id.textView_acceptedRejectedText)
    TextView tvAcceptRejectText;
    @BindView(R.id.textView_acceptedRejectedDate)
    TextView tvAcceptRejectDate;

    @BindView(R.id.linearLayout_buttons)
    LinearLayout llButtons;
    @BindView(R.id.button_accept)
    Button btnAccept;
    @BindView(R.id.button_deny)
    Button btnDeny;
    @BindView(R.id.button_viewPaper)
    Button btnViewPaper;

    @BindView(R.id.cardView_printable)
    CardView cvPrintable;

    @BindView(R.id.layout_body)
    View llBody;

    @BindView(R.id.textView_description)
    TextView tvDescription;
    @BindView(R.id.cardView_description)
    CardView cvDescription;

    private boolean isAccepted;
    private boolean isRejected;
    private String keyIssue;
    private String issuedDate;
    private String finishDate;
    //    private String rejectedDate;
    private String letterNumber;
    private ArrayList<IssueDetailModelHelper> names;
    private ArrayList<IssueDetailNotation_NotationModel> notations;
    private Map<String, String> issuedByMember;

    private View progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        ButterKnife.bind(this);

        progress = Constant.makeProgressCircle(findViewById(R.id.layout_main));

        fetchData();

    }

    private void fetchData() {

        Context context = IssueDetail.this;

        if (llBody.getVisibility() == View.VISIBLE)
            llBody.setVisibility(View.INVISIBLE);
//        if (llButtons.getVisibility() == View.VISIBLE)
//            llButtons.setVisibility(View.INVISIBLE);
        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        Log.e(TAG, "fetchData: fetching..");
        ApiServices api = RetrofitClient.getInstance(null).getApiServices();
        Call<ResponseBody> call = api.getIssue(getIntent().getIntExtra("ISSUE_ID", 0));
        Callback<ResponseBody> callback = new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(response.body());

                    if (!res.isError()) {

                        try {
                            proceed(res);
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: JSON ERROR: " + e.getMessage(), e);
                            e.printStackTrace();
                        }
                        if (progress.getVisibility() == View.VISIBLE)
                            progress.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "onResponse: response return success proceeding");
                    } else {
                        Constant.displayDialog(context, null, res.getMessage(), true,
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: ", t);
                progress.setVisibility(View.GONE);
                Constant.makeFailFetch(findViewById(R.id.layout_main), view -> {
                    progress = Constant.makeProgressCircle(findViewById(R.id.layout_main));
                    fetchData();
                });
            }
        };
        call.enqueue(callback);
    }

    private void proceed(APIWrapper res) throws JSONException {

        JSONObject obj = res.getData();
        JSONArray issueAuth = obj.getJSONArray("issue_authorization");

        notations = new ArrayList<>();

        for (int i = 0; i < issueAuth.length(); i++) {
            JSONObject auth = issueAuth.getJSONObject(i);
            String authStatus = auth.getString("auth_status");
            String authChurchPosition = auth.getString("auth_church_position");
            String authOn = auth.getString("auth_on");

            notations.add(new IssueDetailNotation_NotationModel(
                    authChurchPosition, authOn, authStatus)
            );
        }
        for (int i = 0; i < issueAuth.length(); i++) {
            JSONObject auth = issueAuth.getJSONObject(i);
            String authStatus = auth.getString("auth_status");

            if (authStatus.contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
                isAccepted = true;
                isRejected = false;
            } else {
                isAccepted = false;
                break;
            }
        }
        for (int i = 0; i < issueAuth.length(); i++) {
            JSONObject auth = issueAuth.getJSONObject(i);
            String authStatus = auth.getString("auth_status");

            if (authStatus.contentEquals(Constant.AUTHORIZATION_STATUS_REJECTED)) {
                isRejected = true;
                isAccepted = false;
            } else {
                isRejected = false;
                break;
            }
        }


        keyIssue = obj.getString("key_issue");
        issuedDate = obj.getString("issued_on");
        issuedDate = issuedDate.contains("yang")
                ? issuedDate.replace("yang ", "")
                : issuedDate;

        if (obj.getBoolean("issue_authorization_complete")) {
            finishDate = obj.getString("issue_authorization_complete_on");
            finishDate = finishDate.contains("yang")
                    ? finishDate.replace("yang ", "")
                    : finishDate;
        }

        // Special operation according to issue KEY_ISSUE
        Log.e(TAG, "proceed: selectiion of key_issue ...");
        Map<String, Object> keyIssueValue = new HashMap<>();
        String keyIssue = obj.getString("key_issue");
        if ( /////////////////////////////////////////////////////// OUTCOME ///////////////////////////////////////////////////////
                keyIssue.contentEquals(Constant.KEY_OUTCOME_CENTRALIZE)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_PAYCHECK)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_PENGADAAN)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_RAPAT_SIDANG_KONVEN)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_DIAKONIA_BESASISWA)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_PEMBEKALAN_PELATIHAN)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_OTHER)
                        || keyIssue.contentEquals(Constant.KEY_OUTCOME_OTHER_NO_ACCOUNT)) {

            keyIssueValue.put("financial_description", obj.getString("financial_description"));
            keyIssueValue.put("financial_type", obj.getString("financial_type"));
            keyIssueValue.put("financial_account_number", obj.getString("financial_account_number"));
            keyIssueValue.put("financial_amount", obj.getString("financial_amount"));

            if (!keyIssueValue.get("financial_description").toString().isEmpty()) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(keyIssueValue.get("financial_description").toString());
            }
        } else if ( /////////////////////////////////////////////////////// INCOME ///////////////////////////////////////////////////////
                keyIssue.contentEquals(Constant.KEY_INCOME_PERSEMBAHAN_IBADAH)
                        || keyIssue.contentEquals(Constant.KEY_INCOME_SAMPUL_SYUKUR)
                        || keyIssue.contentEquals(Constant.KEY_INCOME_LAINNYA)
                        || keyIssue.contentEquals(Constant.KEY_INCOME_LAINNYA_NO_ACCOUNT)) {

            keyIssueValue.put("financial_description", obj.getString("financial_description"));
            keyIssueValue.put("financial_type", obj.getString("financial_type"));
            keyIssueValue.put("financial_account_number", obj.getString("financial_account_number"));
            keyIssueValue.put("financial_amount", obj.getString("financial_amount"));

            if (!keyIssueValue.get("financial_description").toString().isEmpty()) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(keyIssueValue.get("financial_description").toString());
            }
        } else if (/////////////////////////////////////////////////////// PAPERS ///////////////////////////////////////////////////////
                keyIssue.contentEquals(Constant.KEY_PAPERS_VALIDATE_MEMBERS)
                        || keyIssue.contentEquals(Constant.KEY_PAPERS_CREDENTIAL)
                        || keyIssue.contentEquals(Constant.KEY_PAPERS_BAPTIZE)
                        || keyIssue.contentEquals(Constant.KEY_PAPERS_SIDI)
                        || keyIssue.contentEquals(Constant.KEY_PAPERS_MARRIED)) {

            keyIssueValue.put("letter_id", obj.getString("letter_id"));
            keyIssueValue.put("letter_entry_number", obj.getString("letter_entry_number"));
            keyIssueValue.put("letter_type", obj.getString("letter_type"));
            keyIssueValue.put("letter_link_printable", obj.getString("letter_link_printable"));


            if (isAccepted) {
                tvLetterNumberEntry.setVisibility(View.VISIBLE);
                tvLetterNumberEntry.setText(((String) keyIssueValue.get("letter_entry_number")));
                String printable = ((String) keyIssueValue.get("letter_link_printable"));
                if (printable != null && !printable.isEmpty()) {
                    cvPrintable.setVisibility(View.VISIBLE);
                    cvPrintable.setOnClickListener(view -> {
                        Intent i = new Intent(IssueDetail.this, WebViewActivity.class);
                        i.putExtra(WebViewActivity.KEY_INTENT_TITLE, "link to printable");
                        i.putExtra(WebViewActivity.KEY_INTENT_DESTINATION_URL, "trello.com");
                        startActivity(i);
                    });
                }
            }

        } else if ( /////////////////////////////////////////////////////// SERVICE ///////////////////////////////////////////////////////
                keyIssue.contentEquals(Constant.KEY_SERVICE_KOLOM)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_BIPRA)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_HUT)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_PEMAKAMAN)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_PERINGATAN)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_KELUARGA)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_HARI_RAYA)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_SPECIAL)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_LAIN)
                        || keyIssue.contentEquals(Constant.KEY_SERVICE_SPECIAL_IBADAH_MINGGU)) {

            keyIssueValue.put("service_id", obj.getString("service_id"));
            keyIssueValue.put("service_date", obj.getString("service_date"));
            keyIssueValue.put("service_time", obj.getString("service_time"));
            keyIssueValue.put("service_place", obj.getString("service_place"));
            keyIssueValue.put("service_note", obj.getString("service_note"));
            keyIssueValue.put("service_entry_id", obj.getString("service_entry_id"));
            keyIssueValue.put("service_description", obj.getString("service_description"));

            llServiceEntryId.setVisibility(View.VISIBLE);
            tvServiceEntryId.setText(obj.getString("service_entry_id"));

            if (!obj.getString("service_description").isEmpty()) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(obj.getString("service_description"));
            }
        }

        JSONObject issuedByMemberObj = obj.getJSONObject("issued_by_member");
        issuedByMember = new HashMap<>();
        issuedByMember.put("name", issuedByMemberObj.getString("name"));
        issuedByMember.put("church_position", issuedByMemberObj.getString("church_position"));
        issuedByMember.put("column", issuedByMemberObj.getString("column"));


        JSONArray issuedMembers = obj.getJSONArray("issued_members");
        names = new ArrayList<>();
        for (int i = 0; i < issuedMembers.length(); i++) {
            JSONObject member = issuedMembers.getJSONObject(i);
            names.add(new IssueDetailModelHelper(
                            member.getInt("member_id"),
                            member.getString("name"),
                            member.getString("date_of_birth"),
                            member.getString("column"),
                            member.getString("church_position"),
                            member.getString("nomor_surat_baptis"),
                            member.getString("nomor_surat_sidi"),
                            member.getString("nomor_surat_nikah")
                    )
            );
        }
        insertCategory(keyIssue);


        setupHeaderCardView();
        setupIssuedMembers();
        setupNotations();
        setupIssuedBy();
//        setupButtons();

        if (llBody.getVisibility() != View.VISIBLE)
            llBody.setVisibility(View.VISIBLE);
//        if (llButtons.getVisibility() != View.VISIBLE)
//            llButtons.setVisibility(View.VISIBLE);
        if (progress.getVisibility() == View.VISIBLE)
            progress.setVisibility(View.INVISIBLE);

    }


    private void setupHeaderCardView() {


        tvKeyIssue.setText(keyIssue);
        tvIssuedDate.setText(issuedDate);

        if (!isAccepted && !isRejected) {

            tvLetterNumberEntry.setVisibility(View.GONE);
            tvKeyIssue.setPadding(0, 20, 0, 0);

            cvAcceptReject.setVisibility(View.GONE);
        } else if (isAccepted && !isRejected) {

            Constant.toggleViewVisibility(cvAcceptReject);
            tvAcceptRejectText.setText("DITERIMA");
            tvAcceptRejectDate.setText(finishDate);


        } else if (!isAccepted && isRejected) {


            tvLetterNumberEntry.setVisibility(View.GONE);
            tvKeyIssue.setPadding(0, 20, 0, 0);

            Constant.toggleViewVisibility(cvAcceptReject);
            tvAcceptRejectText.setText("DITOLAK");
            tvAcceptRejectDate.setText(finishDate);

        }
    }

    private void setupIssuedBy() {

        TextView tvName = findViewById(R.id.textView_issuedByName);
        TextView tvPosition = findViewById(R.id.textView_issuedByPosition);
        TextView tvColumn = findViewById(R.id.textView_issuedByColumn);

        tvName.setText(issuedByMember.get("name"));
        tvColumn.setText(issuedByMember.get("column"));
        tvPosition.setText(issuedByMember.get("church_position"));
    }

    private void setupNotations() {

        ViewGroup parent = llNotationPlaceHolder;
        for (int i = 0; i < notations.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.recycler_item_notation, parent, false);
            TextView tvPos = v.findViewById(R.id.textView_position);
            TextView tvDate = v.findViewById(R.id.textView_date);
            tvPos.setText(notations.get(i).getPosition());
            String date = notations.get(i).getConfirmedDate();
            date = date.contains("yang") ? date.replace("yang ", "") : date;
            String str = notations.get(i).getAuthStatus() + " " + date;
            tvDate.setText(str);
            llNotationPlaceHolder.addView(v);
        }

        if (notations.isEmpty()) {
            llNotationPlaceHolder.setVisibility(View.GONE);
        } else {
            if (llNotationPlaceHolder.getVisibility() == View.GONE)
                llNotationPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    private void setupIssuedMembers() {

        ArrayList<View> vh = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            IssueDetailModelHelper m = names.get(i);

            View v = getLayoutInflater().inflate(R.layout.recycler_item_adding, llNames, false);

            TextView tvName = v.findViewById(R.id.textView_name);
            TextView tvDOB = v.findViewById(R.id.textView_birthDate);
            TextView tvColumn = v.findViewById(R.id.textView_kolom);
            TextView tvCategory = v.findViewById(R.id.textView_category);
            CardView cvBaptize = v.findViewById(R.id.cardView_baptis);
            CardView cvSidi = v.findViewById(R.id.cardView_sidi);
            CardView cvMarried = v.findViewById(R.id.cardView_nikah);

            tvName.setText(m.getName());
            tvDOB.setText(m.dateOfBirth);
            tvColumn.setText(m.getColumn());

            if (!m.getBaptizeLetter().isEmpty()) {
                cvBaptize.setVisibility(View.VISIBLE);
            }
            if (!m.getSidiLetter().isEmpty()) {
                cvSidi.setVisibility(View.VISIBLE);
            }
            if (!m.getMarriedLetter().isEmpty()) {
                cvMarried.setVisibility(View.VISIBLE);
            }
            if (!m.getCategory().isEmpty()) {
//                Log.e(TAG, "setupIssuedMembers: category = "+m.getCategory() );
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(m.getCategory());
            }
            vh.add(v);
        }

        for (View v : vh) {
            llNames.addView(v);
        }

    }

    private void insertCategory(String keyIssue) {
        if (!names.isEmpty()) {
            switch (keyIssue) {
                case Constant.KEY_PAPERS_BAPTIZE:
                    names.get(0).setCategory("Yang di baptis :");
                    names.get(1).setCategory("Ayah :");
                    names.get(2).setCategory("Ibu :");
                    names.get(3).setCategory("Saksi-saksi :");
                    break;
                case Constant.KEY_PAPERS_MARRIED:
                    names.get(0).setCategory("Suami :");
                    names.get(1).setCategory("Ayah Suami :");
                    names.get(2).setCategory("Ibu Suami :");
                    names.get(3).setCategory("Istri :");
                    names.get(4).setCategory("Ayah Istri :");
                    names.get(5).setCategory("Ibu Istri");
                    break;
                default:
                    names.get(0).setCategory("Atas Nama :");
            }
        }
    }

    private class IssueDetailModelHelper {
        private int id;
        private String name;
        private String dateOfBirth;
        private String column;
        private String church_position;
        private String baptizeLetter;
        private String sidiLetter;
        private String marriedLetter;
        private String category = "";

        public IssueDetailModelHelper(int id, String name, String dateOfBirth, String column, String church_position, String baptizeLetter, String sidiLetter, String marriedLetter) {
            this.id = id;
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.column = column;
            this.church_position = church_position;
            this.baptizeLetter = baptizeLetter;
            this.sidiLetter = sidiLetter;
            this.marriedLetter = marriedLetter;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public String getColumn() {
            return column;
        }

        public String getChurch_position() {
            return church_position;
        }

        public String getBaptizeLetter() {
            return baptizeLetter;
        }

        public String getSidiLetter() {
            return sidiLetter;
        }

        public String getMarriedLetter() {
            return marriedLetter;
        }


        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
