
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnrollDecl {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("usr_id")
    @Expose
    private Integer usrId;
    @SerializedName("dodge_14_days")
    @Expose
    private Integer dodge14Days;
    @SerializedName("terms_conditions")
    @Expose
    private Integer termsConditions;
    @SerializedName("location_tracking")
    @Expose
    private Integer locationTracking;
    @SerializedName("location_pre_infection")
    @Expose
    private Integer locationPreInfection;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsrId() {
        return usrId;
    }

    public void setUsrId(Integer usrId) {
        this.usrId = usrId;
    }

    public Integer getDodge14Days() {
        return dodge14Days;
    }

    public void setDodge14Days(Integer dodge14Days) {
        this.dodge14Days = dodge14Days;
    }

    public Integer getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(Integer termsConditions) {
        this.termsConditions = termsConditions;
    }

    public Integer getLocationTracking() {
        return locationTracking;
    }

    public void setLocationTracking(Integer locationTracking) {
        this.locationTracking = locationTracking;
    }

    public Integer getLocationPreInfection() {
        return locationPreInfection;
    }

    public void setLocationPreInfection(Integer locationPreInfection) {
        this.locationPreInfection = locationPreInfection;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
