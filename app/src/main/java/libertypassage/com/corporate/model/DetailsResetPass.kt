package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import libertypassage.com.corporate.model.login.UserDetails


class DetailsResetPass {
    @SerializedName("bearer_token")
    @Expose
    var bearer_token: String? = null

    @SerializedName("otp")
    @Expose
    var otp: Int? = null

    @SerializedName("user_details")
    @Expose
    var details: UserDetails? = null
}