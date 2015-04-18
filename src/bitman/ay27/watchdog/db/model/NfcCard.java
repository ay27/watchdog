package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/17.
 */
@DatabaseTable
public class NfcCard {
    @DatabaseField(generatedId = true)
    public long id;
    @DatabaseField
    public String name;
    @DatabaseField(unique = true)
    public String code;

    public NfcCard() {
    }

    public NfcCard(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
