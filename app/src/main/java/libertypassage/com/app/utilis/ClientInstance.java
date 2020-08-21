package libertypassage.com.app.utilis;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ClientInstance {

    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.mainUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


//    private static Retrofit retrofit1;
//    public static Retrofit getRetrofitInstance1() {
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        if (retrofit1 == null) {
//            retrofit1 = new Retrofit.Builder()
//                    .baseUrl("https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/")
//                    .client(okHttpClient)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();
//        }
//        return retrofit1;
//    }



}