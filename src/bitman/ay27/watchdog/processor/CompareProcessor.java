package bitman.ay27.watchdog.processor;

import bitman.ay27.watchdog.db.model.AngleChain;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */
class CompareProcessor {

    private static final String TAG = "CompareProcessor";
    /**
     * 起点位置误差16px
     */
    private static final double START_POSITION_THRESHOLD = 16.0;

    /**
     * 每个直线段的时间误差500ms
     */
    private static final double TIME_THRESHOLD = 500.0;

    /**
     * 每个直线段长度误差20px
     */
    private static final double SEGMENT_LENGTH_THRESHOLD = 20.0;

    /**
     * 每段直线的夹角误差+-30度，即+-Pi/6
     */
    private static final double ANGLE_THRESHOLD = Math.tan(Math.PI / 6.0);

    /**
     * 待比较的两个曲线
     */
    private AngleChain chain1, chain2;

    public CompareProcessor(AngleChain chain1, AngleChain chain2) {
        this.chain1 = chain1;
        this.chain2 = chain2;
    }

    /**
     * 需要对比的内容有：
     * 1. 起始点位置和时间
     * 2. 直线段的长度和个数
     * 3. 链码
     *
     * @return
     */
    public boolean comp() {
        boolean result = comp_start_point(chain1.start_point, chain2.start_point);
        if (!result)
            return false;

        result = comp_segment_param(chain1.num_of_segments, chain1.num_of_segments, chain2.num_of_segments, chain2.num_of_segments);
        if (!result)
            return false;

        result = comp_angle();

        return result;
    }

    /**
     * 以链码的顺序来推进比较
     *
     * @return
     */
    private boolean comp_angle() {
        double deviation = 0.0;

        ArrayList<Double> angles1 = chain1.angles, angels2 = chain2.angles;
        ArrayList<Double> tl1 = chain1.time_lines, tl2 = chain2.time_lines;

        for (int i = 0; i < chain1.num_of_segments; i++) {
            // 角度差距太大，拒绝
            if (ANGLE_THRESHOLD - Math.abs(angles1.get(i) - angels2.get(i)) <= Utils.PRECISION_THRESHOLD) {
                return false;
            }
            // 累积偏差过大，拒绝
            deviation += angles1.get(i) - angels2.get(i);
            if (ANGLE_THRESHOLD - Math.abs(deviation) <= Utils.PRECISION_THRESHOLD) {
                return false;
            }
            // 时间差过大，拒绝
            if (TIME_THRESHOLD - Math.abs(tl1.get(i) - tl2.get(i)) <= Utils.PRECISION_THRESHOLD) {
                return false;
            }
        }

        return true;
    }

    private boolean comp_segment_param(int num_of_segments1, double segment_length1, int num_of_segments2, double segment_length2) {
        return ((SEGMENT_LENGTH_THRESHOLD - Math.abs(num_of_segments1 - num_of_segments2) <= Utils.PRECISION_THRESHOLD)
                &&
                (segment_length1 == segment_length2));
    }

    private boolean comp_start_point(RhythmPoint p1, RhythmPoint p2) {
        return (START_POSITION_THRESHOLD - Utils.get_distance(p1, p2) <= Utils.PRECISION_THRESHOLD)
                &&
                (TIME_THRESHOLD - Math.abs(p1.timestamp - p2.timestamp) <= Utils.PRECISION_THRESHOLD);
    }
}
