package bitman.ay27.watchdog.ui.activity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import bitman.ay27.watchdog.R;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-20.
 */
public class Semicircle extends View {

    private int sectorNum;
    private int r;
    private boolean[] enables;

    public Semicircle(Context context) {
        super(context);
        initial(null);
    }

    public Semicircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial(attrs);
    }

    public Semicircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial(attrs);
    }

    private void initial(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Semicircle);
        sectorNum = a.getInt(R.styleable.Semicircle_sector_num, 7);
        r = a.getDimensionPixelSize(R.styleable.Semicircle_r, 500);
        a.recycle();

        enablePaint = new Paint();
        enablePaint.setColor(getResources().getColor(R.color.green_1));
        enablePaint.setStyle(Paint.Style.FILL);
        enablePaint.setStrokeWidth(1);
        enablePaint.setAntiAlias(true);

        disablePaint = new Paint();
        disablePaint.setColor(Color.GRAY);
        disablePaint.setStyle(Paint.Style.FILL);
        disablePaint.setStrokeWidth(1);
        disablePaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.blue_1));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);


        enables = new boolean[sectorNum];

    }

    private Paint enablePaint, disablePaint, linePaint;

    public void setEnable(boolean[] values) {
        this.enables = values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF(getWidth()/2-r, getHeight()/2-r, getWidth()/2+r, getHeight()/2+r);

        float degree = (float) (180.0 / 7.0);
        for (int i = 0; i < 7; i++) {
            canvas.drawArc(rectF, 180+i*degree, degree, true, enables[i] ? enablePaint : disablePaint);
            canvas.drawArc(rectF, 180+i*degree, degree, true, linePaint);
        }
    }
}
