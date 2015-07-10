package bitman.ay27.watchdog.ui.activity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import bitman.ay27.watchdog.db.model.AngleChain;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.processor.RhythmPoint;
import bitman.ay27.watchdog.widget.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/2/10.
 */
public class DrawingCanvas extends View {
    private static final String TAG = "DrawingCanvas";
    private static final int DEFAULT_WIDTH = 8;
    private static final long CLEAN_CANVAS_TIME_DELAY = 3000;
    private Paint paint;
    private Curve newestCurve;
    private ArrayList<Curve> curves;
    private long init_time = -1;
    private Timer timer = new Timer();
    private DrawingCallback callback;
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (callback != null) {
                callback.onDrawPause();
            }
            cleanCanvas();
        }
    };

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
        setPaint();
        curves = new ArrayList<Curve>();
        newestCurve = new Curve();
        init_time = -1;
    }

    private void setPaint() {
        paint = new Paint();
        int width = (int) (DEFAULT_WIDTH * getResources().getDisplayMetrics().density);
        paint.setStrokeWidth(width);
        paint.setColor(getResources().getColor(R.color.color_rhythmdrawing_paint));
        paint.setStyle(Paint.Style.STROKE);
    }

    public void cleanCanvas() {
        init();
        this.invalidate();
    }

    private TimerTask generateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                uiHandler.obtainMessage(1).sendToTarget();
            }
        };
    }

    public void setOnDrawFinishedListener(DrawingCallback callback) {
        this.callback = callback;
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
                timer = new Timer();
                timer.schedule(generateTask(), CLEAN_CANVAS_TIME_DELAY);
                if (callback != null) {
                    callback.onActionUp(curves);
                }
//                init_time = -1;
                break;
            case MotionEvent.ACTION_DOWN:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (callback != null) {
                    callback.onActionDown();
                }
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

    public void drawChain(AngleChain chain) {
        cleanCanvas();

        Paint old = new Paint(paint);

        paint.setColor(Color.RED);

        curves = new ArrayList<Curve>();
        Curve curve = new Curve();
        curve.add(chain.start_point);
        double x1 = chain.start_point.x, x2, y2;
        for (int i = 0; i < chain.angles.size(); i++) {
            x2 = x1 + (chain.segment_length / (chain.angles.get(i) + 1.0));
            y2 = x2 * chain.angles.get(i);
            curve.add(new RhythmPoint(x2, y2, 0));

            x1 = x2;
        }

        curves.add(curve);

        invalidate();

//        this.paint = old;
    }


    public static interface DrawingCallback {
        public void onDrawPause();

        public void onActionDown();

        public void onActionUp(ArrayList<Curve> rawCurves);
    }

}
