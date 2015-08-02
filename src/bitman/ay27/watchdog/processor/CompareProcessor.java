package bitman.ay27.watchdog.processor;

import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.db.model.AngleChain;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */
class CompareProcessor {

    public final double ANGLE_CHAIN_THRESHOLD1 = Math.tan(Math.PI / (9.0 + PrefUtils.getImgPasswdThreshold() * 10.0 / 100.0));
    public final double ANGLE_CHAIN_THRESHOLD2 = Math.tan(Math.PI / (6.0 + PrefUtils.getImgPasswdThreshold() * 10.0 / 100.0));
    public final double CHAIN_MATCHING_TOLERANCE = 11.0 - PrefUtils.getImgPasswdThreshold() * 6.0 / 100.0;
    private final String TAG = "CompareProcessor";
    /**
     * 起点位置误差100px
     */
    private final double START_POSITION_THRESHOLD = 100.0 - PrefUtils.getImgPasswdThreshold() * 60.0 / 100.0;
    /**
     * 每个直线段的时间误差500ms
     */
    private final double TIME_THRESHOLD = 500.0 - PrefUtils.getImgPasswdThreshold() * 200.0 / 100.0;
    /**
     * 每个直线段长度误差20px
     */
    private final double SEGMENT_LENGTH_THRESHOLD = 20.0 - PrefUtils.getImgPasswdThreshold() * 10.0 / 100.0;
    /**
     * 每段直线的夹角误差+-60度，即+-Pi/3
     */
    private final double ANGLE_THRESHOLD = Math.tan(Math.PI / 3.0);
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
        boolean result = comp_point(chain1.start_point, chain2.start_point);
        Log.i(TAG, "comp start point : " + result);
        if (!result)
            return false;

        result = comp_point(chain1.end_point, chain2.end_point);
        Log.i(TAG, "comp end point : " + result);
        if (!result) {
            return false;
        }


        result = comp_segment_param(chain1.num_of_segments, chain1.segment_length, chain2.num_of_segments, chain2.segment_length);
        Log.i(TAG, "comp segment param : " + result);
        if (!result)
            return false;

        result = comp_angle();
        Log.i(TAG, "comp angle : " + result);

        return result;
    }

    /**
     * 以链码的顺序来推进比较
     *
     * @return
     */
    private boolean comp_angle() {

        double deviationTolerance = 0.0;

        ArrayList<Double> angles1 = chain1.angles, angles2 = chain2.angles;
        ArrayList<Double> tl1 = chain1.time_lines, tl2 = chain2.time_lines;

        if (angles1.size() != angles2.size()) {
            Log.i(TAG, "angle size error : " + angles1.size() + " " + angles2.size());
            return false;
        }

        for (int i = 0; i < angles1.size(); i++) {
            double angle_tmp = angles1.get(i) - angles2.get(i);
            if (angle_tmp - ANGLE_CHAIN_THRESHOLD1 <= Utils.PRECISION_THRESHOLD) {
                if (angle_tmp < Utils.PRECISION_THRESHOLD) {
                    deviationTolerance -= 1.0;
                } else deviationTolerance += 1.0;
            } else if (angle_tmp - ANGLE_CHAIN_THRESHOLD2 <= Utils.PRECISION_THRESHOLD) {
                if (angle_tmp < Utils.PRECISION_THRESHOLD) {
                    deviationTolerance -= 2.0;
                } else deviationTolerance += 2.0;
            } else {
                if (angle_tmp < Utils.PRECISION_THRESHOLD) {
                    deviationTolerance -= 3.0;
                } else deviationTolerance += 3.0;
            }
//            if (angle_tmp - ANGLE_CHAIN_THRESHOLD1 <= Utils.PRECISION_THRESHOLD) {
//
//                deviationTolerance = Math.max(0.0, deviationTolerance - 1.0);
//            } else if (angle_tmp - ANGLE_CHAIN_THRESHOLD2 <= Utils.PRECISION_THRESHOLD) {
//                deviationTolerance += 1.0;
//            } else {
//                deviationTolerance += 2.5;
//            }

            if (deviationTolerance - CHAIN_MATCHING_TOLERANCE >= Utils.PRECISION_THRESHOLD) {
                Log.i(TAG, "deviation tolerance : " + deviationTolerance);
                return false;
            }

            double time_tmp = Math.abs(tl1.get(i) - tl2.get(i));
            if (TIME_THRESHOLD - time_tmp < Utils.PRECISION_THRESHOLD) {
                Log.i(TAG, "time error : " + time_tmp);
                return false;
            }
        }
        return true;

//        double deviation = 0.0;
//
//        ArrayList<Double> angles1 = chain1.angles, angels2 = chain2.angles;
//        ArrayList<Double> tl1 = chain1.time_lines, tl2 = chain2.time_lines;
//
//        for (int i = 0; i < chain1.num_of_segments; i++) {
//
//            if (i == angles1.size() || i == angels2.size()) {
//                return false;
//            }
//
//            // 角度差距太大，拒绝
//            if (ANGLE_THRESHOLD - Math.abs(angles1.get(i) - angels2.get(i)) <= Utils.PRECISION_THRESHOLD) {
//                Log.i(TAG, "angle error : "+Math.abs(angles1.get(i) - angels2.get(i)));
//                return false;
//            }
//            // 累积偏差过大，拒绝
//            deviation += angles1.get(i) - angels2.get(i);
//            if (ANGLE_THRESHOLD*10 - Math.abs(deviation) <= Utils.PRECISION_THRESHOLD) {
//                Log.i(TAG, "deviation error : "+deviation);
//                return false;
//            }
//            // 时间差过大，拒绝
//            if (TIME_THRESHOLD - Math.abs(tl1.get(i) - tl2.get(i)) <= Utils.PRECISION_THRESHOLD) {
//                Log.i(TAG, "time error : "+Math.abs(tl1.get(i) - tl2.get(i)));
//                return false;
//            }
//        }
//
//        return true;
    }

    private boolean comp_segment_param(int num_of_segments1, double segment_length1, int num_of_segments2, double segment_length2) {
        Log.i(TAG, "num : " + num_of_segments1 + " " + num_of_segments2 + "   length : " + segment_length1 + " " + segment_length2);
        return ((Math.abs(segment_length1 - segment_length2) - SEGMENT_LENGTH_THRESHOLD <= Utils.PRECISION_THRESHOLD)
                &&
                (num_of_segments1 == num_of_segments2));
    }

    private boolean comp_point(RhythmPoint p1, RhythmPoint p2) {
        Log.i(TAG, "start point : " + p1.x + "," + p1.y + " " + p2.x + "," + p2.y);
        return (Utils.get_distance(p1, p2) - START_POSITION_THRESHOLD <= Utils.PRECISION_THRESHOLD)
                &&
                (Math.abs(p1.timestamp - p2.timestamp) - TIME_THRESHOLD <= Utils.PRECISION_THRESHOLD);
    }
}
