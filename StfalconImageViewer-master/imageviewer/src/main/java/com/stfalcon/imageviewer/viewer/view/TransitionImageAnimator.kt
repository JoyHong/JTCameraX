package com.stfalcon.imageviewer.viewer.view


import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import com.stfalcon.imageviewer.common.extensions.isRectVisible
import com.stfalcon.imageviewer.common.pager.RecyclingPagerAdapter
import com.stfalcon.imageviewer.viewer.adapter.ImagesPagerAdapter

internal class TransitionImageAnimator(
    private val externalImage: View?,
    private var internalImage: View?,
    private var imagesPager: ViewPager2
) {

    companion object {
        private const val TRANSITION_DURATION = 5000L
    }

    internal var isAnimating = false
    private var isClosing = false
    private var scaleNumber: Float = 0f
    private var resetToXValue: Float = 0f
    private var resetToYValue: Float = 0f
    var viewType = RecyclingPagerAdapter.VIEW_TYPE_IMAGE
    var scaleSize = 1.0f
    internal fun animateOpen(
        onTransitionStart: (Long) -> Unit, onTransitionEnd: () -> Unit
    ) {
        if (externalImage.isRectVisible) {
            onTransitionStart(TRANSITION_DURATION)
            doOpenTransition(onTransitionEnd)
        } else {
            onTransitionEnd()
        }
    }

    internal fun transitionAnimateClose(
        translationX: Float,
        translationY: Float,
        scaleTemp: Float,
        shouldDismissToBottom: Boolean,
        onTransitionStart: (Long) -> Unit,
        onTransitionEnd: () -> Unit
    ) {
        if (externalImage.isRectVisible && !shouldDismissToBottom) {
            onTransitionStart(TRANSITION_DURATION)
            doCloseTransition(translationX, translationY, scaleTemp, onTransitionEnd)
        } else {
            onTransitionEnd()
        }
    }

    internal fun animateClose(
        shouldDismissToBottom: Boolean,
        onTransitionStart: (Long) -> Unit,
        onTransitionEnd: () -> Unit
    ) {
        if (externalImage.isRectVisible && !shouldDismissToBottom) {
            onTransitionStart(TRANSITION_DURATION)
            doCloseTransition(onTransitionEnd)
        } else {
            onTransitionEnd()
        }
    }

    private fun doOpenTransition(onTransitionEnd: () -> Unit) {
        isAnimating = true
        startAnimation(internalImage, externalImage, onTransitionEnd, true)

    }


    private fun doCloseTransition(
        translationX: Float, translationY: Float, scaleTemp: Float, onTransitionEnd: () -> Unit
    ) {
        isAnimating = true
        isClosing = true
        val p1: PropertyValuesHolder =
            PropertyValuesHolder.ofFloat("translationX", translationX, resetToXValue)
        val p2: PropertyValuesHolder =
            PropertyValuesHolder.ofFloat("translationY", translationY, resetToYValue)
        val p3: PropertyValuesHolder =
            PropertyValuesHolder.ofFloat("scaleX", scaleTemp, scaleNumber)
        val p4: PropertyValuesHolder =
            PropertyValuesHolder.ofFloat("scaleY", scaleTemp, scaleNumber)
        val animator: ObjectAnimator =
            ObjectAnimator.ofPropertyValuesHolder(internalImage, p1, p2, p3, p4)
        animator.duration = TRANSITION_DURATION
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                if (!isClosing) {
                    isAnimating = false
                }
                onTransitionEnd.invoke()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
        animator.start()
    }


    private fun doCloseTransition(onTransitionEnd: () -> Unit) {
        isAnimating = true
        isClosing = true
        startAnimation(internalImage, externalImage, onTransitionEnd, false)
    }

    fun updateTransitionView(itemView: View?, externalImage: View?) {
        this.internalImage = itemView!!
        //缩放动画
        val imagesAdapter = imagesPager.adapter as ImagesPagerAdapter<*>
        val position = imagesPager.currentItem
        val imageItemRatio: Float = imagesAdapter.getItemViewRatio(position)
        val toX: Float
        if (imageItemRatio == -1F) {//若图片的比例是null就会传递一个-1F进来，就用原来方案
            val scaleX = externalImage!!.width * 1f / itemView.width
            val scaleY = externalImage.height * 1f / itemView.height
            toX = if (scaleX > scaleY) {  //那个缩放的比例小就用哪个(例如: 0.9 收缩比例 比0.3要小)
                scaleX
            } else {
                scaleY
            }
        } else {
            val imageViewRatio = externalImage!!.width * 1F / externalImage.height//图片的imageview的宽高比
            val scale1 = externalImage.height * 1F / (itemView.width / imageItemRatio)//x轴满，拉伸y轴方向
            val scale2 = externalImage.width * 1F / (itemView.height * imageItemRatio)//y轴满，拉伸x轴方向
            val scale3 = externalImage.width * 1F / itemView.width//x轴满，拉伸x轴方向
            val scale4 = externalImage.height * 1F / itemView.height//y轴满，拉伸y轴方向
            val phoneRatio = itemView.width * 1F / itemView.height//手机的屏幕比例
            toX =
                if (imageItemRatio > imageViewRatio && imageItemRatio > phoneRatio) {//图片宽高比大于imageview宽高比，说明要拉伸y轴
                    //  图片宽高比大于手机的宽高比说明图片点击查看的时候在x轴是满的
                    scale1
                } else if (imageItemRatio > imageViewRatio && imageItemRatio <= phoneRatio) {
                    //图片宽高比小于，说明在图片点击查看的时候y轴是满的
                    scale4
                } else if (imageItemRatio < imageViewRatio && imageItemRatio < phoneRatio) {//图片宽高比小于imageview宽高比，说明要拉伸x轴
                    //图片宽高比小于手机宽高比，说明点击图片的时候，y轴是满的
                    scale2
                } else {
                    //图片宽高比大于手机狂高比，说明点击图片的时候，x轴是满的
                    scale3
                }
        }

        //保存缩放比例,拖动缩小后恢复到原图大小需用到比例
        scaleNumber = toX

        //平移到外部imageView的中心点
        val location = IntArray(2)
        externalImage.getLocationOnScreen(location)

        val externalCenterX = (location[0] + externalImage.width / 2)
        val externalCenterY = (location[1] + externalImage.height / 2)

        //获取itemView中心点
        val itemViewLocation = IntArray(2)
        itemView.getLocationOnScreen(itemViewLocation)


        val centerX = itemViewLocation[0] + itemView.width / 2
        val centerY = itemViewLocation[1] + itemView.height / 2

        val toXValue = (externalCenterX - centerX) * 1f
        val toYValue = (externalCenterY - centerY) * 1f

        resetToXValue = toXValue
        resetToYValue = toYValue
    }

    private fun startAnimation(
        itemView: View?,
        externalImage: View?,
        onTransitionEnd: (() -> Unit)? = null,
        isOpen: Boolean,
    ) {
        //缩放动画
        //externalImage可以得到imageview的宽高
        val imagesAdapter = imagesPager.adapter as ImagesPagerAdapter<*>
        val position = imagesPager.currentItem
        val imageItemRatio: Float = imagesAdapter.getItemViewRatio(position)
        val toX: Float
        if (imageItemRatio == -1F) {//若图片的比例是null就会传递一个-1F进来，就用原来方案
            val scaleX = externalImage!!.width * 1f / itemView!!.width
            val scaleY = externalImage.height * 1f / itemView.height
            toX = if (scaleX > scaleY) {  //那个缩放的比例小就用哪个(例如: 0.9 收缩比例 比0.3要小)
                scaleX
            } else {
                scaleY
            }
        } else {
            val imageViewRatio = externalImage!!.width * 1F / externalImage.height//图片的imageview的宽高比
            val scale1 = externalImage.height * 1F / (itemView!!.width / imageItemRatio)//x轴满，拉伸y轴方向
            val scale2 = externalImage.width * 1F / (itemView.height * imageItemRatio)//y轴满，拉伸x轴方向
            val scale3 = externalImage.width * 1F / itemView.width//x轴满，拉伸x轴方向
            val scale4 = externalImage.height * 1F / itemView.height//y轴满，拉伸y轴方向
            val phoneRatio = itemView.width * 1F / itemView.height//手机的屏幕比例
            toX =
                if (imageItemRatio > imageViewRatio && imageItemRatio > phoneRatio) {//图片宽高比大于imageview宽高比，说明要拉伸y轴
                    //  图片宽高比大于手机的宽高比说明图片点击查看的时候在x轴是满的
                    scale1
                } else if (imageItemRatio > imageViewRatio && imageItemRatio <= phoneRatio) {
                    //图片宽高比小于，说明在图片点击查看的时候y轴是满的
                    scale4
                } else if (imageItemRatio < imageViewRatio && imageItemRatio < phoneRatio) {//图片宽高比小于imageview宽高比，说明要拉伸x轴
                    //图片宽高比小于手机宽高比，说明点击图片的时候，y轴是满的
                    scale2
                } else {
                    //图片宽高比大于手机狂高比，说明点击图片的时候，x轴是满的
                    scale3
                }
        }

        if (!isOpen && viewType == RecyclingPagerAdapter.VIEW_TYPE_IMAGE) {
            fun animResetScale(view: View?) {
                if (view is PhotoView) {
                    view.setScale(1F, true)
                    return
                }
                if (view is ViewGroup) {
                    val count = view.childCount
                    for (index in 0..count) {
                        val childView = view.getChildAt(index)
                        animResetScale(childView)
                    }
                }
            }
            // itemView 是 dismissContainer, FrameLayout 包裹一个 ViewPager2, @see image_viewer_mage_viewer
            val viewPager2 =
                itemView.findViewById<ViewPager2>(com.stfalcon.imageviewer.R.id.imagesPager)
            val childView =
                (viewPager2?.getChildAt(0) as RecyclerView).layoutManager?.findViewByPosition(
                    viewPager2.currentItem
                )
            animResetScale(childView)
        }

        scaleNumber = toX
        //以自己为中心进行缩放
        val scaleAnimation: ScaleAnimation = if (isOpen) {
            ScaleAnimation(
                toX, 1f, toX, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        } else {
            ScaleAnimation(
                1f, toX, 1f, toX, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        }

        //平移到外部imageView的中心点
        val location = IntArray(2)
        externalImage.getLocationOnScreen(location)

        val externalCenterX = (location[0] + externalImage.width / 2)
        val externalCenterY = (location[1] + externalImage.height / 2)

        //获取itemView中心点
        val itemViewLocation = IntArray(2)
        itemView.getLocationOnScreen(itemViewLocation)


        val centerX = itemViewLocation[0] + itemView.width / 2
        val centerY = itemViewLocation[1] + itemView.height / 2

        val toXValue = (externalCenterX - centerX) * 1f
        val toYValue = (externalCenterY - centerY) * 1f

        resetToXValue = toXValue
        resetToYValue = toYValue

        val translateAnimation: TranslateAnimation = if (isOpen) {
            TranslateAnimation(
                Animation.ABSOLUTE,
                toXValue,
                Animation.ABSOLUTE,
                0f,
                Animation.ABSOLUTE,
                toYValue,
                Animation.ABSOLUTE,
                0f
            )
        } else {
            TranslateAnimation(
                Animation.ABSOLUTE,
                0f,
                Animation.ABSOLUTE,
                toXValue,
                Animation.ABSOLUTE,
                0f,
                Animation.ABSOLUTE,
                toYValue
            )
        }

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)//缩放动画
        animationSet.addAnimation(translateAnimation)//移动图片至界面中心
        animationSet.duration = TRANSITION_DURATION
        animationSet.fillAfter = true
        itemView.startAnimation(animationSet)

        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                //结束以后关闭dialog即可
                if (!isClosing) {
                    isAnimating = false
                }
                onTransitionEnd?.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
    }
}
