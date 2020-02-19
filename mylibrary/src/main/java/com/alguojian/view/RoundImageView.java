package com.alguojian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
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
    private boolean fitxy = false;
    private Context mContext;

    private int initWidth;
    private int initHeight;

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

    public void setInitSizi(int width, int height) {
        this.initWidth = width;
        this.initHeight = height;
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        mType = a.getInt(R.styleable.RoundImageView_img_type, TYPE_CIRCLE);
        mRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_radius, RADIUS_INVALIDATE);

        mLeftTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_leftTopRadius, 0);
        fitxy = a.getBoolean(R.styleable.RoundImageView_img_fitxy, false);
        mLeftBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_leftBottomRadius, 0);
        mRightTopRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_rightTopRadius, 0);
        mRightBottomRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_img_rightBottomRadius, 0);
        mAspectRatio = a.getFloat(R.styleable.RoundImageView_img_aspectRatio, -1);
        if (mRadius == RADIUS_INVALIDATE) {
            mRadius = mLeftBottomRadius == 0 ? mLeftTopRadius == 0 ? mRightBottomRadius == 0 ? mRightTopRadius : mRightBottomRadius : mLeftTopRadius : mLeftBottomRadius;
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (initWidth > 0f && initHeight > 0f) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            float scale = (float) initHeight / (float) initWidth;
            if (width > 0) {
                height = (int) ((float) width * scale);
            }
            setMeasuredDimension(width, height);
            return;
        }
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
        mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);

        float scaleWidth = 1.0f;
        float scaleHeight = 1.0f;
        float widthroportion = getWidth() * 1.0f / bmp.getWidth();
        float heightroportion = getHeight() * 1.0f / bmp.getHeight();

        //设置图片全部加载，不进行裁剪
        if (fitxy) {
            mMatrix.setScale(widthroportion, heightroportion);
        } else {
            if (mType == TYPE_CIRCLE) {
                // 拿到bitmap宽或高的小值
                int bSize = Math.max(bmp.getWidth(), bmp.getHeight());
                scaleWidth = mWidth * 1.0f / bSize;
                scaleHeight = scaleWidth;
            } else if (mType == TYPE_ROUND) {
                //图片宽度大于控件宽度，或者图片高度大于图片高度
                if (bmp.getWidth() != getWidth() || bmp.getHeight() != getHeight()) {
                    scaleHeight = scaleWidth = Math.max(widthroportion, heightroportion);
                }
            }
            mMatrix.setScale(scaleWidth, scaleHeight);
            mMatrix.postTranslate(-(bmp.getWidth() * scaleWidth - getWidth()) / 2, 0);
        }
        mBitmapShader.setLocalMatrix(mMatrix);
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
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public void setLeftRadius(int dp) {
        this.mLeftTopRadius = dip2px(dp);
        this.mLeftBottomRadius = dip2px(dp);
        this.mRadius = mLeftTopRadius;
        invalidate();
    }

    public void setRightRadius(int dp) {
        this.mRightTopRadius = dip2px(dp);
        this.mRightBottomRadius = dip2px(dp);
        this.mRadius = mRightTopRadius;
        invalidate();
    }

    public int dip2px(int dpValue) {
        if (mContext == null || mContext.getResources() == null || mContext.getResources().getDisplayMetrics() == null) {
            return 0;
        }
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
