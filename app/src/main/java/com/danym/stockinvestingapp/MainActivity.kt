package com.danym.stockinvestingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.danym.stockinvestingapp.model.Stock
import com.danym.stockinvestingapp.model.StockInfo
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val stockList =
    mapOf(
        "AAPL" to "Apple Inc.",
        "MSFT" to "Microsoft Corporation",
        "GOOG" to "Alphabet Inc.",
        "AMZN" to "Amazon.com, Inc.",
        "NVDA" to "NVIDIA Corporation",
        "META" to "Meta Platforms, Inc.",
        "TSLA" to "Tesla, Inc.",
        "JPM" to "JPMorgan Chase & Co.",
        "AVGO" to "Broadcom Inc.",
        "WMT" to "Walmart Inc.",
        "PG" to "The Procter & Gamble Company",
        "JNJ" to "Johnson & Johnson",
        "ORCL" to "Oracle Corporation"
    ).entries.map { Stock(it.value, it.key) }

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
                ListingScreen {
                    startActivity(Intent(this, CompanyProfile::class.java))
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


            Log.i("test", "first log: ${stockInfoBody?.getOrNull(0)?.ceo}")
        }

    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScreen(navigateToCompanyProfile: (Stock) -> Unit) {
    Scaffold(content = {
        StockHomeContent(navigateToCompanyProfile)
    })
}

@Composable
fun StockHomeContent(navigateToCompanyProfile: (Stock) -> Unit) {
    val stocks = remember {
        stockList
    }
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(
            items = stocks,
            itemContent = {
                StockListItem(it, navigateToCompanyProfile)
            })
    }
}

@Composable
fun StockListItem(stock: Stock, navigateToCompanyProfile: (Stock) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            Modifier.clickable { navigateToCompanyProfile(stock) }
        ) {
            StockImage(ticker = stock.ticker)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = stock.name, style = MaterialTheme.typography.titleLarge)
                Text(text = stock.ticker, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun StockImage(ticker: String) {
    val context = LocalContext.current
    Image(
        painter = painterResource(
            id = context.resources.getIdentifier(
                ticker.lowercase(),
                "drawable",
                context.packageName
            )
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )
}