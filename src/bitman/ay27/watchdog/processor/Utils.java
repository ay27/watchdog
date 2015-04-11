package bitman.ay27.watchdog.processor;

import bitman.ay27.watchdog.db.model.AngleChain;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
class Utils {

    public static final double PRECISION_THRESHOLD = 0.000001;
    private static final double DEFAULT_SEGMENT_LENGTH = 120.0;

    private Utils() {
    }

    public static double get_distance(RhythmPoint a, RhythmPoint b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public static Curve de_noise(Curve curve, final double FIT_DOT_THRESHOLD, final double FIT_TIME_THRESHOLD) {

        Curve result = new Curve();

        RhythmPoint lastOne = curve.first();
        result.add(lastOne);

        for (int i = 1; i < curve.size(); i++) {
            if ((get_distance(lastOne, curve.get(i)) - FIT_DOT_THRESHOLD <= PRECISION_THRESHOLD)
                    && (Math.abs(lastOne.timestamp - curve.get(i).timestamp) - FIT_TIME_THRESHOLD <= PRECISION_THRESHOLD)) {
                RhythmPoint tmp = Utils.bind_dot(lastOne, curve.get(i));
                result.rm(lastOne);
                result.add(lastOne = tmp);
            } else {
                result.add(lastOne = curve.get(i));
            }
        }

        return result;
    }

    public static RhythmPoint bind_dot(RhythmPoint p1, RhythmPoint p2) {
        return new RhythmPoint(p1.x, p1.y, (p1.timestamp + p2.timestamp) / 2);
    }

    /**
     * 将一条曲线切分成多段直线。由于无法事先知道直线段的长度，故使用二分搜索对线段长度进行搜索
     *
     * @param curve
     * @param num_of_segments
     * @return
     */
    public static AngleChain split_curve(Curve curve, final int num_of_segments) {
        double left = 1.0, right = 2 * DEFAULT_SEGMENT_LENGTH, mid = DEFAULT_SEGMENT_LENGTH;

        ArrayList<Segment> segments = null;

        while ((left <= right) && (segments = _split_one_curve(curve, mid)).size() != num_of_segments) {
            if (segments.size() > num_of_segments) {
                left = mid;
                mid = (right + mid) / 2 + 1;
            } else {
                right = mid;
                mid = (left + mid) / 2 - 1;
            }
        }

        if (segments == null) {
            return null;
        }

        AngleChain chain = new AngleChain(curve.first(), mid, num_of_segments);
        for (Segment segment : segments) {
            chain.add_sgm(segment.getA(), segment.getLeftTime());
        }

        return chain;
    }

    /**
     * 将一条曲线切分成多条指定长度的直线段。具体做法：根据点集进行推进，尝试把当前点加入到当前直线段，若加入后长度超过目标长度，则以当前点为起点，\
     * 另起一条直线段；否则加入当前直线段，继续推进。
     *
     * @param curve
     * @param segment_length
     * @return
     */
    private static ArrayList<Segment> _split_one_curve(Curve curve, double segment_length) {
        ArrayList<Segment> result = new ArrayList<Segment>();
        Segment sgm = null;
        for (RhythmPoint point : curve.getPoints()) {
            if (sgm == null) {
                sgm = new Segment(segment_length);
            }
            if (sgm.if_in_range(point)) {
                sgm.add(point);
            } else {
                // TODO : adjust the segment_length
                sgm.fitting();
                result.add(sgm);

                sgm = new Segment(segment_length);
                sgm.add(point);
            }
        }
        if (sgm != null) {
            if (sgm.getCurve().size() >= 2) {
                sgm.fitting();
                result.add(sgm);
            }
        }

        return result;
    }

}
