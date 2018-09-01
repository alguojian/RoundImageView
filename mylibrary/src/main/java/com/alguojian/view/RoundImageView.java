package com.alguojian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;


/**
 * 自定义imageview，支持圆角，指定某一个角圆角
 *
 * @author alguojian
 * @date 2018.03.03
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    private static final int RADIUS_INVALIDATE = -1;
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";
    private Paint mBitmapPaint;
    private int mCircleRadius;
    private Matrix mMatrix;
    private BitmapShader mBitmapShader;
    private int mWidth;
    private RectF mRoundRect;
    private int mType;
    private int mRadius;
    private int mLeftTopRadius;
    private int mLeftBottomRadius;
    private int mRightTopRadius;
    private int mRightBottomRadius;
    private float mAspectRatio;
    private Context mContext;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mContext = context;
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        //默认为圆形图片
        mType = a.getInt(R.styleable.RoundImageView_imgType, TYPE_CIRCLE);
        mRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_radius, RADIUS_INVALIDATE);

        mLeftTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_leftTopRadius, 0);
        mLeftBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_leftBottomRadius, 0);
        mRightTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_rightTopRadius, 0);
        mRightBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_rightBottomRadius, 0);
        mAspectRatio = a.getFloat(R.styleable.RoundImageView_aspectRatio, -1);
        if (mRadius == RADIUS_INVALIDATE) {
            mRadius = mLeftBottomRadius == 0 ? mLeftTopRadius == 0 ? mRightBottomRadius == 0 ? mRightTopRadius : mRightBottomRadius : mLeftTopRadius : mLeftBottomRadius;
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 如果类型是圆形，则强制改变view的宽高一致，以小值为准
        if (mType == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mCircleRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
            return;
        }

        if (mAspectRatio > 0) {
            int height = (int) (getMeasuredWidth() / mAspectRatio);
            setMeasuredDimension(getMeasuredWidth(), height);
        } else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        if (mType == TYPE_ROUND) {

            canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mBitmapPaint);

            if (mLeftBottomRadius != 0 || mLeftTopRadius != 0 || mRightTopRadius != 0 || mRightBottomRadius != 0) {

                RectF rectF = new RectF();
                if (mLeftTopRadius == 0) {
                    rectF.left = mRoundRect.left;
                    rectF.right = mRoundRect.left + mRadius;
                    rectF.top = mRoundRect.top;
                    rectF.bottom = mRoundRect.top + mRadius;
                    canvas.drawRect(rectF, mBitmapPaint);
                }

                if (mRightTopRadius == 0) {
                    rectF.left = mRoundRect.right - mRadius;
                    rectF.right = mRoundRect.right;
                    rectF.top = mRoundRect.top;
                    rectF.bottom = mRoundRect.top + mRadius;
                    canvas.drawRect(rectF, mBitmapPaint);
                }

                if (mLeftBottomRadius == 0) {
                    rectF.left = mRoundRect.left;
                    rectF.right = mRoundRect.left + mRadius;
                    rectF.top = mRoundRect.bottom - mRadius;
                    rectF.bottom = mRoundRect.bottom;
                    canvas.drawRect(rectF, mBitmapPaint);
                }

                if (mRightBottomRadius == 0) {
                    rectF.left = mRoundRect.right - mRadius;
                    rectF.right = mRoundRect.right;
                    rectF.top = mRoundRect.bottom - mRadius;
                    rectF.bottom = mRoundRect.bottom;
                    canvas.drawRect(rectF, mBitmapPaint);
                }

            }

        } else {
            canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mBitmapPaint);
        }
    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitmap(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
        float scale = 1.0f;
        if (mType == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            int bSize = Math.max(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (mType == TYPE_ROUND) {
            if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())) {
                // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                scale = Math.max(getWidth() * 1.0f / bmp.getWidth(),
                        getHeight() * 1.0f / bmp.getHeight());
            }
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 设置左边两个角圆形
     * @param dp 角度
     */
    public void setLeftRadius(int dp) {
        this.mLeftTopRadius = dip2px(mContext, dp);
        this.mLeftBottomRadius = dip2px(mContext, dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }

    /**
     * 设置左上角圆形
     * @param dp 角度
     */
    public void setLeftTopRadius(int dp) {
        this.mLeftTopRadius = dip2px(mContext, dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }

    /**
     * 设置左下角圆形
     * @param dp 角度
     */
    public void setLeftBottomRadius(int dp) {
        this.mLeftBottomRadius = dip2px(mContext, dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }

    /**
     * 设置右下角圆形
     * @param dp 角度
     */
    public void setRightBottomRadius(int dp) {
        this.mRightBottomRadius = dip2px(mContext, dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }

    /**
     * 设置右上角圆形
     * @param dp 角度
     */
    public void setRightTopRadius(int dp) {
        this.mRightTopRadius = dip2px(mContext, dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }


    public int dip2px(Context context, float dpValue) {
        if (context == null || context.getResources() == null || context.getResources().getDisplayMetrics() == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setRightRadius(int dp) {
        this.mRightTopRadius = dip2px(mContext, dp);
        this.mRightBottomRadius = dip2px(mContext, dp);
        this.mRadius = mRightTopRadius;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (mType == TYPE_ROUND) {
            mRoundRect = new RectF(0, 0, w, h);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, mType);
        bundle.putInt(STATE_BORDER_RADIUS, mRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.mType = bundle.getInt(STATE_TYPE);
            this.mRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }

    }

    public void setRadius(int radius) {
        int pxVal = dp2px(radius);
        if (this.mRadius != pxVal) {
            this.mRadius = pxVal;
            invalidate();
        }
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public void setType(int type) {
        if (this.mType != type) {
            this.mType = type;
            if (this.mType != TYPE_ROUND && this.mType != TYPE_CIRCLE) {
                this.mType = TYPE_CIRCLE;
            }
            requestLayout();
        }
    }

    public float getAspectRatio() {
        return mAspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }
}
