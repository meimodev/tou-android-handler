package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class PntSym_InputForm extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PntSym_InputForm";
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_churchName)
    TextView tvChurchName;
    @BindView(R.id.textView_column)
    TextView tvColumn;

    @BindView(R.id.layout_editMember)
    LinearLayout llEditMember;
    @BindView(R.id.editText_findName)
    EditText etFindName;

    @BindView(R.id.editText_firstName)
    CustomEditText etFirstName;
    @BindView(R.id.editText_middleName)
    CustomEditText etMiddleName;
    @BindView(R.id.editText_lastName)
    CustomEditText etLastName;

    @BindView(R.id.layout_degreeCardHolder)
    LinearLayout llDegreeHolder;
    @BindView(R.id.button_addDegree)
    Button btnAddDegree;

    @BindView(R.id.spinner_sex)
    MaterialSpinner spinnerSex;
    @BindView(R.id.editText_DOB)
    CustomEditText etDOB;

    @BindView(R.id.switch_baptize)
    Switch switchBaptize;
    @BindView(R.id.layout_baptize)
    LinearLayout llBaptize;
    @BindView(R.id.editText_baptizeLetter)
    CustomEditText etBaptizeLetterEntry;
    @BindView(R.id.switch_sidi)
    Switch switchSidi;
    @BindView(R.id.layout_sidi)
    LinearLayout llSidi;
    @BindView(R.id.editText_sidiLetter)
    CustomEditText etSidiLetterEntry;
    @BindView(R.id.switch_married)
    Switch switchMarried;
    @BindView(R.id.layout_married)
    LinearLayout llMarried;
    @BindView(R.id.editText_marriedLetter)
    CustomEditText etMarriedLetterEntry;

    @BindView(R.id.button_end)
    Button btnEnd;
    @BindView(R.id.button_cancel)
    Button btnCancel;

    public static final String ADD_MEMBER = "Tambah Anggota";
    public static final String EDIT_MEMBER = "Sunting Anggota";
    public static final String DELETE_MEMBER = "Hapus Anggota";

    public static final String DEGREE_PARAMETER_POSITION = "-pos:";
    public static final String DEGREE_PARAMETER_POSITION_FRONT = "-pos:f-";
    public static final String DEGREE_PARAMETER_POSITION_BACK = "-pos:b-";

    private String OPERATION_TYPE;
    //    private ArrayList<String> fullDegrees;
//    private ArrayList<String> titleDegrees;
    private ArrayList<View> degreeViewsHolder;
    private ArrayList<DegreeHolder> degreeHolders;

    private String firstName;
    private String middleName;
    private String lastName;
    private String degreePre = "", degreePost = "";
    private String dateOfBirth;
    private String selectedSex;
    private String letterBaptize, letterSidi, letterMarried;

    private Integer MEMBER_ID = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnt_sym_input_form);
        ButterKnife.bind(this);

        String churchName =
                SharedPrefManager.load(this, SharedPrefManager.KEY_CHURCH_NAME) + ", "
                        + SharedPrefManager.load(this, SharedPrefManager.KEY_CHURCH_VILLAGE);
        String column = SharedPrefManager.load(this, SharedPrefManager.KEY_MEMBER_COLUMN).toString();
        tvChurchName.setText(churchName);
        tvColumn.setText(column);
        etDOB.setAsDatePicker(getSupportFragmentManager());

        OPERATION_TYPE = getIntent().getStringExtra("type");
        if (OPERATION_TYPE.contentEquals(ADD_MEMBER)) setupForAdding();
        else if (OPERATION_TYPE.contentEquals(EDIT_MEMBER)) setupForEditing();
        else if (OPERATION_TYPE.contentEquals(DELETE_MEMBER)) setupForDeleting();

        btnCancel.setOnClickListener(view -> finish());
        btnEnd.setOnClickListener(this);

    }

    private void setupForAdding() {
        tvTitle.setText(ADD_MEMBER);
        fetchDegrees();
        btnEnd.setText("TAMBAH");

    }

    private void setupForEditing() {
        tvTitle.setText(EDIT_MEMBER);
        fetchDegrees();
        //setup search EditText
        etFindName.clearFocus();

        etFindName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent i = new Intent(this, Adding.class);
                i.putExtra("OPERATION_TYPE", Adding.OPERATION_ADD_NAME_REGISTERED_ONLY);
                startActivityForResult(i, Adding.REQUEST_CODE_PERSONAL_NAME);
                etFindName.clearFocus();
            }
        });

        llEditMember.setVisibility(View.VISIBLE);
        btnEnd.setText("UBAH");

    }

    private void setupForDeleting() {
        tvTitle.setText(DELETE_MEMBER);
        fetchDegrees();
        //setup search EditText

        etFindName.clearFocus();

        etFindName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent i = new Intent(this, Adding.class);
                i.putExtra("OPERATION_TYPE", Adding.OPERATION_ADD_NAME_REGISTERED_ONLY);
                startActivityForResult(i, Adding.REQUEST_CODE_PERSONAL_NAME);
                etFindName.clearFocus();
            }
        });

        llEditMember.setVisibility(View.VISIBLE);

        etFirstName.setEnabled(false);
        etMiddleName.setEnabled(false);
        etLastName.setEnabled(false);
        etDOB.setEnabled(false);
        btnAddDegree.setEnabled(false);
        spinnerSex.setEnabled(false);
        switchBaptize.setEnabled(false);
        switchSidi.setEnabled(false);
        switchMarried.setEnabled(false);

        btnEnd.setText("HAPUS");

        llBaptize.setClickable(false);
        llBaptize.setEnabled(false);
        llSidi.setClickable(false);
        llSidi.setEnabled(false);
        llMarried.setClickable(false);
        llMarried.setEnabled(false);

        etBaptizeLetterEntry.setClickable(false);
        etBaptizeLetterEntry.setEnabled(false);
        etSidiLetterEntry.setClickable(false);
        etSidiLetterEntry.setEnabled(false);
        etMarriedLetterEntry.setClickable(false);
        etMarriedLetterEntry.setEnabled(false);

    }

    private void fetchDegrees() {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().getAcademicDegrees());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) {

//                fullDegrees = new ArrayList<>();
//                titleDegrees = new ArrayList<>();
                degreeHolders = new ArrayList<>();
                try {
                    JSONArray data = res.getDataArray();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = data.getJSONObject(i);
                        degreeHolders.add(new DegreeHolder(
                                o.getInt("id"),
                                o.getString("full_title"),
                                o.getString("abv"),
                                o.getString("position")
                        ));
                    }

                    setupViews();
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: JSON ERROR " + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onRetry() {
                req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().getAcademicDegrees());
            }
        });
    }

    private void setupViews() {
        //setup btn add degree, spinner sex, membership switches

//        setup Spinner Sex
        ArrayList<String> sex = new ArrayList<>();
        sex.add("Pilih Jenis Kelamin : ");
        sex.add("Laki-laki");
        sex.add("Perempuan");
        spinnerSex.setItems(sex);
        spinnerSex.setOnItemSelectedListener((view, position, id, item) -> {
            if (sex.get(0).contains("Pilih")) {
                sex.remove(0);
                spinnerSex.setItems(Objects.requireNonNull(sex));
                spinnerSex.setSelectedIndex(position - 1);
            }
        });

//        setup add degreeButton
        degreeViewsHolder = new ArrayList<>();
        ArrayList<String> filteredDegrees = new ArrayList<>();
        for (DegreeHolder d : degreeHolders) {
            filteredDegrees.add(d.getFullTitle());
        }

        btnAddDegree.setOnClickListener(view -> {
            LinearLayout llHolder = findViewById(R.id.layout_degreeCardHolder);
            View v = getLayoutInflater().inflate(R.layout.resource_add_member_data, llHolder, false);
            Button btnDelete = v.findViewById(R.id.button_delete);
            btnDelete.setOnClickListener(view1 -> {
                llHolder.removeView(v);
                degreeViewsHolder.remove(v);
            });

            MaterialSpinner spinnerDegree = v.findViewById(R.id.spinner_degree);
            ArrayList<String> filteredDegreesWithIndicator = new ArrayList<>();
            filteredDegreesWithIndicator.add("Pilih Gelar Akademik :");
            filteredDegreesWithIndicator.addAll(filteredDegrees);
            spinnerDegree.setOnItemSelectedListener((view1, position, id, item) -> {
                if (filteredDegreesWithIndicator.get(0).contains("Pilih")) {
                    filteredDegreesWithIndicator.remove(0);
                    spinnerDegree.setItems(Objects.requireNonNull(filteredDegreesWithIndicator));
                    spinnerDegree.setSelectedIndex(position - 1);
                }
            });
            spinnerDegree.setItems(filteredDegreesWithIndicator);
            llHolder.addView(v);
            degreeViewsHolder.add(v);
        });

//        setup switches
        llBaptize.setOnClickListener(view -> switchBaptize.toggle());
        llSidi.setOnClickListener(view -> switchSidi.toggle());
        llMarried.setOnClickListener(view -> switchMarried.toggle());
//        TextWatcher mask = new Validator.MaskWatcher("--##/###.###/##/####");
        switchBaptize.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                llBaptize.setVisibility(View.VISIBLE);
                etBaptizeLetterEntry.setText("");
//                etBaptizeLetterEntry.addTextChangedListener(mask);
            } else {
                llBaptize.setVisibility(View.GONE);
//                etBaptizeLetterEntry.removeTextChangedListener(mask);
            }
        });
        switchSidi.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                llSidi.setVisibility(View.VISIBLE);
                etSidiLetterEntry.setText("");
//                etSidiLetterEntry.addTextChangedListener(mask);

            } else {
                llSidi.setVisibility(View.GONE);
//                etSidiLetterEntry.removeTextChangedListener(mask);
            }
        });
        switchMarried.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                llMarried.setVisibility(View.VISIBLE);
                etMarriedLetterEntry.setText("");
//                etMarriedLetterEntry.addTextChangedListener(mask);
            } else {
                llMarried.setVisibility(View.GONE);
//                etMarriedLetterEntry.removeTextChangedListener(mask);
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == btnEnd) {

            // read & set values return from searched member data (if edit or delete member)

            // validate & read input
            Validator v = new Validator();
            if (etFirstName.getVisibility() == View.VISIBLE && v.validateEditText_isEmpty(etFirstName)) {
                v.displayErrorMessage(this, "Nama Depan tidak boleh kosong");
                return;
            }
            if (etLastName.getVisibility() == View.VISIBLE && v.validateEditText_isEmpty(etLastName)) {
                v.displayErrorMessage(this, "Nama Belakang tidak boleh kosong");
                return;
            }

            if (!degreeViewsHolder.isEmpty()) {
                for (View viewHolder : degreeViewsHolder) {
                    if (v.validateSpinner_isItemNotSelected(findViewById(R.id.spinner_degree))) {
                        v.displayErrorMessage(this, "Silahkan Pilih gelar akademik");
                        return;
                    }
                }
            }

            if (etDOB.getVisibility() == View.VISIBLE && v.validateEditText_isEmpty(etDOB)) {
                v.displayErrorMessage(this, "Tanggal Lahir tidak boleh kosong");
                return;
            }
            if (v.validateSpinner_isItemNotSelected(spinnerSex)) {
                v.displayErrorMessage(this, "Silahkan Pilih jenis kelamin");
                return;
            }
            if (switchBaptize.isChecked() && v.validateEditText_isInvalidLetterEntry(etBaptizeLetterEntry)) {
                v.displayErrorMessage(this, "Nomor Surat Baptis belum valid");
                return;
            }
            if (switchSidi.isChecked() && v.validateEditText_isInvalidLetterEntry(etSidiLetterEntry)) {
                v.displayErrorMessage(this, "Nomor Surat Sidi belum valid");
                return;
            }
            if (switchMarried.isChecked() && v.validateEditText_isInvalidLetterEntry(etMarriedLetterEntry)) {
                v.displayErrorMessage(this, "Nomor Surat Nikah belum valid");
                return;
            }

            // == LOGGING inputs data ==
            Log.e(TAG, "====================================== INPUT DATA ======================================");
            Log.e(TAG, "--------------------------------------- ISSUED BY --------------------------------------");
            Log.e(TAG, "Issued By: " + SharedPrefManager.load(this, SharedPrefManager.KEY_USER_FULL_NAME).toString());
            Log.e(TAG, "Church Position: "
                    + SharedPrefManager.load(this, SharedPrefManager.KEY_CHURCH_POSITION).toString()
                    + ", "
                    + SharedPrefManager.load(this, SharedPrefManager.KEY_MEMBER_COLUMN
            ));
            Log.e(TAG, "----------------------------------------------------------------------------------------");
            Log.e(TAG, "Operation Type: " + OPERATION_TYPE);
            Log.e(TAG, "Church Name: " + tvChurchName.getText());
            Log.e(TAG, "Column Name: " + tvColumn.getText());
            firstName = etFirstName.getText().toString();
            Log.e(TAG, "First Name: " + firstName);
            middleName = etMiddleName.getText().toString();
            Log.e(TAG, "Middle Name: " + (middleName.length() < 2 ? "" : middleName));
            lastName = etLastName.getText().toString();
            Log.e(TAG, "Last Name: " + lastName);
            if (!degreeViewsHolder.isEmpty()) {
                for (View h : degreeViewsHolder) {
                    MaterialSpinner s = h.findViewById(R.id.spinner_degree);

                    DegreeHolder selected = degreeHolders.get(s.getSelectedIndex());
                    if (selected.position.contentEquals("front"))
                        degreePre = degreePre.concat(selected.getAbv()).concat(" ");
                    else
                        degreePost = degreePost.concat(selected.getAbv()).concat(", ");

                    Log.e(TAG, "Degree " + degreeViewsHolder.size() + " : " + selected.getAbv());
                }
            } else Log.e(TAG, "Academic Degree: none");
            dateOfBirth = etDOB.getText().toString();
            Log.e(TAG, "DOB: " + dateOfBirth);
            selectedSex = spinnerSex.getSelectedIndex() == 0 ? "MALE" : "FEMALE";
            Log.e(TAG, "Sex: " + selectedSex);

            if (etBaptizeLetterEntry.getVisibility() == View.VISIBLE) {
                Log.e(TAG, "Baptize Letter Entry: " + etBaptizeLetterEntry.getText());
                letterBaptize = etBaptizeLetterEntry.getText().toString();
            } else {
                Log.e(TAG, "Baptize Letter Entry: none");
                letterBaptize = "";
            }

            if (etSidiLetterEntry.getVisibility() == View.VISIBLE) {
                Log.e(TAG, "Sidi Letter Entry: " + etSidiLetterEntry.getText());
                letterSidi = etSidiLetterEntry.getText().toString();
            } else {
                Log.e(TAG, "Sidi Letter Entry: none");
                letterSidi = "";
            }

            if (etMarriedLetterEntry.getVisibility() == View.VISIBLE) {
                Log.e(TAG, "Married Letter Entry: " + etMarriedLetterEntry.getText());
                letterMarried = etMarriedLetterEntry.getText().toString();
            } else {
                Log.e(TAG, "Married Letter Entry: none");
                letterMarried = "";
            }

            Log.e(TAG, "========================================================================================");

            IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
            switch (OPERATION_TYPE) {
                case ADD_MEMBER:
                    // setup end button to ADD BUTTON
                    req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().setMemberUserData(
                            ((int) SharedPrefManager.load(this, SharedPrefManager.KEY_MEMBER_ID)),
                            firstName,
                            middleName,
                            lastName,
                            degreePre,
                            degreePost,
                            dateOfBirth,
                            selectedSex,
                            "",
                            "",
                            letterBaptize,
                            letterSidi,
                            letterMarried));

                    req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                        @Override
                        public void onTry() {

                        }

                        @Override
                        public void onSuccess(APIWrapper res, String message) {
                            AlertDialog.Builder b = new AlertDialog.Builder(PntSym_InputForm.this);
                            b.setTitle("OK! Operasi berhasil");
                            b.setMessage("" + firstName + " " + lastName + " berhasil di tambahkan dalam kolom ");
                            b.setPositiveButton("OK", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                finish();
                            });
                            b.setCancelable(false);
                            b.create().show();
                        }

                        @Override
                        public void onRetry() {
                            req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().setMemberUserData(
                                    ((int) SharedPrefManager.load(PntSym_InputForm.this, SharedPrefManager.KEY_MEMBER_ID)),
                                    firstName,
                                    middleName,
                                    lastName,
                                    degreePre,
                                    degreePost,
                                    dateOfBirth,
                                    selectedSex,
                                    "",
                                    "",
                                    letterBaptize,
                                    letterSidi,
                                    letterMarried));
                        }
                    });
                    break;
                case EDIT_MEMBER:
                    // setup end button to EDIT BUTTON
                    if (MEMBER_ID != null) {

                        Call call = RetrofitClient.getInstance(null).getApiServices().editMember(
                                MEMBER_ID,
                                ((int) SharedPrefManager.load(PntSym_InputForm.this, SharedPrefManager.KEY_MEMBER_ID)),
                                firstName,
                                middleName,
                                lastName,
                                degreePre,
                                degreePost,
                                dateOfBirth,
                                selectedSex,
                                "",
                                "",
                                letterBaptize,
                                letterSidi,
                                letterMarried
                        );

                        req.issueRequestResponse(call);
                        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                            @Override
                            public void onTry() {

                            }

                            @Override
                            public void onSuccess(APIWrapper res, String message) {
                                AlertDialog.Builder b = new AlertDialog.Builder(PntSym_InputForm.this);
                                b.setTitle("OK! Operasi berhasil");
                                b.setMessage("Data anggota bernama" + firstName + " " + lastName + " berhasil di ubah ");
                                b.setPositiveButton("OK", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    finish();
                                });
                                b.setCancelable(false);
                                b.create().show();
                            }

                            @Override
                            public void onRetry() {

                            }
                        });
                    }
                    break;
                case DELETE_MEMBER:
                    // setup end button to DELETE BUTTON

                    if (MEMBER_ID != null) {

                        Call call = RetrofitClient.getInstance(null).getApiServices().deleteMember(
                                MEMBER_ID
                        );

                        req.issueRequestResponse(call);
                        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                            @Override
                            public void onTry() {

                            }

                            @Override
                            public void onSuccess(APIWrapper res, String message) {
                                AlertDialog.Builder b = new AlertDialog.Builder(PntSym_InputForm.this);
                                b.setTitle("OK! Operasi berhasil");
                                b.setMessage("Data anggota bernama " + firstName + " " + lastName + " berhasil di hapus ");
                                b.setPositiveButton("OK", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    finish();
                                });
                                b.setCancelable(false);
                                b.create().show();
                            }

                            @Override
                            public void onRetry() {

                            }
                        });
                    }

                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            int memberId = data.getIntExtra("model.id", 0);
            IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
            req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().getMemberUserData(memberId));
            req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                @Override
                public void onTry() {

                }

                @Override
                public void onSuccess(APIWrapper res, String message) {

                    JSONObject data = res.getData();
                    try {
                        MEMBER_ID = memberId;
                        etFindName.setText(data.getString("first_name"));
                        etFirstName.setText(data.getString("first_name"));
                        etMiddleName.setText(data.getString("middle_name"));
                        etLastName.setText(data.getString("last_name"));
                        handleEditDegree(data.getString("degree_pre"), data.getString("degree_post"));
                        etDOB.setText(data.getString("date_of_birth"));

                        if (data.getString("sex").contentEquals("MALE"))
                            spinnerSex.setSelectedIndex(1);
                        else spinnerSex.setSelectedIndex(2);

                        String baptizeLetterEntry = data.getString("baptize_letter_entry"),
                                sidiLetterEntry = data.getString("sidi_letter_entry"),
                                marriedLetterEntry = data.getString("married_letter_entry");

                        if (!baptizeLetterEntry.isEmpty()) {
                            switchBaptize.setChecked(true);
                            etBaptizeLetterEntry.setText(baptizeLetterEntry);
                        } else {
                            switchBaptize.setChecked(false);
                            etBaptizeLetterEntry.setText("");
                        }
                        if (!sidiLetterEntry.isEmpty()) {
                            switchSidi.setChecked(true);
                            etSidiLetterEntry.setText(sidiLetterEntry);
                        } else {
                            switchSidi.setChecked(false);
                            etSidiLetterEntry.setText("");
                        }
                        if (!marriedLetterEntry.isEmpty()) {
                            switchMarried.setChecked(true);
                            etMarriedLetterEntry.setText(marriedLetterEntry);
                        } else {
                            switchMarried.setChecked(false);
                            etMarriedLetterEntry.setText("");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "onSuccess: JSON ERROR = " + e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void onRetry() {
                    req.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().getMemberUserData(memberId));
                }
            });
        }
    }

    //////////////////////////////////// HELPERS ////////////////////////////////////
    private class DegreeHolder {

        private int id;
        private String fullTitle, abv, position;

        public DegreeHolder(int id, String fullTitle, String abv, String position) {
            this.id = id;
            this.fullTitle = fullTitle;
            this.abv = abv;
            this.position = position;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFullTitle() {
            return fullTitle;
        }

        public void setFullTitle(String fullTitle) {
            this.fullTitle = fullTitle;
        }

        public String getAbv() {
            return abv;
        }

        public void setAbv(String abv) {
            this.abv = abv;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }

    private void handleEditDegree(String dPre, String dPost) {
        if (llDegreeHolder != null) llDegreeHolder.removeAllViews();
        if (degreeViewsHolder != null) degreeViewsHolder.clear();
        ArrayList<String> holderDegree = new ArrayList<>();
        if (!dPre.isEmpty()) {
            if (dPre.contains(" ")) {
                //plural
                String[] dPrePlural = dPre.split(" ");

                //add splited degree to holderDegree
                for (String d : dPrePlural) {
                    holderDegree.add(d.contains(" ") ? d.trim() : d);
                }
            } else {
                holderDegree.add(dPre);
            }

        }

        if (!dPost.isEmpty()) {
            dPost = dPost.replace(" ", "");
            if (dPost.contains(",")) {
                //plural
                String[] dPostPlural = dPost.trim().split(",");

                //add splited degree to holderDegree
                for (String d : dPostPlural) {
                    holderDegree.add(d.contains(",") ? d.replace(",", "") : d);
                }
            } else {
                holderDegree.add(dPost);
            }


        }

        for (int i = 0; i < holderDegree.size(); i++) {
            btnAddDegree.performClick();
            MaterialSpinner spinner = degreeViewsHolder.get(i).findViewById(R.id.spinner_degree);

            if (OPERATION_TYPE.contentEquals(DELETE_MEMBER)) {
                spinner.setEnabled(false);
                View del = degreeViewsHolder.get(i).findViewById(R.id.button_delete);
                del.setEnabled(false);
            }

            for (int j = 0; j < spinner.getItems().size(); j++) {
                String str = spinner.getItems().get(j).toString();
                if (str.contains("(") || str.contains(")")) {
                    String strAbv = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                    if (strAbv.equals(holderDegree.get(i))) {
                        spinner.setSelectedIndex(j);
                        break;
                    }
                }
            }

        }

    }
}
