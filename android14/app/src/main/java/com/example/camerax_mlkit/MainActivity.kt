/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.camerax_mlkit

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.app.Activity
import android.app.GrammaticalInflectionManager
import android.app.LocaleManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //行为变更：所有应用

        //系统共享表：
        //使用 ChooserAction.Builder 创建自定义 ChooserAction，
        // 并将 ChooserActions 列表指定为使用 Intent.createChooser 创建的 Intent 的Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS。

        //accessibilityDataSensitive：允许应用将指定视图的可见性限制为仅对声称可以帮助残障用户的无障碍服务可见



        //在 Android 14 中，当应用请求 Android 13（API 级别 33）中引入的任何视觉媒体权限时，
        // 用户可以授予对其照片和视频的部分访问权限：READ_MEDIA_IMAGES 或 READ_MEDIA_VIDEO。
        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

            }

        button1 = findViewById(R.id.alarm)
        button1.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.launch(
                    arrayOf(
                        READ_MEDIA_IMAGES,
                        READ_MEDIA_VIDEO,
                        READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
            } else {
                requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
        }

        //用户无法安装 targetSdkVersion 低于 23 的应用。

        //默认拒绝设定精确的闹钟，SCHEDULE_EXACT_ALARM可让应用安排精确闹钟的权限，在android13及以上（默认情况下，设置为“拒绝”）
        //需要 SCHEDULE_EXACT_ALARM 权限才能通过以下 API 启动精确闹钟，否则系统会抛出 SecurityException：
        //setExact()
        //setExactAndAllowWhileIdle()
        //setAlarmClock()
        //日历和闹钟应用应声明 USE_EXACT_ALARM


        //从 Android 14 开始，调用 killBackgroundProcesses() 时，该 API 只能终止您自己应用的后台进程


        //USE_FULL_SCREEN_INTENT，安全的全屏 intent 通知，从 Android 开始 14、允许使用此权限的应用仅限于提供仅限呼叫和警报
        //对于不适合此情况的任何应用，Google Play 商店会撤消其默认的 USE_FULL_SCREEN_INTENT 权限。
        button2 = findViewById(R.id.killb)
        button2.setOnClickListener {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 34) {
                //新 API NotificationManager.canUseFullScreenIntent 检查应用是否具有该权限
                val hasintent = notificationManager.canUseFullScreenIntent()
                Toast.makeText(this, "$hasintent", Toast.LENGTH_SHORT).show()
                //  <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
                //自动授予此权限
                if (hasintent == false) {
                    //新 intent ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT 启动设置页面
                    val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                    intent.data = Uri.parse("package:" + packageName)
                    startActivity(intent)

                }
            }

        }

//电话监听
        if (Build.VERSION.SDK_INT < 31) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneStateListener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                    super.onCallStateChanged(state, incomingNumber)
                    when (state) {
                        TelephonyManager.CALL_STATE_RINGING -> {
                            // 当电话正在响铃时执行的操作
                            // 这表示当前有来电
                            Log.d(TAG, "onCallStateChangeddemo: 电话正在响铃")
                        }

                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            // 当电话处于接听状态时执行的操作
                            // 这表示用户正在通话中
                            Log.d(TAG, "onCallStateChangeddemo:电话正在通话中 ")
                        }

                        TelephonyManager.CALL_STATE_IDLE -> {
                            // 当电话处于空闲状态时执行的操作
                            // 这表示电话挂断或结束通话
                            Log.d(TAG, "onCallStateChangeddemo: 电话空闲状态")
                        }
                    }
                }
            }
            // 注册电话状态监听器
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
// PhoneStateListener.LISTEN_CALL_STATE监听电话呼叫状态的改变
        } else {
            val telephonyManager1 = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val listener = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    when (state) {
                        TelephonyManager.CALL_STATE_RINGING -> {
                            Log.d(TAG, "onCallStateChangeddemo: 电话正在响铃")
                        }

                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            Log.d(TAG, "onCallStateChangeddemo:电话正在通话中 ")
                        }

                        TelephonyManager.CALL_STATE_IDLE -> {
                            Log.d(TAG, "onCallStateChangeddemo: 电话空闲状态")
                        }
                    }
                }
            }
            telephonyManager1.registerTelephonyCallback(this.mainExecutor, listener)

        }


        //关于不可关闭通知用户体验方式的变更：
        //FLAG_ONGOING_EVENT，Android14允许用户关闭此类通知（不可关闭的前台通知）
        //在以下情况下，此类通知仍不可关闭：
        //当手机处于锁定状态时
        //如果用户选择全部清除通知操作（有助于防止意外关闭）

        //也不适用于使用 MediaStyle 创建的通知
        //安全和隐私用例的政策限制使用
        //企业设备政策控制器 (DPC) 和支持软件包
        button4 = findViewById(R.id.note)
        button4.setOnClickListener {
            val CHANNEL_ID = "my_channel_id"
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Title")
                .setContentText("这是一句话在通知栏中显示的内容")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true) // 设置通知为持续状态，但从android14开始除了一些特殊的条件，通知都可以被关闭
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            val notification = notificationBuilder.build()
            val NOTIFICATION_ID = 1
            notificationManager.notify(NOTIFICATION_ID, notification)

        }


        // OWNER_PACKAGE_NAME:该列表示存储特定媒体文件的应用,从Android14起，除非满足其中之一否则隐去此值
        // 存储媒体文件的应用有一个软件包名称始终对其他应用可见。
        //查询媒体库的应用会请求 QUERY_ALL_PACKAGES 权限。


        //非线性字体放大至 200%，从 Android 14 开始，系统支持字体放大高达 200%


        //以 Android 14 或更高版本为目标平台的应用

        //前台服务类型是必填项:
        //应用以 Android 14 为目标平台，则必须为应用中的每个前台服务至少指定一项前台服务类型。
        //要应用前台服务，至少要指定一个foregroundServiceType
        //<service
        //          android:name=".MyMediaPlaybackService"
        //          android:foregroundServiceType="mediaPlayback"
        //          android:exported="false">
        //      </service>
        //如果调用 startForeground() 但未声明适当的前台服务类型权限，系统会抛出 SecurityException。

        //OpenJDK 17 更新:
        //对正则表达式的更改
        //UUID 处理
        //ProGuard 问题

        //对隐式 intent 和待处理 intent 的限制
        //以 Android 14 为目标平台的应用,通过以下方式限制应用向内部应用组件发送隐式 intent:
        //隐式 intent 只能传送到导出的组件。应用必须使用显式 intent 传送到未导出的组件，或将该组件标记为已导出。
        //如果应用通过未指定组件或软件包的 intent 创建可变待处理 intent，系统现在会抛出异常。

        //在运行时注册的广播接收器必须指定导出行为
        //以 Android 14 为目标平台并使用上下文注册的接收器的应用和服务必须指定以下标志，
        // 以指明接收器是否应导出到设备上的所有其他应用：RECEIVER_EXPORTED 或 RECEIVER_NOT_EXPORTED
        //仅接收系统广播的接收器的例外情况:
        //如果应用仅通过 Context#registerReceiver 方法，针对系统广播注册接收器，那么它在注册接收器时不应指定标志。

        //更安全的动态代码加载
        //使用动态代码加载 (DCL) 功能，则必须将所有动态加载的文件标记为只读。否则，系统会抛出异常

        //压缩路径遍历
        //以 Android 14 为目标平台的应用，Android 会通过以 下方式防止 Zip 路径遍历漏洞：
        // 如果 Zip 文件条目名称包含“..”或以“/”开头，ZipFile(String) 和 ZipInputStream.getNextEntry() 会抛出 ZipException。
        //应用可以通过调用 dalvik.system.ZipPathValidator.clearCallback() 选择停用此验证。

        //针对从后台启动 activity 的其他限制
        //Android 14 为目标平台的应用，系统会进一步限制允许应用在后台启动 activity 的时间：
        //发送 PendingIntent 时，如果它想要授予自己的后台 activity 启动待处理 intent 的启动特权，
        // 应用应通过 setPendingIntentBackgroundActivityStartMode(MODE_BACKGROUND_ACTIVITY_START_ALLOWED) 传递 ActivityOptions 软件包。

        //当可见应用使用 bindService() 方法绑定其他在后台应用的服务时，如果想授予后台的activity对绑定的服务的启动特权，就要选择启用
        // 应用应在调用 bindService() 方法时包含 BIND_ALLOW_ACTIVITY_STARTS 标志。


        //新功能和 API:

        //Android 14 扩展了 Android 13（API 级别 33）中引入的按应用设定语言功能:
        //自动生成应用的 localeConfig
        //动态更新应用的 localeConfig
        // 使用 LocaleManager 方法中的 setOverrideLocaleConfig() 和 getOverrideLocaleConfig()
        if (Build.VERSION.SDK_INT >= 34) {
            val localeManager: LocaleManager? = null
            localeManager?.setOverrideLocaleConfig(null)//允许应用程序在运行时动态地更改其所支持的语言环境列表
            localeManager?.getOverrideLocaleConfig()//得到当前的LocaleConfig
        }

        // 可以在设备的系统设置中动态更新应用的受支持语言列表
        //输入法 (IME) 的应用语言可见性
        //ME 可以利用 getApplicationLocales() 方法查看当前应用的语言，并将 IME 语言与该语言进行匹配。


        //语法变化 API

        button3 = findViewById(R.id.broadcast)
        button3.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 34) {
                val gIM = getSystemService(GrammaticalInflectionManager::class.java)
                gIM.setRequestedApplicationGrammaticalGender(
                    Configuration.GRAMMATICAL_GENDER_FEMININE
                )//设置语法为雌性
                val grammaticalGender = gIM.getApplicationGrammaticalGender()
                Toast.makeText(this, "$grammaticalGender", Toast.LENGTH_SHORT).show()
            }
        }

        //地区偏好设置
        // 获取温度单位
        // 获取一周首日
        // 获取小时周期
        // 获取日历类型
        //public static @NonNull String getTemperatureUnit() 温度单位  LocalePreferences.getTemperatureUnit()
        //public static @NonNull String getFirstDayOfWeek()//一周的第一天
        //getHourCycle() 获取小时周期
        //getCalendarType() 获取日历类型


        //支持内置和自定义预测性返回动画

        //针对应用商店的改进
        //下载之前请求批准安装，安装或更新应用可能需要用户批准，
        // 从 Android 14 开始，requestUserPreapproval() 方法可让安装程序在提交安装会话之前请求用户批准
        //承担未来更新的责任， setRequestUpdateOwnership() 方法，安装程序可以向系统表明它打算负责将被安装的应用未来的更新
        //在干扰性更低的情况下更新应用，从 Android 14 开始，InstallConstraints API 让安装程序可以确保其应用更新在适当的时机进行
        //无缝安装可选拆分，借助拆分 APK，应用的功能可以通过单独的 APK 文件提供，而不是以单体式 APK 的形式提供
        //在 Android 14 中，setDontKillApp() 方法可让安装程序指明在安装新的拆分项时应用的运行进程不应终止。


        //路径现在可查询和插值,从 Android 14 开始，您可以查询路径以了解其内部内容
    }


    //检测用户何时截取设备屏幕截图
    //Android 14 引入了可保护隐私的屏幕截图检测 API。
    // 应用可以按 activity 注册回调。如果用户在该 activity 可见时截取屏幕截图，系统会调用这些回调并通知用户。
    @RequiresApi(34)
    override fun onStart() {
        super.onStart()
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
    }

    val screenCaptureCallback = Activity.ScreenCaptureCallback {
        Toast.makeText(this, "检测到屏幕截图", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(34)
    override fun onStop() {
        super.onStop()
        unregisterScreenCaptureCallback(screenCaptureCallback)
    }

}
