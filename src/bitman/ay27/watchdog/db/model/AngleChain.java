package bitman.ay27.watchdog.db.model;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */

import bitman.ay27.watchdog.processor.RhythmPoint;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;

/**
 * 用于表征一条曲线的所有需要的信息。
 * 包括：
 * 1. 起点坐标
 * 2. 拟合时，直线段的长度值
 * 3. 直线段的个数
 * 4. 使用夹角链码拟合后，所有直线段的斜率
 * 5. 每个直线段的时间
 */

@DatabaseTable
public class AngleChain {
    private static final String TAG = "AngleChain";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public RhythmPoint start_point;

//    @DatabaseField(canBeNull = false)
//    private int x;
//    @DatabaseField(canBeNull = false)
//    private int y;
//    @DatabaseField(canBeNull = false)
//    private long timestamp;

    @DatabaseField(canBeNull = false)
    public double segment_length;

    @DatabaseField(canBeNull = false)
    public int num_of_segments;

    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    public ArrayList<Double> angles;

    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    public ArrayList<Double> time_lines;

    public AngleChain() {
    }

    public AngleChain(RhythmPoint start_point, double segment_length, int num_of_segments) {
        this.start_point = start_point;
//        this.x =start_point.x; this.y = start_point.y; this.timestamp = start_point.timestamp;

        this.segment_length = segment_length;
        this.num_of_segments = num_of_segments;

        angles = new ArrayList<Double>();
        time_lines = new ArrayList<Double>();
    }

//    public RhythmPoint getStart_point() {
//        if (start_point == null) {
//            start_point = new RhythmPoint(x, y, timestamp);
//        }
//        return start_point;
//    }
//
//    public void setStart_point(RhythmPoint start_point) {
//        this.start_point = start_point;
//        this.x = start_point.x;
//        this.y = start_point.y;
//        this.timestamp = start_point.timestamp;
//    }

    public void add_sgm(double angle, double time) {
        angles.add(angle);
        time_lines.add(time);
    }

    final double factor_a = 2.0/3.0, factor_b=1.0-factor_a;
    public void bind(AngleChain chain) {
        start_point.x = (int) _bind(start_point.x, chain.start_point.x);
        start_point.y = (int) _bind(start_point.y, chain.start_point.y);
        start_point.timestamp = (long) _bind(start_point.timestamp, chain.start_point.timestamp);
//        this.x = start_point.x; this.y = start_point.y; this.timestamp = start_point.timestamp;

        ArrayList<Double> angles2 = chain.angles, time_lines2 = chain.time_lines;
        for (int i = 0; i < angles.size(); i++) {
            angles.set(i, _bind(angles.get(i), angles2.get(i)));
            time_lines.set(i, _bind(time_lines.get(i), time_lines2.get(i)));
        }
    }

    private double _bind(double a, double b) {
        return a*factor_a+b*factor_b;
    }

}
