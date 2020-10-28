package com.boutiquesworld.util

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object TimeUtils {

    fun getFormattedDate(date: String): String {
        if (date.isEmpty())
            return "Unknown"
        return try {
            if (date.contains('-')) {
                // Grab the last possible day (ex: 5 - 7 days, grab 7)
                // Ex - Today: 28 Oct, 2020, returns 4 Nov, 2020
                val needDaysAfter = date.split('-')[1].split(' ')[1].toInt()
                SimpleDateFormat("d MMM, y").format(
                    Date.from(LocalDate.now().plusDays(needDaysAfter.toLong()).atStartOfDay().toInstant(
                    ZoneOffset.UTC)))
            } else // date is already formatted (ex - 25 Dec, 2020), simply return it.
                date
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown"
        }
    }
}