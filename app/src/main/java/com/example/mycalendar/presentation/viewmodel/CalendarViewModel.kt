// Kotlin
package com.example.mycalendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.remote.Day
import com.example.mycalendar.data.remote.RetrofitProvider
import com.example.mycalendar.presentation.uistate.CalendarDay
import com.example.mycalendar.presentation.uistate.CalendarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

class CalendarViewModel : ViewModel() {

    private val api = RetrofitProvider.create()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    private val today: LocalDate = LocalDate.now()
    private var bsYear: Int
    private var bsMonth: Int

    init {
        val (y, m) = guessBsYearMonth(today)
        bsYear = y
        bsMonth = m
        loadMonth()
    }

    private fun loadMonth() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val resp = api.getMonth(year = bsYear, month = bsMonth)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    val (adMonths, adYears) = parseEnMetadataToAdMonthsYears(body?.metadata?.get("en"))
                    val splitIdx = splitIndexForSecondMonth(body?.days.orEmpty())

                    val daysUi = body?.days?.mapIndexed { index, d ->
                        val adDay = d.e.toIntOrNull() ?: 0
                        val (adMonthNum, adYearNum) = assignAdMonthYear(index, adMonths, adYears, splitIdx)

                        val isTodayFlag = d.n.isNotBlank() &&
                                adDay > 0 &&
                                adMonthNum == today.monthValue &&
                                adYearNum == today.year &&
                                adDay == today.dayOfMonth

                        val adDateDisplay =
                            if (adDay > 0 && adMonthNum > 0) "$adDay/$adMonthNum" else ""

                        val festText = listOf(d.t, d.f)
                            .filter { it.isNotBlank() }
                            .joinToString("\n")
                            .ifBlank { null }

                        CalendarDay(
                            bsDate = d.n,
                            adDate = adDateDisplay,
                            festivalName = festText,
                            isToday = isTodayFlag,
                            isHoliday = d.h,
                            isCurrentMonth = d.n.isNotBlank()
                        )
                    } ?: emptyList()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bsMonthName = body?.metadata?.get("np") ?: "",
                            adMonthSpan = formatAdMonthSpan(adMonths, adYears),
                            days = daysUi
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNextMonth() {
        if (bsMonth == 12) {
            bsMonth = 1
            bsYear += 1
        } else {
            bsMonth += 1
        }
        loadMonth()
    }

    fun onPreviousMonth() {
        if (bsMonth == 1) {
            bsMonth = 12
            bsYear -= 1
        } else {
            bsMonth -= 1
        }
        loadMonth()
    }

    // --- Helpers ---

    private fun guessBsYearMonth(ad: LocalDate): Pair<Int, Int> {
        val switchMonth = Month.APRIL.value
        val switchDay = 13

        val approxBsYear = if (ad.monthValue > switchMonth || (ad.monthValue == switchMonth && ad.dayOfMonth >= switchDay)) {
            ad.year + 57
        } else {
            ad.year + 56
        }

        val baseMap = mapOf(
            4 to 1, 5 to 2, 6 to 3, 7 to 4, 8 to 5, 9 to 6,
            10 to 7, 11 to 8, 12 to 9, 1 to 10, 2 to 11, 3 to 12
        )
        var approxBsMonth = baseMap[ad.monthValue] ?: 1
        if (ad.dayOfMonth < switchDay) {
            approxBsMonth = if (approxBsMonth == 1) 12 else approxBsMonth - 1
        }
        return approxBsYear to approxBsMonth
    }

    private fun parseEnMetadataToAdMonthsYears(en: String?): Pair<List<Int>, List<Int>> {
        if (en.isNullOrBlank()) return emptyList<Int>() to emptyList()
        val parts = en.trim().split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) return emptyList<Int>() to emptyList()

        val monthPart = parts[0] // e.g., "Oct/Nov" or "Nov"
        val monthNames = monthPart.split("/").filter { it.isNotBlank() }
        val months = monthNames.mapNotNull { monthNameToNumber(it) }

        val yearsPart = parts.drop(1).joinToString(" ") // e.g., "2025" or "2025/2026"
        val yearTokens = yearsPart.split("/").filter { it.isNotBlank() }
        val years = when (yearTokens.size) {
            0 -> List(months.size) { today.year }
            1 -> List(months.size) { yearTokens[0].toIntOrNull() ?: today.year }
            else -> yearTokens.take(months.size).map { it.toIntOrNull() ?: today.year }
        }

        return months to years
    }

    private fun monthNameToNumber(shortName: String): Int? = when (shortName.lowercase()) {
        "jan" -> 1; "feb" -> 2; "mar" -> 3; "apr" -> 4; "may" -> 5; "jun" -> 6;
        "jul" -> 7; "aug" -> 8; "sep", "sept" -> 9; "oct" -> 10; "nov" -> 11; "dec" -> 12
        else -> null
    }

    private fun monthNumToShort(n: Int) = when (n) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
        7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> ""
    }

    private fun formatAdMonthSpan(adMonths: List<Int>, adYears: List<Int>): String {
        if (adMonths.isEmpty()) return ""
        val m1 = adMonths.first()
        val y1 = adYears.getOrNull(0) ?: today.year
        if (adMonths.size == 1 || adMonths.distinct().size == 1) {
            return "${monthNumToShort(m1)} $y1"
        }
        val m2 = adMonths.getOrNull(1) ?: m1
        val y2 = adYears.getOrNull(1) ?: y1
        return if (y1 == y2) "${monthNumToShort(m1)}–${monthNumToShort(m2)} $y1"
        else "${monthNumToShort(m1)} $y1–${monthNumToShort(m2)} $y2"
    }

    private fun splitIndexForSecondMonth(days: List<Day>): Int {
        val idx = days.indexOfFirst { it.e == "01" }
        return if (idx >= 0) idx else Int.MAX_VALUE
    }

    private fun assignAdMonthYear(
        index: Int,
        adMonths: List<Int>,
        adYears: List<Int>,
        splitIdx: Int
    ): Pair<Int, Int> {
        if (adMonths.isEmpty()) return today.monthValue to today.year
        val half = if (index < splitIdx) 0 else 1
        val m = adMonths.getOrNull(half) ?: adMonths.last()
        val y = adYears.getOrNull(half) ?: adYears.lastOrNull() ?: today.year
        return m to y
    }
}
