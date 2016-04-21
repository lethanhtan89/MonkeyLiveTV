package atv.com.project.popkontv.lib;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class StreamDrawable extends Drawable {
    private static final boolean USE_VIGNETTE = true;

    private final Bitmap mBitmap;
    private final float mCornerRadius;
    private final RectF mRect = new RectF();
    private BitmapShader mBitmapShader;
    private final Paint mPaint;
    private final int mMargin;
    private final int mColor;

    public StreamDrawable(Bitmap bitmap, float cornerRadius, int margin) {
        this(bitmap,cornerRadius,margin,-1);
    }

    public StreamDrawable(Bitmap bitmap,boolean noRound) {
        this(bitmap,0f,0);
    }

    public StreamDrawable(Bitmap bitmap) {
        this(bitmap,200f,2);
    }

    public StreamDrawable(Bitmap bitmap,int color,float cornerRadius) {
        this(bitmap,cornerRadius,2,color);
    }
    public StreamDrawable(Bitmap bitmap,int color) {
        this(bitmap,200f,2,color);
    }

    public StreamDrawable(Bitmap bitmap, float cornerRadius, int margin,int color) {
        mCornerRadius = cornerRadius;

        mBitmap = bitmap;
        mBitmapShader = new BitmapShader(mBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);

        mMargin = margin;
        mColor = color;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        final int width = bounds.width() - mMargin;
        final int height = bounds.height() - mMargin;

        mRect.set(mMargin, mMargin, width, height);

        mBitmapShader = new BitmapShader(Bitmap.createScaledBitmap(mBitmap,width,height,true),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        if (USE_VIGNETTE) {
            RadialGradient vignette = new RadialGradient(
                    mRect.centerX(), mRect.centerY() * 1.0f / 0.7f, mRect.centerX() * 1.3f,
                    new int[] { 0, 0, 0x7f000000 }, new float[] { 0.0f, 0.7f, 1.0f },
                    Shader.TileMode.CLAMP);

            Matrix oval = new Matrix();
            oval.setScale(1.0f, 0.7f);
            vignette.setLocalMatrix(oval);

            mPaint.setShader(
                    new ComposeShader(mBitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);

        if(mColor != -1) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mColor);
            paint.setStrokeWidth(4);
            canvas.drawArc(mRect, 0, 360, true, paint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
}
