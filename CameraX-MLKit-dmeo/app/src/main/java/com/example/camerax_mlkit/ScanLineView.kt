package com.example.camerax_mlkit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet


class ScanLineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var scanLineBitmap: Bitmap? = null
    private var animationSet: AnimationSet? = null
    private val maxImageWidthInDp = 480F
    private val paint = Paint()
    private var scanLineY = 0f
    private var viewHeight = 0
    private var viewWidth = 0


    init {
        scanLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scan_line_60)
        paint.isAntiAlias = true
        setAnimation()
    }

    private fun setAnimation() {
        val totalDuration = 2000L
        val endDuration = 1500L

// 由不透明到透明
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = totalDuration - endDuration
        alphaAnimation.repeatCount = 0
        alphaAnimation.startOffset = 1000L
        alphaAnimation.repeatMode = Animation.RESTART
        alphaAnimation.interpolator = LinearInterpolator()
// 创建一个位置渐变动画
        val translateAnimation = TranslateAnimation(0f, 0f, 0f, viewHeight.toFloat())
        translateAnimation.duration = totalDuration
        translateAnimation.repeatCount = 0
        translateAnimation.repeatMode = Animation.RESTART
        translateAnimation.interpolator = LinearInterpolator()
// 创建一个动画集合，包括透明度和位置渐变动画
        animationSet = AnimationSet(true)
        animationSet!!.addAnimation(alphaAnimation)
        animationSet!!.addAnimation(translateAnimation)
        animationSet!!.setAnimationListener(object : Animation.AnimationListener {
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

    private fun initialize() {
        var scaleRatio =
            (viewWidth.toFloat() / scanLineBitmap!!.width).coerceAtMost(viewHeight.toFloat() / scanLineBitmap!!.height)
        val maxImageWidthInPixels =
            (maxImageWidthInDp * resources.displayMetrics.density).toInt()
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
        setAnimation()
    }

    private fun stopAnimation() {
        if (animationSet != null) {
            animationSet?.cancel() // 取消动画
            clearAnimation() // 清除动画
            animationSet = null
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        scanLineBitmap?.let {
            canvas?.drawBitmap(
                it,
                ((viewWidth - scanLineBitmap!!.width) / 2).toFloat(),
                scanLineY,
                paint
            )
            //保证在视图的中心位置
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        initialize()
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        setAnimation()
//    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}






