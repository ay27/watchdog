package bitman.ay27.watchdog.processor;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class Utils {

    private static final double PRECISION_THRESHOLD = 0.000001;

    private Utils() {
    }

    public static double get_distance(RhythmPoint a, RhythmPoint b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public static void de_noise(Curve curve, final double FIT_DOT_THRESHOLD, final double FIT_TIME_THRESHOLD) {

        RhythmPoint lastOne = curve.first();

        for (int i = 1; i < curve.size(); i++) {
            if (get_distance(lastOne, curve.get(i)) - FIT_DOT_THRESHOLD <= PRECISION_THRESHOLD
                    && Math.abs(lastOne.timestamp - curve.get(i).timestamp) - FIT_TIME_THRESHOLD <= PRECISION_THRESHOLD) {
                RhythmPoint tmp = Utils.bind_dot(lastOne, curve.get(i));
                curve.rm(lastOne);
                curve.add(lastOne = tmp);
            } else {
                curve.add(lastOne = curve.get(i));
            }
        }
    }

    public static RhythmPoint bind_dot(RhythmPoint p1, RhythmPoint p2) {
        return new RhythmPoint(p1.x, p1.y, (p1.timestamp + p2.timestamp) / 2);
    }
}
