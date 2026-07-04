package com.example.pc02fuentes24101108.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 1. Modelo de respuesta de la API
data class CurrencyResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)

// 2. Interfaz de endpoints
interface CurrencyApiService {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): CurrencyResponse
}

// 3. Objeto Singleton de Retrofit
object RetrofitClient {
    const val API_KEY = "5077af16fdde759828fe12d0"
    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    val apiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}