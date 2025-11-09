package com.example.fan

import android.content.Context
import android.util.Log
import com.syu.ipc.FinalRemoteToolkit
import com.syu.remote.ModuleManager
import com.syu.remote.MessageObserver
import module.sound.FinalSound

/**
 * 设备集成控制器
 * 负责管理与设备原生风扇控制系统的联动
 * 
 * 基于厂商API文档实现：
 * - 自动/手动模式切换
 * - 风扇控制命令发送
 * - 设备状态监听和同步
 * - CPU温度监控
 */
class DeviceIntegrationController(
    private val context: Context,
    private val moduleManager: ModuleManager,
    private val toolkit: FinalRemoteToolkit
) {
    
    // 回调接口
    interface DeviceStatusListener {
        fun onAutoModeChanged(autoMode: Boolean)
        fun onFanStateChanged(fanState: Int, isRunning: Boolean)
        fun onCpuTemperatureUpdated(temperature: Float)
        fun onTemperatureThresholdUpdated(thresholdData: IntArray)
        fun onAmpStateChanged(isOn: Boolean)
    }
    
    private var listener: DeviceStatusListener? = null
    private var mainModuleObserver: MessageObserver.ModuleObserver? = null
    private var soundModuleObserver: MessageObserver.ModuleObserver? = null
    
    companion object {
        private const val TAG = "DeviceIntegrationController"
    }
    
    /**
     * 初始化设备控制器
     */
    fun initialize(statusListener: DeviceStatusListener) {
        this.listener = statusListener
        registerObservers()
        Log.d(TAG, "Device integration controller initialized")
    }
    
    /**
     * 注册设备状态观察者
     */
    private fun registerObservers() {
        // 注册主模块观察者
        registerMainModuleObserver()
        
        // 注册声音模块观察者  
        registerSoundModuleObserver()
    }
    
    /**
     * 注册主模块观察者 - 监听风扇和温度相关消息
     */
    private fun registerMainModuleObserver() {
        mainModuleObserver = object : MessageObserver.ModuleObserver(
            moduleManager, 
            FinalRemoteToolkit.MODULE_CODE_MAIN, 
            FinalMain.Options.FAN_AUTO_MODE
        ) {
            override fun onReceiver(message: com.syu.remote.Message) {
                when (message.code) {
                    FinalMain.Options.FAN_AUTO_MODE -> {
                        val autoMode = message.ints?.get(0) ?: 0
                        Log.d(TAG, "Received FAN_AUTO_MODE: $autoMode")
                        listener?.onAutoModeChanged(autoMode == FinalMain.Mode.AUTO)
                    }
                    FinalMain.Updates.U_FAN_CYCLE -> {
                        val fanState = message.ints?.get(0) ?: 0
                        Log.d(TAG, "Received U_FAN_CYCLE: $fanState")
                        val isRunning = fanState != FinalMain.FanState.STOPPED
                        listener?.onFanStateChanged(fanState, isRunning)
                    }
                    FinalMain.Options.CPU_RUNNING_TEMP -> {
                        val messageInts = message.ints
                        if (messageInts != null) {
                            if (messageInts.size >= 2) {
                                // 温度阈值设置消息: [下限, 上限]
                                Log.d(TAG, "Received temperature threshold: ${messageInts.contentToString()}")
                                listener?.onTemperatureThresholdUpdated(messageInts)
                            } else if (messageInts.size == 1) {
                                // CPU温度更新
                                val temp = messageInts[0]
                                Log.d(TAG, "Received CPU_RUNNING_TEMP: $temp")
                                listener?.onCpuTemperatureUpdated(temp.toFloat())
                            }
                        }
                    }
                }
            }
        }
        
        // 注册观察多个消息码
        moduleManager.observeModule(
            FinalRemoteToolkit.MODULE_CODE_MAIN,
            FinalMain.Options.FAN_AUTO_MODE,
            FinalMain.Updates.U_FAN_CYCLE,
            FinalMain.Options.CPU_RUNNING_TEMP
        )
        
        mainModuleObserver?.let { moduleManager.addObserver(it) }
    }
    
    /**
     * 注册声音模块观察者 - 监听功放状态
     */
    private fun registerSoundModuleObserver() {
        soundModuleObserver = object : MessageObserver.ModuleObserver(
            moduleManager,
            FinalRemoteToolkit.MODULE_CODE_SOUND,
            FinalSound.U_AMP
        ) {
            override fun onReceiver(message: com.syu.remote.Message) {
                if (message.code == FinalSound.U_AMP) {
                    val ampState = message.ints?.get(0) ?: -1
                    Log.d(TAG, "Received U_AMP: $ampState")
                    listener?.onAmpStateChanged(ampState == 1)
                }
            }
        }
        
        soundModuleObserver?.let { moduleManager.addObserver(it) }
    }
    
    /**
     * 设置自动模式
     */
    fun setAutoMode(enabled: Boolean): Boolean {
        return try {
            val mode = if (enabled) FinalMain.Mode.AUTO else FinalMain.Mode.MANUAL
            moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN, FinalMain.Options.FAN_AUTO_MODE, mode)
            Log.d(TAG, "Sent FAN_AUTO_MODE command: ${if (enabled) "AUTO" else "MANUAL"}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set auto mode: ${e.message}")
            false
        }
    }
    
    /**
     * 手动控制风扇
     */
    fun setFanManualControl(enabled: Boolean): Boolean {
        return try {
            val state = if (enabled) FinalMain.FanState.MANUAL_ON else FinalMain.FanState.MANUAL_OFF
            moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_MAIN, FinalMain.Commands.C_FAN_CYCLE, state)
            Log.d(TAG, "Sent C_FAN_CYCLE command: ${if (enabled) "ON" else "OFF"}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to control fan manually: ${e.message}")
            false
        }
    }
    
    /**
     * 控制功放开关
     */
    fun setAmpControl(enabled: Boolean): Boolean {
        return try {
            val state = if (enabled) 1 else 0
            moduleManager.sendCmd(FinalRemoteToolkit.MODULE_CODE_SOUND, FinalSound.C_AMP, state)
            Log.d(TAG, "Sent C_AMP command: ${if (enabled) "ON" else "OFF"}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to control amp: ${e.message}")
            false
        }
    }
    
    /**
     * 设置温度阈值
     */
    fun setTemperatureThreshold(thresholdCelsius: Float): Boolean {
        return try {
            // 根据设备原生系统的实现：发送 [下限, 上限] 温度
            // 下限设为阈值-5°C，上限设为阈值温度  
            val lowerTemp = (thresholdCelsius - 5).toInt()
            val upperTemp = thresholdCelsius.toInt()
            
            moduleManager.sendCmd(
                FinalRemoteToolkit.MODULE_CODE_MAIN,
                FinalMain.Options.FAN_TEMP_THRESHOLD,
                lowerTemp, upperTemp
            )
            
            Log.d(TAG, "Sent temperature threshold: lower=${lowerTemp}°C, upper=${upperTemp}°C")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set temperature threshold: ${e.message}")
            false
        }
    }
    
    /**
     * 进入手动模式并控制风扇
     */
    fun enterManualModeAndControlFan(fanEnabled: Boolean, ampEnabled: Boolean): Boolean {
        var success = true
        
        // 1. 切换到手动模式
        if (!setAutoMode(false)) {
            success = false
        }
        
        // 2. 控制风扇
        if (!setFanManualControl(fanEnabled)) {
            success = false
        }
        
        // 3. 控制功放
        if (!setAmpControl(ampEnabled)) {
            success = false
        }
        
        Log.d(TAG, "Manual control completed. Success: $success, Fan: $fanEnabled, Amp: $ampEnabled")
        return success
    }
    
    /**
     * 获取设备连接状态
     */
    fun isDeviceConnected(): Boolean {
        return moduleManager.isConnected()
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        mainModuleObserver?.let { moduleManager.removeObserver(it) }
        soundModuleObserver?.let { moduleManager.removeObserver(it) }
        listener = null
        Log.d(TAG, "Device integration controller cleaned up")
    }
}
