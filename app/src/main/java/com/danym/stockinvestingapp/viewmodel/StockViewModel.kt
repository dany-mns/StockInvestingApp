package com.danym.stockinvestingapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danym.stockinvestingapp.data.AppDatabase
import com.danym.stockinvestingapp.data.StockEntity
import com.danym.stockinvestingapp.network.getStockData
import com.danym.stockinvestingapp.utility.formatter
import com.danym.stockinvestingapp.utility.getDateFormatted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate

class StockViewModel : ViewModel() {
    private fun isDeviceConnectedToNetwork(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val actNw =
            cm.getNetworkCapabilities(cm.activeNetwork)
        return when {
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> true
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> true
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> true
            else -> false
        }
    }

    fun getStockPrices(
        context: Context, symbol: String, lastNDays: Int, callback: (prices: List<Double>) -> Unit
    ) {
        val room = AppDatabase.getInstance(context)
        val deviceIsConnectedToNetwork = isDeviceConnectedToNetwork(context)
        Log.i("info", "Device connectivity: $deviceIsConnectedToNetwork")


        if (!deviceIsConnectedToNetwork) {
            room.stockDao().getLastNStockData(symbol, lastNDays).onEach { stockResults ->
                callback(stockResults.map { it.price })
                Log.i(
                    "info",
                    "Using Room to get ${stockResults.size} results for $symbol."
                )
            }.launchIn(viewModelScope)

        } else {
            try {
                val from = getDateFormatted(LocalDate.now().minusDays(lastNDays.toLong()))
                val to = getDateFormatted()
                viewModelScope.launch(Dispatchers.IO) {
                    val body = getStockData(symbol, from, to)
                    Log.i(
                        "info",
                        "Using Retrofit to get ${body.historical.size} results for ${body.symbol}."
                    )
                    if (body.historical.isNotEmpty()) {
                        body.historical.forEach {
                            room.stockDao().insertStock(
                                StockEntity(
                                    body.symbol,
                                    it.close,
                                    LocalDate.parse(it.date, formatter)
                                )
                            )
                        }
                        callback(body.historical.map { it.close })
                    }
                }
            } catch (exception: Exception) {
                Log.i("info", "Fail to get info. Error: ${exception.message}")
            }
        }
    }
}