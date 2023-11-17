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

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

const val FLAG_SHOW_STATUS_BAR = 0x00000001 // 显示状态栏
const val FLAG_HIDE_STATUS_BAR = 0x00000002 // 隐藏状态
const val FLAG_SHOW_NAVIGATION_BAR = 0x00000010 // 显示导航栏
const val FLAG_HIDE_NAVIGATION_BAR = 0x00000020 // 隐藏导航栏
const val MODE_LIGHT = 1 // 使状态栏和导航栏控件变成深色(背景色为浅色)background_light
const val MODE_DARK = 2 // 使状态栏和导航栏控件变成浅色(背景色为深色)background_dark
const val MODE_TRANSPARENT=3//使状态栏为透明
/**
 * 设置状态栏和导航栏
 * 不使用WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN和WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS来实现全屏显示布局
 * WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS会导致一些系统控件超戳屏幕，比如PopupWindow
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        demo(this,FLAG_HIDE_STATUS_BAR )
    }

    fun demo(activity: Activity, flags: Int=0,mode:Int=-1){
        val window:Window=activity.window
        val controller =  WindowCompat.getInsetsController(window,window.decorView)
        if (controller != null) {
            if (hasFlag(flags, FLAG_SHOW_STATUS_BAR)) {
                controller.show(WindowInsetsCompat.Type.statusBars())//显示状态栏
            } else if (hasFlag(flags, FLAG_HIDE_STATUS_BAR)) {
                controller.hide(WindowInsetsCompat.Type.statusBars())
                //系统栏会临时显示，但不会持续显示。
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            //设置导航栏
            if (hasFlag(flags, FLAG_SHOW_NAVIGATION_BAR)) {
                controller.show(WindowInsetsCompat.Type.navigationBars())//显示导航栏
            } else if (hasFlag(flags, FLAG_HIDE_NAVIGATION_BAR)) {
                controller.hide(WindowInsetsCompat.Type.navigationBars())//隐藏导航栏
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            if (mode > 0) {
                if (mode == MODE_DARK) {
                    val darkColor=  ContextCompat.getColor(activity, R.color.background_dark)
                    window.statusBarColor=darkColor
                    window.navigationBarColor=darkColor
                    controller.isAppearanceLightStatusBars = false
                } else if (mode==MODE_LIGHT){
                    val lightColor=  ContextCompat.getColor(activity, R.color.background_light)
                    window.statusBarColor=lightColor
                    window.navigationBarColor=lightColor
                    controller.isAppearanceLightStatusBars = true//通知栏的文字显示
                }
                if (mode==MODE_TRANSPARENT){
                    window.statusBarColor=Color.TRANSPARENT
                    window.navigationBarColor=Color.TRANSPARENT
                    controller.isAppearanceLightStatusBars = true
                }
            }

        }
    }
    private fun hasFlag(flag: Int, mask: Int): Boolean {
        return flag and mask == mask
    }
}