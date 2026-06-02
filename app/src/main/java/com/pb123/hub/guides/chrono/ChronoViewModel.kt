package com.pb123.hub.guides.chrono

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

/** État du minuteur de séchage. [endTimeMillis] == 0 signifie "aucun chrono en cours". */
data class ChronoState(
    val endTimeMillis: Long = 0L,
    val totalDurationMillis: Long = 0L,
    val label: String = "",
) {
    val isActive: Boolean get() = endTimeMillis > 0L
    fun remainingMillis(now: Long): Long = (endTimeMillis - now).coerceAtLeast(0L)
    fun isFinished(now: Long): Boolean = isActive && now >= endTimeMillis
}

class ChronoViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("chrono", android.content.Context.MODE_PRIVATE)

    var state by mutableStateOf(load())
        private set

    fun start(durationMillis: Long, label: String) {
        val end = System.currentTimeMillis() + durationMillis
        state = ChronoState(end, durationMillis, label.trim())
        save()
        ChronoScheduler.schedule(getApplication(), end, label.trim())
    }

    fun stop() {
        ChronoScheduler.cancel(getApplication())
        state = ChronoState()
        save()
    }

    private fun load(): ChronoState = ChronoState(
        endTimeMillis = prefs.getLong(KEY_END, 0L),
        totalDurationMillis = prefs.getLong(KEY_DURATION, 0L),
        label = prefs.getString(KEY_LABEL, "").orEmpty(),
    )

    private fun save() {
        prefs.edit()
            .putLong(KEY_END, state.endTimeMillis)
            .putLong(KEY_DURATION, state.totalDurationMillis)
            .putString(KEY_LABEL, state.label)
            .apply()
    }

    private companion object {
        const val KEY_END = "end"
        const val KEY_DURATION = "dur"
        const val KEY_LABEL = "label"
    }
}
