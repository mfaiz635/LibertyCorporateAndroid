package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TrackUserLocationDetails {
    @SerializedName("timer_countdown")
    @Expose
    var timerCountdown: String? = null
    @SerializedName("user_details")
    @Expose
    var userDetails: UserDetailsTrackUser? = null
}