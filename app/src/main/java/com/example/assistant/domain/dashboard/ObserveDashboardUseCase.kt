package com.example.assistant.domain.dashboard

import com.example.assistant.domain.battery.BatteryRepository
import com.example.assistant.domain.push.PushRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveDashboardUseCase(
    private val batteryRepository: BatteryRepository,
    private val pushRepository: PushRepository
) {
    operator fun invoke(): Flow<DashboardState> {
        return combine(
            batteryRepository.batteryState,
            pushRepository.notificationCount
        ) { batteryInfo, pushCount ->
            DashboardState(
                batteryInfo = batteryInfo,
                pushMessageCount = pushCount
            )
        }
    }
}
