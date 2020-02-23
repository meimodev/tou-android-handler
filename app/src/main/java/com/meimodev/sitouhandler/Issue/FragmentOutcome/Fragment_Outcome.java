package com.meimodev.sitouhandler.Issue.FragmentOutcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.meimodev.sitouhandler.Constant.*;

public class Fragment_Outcome extends Fragment {
    private final String TAG = "Fragment_Outcome : ";

    private View rootView;
    private Context context;

    private String keyIssue;

    @Nullable
    @BindView(R.id.tv_Title)
    TextView tvTitle;
    @Nullable
    @BindView(R.id.editText_amount)
    CustomEditText etAmount;
    @Nullable
    @BindView(R.id.radioGroup_sentralisasi)
    RadioGroup rgSentralisasi;
    @Nullable
    @BindView(R.id.linearLayout_paycheck)
    LinearLayout llPaycheck;
    @Nullable
    @BindView(R.id.spinner_paycheckType)
    MaterialSpinner spinnerPaycheckType;
    @Nullable
    @BindView(R.id.layout_spinnerStaff)
    LinearLayout llSpinnerStaff;

    @Nullable
    @BindView(R.id.linearLayout_pengadaan)
    LinearLayout llPengadaan;
    @Nullable
    @BindView(R.id.spinner_pengadaanType)
    MaterialSpinner spinnerPengadaanType;
    @Nullable
    @BindView(R.id.linearLayout_pengadaanPlaceHolder)
    LinearLayout llPengadaanPlaceHolder;
    @Nullable
    @BindView(R.id.button_addPengadaan)
    CustomButtonAdd btnAddPengadaan;
    private ArrayList<View> pengadaanViews;

    @Nullable
    @BindView(R.id.linearLayout_penunjangFasilitas)
    LinearLayout llPenunjang;
    @Nullable
    @BindView(R.id.editText_detail)
    CustomEditText etDetail;

    @Nullable
    @BindView(R.id.linearLayout_rapatSidangKonven)
    LinearLayout llRapat;
    @Nullable
    @BindView(R.id.radioGroup_rapatSidangKonven)
    RadioGroup rgRapatSidangKonven;
    @Nullable
    @BindView(R.id.spinner_rapatSidangKonven)
    MaterialSpinner spinnerRapatSidangKonven;

    @Nullable
    @BindView(R.id.linearLayout_diakonia)
    LinearLayout llDiakonia;
    @Nullable
    @BindView(R.id.radioGroup_diakoniaBeasiswa)
    RadioGroup rgDiakonia;


    @Nullable
    @BindView(R.id.linearLayout_namePlaceHolder)
    LinearLayout llNamePlaceHolder;
    @Nullable
    @BindView(R.id.button_addName)
    CustomButtonAdd btnAddName;

    @Nullable
    @BindView(R.id.linearLayout_pelatihan)
    LinearLayout llPembekalan;
    @Nullable
    @BindView(R.id.radioGroup_pembekalan)
    RadioGroup rgPembekalan;


    @Nullable
    @BindView(R.id.linearLayout_subsidi)
    LinearLayout llSubsidi;
    @Nullable
    @BindView(R.id.spinner_subsidi)
    MaterialSpinner spinnerSubsidi;
    @Nullable
    @BindView(R.id.spinner_subsidiIbadah)
    MaterialSpinner spinnerSubsidiIbadah;

    @Nullable
    @BindView(R.id.linearLayout_other)
    LinearLayout llOther;
    @Nullable
    @BindView(R.id.spinner_other)
    MaterialSpinner spinnerOther;
    @Nullable
    @BindView(R.id.radioGroup_other)
    RadioGroup rgOther;

    @Nullable
    @BindView(R.id.linearLayout_otherNoAccount)
    LinearLayout llotherNoAccount;
    @Nullable
    @BindView(R.id.editText_note)
    CustomEditText etNote;

    @Nullable
    @BindView(R.id.editText_diakonia_detail)
    CustomEditText etDiakoniaDetail;
    @Nullable
    @BindView(R.id.editText_rapat_detail)
    CustomEditText etRapatDetail;
    @Nullable
    @BindView(R.id.editText_pembekalan_detail)
    CustomEditText etPembekalanDetail;
    @Nullable
    @BindView(R.id.editText_subsidi_detail)
    CustomEditText etSubsidiDetail;
    @Nullable
    @BindView(R.id.editText_lainnya_detail)
    CustomEditText etLainnyaDetail;

    @Nullable
    @BindView(R.id.spinner_staff)
    MaterialSpinner spinnerStaff;
    @Nullable
    @BindView(R.id.spinner_fasilitas)
    MaterialSpinner spinnerFacility;

    private final int REQUEST_CODE_PERSONAL_NAME = 1;

    private ArrayList<CustomEditText> customEditTexts = new ArrayList<>();
    private ArrayList<CustomButtonAdd> customButtonAdds = new ArrayList<>();
    private ArrayList<MaterialSpinner> materialSpinners = new ArrayList<>();
    private ArrayList<RadioGroup> radioGroups = new ArrayList<>();

    private String POST_accountNumberKey = "";
    private String POST_issuedMemberData = "";
    private String POST_note = "";
    private String POST_description = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_issue_outcome, container, false);
        ButterKnife.bind(this, rootView);
        context = rootView.getContext();


        if (getArguments() != null) {
            keyIssue = getArguments().getString(KEY_OUTCOME);

            if (keyIssue != null) {

                etAmount.setAsThousandSeparator();
                etAmount.setAsNoLeadingZero();


                tvTitle.setText("Pengajuan Biaya ".concat(keyIssue));

                ArrayList<String> costs;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                switch (keyIssue) {
                    case KEY_OUTCOME_CENTRALIZE:
                        rgSentralisasi.setVisibility(View.VISIBLE);

                        break;
                    case KEY_OUTCOME_PAYCHECK:
                        llPaycheck.setVisibility(View.VISIBLE);
                        llSpinnerStaff.setVisibility(View.GONE);

                        ArrayList<String> paycheckType = new ArrayList<>();
                        ArrayList<String> staff = new ArrayList<>();

                        paycheckType.add("Pilih Jenis Gaji / Tunjangan");
                        paycheckType.add("Gaji / Tunjangan Pekerja Gereja");
                        paycheckType.add("Tunjangan Struktural BPMJ ");
                        paycheckType.add("Tunjangan Hari Raya Pekerja Gereja");
                        spinnerPaycheckType.setItems(paycheckType);
                        spinnerPaycheckType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (paycheckType.get(0).contains("Pilih")) {
                                paycheckType.remove(0);
                                spinnerPaycheckType.setItems(Objects.requireNonNull(paycheckType));
                                spinnerPaycheckType.setSelectedIndex(position - 1);
                            }
                            staff.clear();
                            staff.add("Pilih Staff");
                            if (item.toString().contentEquals(paycheckType.get(0))) {
                                staff.add("Pendeta ditugaskan sebagai Ketua BPMJ");
                                staff.add("Pendeta jemaat");
                                staff.add("Pendeta jemaat belum PO");
                                staff.add("Pegawai Tata Usaha");
                                staff.add("Kostor digereja / pastori");
                                staff.add("Kostor di kanisah");
                                staff.add("Pelayan khusus Non Pendeta");
                                staff.add("BPPJ");
                                staff.add("Penasehat Majelis Jemaat");
                                staff.add("Koordinator Komisi Kerja");
                                staff.add("Tenaga Orientasi Pelayanan");
                            } else if (item.toString().contentEquals(paycheckType.get(1))) {
                                staff.add("Ketua BPMJ");
                                staff.add("Wakil Ketua");
                                staff.add("Sekretaris");
                                staff.add("Bendahara");
                                staff.add("Anggota");
                            } else if (item.toString().contentEquals(paycheckType.get(2))) {
                                staff.add("Ketua BPMJ");
                                staff.add("Wakil Ketua");
                                staff.add("Sekretaris");
                                staff.add("Bendahara");
                                staff.add("Anggota");
                                staff.add("Pelsus");
                                staff.add("Pendeta jemaat");
                                staff.add("Pegawai Tata Usaha");
                                staff.add("Kostor");
                                staff.add("Koordinator Komisi Kerja");
                                staff.add("Guru Sekolah Minggu");
                                staff.add("Penasehat Majelis Jemaat");

                            }
                            spinnerStaff.setItems(staff);
                            if (llSpinnerStaff.getVisibility() != View.VISIBLE)
                                llSpinnerStaff.setVisibility(View.VISIBLE);

                        });
                        spinnerStaff.setOnItemSelectedListener((view, position, id, item) -> {
                            if (staff.get(0).contains("Pilih")) {
                                staff.remove(0);
                                spinnerStaff.setItems(Objects.requireNonNull(staff));
                                spinnerStaff.setSelectedIndex(position - 1);
                            }

                            if (spinnerPaycheckType.getItems().get(spinnerPaycheckType.getSelectedIndex()).toString()
                                    .contentEquals(spinnerPaycheckType.getItems().get(0).toString())) {
                                Map<String, String> holder = new HashMap<>();
                                holder.put("Pendeta ditugaskan sebagai Ketua BPMJ", "3.2.1");
                                holder.put("Pendeta jemaat", "3.2.2");
                                holder.put("Pendeta jemaat belum PO", "3.2.3");
                                holder.put("Pegawai Tata Usaha", "3.2.4");
                                holder.put("Kostor digereja / pastori", "3.2.6");
                                holder.put("Kostor di kanisah", "3.2.7");
                                holder.put("Pelayan khusus Non Pendeta", "3.2.8");
                                holder.put("BPPJ", "3.2.9");
                                holder.put("Penasehat Majelis Jemaat", "3.2.10");
                                holder.put("Koordinator Komisi Kerja", "3.2.11");
                                holder.put("Tenaga Orientasi Pelayanan", "3.2.12");

                                POST_accountNumberKey = holder.get(spinnerStaff.getItems().get(spinnerStaff.getSelectedIndex()).toString());

                            } else if (spinnerPaycheckType.getItems().get(spinnerPaycheckType.getSelectedIndex()).toString()
                                    .contentEquals(spinnerPaycheckType.getItems().get(1).toString())) {
                                Map<String, String> holder = new HashMap<>();
                                holder.put("Ketua BPMJ", "3.3.1");
                                holder.put("Wakil Ketua", "3.3.2");
                                holder.put("Sekretaris", "3.3.3");
                                holder.put("Bendahara", "3.3.4");
                                holder.put("Anggota", "3.3.5");

                                POST_accountNumberKey = holder.get(spinnerStaff.getItems().get(spinnerStaff.getSelectedIndex()).toString());


                            } else if (spinnerPaycheckType.getItems().get(spinnerPaycheckType.getSelectedIndex()).toString()
                                    .contentEquals(spinnerPaycheckType.getItems().get(2).toString())) {
                                Map<String, String> holder = new HashMap<>();

                                holder.put("Ketua BPMJ", "3.4.1");
                                holder.put("Wakil Ketua", "3.4.2");
                                holder.put("Sekretaris", "3.4.3");
                                holder.put("Bendahara", "3.4.4");
                                holder.put("Anggota", "3.4.5");
                                holder.put("Pelsus", "3.4.6");
                                holder.put("Pendeta jemaat", "3.4.7");
                                holder.put("Pegawai Tata Usaha", "3.4.8");
                                holder.put("Kostor", "3.4.9");
                                holder.put("Koordinator Komisi Kerja", "3.4.10");
                                holder.put("Guru Sekolah Minggu", "3.4.11");
                                holder.put("Penasehat Majelis Jemaat", "3.4.12");

                                POST_accountNumberKey = holder.get(spinnerStaff.getItems().get(spinnerStaff.getSelectedIndex()).toString());
                            }
                        });


                        break;
                    case KEY_OUTCOME_PENGADAAN:
                        llPengadaan.setVisibility(View.VISIBLE);
                        etAmount.setVisibility(View.GONE);
                        etAmount.setEnabled(false);
                        etAmount.setHint("Jumlah Total :");

                        ArrayList<String> pengadaanType = new ArrayList<>();
                        pengadaanType.add("Pilih Jenis Pengadaan"); // 0
                        pengadaanType.add("Liturgi / Pengadaan Materi / Surat / Fotocopy"); // 0
                        pengadaanType.add("Formulir GMIM (sertifikat baptis, sidi, nikah)");// 1
                        pengadaanType.add("Sampul-sampul / Amplop"); // 2
                        pengadaanType.add("Alat Perlengkapan Kantor"); // 3
                        pengadaanType.add("Pundi, Stola, Kain Mimbar, Lilin, Alat Perjamuan"); // 4
                        pengadaanType.add("Pengadaan Bacaan: Bina Ibu"); // 5
                        pengadaanType.add("Pengadaan Bacaan: Bina Bapa"); // 6
                        pengadaanType.add("Pengadaan Bacaan: Pemuda"); // 7
                        pengadaanType.add("Pengadaan Bacaan: Remaja"); // 8
                        pengadaanType.add("Pengadaan Bacaan: Anak"); // 9
                        pengadaanType.add("Pengadaan Bacaan: Menjabarkan Trilogi Pembangunan Jemaat (MTPJ)"); // 10
                        pengadaanType.add("Pengadaan Bacaan: Renungan Harian Keluarga (RHK)"); // 11
                        pengadaanType.add("Pengadaan Lainnya"); // 12
                        spinnerPengadaanType.setItems(pengadaanType);
                        spinnerPengadaanType.setOnItemSelectedListener((view, position, id, item) -> {
                            if (pengadaanType.get(0).contains("Pilih")) {
                                pengadaanType.remove(0);
                                spinnerPengadaanType.setItems(Objects.requireNonNull(pengadaanType));
                                spinnerPengadaanType.setSelectedIndex(position - 1);
                            }
//                            if (llPengadaanPlaceHolder.getChildCount() > 0) {
//                                llPengadaanPlaceHolder.removeAllViews();
//
//                            }

                        });

                        pengadaanViews = new ArrayList<>();
                        btnAddPengadaan.setOnClickListener(view -> {

                            if (etAmount.getVisibility() == View.GONE) {
                                etAmount.setVisibility(View.VISIBLE);
                                etAmount.setText("");
                            }
                            View v = inflater.inflate(R.layout.recycler_item_adding_details, llPengadaanPlaceHolder, false);
                            CustomEditText etDetail = v.findViewById(R.id.recyclerItem_detail);
                            CustomEditText etSubAmount = v.findViewById(R.id.recyclerItem_amount);
                            etDetail.setHint("Detil : (Nama - jumlah per unit - dll) ");
                            etSubAmount.setAsThousandSeparator();
                            etSubAmount.setAsNoLeadingZero();
                            v.findViewById(R.id.recyclerItem_delete).setOnClickListener(view1 -> {
                                llPengadaanPlaceHolder.removeView(v);
                                pengadaanViews.remove(v);
                                calculatePengadaanTotal();
                                if (pengadaanViews.isEmpty()) {
                                    etAmount.setVisibility(View.GONE);
                                    etAmount.setText("");
                                }

                            });

                            etSubAmount.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    calculatePengadaanTotal();
                                }
                            });

                            pengadaanViews.add(v);
                            llPengadaanPlaceHolder.addView(v);
                            etSubAmount.requestFocus();
                        });

                        break;
                    case KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN:
                        llPenunjang.setVisibility(View.VISIBLE);
//                        etAmount.setEnabled(false);

                        ArrayList<String> facilities = new ArrayList<>();
                        facilities.add("Pilih Biaya Fasilitas");
                        facilities.add("Listrik"); // 0
                        facilities.add("Air"); // 1
                        facilities.add("Telepon"); // 2
                        facilities.add("Pulsa untuk Pembantu Pelayanan"); // 3
                        facilities.add("Pemeliharaan Gedung Gereja / Inventaris"); // 4
                        facilities.add("Pemeliharaan Pastori"); // 5
                        facilities.add("Pemeliharaan Balai Jemaat"); // 6
                        facilities.add("Pemeliharaan Sound System / Proyektor"); // 7
                        facilities.add("Pemeliharaan / Sewa / Kontrak Rumah Pendeta / Vicaris"); // 8
                        facilities.add("Perawatan / Pemeliharaan Lainnya"); // 9
                        spinnerFacility.setItems(facilities);

                        spinnerFacility.setOnItemSelectedListener((view, position, id, item) -> {
                            if (facilities.get(0).contains("Pilih")) {
                                facilities.remove(0);
                                spinnerFacility.setItems(Objects.requireNonNull(facilities));
                                spinnerFacility.setSelectedIndex(position - 1);
                            }
                        });

                        break;
                    case KEY_OUTCOME_RAPAT_SIDANG_KONVEN:
                        llRapat.setVisibility(View.VISIBLE);

                        costs = new ArrayList<>();
//                        costs.add("Pilih Biaya");

                        costs.add("Pilih Biaya Rapat/Sidang/Konven");
                        costs.add("Konsumsi Rapat BPMJ"); // 0
                        costs.add("Konsumsi Sidang Pleno Majelis Jemaat"); // 1
                        costs.add("Konsumsi Sidang Tahunan"); // 2
                        costs.add("Uang Duduk Sidang Majelis"); // 3
                        costs.add("Uang Duduk Rapat BPMJ"); // 4
                        costs.add("Rapat Sidi Jemaat"); // 5
                        costs.add("Konsultasi dengan BPW / Sinode"); // 6
                        costs.add("Sidang Tahunan Majelis Sinode"); // 7
                        costs.add("Sidang Tahunan Majelis Sinode Istimewa"); // 8
                        costs.add("Sidang Sinode"); // 9
                        costs.add("Sidang Tahunan Wilayah"); // 10
                        costs.add("Rapat Kerja Wilayah"); // 11
                        costs.add("Rapat Kerja Ketua BPMJ se-Sinode"); // 12
                        costs.add("Rapat Kerja KSB se-Sinode"); // 13
                        costs.add("Konven Komisi Kerja Wilayah / Sinode"); // 14
                        costs.add("Konven Pendeta / Guru Agama"); // 15
                        costs.add("Konven Penatua & Syamas Wilayah / Sinode"); // 16
                        costs.add("Konven Kostor"); // 17
                        costs.add("Biaya Konsumsi Rapat Evaluasi"); // 18
                        costs.add("Biaya Administrasi Rapat Evaluasi"); // 19
                        costs.add("Biaya Tim ABP dan Program"); // 20
                        costs.add("Konsumsi Penyusunan Materi Rapat Akhir Tahun"); // 21
                        costs.add("Biaya Administrasi Rapat Akhir Tahun"); // 22
                        spinnerRapatSidangKonven.setItems(costs);
                        spinnerRapatSidangKonven.setOnItemSelectedListener((view, position, id, item) -> {
                            if (costs.get(0).contains("Pilih")) {
                                costs.remove(0);
                                spinnerRapatSidangKonven.setItems(Objects.requireNonNull(costs));
                                spinnerRapatSidangKonven.setSelectedIndex(position - 1);
                            }
                        });


                        break;
                    case KEY_OUTCOME_DIAKONIA_BESASISWA:
                        llDiakonia.setVisibility(View.VISIBLE);

                        btnAddName.setVisibility(View.GONE);
                        btnAddName.setOnClickListener(view -> {
                            Intent intent = new Intent(context, Adding.class);
                            intent.putExtra("request", "name");
                            startActivityForResult(intent, REQUEST_CODE_PERSONAL_NAME);
                        });

                        btnAddName.setAsAddingButton(container, inflater, llNamePlaceHolder, 1, () -> {
                            Snackbar.make(rootView, "Nama Berhasil Dihapus", Snackbar.LENGTH_SHORT).show();
                        });

                        costs = new ArrayList<>();

                        costs.add("Dana Sehat Keluarga Pendeta non PO");
                        costs.add("Dana Sehat Keluarga Pendeta di Wilayah");
                        costs.add("Dana Sehat Anggota Jemaat");
                        costs.add("Beasiswa Tingkat SD, SMP, SMA / SMK");
                        costs.add("Beasiswa Mahasiswa Theologi UKIT");
                        costs.add("Dana Duka");
                        costs.add("Bantuan Bencana");
                        costs.add("Diakonia Hasil Ibadah Inatura");
                        costs.add("Kepedulian untuk jemaat lainnya");
                        costs.add("Perbaikan Lingkungan Jemaat");
                        costs.add("Kursus Pengembangan Bakat");
                        costs.add("Panti Asuhan");


                        for (String cost : costs) {
                            RadioButton rb = new RadioButton(context);
                            rb.setLayoutParams(params);
                            rb.setText(cost);
                            rgDiakonia.addView(rb);
                        }


                        break;
                    case KEY_OUTCOME_PEMBEKALAN_PELATIHAN:
                        llPembekalan.setVisibility(View.VISIBLE);

                        costs = new ArrayList<>();

                        costs.add("Pembekalan Pelayan Khusus");
                        costs.add("Pembekalan Komisi BIPRA / Komisi Kerja");
                        costs.add("Pembekalan / Raker Perbendaharaan");
                        costs.add("Pembekalan Anggota Jemaat");
                        costs.add("Katekisasi Calon Sidi Jemaat");

                        for (String cost : costs) {
                            RadioButton rb = new RadioButton(context);
                            rb.setLayoutParams(params);
                            rb.setText(cost);
                            rgPembekalan.addView(rb);
                        }


                        break;
                    case KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN:
                        llSubsidi.setVisibility(View.VISIBLE);

                        costs = new ArrayList<>();
                        costs.add("Pilih Jenis Subsidi");
                        costs.add("Keperluan Pria / Kaum Bapa"); // 0
                        costs.add("Keperluan Wanita / Kaum Ibu"); // 1
                        costs.add("Keperluan Pemuda"); // 2
                        costs.add("Keperluan Remaja"); // 3
                        costs.add("Keperluan Anak Sekolah Minggu"); // 4
                        costs.add("Keperluan Kegiatan Lainnya"); // 5
                        spinnerSubsidi.setItems(costs);
                        spinnerSubsidi.setOnItemSelectedListener((view, position, id, item) -> {
                            if (costs.get(0).contains("Pilih")) {
                                costs.remove(0);
                                spinnerSubsidi.setItems(Objects.requireNonNull(costs));
                                spinnerSubsidi.setSelectedIndex(position - 1);
                            }

                            if (item.toString().contentEquals(costs.get(costs.size() - 1))) {
                                ArrayList<String> holder = new ArrayList<>();
                                holder.add("Ibadah Paskah Jemaat");
                                holder.add("Ibadah Natal Jemaat");
                                holder.add("Ibadah Natal Kolom");
                                holder.add("Ibadah Natal BIPRA");
                                holder.add("Ibadah Natal Keluarga Pelayan");
                                holder.add("Ibadah Natal Komisi Kerja");
                                holder.add("Ibadah HUT Jemaat");
                                holder.add("Ibadah Tamasya Jemaat");
                                holder.add("Kegiatan Jemaat Lainnya");
                                spinnerSubsidiIbadah.setItems(holder);

                                spinnerSubsidiIbadah.setVisibility(View.VISIBLE);

                            } else spinnerSubsidiIbadah.setVisibility(View.GONE);
                        });


                        break;
                    case KEY_OUTCOME_OTHER:
                        llOther.setVisibility(View.VISIBLE);

                        costs = new ArrayList<>();
                        costs.add("Pilih Jenis Biaya");
                        costs.add("BPPJ");
                        costs.add("BPPW");
                        costs.add("Mutasi Cendramata Pendeta Ketua Jemaat");
                        costs.add("Mutasi Cendramata Pendeta Jemaat");
                        costs.add("Mutasi Cendramata Pendeta Wilayah");
                        costs.add("Administrasi Bank");
                        costs.add("Setoran Minggu ke-III ke Wilayah");
                        costs.add("Tamu Gereja");
                        costs.add("Biaya Tak Terduga");

                        spinnerOther.setItems(costs);
                        spinnerOther.setOnItemSelectedListener((view, position, id, item) -> {
                            if (costs.get(0).contains("Pilih")) {
                                costs.remove(0);
                                spinnerOther.setItems(Objects.requireNonNull(costs));
                                spinnerOther.setSelectedIndex(position - 1);
                            }
                        });

//                        for (String cost : costs) {
//                            RadioButton rb = new RadioButton(context);
//                            rb.setLayoutParams(params);
//                            rb.setText(cost);
//                            rgOther.addView(rb);
//                        }

                        break;
                    case KEY_OUTCOME_OTHER_NO_ACCOUNT:
                        llotherNoAccount.setVisibility(View.VISIBLE);


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

            selectedModel.setCategory("Atas Nama : ");
            btnAddName.addSelected(selectedModel);
        }
    }

    @OnClick(R.id.button_submit)
    void btnSubmit() {
        rootView.clearFocus();

        ////////////////////////////////////// index sensitive //////////////////////////////////////
        customEditTexts.add(etAmount); // 0
        customEditTexts.add(etNote); // 1
        customEditTexts.add(etDetail);// 2
        customEditTexts.add(etLainnyaDetail); // 3
        customEditTexts.add(etPembekalanDetail); // 4
        customEditTexts.add(etRapatDetail); //5
        customEditTexts.add(etSubsidiDetail); // 6
        customEditTexts.add(etDiakoniaDetail); // 7
        customButtonAdds.add(btnAddName); // 0
        customButtonAdds.add(btnAddPengadaan); // 1
        materialSpinners.add(spinnerStaff); // 0
        materialSpinners.add(spinnerFacility); // 1
        materialSpinners.add(spinnerPaycheckType); // 2
        materialSpinners.add(spinnerPengadaanType); //3
        materialSpinners.add(spinnerRapatSidangKonven); //4
        materialSpinners.add(spinnerSubsidi); //5
        materialSpinners.add(spinnerOther); // 6
        radioGroups.add(rgSentralisasi); // 0
        radioGroups.add(null); // 1
        radioGroups.add(rgDiakonia); // 2
        radioGroups.add(rgOther);// 3
        radioGroups.add(rgPembekalan); // 4
        radioGroups.add(rgRapatSidangKonven); // 5
        ////////////////////////////////////// index sensitive //////////////////////////////////////

        Validator validator = new Validator();

//        Special Validation in Pengadaan
        if (pengadaanViews != null) {
            if (pengadaanViews.isEmpty()) {
                validator.displayErrorMessage(context, "Silahkan sentuh 'Tambah Pengadaan' untuk menambahkan detil pengadaan ");
                return;
            }

            for (View v : pengadaanViews) {
                CustomEditText etAmount = v.findViewById(R.id.recyclerItem_amount);
                CustomEditText etDetail = v.findViewById(R.id.recyclerItem_detail);
                if (etDetail.getText().length() == 0 || etAmount.getText().length() < 2) {
                    validator.displayErrorMessage(context, "Detil Pengadaan tidak valid (Jumlah tidak valid / Detail tidak boleh kosong)");
                    return;
                }
            }
        }

        Map<String, String> validation = validator.validateInput(keyIssue, customButtonAdds, customEditTexts, materialSpinners, radioGroups);

        if (validation.get("result").contentEquals("ERROR")) {
            validator.displayErrorMessage(context, validation.get("message"));
            return;
        }

        // Reset View Holder Array List
        customEditTexts.clear();
        customButtonAdds.clear();
        materialSpinners.clear();
        radioGroups.clear();


        Log.e(TAG, "------------------------ LOG HEAD ------------------------");
        Log.e(TAG, "SUBMIT FRAGMENT INCOME : KEY ISSUE = " + keyIssue);

        Log.e(TAG, "Amount - " + etAmount.getText());

        int index;

        switch (keyIssue) {
            case KEY_OUTCOME_CENTRALIZE:

                RadioButton checked = rgSentralisasi.findViewById(rgSentralisasi.getCheckedRadioButtonId());
                if (checked.getText().toString().contains("Sinode"))
                    POST_accountNumberKey = "I";
                else
                    POST_accountNumberKey = "II";

                POST_description =
                        checked.getText().toString().concat(", berjumlah Rp. ")
                                .concat(etAmount.getText().toString().replace(",", "."));

                Log.e(TAG, "SELECTED OPTION = " + checked);

                break;
            case KEY_OUTCOME_PAYCHECK:

                String selectedType = spinnerPaycheckType.getItems().get(spinnerPaycheckType.getSelectedIndex()).toString();
                String selectedStaff = spinnerStaff.getItems().get(spinnerStaff.getSelectedIndex()).toString();
                Log.e(TAG, "Selected payCheckType = " + selectedType);
                Log.e(TAG, "Selected Staff = " + selectedStaff);

                POST_description =
                        keyIssue
                                + " berupa: "
                                + selectedType
                                + " kepada "
                                + selectedStaff
                                + ", Jumlah keseluruhan Rp. "
                                + etAmount.getText().toString().replace(",", ".");

                break;
            case KEY_OUTCOME_PENGADAAN:
                Log.e(TAG, "DETAIL COUNT = " + pengadaanViews.size());

                int selectedSpinnerIndex = spinnerPengadaanType.getSelectedIndex();

                switch (selectedSpinnerIndex) {
                    case 0:
                        POST_accountNumberKey = "3.7.1";
                        break;
                    case 1:
                        POST_accountNumberKey = "3.7.2";
                        break;
                    case 2:
                        POST_accountNumberKey = "3.7.3";
                        break;
                    case 3:
                        POST_accountNumberKey = "3.7.4";
                        break;
                    case 4:
                        POST_accountNumberKey = "3.7.6";
                        break;
                    case 5:
                        POST_accountNumberKey = "3.8.1";
                        break;
                    case 6:
                        POST_accountNumberKey = "3.8.2";
                        break;
                    case 7:
                        POST_accountNumberKey = "3.8.3";
                        break;
                    case 8:
                        POST_accountNumberKey = "3.8.4";
                        break;
                    case 9:
                        POST_accountNumberKey = "3.8.5";
                        break;
                    case 10:
                        POST_accountNumberKey = "3.8.6";
                        break;
                    case 11:
                        POST_accountNumberKey = "3.8.7";
                        break;
                    case 12:
                        POST_accountNumberKey = "3.999";
                        break;
                }

                String selectedSpinnerItem = spinnerPengadaanType.getItems().get(selectedSpinnerIndex).toString();
                Log.e(TAG, "Pengadaan Type: " + selectedSpinnerItem);

                String holder = "";
                index = 1;
                for (View view : pengadaanViews) {
                    CustomEditText etDetail = view.findViewById(R.id.recyclerItem_detail),
                            etAmount = view.findViewById(R.id.recyclerItem_amount);
                    Log.e(TAG, "DETAIL " + index + " Details = " + etDetail.getText());
                    Log.e(TAG, "DETAIL " + index + " Amount = " + etAmount.getText());

                    holder = holder.concat(etDetail.getText().toString())
                            .concat(", ")
                            .concat("Rp. ")
                            .concat(etAmount.getText().toString().replace(",", ".")
                                    .concat(" | ")
                            );

                    index++;
                }

                POST_description = keyIssue
                        + " "
                        + selectedSpinnerItem
                        + " berupa: "
                        + holder
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");

                break;
            case KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN:
                int selectedIndex = spinnerFacility.getSelectedIndex();
                String selectedFacility = spinnerFacility.getItems().get(selectedIndex).toString();
                Log.e(TAG, "COST NAME = " + selectedFacility);
                Log.e(TAG, "Fasilitas Penunjang Pelayanan NOTE = " + etDetail.getText());

                switch (selectedIndex) {
                    case 0:
                        POST_accountNumberKey = "3.19.1";
                        break;
                    case 1:
                        POST_accountNumberKey = "3.19.2";
                        break;
                    case 2:
                        POST_accountNumberKey = "3.19.3";
                        break;
                    case 3:
                        POST_accountNumberKey = "3.19.4";
                        break;
                    case 4:
                        POST_accountNumberKey = "3.21.1";
                        break;
                    case 5:
                        POST_accountNumberKey = "3.21.3";
                        break;
                    case 6:
                        POST_accountNumberKey = "3.21.4";
                        break;
                    case 7:
                        POST_accountNumberKey = "3.21.5";
                        break;
                    case 8:
                        POST_accountNumberKey = "3.21.6";
                        break;
                    case 9:
                        POST_accountNumberKey = "3.21.7";
                        break;
                }

                POST_description = keyIssue
                        + " berupa: "
                        + selectedFacility
                        + "| "
                        + etDetail.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");

                break;
            case KEY_OUTCOME_RAPAT_SIDANG_KONVEN:

                int selectedIndex1 = spinnerRapatSidangKonven.getSelectedIndex();
                String selectedSpinnerText = spinnerRapatSidangKonven.getItems().get(selectedIndex1).toString();

                Log.e(TAG, "SELECTED OPTION = " + selectedSpinnerText);

                switch (selectedIndex1) {
                    case 0:
                        POST_accountNumberKey = "3.9.1";
                        break;
                    case 1:
                        POST_accountNumberKey = "3.9.2";
                        break;
                    case 2:
                        POST_accountNumberKey = "3.9.3";
                        break;
                    case 3:
                        POST_accountNumberKey = "3.9.4";
                        break;
                    case 4:
                        POST_accountNumberKey = "3.9.5";
                        break;
                    case 5:
                        POST_accountNumberKey = "3.9.6";
                        break;
                    case 6:
                        POST_accountNumberKey = "3.9.7";
                        break;
                    case 7:
                        POST_accountNumberKey = "3.9.8";
                        break;
                    case 8:
                        POST_accountNumberKey = "3.9.9";
                        break;
                    case 9:
                        POST_accountNumberKey = "3.9.10";
                        break;
                    case 10:
                        POST_accountNumberKey = "3.9.11";
                        break;
                    case 11:
                        POST_accountNumberKey = "3.9.12";
                        break;
                    case 12:
                        POST_accountNumberKey = "3.9.13";
                        break;
                    case 13:
                        POST_accountNumberKey = "3.9.14";
                        break;
                    case 14:
                        POST_accountNumberKey = "3.9.15";
                        break;
                    case 15:
                        POST_accountNumberKey = "3.9.16";
                        break;
                    case 16:
                        POST_accountNumberKey = "3.9.17";
                        break;
                    case 17:
                        POST_accountNumberKey = "3.9.18";
                        break;
                    case 18:
                        POST_accountNumberKey = "3.10.1";
                        break;
                    case 19:
                        POST_accountNumberKey = "3.10.2";
                        break;
                    case 20:
                        POST_accountNumberKey = "3.10.3";
                        break;
                    case 21:
                        POST_accountNumberKey = "3.11.1";
                        break;
                    case 22:
                        POST_accountNumberKey = "3.11.2";
                        break;
                }

                POST_description = keyIssue
                        + " untuk: "
                        + selectedSpinnerText
                        + "| "
                        + etRapatDetail.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");


                break;
            case KEY_OUTCOME_DIAKONIA_BESASISWA:
                Log.e(TAG, "NAMES COUNT = " + btnAddName.getSelectedList().size());
                index = 1;
                for (Adding_RecyclerModel model : btnAddName.getSelectedList()) {
                    Log.e(TAG, "NAMES " + index + " ID : " + model.getId());
                    Log.e(TAG, "NAMES " + index + " Name : " + model.getName());
                    Log.e(TAG, "NAMES " + index + " Kolom : " + model.getKolom());
                    Log.e(TAG, "NAMES " + index + " BOD : " + model.getBirthDate());
                    Log.e(TAG, "NAMES " + index + " Category : " + model.getCategory());
                    index++;
                }
                String selectedRadio = ((RadioButton) rgDiakonia.findViewById(rgDiakonia.getCheckedRadioButtonId())).getText().toString();
                Log.e(TAG, "SELECTED OPTION = " + selectedRadio);
                String diakoniaDetail = etDiakoniaDetail.getText().toString();
                Log.e(TAG, "Diakonia / Beasiswa Note = " + diakoniaDetail);

                POST_issuedMemberData = encodeMemberData(btnAddName);

                if (rgDiakonia.getChildAt(0).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.1.2";
                } else if (rgDiakonia.getChildAt(1).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.1.3";
                } else if (rgDiakonia.getChildAt(2).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.1.4";
                } else if (rgDiakonia.getChildAt(3).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.2.1";
                } else if (rgDiakonia.getChildAt(4).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.2.2";
                } else if (rgDiakonia.getChildAt(5).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.4";
                } else if (rgDiakonia.getChildAt(6).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.6";
                } else if (rgDiakonia.getChildAt(7).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.7";
                } else if (rgDiakonia.getChildAt(8).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.8";
                } else if (rgDiakonia.getChildAt(9).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.9";
                } else if (rgDiakonia.getChildAt(10).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.10";
                } else if (rgDiakonia.getChildAt(11).getId() == rgDiakonia.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.14.11";
                }

                POST_description = keyIssue
                        + " untuk: "
                        + selectedRadio
                        + "| "
                        + diakoniaDetail
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");

                break;
            case KEY_OUTCOME_PEMBEKALAN_PELATIHAN:
                Log.e(TAG, "Pembekalan / Pelatihan NOTE = " + etPembekalanDetail.getText().toString());
                String selectedRadioText = ((RadioButton) rgPembekalan.findViewById(rgPembekalan.getCheckedRadioButtonId())).getText().toString();
                Log.e(TAG, "SELECTED OPTION = " + selectedRadioText);

                if (rgPembekalan.getChildAt(0).getId() == rgPembekalan.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.12.1";
                } else if (rgPembekalan.getChildAt(1).getId() == rgPembekalan.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.12.2";
                } else if (rgPembekalan.getChildAt(2).getId() == rgPembekalan.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.12.3";
                } else if (rgPembekalan.getChildAt(3).getId() == rgPembekalan.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.12.4";
                } else if (rgPembekalan.getChildAt(4).getId() == rgPembekalan.getCheckedRadioButtonId()) {
                    POST_accountNumberKey = "3.12.4";
                }


                POST_description = keyIssue
                        + " untuk: "
                        + selectedRadioText
                        + "| "
                        + etPembekalanDetail.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");


                break;
            case KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN:
                Log.e(TAG, "Subsidi BIPRA NOTE = " + etSubsidiDetail.getText().toString());
                String selectedSubsidi = spinnerSubsidi.getItems().get(spinnerSubsidi.getSelectedIndex()).toString();
                if (spinnerSubsidiIbadah.getVisibility() == View.VISIBLE) {
                    selectedSubsidi = spinnerSubsidiIbadah.getItems().get(spinnerSubsidiIbadah.getSelectedIndex()).toString();

                    String s = spinnerSubsidiIbadah.getItems().get(spinnerSubsidiIbadah.getSelectedIndex()).toString();
                    if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(0).toString())) {
                        POST_accountNumberKey = "3.15.1";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(1).toString())) {
                        POST_accountNumberKey = "3.15.2.1";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(2).toString())) {
                        POST_accountNumberKey = "3.15.2.2";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(3).toString())) {
                        POST_accountNumberKey = "3.15.2.3";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(4).toString())) {
                        POST_accountNumberKey = "3.15.2.4";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(5).toString())) {
                        POST_accountNumberKey = "3.15.2.5";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(6).toString())) {
                        POST_accountNumberKey = "3.16";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(7).toString())) {
                        POST_accountNumberKey = "3.17";
                    } else if (s.contentEquals(spinnerSubsidiIbadah.getItems().get(8).toString())) {
                        POST_accountNumberKey = "3.18";
                    }

                } else {
                    String s = spinnerSubsidi.getItems().get(spinnerSubsidi.getSelectedIndex()).toString();
                    if (s.contentEquals(spinnerSubsidi.getItems().get(0).toString())) {
                        POST_accountNumberKey = "3.1.1";
                    } else if (s.contentEquals(spinnerSubsidi.getItems().get(1).toString())) {
                        POST_accountNumberKey = "3.1.2";
                    } else if (s.contentEquals(spinnerSubsidi.getItems().get(2).toString())) {
                        POST_accountNumberKey = "3.1.3";
                    } else if (s.contentEquals(spinnerSubsidi.getItems().get(3).toString())) {
                        POST_accountNumberKey = "3.1.4";
                    } else if (s.contentEquals(spinnerSubsidi.getItems().get(4).toString())) {
                        POST_accountNumberKey = "3.1.5";
                    }
                }

                POST_description = keyIssue
                        + " untuk: "
                        + selectedSubsidi
                        + "| "
                        + etSubsidiDetail.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");

                break;
            case KEY_OUTCOME_OTHER:
                int selectedOtherIndex = spinnerOther.getSelectedIndex();
                String selectedOther = spinnerOther.getItems().get(selectedOtherIndex).toString();

                switch (selectedOtherIndex) {
                    case 0:
                        POST_accountNumberKey = "3.22.1";
                        break;
                    case 1:
                        POST_accountNumberKey = "3.22.2";
                        break;
                    case 2:
                        POST_accountNumberKey = "3.23.1.1";
                        break;
                    case 3:
                        POST_accountNumberKey = "3.23.1.2";
                        break;
                    case 4:
                        POST_accountNumberKey = "3.23.1.3";
                        break;
                    case 5:
                        POST_accountNumberKey = "3.23.2";
                        break;
                    case 6:
                        POST_accountNumberKey = "3.23.3";
                        break;
                    case 7:
                        POST_accountNumberKey = "3.23.4";
                        break;
                    case 8:
                        POST_accountNumberKey = "3.23.5";
                        break;
                }

                Log.e(TAG, "SELECTED OPTION = " + selectedOther);
                Log.e(TAG, "Lainnya NOTE = " + etLainnyaDetail.getText().toString());

                POST_description = keyIssue
                        + " untuk: "
                        + selectedOther
                        + "| "
                        + etLainnyaDetail.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");
                break;
            case KEY_OUTCOME_OTHER_NO_ACCOUNT:
                POST_accountNumberKey = "3.999";
                Log.e(TAG, "Lainnya No account NOTE : " + etNote.getText());

                POST_description = keyIssue
                        + ": " + etNote.getText().toString()
                        + ", Jumlah keseluruhan Rp. "
                        + etAmount.getText().toString().replace(",", ".");

                break;
        }

        Log.e(TAG, "------------------------------------");
        Log.e(TAG, ":REQUEST TO SERVER:");
        Log.e(TAG, "------------------------------------");
        Log.e(TAG, "member_id: " + Guru.getInt(KEY_MEMBER_ID, 0));
        Log.e(TAG, "keyIssue: " + keyIssue);
        Log.e(TAG, "amount: " + etAmount.getText().toString().trim());
        Log.e(TAG, "accountNumberKey: " + POST_accountNumberKey);
        Log.e(TAG, "issuedMemberDataRaw: " + POST_issuedMemberData);
        Log.e(TAG, "note: " + POST_note);
        Log.e(TAG, "description: " + POST_description);

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);
        Call call =RetrofitClient.getInstance(null).getApiServices().setIssueFinancial(
                Guru.getInt(KEY_MEMBER_ID, 0),
                keyIssue,
                "-" + etAmount.getText().toString().trim().replace(",", ""),
                POST_accountNumberKey,
                POST_issuedMemberData,
                POST_note,
                POST_description,
                0
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
                        dialogInterface -> {
                            context.sendBroadcast(new Intent(ACTION_ACTIVITY_FINISH));
                        }
                );
            }

            @Override
            public void onRetry() {
                requestHandler.enqueue(call);
            }
        });
        requestHandler.enqueue(call);

    }

    ///////////////////////////////////////////////////////////////////////////
    // Minor function & Variable
    ///////////////////////////////////////////////////////////////////////////

    private void calculatePengadaanTotal() {
        int count = 0;
        for (View v : pengadaanViews) {
            CustomEditText et = v.findViewById(R.id.recyclerItem_amount);
            if (et.getText() != null && !et.getText().toString().isEmpty())
                count += Integer.parseInt(et.getText().toString().replace(",", ""));
        }
        etAmount.setText(String.valueOf(count));
        if (etAmount.getText() != null && etAmount.getText().toString().contentEquals("0"))
            etAmount.setText("");

    }

//    private class HelperPaycheck{
//        private String key;
//        private String value;
//
//        public HelperPaycheck(String key, String value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        public String getKey() {
//            return key;
//        }
//
//        public String getValue() {
//            return value;
//        }
//    }


}
