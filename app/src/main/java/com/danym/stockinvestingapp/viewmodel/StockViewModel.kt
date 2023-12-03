package com.danym.stockinvestingapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.danym.stockinvestingapp.data.AppDatabase
import com.danym.stockinvestingapp.data.StockEntity
import com.danym.stockinvestingapp.model.StockData
import com.danym.stockinvestingapp.network.getStockData2
import com.danym.stockinvestingapp.utility.formatter
import com.danym.stockinvestingapp.utility.getDateFormatted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class StockViewModel : ViewModel() {
    private val stocks = mutableMapOf<String, StockData>()

    fun getStockPrices(
        context: Context,
        symbol: String,
        lastNDays: Int,
        callback: (prices: List<Double>) -> Unit,
        useCache: Boolean = false
    ) {
        val containsActualDate = { date: String? -> date == getDateFormatted() }

        if (useCache && containsActualDate(stocks[symbol]?.historical?.getOrNull(0)?.date)) {
            // take from memory
            Log.i("info", "Use caching for stock: $symbol")
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
                        val room = AppDatabase.getInstance(context)
                        GlobalScope.launch(Dispatchers.IO) {
                            body.historical.forEach {
                                room.stockDao()
                                    .insertStock(
                                        StockEntity(
                                            body.symbol,
                                            it.close,
                                            LocalDate.parse(it.date, formatter)
                                        )
                                    )
                            }
                        }
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