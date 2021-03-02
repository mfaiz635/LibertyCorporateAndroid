package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CorporateEmployee {
    @SerializedName("error")
    @Expose
    var error = false

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("details")
    @Expose
    var details: CorporateDetails? = null

}