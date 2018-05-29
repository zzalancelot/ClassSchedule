package asus.classschedule.DataBase;

/**
 * Created by ASUS on 2018/5/29.
 */
public enum WeekType {

    Once("Once"),
    OddWeeks("OddWeek"),
    Biweekly("Biweekly"),
    EveryWeek("EveryWeek");

    private String type;

    WeekType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
