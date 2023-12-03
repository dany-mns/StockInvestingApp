package com.danym.stockinvestingapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.danym.stockinvestingapp.model.StockData
import com.danym.stockinvestingapp.network.getStockData
import com.danym.stockinvestingapp.utility.getDateFormatted
import java.time.LocalDate

class StockViewModel : ViewModel() {
    private val stocks = mutableMapOf<String, StockData>()

    fun getStockPrices(symbol: String, lastNDays: Int, useCache: Boolean = false): List<Double> {
        val containsActualDate = { date: String? -> date == getDateFormatted() }

        if (useCache && containsActualDate(stocks[symbol]?.historical?.getOrNull(0)?.date)) {
            // take from memory
            return stocks[symbol]!!.historical.map { it.close }
        } else if (false) { // TODO change condition
            // take from room
            return emptyList()
        } else {
            // take from network
            val from = getDateFormatted(LocalDate.now().minusDays(lastNDays.toLong()))
            val to = getDateFormatted()
            val result = getStockData(symbol, from, to)
            return if (result != null) {
                stocks[symbol] = result
                result.historical.map { it.close }
            } else {
                Log.i("info", "Fail to get stock data for $symbol with dates: $from -> $to")
                emptyList()
            }
        }
    }
}