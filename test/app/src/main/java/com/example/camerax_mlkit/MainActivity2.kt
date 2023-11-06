package com.example.camerax_mlkit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewStructure
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext

class MainActivity2 : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var Button: Button
    private lateinit var Button1: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Button = findViewById(R.id.submit)
        Button.setOnClickListener {
            val autofillManager = getSystemService(AutofillManager::class.java)
            autofillManager.commit()//提交信息，直接保存,也可以用finish（）进行保存
        }
        //autofillManager
        //	isAutofillSupported() 当前设备是否支持自动填充
        //	isEnabled() 检查是否为当前用户启用了自动填充。
        //如果该视图可修改，请通过对 AutofillManager 对象调用 notifyValueChanged() 将相关更改告知自动填充框架。
        //	getNextAutofillId()  获取活动上下文的下一个唯一自动填充 ID。
        //	getUserData() 获取用于字段分类的用户数据。
        //	getUserDataId() 获取用于字段分类的 ID
        // registerCallback () 自动填充事件的回调
        //	unregisterCallback()取消回调
        //getNextAutofillId ()//获取下一个自动填充的唯一id
        //	setUserData() 设置用于字段分类的UserData
        //	setUserData() 设置用于字段分类的UserData
        editText = findViewById(R.id.passwordField)
        editText.setOnClickListener { view ->
            eventHandler(view)
        }
        var name = getMyContactInfo(this)
        Toast.makeText(this, "$name", Toast.LENGTH_SHORT).show()
        Button1 = findViewById(R.id.cancel)
        Button1.setOnClickListener { cancelAutofill() }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun eventHandler(view: View) {
        val afm = this.getSystemService(AutofillManager::class.java)
        afm?.requestAutofill(view)//强制执行自动填充请求
    }
    @RequiresApi(Build.VERSION_CODES.O)
     fun cancelAutofill() {//取消自动填充
        val autofillManager = getSystemService(AutofillManager::class.java)
        autofillManager?.cancel()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onProvideAutofillStructure(structure: ViewStructure, flags: Int) {
        structure.setDataIsSensitive(false)//设置敏感信息
    }
}