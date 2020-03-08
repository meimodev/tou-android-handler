package com.meimodev.sitouhandler.IssueDetail;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.hmomeni.progresscircula.ProgressCircula;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.WebViewActivity;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
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

import static com.meimodev.sitouhandler.Constant.*;

public class IssueDetail extends AppCompatActivity {

    public String TAG = "IssueDetail : ";

    private long downloadID;

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

    @BindView(R.id.cardView_printable)
    CardView cvPrintable;
    @BindView(R.id.button_download)
    CardView cvButtonDownload;


    @BindView(R.id.textView_description)
    TextView tvDescription;
    @BindView(R.id.cardView_description)
    CardView cvDescription;

    @BindView(R.id.layout_buttons)
    LinearLayout llButtons;
    @BindView(R.id.button_accept)
    Button btnAccept;
    @BindView(R.id.button_reject)
    Button btnReject;

    private String keyIssue;
    private String issuedDate;
    private String finishDate;
    //    private String rejectedDate;
    private String letterNumber;
    private ArrayList<IssueDetailModelHelper> names;
    private ArrayList<IssueDetailNotation_NotationModel> notations;
    private Map<String, String> issuedByMember;
    private String issueAuthStatus;

    private CardView cvLoading;

    @BindView(R.id.imageView_download)
    ImageView ivDownload;
    @BindView(R.id.textView_link)
    TextView tvLink;
    @BindView(R.id.text_download)
    TextView tvTextDownload;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        ButterKnife.bind(this);
        changeStatusColor(this, R.color.colorPrimary);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        llButtons.setVisibility(View.GONE);

        fetchData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();
    }

    private void fetchData() {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().getIssue(getIntent().getIntExtra("ISSUE_ID", 0));

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

        JSONObject obj = res.getData();
        JSONObject issueAuthorization = obj.getJSONObject("issue_authorization");

        notations = new ArrayList<>();

        JSONArray issueAuth = issueAuthorization.getJSONArray("auth");
        for (int i = 0; i < issueAuth.length(); i++) {
            JSONObject auth = issueAuth.getJSONObject(i);
            String authStatus = auth.getString("status");
            JSONArray authChurchPositions = auth.getJSONArray("church_position");
            String authOn = auth.getString("on");

            notations.add(new IssueDetailNotation_NotationModel(
                    authChurchPositions, authOn, authStatus)
            );

        }

        keyIssue = obj.getString("key_issue");
        issuedDate = obj.getString("issued_on");
        issueAuthStatus = issueAuthorization.getString("status");

        if (issueAuthorization.getBoolean("is_complete")) {
            finishDate = issueAuthorization.getString("complete_on");
        }
        else {
            finishDate = "";
        }

        // Special operation according to issue KEY_ISSUE
        Map<String, Object> keyIssueValue = new HashMap<>();
        if (isIssueOutcome(keyIssue)) {

            keyIssueValue.put("financial_description", obj.getString("financial_description"));
            keyIssueValue.put("financial_type", obj.getString("financial_type"));
            keyIssueValue.put("financial_account_number", obj.getString("financial_account_number"));
            keyIssueValue.put("financial_amount", obj.getString("financial_amount"));

            if (!keyIssueValue.get("financial_description").toString().isEmpty()) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(keyIssueValue.get("financial_description").toString());
            }
        }
        else if (isIssueIncome(keyIssue)) {

            keyIssueValue.put("financial_description", obj.getString("financial_description"));
            keyIssueValue.put("financial_type", obj.getString("financial_type"));
            keyIssueValue.put("financial_account_number", obj.getString("financial_account_number"));
            keyIssueValue.put("financial_amount", obj.getString("financial_amount"));

            if (!keyIssueValue.get("financial_description").toString().isEmpty()) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(keyIssueValue.get("financial_description").toString());
            }
        }
        else if (isIssuePaper(keyIssue)) {

            keyIssueValue.put("letter_id", obj.getString("letter_id"));
            keyIssueValue.put("letter_entry_number", obj.getString("letter_entry_number"));
            keyIssueValue.put("letter_type", obj.getString("letter_type"));
            keyIssueValue.put("letter_link_printable", obj.getString("letter_link_printable"));

            if (!issueAuthStatus.contentEquals(AUTHORIZATION_STATUS_ACCEPTED)) {
                tvLetterNumberEntry.setVisibility(View.VISIBLE);
                tvLetterNumberEntry.setText(((String) keyIssueValue.get("letter_entry_number")));
            }

        }
        else if (isIssueService(keyIssue)) {

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
        else if (keyIssue.contentEquals(KEY_OTHER_APPLY_MEMBER)) {

            if (!obj.isNull("description")) {
                cvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(obj.getString("description"));
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

        if (!obj.isNull("printable_key")) {
            String printableKey = obj.getString("printable_key");
            cvPrintable.setVisibility(View.VISIBLE);
            cvButtonDownload.setOnClickListener(view -> downloadFile(printableKey));
            TextView tvLink = cvPrintable.findViewById(R.id.textView_link);
            String url = ROOT_URL_PRINTABLE + printableKey;
            tvLink.setText(url);
        }

        setupHeaderCardView();
        setupIssuedMembers();
        setupNotations();
        setupIssuedBy();
        setupAcceptRejectButtons();

    }

    private void setupHeaderCardView() {

        tvKeyIssue.setText(keyIssue);
        tvIssuedDate.setText(issuedDate);

        if (issueAuthStatus.contentEquals(AUTHORIZATION_STATUS_UNCONFIRMED)) {

            tvLetterNumberEntry.setVisibility(View.GONE);
            tvKeyIssue.setPadding(0, 20, 0, 0);

            cvAcceptReject.setVisibility(View.GONE);
        }
        else if (issueAuthStatus.contentEquals(AUTHORIZATION_STATUS_ACCEPTED)) {

            cvAcceptReject.setVisibility(View.VISIBLE);
            tvAcceptRejectText.setText("DITERIMA");
            tvAcceptRejectDate.setText(finishDate);

        }
        else if (issueAuthStatus.contentEquals(AUTHORIZATION_STATUS_REJECTED)) {

            tvLetterNumberEntry.setVisibility(View.GONE);
            tvKeyIssue.setPadding(0, 20, 0, 0);

            cvAcceptReject.setVisibility(View.VISIBLE);
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

    private void setupNotations() throws JSONException {

        ViewGroup parent = llNotationPlaceHolder;
        for (int i = 0; i < notations.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.recycler_item_notation, parent, false);
            TextView tvPos = v.findViewById(R.id.textView_position);
            TextView tvDate = v.findViewById(R.id.textView_date);

            IssueDetailNotation_NotationModel model = notations.get(i);

            StringBuilder positions = new StringBuilder();
            for (int j = 0; j < model.getPositions().length(); j++) {
                String p = ((String) model.getPositions().get(j));
                positions = j == model.getPositions().length() - 1 ? positions.append(p) : positions.append(p).append("\n");
            }

            tvPos.setText(positions.toString());
            String date = model.getConfirmedDate();
            date = date.contains("yang") ? date.replace("yang ", "") : date;
            String str = model.getAuthStatus() + " " + date;
            tvDate.setText(str);
            llNotationPlaceHolder.addView(v);
        }

        if (notations.isEmpty()) {
            llNotationPlaceHolder.setVisibility(View.GONE);
        }
        else {
            if (llNotationPlaceHolder.getVisibility() == View.GONE) {
                llNotationPlaceHolder.setVisibility(View.VISIBLE);
            }
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

    public static final String KEY_ISSUE_DETAIL_CONFIRM_AUTH = "key_issue_detail_confirm_auth";

    private void setupAcceptRejectButtons() {
        int authStatusCode = getIntent().getIntExtra("AUTHORIZATION_STATUS_CODE", 99);
        int authId = getIntent().getIntExtra("AUTHORIZATION_ID", 0);
        if (authStatusCode != 99) {
            if (authStatusCode == AUTHORIZATION_STATUS_CODE_UNCONFIRMED) {
                llButtons.setVisibility(View.VISIBLE);
                btnAccept.setOnClickListener(v -> {
                    Bundle data = new Bundle();
                    data.putInt("AUTH_ID", authId);
                    data.putInt("AUTH_CODE", AUTHORIZATION_STATUS_CODE_ACCEPTED);

                    String title = "Konfirmasi Penerimaan";
                    String message = "Anda akan MENERIMA pengajuan ini, silahkan sentuh tombol 'OK' untuk konfirmasi bahwa pengajuan ini akan DITERIMA";
                    Intent i = new Intent(KEY_ISSUE_DETAIL_CONFIRM_AUTH);
                    i.putExtra("AUTH_ID", authId);
                    i.putExtra("AUTH_CODE", AUTHORIZATION_STATUS_CODE_ACCEPTED);
                    Constant.displayDialog(IssueDetail.this, title, message, false,
                            (dialog, which) -> {
                                sendBroadcast(i);
                                finish();
                            },
                            (dialog, which) -> dialog.dismiss());
                });
                btnReject.setOnClickListener(v -> {
                    String title = "Konfirmasi Penolakan";
                    String message = "Anda akan MENOLAK pengajuan ini, silahkan sentuh tombol 'OK' untuk konfirmasi bahwa pengajuan ini akan DITOLAK";
                    Intent i = new Intent(KEY_ISSUE_DETAIL_CONFIRM_AUTH);
                    i.putExtra("AUTH_ID", authId);
                    i.putExtra("AUTH_CODE", AUTHORIZATION_STATUS_CODE_REJECTED);
                    Constant.displayDialog(IssueDetail.this, title, message, false,
                            (dialog, which) -> {
                                sendBroadcast(i);
                                finish();
                            },
                            (dialog, which) -> dialog.dismiss());
                });
            }
        }
        else {
            Log.e(TAG, "setupAcceptRejectButtons: authStatusCode =" + authStatusCode);
        }
    }

    private void insertCategory(String keyIssue) {
        if (!names.isEmpty()) {
            switch (keyIssue) {
                case KEY_PAPERS_BAPTIZE:
                    names.get(0).setCategory("Yang di baptis :");
                    names.get(1).setCategory("Ayah :");
                    names.get(2).setCategory("Ibu :");
                    names.get(3).setCategory("Saksi-saksi :");
                    break;
                case KEY_PAPERS_MARRIED:
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

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                cvButtonDownload.setEnabled(true);
                cvButtonDownload.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

                tvTextDownload.setText("Lihat Dokumen");

                ivDownload.setImageDrawable(getDrawable(R.drawable.ic_description_24px));

                View.OnClickListener onClick = v -> startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

                cvButtonDownload.setOnClickListener(onClick);

                Snackbar.make(findViewById(android.R.id.content), keyIssue.toUpperCase().replace(" ", "_") + ".pdf", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Lihat File",
                        onClick
                ).show();
            }
        }
    };

    private void downloadFile(String printableKey) {
        cvButtonDownload.setEnabled(false);
        cvButtonDownload.setCardBackgroundColor(getResources().getColor(R.color.TextView_Label_Light));

        Snackbar.make(findViewById(android.R.id.content), "Downloading / Mengunduh ...", Snackbar.LENGTH_INDEFINITE).show();

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(ROOT_URL_PRINTABLE + printableKey));

        req.setTitle(keyIssue.toUpperCase().replace(" ", "_") + ".pdf")// Title of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = dm.enqueue(req);
    }

    private static class IssueDetailModelHelper {
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
