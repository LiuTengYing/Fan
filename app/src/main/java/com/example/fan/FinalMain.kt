package com.example.fan

/**
 * 主模块控制常量定义
 * 基于厂商提供的API接口文档
 */
object FinalMain {
    
    // 控制命令码
    object Commands {
        const val C_FAN_CYCLE = 34          // 风扇手动控制命令
    }
    
    // 更新码
    object Updates {
        const val U_FAN_CYCLE = 56          // 手动模式开关状态更新
    }
    
    // 选项码
    object Options {
        const val FAN_AUTO_MODE = 144       // 风扇自动模式
        const val CPU_RUNNING_TEMP = 145    // CPU运行温度
        const val FAN_TEMP_THRESHOLD = 145  // 风扇温度阈值设置 (同CPU_RUNNING_TEMP)
    }
    
    // 风扇状态值
    object FanState {
        const val STOPPED = 0x00            // 不转
        const val RUNNING = 0xFF            // 常转
        const val MANUAL_OFF = 0            // 手动关闭
        const val MANUAL_ON = 1             // 手动开启
    }
    
    // 模式值
    object Mode {
        const val MANUAL = 0                // 手动模式
        const val AUTO = 1                  // 自动模式
    }
}
