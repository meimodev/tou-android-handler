package com.meimodev.sitouhandler.Issue.FragmentServices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomButtonAdd;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.Validator;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import retrofit2.Call;

import static com.meimodev.sitouhandler.Constant.*;

public class Fragment_Services extends Fragment {

    private View rootView;
    private Context context;
    private String keyIssue;

    private final String TAG = "Fragment_Service: ";

    @Nullable
    @BindView(R.id.button_addPriest)
    CustomButtonAdd btnAddPriest;
    @Nullable
    @BindView(R.id.linearLayout_priestPlaceHolder)
    LinearLayout llPriestPlaceHolder;
    @Nullable
    @BindView(R.id.button_addName)
    CustomButtonAdd btnAddName;
    @Nullable
    @BindView(R.id.linearLayout_namePlaceHolder)
    LinearLayout llNamePlaceHolder;

    @Nullable
    @BindView(R.id.tv_Title)
    TextView tvTitle;

    @Nullable
    @BindView(R.id.editText_date)
    CustomEditText etDate;
    @Nullable
    @BindView(R.id.editText_time)
    CustomEditText etTime;
    @Nullable
    @BindView(R.id.editText_place)
    CustomEditText etPlace;

    @Nullable
    @BindView(R.id.spinner_BIPRA)
    MaterialSpinner spinnerBIPRA;
    @Nullable
    @BindView(R.id.spinner_level)
    MaterialSpinner spinnerLevel;
    @Nullable
    @BindView(R.id.spinner_familyServiceType)
    MaterialSpinner spinnerFamilyServiceType;
    @Nullable
    @BindView(R.id.spinner_hariRayaType)
    MaterialSpinner spinnerHariRayaType;
    @Nullable
    @BindView(R.id.spinner_specialType)
    MaterialSpinner spinnerSpecialType;
    @Nullable
    @BindView(R.id.spinner_otherType)
    MaterialSpinner spinnerOtherType;
    @Nullable
    @BindView(R.id.spinner_sundayServiceTime)
    MaterialSpinner spinnerSundayServiceTime;

    @Nullable
    @BindView(R.id.radio_group_HUT)
    RadioGroup rgHUT;

    @Nullable
    @BindView(R.id.editText_note)
    CustomEditText etNote;

    @Nullable
    @BindView(R.id.layout_sundayService)
    LinearLayout llSundayService;
    @Nullable
    @BindView(R.id.layout_coordinatorPlaceHolder)
    LinearLayout llCoordinatorPlaceHolder;
    @Nullable
    @BindView(R.id.button_coordinator)
    CustomButtonAdd btnCoordinator;
    @Nullable
    @BindView(R.id.spinner_onDuty)
    MaterialSpinner spinnerDuty;
    @Nullable
    @BindView(R.id.spinner_guess)
    MaterialSpinner spinnerGuess;
    @Nullable
    @BindView(R.id.layout_penerimaHolder)
    LinearLayout llPenerimaHolder;
    @Nullable
    @BindView(R.id.layout_petugasHolder)
    LinearLayout llPetugasHolder;

    private final int REQUEST_CODE_PERSONAL_NAME = 1;
    private final int REQUEST_CODE_PRIEST_NAME = 2;
    private final int REQUEST_CODE_COORDINATOR_NAME = 3;

    private ArrayList<CustomEditText> customEditTexts = new ArrayList<>();
    private ArrayList<CustomButtonAdd> customButtonAdds = new ArrayList<>();
    private ArrayList<MaterialSpinner> materialSpinners = new ArrayList<>();
    private ArrayList<RadioGroup> radioGroups = new ArrayList<>();
    private String POST_issuedMemberData = "";
    private String POST_date = "";
    private String POST_time = "";
    private String POST_place = "";
    private String POST_note = "";
    private String POST_khadimId = "";
    private String POST_financialAccountNumber = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            keyIssue = getArguments().getString(KEY_SERVICE);
            rootView = inflater.inflate(R.layout.fragment_issue_service, container, false);
            context = rootView.getContext();

            ButterKnife.bind(this, rootView);

            if (keyIssue != null) {
                String title;
                title = keyIssue.contains("Ibadah") ? "Pengajuan " + keyIssue : "Pengajuan Ibadah" + keyIssue;
                tvTitle.setText(title);

                etDate.setAsDatePicker(getFragmentManager());
                etTime.setAsTimePicker(getFragmentManager());

                btnAddPriest.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Adding.class);
                    intent.putExtra("request", "priest");
                    startActivityForResult(intent, REQUEST_CODE_PRIEST_NAME);
                });

                btnAddName.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Adding.class);
                    intent.putExtra("request", "name");
                    startActivityForResult(intent, REQUEST_CODE_PERSONAL_NAME);
                });

                btnAddPriest.setAsAddingButton(container, inflater, llPriestPlaceHolder, 1, () -> {
                    Snackbar.make(rootView, "Khadim Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                });

                btnAddName.setAsAddingButton(container, inflater, llNamePlaceHolder, 1, () -> {
                    Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                });

                switch (keyIssue) {
                    case KEY_SERVICE_KOLOM:
                        break;
                    case KEY_SERVICE_BIPRA:

                        spinnerLevel.setVisibility(View.VISIBLE);
                        ArrayList<String> levelList = new ArrayList<>();
                        levelList.add("Pilih Jangkauan Ibadah : ");
                        levelList.add("Jemaat");
                        levelList.add("Koordinator");
                        levelList.add("Kolom");
                        spinnerLevel.setItems(levelList);
                        spinnerLevel.setOnItemSelectedListener((view, position, id, item) -> {
                            if (levelList.get(0).contains("Pilih")) {
                                levelList.remove(0);
                                spinnerLevel.setItems(Objects.requireNonNull(levelList));
                                spinnerLevel.setSelectedIndex(position - 1);
                            }
                        });

                        spinnerBIPRA.setVisibility(View.VISIBLE);
                        ArrayList<String> BIPRAlist = new ArrayList<>();
                        BIPRAlist.add("Pilih BIPRA : ");
                        BIPRAlist.add("Pria / Kaum Bapa");
                        BIPRAlist.add("Wanita / Kaum Ibu");
                        BIPRAlist.add("Pemuda");
                        BIPRAlist.add("Remaja");
                        BIPRAlist.add("Anak");
                        spinnerBIPRA.setItems(BIPRAlist);
                        spinnerBIPRA.setOnItemSelectedListener((view, position, id, item) -> {
                            if (BIPRAlist.get(0).contains("Pilih")) {
                                BIPRAlist.remove(0);
                                spinnerBIPRA.setItems(Objects.requireNonNull(BIPRAlist));
                                spinnerBIPRA.setSelectedIndex(position - 1);
                            }
                        });

                        break;
                    case KEY_SERVICE_HUT:

                        rgHUT.setVisibility(View.VISIBLE);
                        rgHUT.setOnCheckedChangeListener(((radioGroup, i) -> {
                            int checkedId = radioGroup.getCheckedRadioButtonId();
                            switch (checkedId) {
                                case R.id.radio_hut_pribadi:
                                    Toast.makeText(context, "pribadi", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.radio_hut_nikah:
                                    Toast.makeText(context, "nikah", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }));

                        break;
                    case KEY_SERVICE_PEMAKAMAN:

                        break;
                    case KEY_SERVICE_PERINGATAN:

                        break;
                    case KEY_SERVICE_KELUARGA:
                        spinnerFamilyServiceType.setVisibility(View.VISIBLE);

                        ArrayList<String> fServiceType = new ArrayList<>();
                        fServiceType.add("Pilih Jenis Ibadah Keluarga : ");
                        fServiceType.add("Ibadah Syukur Keluarga"); // 0
                        fServiceType.add("Ibadah Peletakan Batu Pertama Bangunan"); //1
                        fServiceType.add("Ibadah Pentahbisan Rumah"); // 2
                        fServiceType.add("Ibadah Penghiburan/Makan Kasih"); //3
                        fServiceType.add("Ibadah Pertunangan"); // 4
                        fServiceType.add("Ibadah Kelengkapan Baptisan"); //5
                        spinnerFamilyServiceType.setItems(fServiceType);
                        spinnerFamilyServiceType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (fServiceType.get(0).contains("Pilih")) {
                                fServiceType.remove(0);
                                spinnerFamilyServiceType.setItems(Objects.requireNonNull(fServiceType));
                                spinnerFamilyServiceType.setSelectedIndex(position - 1);
                            }
                        });

                        break;
                    case KEY_SERVICE_HARI_RAYA:
                        spinnerHariRayaType.setVisibility(View.VISIBLE);
                        ArrayList<String> hrType = new ArrayList<>();
                        hrType.add("Pilih Jenis Hari Raya : ");
                        hrType.add("Ibadah Tahun Baru I"); // 0
                        hrType.add("Ibadah Tahun Baru II"); // 1
                        hrType.add("Ibadah Hari Jumat Agung"); // 2
                        hrType.add("Ibadah Hari Paskah"); // 3
                        hrType.add("Ibadah Hari Kenaikan"); // 4
                        hrType.add("Ibadah Hari Pentakosta"); // 5
                        hrType.add("Ibadah Hari Natal I"); // 6
                        hrType.add("Ibadah Hari Natal II"); // 7
                        hrType.add("Ibadah Akhir Tahun"); // 8
                        hrType.add("Ibadah Pra Natal Jemaat"); // 9
                        hrType.add("Ibadah Pra Natal Kolom"); // 10
                        hrType.add("Ibadah Pra Natal BIPRA Jemaat"); // 11
                        hrType.add("Ibadah Pra Natal BIPRA Kolom"); // 12
                        hrType.add("Ibadah Pra Natal Keluarga Pelayan"); // 13
                        hrType.add("Ibadah Pra Natal Komisi Kerja"); // 14
                        hrType.add("Ibadah Pra Natal Rukun Keluarga"); // 15
                        hrType.add("Ibadah Pra Natal Rukun Fungsional"); // 16
                        hrType.add("Ibadah Pra Natal Rukun / Organisasi Lainnya"); // 17
                        hrType.add("Ibadah Pra Natal Instansi / Jawatan"); // 18
                        spinnerHariRayaType.setItems(hrType);
                        spinnerHariRayaType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (hrType.get(0).contains("Pilih")) {
                                hrType.remove(0);
                                spinnerHariRayaType.setItems(Objects.requireNonNull(hrType));
                                spinnerHariRayaType.setSelectedIndex(position - 1);
                            }
                        });


                        break;
                    case KEY_SERVICE_SPECIAL:

                        spinnerSpecialType.setVisibility(View.VISIBLE);
                        ArrayList<String> spType = new ArrayList<>();
                        spType.add("Pilih Jenis Ibadah Khusus");
                        spType.add("Ibadah HUT Kemerdekaan RI"); // 0
                        spType.add("Ibadah HUT GPI"); // 1
                        spType.add("Ibadah HUT Sinode Am Sulutteng"); // 2
                        spType.add("Ibadah HUT PGI / Hari Oikumene"); // 3
                        spType.add("Ibadah HUT PI/Pendidikan Kristen GMIM"); // 4
                        spType.add("Ibadah HUT Hari Alkitab"); // 5
                        spType.add("Ibadah HUT Hari Doa Sedunia"); // 6
                        spType.add("Ibadah Minggu Ke III (Pelayanan Wilayah)"); // 7
                        spType.add("Ibadah HUT GMIM Bersinode"); // 8
                        spType.add("Ibadah HUT Berdirinya Jemaat"); // 9

                        spinnerSpecialType.setItems(spType);
                        spinnerSpecialType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (spType.get(0).contains("Pilih")) {
                                spType.remove(0);
                                spinnerSpecialType.setItems(Objects.requireNonNull(spType));
                                spinnerSpecialType.setSelectedIndex(position - 1);
                            }
                        });


                        break;
                    case KEY_SERVICE_LAIN:

                        etNote.setVisibility(View.VISIBLE);
                        spinnerOtherType.setVisibility(View.VISIBLE);

                        ArrayList<String> otherType = new ArrayList<>();

                        otherType.add("Pilih Jenis Ibadah Lainnya : ");
                        otherType.add("Ibadah Komisi PDP"); // 0
                        otherType.add("Ibadah Komisi Diakonia"); // 1
                        otherType.add("Ibadah Komisi Kesenian"); // 2
                        otherType.add("Ibadah Rukun Keluarga"); // 3
                        otherType.add("Ibadah Rukun Organisasi"); // 4
                        otherType.add("Ibadah Salinan"); // 5
                        otherType.add("Ibadah Lansia"); // 6
                        otherType.add("Ibadah PAS / Konven Pelayan Khusus"); // 7
                        otherType.add("Ibadah Rapat BPMJ / Sidang MJ"); // 8
                        otherType.add("Ibadah Sidang Tahunan MJ"); // 9
                        otherType.add("Ibadah Perjamuan Kudus Jumat Agung"); // 10
                        otherType.add("Ibadah Perjamuan Kudus Biasa"); // 11
                        otherType.add("Ibadah Peneguhan Pernikahan"); // 12
                        otherType.add("Ibadah Katekisasi"); // 13
                        otherType.add("Ibadah Pengucapan Syukur Jemaat Minggu"); // 14
                        otherType.add("Ibadah Inatura"); // 15
                        otherType.add("Ibadah KPI"); // 16
                        otherType.add("Ibadah Puasa Diakonal Jemaat"); // 17
                        otherType.add("Ibadah Puasa Diakonal Kolom"); // 18
                        otherType.add("Ibadah Puasa Diakonal BIPRA"); // 19

                        spinnerOtherType.setItems(otherType);
                        spinnerOtherType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (otherType.get(0).contains("Pilih")) {
                                otherType.remove(0);
                                spinnerOtherType.setItems(Objects.requireNonNull(otherType));
                                spinnerOtherType.setSelectedIndex(position - 1);
                            }

                        });

                        break;
                    case KEY_SERVICE_SPECIAL_IBADAH_MINGGU:
                        llSundayService.setVisibility(View.VISIBLE);
                        btnAddName.setVisibility(View.GONE);
                        spinnerSundayServiceTime.setVisibility(View.VISIBLE);
                        ArrayList<String> hTime = new ArrayList<>();
                        hTime.add("Pilih Jenis Ibadah Minggu");
                        hTime.add("Subuh");
                        hTime.add("Pagi");
                        hTime.add("Malam");
                        spinnerSundayServiceTime.setItems(hTime);
                        spinnerSundayServiceTime.setOnItemSelectedListener((view, position, id, item) -> {
                            if (hTime.get(0).contains("Pilih")) {
                                hTime.remove(0);
                                spinnerSundayServiceTime.setItems(Objects.requireNonNull(hTime));
                                spinnerSundayServiceTime.setSelectedIndex(position - 1);
                            }
                        });

                        ArrayList<String> holderDuty = new ArrayList<>();
                        holderDuty.add("Pilih Petugas");
                        holderDuty.add("Kolom 1");
                        holderDuty.add("Kolom 2");
                        holderDuty.add("Kolom 3");
                        holderDuty.add("Kolom 4");
                        holderDuty.add("Kolom 5");
                        holderDuty.add("Kolom 6");
                        holderDuty.add("Kolom 7");
                        spinnerDuty.setItems(holderDuty);

                        spinnerDuty.setOnItemSelectedListener((view, position, id, item) -> {
                            if (holderDuty.get(0).contains("Pilih")) {
                                holderDuty.remove(0);
                                spinnerDuty.setItems(Objects.requireNonNull(holderDuty));
                                spinnerDuty.setSelectedIndex(position - 1);
                            }

                        });

                        ArrayList<String> holderGuess = new ArrayList<>();
                        holderGuess.add("Pilih Penerima Tamu");
                        holderGuess.add("Kolom 1");
                        holderGuess.add("Kolom 2");
                        holderGuess.add("Kolom 3");
                        holderGuess.add("Kolom 4");
                        holderGuess.add("Kolom 5");
                        holderGuess.add("Kolom 6");
                        holderGuess.add("Kolom 7");
                        spinnerGuess.setItems(holderGuess);

                        spinnerGuess.setOnItemSelectedListener((view, position, id, item) -> {
                            if (holderGuess.get(0).contains("Pilih")) {
                                holderGuess.remove(0);
                                spinnerGuess.setItems(Objects.requireNonNull(holderGuess));
                                spinnerGuess.setSelectedIndex(position - 1);
                            }

                        });

                        btnCoordinator.setOnClickListener(view -> {
                            Intent intent = new Intent(context, Adding.class);
                            intent.putExtra("request", "name");
                            startActivityForResult(intent, REQUEST_CODE_COORDINATOR_NAME);
                        });

                        btnCoordinator.setAsAddingButton(container, inflater, llCoordinatorPlaceHolder, 1, () -> {
                            Snackbar.make(rootView, "Koordinator Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                        });

                        llPenerimaHolder.setOnClickListener(view -> spinnerGuess.expand());
                        llPetugasHolder.setOnClickListener(view -> spinnerDuty.expand());

                        break;
                }
            }
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        rootView.clearFocus();
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
                case REQUEST_CODE_PRIEST_NAME:
                    selectedModel.setCategory("Khadim : ");
                    btnAddPriest.addSelected(selectedModel);
                    break;
                case REQUEST_CODE_PERSONAL_NAME:
                    selectedModel.setCategory("Atas Nama : ");
                    btnAddName.addSelected(selectedModel);
                    break;
                case REQUEST_CODE_COORDINATOR_NAME:
                    selectedModel.setCategory("Koordinator Ibadah");
                    btnCoordinator.addSelected(selectedModel);
            }
        }
    }

    @Optional
    @OnClick(R.id.btn_submit)
    void btnSubmit() {
        rootView.clearFocus();

//        Validate all input
//        if validation error, show popup dialog about the error
//        if validation ok, continue

        ////////////////////////////////////// index sensitive //////////////////////////////////////
        customEditTexts.add(etDate); // 0
        customEditTexts.add(etTime); // 1
        customEditTexts.add(etPlace); // 2
        customEditTexts.add(etNote); // 3
        customButtonAdds.add(btnAddPriest); // 0
        customButtonAdds.add(btnAddName); // 1
        customButtonAdds.add(btnCoordinator); // 2
        materialSpinners.add(spinnerBIPRA);// 0
        materialSpinners.add(spinnerDuty);// 1
        materialSpinners.add(spinnerGuess);// 2
        materialSpinners.add(spinnerLevel);// 3
        materialSpinners.add(spinnerFamilyServiceType); //4
        materialSpinners.add(spinnerHariRayaType); // 5
        materialSpinners.add(spinnerSpecialType); // 6
        materialSpinners.add(spinnerOtherType); // 7
        materialSpinners.add(spinnerSundayServiceTime); // 8
        radioGroups.add(rgHUT);
        ////////////////////////////////////// index sensitive //////////////////////////////////////

        Validator validator = new Validator();
        Map<String, String> validation = validator.validateInput(keyIssue, customButtonAdds, customEditTexts, materialSpinners, radioGroups);

        if (keyIssue.contentEquals(KEY_SERVICE_SPECIAL_IBADAH_MINGGU)) {
            if (spinnerSundayServiceTime != null && validator.validateSpinner_isItemNotSelected(spinnerSundayServiceTime)) {
                validator.displayErrorMessage(context, "Silahkan pilih jenis ibadah minggu");
                return;
            }

            if (btnAddName != null && validator.validateButton_isNotSelectingAnything(btnAddPriest)) {
                validator.displayErrorMessage(context, "Silahkan pilih Khadim yang akan diajukan");
                return;
            }
            if (etDate != null && validator.validateEditText_isEmpty(etDate)) {
                validator.displayErrorMessage(context, "Silahkan pilih Tanggal pelaksanaan");
                return;
            }
            if (etTime != null && validator.validateEditText_isEmpty(etTime)) {
                validator.displayErrorMessage(context, "Silahkan pilih Waktu pelaksanaan");
                return;
            }
            if (etPlace != null && validator.validateEditText_isEmpty(etPlace)) {
                validator.displayErrorMessage(context, "Silahkan pilih Tempat pelaksanaan");
                return;
            }
            if (btnCoordinator != null && validator.validateButton_isNotSelectingAnything(btnCoordinator)) {
                validator.displayErrorMessage(context, "Silahkan pilih Koordinator Ibadah");
                return;
            }
            if (spinnerGuess != null && validator.validateSpinner_isItemNotSelected(spinnerGuess)) {
                validator.displayErrorMessage(context, "Silahkan pilih Kolom penerima tamu");
                return;
            }
            if (spinnerDuty != null && validator.validateSpinner_isItemNotSelected(spinnerDuty)) {
                validator.displayErrorMessage(context, "Silahkan pilih Kolom yang bertugas");
                return;
            }

        }

        if (validation.get("result").contentEquals("ERROR")) {
            validator.displayErrorMessage(context, validation.get("message"));
            customEditTexts.clear();
            customButtonAdds.clear();
            materialSpinners.clear();
            radioGroups.clear();
            return;
        }

        Log.e(TAG, "------------------------ LOG HEAD ------------------------");
        Log.e(TAG, "SUBMIT FRAGMENT INCOME : KEYBUNDLE -- Ibadah " + keyIssue);

        int index;

        switch (keyIssue) {
            case KEY_SERVICE_KOLOM:
                POST_financialAccountNumber = "1.6";
                break;
            case KEY_SERVICE_BIPRA:
                String selectedBIPRA = spinnerBIPRA.getItems().get(spinnerBIPRA.getSelectedIndex()).toString();
                String selectedArea = spinnerLevel.getItems().get(spinnerLevel.getSelectedIndex()).toString();

                Log.e(TAG, "BIPRA Area = " + selectedArea);
                Log.e(TAG, " BIPRA = " + selectedBIPRA);
                POST_note = "Ibadah " + selectedBIPRA;

                if (selectedArea.contentEquals("Jemaat")) {

                    if (selectedBIPRA.contains("Bapa")) {
                        POST_financialAccountNumber = "1.5.1.1";
                    } else if (selectedBIPRA.contains("Ibu")) {
                        POST_financialAccountNumber = "1.5.2.1";
                    } else if (selectedBIPRA.contains("Pemuda")) {
                        POST_financialAccountNumber = "1.5.3.1";
                    } else if (selectedBIPRA.contains("Remaja")) {
                        POST_financialAccountNumber = "1.5.4.1";
                    } else if (selectedBIPRA.contains("Anak")) {
                        POST_financialAccountNumber = "1.5.5.1";
                    }

                    POST_note = POST_note + " Jemaat";

                } else if (selectedArea.contentEquals("Koordinator")) {

                    if (selectedBIPRA.contains("Bapa")) {
                        POST_financialAccountNumber = "1.5.1.2";
                    } else if (selectedBIPRA.contains("Ibu")) {
                        POST_financialAccountNumber = "1.5.2.2";
                    } else if (selectedBIPRA.contains("Pemuda")) {
                        POST_financialAccountNumber = "1.5.3.2";
                    } else if (selectedBIPRA.contains("Remaja")) {
                        POST_financialAccountNumber = "1.5.4.2";
                    } else if (selectedBIPRA.contains("Anak")) {
                        POST_financialAccountNumber = "1.5.5.2";
                    }

                    POST_note = POST_note + " Koordinator";

                } else if (selectedArea.contentEquals("Kolom")) {

                    if (selectedBIPRA.contains("Bapa")) {
                        POST_financialAccountNumber = "1.5.1.3";
                    } else if (selectedBIPRA.contains("Ibu")) {
                        POST_financialAccountNumber = "1.5.2.3";
                    } else if (selectedBIPRA.contains("Pemuda")) {
                        POST_financialAccountNumber = "1.5.3.3";
                    } else if (selectedBIPRA.contains("Remaja")) {
                        POST_financialAccountNumber = "1.5.4.3";
                    } else if (selectedBIPRA.contains("Anak")) {
                        POST_financialAccountNumber = "1.5.5.3";
                    }

                    POST_note = POST_note + " Kolom";
                }

                break;
            case KEY_SERVICE_HUT:

                String selectedHUT = ((RadioButton) rgHUT.findViewById(rgHUT.getCheckedRadioButtonId())).getText().toString();
                Log.e(TAG, "SELECTED HUT = " + selectedHUT);

                if (selectedHUT.contains("Pribadi")) {
                    POST_financialAccountNumber = "1.7.1";
                } else if (selectedHUT.contains("Pernikahan")) {
                    POST_financialAccountNumber = "1.7.2";
                }

                break;
            case KEY_SERVICE_PEMAKAMAN:
                POST_financialAccountNumber = "1.19.9";
                break;
            case KEY_SERVICE_PERINGATAN:
                break;
            case KEY_SERVICE_KELUARGA:
                String sFamilyService = spinnerFamilyServiceType.getItems().get(spinnerFamilyServiceType.getSelectedIndex()).toString();
                Log.e(TAG, "SELECTED Family Service Type = " + sFamilyService);

                if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(0).toString())) {
                    POST_financialAccountNumber = "1.7.4.1";
                } else if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(1).toString())) {
                    POST_financialAccountNumber = "1.7.4.2";
                } else if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(2).toString())) {
                    POST_financialAccountNumber = "1.7.4.3";
                } else if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(3).toString())) {
                    POST_financialAccountNumber = "1.7.4.4";
                } else if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(4).toString())) {
                    POST_financialAccountNumber = "1.7.4.5";
                } else if (sFamilyService.contentEquals(spinnerFamilyServiceType.getItems().get(5).toString())) {
                    POST_financialAccountNumber = "1.7.4.6";
                }

                break;
            case KEY_SERVICE_HARI_RAYA:

                String sHarRaya = spinnerHariRayaType.getItems().get(spinnerHariRayaType.getSelectedIndex()).toString();
                Log.e(TAG, "SELECTED Hari Raya Type = " + sHarRaya);

                if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(0).toString())) {
                    POST_financialAccountNumber = "1.8.1";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(1).toString())) {
                    POST_financialAccountNumber = "1.8.2";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(2).toString())) {
                    POST_financialAccountNumber = "1.8.3";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(3).toString())) {
                    POST_financialAccountNumber = "1.8.4";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(4).toString())) {
                    POST_financialAccountNumber = "1.8.5";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(5).toString())) {
                    POST_financialAccountNumber = "1.8.6";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(6).toString())) {
                    POST_financialAccountNumber = "1.8.7";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(7).toString())) {
                    POST_financialAccountNumber = "1.8.8";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(8).toString())) {
                    POST_financialAccountNumber = "1.8.9";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(9).toString())) {
                    POST_financialAccountNumber = "1.18.1";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(10).toString())) {
                    POST_financialAccountNumber = "1.18.2";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(11).toString())) {
                    POST_financialAccountNumber = "1.18.3";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(12).toString())) {
                    POST_financialAccountNumber = "1.18.4";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(13).toString())) {
                    POST_financialAccountNumber = "1.18.5";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(14).toString())) {
                    POST_financialAccountNumber = "1.18.6";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(15).toString())) {
                    POST_financialAccountNumber = "1.18.7";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(16).toString())) {
                    POST_financialAccountNumber = "1.18.8";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(17).toString())) {
                    POST_financialAccountNumber = "1.18.9";
                } else if (sHarRaya.contentEquals(spinnerHariRayaType.getItems().get(18).toString())) {
                    POST_financialAccountNumber = "1.18.10";
                }

                break;
            case KEY_SERVICE_SPECIAL:
                String sSpecial = spinnerSpecialType.getItems().get(spinnerSpecialType.getSelectedIndex()).toString();
                Log.e(TAG, "SELECTED Special Service Type = " + sSpecial);

                if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(0).toString())) {
                    POST_financialAccountNumber = "1.19.1";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(1).toString())) {
                    POST_financialAccountNumber = "1.19.2";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(2).toString())) {
                    POST_financialAccountNumber = "1.19.3";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(3).toString())) {
                    POST_financialAccountNumber = "1.19.4";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(4).toString())) {
                    POST_financialAccountNumber = "1.19.5";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(5).toString())) {
                    POST_financialAccountNumber = "1.19.6";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(6).toString())) {
                    POST_financialAccountNumber = "1.19.7";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(7).toString())) {
                    POST_financialAccountNumber = "1.19.8";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(8).toString())) {
                    POST_financialAccountNumber = "1.17.2.1";
                } else if (sSpecial.contentEquals(spinnerSpecialType.getItems().get(9).toString())) {
                    POST_financialAccountNumber = "1.17.2.3";
                }
                POST_note = sSpecial;

                break;
            case KEY_SERVICE_LAIN:
                Log.e(TAG, "NOTE = " + etNote.getText());

                String sOtherType = spinnerOtherType.getItems().get(spinnerOtherType.getSelectedIndex()).toString();
                POST_note = sOtherType + "| " + etNote.getText().toString();

                if (sOtherType.contentEquals(spinnerOtherType.getItems().get(0).toString())) {
                    POST_financialAccountNumber = "1.5.6.1";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(1).toString())) {
                    POST_financialAccountNumber = "1.5.6.2";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(2).toString())) {
                    POST_financialAccountNumber = "1.5.6.3";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(3).toString())) {
                    POST_financialAccountNumber = "1.9.1";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(4).toString())) {
                    POST_financialAccountNumber = "1.9.2";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(5).toString())) {
                    POST_financialAccountNumber = "1.9.3";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(6).toString())) {
                    POST_financialAccountNumber = "1.9.4";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(7).toString())) {
                    POST_financialAccountNumber = "1.10";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(8).toString())) {
                    POST_financialAccountNumber = "1.11";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(9).toString())) {
                    POST_financialAccountNumber = "1.12";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(10).toString())) {
                    POST_financialAccountNumber = "1.13.1";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(11).toString())) {
                    POST_financialAccountNumber = "1.13.2";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(12).toString())) {
                    POST_financialAccountNumber = "1.14";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(13).toString())) {
                    POST_financialAccountNumber = "1.15";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(14).toString())) {
                    POST_financialAccountNumber = "1.16.1";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(15).toString())) {
                    POST_financialAccountNumber = "1.20";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(16).toString())) {
                    POST_financialAccountNumber = "1.21";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(17).toString())) {
                    POST_financialAccountNumber = "1.22.1";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(18).toString())) {
                    POST_financialAccountNumber = "1.22.2";
                } else if (sOtherType.contentEquals(spinnerOtherType.getItems().get(19).toString())) {
                    POST_financialAccountNumber = "1.22.3";
                }

                break;

            case KEY_SERVICE_SPECIAL_IBADAH_MINGGU:
                String sTime = spinnerSundayServiceTime.getItems().get(spinnerSundayServiceTime.getSelectedIndex()).toString();
                Log.e(TAG, "SUNDAY SERVICE TYPE = " + sTime);

                if (sTime.contentEquals("Subuh")) {
                    POST_financialAccountNumber = "1.1.1";
                } else if (sTime.contentEquals("Pagi")) {
                    POST_financialAccountNumber = "1.2.1";
                } else if (sTime.contentEquals("Malam")) {
                    POST_financialAccountNumber = "1.3.1";
                }

                break;
        }

        ArrayList<Adding_RecyclerModel> selectedPriests = btnAddPriest.getSelectedList();
        ArrayList<Adding_RecyclerModel> selectedNames = btnAddName.getSelectedList();

        Log.e(TAG, "============ Priest In Charge ============");
        Log.e(TAG, "Priest Count : " + selectedPriests.size());
        index = 1;
        for (Adding_RecyclerModel model : selectedPriests) {
            Log.e(TAG, "Priest " + index + " Id : " + model.getId());
            Log.e(TAG, "Priest " + index + " Name : " + model.getName());
            Log.e(TAG, "Priest " + index + " BOD : " + model.getBirthDate());
            Log.e(TAG, "Priest " + index + " Kol : " + model.getKolom());
            Log.e(TAG, "Priest " + index + " isBaptize : " + model.isBaptis());
            Log.e(TAG, "Priest " + index + " isSidi : " + model.isSidi());
            Log.e(TAG, "Priest " + index + " isNikah : " + model.isNikah());
            index++;
        }
        String s = String.valueOf(selectedPriests.get(0).getId());
        POST_khadimId = s.isEmpty() ? "" : s;
        Log.e(TAG, "============================================");

        if (keyIssue.contentEquals(KEY_SERVICE_SPECIAL_IBADAH_MINGGU)) {
            selectedNames = btnCoordinator.getSelectedList();
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
        s = String.valueOf(selectedNames.get(0).getId());
        POST_issuedMemberData = s.isEmpty() ? "" : s;


        Log.e(TAG, "Date : " + etDate.getText());
        POST_date = etDate.getText().toString();
        Log.e(TAG, "Time : " + etTime.getText());
        POST_time = etTime.getText().toString().replace("Pukul ", "").replace(" ", "");
        Log.e(TAG, "Place : " + etPlace.getText());
        POST_place = etPlace.getText().toString();

        if (keyIssue.contentEquals(KEY_SERVICE_SPECIAL_IBADAH_MINGGU)) {
            Adding_RecyclerModel selectedCoordinator = btnCoordinator.getSelectedList().get(0);
            Log.e(TAG, "---------------------SELECTED COORDINATOR------------------------------");
            Log.e(TAG, "Name : " + selectedCoordinator.getName());
            Log.e(TAG, "BOD : " + selectedCoordinator.getBirthDate());
            Log.e(TAG, "Kol : " + selectedCoordinator.getKolom());
            Log.e(TAG, "isBaptize : " + selectedCoordinator.isBaptis());
            Log.e(TAG, "isSidi : " + selectedCoordinator.isSidi());
            Log.e(TAG, "isNikah : " + selectedCoordinator.isNikah());
            Log.e(TAG, "---------------------------------------------------");
            Log.e(TAG, "Penerima Tamu: " + spinnerGuess.getItems().get(spinnerGuess.getSelectedIndex()));
            Log.e(TAG, "Petugas Ibadah: " + spinnerDuty.getItems().get(spinnerDuty.getSelectedIndex()));
        }

        Log.e(TAG, "------------------------------------");
        Log.e(TAG, ":REQUEST TO SERVER:");
        Log.e(TAG, "------------------------------------");
        Log.e(TAG, "issue_by_member_id: " + Guru.getInt(KEY_MEMBER_ID, 0));
        Log.e(TAG, "key_issue: " + keyIssue);
        Log.e(TAG, "issued_member_data: " + POST_issuedMemberData);
        Log.e(TAG, "date: " + POST_date);
        Log.e(TAG, "time: " + POST_time);
        Log.e(TAG, "place: " + POST_place);
        Log.e(TAG, "note: " + POST_note);
        Log.e(TAG, "khadim_id: " + POST_khadimId);
        Log.e(TAG, "financial_account_number: " + POST_financialAccountNumber);

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);
        Call call =RetrofitClient.getInstance(null).getApiServices().setIssueService(
                Guru.getInt(KEY_MEMBER_ID, 0),
                keyIssue,
                POST_issuedMemberData,
                POST_date,
                POST_time,
                POST_place,
                POST_note,
                POST_khadimId,
                POST_financialAccountNumber
        );

        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

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
                        dialogInterface -> context.sendBroadcast(new Intent(ACTION_ACTIVITY_FINISH))
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
