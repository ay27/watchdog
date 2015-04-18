package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/17.
 */

@DatabaseTable
public class WatchCat {

    @DatabaseField(generatedId = true)
    public long id;
    @DatabaseField
    public boolean autoLoadFSP;
    @DatabaseField
    public boolean fLock;
    @DatabaseField
    public boolean autoLoadBCPT;
    @DatabaseField
    public String BCPT_Cipher;

    public WatchCat() {
    }
}
