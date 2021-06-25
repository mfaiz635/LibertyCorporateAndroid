package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClinicQrImage(
    @field:Expose @field:SerializedName("id") var id: Int,
    @field:Expose @field:SerializedName("usr_id") var usrId: Int,
    @field:Expose @field:SerializedName("clinic_qr_image") var clinicQrImage: String,
    @field:Expose @field:SerializedName("date") var date: String,
    @field:Expose @field:SerializedName("updated_at") var updatedAt: String,
    @field:Expose @field:SerializedName("created_at") var createdAt: String
) : Serializable



