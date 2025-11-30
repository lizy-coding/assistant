package com.example.wake_on_lan.permission

import android.Manifest
import android.os.Build

/**
 * 提供与局域网唤醒相关的权限信息。
 */
object WakeOnLanPermissions {

    /**
     * Android 13+ 需要的联网/局域网相关动态权限。
     */
    val nearbyWifiPermission: String?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.NEARBY_WIFI_DEVICES
        } else {
            null
        }

    /**
     * 需要在运行时请求的权限列表。
     */
    val runtimePermissions: List<String>
        get() = listOfNotNull(nearbyWifiPermission)
}
