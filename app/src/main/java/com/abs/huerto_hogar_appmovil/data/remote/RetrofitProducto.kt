package com.abs.huerto_hogar_appmovil.data.remote

import com.abs.huerto_hogar_appmovil.data.remote.api.ProductoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java


object RetrofitProducto {
    private const val BASE_URL = "http://192.168.1.31:8080/"
    val apiProducto: ProductoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductoApi::class.java)
    }
}