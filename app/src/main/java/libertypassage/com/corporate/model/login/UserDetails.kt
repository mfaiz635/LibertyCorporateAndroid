package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserDetails {
    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("email_id")
    @Expose
    var emailId: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("current_status")
    @Expose
    var currentStatus: Int? = null

    @SerializedName("risk_score")
    @Expose
    var riskScore: Int? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: String? = null

    @SerializedName("age_group")
    @Expose
    var ageGroup: String? = null

    @SerializedName("prof_id")
    @Expose
    var profId: Int? = null

    @SerializedName("industry_id")
    @Expose
    var industryId: Int? = null

    @SerializedName("email_verified")
    @Expose
    var emailVerified: Boolean? = null

    @SerializedName("country_id")
    @Expose
    var countryId: Int? = null

    @SerializedName("corp_id")
    @Expose
    var corpId: Int? = null

    @SerializedName("address")
    @Expose
    var address: Address? = null

    @SerializedName("confirmed")
    @Expose
    var confirmed: Confirmed? = null

    @SerializedName("temprature")
    @Expose
    var temprature: Temprature? = null

    @SerializedName("last_track")
    @Expose
    var lastTrack: LastTrack? = null

    @SerializedName("industry_title")
    @Expose
    var industryTitle: String? = null

    @SerializedName("profession_title")
    @Expose
    var professionTitle: String? = null

    @SerializedName("user_status")
    @Expose
    var userStatus: UserStatus? = null
}