
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailsResetPass {

    @SerializedName("bearer_token")
    @Expose
    private String bearer_token;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("user_details")
    @Expose
    private UserDetails details;

    public String getBearer_token() {
        return bearer_token;
    }

    public void setBearer_token(String bearer_token) {
        this.bearer_token = bearer_token;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public UserDetails getDetails() {
        return details;
    }

    public void setDetails(UserDetails details) {
        this.details = details;
    }
}
