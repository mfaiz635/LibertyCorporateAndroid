package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Temprature {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("temp_type")
    @Expose
    var tempType: String? = null

    @SerializedName("temprature")
    @Expose
    var temprature: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null
}