package asus.classschedule.DataBase;

import android.content.Context;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import asus.classschedule.Blocks.ClassBlock;
import asus.classschedule.Blocks.MatterBlock;

/**
 * Created by ASUS on 2018/5/29.
 */
public class DataBase {

    volatile private static DataBase dataBase = null;

    private DaoSession daoSession;
    private ScheduleBlocksBeanDao beanDao;

    public static DataBase getInstance(Context context) {
        if (dataBase == null) {
            synchronized (DataBase.class) {
                if (dataBase == null) {
                    dataBase = new DataBase(context);
                }
            }
        }
        return dataBase;
    }

    private DataBase(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "ClassSchedule.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        beanDao = daoSession.getScheduleBlocksBeanDao();
    }

    public void insertClass(long id, ClassBlock classBlock) {
        ScheduleBlocksBean bean = new ScheduleBlocksBean();
        bean.setId(id);
        bean.setEventType(EventType.Class);
        bean.setEventName(classBlock.getClassName());
        bean.setEventPlace(classBlock.getClassroom());
        bean.setTeacher(classBlock.getTeacher());
        bean.setWeekTimes(classBlock.getWeekType());
        bean.setCanDelete(classBlock.isCanDelete());
        beanDao.insert(bean);
    }

    public void insertEvent(long id, MatterBlock matterBlock) {
        ScheduleBlocksBean bean = new ScheduleBlocksBean();
        bean.setId(id);
        bean.setEventType(EventType.Matter);
        bean.setEventName(matterBlock.getMatterName());
        bean.setEventPlace(matterBlock.getMatterPlace());
        bean.setEventDetail(matterBlock.getMatterDetail());
        bean.setWeekTimes(matterBlock.getWeekType());
        bean.setExpiryTime(matterBlock.getExpiryTime());
        bean.setCanDelete(true);
        beanDao.insert(bean);
    }

    public void delete(long id) {
        beanDao.deleteByKey(id);
    }

    public void select(long id){
        Query<ScheduleBlocksBean> query = beanDao.queryBuilder().where(ScheduleBlocksBeanDao.Properties.Id.eq(id)).build();
    }

}
