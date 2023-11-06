package com.example.camerax_mlkit
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import kotlin.math.log

@SuppressLint("Range")
fun getMyContactInfo(context: Context): String {
    val contentResolver: ContentResolver = context.contentResolver
    val cursor = contentResolver.query(
        ContactsContract.Profile.CONTENT_URI,//获取本机的信息
        null,//查询时不进行筛选
        null,
        null,
        null
    )
    var myInfo = "未找到名片信息"
    cursor?.use { cursor ->
        if (cursor.moveToFirst()) {
            val displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)//查找我的名片中的名字
            if (displayNameIndex != -1 ) {
                val displayName = cursor.getString(displayNameIndex)
                myInfo = "名字: $displayName"
            }
        }
    }

    return myInfo
}


