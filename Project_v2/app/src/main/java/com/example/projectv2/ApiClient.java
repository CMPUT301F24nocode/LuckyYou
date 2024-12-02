package com.example.projectv2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class to create a Retrofit client for making API requests to the Firebase server.
 */
public class ApiClient {
    private static final String BASE_URL = "https://firebase-server-zeta.vercel.app/";
    private static Retrofit retrofit;

    /**
     * Creates a Retrofit client for making API requests to the Firebase server.
     *
     * @return The Retrofit client instance.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
