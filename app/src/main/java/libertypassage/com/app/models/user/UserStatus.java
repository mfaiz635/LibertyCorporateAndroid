
package libertypassage.com.app.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatus {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("no_of_people")
    @Expose
    private Integer noOfPeople;
    @SerializedName("no_of_locations")
    @Expose
    private Integer noOfLocations;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("alert_description")
    @Expose
    private String alertDescription;
    @SerializedName("alert_bg_color")
    @Expose
    private String alertBgColor;
    @SerializedName("alert_text_color")
    @Expose
    private String alertTextColor;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at_tz")
    @Expose
    private String updatedAtTz;
    @SerializedName("created_at_tz")
    @Expose
    private String createdAtTz;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(Integer noOfPeople) {
        this.noOfPeople = noOfPeople;
    }

    public Integer getNoOfLocations() {
        return noOfLocations;
    }

    public void setNoOfLocations(Integer noOfLocations) {
        this.noOfLocations = noOfLocations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlertDescription() {
        return alertDescription;
    }

    public void setAlertDescription(String alertDescription) {
        this.alertDescription = alertDescription;
    }

    public String getAlertBgColor() {
        return alertBgColor;
    }

    public void setAlertBgColor(String alertBgColor) {
        this.alertBgColor = alertBgColor;
    }

    public String getAlertTextColor() {
        return alertTextColor;
    }

    public void setAlertTextColor(String alertTextColor) {
        this.alertTextColor = alertTextColor;
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

    public String getUpdatedAtTz() {
        return updatedAtTz;
    }

    public void setUpdatedAtTz(String updatedAtTz) {
        this.updatedAtTz = updatedAtTz;
    }

    public String getCreatedAtTz() {
        return createdAtTz;
    }

    public void setCreatedAtTz(String createdAtTz) {
        this.createdAtTz = createdAtTz;
    }

}
