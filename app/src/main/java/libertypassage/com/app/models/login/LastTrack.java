
package libertypassage.com.app.models.login;

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
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("wifi_mac_address")
    @Expose
    private String wifiMacAddress;
    @SerializedName("ip_address")
    @Expose
    private String ipAddress;
    @SerializedName("geofence")
    @Expose
    private Integer geofence;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWifiMacAddress() {
        return wifiMacAddress;
    }

    public void setWifiMacAddress(String wifiMacAddress) {
        this.wifiMacAddress = wifiMacAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getGeofence() {
        return geofence;
    }

    public void setGeofence(Integer geofence) {
        this.geofence = geofence;
    }

}
