
package libertypassage.com.app.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogInDetails {

    @SerializedName("bearer_token")
    @Expose
    private String bearerToken;
    @SerializedName("user_details")
    @Expose
    private UserDetails userDetails;

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

}
