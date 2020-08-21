package libertypassage.com.app.utilis;


public interface Constants{

    String mainUrl = "https://passageliberty.azurewebsites.net/api/";
    String ImageUrl = "https://passageliberty.azurewebsites.net/";


    String CHECK_USER_EXISTANCE = "checkUserExistance";
    String GET_OTP = "getOtp";
    String GET_EMAIL_OTP = "getEmailOtp";
    String RESEND_EMAIL_OTP = "resetEmailOtp";
    String UPDATE_EMAIL_VERIFICATION = "updateEmailVerification";
    String COUNTRY_CODE = "getCountries";
    String INDUSTRIES = "getIndustries";
    String INDUSTRIES_PROFESSIONS = "getIndustryProfessions";
    String PROFESSIONS = "getProfessions";
    String SIGN_UP = "addUser";
    String LOG_IN = "userLogin";
    String FORGOT_PASS = "resetPasswordOtp";
    String UPDATE_PASS = "updateUserPassword";
    String ADD_ADDRESS = "addUserAddress";
    String ADD_TEMP = "addUserTemp";
    String ADD_USER_CONF = "addUserConf";
    String USER_ENROLL_DECLARE = "addUserEnrollDeclare";
    String TRACK_USER_LOCATION = "addTrackUserLocation";
    String GET_INFECTED_STATUS = "getInfectedStatuses";
    String FALSE_INFECTED_ALERT = "addUserFalseInfectedAlert";
    String FALSE_LOCATION_ALERT = "addUserFalseLocationAlert";
    String ADDRESS_AND_TEMP = "addUserAddressAndTemp";
    String GET_USER = "getUser";
    String LOCATION_HISTORY = "getHotspots";
    String UPDATE_USER_PROFILE = "updateUser";
    String UPDATE_FIREBASE_TOKEN = "updateFirebaseToken";


    //Shared Pref Keys
    String KEY_DEVICEID = "device_id";
    String KEY_DEVICE_TYPE = "Android";
    String KEY_POFILE_PIC = "profile_pic";
    String KEY_FULLNAME = "full_name";
    String KEY_EMAIL = "user_email";
    String KEY_GENDER = "user_gender";
    String KEY_AGE_GROUP = "user_age";
    String KEY_PROF = "user_prof";
    String KEY_COUNTRY_ID = "country_id";
    String KEY_MOBILE = "user_mobile";
    String KEY_USERID = "user_id";
    String KEY_MY_TEMP = "my_temp";
    String KEY_TEMP_TYPE = "temp_type";
    String KEY_CURRENT_LOC = "corrent_location";
    String KEY_STATUS = "status";
    String KEY_CHANGE_STATUS = "change_status";
    String KEY_HOSPITAL_ADD = "hospital_address";
    String KEY_HOSPITAL_DATE = "hospital_date";
    String KEY_START = "start_id";
    String KEY_BEARER_TOKEN = "bearer_token";
    String KEY_BOT = "1";
    String KEY_HEADER = "Bearer ";
    String ERROR_MSG = "Finding server issue....please try later!";
    String APIVALUE = "SID]O.YI0j2z=Ba)7s,!IW`~IanI{m";
    String KEY_FOR_TITLE = "for_title";
    String KEY_LAT = "lat";
    String KEY_LONG = "long";
    String KEY_MY_LOCATION = "my_location";
}
