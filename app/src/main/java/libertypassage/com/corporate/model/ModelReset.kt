package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelReset {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("bearer_token")
    @Expose
    var bearer_token: String? = null

    @SerializedName("otp")
    @Expose
    var otp = 0

    @SerializedName("details")
    @Expose
    var details: DetailsResetPass? = null
}