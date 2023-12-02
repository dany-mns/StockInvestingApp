package com.danym.stockinvestingapp.model

import java.io.Serializable

data class Stock(
    val name: String,
    val ticker: String,
) : Serializable