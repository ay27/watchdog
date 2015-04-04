package bitman.ay27.watchdog.processor;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */

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
public class AngleChain {
    private static final String TAG = "AngleChain";

    private RhythmPoint start_point;
    private double segment_length;
    private int num_of_segments;
    private ArrayList<Double> angles;
    private ArrayList<Double> time_lines;

    public AngleChain(RhythmPoint start_point, double segment_length, int num_of_segments) {
        this.start_point = start_point;
        this.segment_length = segment_length;
        this.num_of_segments = num_of_segments;

        angles = new ArrayList<Double>();
        time_lines = new ArrayList<Double>();
    }

    public void add_sgm(double angle, double time) {
        angles.add(angle);
        time_lines.add(time);
    }

    @Override
    public String toString() {
        String str = "";

        str += "start_point:"+start_point.x+","+start_point.y+","+start_point.timestamp+"\n";
        str += "sgm_length:"+segment_length+"\n";
        str += "num_of_sgm:"+num_of_segments+"\n";
        for (int i = 0; i < num_of_segments; i++) {
            str += ""+angles.get(i)+":"+time_lines.get(i)+"\n";
        }

        return str;
    }

}
