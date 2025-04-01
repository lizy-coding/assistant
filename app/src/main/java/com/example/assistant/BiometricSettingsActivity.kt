package com.example.assistant

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assistant.adapter.FingerprintAdapter
import com.example.assistant.databinding.ActivityBiometricSettingsBinding
import com.example.biometric_auth.api.BiometricAuth
import com.example.biometric_auth.api.FingerprintManager

/**
 * 生物识别设置界面
 * 提供指纹管理和密码修改功能
 */
class BiometricSettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBiometricSettingsBinding
    private lateinit var fingerprintAdapter: FingerprintAdapter
    private val fingerprints = mutableListOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadFingerprints()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "生物识别设置"
    }
    
    private fun setupRecyclerView() {
        fingerprintAdapter = FingerprintAdapter(fingerprints) { position ->
            showDeleteFingerprintDialog(position)
        }
        
        binding.recyclerViewFingerprints.adapter = fingerprintAdapter
        binding.recyclerViewFingerprints.layoutManager = LinearLayoutManager(this@BiometricSettingsActivity)
    }
    
    private fun setupButtons() {
        // 添加指纹按钮
        binding.buttonAddFingerprint.setOnClickListener {
            if (FingerprintManager.getFingerprintCount(this) >= 5) {
                Toast.makeText(this, "最多只能添加5个指纹", Toast.LENGTH_SHORT).show()
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
    }
    
    private fun loadFingerprints() {
        fingerprints.clear()
        fingerprints.addAll(FingerprintManager.getAllFingerprints(this).mapIndexed { index, hash ->
            "指纹 ${index + 1}"
        })
        fingerprintAdapter.notifyDataSetChanged()
        
        // 更新指纹数量显示
        val count = FingerprintManager.getFingerprintCount(this)
        binding.textFingerprintCount.text = "已存储 $count/5 个指纹"
    }
    
    private fun showAddFingerprintDialog() {
        // 先验证当前指纹
        showBiometricPrompt(
            "添加指纹",
            "请验证身份",
            "使用已有指纹验证身份后添加新指纹",
            onSuccess = {
                // 验证成功后，生成新的指纹哈希并添加
                val newFingerprint = FingerprintManager.generateTestFingerprint(this)
                if (FingerprintManager.addFingerprint(this, newFingerprint)) {
                    Toast.makeText(this, "指纹添加成功", Toast.LENGTH_SHORT).show()
                    loadFingerprints()
                } else {
                    Toast.makeText(this, "指纹添加失败", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    private fun showDeleteFingerprintDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("删除指纹")
            .setMessage("确定要删除这个指纹吗？")
            .setPositiveButton("删除") { _, _ ->
                if (FingerprintManager.deleteFingerprint(this, position)) {
                    Toast.makeText(this, "指纹已删除", Toast.LENGTH_SHORT).show()
                    loadFingerprints()
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showClearAllFingerprintsDialog() {
        AlertDialog.Builder(this)
            .setTitle("清除所有指纹")
            .setMessage("确定要清除所有已保存的指纹吗？此操作无法撤销。")
            .setPositiveButton("清除") { _, _ ->
                FingerprintManager.clearAllFingerprints(this)
                Toast.makeText(this, "所有指纹已清除", Toast.LENGTH_SHORT).show()
                loadFingerprints()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showChangePasswordDialog() {
        // TODO: 实现密码更改功能
        // 这里仅作为示例，实际应用中应实现密码修改的逻辑
        Toast.makeText(this, "密码修改功能待实现", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "验证失败，请重试", Toast.LENGTH_SHORT).show()
            },
            onError = { errorCode: Int, errorMessage: String ->
                Toast.makeText(this, "错误: $errorMessage", Toast.LENGTH_SHORT).show()
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