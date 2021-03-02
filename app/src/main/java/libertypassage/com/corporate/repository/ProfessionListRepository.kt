package libertypassage.com.corporate.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import libertypassage.com.corporate.model.IndustryProfessions
import libertypassage.com.corporate.retofit.RetrofitClient
import libertypassage.com.corporate.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



object ProfessionListRepository {

    var model = MutableLiveData<IndustryProfessions>()

    fun getServicesApiCall(industryId: String): MutableLiveData<IndustryProfessions> {
        val call = RetrofitClient.apiInterface.getIndustryProfessions(Constants.KEY_BOT, industryId)
        call.enqueue(object: Callback<IndustryProfessions> {
            override fun onResponse(call: Call<IndustryProfessions>?, response: Response<IndustryProfessions>) {
                model.value = response.body()
            }

            override fun onFailure(call: Call<IndustryProfessions>, t: Throwable) {
                Log.e("onFailureForgot : ", t.message.toString())
            }
        })
        return model
    }
}