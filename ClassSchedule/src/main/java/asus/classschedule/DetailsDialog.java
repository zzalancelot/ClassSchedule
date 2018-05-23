package asus.classschedule;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

class DetailsDialog extends View {

    private Context context;

    private int textColor = Color.BLACK;

    private float shadowWidth;

    private float elevation = 0;

    private Paint fillPaint;
    private TextPaint textPaint;

    private Schedule.Block classBlock;

    private OnDialogClickListener onDialogClickListener;


    public DetailsDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DetailsDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(textColor);

        shadowWidth = dpToPx(5);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setClassBlock(Schedule.Block classBlock) {
        this.classBlock = classBlock;
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    //阴影
    public void setElevation(float elevation) {
        this.elevation = elevation;
    }
    //为阴影预留的距离
    public void setShadowWidth(float shadowWidth) {
        this.shadowWidth = shadowWidth;
    }

//    public void gc(){
//        System.gc();
//    }

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
//        if (width > height) {
//            width = height;
//        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        RectF rectF = new RectF(shadowWidth, shadowWidth, getWidth() - shadowWidth, getHeight() - shadowWidth);

        fillPaint.setColor(Color.DKGRAY);
        if (elevation > 0) {
            fillPaint.setMaskFilter(new BlurMaskFilter(elevation, BlurMaskFilter.Blur.SOLID));
        }
        canvas.drawRoundRect(rectF, 20, 20, fillPaint);

        fillPaint.setMaskFilter(null);
        fillPaint.setColor(classBlock.getBlockColor());

        canvas.drawRoundRect(rectF, 20, 20, fillPaint);

        drawText(canvas, classBlock);

        fillPaint.setColor(Color.BLACK);

    }

    //TODO:增加“事情”适配
    private void drawText(Canvas canvas, Schedule.Block c) {

        ArrayList<Datas> strings = new ArrayList<>();

        Datas className = new Datas("课程名称：", c.getClassName());
        Datas classroom = new Datas("上课教室：", c.getClassroom());
        Datas teacher = new Datas("授课教师：", c.getTeacher());
        strings.add(className);
        if (!c.getClassroom().equals("null")) {
            strings.add(classroom);
        }
        if (!c.getTeacher().equals("null")) {
            strings.add(teacher);
        }
        int height = getHeight() / 3;

        float textSize = dpToPx(11);
        textPaint.setTextSize(textSize);
        float titleWidth = className.getTitle().length() * (int) textSize;
        float dataWidth = c.getWidth() * 2 - dpToPx(3.5f) - shadowWidth;

        int excessHeight = 0;

        for (int i = 0; i < strings.size(); i++) {

            textPaint.setFakeBoldText(true);

            StaticLayout staticLayout = new StaticLayout(strings.get(i).getTitle(), textPaint, (int) (titleWidth),
                    Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
            canvas.translate(dpToPx(1) + shadowWidth, dpToPx(0.5f) + shadowWidth + excessHeight);
            staticLayout.draw(canvas);
            canvas.translate(-(dpToPx(1) + shadowWidth), -(dpToPx(0.5f) + shadowWidth + excessHeight));

            textPaint.setFakeBoldText(false);

            StaticLayout staticLayout1 = new StaticLayout(strings.get(i).getData(), textPaint, (int) (dataWidth),
                    Layout.Alignment.ALIGN_CENTER, 1, 0, true);
            canvas.translate(titleWidth + dpToPx(2), dpToPx(0.5f) + shadowWidth + excessHeight);
            staticLayout1.draw(canvas);
            canvas.translate(-(titleWidth + dpToPx(2)), -(dpToPx(0.5f) + shadowWidth + excessHeight));


//            float padding_x = dpToPx(3);
//            //每行多少个字
//            int lineText = (int) ((dataWidth - (padding_x * 2)) / textSize);
            int lineText = (int) Math.ceil(dataWidth / textSize);
            int columnText = (int) (Math.ceil(strings.get(i).getData().length() / (float) lineText));
            float textHeight = columnText * textSize + dpToPx(2) * columnText + dpToPx(5);
            excessHeight += (textHeight > height) ? (int) (Math.ceil(textHeight)) : height;
        }

    }

    private float dpToPx(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (onDialogClickListener != null) {
                    onDialogClickListener.onClick();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private class Datas {
        private String title;
        private String data;

        public Datas(String title, String data) {
            this.title = title;
            this.data = data;
        }

        public String getTitle() {
            return title;
        }

        public String getData() {
            return data;
        }
    }

    interface OnDialogClickListener {
        void onClick();
    }

}
