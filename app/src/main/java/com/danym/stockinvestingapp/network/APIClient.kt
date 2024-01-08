package com.danym.stockinvestingapp.network

import com.danym.stockinvestingapp.model.StockData
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object Network {
    val api by lazy {
        Retrofit.Builder().baseUrl("https://financialmodelingprep.com/")
            .addConverterFactory(JacksonConverterFactory.create()).build()
            .create(StockInfoApi::class.java)
    }
}

interface StockInfoApi {
    @GET("/api/v3//historical-price-full/{symbol}?apikey=QZXaYu1lbCqqIqsBvLBRw1XEtHP7lMo4")
    suspend fun getStockData(
        @Path("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): StockData
}

suspend fun getStockData(symbol: String, from: String, to: String): StockData {
    return Network.api.getStockData(symbol, from, to)
}
