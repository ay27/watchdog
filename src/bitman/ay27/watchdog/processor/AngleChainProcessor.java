package bitman.ay27.watchdog.processor;

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

    public AngleChainProcessor(ArrayList<AngleChain> patternCurves, ArrayList<Curve> matchingCurves) {
        this.patternChains = patternCurves;
        this.matchingCurves = matchingCurves;
    }

    public boolean compare() {
        return do_compare(fit_all());
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

    private List<AngleChain> fit_all() {
        ArrayList<AngleChain> chains = new ArrayList<AngleChain>();
        for (Curve curve : matchingCurves) {
            FittingProcessor processor = new FittingProcessor(curve);
            chains.add(processor.fit());
        }

        return chains;
    }
}
