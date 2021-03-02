package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryDetail {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("country_code")
    @Expose
    var countryCode: String? = null

    @SerializedName("country_name")
    @Expose
    var countryName: String? = null

    @SerializedName("currencies")
    @Expose
    var currencies: String? = null

    @SerializedName("flag")
    @Expose
    var flag: String? = null

    @SerializedName("callingcodes")
    @Expose
    var callingcodes: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("created_timestamp")
    @Expose
    var createdTimestamp: Int? = null
}