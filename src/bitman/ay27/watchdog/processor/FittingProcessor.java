package bitman.ay27.watchdog.processor;

import bitman.ay27.watchdog.db.model.AngleChain;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */
class FittingProcessor {

    public static final double FIT_DOT_THRESHOLD = 16.0;
    public static final double FIT_DOT_TIME_THRESHOLD = 50.0;
    private static final String TAG = "FittingProcessor";
    private Curve curve_wait_for_fitting;

    public FittingProcessor(Curve curve) {
        this.curve_wait_for_fitting = curve;
    }

    public AngleChain fit(final int num_of_segments) {
        curve_wait_for_fitting = Utils.de_noise(curve_wait_for_fitting, FIT_DOT_THRESHOLD, FIT_DOT_TIME_THRESHOLD);
        if (num_of_segments == 0) {
            return Utils.split_curve(curve_wait_for_fitting, curve_wait_for_fitting.size() / 3 == 0 ? 1 : curve_wait_for_fitting.size() / 3);
        } else {
            return Utils.split_curve(curve_wait_for_fitting, num_of_segments);
        }
    }

}
