package com.example.assistant.domain.battery

import android.content.Context

object BatteryRepositoryProvider {
    private val lock = Any()
    private var repository: BatteryRepositoryImpl? = null
    private var consumers = 0

    fun acquire(context: Context): BatteryRepository {
        synchronized(lock) {
            consumers += 1
            val current = repository
            if (current != null) return current
            return BatteryRepositoryImpl(context).also { repository = it }
        }
    }

    fun release() {
        synchronized(lock) {
            consumers = (consumers - 1).coerceAtLeast(0)
            if (consumers == 0) {
                repository?.release()
                repository = null
            }
        }
    }
}
