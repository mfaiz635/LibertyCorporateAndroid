package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailIndustryProf(
    @field:Expose @field:SerializedName("prof_id") var profId: Int,
    @field:Expose @field:SerializedName("industry_id") var industryId: Int,
    @field:Expose @field:SerializedName("title") var title: String
)