package libertypassage.com.corporate.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import libertypassage.com.corporate.model.Industry
import libertypassage.com.corporate.retofit.RetrofitClient
import libertypassage.com.corporate.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



object IndustryListRepository {

    var model = MutableLiveData<Industry>()

    fun getServicesApiCall(): MutableLiveData<Industry> {
        val call = RetrofitClient.apiInterface.getIndustries(Constants.KEY_BOT)
        call.enqueue(object: Callback<Industry> {
            override fun onResponse(call: Call<Industry>?, response: Response<Industry>) {
                model.value = response.body()
                Log.e("IndustryList : ", response.body().toString())
            }

            override fun onFailure(call: Call<Industry>, t: Throwable) {
                Log.e("onFailureForgot : ", t.message.toString())
            }
        })
        return model
    }
}