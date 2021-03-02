package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailVaccines(
    @field:Expose @field:SerializedName("id") var id: Int,
    @field:Expose @field:SerializedName("name" ) var name: String
)