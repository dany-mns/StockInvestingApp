package com.danym.stockinvestingapp.utility

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getDateFormatted(currentDate: LocalDate = LocalDate.now()): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}
