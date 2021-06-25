package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import libertypassage.com.corporate.model.login.UserDetails

class ConfirmDetails {
    @SerializedName("clinic_qr_image")
    @Expose
    var clinicQrImage: List<ClinicQrImage>? = null

    @SerializedName("user_details")
    @Expose
    var userDetails: UserDetails? = null
}