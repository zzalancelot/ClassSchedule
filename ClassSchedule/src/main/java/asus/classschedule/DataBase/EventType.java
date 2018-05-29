package asus.classschedule.DataBase;

/**
 * Created by ASUS on 2018/5/29.
 */
public enum EventType {

    Class("Class"),
    Matter("Matter"),
    Mix("Class And Matter");

    private String type;

    EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
