package asus.classschedule;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ASUS on 2018/5/25.
 */
class Dialog extends View {

    private Context context;
    private int textColor = Color.BLACK;

    public Dialog(Context context) {
        super(context);
        this.context = context;
    }

    public Dialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public Dialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public Dialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    protected int getTextColor(){
        return textColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        int mWidth = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = mWidth + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mWidth + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    protected float dpToPx(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

}
