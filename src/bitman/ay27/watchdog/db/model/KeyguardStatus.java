package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/6.
 */

@DatabaseTable
public class KeyguardStatus implements Serializable {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<Long> patternAngelChainIds;

    @DatabaseField
    public String passwd;

    @DatabaseField(canBeNull = false)
    public PasswdType unlockType;

    public KeyguardStatus() {
        unlockType = PasswdType.image;
    }

    public KeyguardStatus(String passwd, ArrayList<Long> patternAngelChainIds, PasswdType type) {
        this.passwd = passwd;
        this.patternAngelChainIds = patternAngelChainIds;
        this.unlockType = type;
    }

    public static enum PasswdType {
        keyboard, image
    }
}
