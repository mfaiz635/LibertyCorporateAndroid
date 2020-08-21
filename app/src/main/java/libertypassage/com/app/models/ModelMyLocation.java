
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelMyLocation {


    private double latitudes, longitudes, altitudes;
    private String address, city, state, country, postalCode, knownName;



    public double getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(double latitudes) {
        this.latitudes = latitudes;
    }

    public double getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(double longitudes) {
        this.longitudes = longitudes;
    }

    public double getAltitudes() {
        return altitudes;
    }

    public void setAltitudes(double altitudes) {
        this.altitudes = altitudes;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getKnownName() {
        return knownName;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }

}
