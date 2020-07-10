package com.alguojian.view

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet


/**
 * 自定义ImageView，自定义高度为灰色
 *
 * @author alguojian
 * @date 2020.07.08
 */
class HeightColorImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    private lateinit var mBitmapPaint: Paint
    private var mContext: Context? = null
    private lateinit var mMatrix: Matrix
    private var percentage = 0.5f

    private lateinit var paint: Paint

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        mBitmapPaint = Paint()
        paint = Paint()
        mMatrix = Matrix()
        mBitmapPaint.isAntiAlias = true
        paint.isAntiAlias = true
        val saturation = ColorMatrix()
        saturation.setSaturation(0f)
        mBitmapPaint.colorFilter = ColorMatrixColorFilter(saturation)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        val bmp = drawableToBitmap(drawable)

        val widthProportion = width * 1.0f / bmp.width
        val heightProportion = height * 1.0f / bmp.height

        mMatrix.setScale(widthProportion, heightProportion)

        val createBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mMatrix, true)

        canvas.drawBitmap(createBitmap, 0f, 0f, mBitmapPaint)

        val aa = (bmp.height * (1.0 - percentage)).toInt()
        val bb = (bmp.height * percentage).toInt()

        if (bb != 0) {
            val createBitmap2 = Bitmap.createBitmap(bmp, 0, aa, bmp.width, bb, mMatrix, false)
            canvas.drawBitmap(createBitmap2, 0f, height * (1.0f - percentage), paint)
            createBitmap2.recycle()
        }
        createBitmap.recycle()
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

    /**
     * 设置高度灰色的占比，百分比
     */
    fun setProgress(percentage: Float) {
        this.percentage = percentage
        invalidate()
    }


    init {
        init(context, attrs)
    }

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["percentage"])
        fun HeightColorImageView.setFloat(float: Float = 0.0f) {
            this.percentage = float
        }
    }
}

