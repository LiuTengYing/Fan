package com.example.fan

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.syu.ipc.FinalRemoteToolkit
import com.syu.remote.ModuleManager
import com.syu.remote.MessageObserver
import module.sound.FinalSound
import java.io.File

class MainActivity : AppCompatActivity() {

    internal var isAmpOn = false
    private var isFanOn = false
    private var isAutoMode = false  // 自动模式默认关闭
    private var isManualMode = false // 手动模式标志
    private var isCelsius = true // 温度单位，默认为摄氏度
    private var tempThreshold = 68.0f // 默认温度阈值（摄氏度）
    
    // 设备原生控制状态
    private var deviceCpuTemp: Float = 0.0f // 设备CPU温度
    private var deviceFanState = false // 设备风扇状态
    private var deviceAutoMode = false // 设备自动模式状态

    private lateinit var ampReceiver: BroadcastReceiver
    private lateinit var moduleManager: ModuleManager
    private lateinit var toolkit: FinalRemoteToolkit
    private lateinit var fanAnimator: ObjectAnimator
    private lateinit var indicatorLight: ImageView
    private lateinit var fanLight: ImageView
    private lateinit var cpuTemperature: TextView
    private lateinit var hintButton: ImageView
    private lateinit var autoModeSwitch: ImageView
    private lateinit var tempUnitSwitch: ImageView
    private var isReceiverRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        // 初始化声音模块
        moduleManager = ModuleManager(this)
        toolkit = FinalRemoteToolkit()

        // 注册声音模块回调
        registerSoundModuleCallback()
        
        // 注册主模块回调
        registerMainModuleCallback()

        // 初始化UI元素
        val fanBlades = findViewById<ImageView>(R.id.fan_blades)
        val leftSwitch = findViewById<ImageView>(R.id.left_switch)
        val rightSwitch = findViewById<ImageView>(R.id.right_switch)
        val leftArrow = findViewById<ImageView>(R.id.left_arrow)
        val rightArrow = findViewById<ImageView>(R.id.right_arrow)
        hintButton = findViewById(R.id.hint_button)
        indicatorLight = findViewById(R.id.indicator_light)
        fanLight = findViewById(R.id.fan_light)
        cpuTemperature = findViewById(R.id.cpu_temperature)
        autoModeSwitch = findViewById(R.id.auto_mode_switch)
        tempUnitSwitch = findViewById(R.id.temp_unit_switch)

        // 设置自动模式开关点击事件
        autoModeSwitch.setOnClickListener {
            val newAutoMode = !isAutoMode
            // 发送命令到设备
            try {
                moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN, 
                    FinalMain.Options.FAN_AUTO_MODE, 
                    if (newAutoMode) FinalMain.Mode.AUTO else FinalMain.Mode.MANUAL)
                Log.d("MainActivity", "Sent FAN_AUTO_MODE command: ${if (newAutoMode) "AUTO" else "MANUAL"}")
                
                // 本地状态更新（设备回调会同步）
                isAutoMode = newAutoMode
                if (newAutoMode) {
                    isManualMode = false // 退出手动模式
                }
                updateAutoModeSwitch()
                val message = if (newAutoMode) getString(R.string.auto_mode_enabled) else getString(R.string.auto_mode_disabled)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to send FAN_AUTO_MODE command: ${e.message}")
                Toast.makeText(this, getString(R.string.failed_to_switch_mode), Toast.LENGTH_SHORT).show()
            }
        }

        // 设置温度单位切换按钮点击事件
        tempUnitSwitch.setOnClickListener {
            isCelsius = !isCelsius
            updateTemperatureDisplay()
            val message = if (isCelsius) getString(R.string.switched_to_celsius) else getString(R.string.switched_to_fahrenheit)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // 初始化风扇动画
        fanAnimator = ObjectAnimator.ofFloat(fanBlades, "rotation", -0f, 540f).apply {
            duration = 50
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        // 设置按钮点击事件
        leftSwitch.setOnClickListener {
            try {
                isManualMode = true // 进入手动模式
                isAutoMode = false // 退出自动模式
                
                // 发送自动模式关闭命令
                moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN, 
                    FinalMain.Options.FAN_AUTO_MODE, FinalMain.Mode.MANUAL)
                
                // 发送风扇开启命令
                moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN,
                    FinalMain.Commands.C_FAN_CYCLE, FinalMain.FanState.MANUAL_ON)
                
                if (!isAmpOn) {
                    toggleAmp(true) // 打开功放开关
                }
                
                if (!isFanOn) {
                    turnOnFan(leftSwitch, rightSwitch, leftArrow, rightArrow)
                }
                
                updateAutoModeSwitch()
                Log.d("MainActivity", "Manual fan ON command sent")
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to send manual fan ON command: ${e.message}")
                Toast.makeText(this, getString(R.string.failed_to_control_fan), Toast.LENGTH_SHORT).show()
            }
        }

        rightSwitch.setOnClickListener {
            try {
                isManualMode = true // 进入手动模式
                isAutoMode = false // 退出自动模式
                
                // 发送自动模式关闭命令
                moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN, 
                    FinalMain.Options.FAN_AUTO_MODE, FinalMain.Mode.MANUAL)
                
                // 发送风扇关闭命令
                moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN,
                    FinalMain.Commands.C_FAN_CYCLE, FinalMain.FanState.MANUAL_OFF)
                
                if (isFanOn) {
                    turnOffFan(leftSwitch, rightSwitch, leftArrow, rightArrow)
                }
                toggleAmp(false) // 关闭功放开关
                
                updateAutoModeSwitch()
                Log.d("MainActivity", "Manual fan OFF command sent")
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to send manual fan OFF command: ${e.message}")
                Toast.makeText(this, getString(R.string.failed_to_control_fan), Toast.LENGTH_SHORT).show()
            }
        }

        // 从SharedPreferences加载保存的温度阈值
        val sharedPrefs = getSharedPreferences("fan_settings", Context.MODE_PRIVATE)
        tempThreshold = sharedPrefs.getFloat("temp_threshold", 68.0f)

        // 设置提示按钮点击事件（长按修改阈值）
        hintButton.setOnClickListener {
            val currentThreshold = if (isCelsius) String.format("%.1f℃", tempThreshold) 
                else String.format("%.1f℉", celsiusToFahrenheit(tempThreshold))
            val hintMessage = getString(R.string.temperature_threshold_hint, currentThreshold)
            Toast.makeText(this, hintMessage, Toast.LENGTH_LONG).show()
        }

        hintButton.setOnLongClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            val input = android.widget.EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            input.setText(if (isCelsius) tempThreshold.toString() else celsiusToFahrenheit(tempThreshold).toString())
            
            val dialogMessage = if (isCelsius) getString(R.string.enter_celsius_value) else getString(R.string.enter_fahrenheit_value)
            builder.setTitle(getString(R.string.set_temperature_threshold))
                .setMessage(dialogMessage)
                .setView(input)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    try {
                        val newValue = input.text.toString().toFloat()
                        tempThreshold = if (isCelsius) newValue else fahrenheitToCelsius(newValue)
                        // 保存到SharedPreferences
                        sharedPrefs.edit().putFloat("temp_threshold", tempThreshold).apply()
                        
                        // 同步温度阈值到设备系统
                        syncTemperatureThresholdToDevice(tempThreshold)
                        
                        Toast.makeText(this, getString(R.string.temperature_threshold_updated), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
            true
        }

        // 注册广播接收器
        registerAmpReceiver()

        // 开始实时更新温度显示
        startTemperatureUpdate()

        // 启动温度监控
        startTemperatureMonitor(leftArrow, rightArrow)
        
        // 初始化完成后，同步当前温度阈值到设备
        Handler(Looper.getMainLooper()).postDelayed({
            syncTemperatureThresholdToDevice(tempThreshold)
            Log.d("MainActivity", "Initial temperature threshold synced to device: ${tempThreshold}°C")
        }, 1000) // 延迟1秒确保设备连接稳定
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerAmpReceiver() {
        ampReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("MainActivity", "Received broadcast: ${intent?.action}")
                if (intent?.action == "com.syu.ms.toolkit.ACTION_CMD") {
                    val ampState = intent.getIntExtra("state", -1)
                    Log.d("MainActivity", "Received AMP state update: $ampState")
                    if (ampState != -1) {
                        handleAmpStateChange(ampState == 1)
                    } else {
                        Log.e("MainActivity", "Invalid AMP state received")
                    }
                }
            }
        }

        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction("com.syu.ms.toolkit.ACTION_CMD")
            }
            registerReceiver(ampReceiver, filter)
            isReceiverRegistered = true
            Log.d("MainActivity", "AMP BroadcastReceiver registered successfully")
        }
    }

    // 注册声音模块的回调函数
    private fun registerSoundModuleCallback() {
        val soundModuleObserver = object : MessageObserver.ModuleObserver(moduleManager, FinalRemoteToolkit.MODULE_CODE_SOUND, FinalSound.U_AMP) {
            override fun onReceiver(message: com.syu.remote.Message) {
                if (message.code == FinalSound.U_AMP) {
                    val ampState = message.ints?.get(0) ?: -1
                    Log.d("MainActivity", "Received sound module U_AMP update with ampState = $ampState")
                    handleAmpStateChange(ampState == 1)
                }
            }
        }

        moduleManager.addObserver(soundModuleObserver)
    }
    
    // 注册主模块的回调函数
    private fun registerMainModuleCallback() {
        val mainModuleObserver = object : MessageObserver.ModuleObserver(moduleManager, FinalRemoteToolkit.MODULE_CODE_MAIN, FinalMain.Options.FAN_AUTO_MODE) {
            override fun onReceiver(message: com.syu.remote.Message) {
                when (message.code) {
                    FinalMain.Options.FAN_AUTO_MODE -> {
                        val autoMode = message.ints?.get(0) ?: 0
                        Log.d("MainActivity", "Received FAN_AUTO_MODE update: $autoMode")
                        handleAutoModeChange(autoMode == FinalMain.Mode.AUTO)
                    }
                    FinalMain.Updates.U_FAN_CYCLE -> {
                        val fanState = message.ints?.get(0) ?: 0
                        Log.d("MainActivity", "Received U_FAN_CYCLE update: $fanState")
                        handleDeviceFanStateChange(fanState)
                    }
                    FinalMain.Options.CPU_RUNNING_TEMP -> {
                        val messageInts = message.ints
                        if (messageInts != null) {
                            if (messageInts.size >= 2) {
                                // 可能是温度阈值设置消息: [下限, 上限]
                                Log.d("MainActivity", "Received temperature threshold update: ${messageInts.contentToString()}")
                                handleDeviceTemperatureThresholdUpdate(messageInts)
                            } else if (messageInts.size == 1) {
                                // 单个温度值，是CPU温度更新
                                val temp = messageInts[0]
                                Log.d("MainActivity", "Received CPU_RUNNING_TEMP update: $temp")
                                handleDeviceCpuTempUpdate(temp.toFloat())
                            }
                        }
                    }
                }
            }
        }
        
        // 注册观察多个消息码
        moduleManager.observeModule(FinalRemoteToolkit.MODULE_CODE_MAIN, 
            FinalMain.Options.FAN_AUTO_MODE,
            FinalMain.Updates.U_FAN_CYCLE,
            FinalMain.Options.CPU_RUNNING_TEMP)
        
        moduleManager.addObserver(mainModuleObserver)
    }
    
    // 处理设备自动模式变化
    private fun handleAutoModeChange(autoMode: Boolean) {
        runOnUiThread {
            deviceAutoMode = autoMode
            // 同步UI状态
            if (isAutoMode != autoMode) {
                isAutoMode = autoMode
                updateAutoModeSwitch()
                val message = if (autoMode) getString(R.string.device_auto_mode_on) else getString(R.string.device_auto_mode_off)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
            Log.d("MainActivity", "Device auto mode synchronized: $autoMode")
        }
    }
    
    // 处理设备风扇状态变化
    private fun handleDeviceFanStateChange(fanState: Int) {
        runOnUiThread {
            deviceFanState = fanState != FinalMain.FanState.STOPPED
            val statusText = if (deviceFanState) getString(R.string.device_running) else getString(R.string.device_stopped)
            Log.d("MainActivity", "Device fan state updated: $fanState ($statusText)")
            
            // 强制同步设备风扇状态到UI，确保界面与实际硬件状态一致
            val newFanState = fanState != FinalMain.FanState.STOPPED
            if (isFanOn != newFanState) {
                Log.d("MainActivity", "Syncing device fan state to UI: ${if (newFanState) "ON" else "OFF"}")
                isFanOn = newFanState
                updateFanLight()
                if (isFanOn) {
                    startFanAnimation()
                    startArrowAnimation(
                        findViewById(R.id.left_arrow),
                        findViewById(R.id.right_arrow)
                    )
                } else {
                    stopFanAnimation()
                    stopArrowAnimation(
                        findViewById(R.id.left_arrow),
                        findViewById(R.id.right_arrow)
                    )
                }
                updateSwitches(
                    findViewById(R.id.left_switch),
                    findViewById(R.id.right_switch)
                )
                
                // 如果设备关闭了风扇，自动退出手动模式，让系统接管控制
                if (!newFanState && isManualMode) {
                    isManualMode = false
                    Log.d("MainActivity", "Device turned off fan, exiting manual mode")
                    Toast.makeText(this@MainActivity, getString(R.string.device_control_detected), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    // 处理设备CPU温度更新
    private fun handleDeviceCpuTempUpdate(temp: Float) {
        runOnUiThread {
            deviceCpuTemp = temp
            Log.d("MainActivity", "Device CPU temperature updated: ${temp}°C")
            
            // 更新温度显示，优先使用设备温度
            val displayTemp = if (isCelsius) {
                String.format("%.1f℃", temp)
            } else {
                String.format("%.1f℉", celsiusToFahrenheit(temp))
            }
            cpuTemperature.text = "CPU: $displayTemp"
        }
    }
    
    // 处理设备温度阈值更新 (从设备接收的阈值设置)
    private fun handleDeviceTemperatureThresholdUpdate(thresholdData: IntArray) {
        if (thresholdData.size >= 2) {
            // 设备发送的格式: [下限温度, 上限温度]
            // 我们使用上限温度作为阈值
            val deviceThreshold = thresholdData[1].toFloat()
            
            runOnUiThread {
                // 检查是否与本地阈值不同
                if (Math.abs(tempThreshold - deviceThreshold) > 1.0f) {
                    tempThreshold = deviceThreshold
                    
                    // 保存到SharedPreferences
                    val sharedPrefs = getSharedPreferences("fan_settings", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putFloat("temp_threshold", tempThreshold).apply()
                    
                    Log.d("MainActivity", "Temperature threshold synced from device: ${tempThreshold}°C")
                    val syncMessage = getString(R.string.temperature_threshold_synchronized, "${tempThreshold}")
                    Toast.makeText(this, syncMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    // 同步温度阈值到设备
    private fun syncTemperatureThresholdToDevice(threshold: Float) {
        try {
            // 根据设备原生系统的实现：发送 [下限, 上限] 温度
            // 下限设为阈值-5°C，上限设为阈值温度
            val lowerTemp = (threshold - 5).toInt()
            val upperTemp = threshold.toInt()
            
            moduleManager.sendCmd(
                FinalRemoteToolkit.MODULE_CODE_MAIN,
                FinalMain.Options.FAN_TEMP_THRESHOLD,
                lowerTemp, upperTemp
            )
            
            Log.d("MainActivity", "Temperature threshold sent to device: lower=${lowerTemp}°C, upper=${upperTemp}°C")
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to sync temperature threshold to device: ${e.message}")
            Toast.makeText(this, getString(R.string.failed_sync_temperature_threshold), Toast.LENGTH_SHORT).show()
        }
    }

    internal fun handleAmpStateChange(isOn: Boolean) {
        // 更新状态
        isAmpOn = isOn

        runOnUiThread {
            try {
                // 更新指示灯状态
                updateIndicatorLight()
                Log.d("MainActivity", "Amp state changed: isAmpOn = $isAmpOn")

                if (!isOn) {
                    // 停止风扇并更新状态
                    stopFanAnimation()
                    stopArrowAnimation(
                        findViewById(R.id.left_arrow),
                        findViewById(R.id.right_arrow)
                    )
                    isFanOn = false

                    // 更新开关和指示灯状态
                    updateSwitches(
                        findViewById(R.id.left_switch),
                        findViewById(R.id.right_switch)
                    )
                    updateFanLight()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error handling AMP state change: ${e.message}")
            }
        }
    }

    private fun toggleAmp(state: Boolean) {
        val cmd = if (state) 1 else 0
        try {
            Log.d("MainActivity", "Preparing to send broadcast: state = $cmd")

            val intent = Intent("com.syu.ms.toolkit.ACTION_CMD")
            intent.putExtra("module_code", FinalRemoteToolkit.MODULE_CODE_SOUND)
            intent.putExtra("command_code", FinalSound.C_AMP)
            intent.putExtra("state", cmd)

            // 发送广播以切换功放状态
            sendBroadcast(intent)

            // 使用 ModuleManager 发送指令来切换功放状态
            moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_SOUND, FinalSound.C_AMP, cmd)

            Log.d("MainActivity", "Broadcast and command sent successfully with state = $cmd to AMP")

            // 更新本地状态
            isAmpOn = state

            // 更新UI
            runOnUiThread {
                updateIndicatorLight()
                updateFanLight()
            }

            // 延迟检查状态
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("MainActivity", "Checking AMP state after broadcast: isAmpOn = $isAmpOn")
            }, 500)  // 延迟500毫秒
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to send command to AMP via broadcast: ${e.message}")
        }
    }

    private fun startTemperatureUpdate() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                updateTemperatureDisplay()
                handler.postDelayed(this, 2000) // 每2秒更新一次
            }
        }
        handler.post(runnable)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTemperatureDisplay() {
        // 优先使用设备提供的温度数据
        val temperature = if (deviceCpuTemp > 0) {
            deviceCpuTemp
        } else {
            readCpuTemperature()
        }
        
        if (temperature != null && temperature > 0) {
            val displayTemp = if (isCelsius) {
                String.format("%.1f℃", temperature)
            } else {
                String.format("%.1f℉", celsiusToFahrenheit(temperature))
            }
            cpuTemperature.text = "CPU: $displayTemp"
        } else {
            cpuTemperature.text = "CPU: N/A"
        }
    }

    private fun celsiusToFahrenheit(celsius: Float): Float {
        return celsius * 9 / 5 + 32
    }

    private fun fahrenheitToCelsius(fahrenheit: Float): Float {
        return (fahrenheit - 32) * 5 / 9
    }

    private fun updateAutoModeSwitch() {
        autoModeSwitch.setImageDrawable(ContextCompat.getDrawable(this,
            if (isAutoMode) R.drawable.auto_mode_on else R.drawable.auto_mode_off))
    }

    private fun readCpuTemperature(): Float? {
        return try {
            val tempFile = File("/sys/class/thermal/thermal_zone0/temp")
            val tempValue = tempFile.readText().trim().toFloat() / 1000
            Log.d("MainActivity", "Read CPU Temperature: $tempValue °C")
            tempValue
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to read CPU temperature", e)
            null
        }
    }

    private fun getCurrentCpuTemperature(): Float {
        // 优先使用设备提供的温度数据
        return if (deviceCpuTemp > 0) {
            deviceCpuTemp
        } else {
            readCpuTemperature() ?: 49.0f // 默认温度
        }
    }

    private fun startTemperatureMonitor(leftArrow: ImageView, rightArrow: ImageView) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (isAutoMode && !isManualMode) { // 只有在非手动模式下，自动模式才有效
                    // 自动模式下持续监控温度
                    checkAndStartFanIfNeeded(leftArrow, rightArrow)
                }
                handler.postDelayed(this, 2000) // 每2秒检查一次温度
            }
        }
        handler.post(runnable)
    }

    private fun checkAndStartFanIfNeeded(leftArrow: ImageView, rightArrow: ImageView) {
        val currentTemperature = getCurrentCpuTemperature()
        val thresholdTemp = tempThreshold
        
        // 如果当前显示华氏度，需要将当前温度转换为摄氏度进行比较
        val tempForComparison = if (isCelsius) currentTemperature else fahrenheitToCelsius(currentTemperature)

        if (isAutoMode && !isManualMode && tempForComparison >= thresholdTemp) {
            // 温度达到阈值或以上，启动风扇和功放
            if (!isFanOn) {
                isFanOn = true
                updateFanLight()  // 更新风扇指示灯为绿色
                startFanAnimation()
                startArrowAnimation(leftArrow, rightArrow)
                toggleAmp(true)  // 打开功放
                val displayTemp = if (isCelsius) "${currentTemperature}℃" else "${celsiusToFahrenheit(currentTemperature)}℉"
                Log.d("MainActivity", "Fan and AMP started in auto mode at temperature: $displayTemp")
            }
        } else if (isAutoMode && !isManualMode && isFanOn && tempForComparison < thresholdTemp) {
            // 温度下降到65°C以下，关闭风扇和功放
            isFanOn = false
            updateFanLight()  // 更新风扇指示灯
            stopFanAnimation()
            stopArrowAnimation(leftArrow, rightArrow)
            toggleAmp(false)  // 关闭功放
            Log.d("MainActivity", "Fan and AMP stopped in auto mode at temperature: $currentTemperature")
        } else {
            Log.d("MainActivity", "Current temperature ($currentTemperature°C) is between 65°C and 68°C. No change in fan and AMP state.")
        }
    }

    private fun turnOnFan(leftSwitch: ImageView, rightSwitch: ImageView, leftArrow: ImageView, rightArrow: ImageView) {
        isManualMode = true // 手动模式激活
        if (!isAmpOn) toggleAmp(true) // 确保功放打开
        isFanOn = true
        updateSwitches(leftSwitch, rightSwitch)
        startFanAnimation()
        startArrowAnimation(leftArrow, rightArrow)
        updateFanLight()
        Log.d("MainActivity", "Fan turned on manually")
    }

    private fun turnOffFan(leftSwitch: ImageView, rightSwitch: ImageView, leftArrow: ImageView, rightArrow: ImageView) {
        isManualMode = true // 手动模式激活
        isFanOn = false
        updateSwitches(leftSwitch, rightSwitch)
        stopFanAnimation()
        stopArrowAnimation(leftArrow, rightArrow)
        updateFanLight()
        toggleAmp(false) // 关闭功放
        Log.d("MainActivity", "Fan turned off manually")
    }

    internal fun updateIndicatorLight() {
        val lightDrawable = if (isAmpOn) R.drawable.circle_shape_green else R.drawable.circle_shape_red
        indicatorLight.setImageDrawable(ContextCompat.getDrawable(this, lightDrawable))
    }

    internal fun updateFanLight() {
        val lightDrawable = if (isFanOn) R.drawable.fan_start_light else R.drawable.fan_inner_light
        fanLight.setImageDrawable(ContextCompat.getDrawable(this, lightDrawable))
    }

    internal fun stopFanAnimation() {
        if (fanAnimator.isStarted) {
            fanAnimator.cancel()
        }
    }

    internal fun updateSwitches(leftSwitch: ImageView, rightSwitch: ImageView) {
        leftSwitch.setImageDrawable(ContextCompat.getDrawable(this,
            if (isFanOn) R.drawable.open else R.drawable.close))
        rightSwitch.setImageDrawable(ContextCompat.getDrawable(this,
            if (!isFanOn) R.drawable.open else R.drawable.close))
    }

    private fun startFanAnimation() {
        if (!fanAnimator.isStarted) {
            fanAnimator.start()
        }
    }

    private fun stopArrowAnimation(leftArrow: ImageView, rightArrow: ImageView) {
        leftArrow.clearAnimation()
        rightArrow.clearAnimation()
    }

    private fun startArrowAnimation(leftArrow: ImageView, rightArrow: ImageView) {
        val leftAnimation = android.view.animation.TranslateAnimation(0f, 20f, 0f, 0f).apply {
            duration = 500
            repeatCount = android.view.animation.Animation.INFINITE
            repeatMode = android.view.animation.Animation.REVERSE
        }
        leftArrow.startAnimation(leftAnimation)

        val rightAnimation = android.view.animation.TranslateAnimation(0f, -20f, 0f, 0f).apply {
            duration = 500
            repeatCount = android.view.animation.Animation.INFINITE
            repeatMode = android.view.animation.Animation.REVERSE
        }
        rightArrow.startAnimation(rightAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isReceiverRegistered) {
            Log.d("MainActivity", "Unregistering BroadcastReceiver")
            unregisterReceiver(ampReceiver)
            isReceiverRegistered = false
        }
        moduleManager.release()  // 释放资源
    }
}
