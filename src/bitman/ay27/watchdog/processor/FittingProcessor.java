package bitman.ay27.watchdog.processor;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/4.
 */
class FittingProcessor {

    public static final double FIT_DOT_THRESHOLD = 16.0;
    public static final double FIT_DOT_TIME_THRESHOLD = 50.0;
    public static final int NUM_OF_SEGMENTS_FOR_EVERY_CURVE = 20;
    private static final String TAG = "FittingProcessor";
    private Curve curve_wait_for_fitting;
    private ArrayList<Segment> segments;
    private AngleChain chain;

    public FittingProcessor(Curve curve) {
        this.curve_wait_for_fitting = curve;
        segments = new ArrayList<Segment>();
    }

    public AngleChain fit() {
        Utils.de_noise(curve_wait_for_fitting, FIT_DOT_THRESHOLD, FIT_DOT_TIME_THRESHOLD);
        double sgm_length = Utils.split_curve(curve_wait_for_fitting, segments, NUM_OF_SEGMENTS_FOR_EVERY_CURVE);
        chain = new AngleChain(curve_wait_for_fitting.first(), sgm_length, NUM_OF_SEGMENTS_FOR_EVERY_CURVE);

        _fill_chain();

        return chain;
    }

    private void _fill_chain() {
        for (Segment segment : segments) {
            chain.add_sgm(segment.getA(), segment.getLeftTime());
        }
    }

}
