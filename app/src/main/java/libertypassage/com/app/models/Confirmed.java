
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Confirmed {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("usr_id")
    @Expose
    private Integer usrId;
    @SerializedName("confirmed")
    @Expose
    private Integer confirmed;
    @SerializedName("clinic_qr_image")
    @Expose
    private Object clinicQrImage;
    @SerializedName("clinic_address")
    @Expose
    private String clinicAddress;
    @SerializedName("date")
    @Expose
    private String date;
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

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public Object getClinicQrImage() {
        return clinicQrImage;
    }

    public void setClinicQrImage(Object clinicQrImage) {
        this.clinicQrImage = clinicQrImage;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
