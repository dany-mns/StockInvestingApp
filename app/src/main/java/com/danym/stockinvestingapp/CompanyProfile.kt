package com.danym.stockinvestingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme

class CompanyProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockInvestingAppTheme {
                Text("Hello world")
            }
        }
    }
}