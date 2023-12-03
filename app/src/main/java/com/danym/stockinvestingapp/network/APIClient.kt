package com.danym.stockinvestingapp.network

import com.danym.stockinvestingapp.model.StockData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StockInfoApi {
    @GET("/api/v3//historical-price-full/{symbol}?apikey=QZXaYu1lbCqqIqsBvLBRw1XEtHP7lMo4")
    fun getStockData(
        @Path("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<StockData>
}

fun getStockData(symbol: String, from: String, to: String): StockData? {
    val client = Retrofit.Builder().baseUrl("https://financialmodelingprep.com/")
        .addConverterFactory(JacksonConverterFactory.create()).build()
    val stockApi = client.create(StockInfoApi::class.java)
    return stockApi.getStockData(symbol, from, to).execute().body()
}

fun getStockData2(symbol: String, from: String, to: String, callback: Callback<StockData>) {
    val client = Retrofit.Builder().baseUrl("https://financialmodelingprep.com/")
        .addConverterFactory(JacksonConverterFactory.create()).build()
    val stockApi = client.create(StockInfoApi::class.java)
    stockApi.getStockData(symbol, from, to).enqueue(callback)
}
