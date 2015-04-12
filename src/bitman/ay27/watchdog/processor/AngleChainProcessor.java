package bitman.ay27.watchdog.processor;

import bitman.ay27.watchdog.db.model.AngleChain;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class AngleChainProcessor {

    private static final String TAG = "AngleChainProcessor";
    private static final int DEFAULT_NUM_OF_SEGMENTS = 15;

    private ArrayList<AngleChain> patternChains;
    private ArrayList<Curve> matchingCurves;
    private ArrayList<AngleChain> matchingChains;

    public AngleChainProcessor(ArrayList<AngleChain> patternCurves, ArrayList<Curve> matchingCurves) {
        this.patternChains = patternCurves;
        this.matchingCurves = matchingCurves;
    }

    public AngleChainProcessor(ArrayList<Curve> matchingCurves) {
        this.matchingCurves = matchingCurves;
    }

    public ArrayList<AngleChain> getMatchingChains() {
        return matchingChains;
    }

    public boolean compare() {
        return do_compare(fit_matching_curves());
    }

    private boolean do_compare(List<AngleChain> chains) {
        if (patternChains.size() != chains.size()) {
            return false;
        }
        for (int i = 0; i < patternChains.size(); i++) {
            CompareProcessor processor = new CompareProcessor(patternChains.get(i), chains.get(i));
            if (!processor.comp()) {
                return false;
            }
        }
        return true;
    }

    public List<AngleChain> fit_matching_curves() {
        matchingChains = new ArrayList<AngleChain>();
        for (int i = 0; i < matchingCurves.size(); i++) {
            FittingProcessor processor = new FittingProcessor(matchingCurves.get(i));
            if (patternChains == null) {
                matchingChains.add(processor.fit(0));
//                if (matchingCurves.get(i).size() / 3 >= DEFAULT_NUM_OF_SEGMENTS) {
//                    matchingChains.add(processor.fit(DEFAULT_NUM_OF_SEGMENTS));
//                } else {
//                    matchingChains.add(processor.fit(matchingCurves.get(i).size() / 3));
//                }
            } else {
                if (patternChains.size() <= i) {
                    matchingChains.add(processor.fit(0));
                } else {
                    matchingChains.add(processor.fit(patternChains.get(i).num_of_segments));
                }
            }
        }

        return matchingChains;
    }

    public void updatePattern() {
        for (int i = 0; i < matchingChains.size(); i++) {
            patternChains.get(i).bind(matchingChains.get(i));
        }
    }
}
