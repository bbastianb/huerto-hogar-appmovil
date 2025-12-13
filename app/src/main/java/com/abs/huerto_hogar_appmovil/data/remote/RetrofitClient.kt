package com.abs.huerto_hogar_appmovil.data.remote

import com.abs.huerto_hogar_appmovil.data.remote.api.UsuarioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.31:8080/"

    private val client = OkHttpClient.Builder()
        .build()

    val usuarioApi: UsuarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioApi::class.java)
    }
}
