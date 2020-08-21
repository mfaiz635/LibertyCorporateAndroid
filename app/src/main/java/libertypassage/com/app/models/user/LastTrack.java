
package libertypassage.com.app.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastTrack {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("usr_id")
    @Expose
    private Integer usrId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("height_azimuth")
    @Expose
    private String heightAzimuth;
    @SerializedName("qr_image")
    @Expose
    private Object qrImage;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_at_tz")
    @Expose
    private String createdAtTz;

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHeightAzimuth() {
        return heightAzimuth;
    }

    public void setHeightAzimuth(String heightAzimuth) {
        this.heightAzimuth = heightAzimuth;
    }

    public Object getQrImage() {
        return qrImage;
    }

    public void setQrImage(Object qrImage) {
        this.qrImage = qrImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtTz() {
        return createdAtTz;
    }

    public void setCreatedAtTz(String createdAtTz) {
        this.createdAtTz = createdAtTz;
    }

}
