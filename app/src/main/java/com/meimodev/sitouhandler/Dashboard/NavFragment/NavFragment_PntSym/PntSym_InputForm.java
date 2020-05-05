/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_PntSym;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomDialog;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler.OnRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.ActivityPntSymInputFormBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;

import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_KELURAHAN;
import static com.meimodev.sitouhandler.Constant.KEY_CHURCH_NAME;
import static com.meimodev.sitouhandler.Constant.KEY_COLUMN_NAME_INDEX;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_CHURCH_POSITION;
import static com.meimodev.sitouhandler.Constant.KEY_MEMBER_ID;
import static com.meimodev.sitouhandler.Constant.KEY_USER_FULL_NAME;

public class PntSym_InputForm extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PntSym_InputForm";

    private ActivityPntSymInputFormBinding b;

    private String OPERATION_TYPE;
    public static final String ADD_MEMBER = "Tambah Anggota";
    public static final String EDIT_MEMBER = "Sunting Anggota";
    public static final String DELETE_MEMBER = "Hapus Anggota";

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
        b = ActivityPntSymInputFormBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Constant.changeStatusColor(this, R.color.colorPrimary);

        setupViews();

        OPERATION_TYPE = getIntent().getStringExtra("type");
        if (OPERATION_TYPE.contentEquals(ADD_MEMBER)) {
            setupForAdding();
        }
        else if (OPERATION_TYPE.contentEquals(EDIT_MEMBER)) {
            setupForEditing();
        }
        else if (OPERATION_TYPE.contentEquals(DELETE_MEMBER)) setupForDeleting();

        b.buttonCancel.setOnClickListener(view -> finish());
        b.buttonEnd.setOnClickListener(this);
    }

    private void setupForAdding() {
        b.textViewTitle.setText(ADD_MEMBER);
        b.buttonEnd.setText("TAMBAH");

    }

    private void setupForEditing() {
        b.textViewTitle.setText(EDIT_MEMBER);
        b.editTextFindName.clearFocus();
        b.editTextFindName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent i = new Intent(this, Adding.class);
                i.putExtra("OPERATION_TYPE", Adding.OPERATION_ADD_NAME_REGISTERED_ONLY);
                startActivityForResult(i, Adding.REQUEST_CODE_PERSONAL_NAME);
                this.b.editTextFindName.clearFocus();
            }
        });

        b.layoutEditMember.setVisibility(View.VISIBLE);
        b.buttonEnd.setText("UBAH");
        b.layoutName.setVisibility(View.GONE);
        b.layoutBody.setVisibility(View.GONE);
        b.layoutButtons.setVisibility(View.GONE);
    }

    private void setupForDeleting() {
        b.textViewTitle.setText(DELETE_MEMBER);

        b.editTextFindName.clearFocus();

        b.editTextFindName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent i = new Intent(this, Adding.class);
                i.putExtra("OPERATION_TYPE", Adding.OPERATION_ADD_NAME_REGISTERED_ONLY);
                startActivityForResult(i, Adding.REQUEST_CODE_PERSONAL_NAME);
                this.b.editTextFindName.clearFocus();
            }
        });

        b.layoutEditMember.setVisibility(View.VISIBLE);
        b.buttonEnd.setText("HAPUS");
        b.layoutName.setVisibility(View.GONE);
        b.layoutBody.setVisibility(View.GONE);
        b.layoutButtons.setVisibility(View.GONE);

    }

    private void setupViews() {
        //setup btn add degree, spinner sex, membership switches

//        setup Spinner Sex
        ArrayList<String> sex = new ArrayList<>();
        sex.add("Pilih Jenis Kelamin : ");
        sex.add("Laki-laki");
        sex.add("Perempuan");
        b.spinnerSex.setItems(sex);
        b.spinnerSex.setOnItemSelectedListener((view, position, id, item) -> {
            if (sex.get(0).contains("Pilih")) {
                sex.remove(0);
                b.spinnerSex.setItems(Objects.requireNonNull(sex));
                b.spinnerSex.setSelectedIndex(position - 1);
            }
        });

        String churchName =
                Guru.getString(KEY_CHURCH_NAME, null) + ", "
                        + Guru.getString(KEY_CHURCH_KELURAHAN, null);
        String column = Guru.getString(KEY_COLUMN_NAME_INDEX, null);
        b.textViewChurchName.setText(churchName);
        b.textViewColumn.setText(column);
        b.editTextDOB.setAsDatePicker(getSupportFragmentManager());

    }

    @Override
    public void onClick(View view) {
        if (view == b.buttonEnd) {
            // read & set values return from searched member data (if edit or delete member)

            // validate & read input
            Validator v = new Validator(this);
            if (v.validateName(b.textInputLayoutFirstName) != null) return;
            if (v.validateName(b.textInputLayoutLastName) != null) return;
            if (v.validateEmpty(b.textInputLayoutDob) != null) return;

            if (v.validateSpinner_isItemNotSelected(b.spinnerSex)) {
                v.displayErrorMessage(this, "Silahkan Pilih jenis kelamin");
                return;
            }

            // == LOGGING inputs data ==
            Log.e(TAG, "====================================== INPUT DATA ======================================");
            Log.e(TAG, "--------------------------------------- ISSUED BY --------------------------------------");
            Log.e(TAG, "Issued By: " + Guru.getString(KEY_USER_FULL_NAME, null));
            Log.e(TAG, "Church Position: "
                    + Guru.getString(KEY_MEMBER_CHURCH_POSITION, null)
                    + ", "
                    + Guru.getString(KEY_COLUMN_NAME_INDEX, null)
            );
            Log.e(TAG, "----------------------------------------------------------------------------------------");
            Log.e(TAG, "Operation Type: " + OPERATION_TYPE);
            Log.e(TAG, "Church Name: " + b.textViewChurchName.getText());
            Log.e(TAG, "Column Name: " + b.textViewColumn.getText());
            firstName = b.editTextFirstName.getText().toString();
            Log.e(TAG, "First Name: " + firstName);
            middleName = b.editTextMiddleName.getText().toString();
            Log.e(TAG, "Middle Name: " + (middleName.length() < 2 ? "" : middleName));
            lastName = b.editTextLastName.getText().toString();
            Log.e(TAG, "Last Name: " + lastName);
            dateOfBirth = b.editTextDOB.getText().toString();
            Log.e(TAG, "DOB: " + dateOfBirth);
            selectedSex = b.spinnerSex.getSelectedIndex() == 0 ? "MALE" : "FEMALE";
            Log.e(TAG, "Sex: " + selectedSex);

            letterBaptize = b.checkBoxBaptize.isChecked() ? "SB" : "";
            Log.e(TAG, "Baptize: " + b.checkBoxBaptize.isChecked() + " " + letterBaptize);
            letterSidi = b.checkBoxSidi.isChecked() ? "SS" : "";
            Log.e(TAG, "Sidi: " + b.checkBoxSidi.isChecked()+ " " + letterSidi);
            letterMarried = b.checkBoxMarried.isChecked() ? "SN" : "";
            Log.e(TAG, "Nikah: " + b.checkBoxMarried.isChecked()+ " " + letterMarried);
            Log.e(TAG, "========================================================================================");

            // Server Request
            IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
            Call call = null;
            req.setIntention(new Throwable());
            IssueRequestHandler.OnRequestHandler addingOnRequestHandler = null;
            switch (OPERATION_TYPE) {
                case ADD_MEMBER:
                    call = RetrofitClient.getInstance(null).getApiServices().addMemberUser(
                            Guru.getInt(KEY_MEMBER_ID, 0),
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
                            letterMarried,
                            false);
                    Call finalCall1 = call;
                    addingOnRequestHandler = new OnRequestHandler() {
                        @Override
                        public void onTry() {

                        }

                        @Override
                        public void onSuccess(APIWrapper res, String message) throws JSONException {
                            JSONObject data = res.getData();
                            if (data.isNull("similar_members")) {
                                Constant.displayDialog(
                                        PntSym_InputForm.this,
                                        "OK, Operasi berhasil",
                                        message,
                                        false,
                                        (dialog, which) -> {
                                        },
                                        null,
                                        dialog -> finish()
                                );
                            }
                            else {
                                displayDuplicateDialog(data);
                            }
                        }

                        @Override
                        public void onRetry() {
                            req.enqueue(finalCall1);
                        }
                    };
                    break;
                case EDIT_MEMBER:
                    call = RetrofitClient.getInstance(null).getApiServices().editMember(
                            MEMBER_ID,
                            Guru.getInt(KEY_MEMBER_ID, 0),
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
                    break;
                case DELETE_MEMBER:
                    call = RetrofitClient.getInstance(null).getApiServices().deleteMember(
                            MEMBER_ID
                    );
                    break;
            }
            Call finalCall = call;
            IssueRequestHandler.OnRequestHandler defaultOnRequestHandler =
                    new IssueRequestHandler.OnRequestHandler() {
                        @Override
                        public void onTry() {

                        }

                        @Override
                        public void onSuccess(APIWrapper res, String message) {
                            Constant.displayDialog(
                                    PntSym_InputForm.this,
                                    "OK, Operasi berhasil",
                                    message,
                                    false,
                                    (dialog, which) -> {
                                    },
                                    null,
                                    dialog -> finish()
                            );
                        }

                        @Override
                        public void onRetry() {
                            req.enqueue(finalCall);
                        }
                    };
            req.setOnRequestHandler(addingOnRequestHandler == null ? defaultOnRequestHandler : addingOnRequestHandler);
            req.enqueue(call);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            int memberId = data.getIntExtra("model.id", 0);
            IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
            req.setIntention(new Throwable());
            req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                @Override
                public void onTry() {

                }

                @Override
                public void onSuccess(APIWrapper res, String message) throws JSONException {

                    b.layoutName.setVisibility(View.VISIBLE);
                    b.layoutBody.setVisibility(View.VISIBLE);
                    b.layoutButtons.setVisibility(View.VISIBLE);

                    JSONObject data = res.getData();
                    MEMBER_ID = memberId;
                    b.editTextFindName.setText(data.getString("first_name"));
                    b.editTextFirstName.setText(data.getString("first_name"));
                    b.editTextMiddleName.setText(data.getString("middle_name"));
                    b.editTextLastName.setText(data.getString("last_name"));
                    b.editTextDOB.setText(data.getString("date_of_birth"));

                    String[] sexes = new String[]{"Laki-laki", "Perempuan"};
                    b.spinnerSex.setItems(sexes);

                    if (data.getString("sex").contentEquals("Laki-laki")) {
                        b.spinnerSex.setSelectedIndex(0);
                    }
                    else {
                        b.spinnerSex.setSelectedIndex(1);
                    }

                    b.checkBoxBaptize.setChecked(!data.isNull("baptize"));
                    b.checkBoxSidi.setChecked(!data.isNull("sidi"));
                    b.checkBoxMarried.setChecked(!data.isNull("marriage"));
                }

                @Override
                public void onRetry() {
                    req.enqueue(RetrofitClient.getInstance(null).getApiServices().getMemberUserData(memberId));
                }
            });
            req.enqueue(RetrofitClient.getInstance(null).getApiServices().getMemberUserData(memberId));

        }
    }

    private void displayDuplicateDialog(JSONObject data) throws JSONException {

        JSONArray similarMembers = data.getJSONArray("similar_members");

        CustomDialog dialog = new CustomDialog();
        dialog.setTitle("Perhatian !");
        View view = getLayoutInflater().inflate(R.layout.resource_dialog_duplicate_user,
                ((ViewGroup) dialog.getView()), false);
        LinearLayout llHolder = view.findViewById(R.id.layout_listHolder);
        TextView tvDescription = view.findViewById(R.id.textView_description);
        String desc = tvDescription.getText() + "\nBerjumlah: " + similarMembers.length();
        tvDescription.setText(desc);

        for (int i = 0; i < similarMembers.length(); i++) {
            JSONObject member = similarMembers.getJSONObject(i);
            View v = getLayoutInflater().inflate(
                    R.layout.resource_list_item_duplicate_user,
                    ((ViewGroup) dialog.getView()),
                    false
            );
            TextView tvName = v.findViewById(R.id.textView_name),
                    tvInfo = v.findViewById(R.id.textView_info);

            tvName.setText(member.getString("full_name"));
            String info = member.getString("full_name") + "\n"
                    + member.getString("dob") + "\n"
                    + member.getString("sex") + "\n"
                    + member.getString("column");
            tvInfo.setText(info);

            llHolder.addView(v);
        }

        dialog.setOnClickPositive((dialog1, which) -> forceSaveData());
        dialog.setOnClickNegative(DialogInterface::dismiss);
        dialog.setCustomView(view);
        dialog.show(this);
    }

    private void forceSaveData() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                Constant.displayDialog(
                        PntSym_InputForm.this,
                        "OK, Operasi berhasil",
                        message,
                        false,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> finish()
                );

            }

            @Override
            public void onRetry() {
                forceSaveData();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().addMemberUser(
                Guru.getInt(KEY_MEMBER_ID, 0),
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
                letterMarried,
                true
                )
        );
    }

}
