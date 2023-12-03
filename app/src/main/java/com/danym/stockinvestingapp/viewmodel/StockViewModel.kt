package com.danym.stockinvestingapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.danym.stockinvestingapp.model.StockData
import com.danym.stockinvestingapp.network.getStockData2
import com.danym.stockinvestingapp.utility.getDateFormatted
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class StockViewModel : ViewModel() {
    private val stocks = mutableMapOf<String, StockData>()

    fun getStockPrices(
        symbol: String,
        lastNDays: Int,
        callback: (prices: List<Double>) -> Unit,
        useCache: Boolean = false
    ) {
        val containsActualDate = { date: String? -> date == getDateFormatted() }

        if (useCache && containsActualDate(stocks[symbol]?.historical?.getOrNull(0)?.date)) {
            // take from memory
            callback(stocks[symbol]!!.historical.map { it.close })
        } else if (false) { // TODO change condition
            // take from room
            callback(emptyList())
        } else {
            // take from network
            val from = getDateFormatted(LocalDate.now().minusDays(lastNDays.toLong()))
            val to = getDateFormatted()
            getStockData2(symbol, from, to, object : Callback<StockData> {
                override fun onResponse(
                    call: Call<StockData>,
                    response: Response<StockData>
                ) {
                    val body = response.body()
                    if (body?.historical != null && body.historical.isNotEmpty()) {
                        callback(body.historical.map { it.close })
                    }
                }

                override fun onFailure(call: Call<StockData>, t: Throwable) {
                    Log.i("info", "Fail to get info. Error: ${t.message}")
                }

            })
        }
    }
}