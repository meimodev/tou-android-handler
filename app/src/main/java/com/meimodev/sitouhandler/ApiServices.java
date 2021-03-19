/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import androidx.annotation.Nullable;

import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif.ReadJSONNavFragmentChiefManageServiceArea;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member.ReadJSONNavFragmentMemberHome;

import org.json.JSONObject;

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

    @GET("system-status")
    Call<ResponseBody> checkSystemStatus(
            @Query("type") String type,
            @Query("version") String version
    );

    @FormUrlEncoded
    @POST("sign-in")
    Call<ResponseBody> signIn(
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("sign-in-firebase")
    Call<ResponseBody> signInFirebase(
            @Field("phone") String phone,
            @Field("token") String token
    );

//    @FormUrlEncoded
//    @POST("sign-up-account")
//    Call<ResponseBody> signUpAccount(
//            @Field("phone") String phone,
//            @Field("password") String password,
//            @Field("first_name") String firstName,
//            @Field("last_name") String lastName,
//            @Field("date_of_birth") String dob,
//            @Field("sex") String sex
//    );

    @FormUrlEncoded
    @POST("sign-up-account")
    Call<ResponseBody> signUpFirebase(
            @Field("phone") String phone,
            @Field("fName") String firstName,
            @Field("lName") String lastName,
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
            @Field("issued_member_data") String issuedMemberData,
            @Field("description") String description
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

    @GET("find-church")
    Call<ResponseBody> findChurchById(
            @Query("id") int id
    );

    @GET("find-user")
    Call<ResponseBody> findUserByName(
            @Query("name") String name
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
            @Field("baptize") String baptize_entry,
            @Field("sidi") String sidi_entry,
            @Field("marriage") String nikah_entry,
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
            @Field("baptize") String baptize_entry,
            @Field("sidi") String sidi_entry,
            @Field("marriage") String nikah_entry
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

    @GET("financial")
    Call<ResponseBody> getIssuedFinancial(
            @Query("member_id") int memberId
    );

    @GET("duplicate-check")
    Call<ResponseBody> getDuplicateCheck(
            @Query("member_id") int memberId
    );

    @FormUrlEncoded
    @POST("duplicate-check")
    Call<ResponseBody> setDuplicateCheck(
            @Field("claimer_member_id") int claimerMemberId,
            @Field("param") String param,
            @Field("claimed_member_id") int claimedMemberId
    );

    @FormUrlEncoded
    @POST("duplicate-check")
    Call<ResponseBody> setDuplicateCheck(
            @Field("claimer_member_id") int claimerMemberId,
            @Field("param") String param
    );

    /*
     * Church Creation
     */

    @FormUrlEncoded
    @POST("apply-church-info")
    Call<ResponseBody> churchCreation_setChurchInfo(
            @Field("name") String name,
            @Field("kelurahan") String kelurahan,
            @Field("kecamatan") String kecamatan,
            @Field("kabupaten") String kabupaten,
            @Field("territory") String territory,
            @Field("address") String address,
            @Field("phone") String phone,
            @Field("balance") String balance,
            @Field("columns_count") String columns_count,
            @Field("priests_count") String priests_count
    );

    @FormUrlEncoded
    @POST("apply-church-positions")
    Call<ResponseBody> churchCreation_setChurchPositions(
            @Field("church_id") int churchId,
            @Field("user_json") String userJSON

    );

    /*
     * Account Setting / Editing
     */

    @GET("account/{id}")
    Call<ResponseBody> getAccount(
            @Path("id") int userId
    );

    @FormUrlEncoded
    @POST("account-pass")
    Call<ResponseBody> setAccountPassword(
            @Field("user_id") int userId,
            @Field("old_pass") String oldPass,
            @Field("new_pass") String newPass
    );

    @FormUrlEncoded
    @POST("account-id")
    Call<ResponseBody> setAccountIdentity(
            @Field("user_id") int userId,
            @Field("pass") String pass,
            @Field("f_name") String firstName,
            @Field("m_name") String middleName,
            @Field("l_name") String lastName,
            @Field("dob") String dob,
            @Field("sex") String sex
    );

    @FormUrlEncoded
    @POST("account-member")
    Call<ResponseBody> setAccountMembership(
            @Field("user_id") int userId,
            @Field("pass") String pass,
            @Field("baptize") String baptize,
            @Field("sidi") String sidi,
            @Field("marriage") String marriage
    );

    @GET("sunday-income")
    Call<ResponseBody> getSundayIncome(
            @Query("member_id") int memberId
    );

    /*
     * Service / Order
     */
    @GET("find-product")
    Call<ResponseBody> findProductWithParams(
            @Query("key") String key,
            @Query("type") String type,
            @Query("vendor") String vendor
    );

    @GET("find-product-recommendation")
    Call<ResponseBody> findProductRecommendation(
            @Query("type") String type
    );

    @GET("transport-time")
    Call<ResponseBody> getTransportAndTime(
            @Query("dis") int distance
    );

    @GET("orders/{id}")
    Call<ResponseBody> getOrders(
            @Path("id") int userId
    );

    @GET("order/{id}")
    Call<ResponseBody> getOrder(@Path("id") int orderId);

    @FormUrlEncoded
    @POST("order-auth")
    Call<ResponseBody> authorizeOrder(
            @Field("order_id") int orderId,
            @Field("auth") String authorize
    );

    @GET("order-finish")
    Call<ResponseBody> finishOrder(
            @Query("id") int id,
            @Query("rating") int rating
    );

    @FormUrlEncoded
    @POST("order")
    Call<ResponseBody> setOrder(
            @Field("user_id") int userId,
            @Field("delivery_location") String location,
            @Field("delivery_time") String time,
            @Field("transport_fee") int transportFee,
            @Field("total_bill") int total,
            @Field("type") String type,
            @Field("coordinate") String coordinate,
            @Field("products") String products
//            @Field("vendor_id") int vendorId
    );

    /*
     * Vendor
     */

    @GET("vendor/{id}")
    Call<ResponseBody> getVendor(@Path("id") int vendorId, @Query("categorize") boolean isCategorize);

    @GET("vendors")
    Call<ResponseBody> getVendors(@Query("minimal") boolean minimal);


    @GET("products")
    Call<ResponseBody> getAllProducts();

    @FormUrlEncoded
    @POST("product")
    Call<ResponseBody> setProduct(
            @Field("name") String name,
            @Field("price") int price,
            @Field("unit") String unit
    );

    @FormUrlEncoded
    @PATCH("product")
    Call<ResponseBody> editProduct(
            @Field("id") int id,
            @Field("name") String name,
            @Field("price") int price,
            @Field("unit") String unit
    );

    @DELETE("product/{id}")
    Call<ResponseBody> deleteProduct(
            @Path("id") int id
    );


}
