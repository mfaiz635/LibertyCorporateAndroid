package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Address {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("address")
    @Expose
    var address: String? = null
}