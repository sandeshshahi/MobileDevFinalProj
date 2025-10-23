// Kotlin
package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.presentation.uistate.CalendarDay
import com.example.mycalendar.presentation.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel() ,
    modifier: Modifier = Modifier,
    onOpenFestival: (festivalName: String, bsMonth: String, bsDate: String, enDate: String) -> Unit = { _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Bottom list state and selection
    val bottomListState: LazyListState = rememberLazyListState()
    var selectedBottomIndex by remember { mutableIntStateOf(0) }
    val currentMonthIndices = remember(uiState.days) {
        uiState.days.mapIndexedNotNull { idx, d -> if (d.isCurrentMonth) idx else null }
    }
    val currentMonthDays = remember(uiState.days) { currentMonthIndices.map { uiState.days[it] } }
    val selectedGridIndex = currentMonthIndices.getOrNull(selectedBottomIndex) ?: -1

    // Reset selection to today (or first day) on month change
    LaunchedEffect(uiState.days) {
        val todayIdxInCurrent = currentMonthDays.indexOfFirst { it.isToday }
        selectedBottomIndex = if (todayIdxInCurrent >= 0) todayIdxInCurrent else 0
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 16.dp),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.onPreviousMonth() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.bsMonthName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (uiState.adMonthSpan.isNotBlank()) {
                        Text(
                            text = uiState.adMonthSpan,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { viewModel.onNextMonth() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                        item(span = { GridItemSpan(7) }) { WeekdayHeaderRow() }
                        itemsIndexed(uiState.days) { index, day ->
                            val isSelected = index == selectedGridIndex
                            DayCell(day = day, selected = isSelected) {
                                if (!day.isCurrentMonth) return@DayCell
                                val bottomIndex = currentMonthIndices.indexOf(index)
                                if (bottomIndex >= 0) {
                                    selectedBottomIndex = bottomIndex
                                    scope.launch {
                                        bottomListState.animateScrollToItem(bottomIndex)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom list: "MonAbbrev date: festivalName"
            if (!uiState.isLoading) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
//                        .height(160.dp),
                    state = bottomListState
                ) {
                    itemsIndexed(currentMonthDays) { idx, day ->
                        val sel = idx == selectedBottomIndex
                        val bg = if (sel) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                        val fg = if (sel) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedBottomIndex = idx
                                    scope.launch { bottomListState.animateScrollToItem(idx) }
                                    val fest = day.festivalName?.trim().orEmpty()
                                    if (fest.isNotEmpty() && fest != "No events") {
                                        val bsMonth = bsMonthOnly(uiState.bsMonthName)
                                        val enPart = enMonthDay(day)
                                        onOpenFestival(fest, bsMonth, day.bsDate, enPart)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = formatBottomLine(day, uiState.bsMonthName),
                                    color = fg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }

                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp), // Optional padding
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant // A subtle color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeaderRow() {
    val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
        weekdays.forEachIndexed { index, label ->
            val color = if (index == 0) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            Box(modifier = Modifier.weight(1f).padding(2.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DayCell(day: CalendarDay, selected: Boolean, onClick: () -> Unit) {
    var backgroundColor = MaterialTheme.colorScheme.surface
    var contentColor = MaterialTheme.colorScheme.onSurface
    var borderColor = Color.LightGray.copy(alpha = 0.5f)
    var bsDateColor = MaterialTheme.colorScheme.onSurface

    if (day.isHoliday) {
        borderColor = Color.Red
        bsDateColor = Color.Red
    }
    // Selected highlight (keeps today's primary)
    if (selected && !day.isToday) {
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        borderColor = MaterialTheme.colorScheme.secondary
    }
    // Today overrides
    if (day.isToday) {
        backgroundColor = MaterialTheme.colorScheme.primary
        contentColor = MaterialTheme.colorScheme.onPrimary
        borderColor = Color.Transparent
        bsDateColor = MaterialTheme.colorScheme.onPrimary
    }

    Surface(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clickable(enabled = day.isCurrentMonth) { onClick() },
//        shape = RoundedCornerShape(1.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        if (day.isCurrentMonth) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = day.bsDate,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = bsDateColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = day.adDate,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

// --- helpers (UI-only) ---
private fun monthNumToShort(n: Int) = when (n) {
    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
    7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
    else -> ""
}

private fun formatBottomLine(day: CalendarDay, bsMonthName: String): String {
    val bsMonthOnly = bsMonthName
        .replace(Regex("[0-9०-९]+"), "")
        .trim()
        .replace(Regex("\\s+"), " ")

    val parts = day.adDate.split("/")
    val enDay = parts.getOrNull(0).orEmpty()
    val enMonthNum = parts.getOrNull(1)?.toIntOrNull()
    val enMonth = enMonthNum?.let { monthNumToShort(it) }.orEmpty()
    val enPart = if (enMonth.isNotBlank() && enDay.isNotBlank()) " ($enMonth $enDay)" else ""
    val fest = day.festivalName?.takeIf { it.isNotBlank() } ?: "No events"
    return "$bsMonthOnly ${day.bsDate}$enPart: $fest"
}

private fun bsMonthOnly(bsMonthName: String): String =
    bsMonthName.replace(Regex("[0-9०-९]+"), "").trim().replace(Regex("\\s+"), " ")

private fun enMonthDay(day: CalendarDay): String {
    val parts = day.adDate.split("/")
    val enDay = parts.getOrNull(0).orEmpty()
    val enMonthNum = parts.getOrNull(1)?.toIntOrNull()
    val enMonth = enMonthNum?.let { monthNumToShort(it) }.orEmpty()
    return if (enMonth.isNotBlank() && enDay.isNotBlank()) "$enMonth $enDay" else ""
}
