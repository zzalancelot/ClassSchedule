package asus.classschedule.Blocks;

import android.support.annotation.NonNull;

import java.io.Serializable;

import asus.classschedule.DataBase.WeekType;

public class ClassBlock implements Serializable {

    private boolean canDelete = false;

    private String className;
    private String classroom;
    private String teacher;

    private WeekType weekType = WeekType.EveryWeek;

    public ClassBlock(@NonNull String className, String classroom, String teacher) {
        this.className = className;
        this.classroom = classroom;
        this.teacher = teacher;
    }

    public ClassBlock(@NonNull String className, String classroom, String teacher, boolean canDelete) {
        this.className = className;
        this.classroom = classroom;
        this.teacher = teacher;
        this.canDelete = canDelete;
    }

    public ClassBlock(@NonNull String className, String classroom, String teacher, boolean canDelete,WeekType weekType) {
        this.className = className;
        this.classroom = classroom;
        this.teacher = teacher;
        this.canDelete = canDelete;
        this.weekType = weekType;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public void setWeekType(WeekType weekType){
        this.weekType = weekType;
    }

    public boolean isCanDelete() {
        return canDelete;
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

    public WeekType getWeekType() {
        return weekType;
    }
}
