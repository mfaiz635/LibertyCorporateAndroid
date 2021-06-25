package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TrackUserLocationDetails {
    @SerializedName("user_details")
    @Expose
    var userDetails: UserDetailsTrackUser? = null
}