package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/6.
 */

@DatabaseTable
public class KeyguardStatus {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<Long> patternAngelChainIds;

    @DatabaseField
    public String numericPasswd;
    @DatabaseField
    public String charPasswd;

    @DatabaseField(canBeNull = false)
    public PasswdType unlockType;

    public static enum PasswdType {
        img, numeric, character
    }

    public KeyguardStatus() {
    }

    public KeyguardStatus(String charPasswd, String numericPasswd, ArrayList<Long> patternAngelChainIds, PasswdType type) {
        this.charPasswd = charPasswd;
        this.numericPasswd = numericPasswd;
        this.patternAngelChainIds = patternAngelChainIds;
        this.unlockType = type;
    }
}
