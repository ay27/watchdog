package bitman.ay27.watchdog.processor;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/31.
 */

/**
 * 拟合夹角链码时的中间表示
 */
class Segment {

    public final double segment_length;

    private Curve curve;
    /// y = ax+b, slope = a
    private double a = 0.0, b = 0.0;
    private double leftTime = 0.0, rightTime = 0.0;
    private boolean hasDone = false;

    public Segment(double segment_length) {
        this.segment_length = segment_length;
        curve = new Curve();
    }

    RhythmPoint firstPoint() {
        return curve.first();
    }

    public Curve getCurve() {
        return curve;
    }


    public boolean if_in_range(RhythmPoint point) {
        if (curve.size() < 2)
            return true;

        for (RhythmPoint p : curve.getPoints()) {
            double dis = Utils.get_distance(p, point);
            if (dis - segment_length >= Utils.PRECISION_THRESHOLD) {
                return false;
            }
        }

        return true;
    }

    /**
     * 最小二乘法计算直线的解析式
     * y = ax+b
     * a = sum((Xi-avgX)*(Yi-avgY)) / sum((Xi-avgX)^2)
     * b = avgY - a*avgX
     */
    public void fitting() {

        double average_x = 0.0, average_y = 0.0;
        for (RhythmPoint point : curve.getPoints()) {
            average_x += point.x;
            average_y += point.y;
        }
        average_x = average_x / (double) curve.size();
        average_y = average_y / (double) curve.size();

        // molecular : 分子
        // denominator : 分母
        double molecular = 0.0, denominator = 0.0;
        for (RhythmPoint point : curve.getPoints()) {
            molecular += (point.x - average_x) * (point.y - average_y);
            denominator += (point.x - average_x) * (point.x - average_x);
        }
        this.a = molecular / denominator;
        this.b = average_y - this.a * average_x;

        for (int i = 0; i < curve.size() / 2; i++) {
            this.leftTime += curve.get(i).timestamp;
        }
        this.leftTime = leftTime / ((double) curve.size() / 2.0);
        for (int i = curve.size() / 2; i < curve.size(); i++) {
            this.rightTime += curve.get(i).timestamp;
        }
        this.rightTime = rightTime / (double) (curve.size() - curve.size() / 2);

        hasDone = true;
    }

    public double getA() {
        if (!hasDone) {
            fitting();
        }
        return a;
    }

    public double getB() {
        if (!hasDone) {
            fitting();
        }
        return b;
    }

    public double getLeftTime() {
        if (!hasDone) {
            fitting();
        }
        return leftTime;
    }

    public double getRightTime() {
        if (!hasDone) {
            fitting();
        }
        return rightTime;
    }


    public void add(RhythmPoint point) {
        if (point == null)
            return;
        curve.add(point);
    }
}
