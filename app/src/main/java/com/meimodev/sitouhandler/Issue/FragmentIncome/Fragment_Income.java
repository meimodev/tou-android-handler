package com.meimodev.sitouhandler.Issue.FragmentIncome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.hmomeni.progresscircula.ProgressCircula;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomButtonAdd;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.Validator;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meimodev.sitouhandler.Constant.*;

public class Fragment_Income extends Fragment {
    private static final String TAG = "Fragment_Income";

    private static final int REQUEST_CODE_PERSONAL_NAME = 1;
    private static final int REQUEST_CODE_PRIEST_NAME = 2;

    private Context context;
    private View rootView;

    private String keyIssue;

    @BindView(R.id.layout_main)
    View layoutMain;

    @BindView(R.id.tv_Title)
    TextView tvTitle;
    @BindView(R.id.editText_amount)
    CustomEditText etAmount;
    @BindView(R.id.button_submit)
    CustomButtonAdd btnSubmit;

    @BindView(R.id.switch_church)
    Switch switchChurch;
    @BindView(R.id.switch_withDetail)
    Switch switchWithDetail;
    @BindView(R.id.layout_incomeDetailPlaceHolder)
    LinearLayout llIncomeDetailPlaceHolder;

    @BindView(R.id.switch_pelDetail)
    Switch switchPelDetail;
    @BindView(R.id.switch_pemDetail)
    Switch switchPemDetail;
    @BindView(R.id.switch_extraDetail)
    Switch switchExtraDetail;
    @BindView(R.id.layout_PelPlaceHolder)
    LinearLayout llPelPlaceHolder;
    @BindView(R.id.layout_PemPlaceHolder)
    LinearLayout llPemPlaceHolder;
    @BindView(R.id.layout_extraPlaceHolder)
    LinearLayout llExtraPlaceHolder;
    @BindView(R.id.editText_pelTotal)
    CustomEditText etPelTotal;
    @BindView(R.id.editText_pemTotal)
    CustomEditText etPemTotal;
    @BindView(R.id.editText_extraTotal)
    CustomEditText etExtraTotal;

    @BindView(R.id.button_details)
    CustomButtonAdd btnAddDetails;
    @BindView(R.id.spinner_sampulSyukurType)
    MaterialSpinner spinnerSampulSyukurType;
    @BindView(R.id.linearLayout_detailsPlaceHolder)
    LinearLayout llDetailsPlaceHolder;

    @BindView(R.id.button_addName)
    CustomButtonAdd btnAddName;
    @BindView(R.id.linearLayout_namePlaceHolder)
    LinearLayout llNamePlaceHolder;

    @BindView(R.id.cardView_infoService)
    CardView cvInfoService;
    @BindView(R.id.editText_findId)
    CustomEditText etFindId;
    @BindView(R.id.textView_loading)
    TextView tvLoading;
    @BindView(R.id.linearLayout_progressHolder)
    LinearLayout llProgressServiceHolder;

    @BindView(R.id.linearLayout_details)
    LinearLayout llDetails;
    @BindView(R.id.linearLayout_infoService)
    LinearLayout llInfoService;
    @BindView(R.id.linearLayout_other)
    LinearLayout llOther;
    @BindView(R.id.linearLayout_otherNoAccount)
    LinearLayout llOtherNoAccount;

    @BindView(R.id.radio_other)
    RadioGroup rgOther;
    @BindView(R.id.editText_other_detail)
    CustomEditText etOtherDetail;
    @BindView(R.id.et_other_note)
    CustomEditText etOther_Note;

    @BindView(R.id.textView_idNotFound)
    TextView tvIdNotFound;
    @BindView(R.id.linearLayout_serviceInfoFound)
    LinearLayout llServiceInfoFound;
    @BindView(R.id.textView_infoServiceType)
    TextView tvInfoServiceType;
    @BindView(R.id.textView_infoServiceId)
    TextView tvInfoServiceId;
    @BindView(R.id.textView_infoName)
    TextView tvInfoName;
    @BindView(R.id.textView_infoDate)
    TextView tvInfoDate;
    @BindView(R.id.textView_infoKhadim)
    TextView tvInfoKhadim;
    @BindView(R.id.textView_infoPlace)
    TextView tvInfoPlace;
    @BindView(R.id.layout_connectionProblem)
    LinearLayout llConnectionProblem;


//    private ArrayList<Integer> recordPelDetails = new ArrayList<>();
//    private ArrayList<Integer> recordPemDetails = new ArrayList<>();
//    private ArrayList<Integer> recordExtraDetails = new ArrayList<>();

    private ArrayList<CustomEditText> editTextsPel = new ArrayList<>();
    private ArrayList<CustomEditText> editTextsPem = new ArrayList<>();
    private ArrayList<CustomEditText> editTextsExtra = new ArrayList<>();

    private ArrayList<CustomEditText> customEditTexts = new ArrayList<>();
    private ArrayList<CustomButtonAdd> customButtonAdds = new ArrayList<>();
    private ArrayList<MaterialSpinner> materialSpinners = new ArrayList<>();
    private ArrayList<RadioGroup> radioGroups = new ArrayList<>();

    private boolean warnAnonymity = true;
    private boolean isServiceDataFound = false;

    private IssueRequestHandler issueRequestHandler;

    private String POST_accountNumberKey = "";
    private String POST_issuedMemberData = "";
    private String POST_note = "";
    private String POST_description = "";
    private int POST_serviceId = 0;
    private ArrayList<String> radioOtherArray;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_issue_income, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);
        etAmount.setAsThousandSeparator();
        etAmount.setAsNoLeadingZero();

        issueRequestHandler = new IssueRequestHandler(rootView);

        View progressHolder = rootView.findViewById(R.id.layout_progressHolder);
        if (progressHolder.getVisibility() == View.VISIBLE)
            progressHolder.setVisibility(View.GONE);

        if (getArguments() != null) {
            keyIssue = getArguments().getString(KEY_INCOME);

            if (keyIssue != null) {
                tvTitle.setText("Pengajuan Pemasukan ".concat(keyIssue));
                Toast.makeText(context, keyIssue, Toast.LENGTH_SHORT).show();

                warnAnonymity = false;

                switch (keyIssue) {
                    case KEY_INCOME_PERSEMBAHAN_IBADAH:
                        warnAnonymity = false;
                        llInfoService.setVisibility(View.VISIBLE);

                        etPelTotal.setAsNoLeadingZero();
                        etPelTotal.setAsThousandSeparator();
                        etPemTotal.setAsNoLeadingZero();
                        etPemTotal.setAsThousandSeparator();
                        etExtraTotal.setAsNoLeadingZero();
                        etExtraTotal.setAsThousandSeparator();

                        View progress = getLayoutInflater().inflate(R.layout.resource_custom_progress_bar_progressing, llProgressServiceHolder, false);
                        ProgressCircula p = progress.findViewById(R.id.pc);
                        p.setSpeed(3);
                        p.setIndeterminate(true);
                        p.setRimWidth(6);
                        p.startRotation();
                        p.setRimColor(rootView.getContext().getResources().getColor(R.color.colorAccent));
                        p.setShowProgress(false);


//                        if (llProgressServiceHolder.getVisibility() == View.VISIBLE)
//                            llProgressServiceHolder.setVisibility(View.GONE);

                        CompoundButton.OnCheckedChangeListener checkedChangeListener = (compoundButton, b) -> {

                            View inflatedDetailPel = getLayoutInflater().inflate(R.layout.resource_income_detail, llPelPlaceHolder, false);
                            View inflatedDetailPem = getLayoutInflater().inflate(R.layout.resource_income_detail, llPemPlaceHolder, false);
                            View inflatedDetailExtra = getLayoutInflater().inflate(R.layout.resource_income_detail, llExtraPlaceHolder, false);
                            CustomEditText et1kLogam;
                            CustomEditText et500Logam;
                            CustomEditText et1k;
                            CustomEditText et2k;
                            CustomEditText et5k;
                            CustomEditText et10k;
                            CustomEditText et20k;
                            CustomEditText et50k;
                            CustomEditText et100k;
//                            CustomEditText etDetailTotal;
//                            TextView tvTotal;

                            if (compoundButton == switchChurch) {
                                if (b) {
                                    switchWithDetail.setVisibility(View.VISIBLE);
                                } else {
                                    switchWithDetail.setVisibility(View.GONE);
                                    switchWithDetail.setChecked(false);
                                }
                            } else if (compoundButton == switchWithDetail) {
                                if (b) {
                                    llIncomeDetailPlaceHolder.setVisibility(View.VISIBLE);

                                    etAmount.setText("");
                                    etAmount.setEnabled(false);

                                    ArrayList<CustomEditText> editTexts = new ArrayList<>();
                                    editTexts.add(etPelTotal);
                                    editTexts.add(etPemTotal);
                                    editTexts.add(etExtraTotal);

                                    for (CustomEditText et : editTexts) {
                                        et.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                int a;
                                                if (etPelTotal.getText().toString().isEmpty()) {
                                                    a = 0;
                                                } else {
                                                    a = Integer.valueOf(etPelTotal.getText().toString().replace(",", ""));
                                                }

                                                int b;
                                                if (etPemTotal.getText().toString().isEmpty()) {
                                                    b = 0;
                                                } else {
                                                    b = Integer.valueOf(etPemTotal.getText().toString().replace(",", ""));
                                                }

                                                int c;
                                                if (etExtraTotal.getText().toString().isEmpty()) {
                                                    c = 0;
                                                } else {
                                                    c = Integer.valueOf(etExtraTotal.getText().toString().replace(",", ""));
                                                }

                                                etAmount.setText(String.valueOf(a + b + c));
                                            }
                                        });
                                    }

                                } else {
                                    llIncomeDetailPlaceHolder.setVisibility(View.GONE);
                                    switchPelDetail.setChecked(false);
                                    switchPemDetail.setChecked(false);
                                    switchExtraDetail.setChecked(false);

                                    if (!etAmount.isEnabled()) etAmount.setEnabled(true);
                                    etPelTotal.setText("");
                                    etPemTotal.setText("");
                                    etExtraTotal.setText("");
                                    etAmount.setText("");

                                }
                            } else if (compoundButton == switchPelDetail) {
                                if (b) {
                                    llPelPlaceHolder.setVisibility(View.VISIBLE);
                                    etPelTotal.setText("");
                                    etPelTotal.setEnabled(false);

                                    et1kLogam = inflatedDetailPel.findViewById(R.id.editText_1000Metal);
                                    et500Logam = inflatedDetailPel.findViewById(R.id.editText_500metal);
                                    et1k = inflatedDetailPel.findViewById(R.id.editText_1k);
                                    et2k = inflatedDetailPel.findViewById(R.id.editText_2k);
                                    et5k = inflatedDetailPel.findViewById(R.id.editText_5k);
                                    et10k = inflatedDetailPel.findViewById(R.id.editText_10k);
                                    et20k = inflatedDetailPel.findViewById(R.id.editText_20k);
                                    et50k = inflatedDetailPel.findViewById(R.id.editText_50k);
                                    et100k = inflatedDetailPel.findViewById(R.id.editText_100k);
//                                    etDetailTotal = inflatedDetailPel.findViewById(R.id.editText_detailTotal);
//                                    etDetailTotal.setAsThousandSeparator();
//                                    etDetailTotal.setAsNoLeadingZero();
//                                    tvTotal = inflatedDetailPel.findViewById(R.id.textView_total);
//                                    tvTotal.setText("Total Pelayanan");
                                    editTextsPel = new ArrayList<>();
                                    editTextsPel.add(et1kLogam);
                                    editTextsPel.add(et500Logam);
                                    editTextsPel.add(et1k);
                                    editTextsPel.add(et2k);
                                    editTextsPel.add(et5k);
                                    editTextsPel.add(et10k);
                                    editTextsPel.add(et20k);
                                    editTextsPel.add(et50k);
                                    editTextsPel.add(et100k);

                                    for (CustomEditText et : editTextsPel) {
                                        et.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                calculateDetailTotal(editTextsPel, /*etDetailTotal,*/ etPelTotal);

                                            }
                                        });
                                    }

                                    llPelPlaceHolder.addView(inflatedDetailPel);

                                } else {
                                    llPelPlaceHolder.setVisibility(View.GONE);
                                    if (!etPelTotal.isEnabled()) etPelTotal.setEnabled(true);

                                    llPelPlaceHolder.removeAllViews();
                                    editTextsPel.clear();

                                }
                            } else if (compoundButton == switchPemDetail) {
                                if (b) {
                                    llPemPlaceHolder.setVisibility(View.VISIBLE);
                                    etPemTotal.setText("");
                                    etPemTotal.setEnabled(false);

                                    et1kLogam = inflatedDetailPem.findViewById(R.id.editText_1000Metal);
                                    et500Logam = inflatedDetailPem.findViewById(R.id.editText_500metal);
                                    et1k = inflatedDetailPem.findViewById(R.id.editText_1k);
                                    et2k = inflatedDetailPem.findViewById(R.id.editText_2k);
                                    et5k = inflatedDetailPem.findViewById(R.id.editText_5k);
                                    et10k = inflatedDetailPem.findViewById(R.id.editText_10k);
                                    et20k = inflatedDetailPem.findViewById(R.id.editText_20k);
                                    et50k = inflatedDetailPem.findViewById(R.id.editText_50k);
                                    et100k = inflatedDetailPem.findViewById(R.id.editText_100k);
//                                    etDetailTotal = inflatedDetailPem.findViewById(R.id.editText_detailTotal);
//                                    etDetailTotal.setAsThousandSeparator();
//                                    tvTotal = inflatedDetailPem.findViewById(R.id.textView_total);
//                                    tvTotal.setText("Total Pembangunan");
                                    editTextsPem = new ArrayList<>();
                                    editTextsPem.add(et1kLogam);
                                    editTextsPem.add(et500Logam);
                                    editTextsPem.add(et1k);
                                    editTextsPem.add(et2k);
                                    editTextsPem.add(et5k);
                                    editTextsPem.add(et10k);
                                    editTextsPem.add(et20k);
                                    editTextsPem.add(et50k);
                                    editTextsPem.add(et100k);

                                    for (CustomEditText et : editTextsPem) {
                                        et.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                calculateDetailTotal(editTextsPem, /*etDetailTotal,*/ etPemTotal);

                                            }
                                        });
                                    }

                                    llPemPlaceHolder.addView(inflatedDetailPem);

                                } else {
                                    llPemPlaceHolder.setVisibility(View.GONE);
                                    if (!etPemTotal.isEnabled()) etPemTotal.setEnabled(true);

                                    llPemPlaceHolder.removeAllViews();
                                    editTextsPem.clear();
                                }
                            } else if (compoundButton == switchExtraDetail) {
                                if (b) {
                                    llExtraPlaceHolder.setVisibility(View.VISIBLE);
                                    etExtraTotal.setText("");
                                    etExtraTotal.setEnabled(false);
//
                                    et1kLogam = inflatedDetailExtra.findViewById(R.id.editText_1000Metal);
                                    et500Logam = inflatedDetailExtra.findViewById(R.id.editText_500metal);
                                    et1k = inflatedDetailExtra.findViewById(R.id.editText_1k);
                                    et2k = inflatedDetailExtra.findViewById(R.id.editText_2k);
                                    et5k = inflatedDetailExtra.findViewById(R.id.editText_5k);
                                    et10k = inflatedDetailExtra.findViewById(R.id.editText_10k);
                                    et20k = inflatedDetailExtra.findViewById(R.id.editText_20k);
                                    et50k = inflatedDetailExtra.findViewById(R.id.editText_50k);
                                    et100k = inflatedDetailExtra.findViewById(R.id.editText_100k);
//                                    etDetailTotal = inflatedDetailExtra.findViewById(R.id.editText_detailTotal);
//                                    etDetailTotal.setAsThousandSeparator();
//                                    tvTotal = inflatedDetailExtra.findViewById(R.id.textView_total);
//                                    tvTotal.setText("Total Extra");
                                    editTextsExtra = new ArrayList<>();
                                    editTextsExtra.add(et1kLogam);
                                    editTextsExtra.add(et500Logam);
                                    editTextsExtra.add(et1k);
                                    editTextsExtra.add(et2k);
                                    editTextsExtra.add(et5k);
                                    editTextsExtra.add(et10k);
                                    editTextsExtra.add(et20k);
                                    editTextsExtra.add(et50k);
                                    editTextsExtra.add(et100k);

                                    for (CustomEditText et : editTextsExtra) {
                                        et.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                calculateDetailTotal(editTextsExtra, /*etDetailTotal,*/ etExtraTotal);
                                            }
                                        });
                                    }

                                    llExtraPlaceHolder.addView(inflatedDetailExtra);

                                } else {
                                    llExtraPlaceHolder.setVisibility(View.GONE);
                                    if (!etExtraTotal.isEnabled()) etExtraTotal.setEnabled(true);

                                    llExtraPlaceHolder.removeAllViews();
                                    editTextsExtra.clear();

                                }
                            }
                        };


                        etFindId.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                countdownToFetchData.cancel();
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (cvInfoService.getVisibility() != View.VISIBLE)
                                    cvInfoService.setVisibility(View.VISIBLE);
                                tvLoading.setText("Mencari ID " + etFindId.getText().toString().toUpperCase() + " ...");
                                if (llServiceInfoFound.getVisibility() == View.VISIBLE)
                                    llServiceInfoFound.setVisibility(View.GONE);
                                if (tvIdNotFound.getVisibility() == View.VISIBLE)
                                    tvIdNotFound.setVisibility(View.GONE);
                                if (llProgressServiceHolder.getVisibility() != View.VISIBLE)
                                    llProgressServiceHolder.setVisibility(View.VISIBLE);
                                countdownToFetchData.start();
                            }
                        });

                        switchChurch.setOnCheckedChangeListener(checkedChangeListener);
                        switchWithDetail.setOnCheckedChangeListener(checkedChangeListener);
                        switchPelDetail.setOnCheckedChangeListener(checkedChangeListener);
                        switchPemDetail.setOnCheckedChangeListener(checkedChangeListener);
                        switchExtraDetail.setOnCheckedChangeListener(checkedChangeListener);

                        etPelTotal.setAsThousandSeparator();
                        etPemTotal.setAsThousandSeparator();
                        etExtraTotal.setAsThousandSeparator();

                        break;
                    case KEY_INCOME_SAMPUL_SYUKUR:
                        llDetails.setVisibility(View.VISIBLE);
                        warnAnonymity = true;
                        btnAddName.setOnClickListener(view -> {
                            Intent intent = new Intent(context, Adding.class);
                            intent.putExtra("request", "name");
                            startActivityForResult(intent, REQUEST_CODE_PERSONAL_NAME);
                        });

                        btnAddName.setAsAddingButton(container, inflater, llNamePlaceHolder, 10, () -> {
                            Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                            if (btnAddName.getSelectedList().isEmpty()) warnAnonymity = true;
                        });

                        btnAddDetails.setSelectedView(new ArrayList<>());
                        btnAddDetails.setOnClickListener(view -> {
//                            Inflate details layout
                            View v = inflater.inflate(R.layout.recycler_item_adding_details, llDetailsPlaceHolder, false);
                            CustomEditText etAmount = v.findViewById(R.id.recyclerItem_amount);
                            etAmount.setAsThousandSeparator();
                            etAmount.setAsNoLeadingZero();
                            v.findViewById(R.id.recyclerItem_delete).setOnClickListener(view1 -> {
                                Snackbar.make(rootView, "Perincian berhasil dihapus", Snackbar.LENGTH_LONG).show();
                                llDetailsPlaceHolder.removeView(v);
                                btnAddDetails.getSelectedView().remove(v);
                            });

                            btnAddDetails.addSelectedView(v);
                            llDetailsPlaceHolder.addView(v);

//                            llDetailsPlaceHolder.addView(btnAddDetails.getSelectedView().get(btnAddDetails.getSelectedView().size() - 1));
                        });

                        ArrayList<String> sampulSyukurType = new ArrayList<>();
                        sampulSyukurType.add("Pilih Jenis Sampul Syukur");
                        sampulSyukurType.add("Persembahan Perpuluhan");  // 0
                        sampulSyukurType.add("Persembahan Bulanan Tetap Keluarga"); // 1
                        sampulSyukurType.add("Leis / sampul pengucapan syukur"); // 2
                        sampulSyukurType.add("Sampul Syukur HUT Kelahiran"); // 3
                        sampulSyukurType.add("Sampul Syukur HUT Pernikahan"); // 4
                        sampulSyukurType.add("Sampul Syukur Pemberkatan Nikah"); // 5
                        sampulSyukurType.add("Sampul Syukur Baptisan"); // 6
                        sampulSyukurType.add("Perjamuan Kudus Jumat Agung"); // 7
                        sampulSyukurType.add("Perjamuan Kudus Biasa"); // 8
                        sampulSyukurType.add("Sampul Syukur Peneguhan Sidi"); // 9
                        sampulSyukurType.add("Sampul Syukur keluarga dalam Jemaat"); // 10
                        sampulSyukurType.add("Sampul Syukur keluarga luar Jemaat (tamu)"); // 11
                        sampulSyukurType.add("Sampul Syukur Paskah"); // 12
                        sampulSyukurType.add("Sampul Syukur HUT Pekabaran Injil & Pendidikan Kristen GMIM"); // 13
                        sampulSyukurType.add("Sampul Syukur GMIM Bersinode "); // 14
                        sampulSyukurType.add("Sampul Syukur Keluarga dalam Jemaat"); // 15
                        sampulSyukurType.add("Sampul Syukur Keluarga luar Jemaat (Tamu)"); // 16
                        sampulSyukurType.add("Sampul Syukur Akhir Tahun"); // 17
                        sampulSyukurType.add("Sampul Syukur Keluarga"); // 18
                        sampulSyukurType.add("Sampul Syukur Naik Kelas / Lulus / Wisuda"); // 19
                        sampulSyukurType.add("Sampul Syukur Rukun / Organisasi"); // 20

                        spinnerSampulSyukurType.setItems(sampulSyukurType);

                        spinnerSampulSyukurType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (sampulSyukurType.get(0).contains("Pilih")) {
                                sampulSyukurType.remove(0);
                                spinnerSampulSyukurType.setItems(Objects.requireNonNull(sampulSyukurType));
                                spinnerSampulSyukurType.setSelectedIndex(position - 1);
                            }
                        });

                        break;

                    case KEY_INCOME_LAINNYA:
//                        switchChurch.setVisibility(View.GONE);
                        llOther.setVisibility(View.VISIBLE);
                        warnAnonymity = false;

                        radioOtherArray = new ArrayList<>();
                        radioOtherArray.add("Bunga Simpanan"); // 0
                        radioOtherArray.add("Jasa Bank Lainnya"); // 1
                        radioOtherArray.add("Usaha Kolom"); // 2
                        radioOtherArray.add("Usaha BIPRA"); // 3
                        radioOtherArray.add("Rapat Badan Pekerja Sinode Lengkap (RBPSL)"); // 4
                        radioOtherArray.add("Sidang Sinode Istimewa (SSI)"); // 5
                        radioOtherArray.add("Partisipasi / Sumbangan"); // 6
                        radioOtherArray.add("Saldo Kas Pelayanan Tahun Sebelumnya"); // 7
                        radioOtherArray.add("Saldo Kas Pembangunan Tahun Sebelumnya"); // 8
                        radioOtherArray.add("Sumbangan Pemerintah / Lainnya"); // 9
                        radioOtherArray.add("Tak Terduga "); // 10

                        ArrayList<RadioButton> rbHolder = new ArrayList<>();

                        for (String s : radioOtherArray) {
                            RadioButton rb = new RadioButton(context);
                            rb.setText(s);
                            rb.setId(View.generateViewId());
                            rbHolder.add(rb);
                        }

                        for (RadioButton v : rbHolder) {
                            rgOther.addView(v);
                        }

                        break;
                    case KEY_INCOME_LAINNYA_NO_ACCOUNT:
                        etOther_Note.setVisibility(View.VISIBLE);
                        warnAnonymity = false;

                        break;
                }

            }
        }
        return rootView;
    }

    @OnClick(R.id.button_submit)
    void btnSubmit() {
        rootView.clearFocus();

        ////////////////////////////////////// index sensitive //////////////////////////////////////
        customEditTexts.add(etAmount);
        customEditTexts.add(etFindId);
        customEditTexts.add(etOtherDetail);
        customEditTexts.add(etOther_Note);
        customButtonAdds.add(btnAddName);
        customButtonAdds.add(btnAddDetails);
        materialSpinners.add(spinnerSampulSyukurType);
        radioGroups.add(rgOther);
        ////////////////////////////////////// index sensitive //////////////////////////////////////

        Validator validator = new Validator();
        // validate persembahan ibadah
        if (switchChurch.isChecked()) {
            if (switchWithDetail.isChecked()) {
                if (switchPelDetail.isChecked()) {
                    // validate pelayanan detail
                    if (validator.validateEditText_isEmpty(etPelTotal)) {
                        validator.displayErrorMessage(context, "Rincian Persembahan Pelayanan tidak valid");
                        return;
                    }
                } else {
                    // validate pelayanan total
                    if (validator.validateEditText_isLeadingZero(etPelTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pelayanan " + Validator.MESSAGE_ERROR_IS_LEADING_ZERO);
                        return;
                    }
                    if (validator.validateEditText_isEmpty(etPelTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pelayanan " + Validator.MESSAGE_ERROR_IS_EMPTY);
                        return;
                    }
                }

                if (switchPemDetail.isChecked()) {
                    // validate pembangunan detail
                    if (validator.validateEditText_isEmpty(etPemTotal)) {
                        validator.displayErrorMessage(context, "Rincian Persembahan Pembangunan tidak valid");
                        return;
                    }

                } else {
                    // validate pembangunan total
                    if (validator.validateEditText_isLeadingZero(etPemTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pembangunan " + Validator.MESSAGE_ERROR_IS_LEADING_ZERO);
                        return;
                    }
                    if (validator.validateEditText_isEmpty(etPemTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pembangunan " + Validator.MESSAGE_ERROR_IS_EMPTY);
                        return;
                    }
                }

                if (switchExtraDetail.isChecked()) {
                    // validate extra detail
                    if (validator.validateEditText_isEmpty(etExtraTotal)) {
                        validator.displayErrorMessage(context, "Rincian Persembahan Pundi Extra tidak boleh kosong");
                        return;
                    }
                } else {
                    // validate extra total
                    if (validator.validateEditText_isLeadingZero(etExtraTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pundi Extra " + Validator.MESSAGE_ERROR_IS_LEADING_ZERO);
                        return;
                    }
                    if (validator.validateEditText_isEmpty(etExtraTotal)) {
                        validator.displayErrorMessage(context, "Jumlah Persembahan Pundi Extra  " + Validator.MESSAGE_ERROR_IS_EMPTY);
                        return;
                    }
                }
            }
        }

        // validate amount edittext
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

        // validate "Service Data" if any record matching to "ID ibadah" text
        if (keyIssue.contentEquals(KEY_INCOME_PERSEMBAHAN_IBADAH)) {
            if (!isServiceDataFound) {
                validator.displayErrorMessage(context, "Data Ibadah belum valid, silahkan masukan ID ibadah yang valid dan mempunyai data ibadah");
                return;
            }
        }

        // validate "Name & Detail" in Sampul Syukur confirm if anonymous
        if (keyIssue.contentEquals(KEY_INCOME_SAMPUL_SYUKUR) && warnAnonymity) {
            if (validator.validateButton_isNotSelectingAnything(btnAddName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                String title = "Anda yakin ?";
                String msg = "Ingin melanjutkan pengajuan Sampul Syukur TANPA NAMA (NN)";

                if (validator.validateButton_isNotContainAnyView(btnAddDetails)) {
                    msg = "Ingin melanjutkan pengajuan Sampul Syukur TANPA NAMA (NN) dan TANPA PERINCIAN";
                }


                builder.setTitle(title);
                builder.setMessage(msg.concat("?\n\nSilahkan sentuh kembali tombol 'AJUKAN' untuk melanjutkan"));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", (dialogInterface, i) -> warnAnonymity = false);
                builder.create().show();

            }
        } else {

            // validate "Detail" in sampul Syukur
            if (!validator.validateButton_isNotContainAnyView(btnAddDetails)) {
                for (View v : btnAddDetails.getSelectedView()) {
                    CustomEditText etD = v.findViewById(R.id.recyclerItem_detail);
                    CustomEditText etA = v.findViewById(R.id.recyclerItem_amount);
                    if (validator.validateEditText_isEmpty(etD)) {
                        validator.displayErrorMessage(context, "Detail Perincian " + Validator.MESSAGE_ERROR_IS_EMPTY);
                        return;
                    }
                    if (validator.validateEditText_isEmpty(etA)) {
                        validator.displayErrorMessage(context, "Jumlah Detail Perincian " + Validator.MESSAGE_ERROR_IS_EMPTY);
                        return;
                    }
                    if (validator.validateEditText_isLeadingZero(etA)) {
                        validator.displayErrorMessage(context, "Jumlah Detail Perincian" + Validator.MESSAGE_ERROR_IS_LEADING_ZERO);
                        return;
                    }
                }
                // reset validator holder
                customEditTexts.clear();
                customButtonAdds.clear();
                materialSpinners.clear();
                radioGroups.clear();
            }

            Snackbar.make(rootView, " SUBMITING ...", Snackbar.LENGTH_SHORT).show();

            Log.e(TAG, "------------------------ LOG HEAD ------------------------");
            Log.e(TAG, "SUBMIT FRAGMENT INCOME : KEYBUNDLE -- " + keyIssue);

            Log.e(TAG, "PENERIMAAN DI GEREJA : " + switchChurch.isChecked());
            if (switchChurch.isChecked())
                POST_description = "Diterima di Gereja, ";
            else POST_description = "";

            Log.e(TAG, "Amount - " + etAmount.getText());

            switch (keyIssue) {
                case KEY_INCOME_PERSEMBAHAN_IBADAH:
                    Log.e(TAG, "Service ID: " + etFindId.getText());
                    Log.e(TAG, "Service Key Issue: " + keyIssue);
                    Log.e(TAG, "Atas Nama: " + tvInfoName.getText());
                    Log.e(TAG, "Tanggal: " + tvInfoDate.getText());
                    Log.e(TAG, "Khadim: " + tvInfoKhadim.getText());
                    Log.e(TAG, "Tempat: " + tvInfoPlace.getText());

                    if (switchWithDetail.isChecked()) {
                        Log.e(TAG, "Extra With Detail = " + switchWithDetail.isChecked());

                        Log.e(TAG, "Total Pelayanan Rp." + etPelTotal.getText());
                        Log.e(TAG, "Total Pembangunan Rp." + etPemTotal.getText());
                        Log.e(TAG, "Total Extra Rp." + etExtraTotal.getText());


                        String holderPel = ":" + etPelTotal.getText().toString().replace(",", "") + ":";
                        String holderPem = ":" + etPemTotal.getText().toString().replace(",", "") + ":";
                        String holderExt = ":" + etExtraTotal.getText().toString().replace(",", "") + ":";

                        if (switchPelDetail.isChecked()) {
                            Log.e(TAG, "Pelayanan Detail = " + switchPelDetail.isChecked());
                            Log.e(TAG, "--------------------- PELAYANAN DETAIL ---------------------");
                            String h = "";
                            for (int i = 0; i < editTextsPel.size(); i++) {

                                int quantity;
                                if (!editTextsPel.get(i).getText().toString().isEmpty()) {
                                    quantity = Integer.valueOf(editTextsPel.get(i).getText().toString());
                                } else quantity = 0;

                                h = h.concat("-").concat(String.valueOf(quantity)).concat("-");

                                switch (i) {
                                    case 0:
                                        Log.e(TAG, "Pel Rp.1.000 (Logam) x " + quantity);
                                        break;
                                    case 1:
                                        Log.e(TAG, "Pel Rp.500 (Logam) x " + quantity);
                                        break;
                                    case 2:
                                        Log.e(TAG, "Pel Rp.1.000 x " + quantity);
                                        break;
                                    case 3:
                                        Log.e(TAG, "Pel Rp.2.000 x " + quantity);
                                        break;
                                    case 4:
                                        Log.e(TAG, "Pel Rp.5.000 x " + quantity);
                                        break;
                                    case 5:
                                        Log.e(TAG, "Pel Rp.10.000 x " + quantity);
                                        break;
                                    case 6:
                                        Log.e(TAG, "Pel Rp.20.000 x " + quantity);
                                        break;
                                    case 7:
                                        Log.e(TAG, "Pel Rp.50.000 x " + quantity);
                                        break;
                                    case 8:
                                        Log.e(TAG, "Pel Rp.100.000 x " + quantity);
                                        break;
                                }
                            }
                            holderPel = ":" + h + ":";
                        }
                        if (switchPemDetail.isChecked()) {
                            Log.e(TAG, "Pembangunan Detail = " + switchPemDetail.isChecked());
                            Log.e(TAG, "--------------------- PEMBANGUNAN DETAIL ---------------------");
                            String h = "";
                            for (int i = 0; i < editTextsPem.size(); i++) {

                                int quantity;
                                if (!editTextsPem.get(i).getText().toString().isEmpty()) {
                                    quantity = Integer.valueOf(editTextsPem.get(i).getText().toString());
                                } else quantity = 0;
                                h = h.concat("-").concat(String.valueOf(quantity)).concat("-");

                                switch (i) {
                                    case 0:
                                        Log.e(TAG, "Pel Rp.1.000 (Logam) x " + quantity);
                                        break;
                                    case 1:
                                        Log.e(TAG, "Pel Rp.500 (Logam) x " + quantity);
                                        break;
                                    case 2:
                                        Log.e(TAG, "Pel Rp.1.000 x " + quantity);
                                        break;
                                    case 3:
                                        Log.e(TAG, "Pel Rp.2.000 x " + quantity);
                                        break;
                                    case 4:
                                        Log.e(TAG, "Pel Rp.5.000 x " + quantity);
                                        break;
                                    case 5:
                                        Log.e(TAG, "Pel Rp.10.000 x " + quantity);
                                        break;
                                    case 6:
                                        Log.e(TAG, "Pel Rp.20.000 x " + quantity);
                                        break;
                                    case 7:
                                        Log.e(TAG, "Pel Rp.50.000 x " + quantity);
                                        break;
                                    case 8:
                                        Log.e(TAG, "Pel Rp.100.000 x " + quantity);
                                        break;
                                }
                            }
                            holderPem = ":" + h + ":";

                        }
                        if (switchExtraDetail.isChecked()) {
                            Log.e(TAG, "Pundi Extra Detail = " + switchExtraDetail.isChecked());
                            Log.e(TAG, "--------------------- PUNDI EXTRA DETAIL ---------------------");
                            String h = "";

                            for (int i = 0; i < editTextsExtra.size(); i++) {

                                int quantity;
                                if (!editTextsExtra.get(i).getText().toString().isEmpty()) {
                                    quantity = Integer.valueOf(editTextsExtra.get(i).getText().toString());
                                } else quantity = 0;
                                h = h.concat("-").concat(String.valueOf(quantity)).concat("-");

                                switch (i) {
                                    case 0:
                                        Log.e(TAG, "Pel Rp.1.000 (Logam) x " + quantity);
                                        break;
                                    case 1:
                                        Log.e(TAG, "Pel Rp.500 (Logam) x " + quantity);
                                        break;
                                    case 2:
                                        Log.e(TAG, "Pel Rp.1.000 x " + quantity);
                                        break;
                                    case 3:
                                        Log.e(TAG, "Pel Rp.2.000 x " + quantity);
                                        break;
                                    case 4:
                                        Log.e(TAG, "Pel Rp.5.000 x " + quantity);
                                        break;
                                    case 5:
                                        Log.e(TAG, "Pel Rp.10.000 x " + quantity);
                                        break;
                                    case 6:
                                        Log.e(TAG, "Pel Rp.20.000 x " + quantity);
                                        break;
                                    case 7:
                                        Log.e(TAG, "Pel Rp.50.000 x " + quantity);
                                        break;
                                    case 8:
                                        Log.e(TAG, "Pel Rp.100.000 x " + quantity);
                                        break;
                                }
                            }
                            holderExt = ":" + h + ":";
                        }

                        POST_note = holderPel + holderPem + holderExt;
                    }

                    POST_description = POST_description.concat(keyIssue
                            + ", "
                            + tvInfoServiceType.getText().toString()
                            + " berjumlah Rp. "
                            + etAmount.getText().toString().replace(",", "."));

                    break;
                case KEY_INCOME_SAMPUL_SYUKUR:

                    Log.e(TAG, "NAMES count : " + btnAddName.getSelectedList().size());
                    int index = 1;
                    for (Adding_RecyclerModel model : btnAddName.getSelectedList()) {
                        Log.e(TAG, "NAMES " + index + " ID : " + model.getId());
                        Log.e(TAG, "NAMES " + index + " Name : " + model.getName());
                        Log.e(TAG, "NAMES " + index + " Kolom : " + model.getKolom());
                        Log.e(TAG, "NAMES " + index + " BOD : " + model.getBirthDate());
                        Log.e(TAG, "NAMES " + index + " Category : " + model.getCategory());
                        index++;
                    }
                    Log.e(TAG, "===========================================================================");
                    Log.e(TAG, "DETAILS count : " + btnAddDetails.getSelectedView().size());
                    index = 1;
                    for (View detailView : btnAddDetails.getSelectedView()) {
                        CustomEditText etDetail = detailView.findViewById(R.id.recyclerItem_detail), etAmount = detailView.findViewById(R.id.recyclerItem_amount);
                        Log.e(TAG, "DETAILS " + index + " Details : " + etDetail.getText());
                        Log.e(TAG, "DETAILS " + index + " Amount : " + etAmount.getText());
                        index++;
                    }

                    String s = spinnerSampulSyukurType.getItems().get(spinnerSampulSyukurType.getSelectedIndex()).toString();
                    if (s.contentEquals(spinnerSampulSyukurType.getItems().get(0).toString())) {
                        POST_accountNumberKey = "1.4.1";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(1).toString())) {
                        POST_accountNumberKey = "1.4.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(2).toString())) {
                        POST_accountNumberKey = "1.16.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(3).toString())) {
                        POST_accountNumberKey = "1.23.1";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(4).toString())) {
                        POST_accountNumberKey = "1.23.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(5).toString())) {
                        POST_accountNumberKey = "1.23.3";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(6).toString())) {
                        POST_accountNumberKey = "1.23.4";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(7).toString())) {
                        POST_accountNumberKey = "1.24.1";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(8).toString())) {
                        POST_accountNumberKey = "1.24.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(9).toString())) {
                        POST_accountNumberKey = "1.25";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(10).toString())) {
                        POST_accountNumberKey = "1.26.1";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(11).toString())) {
                        POST_accountNumberKey = "1.26.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(12).toString())) {
                        POST_accountNumberKey = "1.26.3";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(13).toString())) {
                        POST_accountNumberKey = "1.26.4";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(14).toString())) {
                        POST_accountNumberKey = "1.26.5";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(15).toString())) {
                        POST_accountNumberKey = "1.27.1";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(16).toString())) {
                        POST_accountNumberKey = "1.27.2";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(17).toString())) {
                        POST_accountNumberKey = "1.28";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(18).toString())) {
                        POST_accountNumberKey = "1.29";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(19).toString())) {
                        POST_accountNumberKey = "1.30";
                    } else if (s.contentEquals(spinnerSampulSyukurType.getItems().get(20).toString())) {
                        POST_accountNumberKey = "1.31";
                    }

                    POST_issuedMemberData = encodeMemberData(btnAddName);

                    String detailHolder = "";
                    for (View v : btnAddDetails.getSelectedView()) {
                        EditText etDetail = v.findViewById(R.id.recyclerItem_detail);
                        EditText etAmount = v.findViewById(R.id.recyclerItem_amount);

                        detailHolder = detailHolder
                                .concat(etDetail.getText().toString())
                                .concat(", Rp. ")
                                .concat(etAmount.getText().toString().replace(",", "."));

                    }
                    String nameHolder = "";
                    if (btnAddName.getSelectedList().size() > 1) {
                        for (Adding_RecyclerModel model : btnAddName.getSelectedList()) {
                            nameHolder = nameHolder.concat(model.getName()).concat(" ").concat(model.getKolom()).concat("&");
                        }
                    } else if (btnAddName.getSelectedList().size() == 1) {
                        Adding_RecyclerModel model = btnAddName.getSelectedList().get(0);
                        nameHolder = model.getName() + " " + model.getKolom();
                    }

                    POST_description = POST_description.concat(
                            keyIssue
                                    + ", "
                                    + spinnerSampulSyukurType.getItems().get(spinnerSampulSyukurType.getSelectedIndex()).toString() + " "
                                    + nameHolder
                                    + " "
                                    + detailHolder
                    );
                    break;
                case KEY_INCOME_LAINNYA:
                    Log.e(TAG, "Detail : " + etOtherDetail.getText().toString());
                    RadioButton selectedRadioButton = rgOther.findViewById(rgOther.getCheckedRadioButtonId());
                    Log.e(TAG, "SELECTED_OPTION : " + selectedRadioButton.getText());

                    String sel = selectedRadioButton.getText().toString();
                    if (sel.contentEquals(radioOtherArray.get(0)))
                        POST_accountNumberKey = "1.32.1";
                    else if (sel.contentEquals(radioOtherArray.get(1)))
                        POST_accountNumberKey = "1.32.2";
                    else if (sel.contentEquals(radioOtherArray.get(2)))
                        POST_accountNumberKey = "1.35";
                    else if (sel.contentEquals(radioOtherArray.get(3)))
                        POST_accountNumberKey = "1.36";
                    else if (sel.contentEquals(radioOtherArray.get(4)))
                        POST_accountNumberKey = "1.38";
                    else if (sel.contentEquals(radioOtherArray.get(5)))
                        POST_accountNumberKey = "1.39";
                    else if (sel.contentEquals(radioOtherArray.get(6)))
                        POST_accountNumberKey = "1.40";
                    else if (sel.contentEquals(radioOtherArray.get(7)))
                        POST_accountNumberKey = "1.44";
                    else if (sel.contentEquals(radioOtherArray.get(8)))
                        POST_accountNumberKey = "1.45";
                    else if (sel.contentEquals(radioOtherArray.get(9)))
                        POST_accountNumberKey = "1.46";
                    else if (sel.contentEquals(radioOtherArray.get(10)))
                        POST_accountNumberKey = "1.47";

                    POST_description = POST_description.concat(
                            keyIssue
                                    + " berupa : "
                                    + sel
                                    + ", berjumlah Rp. "
                                    + etAmount.getText().toString().replace(",", ".")
                                    + " " + etOtherDetail.getText().toString()
                    );

                    break;
                case KEY_INCOME_LAINNYA_NO_ACCOUNT:
                    Log.e(TAG, "NOTE : " + etOther_Note.getText().toString());

                    POST_accountNumberKey = "1.999";
                    POST_description = POST_description.concat(
                            keyIssue
                                    + ", berjumlah Rp. "
                                    + etAmount.getText().toString().replace(",", ".")
                                    + " " + etOther_Note.getText().toString()
                    );
                    break;
            }

            Log.e(TAG, "------------------------------------");
            Log.e(TAG, ":REQUEST TO SERVER:");
            Log.e(TAG, "------------------------------------");
            Log.e(TAG, "member_id: " + Guru.getInt(KEY_MEMBER_ID, 0));
            Log.e(TAG, "keyIssue: " + keyIssue);
            Log.e(TAG, "amount: " + etAmount.getText().toString().trim().replace(",", ""));
            Log.e(TAG, "accountNumberKey: " + POST_accountNumberKey);
            Log.e(TAG, "issuedMemberDataRaw: " + POST_issuedMemberData);
            Log.e(TAG, "note: " + POST_note);
            Log.e(TAG, "description: " + POST_description);
            Log.e(TAG, "related_service_id: " + POST_serviceId);

            IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);
            Call call = RetrofitClient.getInstance(null).getApiServices().setIssueFinancial(
                    Guru.getInt(KEY_MEMBER_ID, 0),
                    keyIssue,
                    etAmount.getText().toString().trim().replace(",", ""),
                    POST_accountNumberKey,
                    POST_issuedMemberData,
                    POST_note,
                    POST_description,
                    POST_serviceId
            );
            requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                @Override
                public void onTry() {
                    View progressHolder = rootView.findViewById(R.id.layout_progressHolder);
                    if (progressHolder.getVisibility() != View.VISIBLE)
                        progressHolder.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(APIWrapper res, String message) {

                    displayDialog(
                            context,
                            "Pengajuan Berhasil",
                            message,
                            true,
                            (dialogInterface, i) -> {
                            },
                            null,
                            dialogInterface -> getActivity().finish()
                    );
                }

                @Override
                public void onRetry() {
                    requestHandler.enqueue(call);
                }
            });
            requestHandler.enqueue(call);

        }
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

            if (requestCode == REQUEST_CODE_PERSONAL_NAME) {
                btnAddName.addSelected(selectedModel);
                warnAnonymity = false;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Minor function & Variable
    ///////////////////////////////////////////////////////////////////////////

    private void calculateDetailTotal(ArrayList<CustomEditText> editTexts,
//                                      CustomEditText totalDetail,
                                      CustomEditText mainEditText) {
        int result = 0;
        int amount;
        for (int i = 0; i < editTexts.size(); i++) {

            if (editTexts.get(i).getText().toString().isEmpty()) amount = 0;
            else amount = Integer.valueOf(editTexts.get(i).getText().toString());


            switch (i) {
                case 0:
                    result += 1000 * amount;
                    break;
                case 1:
                    result += 500 * amount;
                    break;
                case 2:
                    result += 1000 * amount;
                    break;
                case 3:
                    result += 2000 * amount;
                    break;
                case 4:
                    result += 5000 * amount;
                    break;
                case 5:
                    result += 10000 * amount;
                    break;
                case 6:
                    result += 20000 * amount;
                    break;
                case 7:
                    result += 50000 * amount;
                    break;
                case 8:
                    result += 100000 * amount;
                    break;
            }
        }
        if (result == 0) {
//            totalDetail.setText("");
            mainEditText.setText("");
        } else {
//            totalDetail.setText(String.valueOf(result));
            mainEditText.setText(String.valueOf(result));
        }
        int a;
        if (etPelTotal.getText().toString().isEmpty()) {
            a = 0;
        } else {
            a = Integer.valueOf(etPelTotal.getText().toString().replace(",", ""));
        }

        int b;
        if (etPemTotal.getText().toString().isEmpty()) {
            b = 0;
        } else {
            b = Integer.valueOf(etPemTotal.getText().toString().replace(",", ""));
        }

        int c;
        if (etExtraTotal.getText().toString().isEmpty()) {
            c = 0;
        } else {
            c = Integer.valueOf(etExtraTotal.getText().toString().replace(",", ""));
        }

        etAmount.setText(String.valueOf(a + b + c));
    }

    private void defaultCase() {
        if (switchChurch.isChecked()) switchChurch.setChecked(false);
        etOther_Note.setVisibility(View.GONE);
        etOther_Note.setText("");
        switchChurch.setVisibility(View.GONE);
    }

    private CountDownTimer countdownToFetchData = new CountDownTimer(1500, 1000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            findId();
        }
    };

    private void findId() {
        if (Objects.requireNonNull(etFindId.getText()).toString().length() > 0) {

            Log.e(TAG, "findServiceId: " + etFindId.getText().toString());
            ApiServices apiServices = RetrofitClient.getInstance(null).getApiServices();
            Call<ResponseBody> call = apiServices.findService(etFindId.getText().toString().toUpperCase());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                        APIWrapper res = APIUtils.parseWrapper(response.body());
                        if (!res.isError()) {

                            if (tvIdNotFound.getVisibility() == View.VISIBLE)
                                tvIdNotFound.setVisibility(View.GONE);
                            if (llServiceInfoFound.getVisibility() != View.VISIBLE)
                                llServiceInfoFound.setVisibility(View.VISIBLE);

                            try {
                                JSONObject obj = res.getData();
                                tvInfoServiceType.setText(obj.getString("key_issue"));
                                tvInfoServiceId.setText(String.format("ID: %s", obj.getString("service_entry_id")));
                                tvInfoName.setText(obj.getString("first_member_name"));
                                tvInfoDate.setText(obj.getString("date"));
                                tvInfoKhadim.setText(obj.getString("khadim_name"));
                                tvInfoPlace.setText(obj.getString("place"));
                                POST_accountNumberKey = obj.getString("financial_account_number");
                                POST_serviceId = obj.getInt("service_id");

                                isServiceDataFound = true;

                            } catch (JSONException e) {
                                Log.e(TAG, "onResponse: JSON ERROR = " + e.getMessage());
                                e.printStackTrace();
                            }


                            Log.e(TAG, "onResponse:"
                                    + context.getClass().getSimpleName()
                                    + ": response return SUCCESS proceeding"
                                    + " message: " + res.getMessage());

                        } else {

                            if (llServiceInfoFound.getVisibility() == View.VISIBLE)
                                llServiceInfoFound.setVisibility(View.GONE);

                            if (llProgressServiceHolder.getVisibility() == View.VISIBLE)
                                llProgressServiceHolder.setVisibility(View.GONE);

                            if (tvIdNotFound.getVisibility() != View.VISIBLE) {
                                tvIdNotFound.setVisibility(View.VISIBLE);
                                tvIdNotFound.setText(res.getMessage());
                            }
                            isServiceDataFound = false;

                            Log.e(TAG, "onResponse:"
                                    + context.getClass().getSimpleName()
                                    + ": response return SUCCESS but Error = true, with message = "
                                    + res.getMessage());
                        }

                    } else {
                        isServiceDataFound = false;
                        APIUtils.parseError(context, response);
                    }

                    if (llProgressServiceHolder.getVisibility() == View.VISIBLE)
                        llProgressServiceHolder.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (llServiceInfoFound.getVisibility() == View.VISIBLE)
                        llServiceInfoFound.setVisibility(View.GONE);

                    if (llProgressServiceHolder.getVisibility() == View.VISIBLE)
                        llProgressServiceHolder.setVisibility(View.GONE);

                    if (tvIdNotFound.getVisibility() == View.VISIBLE)
                        tvIdNotFound.setVisibility(View.GONE);

                    if (llConnectionProblem.getVisibility() != View.VISIBLE)
                        llConnectionProblem.setVisibility(View.VISIBLE);

                }
            });

        } else cvInfoService.setVisibility(View.GONE);

    }


}
