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
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

const val FLAG_SHOW_STATUS_BAR = 0x00000001 // 显示状态栏
const val FLAG_HIDE_STATUS_BAR = 0x00000002 // 隐藏状态
const val FLAG_TRANSLUCENT_STATUS_BAR = 0x00000004 // 布局拓展
const val FLAG_NOT_TRANSLUCENT_STATUS_BAR = 0x00000008 // 布局不拓展
const val FLAG_SHOW_NAVIGATION_BAR = 0x00000010 // 显示导航栏
const val FLAG_HIDE_NAVIGATION_BAR = 0x00000020 // 隐藏导航栏
const val MODE_LIGHT = 1 // 使状态栏和导航栏控件变成深色(背景色为浅色)background_light
const val MODE_DARK = 2 // 使状态栏和导航栏控件变成浅色(背景色为深色)background_dark
/**
 * 设置状态栏和导航栏
 * 不使用WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN和WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS来实现全屏显示布局
 * WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS会导致一些系统控件超戳屏幕，比如PopupWindow
 */
class MainActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        demo(this,  FLAG_SHOW_STATUS_BAR, MODE_DARK)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun demo(activity: Activity, flags: Int=0,mode:Int=-1){
            val controller = activity.window.insetsController
            if (controller != null) {
                if (hasFlag(flags, FLAG_SHOW_STATUS_BAR)) {
                    controller.show(WindowInsets.Type.statusBars())//显示状态栏
                } else if (hasFlag(flags, FLAG_HIDE_STATUS_BAR)) {
                    controller.hide(WindowInsets.Type.statusBars())//隐藏状态栏
                    //系统栏会临时显示，但不会持续显示。
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                if (hasFlag(flags, FLAG_TRANSLUCENT_STATUS_BAR)) {
                    WindowCompat.setDecorFitsSystemWindows(window, false)// 将布局内容拓展
                } else if (hasFlag(flags, FLAG_NOT_TRANSLUCENT_STATUS_BAR)) {
                    WindowCompat.setDecorFitsSystemWindows(window, true)// 布局内容不拓展
                }
                //设置导航栏
                if (hasFlag(flags, FLAG_SHOW_NAVIGATION_BAR)) {
                    controller.show(WindowInsets.Type.navigationBars())//显示导航栏并且布局不扩展
                } else if (hasFlag(flags, FLAG_HIDE_NAVIGATION_BAR)) {
                    controller.hide(WindowInsets.Type.navigationBars())//隐藏导航栏，并且布局拓展
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                if (mode > 0) {
                  if (mode == MODE_DARK) {
                        val darkColor=  ContextCompat.getColor(activity, R.color.background_dark)
                      window.statusBarColor=darkColor
                      window.navigationBarColor=darkColor
                    } else if (mode==MODE_LIGHT){
                      val lightColor=  ContextCompat.getColor(activity, R.color.background_light)
                      window.statusBarColor=lightColor
                      window.navigationBarColor=lightColor
                      controller.setSystemBarsAppearance(//确保文本在浅色的背景下也能看见
                          WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                          WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                      )
                    }
                }

            }
    }
    private fun hasFlag(flag: Int, mask: Int): Boolean {
        return flag and mask == mask
    }
}
