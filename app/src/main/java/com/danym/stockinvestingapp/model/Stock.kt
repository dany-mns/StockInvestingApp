package com.danym.stockinvestingapp.model

import java.io.Serializable

data class Stock(
    val name: String,
    val ticker: String,
) : Serializable

val stockList = mapOf(
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
