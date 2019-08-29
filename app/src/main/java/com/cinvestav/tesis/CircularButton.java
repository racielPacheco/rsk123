package com.cinvestav.tesis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * @author Created by
 */
public class CircularButton extends ImageView{

    private int width;
    private int height;
    private int borderWidth;
    private Paint paint;
    private Paint paintBorder;
    private int defaultColor;

    public CircularButton(Context context) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);

        setHeight(80);
        setWidth(80);

        if(true) {
            setBorderWidth(8);
            defaultColor = Color.rgb(230,230,230);//Color.GRAY;
            setBorderColor(defaultColor);
        }

        if(false)
            addShadow();
    }

    @Override
    public void setImageResource(int resId) {
        setTag(Integer.valueOf(resId));
        super.setImageResource(resId);
    }
    public void setSizeImage(int n){
        if (n == 0){
            setHeight(60);
            setWidth(60);

            if(true) {
                setBorderWidth(5);
                defaultColor = Color.rgb(230,230,230);//Color.GRAY;
                setBorderColor(defaultColor);
            }

            if(false)
                addShadow();
        }else{
            setHeight(80);
            setWidth(80);

            if(true) {
                setBorderWidth(8);
                defaultColor = Color.rgb(230,230,230);//Color.GRAY;
                setBorderColor(defaultColor);
            }

            if(false)
                addShadow();
        }

    }

    public int getImageResource() {
        if(getTag() instanceof Integer)
            return (Integer)getTag();
        else
            return 0;
    }

    public int getViewWidth() {
        return width;
    }

    public void setWidth(int viewWidth) {
        this.width = viewWidth;
        this.invalidate();
    }

    public int getViewHeight() {
        return height;
    }

    public void setHeight(int viewHeight) {
        this.height = viewHeight;
        this.invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        this.invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        this.invalidate();
    }

    public void resetBorderColor(){
        setBorderColor(defaultColor);
    }

    public void addShadow() {
        setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
        invalidate();
    }

    public void remShadow() {
        setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.clearShadowLayer();
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        Bitmap image = null;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();
        if (bitmapDrawable != null)
            image = bitmapDrawable.getBitmap();

        if (image != null) {
            BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvas.getWidth(),
                    canvas.getHeight(), true), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            int circleCenter = width / 2;

            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth - 4.0f, paintBorder);
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter - 4.0f, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = width + (borderWidth * 2);
        int viewHeight = height + (borderWidth * 2) + 2;
        setMeasuredDimension(viewWidth, viewHeight);
    }

}