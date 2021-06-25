package libertypassage.com.corporate.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LastTrack {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("usr_id")
    @Expose
    var usrId: Int? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("height_azimuth")
    @Expose
    var heightAzimuth: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("wifi_mac_address")
    @Expose
    var wifiMacAddress: String? = null

    @SerializedName("ip_address")
    @Expose
    var ipAddress: String? = null

    @SerializedName("geofence")
    @Expose
    var geofence: Int? = null
}