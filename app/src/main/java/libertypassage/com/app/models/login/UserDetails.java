
package libertypassage.com.app.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("usr_id")
    @Expose
    private Integer usrId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("current_status")
    @Expose
    private Integer currentStatus;
    @SerializedName("risk_score")
    @Expose
    private Integer riskScore;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("age_group")
    @Expose
    private String ageGroup;
    @SerializedName("prof_id")
    @Expose
    private Integer profId;
    @SerializedName("industry_id")
    @Expose
    private Integer industryId;
    @SerializedName("email_verified")
    @Expose
    private Boolean emailVerified;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("corp_id")
    @Expose
    private Integer corpId;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("enroll_decl")
    @Expose
    private EnrollDecl enrollDecl;
    @SerializedName("confirmed")
    @Expose
    private Confirmed confirmed;
    @SerializedName("temprature")
    @Expose
    private Temprature temprature;
    @SerializedName("last_track")
    @Expose
    private LastTrack lastTrack;
    @SerializedName("industry_title")
    @Expose
    private String industryTitle;
    @SerializedName("profession_title")
    @Expose
    private String professionTitle;
    @SerializedName("user_status")
    @Expose
    private UserStatus userStatus;

    public Integer getUsrId() {
        return usrId;
    }

    public void setUsrId(Integer usrId) {
        this.usrId = usrId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Integer getProfId() {
        return profId;
    }

    public void setProfId(Integer profId) {
        this.profId = profId;
    }

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public EnrollDecl getEnrollDecl() {
        return enrollDecl;
    }

    public void setEnrollDecl(EnrollDecl enrollDecl) {
        this.enrollDecl = enrollDecl;
    }

    public Confirmed getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Confirmed confirmed) {
        this.confirmed = confirmed;
    }

    public Temprature getTemprature() {
        return temprature;
    }

    public void setTemprature(Temprature temprature) {
        this.temprature = temprature;
    }

    public LastTrack getLastTrack() {
        return lastTrack;
    }

    public void setLastTrack(LastTrack lastTrack) {
        this.lastTrack = lastTrack;
    }

    public String getIndustryTitle() {
        return industryTitle;
    }

    public void setIndustryTitle(String industryTitle) {
        this.industryTitle = industryTitle;
    }

    public String getProfessionTitle() {
        return professionTitle;
    }

    public void setProfessionTitle(String professionTitle) {
        this.professionTitle = professionTitle;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

}
