package libertypassage.com.corporate.retofit

import libertypassage.com.corporate.utilities.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClientInstance {
    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit?
        get() {
            val okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(Constants.mainUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit
        }
}