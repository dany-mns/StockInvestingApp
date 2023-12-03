package com.danym.stockinvestingapp.model

import com.fasterxml.jackson.annotation.JsonProperty

data class StockData(
    @JsonProperty("symbol")
    val symbol: String,
    @JsonProperty("historical")
    val historical: List<Historical>
)

data class Historical(
    @JsonProperty("date")
    val date: String,
    @JsonProperty("open")
    val open: Double,
    @JsonProperty("high")
    val high: Double,
    @JsonProperty("low")
    val low: Double,
    @JsonProperty("close")
    val close: Double,
    @JsonProperty("adjClose")
    val adjustedClose: Double,
    @JsonProperty("volume")
    val volume: Long,
    @JsonProperty("unadjustedVolume")
    val unadjustedVolume: Long,
    @JsonProperty("change")
    val change: Double,
    @JsonProperty("changePercent")
    val changePercent: Double,
    @JsonProperty("vwap")
    val vwap: Double,
    @JsonProperty("label")
    val label: String,
    @JsonProperty("changeOverTime")
    val changeOverTime: Double
)
