package com.example.biometric_auth.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.UUID

/**
 * 指纹数据存储类
 * 用于管理生物识别相关的持久化数据
 */
class FingerprintDataStore private constructor(context: Context) {
    companion object {
        private const val PREFERENCE_NAME = "biometric_fingerprint_data"
        private const val KEY_FINGERPRINT_COUNT = "fingerprint_count"
        private const val KEY_FINGERPRINT_PREFIX = "fingerprint_"
        private const val MAX_FINGERPRINTS = 5
        
        @Volatile
        private var INSTANCE: FingerprintDataStore? = null
        
        fun getInstance(context: Context): FingerprintDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FingerprintDataStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    
    /**
     * 获取已存储的指纹数量
     */
    fun getFingerprintCount(): Int {
        return preferences.getInt(KEY_FINGERPRINT_COUNT, 0)
    }
    
    /**
     * 获取所有已存储的指纹数据
     */
    fun getAllFingerprints(): List<String> {
        val count = getFingerprintCount()
        val fingerprints = mutableListOf<String>()
        
        for (i in 0 until count) {
            val fingerprint = preferences.getString("$KEY_FINGERPRINT_PREFIX$i", null)
            fingerprint?.let { fingerprints.add(it) }
        }
        
        return fingerprints
    }
    
    /**
     * 添加新的指纹数据
     * @return 添加是否成功
     */
    fun addFingerprint(fingerprintHash: String): Boolean {
        val currentCount = getFingerprintCount()
        
        if (currentCount >= MAX_FINGERPRINTS) {
            return false
        }
        
        if (isExistingFingerprint(fingerprintHash)) {
            return false
        }
        
        preferences.edit {
            putString("$KEY_FINGERPRINT_PREFIX$currentCount", fingerprintHash)
            putInt(KEY_FINGERPRINT_COUNT, currentCount + 1)
        }
        
        return true
    }
    
    /**
     * 删除指定索引的指纹数据
     */
    fun deleteFingerprint(index: Int): Boolean {
        val currentCount = getFingerprintCount()
        
        if (index < 0 || index >= currentCount) {
            return false
        }
        
        // 移动所有后面的指纹数据
        for (i in index until currentCount - 1) {
            val nextFingerprint = preferences.getString("$KEY_FINGERPRINT_PREFIX${i+1}", "")
            preferences.edit {
                putString("$KEY_FINGERPRINT_PREFIX$i", nextFingerprint)
            }
        }
        
        // 清除最后一个指纹数据
        preferences.edit {
            remove("$KEY_FINGERPRINT_PREFIX${currentCount-1}")
            putInt(KEY_FINGERPRINT_COUNT, currentCount - 1)
        }
        
        return true
    }
    
    /**
     * 清除所有指纹数据
     */
    fun clearAllFingerprints() {
        val count = getFingerprintCount()
        
        preferences.edit {
            for (i in 0 until count) {
                remove("$KEY_FINGERPRINT_PREFIX$i")
            }
            putInt(KEY_FINGERPRINT_COUNT, 0)
        }
    }
    
    /**
     * 验证指纹是否匹配
     */
    fun verifyFingerprint(fingerprintHash: String): Boolean {
        val fingerprints = getAllFingerprints()
        val result = fingerprints.contains(fingerprintHash)
        android.util.Log.d("FingerprintDataStore", "验证指纹: $fingerprintHash, 结果: $result, 存储的指纹: $fingerprints")
        return result
    }
    
    /**
     * 检查指纹是否已存在
     */
    private fun isExistingFingerprint(fingerprintHash: String): Boolean {
        val result = getAllFingerprints().contains(fingerprintHash)
        android.util.Log.d("FingerprintDataStore", "检查指纹是否存在: $fingerprintHash, 结果: $result")
        return result
    }
    
    /**
     * 生成随机的指纹哈希值，用于测试
     */
    fun generateFakeFingerprintHash(): String {
        return UUID.randomUUID().toString()
    }
} 