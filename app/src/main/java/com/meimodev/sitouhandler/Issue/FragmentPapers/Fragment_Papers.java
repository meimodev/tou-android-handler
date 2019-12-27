package com.meimodev.sitouhandler.Issue.FragmentPapers;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomButtonAdd;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.Validator;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Fragment_Papers extends Fragment {

    private static final String TAG = "Fragment_Papers: ";

    private View rootView;
    private Context context;


    @BindView(R.id.tv_Title)
    TextView tvTitle;
    @BindView(R.id.linearLayout_baptis)
    LinearLayout llBaptis;
    @BindView(R.id.linearLayout_nikah)
    LinearLayout llNikah;

    @BindView(R.id.linearLayout_main)
    LinearLayout llMain;

    @BindView(R.id.button_tambahNama)
    CustomButtonAdd btnAddName;
    @BindView(R.id.button_submit)
    CustomButtonAdd btnSubmit;
    @BindView(R.id.linearLayout_priestPlaceHolder)
    LinearLayout llpriestPlaceHolder;


    //    Credential Views
    @BindView(R.id.linearLayout_kredensi)
    LinearLayout llKredensi;
    @BindView(R.id.editText_credential_name)
    CustomEditText etCredentialName;
    @BindView(R.id.editText_credential_date)
    CustomEditText etCredentialDate;
    @BindView(R.id.editText_credential_time)
    CustomEditText etCredentialTime;
    @BindView(R.id.editText_credential_place)
    CustomEditText etCredentialPlace;

    //    Date executed & executor
    @BindView(R.id.linearLayout_dateExecPendeta)
    LinearLayout lldateExec_pendeta;
    @BindView(R.id.textInputLayout_editText_date)
    TextInputLayout tilEditTextDate;
    @BindView(R.id.editText_dateExecute)
    CustomEditText etDateExecuted;
    @BindView(R.id.button_tambahNamaPriest)
    CustomButtonAdd btnAddPriest;
    @BindView(R.id.linearLayout_namePlaceHolder)
    LinearLayout llnamePlaceHolder;

    private final int REQUEST_CODE_PERSONAL_NAME = 1;
    private final int REQUEST_CODE_PRIEST_NAME = 2;

    private String keyIssue;
    private ArrayList<CustomEditText> customEditTexts = new ArrayList<>();
    private ArrayList<CustomButtonAdd> customButtonAdds = new ArrayList<>();
    private ArrayList<RadioGroup> radioGroups = new ArrayList<>();
    private ArrayList<MaterialSpinner> materialSpinners = new ArrayList<>();
    private boolean warnWitness = true;
    private boolean warned = false;

    private LayoutTransition defaultLayoutTransition;

    private String POST_issuedMemberData = "";
    private String POST_destination = "";
    private String POST_date = "";
    private String POST_time = "";
    private String POST_place = "";
    private String POST_ceremonyDate = "";
    private String POST_priestId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            rootView = inflater.inflate(R.layout.fragment_issue_papers, container, false);
            ButterKnife.bind(this, rootView);
            keyIssue = getArguments().getString(Constant.KEY_PAPERS);
            context = rootView.getContext();

            if (keyIssue != null) {

                warnWitness = false;
                tvTitle.setText("Pengajuan ".concat(keyIssue));

                etDateExecuted.setAsDatePicker(getFragmentManager());

                btnAddName.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Adding.class);
                    intent.putExtra("request", "name");
                    startActivityForResult(intent, REQUEST_CODE_PERSONAL_NAME);
                });

                btnAddPriest.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Adding.class);
                    intent.putExtra("request", "priest");
                    startActivityForResult(intent, REQUEST_CODE_PRIEST_NAME);
                });
                btnAddPriest.setAsAddingButton(container, inflater, llpriestPlaceHolder, 1, () -> {
                    Snackbar.make(rootView, "Pendeta Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                });

                OnRecyclerItemOperationListener.DeleteListener deleteListener = () -> {
                    Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                };
                btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 1, deleteListener);
                defaultLayoutTransition = llnamePlaceHolder.getLayoutTransition();
                switch (keyIssue) {
                    case Constant.KEY_PAPERS_VALIDATE_MEMBERS:
                        btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 1, deleteListener);
                        break;

                    case Constant.KEY_PAPERS_CREDENTIAL:
                        llKredensi.setVisibility(View.VISIBLE);
                        btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 1000, deleteListener);

                        etCredentialDate.setAsDatePicker(getFragmentManager());
                        etCredentialTime.setAsTimePicker(getFragmentManager());

                        break;

                    case Constant.KEY_PAPERS_BAPTIZE:
                        lldateExec_pendeta.setVisibility(View.VISIBLE);
                        llBaptis.setVisibility(View.VISIBLE);
                        deleteListener = () -> {
                            Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                            resetPlaceHolderView(llnamePlaceHolder, btnAddName);

                        };
                        btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 15, deleteListener);
                        break;

                    case Constant.KEY_PAPERS_SIDI:
                        lldateExec_pendeta.setVisibility(View.VISIBLE);
                        btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 1, deleteListener);
                        break;

                    case Constant.KEY_PAPERS_MARRIED:
                        lldateExec_pendeta.setVisibility(View.VISIBLE);
                        llNikah.setVisibility(View.VISIBLE);
                        deleteListener = () -> {
                            Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                            resetPlaceHolderView(llnamePlaceHolder, btnAddName);

                        };
                        btnAddName.setAsAddingButton(container, inflater, llnamePlaceHolder, 6, deleteListener);
                        break;
                }
            }
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            Adding_RecyclerModel selectedModel;
            if (data.getBooleanExtra("unregistered", false)) {
                selectedModel = new Adding_RecyclerModel(0,
                        data.getStringExtra("model.name"),
                        null,
                        null,
                        null);
                selectedModel.setUnregistered(true);
            } else {
                selectedModel = new Adding_RecyclerModel(
                        data.getIntExtra("model.id", 0),
                        data.getStringExtra("model.name"),
                        data.getStringExtra("model.bod"),
                        data.getStringExtra("model.kol"),
                        data.getStringExtra("model.img")

                );
                selectedModel.setCategory(data.getStringExtra("model.cat"));
                selectedModel.setBaptis(data.getBooleanExtra("model.bap", false));
                selectedModel.setSidi(data.getBooleanExtra("model.sid", false));
                selectedModel.setNikah(data.getBooleanExtra("model.nik", false));

            }
            switch (requestCode) {
                case REQUEST_CODE_PERSONAL_NAME:
                    btnAddName.addSelected(selectedModel);
                    if (keyIssue.contentEquals(Constant.KEY_PAPERS_BAPTIZE) || keyIssue.contentEquals(Constant.KEY_PAPERS_MARRIED)) {
                        resetPlaceHolderView(llnamePlaceHolder, btnAddName);
                    }
                    if (keyIssue.contentEquals(Constant.KEY_PAPERS_BAPTIZE)) {
                        warned = false;
                        warnWitness = true;
                    }

                    break;
                case REQUEST_CODE_PRIEST_NAME:
                    selectedModel.setCategory("Pendeta Peneguhan :");
                    btnAddPriest.addSelected(selectedModel);

                    break;
            }
        }
    }

    @OnClick(R.id.button_submit)
    void btnSubmit() {

        ////////////////////////////////////// index sensitive //////////////////////////////////////
        customEditTexts.add(etCredentialName); // 0
        customEditTexts.add(etCredentialDate); // 1
        customEditTexts.add(etCredentialTime); // 2
        customEditTexts.add(etCredentialPlace); // 3
        customEditTexts.add(etDateExecuted); // 4
        customButtonAdds.add(btnAddName); // 0
        customButtonAdds.add(btnAddPriest); // 1
        materialSpinners.add(null);
        radioGroups.add(null);
        ////////////////////////////////////// index sensitive //////////////////////////////////////

        Validator validator = new Validator();


        if (keyIssue.contentEquals(Constant.KEY_PAPERS_BAPTIZE)) {
            if (btnAddName.getSelectedList().size() > 3) {
                if (!warned && (btnAddName.getSelectedList().size() - 3) != 12) {
                    warnWitness = true;
                }
            }

            if (btnAddName.getSelectedList().size() > 3 && warnWitness) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Peringatan !");
                builder.setMessage("Jumlah saksi yang di ajukan adalah " + (btnAddName.getSelectedList().size() - 3) + "\nJumlah maksimal saksi adalah 12\n\nabaikan peringatan ini jika pengajuan ini memang tidak memliki 12 saksi");
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    warnWitness = false;
                    warned = true;
                    dialogInterface.dismiss();
                });
                builder.create().show();
                return;
            }
        }


        Map<String, String> validation = validator.validateInput(keyIssue, customButtonAdds, customEditTexts, materialSpinners, radioGroups);
        if (validation.get("result").contentEquals("ERROR")) {
            validator.displayErrorMessage(context, validation.get("message"));
            // reset validator holder
            customEditTexts.clear();
            customButtonAdds.clear();
            materialSpinners.clear();
            radioGroups.clear();
            return;
        }


        Snackbar.make(rootView, " SUBMITING ...", Snackbar.LENGTH_SHORT).show();

        Log.e(TAG, "------------------------ LOG HEAD ------------------------");
        Log.e(TAG, "SUBMIT FRAGMENT INCOME : KEYBUNDLE -- " + keyIssue);

        int index = 1;

        ArrayList<Adding_RecyclerModel> selectedPriests = btnAddPriest.getSelectedList();
        ArrayList<Adding_RecyclerModel> selectedNames = btnAddName.getSelectedList();
        switch (keyIssue) {
            case Constant.KEY_PAPERS_VALIDATE_MEMBERS:
                break;
            case Constant.KEY_PAPERS_CREDENTIAL:

                POST_destination = etCredentialName.getText().toString();
                POST_date = etCredentialDate.getText().toString();
                POST_time = etCredentialTime.getText().toString();
                POST_place = etCredentialPlace.getText().toString();

                Log.e(TAG, "Destination : " + POST_destination);
                Log.e(TAG, "Date :" + POST_date);
                Log.e(TAG, "Time :" + POST_time);
                Log.e(TAG, "Place :" + POST_place);
                Log.e(TAG, "============ Issued Member ===========");


                break;
            case Constant.KEY_PAPERS_BAPTIZE:
                POST_ceremonyDate = etDateExecuted.getText().toString().trim();

                Log.e(TAG, "Date : " + POST_ceremonyDate);
                Log.e(TAG, "Priest Count : " + selectedPriests.size());
                Log.e(TAG, "============ Priest In Charge ============");
                for (Adding_RecyclerModel model : selectedPriests) {
                    POST_priestId = String.valueOf(model.getId());
                    Log.e(TAG, "Priest " + index + " Name : " + model.getName());
                    Log.e(TAG, "Priest " + index + " BOD : " + model.getBirthDate());
                    Log.e(TAG, "Priest " + index + " Kol : " + model.getKolom());
                    Log.e(TAG, "Priest " + index + " isBaptize : " + model.isBaptis());
                    Log.e(TAG, "Priest " + index + " isSidi : " + model.isSidi());
                    Log.e(TAG, "Priest " + index + " isNikah : " + model.isNikah());
                    index++;
                }
                Log.e(TAG, "==================================================");
                break;
            case Constant.KEY_PAPERS_SIDI:
                POST_ceremonyDate = etDateExecuted.getText().toString();

                Log.e(TAG, "Priest Count : " + selectedPriests.size());
                Log.e(TAG, "============ Priest In Charge ============");
                for (Adding_RecyclerModel model : selectedPriests) {
                    POST_priestId = String.valueOf(model.getId());
                    Log.e(TAG, "Priest " + index + " Name : " + model.getName());
                    Log.e(TAG, "Priest " + index + " BOD : " + model.getBirthDate());
                    Log.e(TAG, "Priest " + index + " Kol : " + model.getKolom());
                    Log.e(TAG, "Priest " + index + " isBaptize : " + model.isBaptis());
                    Log.e(TAG, "Priest " + index + " isSidi : " + model.isSidi());
                    Log.e(TAG, "Priest " + index + " isNikah : " + model.isNikah());
                    index++;
                }
                Log.e(TAG, "==================================================");
                break;
            case Constant.KEY_PAPERS_MARRIED:
                POST_ceremonyDate = etDateExecuted.getText().toString();

                Log.e(TAG, "Priest Count : " + selectedPriests.size());
                Log.e(TAG, "============ Priest In Charge ============");
                for (Adding_RecyclerModel model : selectedPriests) {
                    POST_priestId = String.valueOf(model.getId());
                    Log.e(TAG, "Priest " + index + " Name : " + model.getName());
                    if (model.isCategoryHead())
                        Log.e(TAG, "Name " + index + " Category : " + model.getCategory());
                    Log.e(TAG, "Priest " + index + " BOD : " + model.getBirthDate());
                    Log.e(TAG, "Priest " + index + " Kol : " + model.getKolom());
                    Log.e(TAG, "Priest " + index + " isBaptize : " + model.isBaptis());
                    Log.e(TAG, "Priest " + index + " isSidi : " + model.isSidi());
                    Log.e(TAG, "Priest " + index + " isNikah : " + model.isNikah());
                    index++;
                }
                Log.e(TAG, "==================================================");
                break;
        }

        Log.e(TAG, "Names count : " + selectedNames.size());
        index = 1;
        for (Adding_RecyclerModel model : selectedNames) {
            Log.e(TAG, "Name " + index + " Id : " + model.getId());
            if (model.isCategoryHead())
                Log.e(TAG, "Name " + index + " Category : " + model.getCategory());
            Log.e(TAG, "Name " + index + " Name : " + model.getName());
            Log.e(TAG, "Name " + index + " BOD : " + model.getBirthDate());
            Log.e(TAG, "Name " + index + " Kol : " + model.getKolom());
            Log.e(TAG, "Name " + index + " isBaptize : " + model.isBaptis());
            Log.e(TAG, "Name " + index + " isSidi : " + model.isSidi());
            Log.e(TAG, "Name " + index + " isNikah : " + model.isNikah());
            Log.e(TAG, "---------------------------------------------------");
            index++;
        }
        POST_issuedMemberData = Constant.encodeMemberData(btnAddName);

        Log.e(TAG, "------------------------------------------------------------------------");
        Log.e(TAG, ":REQUEST TO SERVER:");
        Log.e(TAG, "------------------------------------------------------------------------");
        Log.e(TAG, "issued_by_member_id: " + SharedPrefManager.load(context, SharedPrefManager.KEY_MEMBER_ID));
        Log.e(TAG, "keyIssue: " + keyIssue);
        Log.e(TAG, "destination: " + POST_destination);
        Log.e(TAG, "date: " + POST_date);
        Log.e(TAG, "time: " + POST_time);
        Log.e(TAG, "place: " + POST_place);
        Log.e(TAG, "ceremony_date: " + POST_ceremonyDate);
        Log.e(TAG, "priest_id: " + POST_priestId);
        Log.e(TAG, "issued_member_data: " + POST_issuedMemberData);
        Log.e(TAG, "------------------------------------------------------------------------");

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);
        requestHandler.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().setIssuePaper(
                ((int) SharedPrefManager.load(context, SharedPrefManager.KEY_MEMBER_ID)),
                keyIssue,
                POST_destination,
                POST_date,
                POST_time,
                POST_place,
                POST_ceremonyDate,
                POST_priestId,
                POST_issuedMemberData
        ));

        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) {

                Constant.displayDialog(
                        context,
                        "Pengajuan Berhasil",
                        message,
                        true,
                        (dialogInterface, i) -> {
                        },
                        null,
                        dialogInterface -> {
                            context.sendBroadcast(new Intent(Constant.ACTION_ACTIVITY_FINISH));
                        }
                );
            }

            @Override
            public void onRetry() {
                requestHandler.issueRequestResponse(RetrofitClient.getInstance(null).getApiServices().setIssuePaper(
                        ((int) SharedPrefManager.load(context, SharedPrefManager.KEY_MEMBER_ID)),
                        keyIssue,
                        POST_destination,
                        POST_date,
                        POST_time,
                        POST_place,
                        POST_ceremonyDate,
                        POST_priestId,
                        POST_issuedMemberData
                ));
            }
        });
    }


    private void assignCategory(String keyBundle, ArrayList<Adding_RecyclerModel> selectedNames) {

        switch (keyBundle) {
            case Constant.KEY_PAPERS_VALIDATE_MEMBERS:
                break;
            case Constant.KEY_PAPERS_CREDENTIAL:
                break;
            case Constant.KEY_PAPERS_BAPTIZE:
                if (!selectedNames.isEmpty()) {

                    switch (selectedNames.size()) {
                        case 1:
                            selectedNames.get(0).setCategory("Yang di baptis :");
                            break;
                        case 2:
                            selectedNames.get(0).setCategory("Yang di baptis :");
                            selectedNames.get(1).setCategory("Ayah :");
                            break;
                        case 3:
                            selectedNames.get(0).setCategory("Yang di baptis :");
                            selectedNames.get(1).setCategory("Ayah :");
                            selectedNames.get(2).setCategory("Ibu :");
                            break;
                        case 4:
                            selectedNames.get(0).setCategory("Yang di baptis :");
                            selectedNames.get(1).setCategory("Ayah :");
                            selectedNames.get(2).setCategory("Ibu :");
                            selectedNames.get(3).setCategory("Saksi-saksi :");
                            break;
                    }
                }
                break;
            case Constant.KEY_PAPERS_SIDI:
                break;
            case Constant.KEY_PAPERS_MARRIED:
                if (!selectedNames.isEmpty()) {

                    switch (selectedNames.size()) {
                        case 1:
                            selectedNames.get(0).setCategory("Suami :");

                            break;
                        case 2:
                            selectedNames.get(0).setCategory("Suami :");
                            selectedNames.get(1).setCategory("Ayah Suami :");

                            break;
                        case 3:
                            selectedNames.get(0).setCategory("Suami :");
                            selectedNames.get(1).setCategory("Ayah Suami :");
                            selectedNames.get(2).setCategory("Ibu Suami :");

                            break;
                        case 4:
                            selectedNames.get(0).setCategory("Suami :");
                            selectedNames.get(1).setCategory("Ayah Suami :");
                            selectedNames.get(2).setCategory("Ibu Suami :");
                            selectedNames.get(3).setCategory("Istri :");

                            break;
                        case 5:
                            selectedNames.get(0).setCategory("Suami :");
                            selectedNames.get(1).setCategory("Ayah Suami :");
                            selectedNames.get(2).setCategory("Ibu Suami :");
                            selectedNames.get(3).setCategory("Istri :");
                            selectedNames.get(4).setCategory("Ayah Istri :");

                            break;
                        case 6:
                            selectedNames.get(0).setCategory("Suami :");
                            selectedNames.get(1).setCategory("Ayah Suami :");
                            selectedNames.get(2).setCategory("Ibu Suami :");
                            selectedNames.get(3).setCategory("Istri :");
                            selectedNames.get(4).setCategory("Ayah Istri :");
                            selectedNames.get(5).setCategory("Ibu Istri");

                            break;
                    }
                }
                break;
        }

    }

    private void resetPlaceHolderView(LinearLayout placeHolder, CustomButtonAdd caller) {
        if (caller.getSelectedList().size() > 1) placeHolder.setLayoutTransition(null);
        else placeHolder.setLayoutTransition(defaultLayoutTransition);

        placeHolder.removeAllViewsInLayout();
        assignCategory(keyIssue, caller.getSelectedList());
        ArrayList<Adding_RecyclerModel> list = new ArrayList<>(caller.getSelectedList());

        caller.setSelectedList(new ArrayList<>());

        for (Adding_RecyclerModel model : list) {
            caller.addSelected(model);
        }


    }
}
