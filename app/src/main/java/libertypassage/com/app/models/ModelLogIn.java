
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelLogIn {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private LogInDetails logInDetails;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogInDetails getLogInDetails() {
        return logInDetails;
    }

    public void setLogInDetails(LogInDetails logInDetails) {
        this.logInDetails = logInDetails;
    }

}
