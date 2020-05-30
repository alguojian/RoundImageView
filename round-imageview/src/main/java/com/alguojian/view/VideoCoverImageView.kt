package com.alguojian.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * 自定义ImageView，适用于视频封面，类似于抖音的，竖版视频
 *
 * @author alguojian
 * @date 2020.05.28
 */
class VideoCoverImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mBitmapPaint: Paint? = null
    private var mMatrix: Matrix? = null
    private var mBitmapShader: BitmapShader? = null
    private var mRoundRect: RectF? = null
    private var mContext: Context? = null
    private var canvasTop = 0f
    private var bitmapCanvasHeight = 0f

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        mMatrix = Matrix()
        mBitmapPaint = Paint()
        mBitmapPaint!!.isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        val bmp = drawableToBitmap(drawable)
        if (bmp.width >= bmp.height) {
            super.onDraw(canvas)
        } else {
            setUpShader(bmp)
            mRoundRect = RectF(0f, canvasTop, width.toFloat(), canvasTop + bitmapCanvasHeight)
            canvas.drawRect(mRoundRect!!, mBitmapPaint!!)
        }
    }

    /**
     * 初始化BitmapShader
     */
    private fun setUpShader(bmp: Bitmap) {
        mBitmapShader = BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP)
        val widthProportion = width * 1.0f / bmp.width
        val heightProportion = height * 1.0f / bmp.height
        val scaleWidth = widthProportion.coerceAtLeast(heightProportion)
        bitmapCanvasHeight = height.toFloat()
        canvasTop = 0f
        mMatrix!!.setScale(scaleWidth, scaleWidth)
        mMatrix!!.postTranslate(-(bmp.width * scaleWidth - width) / 2, -(bmp.height * scaleWidth - height) / 2)
        mBitmapShader!!.setLocalMatrix(mMatrix)
        mBitmapPaint!!.shader = mBitmapShader
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val config = if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(STATE_INSTANCE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }


    companion object {
        private const val STATE_INSTANCE = "state_instance"
    }

    init {
        init(context, attrs)
    }
}

