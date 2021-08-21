package libertypassage.com.corporate.utilities

import libertypassage.com.corporate.model.ModelSafeEntry
import java.util.ArrayList


interface Constants {
    companion object {
        const val mainUrl = "https://passageliberty.azurewebsites.net/api/"
        const val ImageUrl = "https://passageliberty.azurewebsites.net/"
//        const val mainUrl = "https://passageliberty.azurewebsites.net/dev/api/"

        const val CHECK_USER_EXISTENCE = "checkUserExistance"
        const val GET_OTP = "getOtp"
        const val INDUSTRIES = "getIndustries"
        const val INDUSTRIES_PROFESSIONS = "getIndustryProfessions"
        const val SIGN_UP = "addUser"
        const val LOG_IN = "userLogin"
        const val FORGOT_PASS = "resetPasswordOtp"
        const val UPDATE_PASS = "updateUserPassword"
        const val ADD_USER_CONF = "addUserConf"
        const val USER_ENROLL_DECLARE = "addUserEnrollDeclare"
        const val TRACK_USER_LOCATION = "addTrackUserLocation"
        const val ADDRESS_AND_TEMP = "addUserAddressAndTemp"
        const val LOCATION_HISTORY = "getHotspots"
        const val UPDATE_USER_PROFILE = "updateUser"
        const val GET_VACCINES = "getVaccines"
        const val VERIFY_EMPLOYEE = "verifyEmployee"
        const val GET_APP_VERSION = "getcurrentversion"


//        const val GET_EMAIL_OTP = "getEmailOtp"
//        const val RESEND_EMAIL_OTP = "resetEmailOtp"
//        const val UPDATE_EMAIL_VERIFICATION = "updateEmailVerification"
//        const val ADD_TEMP = "addUserTemp"
//        const val ADD_ADDRESS = "addUserAddress"
//        const val GET_USER = "getUser"
//        const val PROFESSIONS = "getProfessions"
//        const val COUNTRY_CODE = "getCountries"
//        const val GET_INFECTED_STATUS = "getInfectedStatuses"
//        const val FALSE_INFECTED_ALERT = "addUserFalseInfectedAlert"
//        const val FALSE_LOCATION_ALERT = "addUserFalseLocationAlert"



        //Shared Pref Keys
        const val KEY_DEVICE_TYPE = "Android"
        const val KEY_FULLNAME = "full_name"
        const val KEY_EMAIL = "user_email"
        const val KEY_GENDER = "user_gender"
        const val KEY_AGE_GROUP = "user_age"
        const val KEY_INDUSTRY = "industry_id"
        const val KEY_PROF = "user_prof"
        const val KEY_COUNTRY_ID = "country_id"
        const val KEY_MOBILE = "user_mobile"
        const val KEY_EMAIL_VERIFIED = "email_verified"
        const val KEY_MY_TEMP = "my_temp"
        const val KEY_TEMP_TYPE = "temp_type"
        const val KEY_CURRENT_LOC = "current_location"
        const val KEY_CONFIRMATION = "confirmation"
        const val KEY_CHANGE_STATUS = "change_status"
        const val KEY_HOSPITAL_ADD = "hospital_address"
        const val KEY_HOSPITAL_DATE = "hospital_date"
        const val KEY_VACCINE_ID = "vaccine_id"
        const val KEY_VACCINE_NAME = "vaccine_name"
        const val KEY_START = "start_id"
        const val KEY_BEARER_TOKEN = "bearer_token"
        const val KEY_BOT = "1"
        const val KEY_HEADER = "Bearer "
        const val KEY_RISK_SCORE = "risk_score"
        const val KEY_CURRENT_STATUS = "current_status"
        const val KEY_TITLE = "title"
        const val KEY_SUB_TITLE = "sub_title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_ALERT_DESCRIPTION = "alert_description"
        const val KEY_LAT = "latitude"
        const val KEY_LONG = "longitude"
        const val KEY_ALT = "altitudes"
        const val KEY_MY_LOCATION = "my_location"
        const val KEY_CORPORATION_ID = "corporation_id"
        const val KEY_NRIC_ID = "nric_id"
        const val PERMISSION_CODE = 100
        const val KEY_TERMS_ACCEPT = "terms_accept_corporate"
        var safeEntryArrayList: ArrayList<ModelSafeEntry> = ArrayList<ModelSafeEntry>()
//        Log.e("ModelSignUp", Gson().toJson(modelResponse))
    }
}