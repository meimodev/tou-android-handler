/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import androidx.annotation.Nullable;

import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif.ReadJSONNavFragmentChiefManageServiceArea;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.ReadJSONNavFragmentMemberHome;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {

    @FormUrlEncoded
    @POST("sign-in")
    Call<ResponseBody> signIn(
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("sign-up-account")
    Call<ResponseBody> signUpAccount(
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("date_of_birth") String dob,
            @Field("sex") String sex

    );

    @FormUrlEncoded
    @POST("confirm-account")
    Call<ResponseBody> confirmAccount(
            @Field("type") String type,
            @Field("phone") String phone,
            @Field("code") String code
    );

    @GET("member-home")
    Call<ResponseBody> getMemberHome(
            @Query("member_id") int memberId
    );

    @GET("member-issue")
    Call<ResponseBody> getMemberIssue(
            @Query("member_id") int memberId
    );


    @GET("service-area")
    Call<ResponseBody> getServiceArea(
            @Query("member_id") int memberId
    );

    @FormUrlEncoded
    @POST("member-home")
    Call<ResponseBody> authorizeIssue(
            @Field("authorize_id") int authorizeId,
            @Field("authorize_key") String authorizeKey
    );

    @FormUrlEncoded
    @POST("service-area")
    Call<ResponseBody> setServiceArea(
            @Field("service_area_id") int serviceAreaId,
            @Field("from_column") int fromColumn,
            @Field("to_column") int toColumn
    );

    @GET("issue")
    Call<ResponseBody> getIssue(
            @Query("issue_id") int issueId
    );

    @GET("letter")
    Call<ResponseBody> getIssuedLetters(
            @Query("member_id") int member_id
    );

    @FormUrlEncoded
    @POST("issue-financial")
    Call<ResponseBody> setIssueFinancial(
            @Field("issued_by_member_id") int memberId,
            @Field("key_issue") String keyIssue,
            @Field("amount") String amount,
            @Field("account_number_key") String accountNumber,
            @Field("issued_member_data") String issuedMemberData,
            @Field("note") String note,
            @Field("description") String description,
            @Field("related_service_id") int serviceId
    );

    @FormUrlEncoded
    @POST("issue-paper")
    Call<ResponseBody> setIssuePaper(
            @Field("issued_by_member_id") int memberId,
            @Field("key_issue") String keyIssue,
            @Field("destination") String destination,
            @Field("date") String date,
            @Field("time") String time,
            @Field("place") String place,
            @Field("ceremony_date") String ceremonyDate,
            @Field("priest_id") String priestId,
            @Field("issued_member_data") String issuedMemberData
    );

    @FormUrlEncoded
    @POST("issue-service")
    Call<ResponseBody> setIssueService(
            @Field("issued_by_member_id") int memberId,
            @Field("key_issue") String keyIssue,
            @Field("issued_member_data") String issuedMemberData,
            @Field("date") String date,
            @Field("time") String time,
            @Field("place") String place,
            @Field("note") String note,
            @Field("khadim_id") String khadimId,
            @Field("financial_account_number") String financialAccountNumber
    );

    @GET("find-member")
    Call<ResponseBody> findMember(
            @Query("member_id") int memberId,
            @Query("search_key") String searchKey
    );

    @GET("find-member")
    Call<ResponseBody> findMemberByEmail(
            @Query("email") String email
    );

    @GET("find-user")
    Call<ResponseBody> findUserById(
            @Query("id") int id
    );

    @GET("find-user")
    Call<ResponseBody> findUserByEmail(
            @Query("email") String email
    );

    @GET("find-user")
    Call<ResponseBody> findUserByPhone(
            @Query("phone") String phone
    );

    @GET("find-service")
    Call<ResponseBody> findService(
            @Query("service_entry_id") String serviceEntryId
    );

    @GET("column")
    Call<ResponseBody> getColumnOverview(
            @Query("member_id") int member_id
    );

    @GET("academic_degrees")
    Call<ResponseBody> getAcademicDegrees();

    @FormUrlEncoded
    @POST("column")
    Call<ResponseBody> addMemberUser(
            @Field("issued_by_member_id") int issuedByMemberId,
            @Field("first_name") String firstName,
            @Field("middle_name") String middleName,
            @Field("last_name") String lastName,
            @Field("degree_pre") String degreePre,
            @Field("degree_post") String degreePost,
            @Field("date_of_birth") String dateOfBirth,
            @Field("sex") String sex,
            @Field("nik") String nik,
            @Field("phone") String phone,
            @Field("baptis_entry") String baptize_entry,
            @Field("sidi_entry") String baptize_sidi,
            @Field("nikah_entry") String baptize_nikah,
            @Field("force_save") boolean forceSave
    );

    @FormUrlEncoded
    @PATCH("column/{id}")
    Call<ResponseBody> editMember(
            @Path("id") int memberId,
            @Field("issued_by_member_id") int issuedByMemberId,
            @Field("first_name") String firstName,
            @Field("middle_name") String middleName,
            @Field("last_name") String lastName,
            @Field("degree_pre") String degreePre,
            @Field("degree_post") String degreePost,
            @Field("date_of_birth") String dateOfBirth,
            @Field("sex") String sex,
            @Field("nik") String nik,
            @Field("phone") String phone,
            @Field("baptis_entry") String baptize_entry,
            @Field("sidi_entry") String baptize_sidi,
            @Field("nikah_entry") String baptize_nikah
    );

    @DELETE("column/{id}")
    Call<ResponseBody> deleteMember(
            @Path("id") int memberId
    );

    @GET("get-member")
    Call<ResponseBody> getMemberUserData(
            @Query("member_id") int memberId
    );

    @FormUrlEncoded
    @POST("fcm-token")
    Call<ResponseBody> setFCMToken(
            @Field("user_id") int userId,
            @Field("FCM_token") String FCM_token
    );

    @GET("apply-member/{id}")
    Call<ResponseBody> getApplyMemberPrep(
            @Path("id") int user_id
    );

    @FormUrlEncoded
    @POST("apply-member")
    Call<ResponseBody> setApplyMember(
            @Field("user_id") int userId,
            @Field("f_name") String fName,
            @Field("m_name") String mName,
            @Field("l_name") String lName,
            @Field("sex") String sex,
            @Field("dob") String dob,
            @Field("church_id") int churchId,
            @Field("column_index") String columnIndex,
            @Field("baptis") String baptis,
            @Field("sidi") String sidi,
            @Field("nikah") String nikah
    );

    @GET("financial")Call<ResponseBody> getIssuedFinancial(
            @Query("member_id")int memberId
    );

    @GET("duplicate-check")
    Call<ResponseBody> getDuplicateCheck(
            @Query("member_id") int memberId
    );

    @FormUrlEncoded
    @POST("duplicate-check")
    Call<ResponseBody> setDuplicateCheck(
            @Field("claimer_member_id") int claimerMemberId,
            @Field("param")String param,
            @Field("claimed_member_id") int claimedMemberId
    );
    @FormUrlEncoded
    @POST("duplicate-check")
    Call<ResponseBody> setDuplicateCheck(
            @Field("claimer_member_id") int claimerMemberId,
            @Field("param")String param
    );
}
