package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VaccineName {
    @SerializedName("name")
    @Expose
    var name: String? = null
}