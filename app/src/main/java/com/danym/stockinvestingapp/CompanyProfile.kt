package com.danym.stockinvestingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.danym.stockinvestingapp.components.CardComponent
import com.danym.stockinvestingapp.components.StockImage
import com.danym.stockinvestingapp.model.Stock
import com.danym.stockinvestingapp.ui.theme.StockInvestingAppTheme
import com.danym.stockinvestingapp.utility.getDateFormatted
import com.danym.stockinvestingapp.viewmodel.StockViewModel
import java.time.LocalDate

class CompanyProfile : ComponentActivity() {
    companion object {
        private const val STOCK_ID = "stock_id"
        fun newIntent(context: Context, stock: Stock) =
            Intent(context, CompanyProfile::class.java).apply {
                putExtra(STOCK_ID, stock)
            }
    }

    private val stock: Stock by lazy {
        intent?.getSerializableExtra(STOCK_ID) as Stock
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        setContent {
            StockInvestingAppTheme {
                Column {
                    val currentPrice = remember {
                        mutableStateOf("")
                    }
                    val prices = remember {
                        mutableStateOf(listOf<Double>())
                    }
                    viewModel.getStockPrices(stock.ticker, 7, { p ->
                        currentPrice.value = p.getOrNull(0)?.toString() ?: ""
                        prices.value = p
                    })
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .padding(vertical = 20.dp)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    CardComponent {
                        Row {
                            StockImage(ticker = stock.ticker)
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = "Price: ${currentPrice.value}$",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = stock.ticker,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                    CardComponent {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            val points =
                                prices.value.mapIndexed { i, p -> Point(i.toFloat(), p.toFloat()) }
                            if (points.isNotEmpty()) {
                                LineChartScreen(
                                    points,
                                    List(points.size) { i ->
                                        getDateFormatted(
                                            LocalDate.now().minusDays(
                                                (points.size - (i + 1)).toLong()
                                            )
                                        )
                                    }
                                )
                            } else {
                                Toast.makeText(
                                    LocalContext.current,
                                    "No data available for displaying the prices",
                                    LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun LineChartScreen(pointsData: List<Point>, dates: List<String>) {
    val steps = pointsData.size

    val xAxisData = AxisData.Builder().axisStepSize(100.dp).backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1).labelData { i -> dates[i] }.labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary).build()

    val yAxisData = AxisData.Builder().steps(steps).backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp).labelData { i ->
            val yScale = 100 / steps
            (i * yScale).toString()
        }.axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary).build()

    val lineChart = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
                    ShadowUnderLine(
                        alpha = 0.5f, brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary, Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), lineChartData = lineChart
    )
}