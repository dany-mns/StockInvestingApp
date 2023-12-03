package com.danym.stockinvestingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danym.stockinvestingapp.CompanyProfile.Companion.newIntent
import com.danym.stockinvestingapp.components.CardComponent
import com.danym.stockinvestingapp.components.StockImage
import com.danym.stockinvestingapp.model.Stock
import com.danym.stockinvestingapp.model.StockData
import com.danym.stockinvestingapp.model.stockList
import com.danym.stockinvestingapp.network.getStockData
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme
import com.danym.stockinvestingapp.utility.getDateFormatted
import java.time.LocalDate
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockInvestingAppTheme {
                ListingScreen {
                    startActivity(
                        newIntent(this, it)
                    )
                }
            }
        }

        val executor: Executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val stockInfoBody: StockData? = getStockData("AAPL", "2023-11-20", "2023-11-25")

            Log.i(
                "test",
                "first log: ${stockInfoBody?.historical?.size} -> ${
                    getDateFormatted(
                        LocalDate.now().minusDays(7)
                    )
                }"
            )
        }

    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
        items(items = stocks, itemContent = {
            StockListItem(it, navigateToCompanyProfile)
        })
    }
}

@Composable
fun StockListItem(stock: Stock, navigateToCompanyProfile: (Stock) -> Unit) {
    CardComponent {
        Row(Modifier.clickable { navigateToCompanyProfile(stock) }) {
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

