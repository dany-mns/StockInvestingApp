package com.danym.stockinvestingapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.danym.stockinvestingapp.model.Stock
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme

class CompanyProfile : ComponentActivity() {
    companion object {
        private const val STOCK_ID = "stock_id"
        fun newIntent(context: Context, stock: Stock) =
            Intent(context, CompanyProfile::class.java).apply {
                putExtra(STOCK_ID, stock)
            }
    }

    private val stock: Stock by lazy {
        // TODO check later replacement for the deprecated method
        intent?.getSerializableExtra(STOCK_ID) as Stock
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockInvestingAppTheme {
                Text("Hello ${stock.name}")
            }
        }
    }
}