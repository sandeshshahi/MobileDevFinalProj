@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.navigation.FestivalDetail
import com.example.mycalendar.presentation.viewmodel.FestivalDetailViewModel

@Composable
fun FestivalDetailScreen(
    args: FestivalDetail,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val vmKey = buildString {
        append("FestivalDetail:")
        append(args.festivalName).append('|')
        append(args.bsMonth).append('|')
        append(args.bsDate).append('|')
        append(args.enDate)
    }

    val vm: FestivalDetailViewModel = viewModel(
        key = vmKey,
        factory = FestivalDetailViewModel.factory(
            festivalName = args.festivalName,
            bsMonth = args.bsMonth,
            bsDate = args.bsDate,
            enDate = args.enDate
        )
    )
    val ui by vm.uiState.collectAsState()

    val title = buildString {
        append(if (ui.festivalName.isBlank()) "Festival" else ui.festivalName)
        append(" • ")
        append(ui.bsMonth)
        append(" ")
        append(ui.bsDate)
        if (ui.enDate.isNotBlank()) {
            append(" (")
            append(ui.enDate)
            append(")")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festival Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            // Show image only if not loading and available
            if (!ui.isLoading) {
                ui.image.firstOrNull()?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "AI generated image for ${ui.festivalName}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center) // Center loader
                    )
                } else {
                    // This Column is for the text content and is scrollable
                    Column(
                        modifier = Modifier
                            .fillMaxSize() // Fill the parent Box
                            .verticalScroll(rememberScrollState()) // Make it scrollable
                    ) {
                        ui.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(text = ui.description, style = MaterialTheme.typography.bodyLarge)

                    }
                }
            }
//            if (ui.isLoading) {
//                CircularProgressIndicator()
//            } else {
//                ui.image.firstOrNull()?.let { bmp ->
//                    Image(
//                        bitmap = bmp.asImageBitmap(),
//                        contentDescription = "AI generated image for ${ui.festivalName}",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(220.dp),
//                        contentScale = ContentScale.Crop
//                    )
//                    Spacer(Modifier.height(12.dp))
//                }
//                ui.error?.let {
//                    Text(text = it, color = MaterialTheme.colorScheme.error)
//                    Spacer(Modifier.height(8.dp))
//                }
//                Text(text = ui.description, style = MaterialTheme.typography.bodyLarge)
//            }
        }
    }
}
