package asus.classschedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

class DeleteDialog extends Dialog {

    private Context context;

    private boolean showClassName = true;

    private Paint fillPaint;
    private TextPaint textPaint;

    private Schedule.Block block;


    public DeleteDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setBlock(Schedule.Block block) {
        this.block = block;
    }

    private void init() {
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(getTextColor());
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int width;
//        int height;
//        int mWidth = 0;
//
//        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//        } else {
//            width = mWidth + getPaddingLeft() + getPaddingRight();
//        }
//        if (heightMode == MeasureSpec.EXACTLY) {
//            height = heightSize;
//        } else {
//            height = mWidth + getPaddingTop() + getPaddingBottom();
//        }
//        setMeasuredDimension(width, height);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        fillPaint.setColor(block.getBlockColor());
        canvas.drawRoundRect(rectF, 20, 20, fillPaint);
        drawText(canvas, block);
    }

    private void drawText(Canvas canvas, Schedule.Block Block) {
        String text;
        if (showClassName) {
            text = Block.getClassName();
        } else {
            text = Block.getClassroom();
        }
        float textSize = dpToPx(12);
//        linePaint.setTextSize(textSize);
        float padding_x = dpToPx(3);
        //每行多少个字
        int lineText = (int) ((Block.getWidth() - (padding_x * 2)) / textSize);
        //写几行
        int columnText = (int) Math.ceil(text.length() / (float) lineText);
        float textHeight = columnText * textSize + dpToPx(2) * columnText + dpToPx(5);
        float classBlockHeight = Block.getHeight() - dpToPx(10);

        boolean isEllipsis = classBlockHeight > textHeight;

//        canvas.drawLine(0, textHeight + Block.top, getWidth(), textHeight + Block.top, linePaint);

        //从哪里开始写
//        float height = Block.top + Block.getHeight() / 2 - (float) (Math.ceil(columnText / 2) * textSize);

        textPaint.setTextSize(textSize);

//        float start_x = Block.getLeft() ;
//                + dpToPx(1.5f);
//        float start_y = Block.getTop() ;
//                + dpToPx(3);
//                (Block.getHeight() / 2) - (float) (Math.floor(columnText / 2) * textSize);

        StaticLayout staticLayout = null;
        int textWidth = (int) (Block.getWidth() - (padding_x * 2 - dpToPx(1)));
        if (isEllipsis) {
            staticLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, true);
        } else {
            int lastTextNum = text.length() % lineText;
            int column = 0;
            for (int i = 1; i < columnText; i++) {
                if (i * textSize > classBlockHeight) {
                    column = i;
                    break;
                }
                column = i;
            }
            int endIndex = text.length() - lastTextNum - lineText * (columnText - column + 1);
            if (endIndex < 0) {
                endIndex = 0;
            }
            text = text.substring(0, endIndex) + "\n...";
            staticLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, true);
        }

//        canvas.translate(start_x, start_y);
        staticLayout.draw(canvas);
//        canvas.translate(-start_x, -start_y);
    }


}
