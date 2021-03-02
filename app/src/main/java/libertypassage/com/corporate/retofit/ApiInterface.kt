package libertypassage.com.corporate.retofit


import libertypassage.com.corporate.model.*
import libertypassage.com.corporate.model.login.ModelLogin
import libertypassage.com.corporate.utilities.Constants
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiInterface {

    @POST(Constants.LOG_IN)
    @FormUrlEncoded
    fun userLogIn(
        @Field("if_bot") bot: String?,
        @Field("if_device_type") if_device_type: String?,
        @Field("if_device_token") if_device_token: String?,
        @Field("if_username") username: String?,
        @Field("if_password") password: String?
    ): Call<ModelLogin>

    @POST(Constants.CHECK_USER_EXISTENCE)
    @FormUrlEncoded
    fun checkUserExistence(
        @Field("if_bot") if_bot: String?,
        @Field("if_email_id") if_email_id: String?,
        @Field("if_phone_number") if_phone_number: String?
    ): Call<ModelExistence>

    @POST(Constants.INDUSTRIES)
    @FormUrlEncoded
    fun getIndustries(@Field("if_bot") if_bot: String?): Call<Industry>

    @POST(Constants.INDUSTRIES_PROFESSIONS)
    @FormUrlEncoded
    fun getIndustryProfessions(
        @Field("if_bot") if_bot: String?,
        @Field("if_industry_id") if_industry_id: String?
    ): Call<IndustryProfessions>

    @POST(Constants.SIGN_UP)
    @FormUrlEncoded
    fun userSignUp(
        @Field("if_bot") if_bot: String?,
        @Field("if_device_type") if_device_type: String?,
        @Field("if_email_id") if_email_id: String?,
        @Field("if_phone_number") if_phone_number: String?,
        @Field("if_gender") if_gender: String?,
        @Field("if_age_group") if_age_group: String?,
        @Field("if_industry_id") if_industry_id: String?,
        @Field("if_profession_id") if_profession_id: String?,
        @Field("if_country_id") if_country_id: String?,
        @Field("if_password") if_password: String?,
        @Field("if_device_token") if_device_token: String?,
        @Field("if_corp_id") if_corp_id: String?,
        @Field("if_email_verified") if_email_verified: String?
    ): Call<ModelSignUp>

    @POST(Constants.GET_OTP)
    @FormUrlEncoded
    fun sendOtp(
        @Field("if_bot") if_bot: String?,
        @Field("if_mobile_number") mobile: String?
    ): Call<ModelOTP>

    @POST(Constants.GET_EMAIL_OTP)
    @FormUrlEncoded
    fun emailOtp(
        @Field("if_bot") if_bot: String?,
        @Field("if_email_id") if_email_id: String?
    ): Call<ModelOTP>

    @POST(Constants.RESEND_EMAIL_OTP)
    @FormUrlEncoded
    fun resendEmailOtp(
        @Field("if_bot") if_bot: String?,
        @Field("if_email_id") if_email_id: String?
    ): Call<ModelOTP>

    @POST(Constants.UPDATE_EMAIL_VERIFICATION)
    @FormUrlEncoded
    fun emailVerification(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_email_verification") if_email_verification: String?
    ): Call<ModelResponse>

    @POST(Constants.FORGOT_PASS)
    @FormUrlEncoded
    fun forgotPassword(
        @Field("if_bot") ifBot: String,
        @Field("if_mobile_number") mobile: String
    ): Call<ModelReset>

    @POST(Constants.UPDATE_PASS)
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_new_password") password: String?
    ): Call<ModelResponse>

    @POST(Constants.UPDATE_USER_PROFILE)
    @FormUrlEncoded
    fun updateProfile(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_device_type") if_device_type: String?,
        @Field("if_email_id") if_email_id: String?,
        @Field("if_phone_number") if_phone_number: String?,
        @Field("if_gender") if_gender: String?,
        @Field("if_age_group") if_age_group: String?,
        @Field("if_industry_id") if_industry_id: String?,
        @Field("if_profession_id") if_profession_id: String?
    ): Call<ModelSignUp>

    @POST(Constants.USER_ENROLL_DECLARE)
    @FormUrlEncoded
    fun enrollDeclare(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_dodge_14_days") term1: String?,
        @Field("if_terms_conditions") term2: String?,
        @Field("if_location_tracking") term3: String?,
        @Field("if_location_pre_infection") term4: String?
    ): Call<ModelResponse>

    @POST(Constants.ADD_USER_CONF)
    @FormUrlEncoded
    fun changeStatus(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_confirmed") if_confirmed: String?,
        @Field("if_vaccine_id") if_vaccine_id: String?,
        @Field("if_clinic_address") if_clinic_address: String?,
        @Field("if_date") if_date: String?
    ): Call<ModelConforme>

    @POST(Constants.ADD_TEMP)
    @FormUrlEncoded
    fun addTemp(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_temp_type") if_temp_type: String?,
        @Field("if_temprature") if_temprature: String?
    ): Call<ModelResponse>

    @POST(Constants.LOCATION_HISTORY)
    @FormUrlEncoded
    fun getLocationHistory(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?
    ): Call<ModelHotSpots>

    @POST(Constants.TRACK_USER_LOCATION)
    @FormUrlEncoded
    fun trackUserLocation(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_latitude") if_latitude: Double,
        @Field("if_longitude") if_longitude: Double,
        @Field("if_height_azimuth") if_height_azimuth: Double,
        @Field("if_mac_address") if_mac_address: String?,
        @Field("if_ip_address") if_ip_address: String?,
        @Field("if_corp_id") if_corp_id: String?
    ): Call<ModelTrackUserLocation>

    @POST(Constants.ADD_ADDRESS)
    @FormUrlEncoded
    fun addAddress(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_address") if_address: String?,
        @Field("if_postcode") if_postcode: String?,
        @Field("if_town") if_town: String?
    ): Call<ModelResponse>

    @POST(Constants.ADDRESS_AND_TEMP)
    @FormUrlEncoded
    fun addAddressTemp(
        @Header("Authorization") auth: String?,
        @Field("if_bot") if_bot: String?,
        @Field("if_address") if_address: String?,
        @Field("if_temp_type") if_temp_type: String?,
        @Field("if_temprature") if_temperature: String?,
        @Field("venueId") venueId: String?,
        @Field("sub") sub: String,
        @Field("actionType") actionType: String?
    ): Call<ModelResponse>

    @POST(Constants.GET_VACCINES)
    @FormUrlEncoded
    fun getVaccines(@Field("if_bot") if_bot: String?): Call<Vaccines>

    @POST(Constants.VERIFY_EMPLOYEE)
    @FormUrlEncoded
    fun verifyEmployee(
        @Field("if_bot") if_bot: String?,
        @Field("if_mobile_number") if_mobile_number: String?,
        @Field("if_otp") if_otp: String?
    ): Call<CorporateEmployee>

    @POST(Constants.GET_APP_VERSION)
    @FormUrlEncoded
    fun getAppVersion(@Field("if_bot") if_bot: String?): Call<ModelAppVersion>
}