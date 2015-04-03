package bitman.ay27.watchdog.processor;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */
public class RhythmPoint {
    public int x, y;
    public long timestamp;

    public RhythmPoint(int x, int y, long timestamp) {
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
    }

    public RhythmPoint(float x, float y, long timestamp) {
        this.x = (int) x;
        this.y = (int) y;
        this.timestamp = timestamp;
    }
}