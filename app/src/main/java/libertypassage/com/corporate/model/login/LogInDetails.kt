package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogInDetails {
    @SerializedName("bearer_token")
    @Expose
    var bearerToken: String? = null

    @SerializedName("user_details")
    @Expose
    var userDetails: UserDetails? = null
}