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
        for (Curve curve : matchingCurves) {
            FittingProcessor processor = new FittingProcessor(curve);
            matchingChains.add(processor.fit());
        }

        return matchingChains;
    }

    public void updatePattern() {
        for (int i = 0; i < matchingChains.size(); i++) {
            patternChains.get(i).bind(matchingChains.get(i));
        }
    }
}
