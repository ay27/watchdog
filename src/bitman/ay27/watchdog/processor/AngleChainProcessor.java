package bitman.ay27.watchdog.processor;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class AngleChainProcessor {

    private static final String TAG = "AngleChainProcessor";
    private static final double SEGMENT_LENGTH = 120.0;

    private ArrayList<Segment> patternCurves;
    private ArrayList<Curve> matchingCurves;
    private double deviation;

    public AngleChainProcessor(ArrayList<Segment> patternCurves, ArrayList<Curve> matchingCurves) {
        this.patternCurves = patternCurves;
        this.matchingCurves = matchingCurves;

        deviation = 0.0;
    }

}
