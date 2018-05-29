package asus.classschedule.DataBase;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ASUS on 2018/5/29.
 */
@Entity
public class ScheduleBlocksBean {

    @Id
    private long id;

    @NonNull
    @Property(nameInDb = "EVENT_TYPE")
    @Convert(converter = EventTypeConverter.class, columnType = String.class)
    private EventType eventType;

    @NonNull
    @Property(nameInDb = "EVENT_NAME")
    private String eventName;
    @Property(nameInDb = "EVENT_DETAIL")
    private String eventDetail;
    @Property(nameInDb = "EVENT_PLACE")
    private String eventPlace;
    @Property(nameInDb = "TEACHER")
    private String teacher;

    @NonNull
    @Property(nameInDb = "WEEK_TIMES")
    @Convert(converter = WeekTypeConverter.class, columnType = String.class)
    private WeekType weekTimes;

    @Property(nameInDb = "EXPIRY_TIME")
    private Date expiryTime;

    @NonNull
    @Property(nameInDb = "CAN_DELETE")
    private boolean canDelete;

    @Generated(hash = 1171658246)
    public ScheduleBlocksBean(long id, @NonNull EventType eventType,
            @NonNull String eventName, String eventDetail, String eventPlace, String teacher,
            @NonNull WeekType weekTimes, Date expiryTime, boolean canDelete) {
        this.id = id;
        this.eventType = eventType;
        this.eventName = eventName;
        this.eventDetail = eventDetail;
        this.eventPlace = eventPlace;
        this.teacher = teacher;
        this.weekTimes = weekTimes;
        this.expiryTime = expiryTime;
        this.canDelete = canDelete;
    }

    @Generated(hash = 61019499)
    public ScheduleBlocksBean() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDetail() {
        return this.eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventPlace() {
        return this.eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public WeekType getWeekTimes() {
        return this.weekTimes;
    }

    public void setWeekTimes(WeekType weekTimes) {
        this.weekTimes = weekTimes;
    }

    public Date getExpiryTime() {
        return this.expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean getCanDelete() {
        return this.canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }


    static class EventTypeConverter implements PropertyConverter<EventType, String> {
        @Override
        public EventType convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (EventType eventType : EventType.values()) {
                if (eventType.getType() == databaseValue) {
                    return eventType;
                }
            }
            return EventType.Class;
        }

        @Override
        public String convertToDatabaseValue(EventType entityProperty) {
            return (entityProperty == null ? null : entityProperty.getType());
        }
    }

    static class WeekTypeConverter implements PropertyConverter<WeekType, String> {

        @Override
        public WeekType convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (WeekType weekType : WeekType.values()) {
                if (weekType.getType() == databaseValue) {
                    return weekType;
                }
            }
            return WeekType.EveryWeek;
        }

        @Override
        public String convertToDatabaseValue(WeekType entityProperty) {
            return (entityProperty == null ? null : entityProperty.getType());
        }
    }
}
