package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CorpDetails {
    @SerializedName("corp_id")
    @Expose
    var corpId = 0

    @SerializedName("usr_id")
    @Expose
    var usrId = 0

    @SerializedName("ref_no")
    @Expose
    var refNo: String? = null

    @SerializedName("industry_id")
    @Expose
    var industryId = 0

    @SerializedName("company_name")
    @Expose
    var companyName: String? = null

    @SerializedName("company_address")
    @Expose
    var companyAddress: String? = null

    @SerializedName("registration_number")
    @Expose
    var registrationNumber: String? = null

    @SerializedName("company_landline")
    @Expose
    var companyLandline: String? = null

    @SerializedName("contact_person_name")
    @Expose
    var contactPersonName: String? = null

    @SerializedName("contact_person_mobile")
    @Expose
    var contactPersonMobile: String? = null

    @SerializedName("contact_person_email")
    @Expose
    var contactPersonEmail: String? = null

    @SerializedName("staff_count")
    @Expose
    var staffCount = 0

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at_tz")
    @Expose
    var updatedAtTz: String? = null

    @SerializedName("created_at_tz")
    @Expose
    var createdAtTz: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

}