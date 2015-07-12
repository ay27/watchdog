package bitman.ay27.watchdog.processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/28.
 */
public class Curve {

    private ArrayList<RhythmPoint> points;

    public Curve() {
        points = new ArrayList<RhythmPoint>();
    }

    public List<RhythmPoint> getPoints() {
        return points;
    }

    public int size() {
        if (points == null)
            return 0;
        return points.size();
    }

    public void add(RhythmPoint point) {
        if (point == null) {
            return;
        }
        points.add(point);
    }

    public void rm(RhythmPoint point) {
        if (point == null) {
            return;
        }
        points.remove(point);
    }

    public RhythmPoint first() {
        if (points == null || points.size() == 0) {
            return null;
        }
        return points.get(0);
    }

    public RhythmPoint get(int index) {
        if (points == null || points.size() <= index)
            return null;
        return points.get(index);
    }


}
