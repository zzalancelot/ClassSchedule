package asus.classschedule.Blocks;

import android.support.annotation.NonNull;

import java.util.Date;

import asus.classschedule.DataBase.WeekType;

/**
 * Created by ASUS on 2018/5/29.
 */
public class MatterBlock {

    private String matterName;
    private String matterDetail;
    private String matterPlace;

    private Date expiryTime;

    private WeekType weekType = WeekType.Once;

    public MatterBlock(@NonNull String matterName, Date expiryTime) {
        this.matterName = matterName;
        this.expiryTime = expiryTime;
    }

    public MatterBlock(@NonNull String matterName, String matterDetail, Date expiryTime) {
        this.matterName = matterName;
        this.matterDetail = matterDetail;
        this.expiryTime = expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public void setWeekType(WeekType weekType) {
        this.weekType = weekType;
    }

    public String getMatterName() {
        return matterName;
    }

    public String getMatterDetail() {
        return matterDetail;
    }

    public String getMatterPlace() {
        return matterPlace;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public WeekType getWeekType() {
        return weekType;
    }
}
