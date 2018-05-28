package asus.classschedule;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ASUS on 2018/5/28.
 */
class TimeJudge {

    public Date[] getWeekDay(){
        Calendar calendar = Calendar.getInstance();
        Date[] dates = new Date[7];

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
        }
        for (int i = 0; i < dates.length; i++) {
            dates[i] = calendar.getTime();
            calendar.add(Calendar.DATE,1);
        }
        return dates;
    }

}
