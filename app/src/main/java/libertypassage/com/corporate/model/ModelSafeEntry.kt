package libertypassage.com.corporate.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ModelSafeEntry(
    @field:Expose @field:SerializedName("address") var address: String,
    @field:Expose @field:SerializedName("tempType") var tempType: String,
    @field:Expose @field:SerializedName("currentTemp") var currentTemp: String,
    @field:Expose @field:SerializedName("venueId") var venueId: String,
    @field:Expose @field:SerializedName("nircId") var nircId: String,
    @field:Expose @field:SerializedName("currentDate") var currentDate: String,
    @field:Expose @field:SerializedName("checkout") var checkout: Int,
    @field:Expose @field:SerializedName("favorite") var favorite: Int
)
