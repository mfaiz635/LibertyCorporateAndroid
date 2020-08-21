
package libertypassage.com.app.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("usr_id")
    @Expose
    private Integer usrId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("landline_number")
    @Expose
    private String landlineNumber;
    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("gov_id")
    @Expose
    private String govId;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("current_status")
    @Expose
    private Integer currentStatus;
    @SerializedName("risk_score")
    @Expose
    private Integer riskScore;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_at_tz")
    @Expose
    private String createdAtTz;
    @SerializedName("age_group")
    @Expose
    private String ageGroup;
    @SerializedName("prof_id")
    @Expose
    private Integer professionId;
    @SerializedName("industry_id")
    @Expose
    private Integer industryId;
    @SerializedName("email_verified")
    @Expose
    private Boolean emailVerified;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLandlineNumber() {
        return landlineNumber;
    }

    public void setLandlineNumber(String landlineNumber) {
        this.landlineNumber = landlineNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getGovId() {
        return govId;
    }

    public void setGovId(String govId) {
        this.govId = govId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getCreatedAtTz() {
        return createdAtTz;
    }

    public void setCreatedAtTz(String createdAtTz) {
        this.createdAtTz = createdAtTz;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
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

    public void setCreatedAtTz(Boolean emailVerified) {
        this.emailVerified = emailVerified;
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

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

}
