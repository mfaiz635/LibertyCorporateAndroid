
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackUserLocationDetails {

    @SerializedName("user_details")
    @Expose
    private UserDetailsTrackUser userDetails;

    public UserDetailsTrackUser getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsTrackUser userDetails) {
        this.userDetails = userDetails;
    }

}
