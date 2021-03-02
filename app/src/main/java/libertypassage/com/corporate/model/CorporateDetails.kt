package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CorporateDetails {
    @SerializedName("corp_details")
    @Expose
    var corpDetails: CorpDetails? = null

    @SerializedName("user_exists")
    @Expose
    var userExists = false
        set(error) {
            field = userExists
        }

}