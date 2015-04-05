package bitman.ay27.watchdog.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.processor.RhythmPoint;
import bitman.ay27.watchdog.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/2/10.
 */
public class DrawingCanvas extends View {
    private static final String TAG = "DrawingCanvas";
    private static final int DEFAULT_WIDTH = 8;
    private Paint paint;
    private Curve newestCurve;
    private ArrayList<Curve> curves;
    private long init_time = -1;

    public DrawingCanvas(Context context) {
        super(context);
        init();
    }

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        this.setBackground(getResources().getDrawable(R.drawable.image1));
        setPaint();
        curves = new ArrayList<Curve>();
        newestCurve = new Curve();
    }

    private void setPaint() {
        paint = new Paint();
        int width = (int) (DEFAULT_WIDTH * getResources().getDisplayMetrics().density);
        paint.setStrokeWidth(width);
        paint.setColor(getResources().getColor(R.color.color_rhythmdrawing_paint));
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (init_time == -1) {
            init_time = System.currentTimeMillis();
        }

        //=====  add the point to current curve
        long time = System.currentTimeMillis() - init_time;
        if (newestCurve == null) {
            newestCurve = new Curve();
        }
        newestCurve.add(new RhythmPoint(event.getX(), event.getY(), time));
        //=====

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                curves.add(newestCurve);
                newestCurve = new Curve();
                break;
        }

        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        _draw(canvas);
    }

    private void _draw(Canvas canvas) {

        if (curves != null && curves.size() > 0) {
            for (Curve curve : curves) {
                drawCurve(canvas, curve);
            }
        }


        if (newestCurve != null && newestCurve.size() > 0) {
            drawCurve(canvas, newestCurve);
        }
    }

    private void drawCurve(Canvas canvas, Curve curve) {
        if (curve == null || curve.size() == 0) {
            return;
        }

        Path path = new Path();
        path.reset();
        path.moveTo(curve.first().x, curve.first().y);

        List<RhythmPoint> points = curve.getPoints();

        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        canvas.drawPath(path, paint);
    }

}
