package com.example.android.camerax.video

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.sqrt

class CustomGestureDetectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mGestureDetector: GestureDetector
    private var mCustomTouchListener: CustomTouchListener? = null
    private var currentDistance = 0f
    private var lastDistance = 0f


    fun setCustomTouchListener(customTouchListener: CustomTouchListener?) {
        mCustomTouchListener = customTouchListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    private var onGestureListener: GestureDetector.OnGestureListener =
        object : GestureDetector.OnGestureListener {
            //手势相关的回调
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onShowPress(e: MotionEvent) {
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {

                if (e2.pointerCount >= 2) {   // 是否大于两个触摸点
                    val offSetX = e2.getX(0) - e2.getX(1)
                    val offSetY = e2.getY(0) - e2.getY(1)
                    //计算X,Y坐标的差值，计算两点间的距离
                    currentDistance =
                        sqrt((offSetX * offSetX + offSetY * offSetY).toDouble()).toFloat()
                    if (lastDistance == 0f) { //如果是第一次进行判断
                        lastDistance = currentDistance
                    } else {
                        if (currentDistance - lastDistance > 10) {
                            // 放大
                            mCustomTouchListener?.zoom()
                        } else if (lastDistance - currentDistance > 10) {
                            // 缩小

                            mCustomTouchListener?.ZoomOut()
                        }
                    }
                    lastDistance = currentDistance
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                //如果快速滑动松开使距离变为0
                currentDistance = 0f
                lastDistance = 0f
                return true
            }
        }

    init {
        mGestureDetector = GestureDetector(context, onGestureListener)
    }
}