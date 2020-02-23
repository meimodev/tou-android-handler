package com.meimodev.sitouhandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.squti.guru.Guru;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hmomeni.progresscircula.ProgressCircula;
import com.meimodev.sitouhandler.CustomWidget.CustomButtonAdd;
import com.meimodev.sitouhandler.Issue.Adding_RecyclerModel;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.SignIn.SignIn;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class Constant {
    private static final String TAG = "Constant: ";

    public static final String KEY_USER_ID = "User_ID";
    public static final String KEY_USER_FULL_NAME = "User_Full_Name";
    public static final String KEY_USER_AGE = "User_Age";
    public static final String KEY_USER_SEX = "User_Sex";
    public static final String KEY_USER_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String KEY_MEMBER_ID = "Member_ID";
    public static final String KEY_MEMBER_BIPRA = "Member_BIPRA";
    public static final String KEY_MEMBER_CHURCH_POSITION = "Member_Church_Position";
    public static final String KEY_CHURCH_ID = "Church_Id";
    public static final String KEY_CHURCH_NAME = "Church_Name";
    public static final String KEY_CHURCH_KELURAHAN = "Church_Village";
    public static final String KEY_COLUMN_ID = "Column_Id";
    public static final String KEY_COLUMN_NAME_INDEX = "Column_Name_Index";

    public static final String ROOT_TRANSFER_PROTOCOL = "http://";
    public static final String ROOT_DOMAIN = "192.168.1.137:8000";
    public static final String ROOT_URL_API = ROOT_TRANSFER_PROTOCOL + ROOT_DOMAIN + "/api/";
    public static final String ROOT_URL_PRINTABLE = ROOT_TRANSFER_PROTOCOL + ROOT_DOMAIN + "/print/";
    public static final String ROOT_URL_TERMS = ROOT_TRANSFER_PROTOCOL + ROOT_DOMAIN + "/terms-and-condition";

    public static final String ACCOUNT_TYPE_CHIEF = "Ketua Jemaat";
    public static final String ACCOUNT_TYPE_SECRETARY = "Sekretaris Jemaat";
    public static final String ACCOUNT_TYPE_TREASURER = "Bendahara Jemaat";
    public static final String ACCOUNT_TYPE_PEGAWAI = "Pegawai Gereja";
    public static final String ACCOUNT_TYPE_PRIEST = "Pendeta";
    public static final String ACCOUNT_TYPE_PENATUA = "Penatua";
    public static final String ACCOUNT_TYPE_SYAMAS = "Syamas";
    public static final String ACCOUNT_TYPE_PENATUA_PKB = "Penatua PKB";
    public static final String ACCOUNT_TYPE_PENATUA_WKI = "Penatua WKI";
    public static final String ACCOUNT_TYPE_PENATUA_YOUTH = "Penatua Pemuda";
    public static final String ACCOUNT_TYPE_PENATUA_REMAJA = "Penatua Remaja";
    public static final String ACCOUNT_TYPE_PENATUA_ANAK = "Penatua Anak";
    public static final String ACCOUNT_TYPE_MEMBER = "Anggota Jemaat";
    public static final String ACCOUNT_TYPE_USER = "User";

//    public static String ACCOUNT_TYPE = ACCOUNT_TYPE_USER;

    public static final String ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED = "key_content_in_fragment_is_clicked";
    public static final String ACTION_CONTENT_USER_UNATHENTICATED = "user_unauthenticated";
    public static final String ACTION_ACTIVITY_FINISH = "key_activity_finish";
    public static final String ACTION_REFETCH_MEMBER_HOME = "refetch_member_home";

    public static final int ISSUE_TYPE_OUTCOME = 1;
    public static final int ISSUE_TYPE_INCOME = 2;
    public static final int ISSUE_TYPE_PAPERS = 3;
    public static final int ISSUE_TYPE_SERVICE = 4;

    //    ISSUE KEYS
    public static final String KEY_OUTCOME = "OUTCOME";
    public static final String KEY_OUTCOME_CENTRALIZE = "Sentralisasi";
    public static final String KEY_OUTCOME_PAYCHECK = "Pembayaran Gaji / Tunjangan";
    public static final String KEY_OUTCOME_PENGADAAN = "Pengadaan";
    public static final String KEY_OUTCOME_FASILITAS_PENUNJANG_PELAYAN = "Fasilitas Penunjang Pelayanan";
    public static final String KEY_OUTCOME_RAPAT_SIDANG_KONVEN = "Rapat / Sidang / Konven";
    public static final String KEY_OUTCOME_DIAKONIA_BESASISWA = "Diakonia / Beasiswa";
    public static final String KEY_OUTCOME_PEMBEKALAN_PELATIHAN = "Pembekalan Pelatihan";
    public static final String KEY_OUTCOME_SUBSIDI_BIPRA_IBADAH_KEGIATAN = "Subsidi BIPRA / Ibadah / Kegiatan";
    public static final String KEY_OUTCOME_OTHER = "Biaya Lainnya";
    public static final String KEY_OUTCOME_OTHER_NO_ACCOUNT = "Biaya (Tanpa Nomor Akun)";

    public static final String KEY_INCOME = "INCOME";
    public static final String KEY_INCOME_PERSEMBAHAN_IBADAH = "Persembahan Ibadah";
    public static final String KEY_INCOME_SAMPUL_SYUKUR = "Sampul Syukur";
    public static final String KEY_INCOME_LAINNYA = "Pemasukan Lainnya";
    public static final String KEY_INCOME_LAINNYA_NO_ACCOUNT = "Pemasukan (Tanpa Nomor Akun)";

    public static final String KEY_PAPERS = "PAPERS";
    public static final String KEY_PAPERS_VALIDATE_MEMBERS = "Surat Keterangan Anggota Jemaat";
    public static final String KEY_PAPERS_CREDENTIAL = "Surat Kredensi";
    public static final String KEY_PAPERS_BAPTIZE = "Surat Baptis";
    public static final String KEY_PAPERS_SIDI = "Surat Sidi";
    public static final String KEY_PAPERS_MARRIED = "Surat Nikah";

    public static final String KEY_SERVICE = "SERVICE";
    public static final String KEY_SERVICE_KOLOM = "Ibadah Kolom";
    public static final String KEY_SERVICE_BIPRA = "Ibadah BIPRA";
    public static final String KEY_SERVICE_HUT = "Ibadah HUT";
    public static final String KEY_SERVICE_PEMAKAMAN = "Ibadah Pemakaman";
    public static final String KEY_SERVICE_PERINGATAN = "Peringatan 40 Hari";
    public static final String KEY_SERVICE_KELUARGA = "Ibadah Keluarga";
    public static final String KEY_SERVICE_HARI_RAYA = "Ibadah Hari Raya";
    public static final String KEY_SERVICE_SPECIAL = "Ibadah Khusus";
    public static final String KEY_SERVICE_LAIN = "Ibadah Lainnya";

    public static final String KEY_SERVICE_SPECIAL_IBADAH_MINGGU = "Ibadah Minggu";

    public static final String AUTHORIZATION_STATUS_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String AUTHORIZATION_STATUS_UNCONFIRMED = "UNCONFIRMED";
    public static final String AUTHORIZATION_STATUS_ACCEPTED = "ACCEPTED";
    public static final String AUTHORIZATION_STATUS_REJECTED = "REJECTED";
    public static final int AUTHORIZATION_STATUS_CODE_UNAUTHORIZED = 9;
    public static final int AUTHORIZATION_STATUS_CODE_UNCONFIRMED = 0;
    public static final int AUTHORIZATION_STATUS_CODE_ACCEPTED = 1;
    public static final int AUTHORIZATION_STATUS_CODE_REJECTED = 2;

    public static final String LETTER_TYPE_INBOUND = "INBOUND";
    public static final String LETTER_TYPE_OUTBOUND = "OUTBOUND";

    public static final String NOTIFICATION_TOPIC_USER = "TOU_USER";
    public static final String NOTIFICATION_TOPIC_GMIM_MEMBER = "GMIM";
    public static final String NOTIFICATION_TOPIC_CHURCH = "CHURCH";
    public static final String NOTIFICATION_TOPIC_CHURCH_PELSUS = "PELSUS";
    public static final String NOTIFICATION_TOPIC_CHURCH_EXECUTIVES = "CHIEF_SECRETARY_TREASURER";
    public static final String NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_S = "CHIEF_SECRETARY";
    public static final String NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_C_T = "CHIEF_TREASURER";
    public static final String NOTIFICATION_TOPIC_CHURCH_EXECUTIVES_S_T = "SECRETARY_TREASURER";
    public static final String NOTIFICATION_TOPIC_CHURCH_PKB = "CHURCH_PKB";
    public static final String NOTIFICATION_TOPIC_CHURCH_WKI = "CHURCH_WKI";
    public static final String NOTIFICATION_TOPIC_CHURCH_YOUTH = "CHURCH_YOUTH";
    public static final String NOTIFICATION_TOPIC_CHURCH_TEENS = "CHURCH_TEENS";
    public static final String NOTIFICATION_TOPIC_CHURCH_KIDS = "CHURCH_KIDS";
    public static final String NOTIFICATION_TOPIC_COLUMN = "COLUMN";
    public static final String NOTIFICATION_TOPIC_COLUMN_PKB = "COLUMN_PKB";
    public static final String NOTIFICATION_TOPIC_COLUMN_WKI = "COLUMN_WKI";
    public static final String NOTIFICATION_TOPIC_COLUMN_YOUTH = "COLUMN_YOUTH";
    public static final String NOTIFICATION_TOPIC_COLUMN_TEENS = "COLUMN_TEENS";
    public static final String NOTIFICATION_TOPIC_COLUMN_KIDS = "COLUMN_KIDS";

    public static final String BIPRA_PKB = "Pria / Kaum Bapa";
    public static final String BIPRA_WKI = "Wanita / Kaum Ibu";
    public static final String BIPRA_PEMUDA = "Pemuda";
    public static final String BIPRA_REMAJA = "Remaja";
    public static final String BIPRA_ANAK = "Anak";

    public static long coolDownMilliSecondsLeft = 0;

    ///////////////////////////////////////////////////////////////////////////
    // Minor Univ Methods
    ///////////////////////////////////////////////////////////////////////////

    public static String formatDate(int days, int month, int year) {

        String monthNames;
        switch (month + 1) {
            case 1:
                monthNames = "Januari";
                break;
            case 2:
                monthNames = "Februari";
                break;
            case 3:
                monthNames = "Maret";
                break;
            case 4:
                monthNames = "April";
                break;
            case 5:
                monthNames = "Mei";
                break;
            case 6:
                monthNames = "Juni";
                break;
            case 7:
                monthNames = "Juli";
                break;
            case 8:
                monthNames = "Agustus";
                break;
            case 9:
                monthNames = "September";
                break;
            case 10:
                monthNames = "Oktober";
                break;
            case 11:
                monthNames = "November";
                break;
            case 12:
                monthNames = "Desember";
                break;
            default:
                monthNames = "Invalid month !";
        }

        return days + " " + monthNames + " " + year;
    }

    public static String formatTime(int hour, int minute, int second, boolean displayWithSecond) {
        String postfix, h, m, s;

        if (hour > 0 && hour < 11) postfix = ", Pagi";
        else if (hour >= 12 && hour < 15) postfix = ", Siang";
        else if (hour >= 15 && hour < 19) postfix = ", Sore";
        else if (hour >= 19 && hour <= 23) postfix = ", Malam";
        else postfix = ", Subuh";

        h = String.valueOf(hour);
        m = String.valueOf(minute);
        s = String.valueOf(second);

        if (h.length() == 1) h = "0".concat(h);
        if (m.length() == 1) m = "0".concat(m);
        if (s.length() == 1) s = "0".concat(s);

        if (!displayWithSecond) return "Pukul " + h + " : " + m;
        else return "Pukul, " + h + " : " + m + " '" + s;
    }

    public static void toggleViewVisibility(View view) {
        if (view.getVisibility() == View.GONE) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }

    public static void toggleSort(Context context, TextView tv, boolean isSelected) {
        if (isSelected) {
            tv.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            tv.setTextColor(context.getResources().getColor(R.color.ButtonText_LightGrey));
            tv.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);

        } else {
            tv.setBackgroundColor(context.getResources().getColor(R.color.ButtonText_LightGrey));
            tv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    public static void displayDialog(@Nullable Context context,
                                     @Nullable String title,
                                     @Nullable String content,
                                     @Nullable boolean cancelable,
                                     @Nullable DialogInterface.OnClickListener positiveClickListener,
                                     @Nullable DialogInterface.OnClickListener negativeClickListener
    ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (content != null) builder.setMessage(content);
        builder.setCancelable(cancelable);
        if (positiveClickListener != null) builder.setPositiveButton("OK", positiveClickListener);
        if (negativeClickListener != null)
            builder.setNegativeButton("BATAL", negativeClickListener);
        builder.create().show();

    }

    public static void displayDialog(@Nullable Context context,
                                     @Nullable String title,
                                     @Nullable String content,
                                     @Nullable boolean cancelable,
                                     @Nullable DialogInterface.OnClickListener positiveClickListener,
                                     @Nullable DialogInterface.OnClickListener negativeClickListener,
                                     @Nullable DialogInterface.OnDismissListener dismissListener
    ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (content != null) builder.setMessage(content);
        builder.setCancelable(cancelable);
        if (positiveClickListener != null) builder.setPositiveButton("OK", positiveClickListener);
        if (negativeClickListener != null)
            builder.setNegativeButton("BATAL", negativeClickListener);
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }
        builder.create().show();

    }

    public static View makeProgressCircle(View rootView) {

        View v = LayoutInflater.from(rootView.getContext()).inflate(R.layout.resource_custom_progress_bar_progressing, ((ViewGroup) rootView), false);
        ProgressCircula p = v.findViewById(R.id.pc);

        try {

            RelativeLayout holder = rootView.findViewById(R.id.layout_progressHolder);
            holder.removeAllViews();
            holder.addView(v);

            p.setSpeed(3);
            p.setIndeterminate(true);
            p.setRimWidth(6);
            p.startRotation();
            p.setRimColor(rootView.getContext().getResources().getColor(R.color.colorAccent));
            p.setShowProgress(false);

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("IMPORTANT !", "makeProgressCircle: Parent Layout is not found (no id: R.id.layout_progressHolder) on rootView");
        }


        return v;
    }

    public static View makeFailFetch(View rootView, View.OnClickListener retryFetchOnclickListener) {

        View v = LayoutInflater.from(rootView.getContext()).inflate(R.layout.resource_custom_progress_bar_fail, ((ViewGroup) rootView), false);
        RelativeLayout holder = rootView.findViewById(R.id.layout_progressHolder);
        holder.removeAllViews();
        holder.addView(v);
        ImageButton btn = v.findViewById(R.id.button_tryAgain);
        btn.setOnClickListener(retryFetchOnclickListener);
        return v;
    }

    public static String encodeMemberData(CustomButtonAdd customButtonAdd) {

        ArrayList<Adding_RecyclerModel> dataModel = null;
        ArrayList<View> dataView = null;

        StringBuilder res = new StringBuilder();

        if (customButtonAdd.getSelectedList() != null && !customButtonAdd.getSelectedList().isEmpty()) {
            dataModel = customButtonAdd.getSelectedList();
        } else if (customButtonAdd.getSelectedView() != null && !customButtonAdd.getSelectedList().isEmpty()) {
            dataView = customButtonAdd.getSelectedView();
            Log.e(TAG, "encodeMemberData: ERROR, passing selected view not yet supported! will return null");
        }

        if (dataModel != null && !dataModel.isEmpty()) {
            for (Adding_RecyclerModel m : dataModel) {
                if (m.getId() != 0)
                    res.append(":").append(m.getId()).append(":");
                else {
                    res.append(":")
                            .append("-")
                            .append(m.getName())
                            .append("-")
                            .append("-")
                            .append(m.isBaptis() ? "Baptize Entry Number" : "")
                            .append("-")
                            .append("-")
                            .append(m.isSidi() ? "Sidi Entry Number" : "")
                            .append("-")
                            .append("-")
                            .append(m.isNikah() ? "Married Entry Number" : "")
                            .append("-")
                            .append(":");
                }
            }
        } else if (dataView != null && !dataView.isEmpty()) {
            Log.e(TAG, "encodeMemberData: ERROR, passing selected view not yet supported! will return null");
        }

        return res.toString();
    }

    public static void WhatsAppSendMessage(Context context, String phoneNumberWithCountryCode, String message) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                        phoneNumberWithCountryCode, message)));
        context.startActivity(i);
    }

    public static void signOut(Context context) {
        SharedPrefManager.getInstance(context).logout();
        Guru.clear();
        ((Activity) context).finishAffinity();
        context.startActivity(new Intent(context, SignIn.class));
    }

    public static void justify(final TextView textView) {

        final AtomicBoolean isJustify = new AtomicBoolean(false);

        final String textString = textView.getText().toString();

        final TextPaint textPaint = textView.getPaint();

        final SpannableStringBuilder builder = new SpannableStringBuilder();

        textView.post(() -> {
            if (!isJustify.get()) {

                final int lineCount = textView.getLineCount();
                final int textViewWidth = textView.getWidth();

                for (int i = 0; i < lineCount; i++) {

                    int lineStart = textView.getLayout().getLineStart(i);
                    int lineEnd = textView.getLayout().getLineEnd(i);

                    String lineString = textString.substring(lineStart, lineEnd);

                    if (i == lineCount - 1) {
                        builder.append(new SpannableString(lineString));
                        break;
                    }

                    String trimSpaceText = lineString.trim();
                    String removeSpaceText = lineString.replaceAll(" ", "");

                    float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                    float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                    float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                    SpannableString spannableString = new SpannableString(lineString);
                    for (int j = 0; j < trimSpaceText.length(); j++) {
                        char c = trimSpaceText.charAt(j);
                        if (c == ' ') {
                            Drawable drawable = new ColorDrawable(0x00ffffff);
                            drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                            ImageSpan span = new ImageSpan(drawable);
                            spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    builder.append(spannableString);
                }

                textView.setText(builder);
                isJustify.set(true);
            }
        });
    }

    public static void sendFCMTokenToServer(Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "getInstanceId/FCM_TOKEN failed: ", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    int userId= Guru.getInt(KEY_USER_ID, 0);
                    if (userId == 0) throw new NumberFormatException("USER ID cannot be 0");
                    IssueRequestHandler req = new IssueRequestHandler(null);
                    req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().setFCMToken(
                            userId, token
                    ));
                });


    }

    public static void changeStatusColor(Context context, @ColorRes int colorId) {
        Window window = ((Activity) context).getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(context, colorId));
    }

}
