package bitman.ay27.watchdog.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.lang.Exception;

/**
 * Created by Spartan on 2015/5/2.
 */
public class AttitudeView extends GLSurfaceView {
    AttitudeRender render = null;

    public AttitudeView(Context context) {
        super(context);
        init();
    }

    public AttitudeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        try {
            // Create an OpenGL ES 2.0 context
            //setEGLContextClientVersion(1);
            // Set the Renderer for drawing on the GLSurfaceView
            render = new AttitudeRender();
            setRenderer(render);
            // Render the view only when there is a change in the drawing
            // data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            // 注意上面语句的顺序，反了可能会出错
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetAttitudeDeg(float pit, float yaw, float roll) {
        render.SetAttitudeDeg(pit, yaw, roll);
        this.requestRender();
    }
}
