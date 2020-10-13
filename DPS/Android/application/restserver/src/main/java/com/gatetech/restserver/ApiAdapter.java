package com.gatetech.restserver;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAdapter  {

    private static IApiService API_SERVICE;


    public void ApiAdapter(String baseUrl){


    }

    public void ApiAdapter(String baseUrl,String user,String password){


    }



    public static IApiService getApiService(String baseUrl) {

        // Creamos un interceptor y le indicamos el log level a usar
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Asociamos el interceptor a las peticiones
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //httpClient.addInterceptor(new BasicAuthInterceptor("", ""));

        if (API_SERVICE == null) {
            Retrofit retrofit;
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // <-- usamos el log level
                    .build();
            API_SERVICE = retrofit.create(IApiService.class);
        }

        return API_SERVICE;
    }

}
