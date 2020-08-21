
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("currencies")
    @Expose
    private String currencies;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("callingcodes")
    @Expose
    private Integer callingcodes;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_timestamp")
    @Expose
    private Integer createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getCallingcodes() {
        return callingcodes;
    }

    public void setCallingcodes(Integer callingcodes) {
        this.callingcodes = callingcodes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Integer createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

}
