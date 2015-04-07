package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/6.
 */

@DatabaseTable
public class ServiceStatus {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(canBeNull = false)
    public String servicePackageName;

    @DatabaseField(canBeNull = false)
    public String serviceClassName;

    @DatabaseField(canBeNull = false)
    public boolean autoOpen;

    public ServiceStatus() {
    }

    public ServiceStatus(String servicePackageName, String serviceClassName, boolean autoOpen) {
        this.servicePackageName = servicePackageName;
        this.serviceClassName = serviceClassName;
        this.autoOpen = autoOpen;
    }
}
