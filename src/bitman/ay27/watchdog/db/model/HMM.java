package bitman.ay27.watchdog.db.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class HMM {

    public static final int STATE_SUM = 360;

    @DatabaseField(generatedId = true)
    long id;

    @DatabaseField
    int state_no;

    @DatabaseField
    double[][] A = new double[STATE_SUM][STATE_SUM];

    @DatabaseField
    double[][] B = new double[STATE_SUM][STATE_SUM];

    @DatabaseField
    double[] Pi = new double[STATE_SUM];

}
