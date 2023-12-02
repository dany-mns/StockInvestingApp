package com.danym.stockinvestingapp.model

import com.fasterxml.jackson.annotation.JsonProperty

data class StockData(
    val symbol: String,
    val historical: List<Historical>
)

data class Historical(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    @JsonProperty("adjClose")
    val adjustedClose: Double,
    val volume: Long,
    @JsonProperty("unadjustedVolume")
    val unadjustedVolume: Long,
    val change: Double,
    @JsonProperty("changePercent")
    val changePercent: Double,
    val vwap: Double,
    val label: String,
    @JsonProperty("changeOverTime")
    val changeOverTime: Double
)