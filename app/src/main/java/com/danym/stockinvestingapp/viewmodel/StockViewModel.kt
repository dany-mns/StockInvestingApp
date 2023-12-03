package com.danym.stockinvestingapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import com.danym.stockinvestingapp.data.AppDatabase
import com.danym.stockinvestingapp.data.StockEntity
import com.danym.stockinvestingapp.model.StockData
import com.danym.stockinvestingapp.network.getStockData2
import com.danym.stockinvestingapp.utility.formatter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    @OptIn(DelicateCoroutinesApi::class)
    fun getStockPrices(
        context: Context, symbol: String, lastNDays: Int, callback: (prices: List<Double>) -> Unit
    ) {
        callback(emptyList())
        val room = AppDatabase.getInstance(context)
        val deviceIsConnectedToNetwork = isDeviceConnectedToNetwork(context)
        Log.i("info", "Device connectivity: $deviceIsConnectedToNetwork")


        if (!deviceIsConnectedToNetwork) {
            GlobalScope.launch(Dispatchers.IO) {
                val result = room.stockDao().getLastNStockData(symbol, lastNDays)
                Log.i(
                    "info",
                    "Using Room to get ${result.size} results for $symbol."
                )
                callback(result.map { it.price })
            }
        } else {
            getStockData2(symbol, lastNDays, object : Callback<StockData> {
                override fun onResponse(
                    call: Call<StockData>, response: Response<StockData>
                ) {
                    val body = response.body()
                    Log.i(
                        "info",
                        "Using Retrofit to get ${body?.historical?.size} results for ${body?.symbol}."
                    )
                    if (body?.historical != null && body.historical.isNotEmpty()) {
                        GlobalScope.launch(Dispatchers.IO) {
                            body.historical.forEach {
                                room.stockDao().insertStock(
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