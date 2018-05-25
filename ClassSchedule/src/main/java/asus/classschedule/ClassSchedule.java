package asus.classschedule;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import tyrantgit.explosionfield.ExplosionField;

public class ClassSchedule extends FrameLayout {

    private Context context;

    private int textColor = Color.BLACK;

    /**
     * 动画变量
     */
    //动画持续时间
    private int showAnimSpeed = 25;
    private int hideAnimSpeed = 1000;

    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int marginBottom;

    private float changeMarginLeft;
    private float changeMarginRight;
    private float changeMarginTop;
    private float changeMarginBottom;

    private int targetMarginLeft;
    private int targetMarginRight;
    private int targetMarginTop;
    private int targetMarginBottom;

    //判断有几个blocks
    private int blocks;

    private float targetElevation = -1;
    private float changeElevation;
    private float elevation;

    //为阴影预留的空间
    private float shadowWidth = -1;

    /**
     * heightMode，true为向下绘制，false为向上绘制
     * widthMode,1,2,3代表着这个view在行数中处于[1][2][3]的位置；
     * 意思分别是向右绘制，向两边绘制，向左绘制
     */
    private boolean heightMode = true;
    private int widthMode = -1;

    /**
     * Schedule的行列
     */
    private int row = -1;
    private int column = -1;

    private boolean dialogIsShow = false;
    private boolean showDetailsDialog = true;
    private boolean hasExplosionField = false;

    private RelativeLayout.LayoutParams detailsDialogParams;
    private RelativeLayout.LayoutParams deleteDialogParams;

    private Handler marginHandler;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemDoubleClickListener onItemDoubleClickListener;

    private OnDialogClickListener onDialogClickListener;

    private Schedule schedule;
    private RelativeLayout relativeLayout;
    private DetailsDialog detailsDialog;
    private DeleteDialog deleteDialog;
    private ExplosionField explosionField;

    public ClassSchedule(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ClassSchedule(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClassSchedule(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.class_schedule, this);
//        classSchedule = (ClassSchedule) view.findViewById(R.id.ClassSchedule);
//        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        schedule = new Schedule(context);
        relativeLayout = new RelativeLayout(context);
        detailsDialogParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        deleteDialogParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        this.addView(schedule, params);
        this.addView(relativeLayout, params);
    }

    //因为粒子动画需要传入activity，所以在这里传入activity
    public void setActivity(Activity activity) {
        explosionField = ExplosionField.attach2Window(activity);
        hasExplosionField = true;
    }

    public void setRowsAndColumns(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void setDiaLogElevation(float targetElevation) {
        this.targetElevation = targetElevation;
    }

    public void setShadowWidth(float shadowWidth) {
        this.shadowWidth = shadowWidth;
    }

    public void setShowAnimSpeed(int showAnimSpeed) {
        this.showAnimSpeed = showAnimSpeed;
    }

    public void showDayBar(boolean isShowDayBar) {
        schedule.showDayBar(isShowDayBar);
    }

    public void showSideBar(boolean isShowSideBar) {
        schedule.showSideBar(isShowSideBar);
    }

    //设置显示课程名还是教室，true为显示课程名，false为显示教室
    public void showClassName(boolean showClassName) {
        schedule.setShowClassName(showClassName);
    }

    public void showDetailsDialog(boolean showDetailsDialog) {
        this.showDetailsDialog = showDetailsDialog;
    }

    /**
     * set方法：如果原Block有数据，将对原有数据进行覆盖
     * add方法：如果原Block有数据，将会把add的数据拼接在原数据后，以"，"连接
     */
    public void setTextColor(int textColor) {
        schedule.setTextColor(this.textColor);
        schedule.invalidate();
    }

    public void setClassBlock(ClassBlock[][] classBlock) {
        if (classBlock.length < row) {
            for (int i = 0; i < classBlock.length; i++) {
                if (classBlock[i].length < column) {
                    for (int j = 0; j < classBlock[i].length; j++) {
                        schedule.setBlock(classBlock[i][j], i, j);
                    }
                } else {
                    throw new ClassBlockOutOfBoundsException();
                }
            }
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void setClassBlock(ClassBlock[] classBlocks, int row) {
        if (row <= this.row && classBlocks.length <= column) {
            for (int i = 0; i < classBlocks.length; i++) {
                schedule.setBlock(classBlocks[i], row, i);
            }
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void setClassBlock(ClassBlock classBlock, int row, int column) {
        if (row <= this.row && column <= this.column) {
            schedule.setBlock(classBlock, row, column);
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void setClassBlock(ClassBlock classBlock, int row, int column, int length) {
        if (row <= this.row && column <= this.column) {
            for (int i = column; i < column + length; i++) {
                schedule.setBlock(classBlock, row, i);
            }
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void addClassBlock(ClassBlock classBlock, int row, int column) {
        if (row <= this.row && column <= this.column) {
            schedule.addBlock(classBlock, row, column);
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void addClassBlock(ClassBlock classBlock, int row, int column, int length) {
        if (row <= this.row && column <= this.column) {
            for (int i = column; i < column + length; i++) {
                schedule.addBlock(classBlock, row, i);
            }
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
    }

    public void deleteClassBlock(ClassBlock classBlock, int row, int column) {
        if (row <= this.row && column <= this.column) {
            if (!classBlock.getClassName().equals("null")) {
                Schedule.BlockState bs = schedule.deleteClassBlock(row, column);
                Schedule.Block block = bs.getBlock();
                if (deleteDialog == null) {
                    deleteDialog = new DeleteDialog(context);
                    deleteDialog.setTextColor(textColor);
                    deleteDialog.setBlock(block);
                    deleteDialogParams.setMargins((int) (block.getLeft()),
                            (int) (block.getTop()),
                            (int) (getWidth() - block.getRight()),
                            (int) (getHeight() - block.getBottom()));
                    relativeLayout.addView(deleteDialog, deleteDialogParams);
                    schedule.delete(bs, row);
                    final Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (explosionField != null && hasExplosionField) {
                                explosionField.explode(deleteDialog);
                            } else {
                                hideDialog(deleteDialog);
                            }
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    relativeLayout.removeView(deleteDialog);
//                                    deleteDialog.gc();
                                    deleteDialog = null;
                                    handler.removeCallbacks(this);
                                }
                            }, 200);
                            h.removeCallbacks(this);
                        }
                    }, 0);

                }
            }
        } else {
            throw new ClassBlockOutOfBoundsException();
        }
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

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (row == -1) {
            row = 7;
        } else if (row > 7) {
            schedule.showDayBar(false);
        }
        if (column == -1) {
            column = 10;
        } else if (column > 10) {
            schedule.showSideBar(false);
        }
        schedule.setRowAndColumn(row, column);
        setOnClickListener();
    }

    private void setOnClickListener() {
        schedule.setOnItemClickListener(new Schedule.OnItemClickListener() {
            @Override
            public void onClick(Schedule.Block block, int x, int y) {
                if (showDetailsDialog) {
                    blocks = (int) ((block.getBottom() - block.getTop()) / schedule.getStandardHeight()) + 1;
                    if (detailsDialog == null && !block.getClassName().equals("null")) {
                        if (block.getBlockColor() != -1) {
                            setDetailsDialog(block, x, y);
                        }
                    }
                    if (dialogIsShow) {
                        if (hasExplosionField) {
                            if (explosionField != null && detailsDialog != null && detailsDialog.getWidth() > 0 && detailsDialog.getHeight() > 0) {
                                explosionField.explode(detailsDialog);
                            } else {
                                explosionField = new ExplosionField(context);
                                if (detailsDialog != null && detailsDialog.getWidth() > 0 && detailsDialog.getHeight() > 0) {
                                    explosionField.explode(detailsDialog);
                                } else {
                                    if (detailsDialog == null) {
                                        Log.w("ClassSchedule", "detailsDialog is null");
                                    } else {
                                        Log.w("ClassSchedule", "The value of detailsDialog's width or height is 0");
                                    }
                                }
                            }
                        } else {
                            hideDialog(detailsDialog);
                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                relativeLayout.removeView(detailsDialog);
//                            deleteDialog.gc();
                                detailsDialog = null;
                                handler.removeCallbacks(this);
                            }
                        }, 500);
                        dialogIsShow = false;
                    }
                }
                if (onItemClickListener != null) {
                    ClassBlock c = new ClassBlock(block.getClassName(), block.getClassroom(), block.getTeacher(), block.isCanDelete());
                    onItemClickListener.onClick(c, x, y, blocks);
                }
            }
        });

        schedule.setOnItemLongClickListener(new Schedule.OnItemLongClickListener() {
            @Override
            public void onLongClick(Schedule.Block block, int x, int y) {
                blocks = (int) ((block.getBottom() - block.getTop()) / schedule.getStandardHeight()) + 1;
                if (onItemLongClickListener != null) {
                    ClassBlock c = new ClassBlock(block.getClassName(), block.getClassroom(), block.getTeacher(), block.isCanDelete());
                    onItemLongClickListener.onLongClick(c, x, y, blocks);
                }
            }
        });

        schedule.setOnItemDoubleClickListener(new Schedule.OnItemDoubleClickListener() {
            @Override
            public void onDoubleClick(Schedule.Block block, int x, int y) {
                blocks = (int) ((block.getBottom() - block.getTop()) / schedule.getStandardHeight()) + 1;
                if (onItemDoubleClickListener != null) {
                    ClassBlock c = new ClassBlock(block.getClassName(), block.getClassroom(), block.getTeacher(), block.isCanDelete());
                    onItemDoubleClickListener.onDoubleClick(c, x, y, blocks);
                }
            }
        });

    }

    private void setDetailsDialog(Schedule.Block c, int x, int y) {
        detailsDialog = new DetailsDialog(context);
        detailsDialog.setTextColor(textColor);

        if (shadowWidth < 0) {
            shadowWidth = dpToPx(5);
        }
        detailsDialog.setShadowWidth(shadowWidth);

        this.detailsDialogParams.setMargins((int) (c.getLeft()),
                (int) (c.getTop()),
                (int) (getWidth() - c.getRight()),
                (int) (getHeight() - c.getBottom()));

        getShowDialogMargins(c, x, y);
        showDialogMarginsAnim();

        relativeLayout.addView(detailsDialog, detailsDialogParams);
        if (detailsDialog != null) {
            detailsDialog.setClassBlock(c);
            setDialogOnClickListener();
        }
    }

    private void setDialogOnClickListener() {
        detailsDialog.setOnDialogClickListener(new DetailsDialog.OnDialogClickListener() {
            @Override
            public void onClick() {
                if (onDialogClickListener != null) {
                    onDialogClickListener.onClick();
                }
            }
        });
    }

    //TODO:计算DetailsDialog中文字的具体height，来判断是否需要再增加DetailsDialog的高度
    private void getShowDialogMargins(Schedule.Block c, int x, int y) {
//        this.c = c;
//        changeShadow = shadowWidth / showAnimSpeed / 3;
        marginLeft = (int) (c.getLeft() - shadowWidth);
        marginRight = (int) (getWidth() - c.getRight() - shadowWidth);
        marginTop = (int) (c.getTop() - shadowWidth);
        marginBottom = (int) (getHeight() - c.getBottom() - shadowWidth);
        elevation = 0;
        /**
         * 判断单击的X的位置需要怎样的变化
         */
        if (x == 0) {
            targetMarginLeft = marginLeft;
            changeMarginLeft = shadowWidth / showAnimSpeed;

            targetMarginRight = getWidth() - (int) (c.getRight() + schedule.getStandardWidth() * 2 + shadowWidth);
            changeMarginRight = (marginRight - targetMarginRight) / showAnimSpeed;

            widthMode = 1;
        } else if (x == row - 1) {
            targetMarginLeft = (int) (c.getLeft() - schedule.getStandardWidth() * 2 - shadowWidth);
            changeMarginLeft = (marginLeft - targetMarginLeft) / showAnimSpeed;

            targetMarginRight = marginRight;
            changeMarginRight = shadowWidth / showAnimSpeed;

            widthMode = 3;
        } else {
            targetMarginLeft = (int) (c.getLeft() - schedule.getStandardWidth() - shadowWidth);
            changeMarginLeft = (marginLeft - targetMarginLeft) / showAnimSpeed;

            targetMarginRight = (int) (getWidth() - c.getRight() - schedule.getStandardWidth() - shadowWidth);
            changeMarginRight = (marginRight - targetMarginRight) / showAnimSpeed;
//            changeMarginWidth *= 2;
            widthMode = 2;
        }
        /**
         * 判断单击的Y的位置需要怎样变化
         */
        if (y == column - 1) {
            switch (blocks) {
                case 1:
                    targetMarginTop = marginTop - (int) (schedule.getStandardHeight());
                    changeMarginTop = schedule.getStandardHeight() / showAnimSpeed * 2;
                    break;
                case 2:
                    targetMarginTop = marginTop;
                    changeMarginTop = 0;
                    break;
                default:
                    targetMarginTop = getHeight() - (int) (schedule.getStandardHeight() * 2 + shadowWidth);
                    changeMarginTop = (targetMarginTop - marginTop) / showAnimSpeed * 1.5f;
                    break;
            }
            targetMarginBottom = marginBottom;
            changeMarginBottom = shadowWidth / showAnimSpeed;
            heightMode = false;
        } else {
            targetMarginTop = marginTop;
            changeMarginTop = shadowWidth / showAnimSpeed;
            switch (blocks) {
                case 1:
                    targetMarginBottom = marginBottom - (int) (schedule.getStandardHeight());
                    changeMarginBottom = (int) ((marginBottom - targetMarginBottom) / showAnimSpeed * 1.5);
                    break;
                case 2:
                    targetMarginBottom = marginBottom;
                    changeMarginBottom = 0;
                    break;
                default:
                    targetMarginBottom = marginBottom + (int) (schedule.getStandardHeight() * (blocks - 2));
                    changeMarginBottom = (targetMarginBottom - marginBottom) / showAnimSpeed * 1.5f;
                    break;
            }
            heightMode = true;
        }
        /**
         * 阴影的动画计算
         */
        if (targetElevation < 0) {
            targetElevation = 20;
        }
        changeElevation = targetElevation / showAnimSpeed;
    }

    private void hideDialog(final asus.classschedule.Dialog dialog) {
        ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
        anim.setDuration(hideAnimSpeed);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.w("value",String.valueOf(value));
                dialog.setAlpha(value);
            }
        });
        anim.start();
    }

    //启动动画
    private void showDialogMarginsAnim() {
        marginHandler = new Handler();
        if (detailsDialog != null) {
//            dialogIsShow = true;
            marginHandler.postDelayed(runnable, 10);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogIsShow = true;
//                marginHandler.removeCallbacks(runnable);
                handler.removeCallbacks(this);
            }
        }, showAnimSpeed);

    }

    private Runnable runnable = getRunnable();

    //因为太长了不好折叠所以写了这个方法...
    private Runnable getRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                boolean isWidthFinish = false;
                boolean isHeightFinish = false;

                if (elevation < targetElevation) {
                    elevation += changeElevation;
                } else {
                    elevation = targetElevation;
                }
                detailsDialog.setElevation(elevation);
                //根据不同的状态来重新设置margin以达到动画的目的
                switch (widthMode) {
                    case 1:
                        if (marginRight > targetMarginRight) {
                            marginRight -= changeMarginRight;
                        } else {
                            marginRight = targetMarginRight;
                            isWidthFinish = true;
                        }
                        if (marginLeft > targetMarginLeft) {
                            marginLeft -= changeMarginLeft;
                        } else {
                            marginLeft = targetMarginLeft;
                        }
                        break;
                    case 3:
                        if (marginLeft > targetMarginLeft) {
                            marginLeft -= changeMarginLeft;
                        } else {
                            marginLeft = targetMarginLeft;
                            isWidthFinish = true;
                        }
                        if (marginRight > targetMarginRight) {
                            marginRight -= changeMarginRight;
                        } else {
                            marginRight = targetMarginRight;
                        }
                        break;
                    case 2:
                    default:
                        if (marginLeft > targetMarginLeft && marginRight > targetMarginRight) {
                            marginLeft -= changeMarginLeft;
                            marginRight -= changeMarginRight;
                        } else {
                            marginLeft = targetMarginLeft;
                            marginRight = targetMarginRight;
                            isWidthFinish = true;
                        }
                }
                if (heightMode) {
                    if (marginBottom > targetMarginBottom && blocks < 2) {
                        marginBottom -= changeMarginBottom;
                    } else if (marginBottom < targetMarginBottom && blocks > 2) {
                        marginBottom += changeMarginBottom;
                    } else {
                        marginBottom = targetMarginBottom;
                        isHeightFinish = true;
                    }
                    if (marginTop > targetMarginTop) {
                        marginTop -= changeMarginTop;
                    } else {
                        marginTop = targetMarginTop;
                    }
                } else {
                    if (marginTop > targetMarginTop && blocks < 2) {
                        marginTop -= changeMarginTop;
                    } else if (marginTop < targetMarginTop && blocks > 2) {
                        marginTop += changeMarginTop;
                    } else {
                        marginTop = targetMarginTop;
                        isHeightFinish = true;
                    }
                    if (marginBottom > targetMarginBottom) {
                        marginBottom -= changeMarginBottom;
                    } else {
                        marginBottom = targetMarginBottom;
                    }
                }
                if (detailsDialogParams != null && detailsDialog != null) {
                    detailsDialogParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                    detailsDialog.setLayoutParams(detailsDialogParams);
                }
                if (!isWidthFinish || !isHeightFinish) {
                    marginHandler.postDelayed(this, 10);
                }
            }
        };
        return runnable;
    }

    private float dpToPx(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public interface OnItemClickListener {
        void onClick(ClassBlock classBlock, int x, int y, int length);
    }

    public interface OnItemDoubleClickListener {
        void onDoubleClick(ClassBlock classBlock, int x, int y, int length);
    }

    public interface OnItemLongClickListener {
        void onLongClick(ClassBlock classBlock, int x, int y, int length);
    }

    public interface OnDialogClickListener {
        void onClick();
    }

    private class ClassBlockOutOfBoundsException extends ArrayIndexOutOfBoundsException {

    }

}