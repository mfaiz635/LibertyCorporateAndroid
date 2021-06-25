package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserDetailsTrackUser {
    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("current_status")
    @Expose
    var currentStatus: Int? = null

    @SerializedName("risk_score")
    @Expose
    var riskScore: Int? = null

    @SerializedName("user_status")
    @Expose
    var userStatus: UserStatus? = null
}