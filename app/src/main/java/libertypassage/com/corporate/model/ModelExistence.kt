package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelExistence {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("details")
    @Expose
    var details: DetailsErrorTrue? = null
}