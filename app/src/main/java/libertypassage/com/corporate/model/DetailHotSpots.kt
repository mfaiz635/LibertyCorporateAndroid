package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailHotSpots {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("created_at_tz")
    @Expose
    var createdAtTz: String? = null

    @SerializedName("infections")
    @Expose
    var infections: Int? = null

    @SerializedName("country_name")
    @Expose
    var countryName: String? = null
}