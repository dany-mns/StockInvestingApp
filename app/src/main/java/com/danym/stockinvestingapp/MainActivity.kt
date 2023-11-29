package com.danym.stockinvestingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.danym.stockinvestingapp.model.StockInfo
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private val BASE_URL =
        "https://financialmodelingprep.com/api/v3/profile/"

    interface StockInfoApi {
        @GET("AAPL?apikey=QZXaYu1lbCqqIqsBvLBRw1XEtHP7lMo4")
        fun getStockInfo(): Call<List<StockInfo>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockInvestingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        val executor: Executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val client = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
            val stockApi = client.create(StockInfoApi::class.java)
            val result = stockApi.getStockInfo().execute()
            val stockInfoBody: List<StockInfo>? = result.body()

//            if (stockInfoBody.isNullOrEmpty() || true) {
//                Toast.makeText(this, "No information about stock: X", Toast.LENGTH_SHORT).show()
//            }
            Log.i("test", "first log: ${stockInfoBody?.getOrNull(0)?.ceo}")
        }

//        thread {

//
//        }.start()

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StockInvestingAppTheme {
        Greeting("Android")
    }
}