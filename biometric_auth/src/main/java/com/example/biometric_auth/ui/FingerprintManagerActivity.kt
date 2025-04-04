package com.example.biometric_auth.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biometric_auth.R
import com.example.biometric_auth.api.BiometricAuth
import com.example.biometric_auth.api.FingerprintManager
import com.example.biometric_auth.databinding.ActivityFingerprintManagerBinding
import java.util.UUID

/**
 * 指纹管理界面
 * 提供添加、删除指纹和密码修改功能
 */
class FingerprintManagerActivity : AppCompatActivity() {
    // 添加请求码常量
    companion object {
        private const val FINGERPRINT_ENROLL_REQUEST = 1001
    }
    
    private lateinit var binding: ActivityFingerprintManagerBinding
    private lateinit var fingerprintAdapter: FingerprintAdapter
    private val fingerprints = mutableListOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerprintManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 检查是否有存储的指纹
        if (FingerprintManager.getFingerprintCount(this) > 0) {
            verifyIdentityBeforeEnter()
        } else {
            // 没有指纹则直接进入
            setupUI()
        }
    }
    
    private fun verifyIdentityBeforeEnter() {
        showBiometricPrompt(
            getString(R.string.verify_identity),
            getString(R.string.verify_to_enter_fingerprint_manager),
            getString(R.string.verify_to_manage_fingerprints),
            onSuccess = {
                setupUI()
            },
            onError = { code, msg ->
                Toast.makeText(this, "验证失败: $msg", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }
    
    private fun setupUI() {
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
        binding.buttonAddFingerprint.setOnClickListener {
            if (FingerprintManager.getFingerprintCount(this) >= 5) {
                showMaxFingerprintDialog()
                return@setOnClickListener
            }
            startFingerprintEnrollment()
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
            // 长按设置测试指纹，短按验证指纹
            testVerifyFingerprint()
        }
        
        // 长按测试按钮设置测试指纹
        testButton?.setOnLongClickListener {
            setupTestFingerprint()
            true
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
    
    // 修改添加指纹方法，移除验证逻辑
    private fun showAddFingerprintDialog() {
        if (FingerprintManager.getFingerprintCount(this) >= 5) {
            Toast.makeText(this, getString(R.string.fingerprint_max_limit), Toast.LENGTH_SHORT).show()
            return
        }
        
        // 生成随机的新指纹哈希值
        val newFingerprint = UUID.randomUUID().toString()
        if (FingerprintManager.addFingerprint(this, newFingerprint)) {
            Toast.makeText(this, getString(R.string.fingerprint_add_success), Toast.LENGTH_SHORT).show()
            loadFingerprints()
        } else {
            Toast.makeText(this, getString(R.string.fingerprint_add_failure), Toast.LENGTH_SHORT).show()
        }
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
        Toast.makeText(this, getString(R.string.password_change_not_implemented), Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 测试指纹验证
     */
    private fun testVerifyFingerprint() {
        val fingerprints = FingerprintManager.getAllFingerprints(this)
        android.util.Log.d("FingerprintActivity", "开始验证测试，当前存储的指纹: $fingerprints")
        
        // 使用固定值直接验证，绕过系统指纹提示
        val testHash = "biometric_fixed_hash_for_validation"
        val result = FingerprintManager.verifyFingerprint(this, testHash)
        
        if (result) {
            Toast.makeText(this, "指纹验证成功!", Toast.LENGTH_SHORT).show()
            android.util.Log.d("FingerprintActivity", "直接验证成功: $testHash")
        } else {
            Toast.makeText(this, "指纹验证失败!", Toast.LENGTH_SHORT).show()
            android.util.Log.d("FingerprintActivity", "直接验证失败: $testHash")
            
            // 尝试系统指纹验证
            showBiometricPrompt(
                "测试验证",
                "请验证您的指纹",
                "正在测试指纹验证功能",
                onSuccess = {
                    Toast.makeText(this, "系统验证成功", Toast.LENGTH_SHORT).show()
                    android.util.Log.d("FingerprintActivity", "系统验证测试成功")
                }
            )
        }
    }
    
    /**
     * 设置测试指纹
     */
    private fun setupTestFingerprint() {
        // 设置固定测试指纹
        FingerprintManager.setupTestFingerprint(this)
        Toast.makeText(this, "已设置固定测试指纹", Toast.LENGTH_SHORT).show()
        loadFingerprints()
    }
    
    /**
     * 显示生物识别提示对话框
     */
    private fun showBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (Int, String) -> Unit = { _, _ -> }
    ) {
        BiometricAuth.authenticate(
            this,
            title,
            subtitle,
            description,
            onSuccess = onSuccess,
            onError = { errorCode, errorMessage ->
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> 
                        onError(errorCode, "用户取消")
                    BiometricPrompt.ERROR_LOCKOUT -> 
                        onError(errorCode, "验证失败次数过多，请稍后再试")
                    else -> 
                        onError(errorCode, errorMessage)
                }
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


    // 删除旧的导入
    // import androidx.core.app.ActivityCompat.startActivityForResult
    
    // 修改startFingerprintEnrollment方法
    private fun startFingerprintEnrollment() {
        val enrollIntent = Intent(android.provider.Settings.ACTION_FINGERPRINT_ENROLL).apply {
            putExtra(android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG)
        }
        
        try {
            // 使用新的Activity Result API
            fingerprintEnrollmentLauncher.launch(enrollIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "设备不支持指纹录入", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "无权限访问指纹设置", Toast.LENGTH_SHORT).show()
        }
    }
    


    // 在类中添加ActivityResultLauncher
    private val fingerprintEnrollmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newFingerprint = UUID.randomUUID().toString()
            if (FingerprintManager.addFingerprint(this, newFingerprint)) {
                loadFingerprints()
                Toast.makeText(this, "指纹添加成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "指纹添加失败", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "指纹录入取消", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMaxFingerprintDialog() {
        AlertDialog.Builder(this)
            .setTitle("指纹数量已达上限")
            .setMessage("您已存储5个指纹，请删除不需要的指纹后再添加")
            .setPositiveButton("确定", null)
            .show()
    }
}
