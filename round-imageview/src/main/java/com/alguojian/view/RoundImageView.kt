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
import android.util.TypedValue

/**
 * 自定义ImageView，支持圆角，指定某一个角圆角
 *
 * @author alguojian
 * @date 2018.03.03
 */
class RoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mBitmapPaint: Paint? = null
    private var mCircleRadius = 0
    private var mMatrix: Matrix? = null
    private var mBitmapShader: BitmapShader? = null
    private var mWidth = 0
    private var mRoundRect: RectF? = null
    private var mType = 0
    private var mRadius = 0
    private var mLeftTopRadius = 0
    private var mLeftBottomRadius = 0
    private var mRightTopRadius = 0
    private var mRightBottomRadius = 0
    private var mAspectRatio = 0f
    private var scaleType = 0
    private var mContext: Context? = null
    private var initWidth = 0
    private var initHeight = 0

    /**
     * 主动设置宽高
     * @param width
     * @param height
     */
    fun setInitSizi(width: Int, height: Int) {
        initWidth = width
        initHeight = height
        invalidate()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        mMatrix = Matrix()
        mBitmapPaint = Paint()
        mBitmapPaint!!.isAntiAlias = true
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        mType = a.getInt(R.styleable.RoundImageView_img_type, TYPE_CIRCLE)
        mRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_radius, RADIUS_INVALIDATE)
        mLeftTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_leftTopRadius, 0)
        scaleType = a.getInt(R.styleable.RoundImageView_img_scale_type, 2)
        mLeftBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_leftBottomRadius, 0)
        mRightTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_rightTopRadius, 0)
        mRightBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_rightBottomRadius, 0)
        mAspectRatio = a.getFloat(R.styleable.RoundImageView_img_aspectRatio, -1f)
        if (mRadius == RADIUS_INVALIDATE) {
            mRadius = if (mLeftBottomRadius == 0) if (mLeftTopRadius == 0) if (mRightBottomRadius == 0) mRightTopRadius else mRightBottomRadius else mLeftTopRadius else mLeftBottomRadius
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (initWidth > 0f && initHeight > 0f) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            var height = MeasureSpec.getSize(heightMeasureSpec)
            val scale = initHeight.toFloat() / initWidth.toFloat()
            if (width > 0) {
                height = (width.toFloat() * scale).toInt()
            }
            setMeasuredDimension(width, height)
            return
        }
        // 如果类型是圆形，则强制改变view的宽高一致，以小值为准
        if (mType == TYPE_CIRCLE) {
            mWidth = measuredWidth.coerceAtMost(measuredHeight)
            mCircleRadius = mWidth / 2
            setMeasuredDimension(mWidth, mWidth)
            return
        }
        if (mAspectRatio > 0) {
            val height = (measuredWidth / mAspectRatio).toInt()
            setMeasuredDimension(measuredWidth, height)
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        setUpShader()
        if (mType == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRect!!, mRadius.toFloat(), mRadius.toFloat(), mBitmapPaint!!)
            if (mLeftBottomRadius != 0 || mLeftTopRadius != 0 || mRightTopRadius != 0 || mRightBottomRadius != 0) {
                val rectF = RectF()
                if (mLeftTopRadius == 0) {
                    rectF.left = mRoundRect!!.left
                    rectF.right = mRoundRect!!.left + mRadius
                    rectF.top = mRoundRect!!.top
                    rectF.bottom = mRoundRect!!.top + mRadius
                    canvas.drawRect(rectF, mBitmapPaint!!)
                }
                if (mRightTopRadius == 0) {
                    rectF.left = mRoundRect!!.right - mRadius
                    rectF.right = mRoundRect!!.right
                    rectF.top = mRoundRect!!.top
                    rectF.bottom = mRoundRect!!.top + mRadius
                    canvas.drawRect(rectF, mBitmapPaint!!)
                }
                if (mLeftBottomRadius == 0) {
                    rectF.left = mRoundRect!!.left
                    rectF.right = mRoundRect!!.left + mRadius
                    rectF.top = mRoundRect!!.bottom - mRadius
                    rectF.bottom = mRoundRect!!.bottom
                    canvas.drawRect(rectF, mBitmapPaint!!)
                }
                if (mRightBottomRadius == 0) {
                    rectF.left = mRoundRect!!.right - mRadius
                    rectF.right = mRoundRect!!.right
                    rectF.top = mRoundRect!!.bottom - mRadius
                    rectF.bottom = mRoundRect!!.bottom
                    canvas.drawRect(rectF, mBitmapPaint!!)
                }
            }
        } else {
            canvas.drawCircle(mCircleRadius.toFloat(), mCircleRadius.toFloat(), mCircleRadius.toFloat(), mBitmapPaint!!)
        }
    }

    /**
     * 初始化BitmapShader
     */
    private fun setUpShader() {
        val drawable = drawable ?: return
        val bmp = drawableToBitmap(drawable)
        mBitmapShader = BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP)
        var scaleWidth = 1.0f
        var scaleHeight = 1.0f
        val widthProportion = width * 1.0f / bmp.width
        val heightProportion = height * 1.0f / bmp.height

        //设置图片全部加载，不进行裁剪
        if (scaleType == 1) {
            mMatrix!!.setScale(widthProportion, heightProportion)
        } else {
            if (mType == TYPE_CIRCLE) {
                // 拿到bitmap宽或高的小值
                val bSize = bmp.width.coerceAtMost(bmp.height)
                scaleWidth = mWidth * 1.0f / bSize
                scaleHeight = scaleWidth
            } else if (mType == TYPE_ROUND) {
                //图片宽度大于控件宽度，或者图片高度大于图片高度
                if (bmp.width != width || bmp.height != height) {
                    scaleWidth = widthProportion.coerceAtLeast(heightProportion)
                    scaleHeight = scaleWidth
                }
            }
            mMatrix!!.setScale(scaleWidth, scaleHeight)
            if (scaleType == 2) {
                mMatrix!!.postTranslate(0f, -(bmp.height * scaleHeight - height) / 2)
            }
        }
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

    fun setLeftRadius(dp: Int) {
        mLeftTopRadius = dip2px(dp)
        mLeftBottomRadius = dip2px(dp)
        mRadius = mLeftTopRadius
        invalidate()
    }

    fun setRightRadius(dp: Int) {
        mRightTopRadius = dip2px(dp)
        mRightBottomRadius = dip2px(dp)
        mRadius = mRightTopRadius
        invalidate()
    }

    fun dip2px(dpValue: Int): Int {
        if (mContext == null || mContext!!.resources == null || mContext!!.resources.displayMetrics == null) {
            return 0
        }
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 圆角图片的范围
        if (mType == TYPE_ROUND) {
            mRoundRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState())
        bundle.putInt(STATE_TYPE, mType)
        bundle.putInt(STATE_BORDER_RADIUS, mRadius)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            super.onRestoreInstanceState(state
                    .getParcelable(STATE_INSTANCE))
            mType = bundle.getInt(STATE_TYPE)
            mRadius = bundle.getInt(STATE_BORDER_RADIUS)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setRadius(radius: Int) {
        val pxVal = dp2px(radius)
        if (mRadius != pxVal) {
            mRadius = pxVal
            invalidate()
        }
    }

    fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal.toFloat(), resources.displayMetrics).toInt()
    }

    fun setType(type: Int) {
        if (mType != type) {
            mType = type
            if (mType != TYPE_ROUND && mType != TYPE_CIRCLE) {
                mType = TYPE_CIRCLE
            }
            requestLayout()
        }
    }

    var aspectRatio: Float
        get() = mAspectRatio
        set(aspectRatio) {
            mAspectRatio = aspectRatio
            requestLayout()
        }

    companion object {
        const val TYPE_CIRCLE = 0
        const val TYPE_ROUND = 1
        private const val RADIUS_INVALIDATE = -1
        private const val STATE_INSTANCE = "state_instance"
        private const val STATE_TYPE = "state_type"
        private const val STATE_BORDER_RADIUS = "state_border_radius"
    }

    init {
        init(context, attrs)
    }
}