package com.danym.stockinvestingapp.utility

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun getDateFormatted(currentDate: LocalDate = LocalDate.now()): String {
    return currentDate.format(formatter)
}
