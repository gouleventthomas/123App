package com.pb123.hub.guides.chrono

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pb123.hub.ui.theme.OkGreen
import com.pb123.hub.ui.theme.Poppins
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class Preset(val label: String, val minutes: Int)

private val presets = listOf(
    Preset("30 min", 30),
    Preset("1 h", 60),
    Preset("1 h 30", 90),
    Preset("2 h", 120),
    Preset("3 h", 180),
    Preset("4 h", 240),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChronoScreen(
    onBack: () -> Unit,
    viewModel: ChronoViewModel = viewModel(),
) {
    val state = viewModel.state
    val context = LocalContext.current

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(state.endTimeMillis) {
        while (state.endTimeMillis > 0L) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* le chrono démarre de toute façon ; la notif marchera si accordé */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chrono séchage colle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isActive) {
                RunningView(
                    state = state,
                    now = now,
                    onStop = { viewModel.stop() },
                )
            } else {
                SetupView(
                    onStart = { minutes, label ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        viewModel.start(minutes * 60_000L, label)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetupView(onStart: (minutes: Int, label: String) -> Unit) {
    var label by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Int?>(null) }

    OutlinedTextField(
        value = label,
        onValueChange = { label = it },
        label = { Text("Client / immatriculation (optionnel)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Text(
        "Temps de roulage sécurisé",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        presets.forEach { p ->
            FilterChip(
                selected = selected == p.minutes,
                onClick = { selected = p.minutes },
                label = { Text(p.label) },
            )
        }
    }

    Text(
        "⚠️ Durée indicative : suis la préconisation du fabricant de colle selon la température et le nombre d'airbags.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(Modifier.height(4.dp))
    Button(
        onClick = { selected?.let { onStart(it, label) } },
        enabled = selected != null,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Icon(Icons.Filled.PlayArrow, contentDescription = null)
        Spacer(Modifier.height(0.dp))
        Text("  Démarrer le chrono", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun RunningView(state: ChronoState, now: Long, onStop: () -> Unit) {
    val finished = state.isFinished(now)
    val remaining = state.remainingMillis(now)
    val accent = if (finished) OkGreen else MaterialTheme.colorScheme.primary

    if (state.label.isNotBlank()) {
        Text(
            state.label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (finished) {
                Text("✅", fontSize = 44.sp)
                Text(
                    "Véhicule roulable",
                    style = MaterialTheme.typography.headlineSmall,
                    color = accent,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    formatRemaining(remaining),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 56.sp,
                    color = accent,
                )
                Text(
                    "Roulable à ${formatClock(state.endTimeMillis)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val fraction = if (state.totalDurationMillis > 0) {
                    (1f - remaining.toFloat() / state.totalDurationMillis).coerceIn(0f, 1f)
                } else 0f
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 6.dp),
                    color = accent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }

    if (finished) {
        Button(
            onClick = onStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OkGreen),
        ) { Text("Terminer", style = MaterialTheme.typography.titleMedium) }
    } else {
        OutlinedButton(
            onClick = onStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("Annuler le chrono", style = MaterialTheme.typography.titleMedium) }
    }
}

private fun formatRemaining(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) String.format(Locale.FRANCE, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.FRANCE, "%02d:%02d", m, s)
}

private fun formatClock(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(millis))
