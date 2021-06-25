package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailsOTP {
    @SerializedName("otp")
    @Expose
    var otp: Int? = null
}