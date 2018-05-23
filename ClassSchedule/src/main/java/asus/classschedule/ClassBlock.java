package asus.classschedule;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class ClassBlock implements Serializable {

    private boolean canDelete = false;

    private String className;
    private String classroom;
    private String teacher;

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

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
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

    public boolean isCanDelete() {
        return canDelete;
    }

}
