package com.example.assistant

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.push_notification.api.PushNotificationApi

/**
 * 推送通知设置界面
 * 允许用户设置延迟推送通知的内容和时间
 */
class NotificationSettingsActivity : AppCompatActivity() {
    
    private lateinit var titleEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var delaySeekBar: SeekBar
    private lateinit var delayValueText: TextView
    private lateinit var scheduleButton: Button
    
    // 延迟时间（分钟）
    private var delayMinutes: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        
        // 初始化推送通知模块
        PushNotificationApi.initialize(applicationContext)
        
        // 设置标题栏标题和返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "推送通知设置"
        
        // 初始化界面组件
        initializeViews()
        setupListeners()
    }
    
    private fun initializeViews() {
        titleEditText = findViewById(R.id.editTextTitle)
        messageEditText = findViewById(R.id.editTextMessage)
        delaySeekBar = findViewById(R.id.seekBarDelay)
        delayValueText = findViewById(R.id.textViewDelayValue)
        scheduleButton = findViewById(R.id.buttonSchedule)
        
        // 设置SeekBar的范围：1-60分钟
        delaySeekBar.max = 59
        updateDelayText(0) //
    }
    
    private fun setupListeners() {
        // SeekBar监听器
        delaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 最小值为1分钟
                delayMinutes = progress + 1
                updateDelayText(delayMinutes)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // 调度按钮监听器
        scheduleButton.setOnClickListener {
            scheduleNotification()
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun updateDelayText(minutes: Int) {
        delayValueText.text = "${minutes}分钟"
    }
    
    private fun scheduleNotification() {
        val title = titleEditText.text.toString()
        val message = messageEditText.text.toString()
        
        // 简单验证
        if (title.isBlank() || message.isBlank()) {
            Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 调用推送通知模块的API
        PushNotificationApi.scheduleNotification(
            context = this,
            title = title,
            message = message,
            delayMinutes = delayMinutes
        )

        // 提示用户
        Toast.makeText(
            this, 
            "推送通知已设置，将在${delayMinutes}分钟后发送",
            Toast.LENGTH_LONG
        ).show()
        
        // 重置输入
        titleEditText.text.clear()
        messageEditText.text.clear()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}