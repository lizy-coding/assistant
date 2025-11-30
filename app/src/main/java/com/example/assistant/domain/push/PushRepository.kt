package com.example.assistant.domain.push

import kotlinx.coroutines.flow.StateFlow

interface PushRepository {
    val notificationCount: StateFlow<Int>

    fun refreshCount(): Int
    fun release()
}
