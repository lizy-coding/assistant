package com.example.wake_on_lan.api

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 提供发送局域网 Magic Packet 的简单接口。
 */
object WakeOnLanApi {

    private val macPattern =
        Regex("([0-9A-Fa-f]{2}([-:])){5}[0-9A-Fa-f]{2}|[0-9A-Fa-f]{12}")

    /**
     * 判断 MAC 地址格式是否有效。
     */
    fun isValidMac(macAddress: String): Boolean = macPattern.matches(macAddress.trim())

    /**
     * 发送 Magic Packet。
     *
     * @param macAddress 目标设备的 MAC 地址，支持 `AA:BB:CC:DD:EE:FF` 或 `AABBCCDDEEFF`。
     * @param broadcastAddress 广播地址，默认 `255.255.255.255`。
     * @param port 端口，常用 7 或 9。
     */
    suspend fun sendMagicPacket(
        macAddress: String,
        broadcastAddress: String = "255.255.255.255",
        port: Int = 9
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            require(port in 1..65535) { "端口必须在 1-65535 之间" }

            val magicPacket = buildMagicPacket(macAddress)
            val targetAddress = InetAddress.getByName(
                broadcastAddress.ifBlank { "255.255.255.255" }
            )

            DatagramSocket().use { socket ->
                socket.broadcast = true
                val packet = DatagramPacket(magicPacket, magicPacket.size, targetAddress, port)
                socket.send(packet)
            }
        }
    }

    private fun buildMagicPacket(macAddress: String): ByteArray {
        val cleanedMac = macAddress.replace("[-:]".toRegex(), "").lowercase()
        require(cleanedMac.length == 12) { "MAC 地址长度无效" }

        val macBytes = ByteArray(6) { index ->
            cleanedMac.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }

        return ByteArray(6 + 16 * macBytes.size).also { packet ->
            for (i in 0 until 6) {
                packet[i] = 0xFF.toByte()
            }
            var offset = 6
            repeat(16) {
                macBytes.copyInto(packet, destinationOffset = offset)
                offset += macBytes.size
            }
        }
    }
}
