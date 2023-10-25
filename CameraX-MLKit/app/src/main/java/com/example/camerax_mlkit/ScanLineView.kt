package com.example.camerax_mlkit

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet

@SuppressLint("ObjectAnimatorBinding")
class ScanLineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var scanLineBitmap: Bitmap? = null
    val maxImageWidthInDp = 480F
    val density = resources.displayMetrics.density // 获取屏幕密度
    val maxImageWidthInPixels = (maxImageWidthInDp * density).toInt()
    private val paint = Paint()
    private var scanLineY = 0f
    private val paint1 = Paint()
    var viewHeight=0
    var viewWidth=0
    private var startX =0F
    private var startY = 0F
    val displayMetrics = resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels
    private var endX = 0F
    private var endY = 0F
    private var color1 = Color.argb(0, 19, 174, 103)
    private var color2 = Color.argb((0.6 * 255).toInt(), 255, 255, 255)
    private var color3 = Color.argb(0, 19, 174, 103)
    private var colors = intArrayOf(color1, color2, color3) // 渐变的颜色数组
    private var positions = floatArrayOf(0f, 0.5f, 1.0f) // 颜色的位置
    private lateinit var shader:Shader
    init {
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        scanLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.rectangle)
        paint.isAntiAlias = true
        val totalDuration = 2000L
        val startDuration=500L
        val endDuration = 1500L

// 由不透明到透明
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration =totalDuration-endDuration
        alphaAnimation.repeatCount =0
        alphaAnimation.startOffset=1000L
        alphaAnimation.repeatMode = Animation.RESTART
        alphaAnimation.interpolator = LinearInterpolator()
// 创建一个位置渐变动画
        val translateAnimation = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        translateAnimation.duration = totalDuration
        translateAnimation.repeatCount =0
        translateAnimation.repeatMode = Animation.RESTART
        translateAnimation.interpolator = LinearInterpolator()
//由透明到不透明
//        val alphaAnimation1 = AlphaAnimation(0.0f, 1.0f)
//        alphaAnimation1.duration = startDuration
//        alphaAnimation1.repeatCount =0
//        alphaAnimation1.repeatMode = Animation.RESTART
//        alphaAnimation1.interpolator = LinearInterpolator()
// 创建一个动画集合，包括透明度和位置渐变动画
        val animationSet = AnimationSet(true)
//        animationSet.addAnimation(alphaAnimation1)
        animationSet.addAnimation(alphaAnimation)
        animationSet.addAnimation(translateAnimation)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                startAnimation(animationSet)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
        startAnimation(animationSet)
    }

fun initialize(){
    try {
        var scaleRatio = Math.min(
            viewWidth.toFloat() / scanLineBitmap!!.width,
            viewHeight.toFloat() / scanLineBitmap!!.height
        )
        val maxImageWidthInPixels = (maxImageWidthInDp * resources.displayMetrics.density).toInt()
        var scaledWidth = scanLineBitmap!!.width * scaleRatio

        if (scaledWidth > maxImageWidthInPixels) {//保持最大宽度为480dp
            scaleRatio = maxImageWidthInPixels.toFloat() / scanLineBitmap!!.width
            scaledWidth = maxImageWidthInPixels.toFloat()
        }
        val scaledHeight = scanLineBitmap!!.height.toFloat()

        val matrix = Matrix()
        matrix.postScale(scaleRatio, 1f) // 只进行宽度的拉伸，高度保持不变
        matrix.postTranslate((viewWidth - scaledWidth) / 2, (viewHeight - scaledHeight) / 2)
        scanLineBitmap = Bitmap.createBitmap(
            scanLineBitmap!!,
            0,
            0,
            scanLineBitmap!!.width,
            scanLineBitmap!!.height,
            matrix,
            true
        )
    } catch (e: Exception) {
        Log.e(TAG, "demo1: $e")
    }
    startX = (viewWidth / 2-scanLineBitmap!!.width/4).toFloat()
    //计算线画在扫描线的1/4
    startY = 0F
    endX = (scanLineBitmap!!.width/4+viewWidth/2).toFloat()
    //到扫描线的3/4位置
    endY = 0F
    shader = LinearGradient(startX, startY, endX, endY, colors, positions, Shader.TileMode.CLAMP)
    startY = scanLineBitmap!!.height.toFloat() - 5
}

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint1.shader = shader
        paint1.strokeWidth = 5F
        scanLineBitmap?.let {
            canvas?.drawBitmap(it, ((viewWidth-scanLineBitmap!!.width)/2).toFloat(), scanLineY, paint)
            //保证在视图的中心位置
        }
        canvas?.drawLine(startX, startY, endX, startY, paint1)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        initialize()
    }
}






