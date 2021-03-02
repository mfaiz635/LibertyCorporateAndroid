package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import libertypassage.com.corporate.model.login.*


class Details {
    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("middle_name")
    @Expose
    var middleName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("landline_number")
    @Expose
    var landlineNumber: String? = null

    @SerializedName("email_id")
    @Expose
    var emailId: String? = null

    @SerializedName("gov_id")
    @Expose
    var govId: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("profile_picture")
    @Expose
    var profilePicture: Any? = null

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: Any? = null

    @SerializedName("current_status")
    @Expose
    var currentStatus: Int? = null

    @SerializedName("risk_score")
    @Expose
    var riskScore: Int? = null

    @SerializedName("role")
    @Expose
    var role: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("address")
    @Expose
    var address: Address? = null

    @SerializedName("enroll_decl")
    @Expose
    var enrollDecl: EnrollDecl? = null

    @SerializedName("confirmed")
    @Expose
    var confirmed: Confirmed? = null

    @SerializedName("temprature")
    @Expose
    var temprature: Temprature? = null

    @SerializedName("last_track")
    @Expose
    var lastTrack: LastTrack? = null
}