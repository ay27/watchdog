package bitman.ay27.watchdog.processor;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class AngleChainProcessor {

    private static final String TAG = "AngleChainProcessor";
    private static final double SEGMENT_LENGTH = 120.0;
    public static final double FIT_DOT_THRESHOLD = 16.0;
    public static final double FIT_DOT_TIME_THRESHOLD = 50.0;


    private ArrayList<Segment> patternCurves;
    private ArrayList<Curve> matchingCurves;
    private double deviation;

    public AngleChainProcessor(ArrayList<Segment> patternCurves, ArrayList<Curve> matchingCurves) {
        this.patternCurves = patternCurves;
        this.matchingCurves = matchingCurves;

        deviation = 0.0;
    }

//    public boolean juxtapose() {
//
//        if (patternCurves.size() != matchingCurves.size()) {
//            return false;
//        }
//
//        //=====  降噪  =====
//        for (Curve s : matchingCurves) {
//            Utils.de_noise(s, FIT_DOT_THRESHOLD, FIT_DOT_TIME_THRESHOLD);
//        }
//
//        //=====  分割  =====
//        for (int i = 0; i < matchingCurves.size(); i++) {
//            split_curve(matchingCurves.get(i), )
//        }
//    }
}
