package com.example.biometric_auth.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biometric_auth.R
import com.example.biometric_auth.api.BiometricAuth
import com.example.biometric_auth.api.FingerprintManager
import com.example.biometric_auth.databinding.ActivityFingerprintManagerBinding

/**
 * 指纹管理界面
 * 提供添加、删除指纹和密码修改功能
 */
class FingerprintManagerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFingerprintManagerBinding
    private lateinit var fingerprintAdapter: FingerprintAdapter
    private val fingerprints = mutableListOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerprintManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadFingerprints()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.biometric_settings_title)
        
        // 设置Toolbar文本颜色为白色
        binding.toolbar.setTitleTextColor(resources.getColor(R.color.white, theme))
    }
    
    private fun setupRecyclerView() {
        fingerprintAdapter = FingerprintAdapter(fingerprints) { position ->
            showDeleteFingerprintDialog(position)
        }
        
        binding.recyclerViewFingerprints.adapter = fingerprintAdapter
        binding.recyclerViewFingerprints.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupButtons() {
        // 添加指纹按钮
        binding.buttonAddFingerprint.setOnClickListener {
            if (FingerprintManager.getFingerprintCount(this) >= 5) {
                Toast.makeText(this, getString(R.string.fingerprint_max_limit), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            showAddFingerprintDialog()
        }
        
        // 清除所有指纹按钮
        binding.buttonClearFingerprints.setOnClickListener {
            showClearAllFingerprintsDialog()
        }
        
        // 修改密码按钮
        binding.buttonChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
        
        // 测试验证按钮 - 使用findViewById避免ViewBinding问题
        val testButton = findViewById<Button>(R.id.button_test_verify)
        testButton?.setOnClickListener {
            testVerifyFingerprint()
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun loadFingerprints() {
        fingerprints.clear()
        fingerprints.addAll(FingerprintManager.getAllFingerprints(this).mapIndexed { index, hash ->
            getString(R.string.fingerprint_item_name, index + 1)
        })
        fingerprintAdapter.notifyDataSetChanged()
        
        // 更新指纹数量显示
        val count = FingerprintManager.getFingerprintCount(this)
        binding.textFingerprintCount.text = getString(R.string.fingerprint_count, count)
    }
    
    private fun showAddFingerprintDialog() {
        // 先验证当前指纹
        showBiometricPrompt(
            getString(R.string.add_fingerprint),
            getString(R.string.verify_identity),
            getString(R.string.verify_to_add_fingerprint),
            onSuccess = {
                // 验证成功后，生成新的指纹哈希并添加
                val newFingerprint = FingerprintManager.generateTestFingerprint(this)
                android.util.Log.d("FingerprintActivity", "添加新指纹: $newFingerprint")
                
                if (FingerprintManager.addFingerprint(this, newFingerprint)) {
                    Toast.makeText(this, getString(R.string.fingerprint_add_success), Toast.LENGTH_SHORT).show()
                    loadFingerprints()
                    
                    // 列出所有指纹以便调试
                    val allFingerprints = FingerprintManager.getAllFingerprints(this)
                    android.util.Log.d("FingerprintActivity", "当前存储的所有指纹: $allFingerprints")
                } else {
                    Toast.makeText(this, getString(R.string.fingerprint_add_failure), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    private fun showDeleteFingerprintDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_fingerprint))
            .setMessage(getString(R.string.confirm_delete_fingerprint))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                if (FingerprintManager.deleteFingerprint(this, position)) {
                    Toast.makeText(this, getString(R.string.fingerprint_deleted), Toast.LENGTH_SHORT).show()
                    loadFingerprints()
                } else {
                    Toast.makeText(this, getString(R.string.delete_failure), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun showClearAllFingerprintsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.clear_all_fingerprints))
            .setMessage(getString(R.string.confirm_clear_all_fingerprints))
            .setPositiveButton(getString(R.string.clear)) { _, _ ->
                FingerprintManager.clearAllFingerprints(this)
                Toast.makeText(this, getString(R.string.all_fingerprints_cleared), Toast.LENGTH_SHORT).show()
                loadFingerprints()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun showChangePasswordDialog() {
        // TODO: 实现密码更改功能
        // 这里仅作为示例，实际应用中应实现密码修改的逻辑
        Toast.makeText(this, getString(R.string.password_change_not_implemented), Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 测试指纹验证
     */
    private fun testVerifyFingerprint() {
        val fingerprints = FingerprintManager.getAllFingerprints(this)
        android.util.Log.d("FingerprintActivity", "开始验证测试，当前存储的指纹: $fingerprints")
        
        showBiometricPrompt(
            "测试验证",
            "请验证您的指纹",
            "正在测试指纹验证功能",
            onSuccess = {
                Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show()
                android.util.Log.d("FingerprintActivity", "验证测试成功")
            }
        )
    }
    
    /**
     * 显示生物识别提示对话框
     */
    private fun showBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        BiometricAuth.authenticate(
            activity = this,
            title = title,
            subtitle = subtitle,
            description = description,
            onSuccess = onSuccess,
            onFailure = {
                Toast.makeText(this, getString(R.string.verification_failed), Toast.LENGTH_SHORT).show()
            },
            onError = { errorCode: Int, errorMessage: String ->
                Toast.makeText(this, getString(R.string.error_with_message, errorMessage), Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
} 