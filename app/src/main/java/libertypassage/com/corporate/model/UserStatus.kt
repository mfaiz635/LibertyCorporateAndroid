package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserStatus {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("sub_title")
    @Expose
    var subTitle: String? = null

    @SerializedName("no_of_locations")
    @Expose
    var noOfLocations: Int? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("alert_description")
    @Expose
    var alertDescription: String? = null

    @SerializedName("alert_bg_color")
    @Expose
    var alertBgColor: String? = null

    @SerializedName("alert_text_color")
    @Expose
    var alertTextColor: String? = null
}