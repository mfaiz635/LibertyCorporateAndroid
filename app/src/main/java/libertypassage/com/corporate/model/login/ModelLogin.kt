package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelLogin {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("details")
    @Expose
    var details: LogInDetails? = null
}