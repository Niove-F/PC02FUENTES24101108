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
    // Usaremos la API gratuita que no requiere API Key compleja para desarrollo
    @GET("v4/latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String): CurrencyResponse
}

// 3. Objeto Singleton de Retrofit
object RetrofitClient {
    private const val BASE_URL = "https://api.exchangerate-api.com/"

    val apiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}