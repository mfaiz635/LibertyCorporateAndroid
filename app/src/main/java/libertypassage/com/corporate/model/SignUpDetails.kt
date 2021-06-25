package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpDetails {
    @SerializedName("bearer_token")
    @Expose
    var bearerToken: String? = null
}