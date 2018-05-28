package asus.classschedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;
import java.util.Random;

class Schedule extends View {

    private Context context;

    private int width_default;
    private int longClickJudgeClock = 200;
    private int clickTimes = 0;
    private int block_x = -1;
    private int block_y = -1;

    private int row;
    private int column;

    private int textColor = Color.BLACK;

    private float lastX = -1;
    private float lastY = -1;

    private long downSysTime;
    private long lastUpSysTime = 0;

    private boolean isClassBlockAssignment = false;
    private boolean isBlockClick = false;
    private boolean isMoved = false;
    private boolean isDoubleJudgeRunning = false;

    private boolean isShowDayBar = true;
    private boolean isShowSidebar = true;
    //true为显示课程名，false为显示教室
    private boolean showClassName = true;

    private Paint linePaint;
    private Paint edgePaint;
    private Paint fillPaint;
    private TextPaint textPaint;

    private RectF scheduleRectF;
    private float standardWidth;
    private float standardHeight;

    private Block[][] blocks;
    private Block clickBlock;

    private CanvasParameter parameter = null;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemDoubleClickListener onItemDoubleClickListener;

    //默认颜色
    private final String COLOR_BLUE = "#33B5E5";
    private final String COLOR_VIOLET = "#AA66CC";
    private final String COLOR_GREEN = "#99CC00";
    private final String COLOR_ORANGE = "#FFBB33";
    private final String COLOR_RED = "#FF4444";
    private String[] color_default = new String[]{
            COLOR_ORANGE, COLOR_VIOLET, COLOR_BLUE, COLOR_RED
    };

    private final String[] weeks = new String[]{
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    };
    private final String[] classes = new String[]{
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"
    };

    private Date[] dates;

    public Schedule(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Schedule(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setStyle(Paint.Style.FILL);

        edgePaint = new Paint();
        edgePaint.setAntiAlias(true);
        edgePaint.setDither(true);
        edgePaint.setColor(Color.parseColor(COLOR_GREEN));
        edgePaint.setStrokeWidth(dpToPx(3));
        edgePaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setColor(textColor);

        TimeJudge timeJudge = new TimeJudge();
        dates = timeJudge.getWeekDay();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setRowAndColumn(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void setBlock(ClassBlock b, int x, int y) {
        blocks[x][y].setData(b.getClassName(), b.getClassroom(), b.getTeacher());
        blocks[x][y].setCanDelete(b.isCanDelete());
        blocks[x][y].setBlockColor(-1);
        invalidate();
    }

    public void addBlock(ClassBlock c, int x, int y) {
        blocks[x][y].addData(c.getClassName(), c.getClassroom(), c.getTeacher());
        invalidate();
    }

    //获取即将要delete的block，用以启动动画
    public BlockState deleteClassBlock(int x, int y) {
        BlockState bs = getTotalBlock(blocks[x][y], block_y);
        return bs;
    }

    public void delete(BlockState bs, int x) {
        for (int i = bs.getTopBlock(); i < bs.bottomBlock + 1; i++) {
            blocks[x][i].setClassName("null");
            blocks[x][i].setClassroom("null");
            blocks[x][i].setTeacher("null");
            blocks[x][i].setBlockColor(-1);
        }
        invalidate();
    }

    public void showDayBar(boolean isShowDayBar) {
        this.isShowDayBar = isShowDayBar;
        invalidate();
    }

    public void showSideBar(boolean isShowSidebar) {
        this.isShowSidebar = isShowSidebar;
        invalidate();
    }

    public void setShowClassName(boolean showClassName) {
        this.showClassName = showClassName;
        invalidate();
    }

    public void setLongClickTimes_default(int longClickJudgeClock) {
        this.longClickJudgeClock = longClickJudgeClock;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemDoubleClickListener(OnItemDoubleClickListener onItemDoubleClickListener) {
        this.onItemDoubleClickListener = onItemDoubleClickListener;
    }

    public float getStandardWidth() {
        return standardWidth;
    }

    public float getStandardHeight() {
        return standardHeight;
    }

    public RectF getScheduleRectF() {
        return scheduleRectF;
    }

    public boolean isShowClassName() {
        return showClassName;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //TODO:对于默认情况或者是增加了ScrollView的情况进行适配
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        int mWidth = width_default;

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
        if (width > height) {
            width = height;
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (row > 0 && column > 0) {
            blocks = new Block[row][column];
        } else {
            blocks = new Block[7][10];
        }
    }

    private void debug() {
        blocks[0][0].setClassName("毛泽东思想和中国特色社会主义理论体系概论");
        blocks[0][1].setClassName("毛泽东思想和中国特色社会主义理论体系概论");
        blocks[0][2].setClassName("毛泽东思想和中国特色社会主义理论体系概论");
        blocks[1][0].setClassName("数学123456");
        blocks[1][1].setClassName("数学123456");


        blocks[2][3].setClassName("英语");
        blocks[2][4].setClassName("英语");

        blocks[3][0].setClassName("体育");

        for (int i = 0; i < blocks[4].length; i++) {
            blocks[4][i].setClassName("语文");
        }

        blocks[0][9].setClassName("化学");
        blocks[3][9].setClassName("物理");

        blocks[6][7].setClassName("Android开发");
        blocks[6][8].setClassName("Android开发");
        blocks[6][9].setClassName("Android开发");

        blocks[6][0].setClassName("政治123456");
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        if (parameter == null) {
            parameter = new CanvasParameter();
        }

        //填充ClassBlock
        if (!isClassBlockAssignment) {
            float x = parameter.getTitleInterval_x();
            for (int i = 0; i < blocks.length; i++) {
                float y = parameter.getTitleInterval_y();
                for (int j = 0; j < blocks[i].length; j++) {
                    blocks[i][j] = new Block(x, y, parameter.getInterval_x(), parameter.getInterval_y());
                    y = y + parameter.getInterval_y();
//                    Log.d("Block",String.valueOf(x) + " " + String.valueOf(y));
                }
                x = x + parameter.getInterval_x();
            }
//            debug();
            isClassBlockAssignment = true;
        }

        drawLines(canvas, parameter.getCx(), parameter.getCy(), parameter.getTitleInterval_x(), parameter.getTitleInterval_y(), parameter.getInterval_x(), parameter.getInterval_y());

        drawClassBlocks(canvas);

        if (isBlockClick) {
            drawBlockEdge(canvas);
        }

    }

    private class CanvasParameter {
        private float cx;
        private float cy;
        /**
         * 侧边栏距离
         * 顶部距离
         */
        private float titleInterval_x;
        private float titleInterval_y;
        /**
         * x轴增加的宽度
         * y轴增加的宽度
         */
        private float interval_x;
        private float interval_y;

        public CanvasParameter() {
            cx = getWidth();
            cy = getHeight();
            titleInterval_x = (isShowSidebar) ? cx / 20 : 0;
            titleInterval_y = (isShowDayBar) ? cy / 10 : 0;
            interval_x = (cx - titleInterval_x) / row;
            interval_y = (cy - titleInterval_y) / column;
        }

        public float getCx() {
            return cx;
        }

        public float getCy() {
            return cy;
        }

        public float getTitleInterval_x() {
            return titleInterval_x;
        }

        public float getTitleInterval_y() {
            return titleInterval_y;
        }

        public float getInterval_x() {
            return interval_x;
        }

        public float getInterval_y() {
            return interval_y;
        }
    }

    private void drawLines(Canvas canvas, float cx, float cy, float titleInterval_x, float titleInterval_y, float interval_x, float interval_y) {

        if (isShowDayBar) {
            canvas.drawLine(titleInterval_x, 0, titleInterval_x, cy, linePaint);
        }
        if (isShowSidebar) {
            canvas.drawLine(0, titleInterval_y, cx, titleInterval_y, linePaint);
        }

        if (scheduleRectF == null) {
            scheduleRectF = new RectF(titleInterval_x, titleInterval_y, cx, cy);
            standardHeight = interval_y;
            standardWidth = interval_x;
        }

        //画表格
        float weekTextSize = dpToPx(18);
        linePaint.setTextSize(weekTextSize);
        for (int i = 0; i < row; i++) {
            float interval = titleInterval_x + interval_x * i;
            canvas.drawLine(interval, 0, interval, cy, linePaint);
            float j = interval + (interval_x - (weekTextSize * 2)) / 2;
            if (isShowDayBar) {
                String data = (dates[i].getDate() == 1) ? String.valueOf(dates[i].getMonth() + 1) + "月" : String.valueOf(dates[i].getDate());
                String week = weeks[i] + "\n" + data;
                textPaint.setTextSize(weekTextSize);
                StaticLayout staticLayout = new StaticLayout(week, textPaint, (int) (weekTextSize * 2), Layout.Alignment.ALIGN_CENTER, 1, 0, false);
                canvas.translate(j, titleInterval_y / 2 - weekTextSize);
                staticLayout.draw(canvas);
                canvas.translate(-j, -(titleInterval_y / 2 - weekTextSize));
//                canvas.drawText(weeks[i], j, titleInterval_y - (titleInterval_y - weekTextSize) / 2, linePaint);
            }
        }
        float classesTextSize = dpToPx(13);
        linePaint.setTextSize(classesTextSize);
        for (int i = 0; i < column; i++) {
            float interval = titleInterval_y + interval_y * i;
            canvas.drawLine(0, interval, cx, interval, linePaint);
            float j = interval + interval_y / 2;
            if (isShowSidebar) {
                canvas.drawText(classes[i], (titleInterval_x - classesTextSize) / 2, j, linePaint);
            }
        }
    }

    private void drawClassBlocks(Canvas canvas) {
        Random random = new Random();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                Block c = blocks[i][j];
                int k = j;
                while (k < blocks[i].length - 1) {
                    if (!c.getClassName().equals(blocks[i][k + 1].getClassName())) {
                        break;
                    }
                    k++;
                }
                if (!blocks[i][k].getClassName().equals("null")) {
                    if (c.getBlockColor() != -1) {
                        fillPaint.setColor(c.getBlockColor());
                    } else {
                        int color = Color.parseColor(color_default[random.nextInt(color_default.length)]);
                        c.setBlockColor(color);
                        for (int l = j; l < k + 1; l++) {
                            blocks[i][l].setBlockColor(color);
                        }
                        fillPaint.setColor(color);
                    }
                    Block Block = new Block(c.left, c.top, c.getWidth(), blocks[i][k].bottom - c.top);
                    Block.setData(c.getClassName(), c.getClassroom(), c.getTeacher());

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(Block.getRectF(), 20, 20, fillPaint);
//                        canvas.drawRoundRect(c.getLeft(), c.getTop(), c.getRight(), blocks[i][k].getBottom(), 20, 20, fillPaint);
//                    }
                    drawText(canvas, Block);
                    j = k;
                }
            }
        }
    }

    private void drawText(Canvas canvas, Block Block) {
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

        float start_x = Block.getLeft() + dpToPx(1.5f);
        float start_y = Block.top + dpToPx(3);
//                (Block.getHeight() / 2) - (float) (Math.floor(columnText / 2) * textSize);

        StaticLayout staticLayout;
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


        canvas.translate(start_x, start_y);
        staticLayout.draw(canvas);
        canvas.translate(-start_x, -start_y);

//        for (int i = 0; i < text.length(); i++) {
//            for (int j = 0; j < lineText || i < text.length(); i++, j++) {
//                canvas.drawText(text.charAt(i), start,start+textSize , width, linePaint);
//            }
//        }

    }

    private void drawBlockEdge(Canvas canvas) {
        if (clickBlock != null) {
            canvas.drawRoundRect(clickBlock.getRectF(), 20, 20, edgePaint);
        }
    }

    private float dpToPx(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //重构这部分代码
        //感觉现在还可以。。。。。
        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();
        Block c = getClickBlock(x, y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDown(c, block_x, block_y);
                downSysTime = System.currentTimeMillis();
                if (c != null) {
                    if (clickTimes > 0) {
                        if (getClickBlock(lastX, lastY) == getClickBlock(x, y)) {
                            clickTimes++;
                        }
                    } else {
                        clickTimes++;
                    }
                    clickJudge(clickBlock, block_x, block_y);
                }
                lastX = x;
                lastY = y;
//                Log.d("TouchEvent", "Down");
                break;
            case MotionEvent.ACTION_MOVE:
                if (getClickBlock(lastX, lastY) != c) {
                    actionMove();
//                    Log.d("TouchEvent", "Move");
                    lastX = x;
                    lastY = y;
//                    return true;
                }
                lastX = x;
                lastY = y;
            case MotionEvent.ACTION_UP:
                actionUp();
//                Log.d("TouchEvent", "Up");
                break;
        }
        return true;
    }

    //匹配单击的是哪个Block
    private Block getClickBlock(float x, float y) {
        Block c;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                c = blocks[i][j];
                if (x > c.left && x < c.right && y > c.top && y < c.bottom) {
                    block_x = i;
                    block_y = j;
//                        Log.d("ItemOnClick", String.valueOf(i) + "   " + String.valueOf(j));
                    return c;
                }
            }
        }
        return null;
    }

    private BlockState getTotalBlock(Block c, int block_y) {
        int topBlock = block_y;
        int bottomBlock = block_y;
        if (!c.getClassName().equals("null")) {
            for (int i = block_y; i >= 0; i--) {
                if (!c.getClassName().equals(blocks[block_x][i].getClassName())) {
                    break;
                } else {
                    topBlock = i;
                    c = blocks[block_x][i];
                }
            }
            for (int i = block_y; i < blocks[block_x].length; i++) {
                if (!c.getClassName().equals(blocks[block_x][i].getClassName())) {
                    break;
                } else {
                    bottomBlock = i;
                }
            }
//            Log.d("ItemClick", String.valueOf(topBlock) + "   " + String.valueOf(bottomBlock));
        }
        float height = 0;
        if (topBlock != bottomBlock) {
            height = c.getHeight() * (bottomBlock - topBlock + 1);
        } else {
            height = blocks[block_x][block_y].getHeight();
        }
        Block b = new Block(c.left, c.top, c.getWidth(), height);
        b.setData(c.getClassName(), c.getClassroom(), c.getTeacher());
        b.setBlockColor(c.getBlockColor());
        BlockState bs = new BlockState(b, topBlock, bottomBlock);
        return bs;
    }

    private void actionDown(Block c, int block_x, int block_y) {
        //获取到单击的Block的具体大小
        if (block_x >= 0 && block_y >= 0 && c != null) {
            clickBlock = getTotalBlock(c, block_y).getBlock();
        }
        isBlockClick = true;
        invalidate();
        isMoved = false;
    }

    private void actionMove() {
        isMoved = true;
    }

    private void actionUp() {
//        clickJudge(clickBlock, block_x, block_y);
        lastUpSysTime = System.currentTimeMillis();
    }

    private void clickJudge(final Block c, final int block_x, final int block_y) {
        if (!isDoubleJudgeRunning) {
            isDoubleJudgeRunning = true;
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isMoved && block_x >= 0 && block_y >= 0 && c != null) {
                        if (lastUpSysTime == 0 || Math.abs(lastUpSysTime - downSysTime) > longClickJudgeClock) {
                            onItemLongClickListener.onLongClick(c, block_x, block_y);
//                            Log.w("TouchEvent", "longClick");
                        } else {
                            if (clickTimes > 1) {
                                if (onItemDoubleClickListener != null && c != null) {
                                    onItemDoubleClickListener.onDoubleClick(c, block_x, block_y);
//                                    Log.w("TouchEvent", "doubleClick");
                                }
                            } else {
                                if (onItemClickListener != null && c != null) {
                                    onItemClickListener.onClick(c, block_x, block_y);
//                                    Log.w("TouchEvent", "click");
                                }
                            }
                        }
                    }
                    isDoubleJudgeRunning = false;
                    clickTimes = 0;
                }
            }, longClickJudgeClock + 50);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    class Block {

        private String className = "null";
        private String classroom = "null";
        private String teacher = "null";

        private float left;
        private float right;
        private float top;
        private float bottom;
        private float interval = dpToPx(0.5f);

        private int color = -1;

        private boolean canDelete = false;

        public Block(float x, float y, float interval_x, float interval_y) {
            left = x;
            right = x + interval_x;
            top = y;
            bottom = y + interval_y;
        }

        public void setData(String className, String classroom, String teacher) {
            this.className = className;
            this.classroom = classroom;
            this.teacher = teacher;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public void setClassroom(String classroom) {
            this.classroom = classroom;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public void setBlockColor(int color) {
            this.color = color;
        }

        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }

        public void addData(String className, String classroom, String teacher) {
            this.className = (this.className.equals("null")) ? className : this.className + "，" + className;
            this.classroom = (this.classroom.equals("null")) ? classroom : this.classroom + "，" + classroom;
            this.teacher = (this.teacher.equals("null")) ? teacher : this.teacher + "，" + teacher;
        }

        public void addClassName(String className) {
            this.className = (this.className.equals("null")) ? className : this.className + "，" + className;
        }

        public void addClassroom(String classroom) {
            this.classroom = (this.classroom.equals("null")) ? classroom : this.classroom + "，" + classroom;
        }

        public void addTeacher(String teacher) {
            this.teacher = (this.teacher.equals("null")) ? teacher : this.teacher + "，" + teacher;
        }

        public String getClassName() {
            return className;
        }

        public String getClassroom() {
            return classroom;
        }

        public String getTeacher() {
            return teacher;
        }

        public int getBlockColor() {
            return color;
        }

        public boolean isCanDelete() {
            return canDelete;
        }

        public RectF getRectF() {
            RectF rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
            return rectF;
        }

        public float getLeft() {
            return left + interval;
        }

        public float getRight() {
            return right - interval;
        }

        public float getTop() {
            return top + interval;
        }

        public float getBottom() {
            return bottom - interval;
        }

        public float getWidth() {
            return right - left;
        }

        public float getHeight() {
            return bottom - top;
        }

        public float getInterval() {
            return interval;
        }
    }

    class BlockState {
        private int topBlock;
        private int bottomBlock;
        private Block block;

        public BlockState(Block block, int topBlock, int bottomBlock) {
            this.block = block;
            this.topBlock = topBlock;
            this.bottomBlock = bottomBlock;
        }

        public Block getBlock() {
            return block;
        }

        public int getTopBlock() {
            return topBlock;
        }

        public int getBottomBlock() {
            return bottomBlock;
        }
    }

    interface OnItemClickListener {
        void onClick(Block Block, int x, int y);
    }

    interface OnItemLongClickListener {
        void onLongClick(Block Block, int x, int y);
    }

    interface OnItemDoubleClickListener {
        void onDoubleClick(Block Block, int x, int y);
    }

    interface OnItemPressListener {
        void onPress(Block Block, int x, int y);
    }

}
