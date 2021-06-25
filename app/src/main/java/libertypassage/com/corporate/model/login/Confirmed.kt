package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Confirmed {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("clinic_qr_image")
    @Expose
    var clinicQrImage: Any? = null

    @SerializedName("clinic_address")
    @Expose
    var clinicAddress: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("vaccine_id")
    @Expose
    var vaccineId: Int? = null

    @SerializedName("confirmation")
    @Expose
    var confirmation: Int? = null

    @SerializedName("vaccine_name")
    @Expose
    var vaccineName: VaccineName? = null

}