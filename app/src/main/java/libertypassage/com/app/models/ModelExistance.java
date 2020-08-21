
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelExistance {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private DetailsErrorTrue details;

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

    public DetailsErrorTrue getDetails() {
        return details;
    }

    public void setDetails(DetailsErrorTrue details) {
        this.details = details;
    }

}
