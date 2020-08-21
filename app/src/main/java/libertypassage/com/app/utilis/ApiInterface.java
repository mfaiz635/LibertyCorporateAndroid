package libertypassage.com.app.utilis;


import libertypassage.com.app.models.Industry;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelConforme;
import libertypassage.com.app.models.ModelCountryList;
import libertypassage.com.app.models.ModelExistance;
import libertypassage.com.app.models.ModelInfectedStatuses;
import libertypassage.com.app.models.ModelLocationHistory;
import libertypassage.com.app.models.ModelLogIn;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.models.ModelReset;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.models.ModelSignUp;
import libertypassage.com.app.models.ModelStatusMsg;
import libertypassage.com.app.models.user.ModelUser;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    @POST(Constants.LOG_IN)
    @FormUrlEncoded
    Call<ModelLogIn> userLogIn(@Field("if_bot") String bot,
                               @Field("if_device_type") String if_device_type,
                               @Field("if_username") String username,
                               @Field("if_password") String password);

    @POST(Constants.CHECK_USER_EXISTANCE)
    @FormUrlEncoded
    Call<ModelExistance> checkUserExistance(@Field("if_bot") String if_bot,
                                            @Field("if_email_id") String if_email_id,
                                            @Field("if_phone_number") String if_phone_number);

    @POST(Constants.GET_OTP)
    @FormUrlEncoded
    Call<ModelOTP> getOtpResponse(@Field("if_bot") String if_bot,
                                  @Field("if_mobile_number") String mobile);

    @POST(Constants.GET_EMAIL_OTP)
    @FormUrlEncoded
    Call<ModelOTP> getOtpEmail(@Field("if_bot") String if_bot,
                               @Field("if_email_id") String if_email_id);

    @POST(Constants.RESEND_EMAIL_OTP)
    @FormUrlEncoded
    Call<ModelOTP> resendOtpEmail(@Field("if_bot") String if_bot,
                               @Field("if_email_id") String if_email_id);

    @POST(Constants.UPDATE_EMAIL_VERIFICATION)
    @FormUrlEncoded
    Call<ModelResponse> updateEmailVerification(@Header("Authorization") String auth,
                               @Field("if_bot") String if_bot,
                               @Field("if_email_verification") String if_email_verification);

    @POST(Constants.FORGOT_PASS)
    @FormUrlEncoded
    Call<ModelReset> forgotPassword(@Field("if_bot") String if_bot,
                                    @Field("if_mobile_number") String mobile);

    @POST(Constants.UPDATE_PASS)
    @FormUrlEncoded
    Call<ModelResponse> updateUserPassword(@Header("Authorization") String auth,
                                           @Field("if_bot") String if_bot,
                                           @Field("if_new_password") String password);

    @POST(Constants.UPDATE_FIREBASE_TOKEN)
    @FormUrlEncoded
    Call<ModelStatusMsg> updateFirebaseToken(@Field("if_bot") String if_bot,
                                             @Field("if_user_id") String if_user_id,
                                             @Field("if_api_key") String if_api_key,
                                             @Field("if_firebase_token") String firebaseToken);

    @POST(Constants.COUNTRY_CODE)
    @FormUrlEncoded
    Call<ModelCountryList> getCountries(@Field("if_bot") String if_bot);

    @POST(Constants.SIGN_UP)
    @Multipart
    Call<ModelSignUp> userSignUpImage(@Part("if_bot") RequestBody bot,
                                      @Part("if_first_name") RequestBody if_first_name,
                                      @Part("if_last_name") RequestBody if_last_name,
                                      @Part("if_email_id") RequestBody if_email_id,
                                      @Part("if_phone_number") RequestBody if_phone_number,
                                      @Part("if_password") RequestBody if_password,
                                      @Part("if_device_token") RequestBody if_device_token,
                                      @Part MultipartBody.Part file_image);

    @POST(Constants.SIGN_UP)
    @FormUrlEncoded
    Call<ModelSignUp> userSignUp(@Field("if_bot") String if_bot,
                                 @Field("if_device_type") String if_device_type,
                                 @Field("if_email_id") String if_email_id,
                                 @Field("if_phone_number") String if_phone_number,
                                 @Field("if_gender") String if_gender,
                                 @Field("if_age_group") String if_age_group,
                                 @Field("if_industry_id") String if_industry_id,
                                 @Field("if_profession_id") String if_profession_id,
                                 @Field("if_country_id") String if_country_id,
                                 @Field("if_password") String if_password,
                                 @Field("if_device_token") String if_device_token);

    @POST(Constants.INDUSTRIES)
    @FormUrlEncoded
    Call<Industry> getIndustries(@Field("if_bot") String if_bot );

    @POST(Constants.INDUSTRIES_PROFESSIONS)
    @FormUrlEncoded
    Call<IndustryProfessions> getIndustryProfessions(@Field("if_bot") String if_bot,
                                                     @Field("if_industry_id") String if_industry_id);

//    @POST(Constants.PROFESSIONS)
//    @FormUrlEncoded
//    Call<ModelResponse> getProfessions(@Field("if_bot") String if_bot );


    @POST(Constants.UPDATE_USER_PROFILE)
    @Multipart
    Call<ModelSignUp> upadteProfileImage(@Header("Authorization") String auth,
                                         @Part("if_bot") RequestBody bot,
                                         @Part("if_first_name") RequestBody if_first_name,
                                         @Part("if_last_name") RequestBody if_last_name,
                                         @Part("if_email_id") RequestBody if_email_id,
                                         @Part("if_phone_number") RequestBody if_phone_number,
                                         @Part MultipartBody.Part file_image);

    @POST(Constants.UPDATE_USER_PROFILE)
    @FormUrlEncoded
    Call<ModelSignUp> updateProfile(@Header("Authorization") String auth,
                                    @Field("if_bot") String if_bot,
                                    @Field("if_device_type") String if_device_type,
                                    @Field("if_email_id") String if_email_id,
                                    @Field("if_phone_number") String if_phone_number,
                                    @Field("if_gender") String if_gender,
                                    @Field("if_age_group") String if_age_group,
                                    @Field("if_industry_id") int if_industry_id,
                                    @Field("if_profession_id") int if_profession_id);


    @POST(Constants.ADD_USER_CONF)
    @FormUrlEncoded
    Call<ModelConforme> changeStatus(@Header("Authorization") String auth,
                                     @Field("if_bot") String if_bot,
                                     @Field("if_confirmed") String if_confirmed,
                                     @Field("if_clinic_address") String if_clinic_address,
                                     @Field("if_date") String if_date);

    @POST(Constants.ADD_TEMP)
    @FormUrlEncoded
    Call<ModelResponse> addUserTemp(@Header("Authorization") String auth,
                                    @Field("if_bot") String if_bot,
                                    @Field("if_temp_type") String if_temp_type,
                                    @Field("if_temprature") String if_temprature);

    @POST(Constants.LOCATION_HISTORY)
    @FormUrlEncoded
    Call<ModelLocationHistory> getLocationHistory(@Header("Authorization") String auth,
                                                  @Field("if_bot") String if_bot
                                    );

    @POST(Constants.TRACK_USER_LOCATION)
    @FormUrlEncoded
    Call<ModelUser> addTrackUserLocation(@Header("Authorization") String auth,
                                         @Field("if_bot") String if_bot,
                                         @Field("if_latitude") double if_latitude,
                                         @Field("if_longitude") double if_longitude,
                                         @Field("if_height_azimuth") double if_height_azimuth,
                                         @Field("if_mac_address") String if_mac_address,
                                         @Field("if_ip_address") String if_ip_address);

    @POST(Constants.ADD_ADDRESS)
    @FormUrlEncoded
    Call<ModelResponse> addUserAddress(@Header("Authorization") String auth,
                                       @Field("if_bot") String if_bot,
                                       @Field("if_address") String if_address,
                                       @Field("if_postcode") String if_postcode,
                                       @Field("if_town") String if_town);

    @POST(Constants.ADDRESS_AND_TEMP)
    @FormUrlEncoded
    Call<ModelResponse> addressAndTemp(@Header("Authorization") String auth,
                                       @Field("if_bot") String if_bot,
                                       @Field("if_address") String if_address,
                                       @Field("if_temp_type") String if_temp_type,
                                       @Field("if_temprature") String if_temprature);

    @POST(Constants.USER_ENROLL_DECLARE)
    @FormUrlEncoded
    Call<ModelResponse> addUserEnrollDeclare(@Header("Authorization") String auth,
                                             @Field("if_bot") String if_bot,
                                             @Field("if_dodge_14_days") String term1,
                                             @Field("if_terms_conditions") String term2,
                                             @Field("if_location_tracking") String term3,
                                             @Field("if_location_pre_infection") String term4);


    @POST(Constants.GET_INFECTED_STATUS)
    @FormUrlEncoded
    Call<ModelInfectedStatuses> getInfectedStatus(@Header("Authorization") String auth,
                                                  @Field("if_bot") String if_bot);

    @POST(Constants.GET_USER)
    @FormUrlEncoded
    Call<ModelUser> getUser(@Header("Authorization") String auth,
                            @Field("if_bot") String if_bot);

    @POST(Constants.FALSE_INFECTED_ALERT)
    @FormUrlEncoded
    Call<ModelResponse> falseInfectedAlert(@Header("Authorization") String auth,
                                           @Field("if_bot") String if_bot,
                                           @Field("if_confirmation") String if_confirmation,
                                           @Field("if_address") String if_address);

    @POST(Constants.FALSE_LOCATION_ALERT)
    @FormUrlEncoded
    Call<ModelResponse> falseLocationAlert(@Header("Authorization") String auth,
                                           @Field("if_bot") String if_bot,
                                           @Field("if_confirmation") String if_confirmation,
                                           @Field("if_address") String if_address);


//    @POST("api.php/")
//    @FormUrlEncoded
//    Call<List<ModelMerchansisers>> getMerchansisers(@Field("action") String action);
}