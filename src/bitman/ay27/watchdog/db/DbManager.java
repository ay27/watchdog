package bitman.ay27.watchdog.db;

import android.content.Context;
import bitman.ay27.watchdog.WatchdogApplication;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class DbManager {

    private Context context = WatchdogApplication.getContext();
    private static DbManager _instance;

    private DatabaseHelper helper;

    private DbManager() {
        helper = new DatabaseHelper(context);
    }

    public static DbManager getInstance() {
        if (_instance == null) {
            _instance = new DbManager();
        }
        return _instance;
    }

    public List query(Class cls) {
        RuntimeExceptionDao dao = helper.getRuntimeExceptionDao(cls);
        return dao.queryForAll();
    }

    public List query(Class cls, String columnName, boolean ascending) throws SQLException {
        RuntimeExceptionDao dao = helper.getRuntimeExceptionDao(cls);
        return dao.queryBuilder().orderBy(columnName, ascending).query();
    }

    public int insert(Class cls, Object obj) {
        RuntimeExceptionDao dao = helper.getRuntimeExceptionDao(cls);
        return dao.create(obj);
    }

    public int update(Class cls, Object obj) {
        RuntimeExceptionDao dao = helper.getRuntimeExceptionDao(cls);
        return dao.update(obj);
    }

    public int delete(Class cls, Object obj) {
        RuntimeExceptionDao dao = helper.getRuntimeExceptionDao(cls);
        return dao.delete(obj);
    }
}
