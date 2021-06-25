package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DetailsAppVersion {

    @SerializedName("Android")
    @Expose
    var android: Float? = null

    @SerializedName("iOS")
    @Expose
    var iOS: Float? = null
}