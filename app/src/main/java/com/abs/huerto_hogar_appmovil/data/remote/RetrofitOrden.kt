package com.abs.huerto_hogar_appmovil.data.remote

import com.abs.huerto_hogar_appmovil.data.remote.api.OrdenApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitOrden {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiOrden: OrdenApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrdenApi::class.java)
    }
}
