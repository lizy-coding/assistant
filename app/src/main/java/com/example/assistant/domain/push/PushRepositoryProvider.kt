package com.example.assistant.domain.push

import android.content.Context

object PushRepositoryProvider {
    private val lock = Any()
    private var repository: PushRepositoryImpl? = null
    private var consumers = 0

    fun acquire(context: Context): PushRepository {
        synchronized(lock) {
            consumers += 1
            val current = repository
            if (current != null) return current
            return PushRepositoryImpl(context).also { repository = it }
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
