package bitman.ay27.watchdog.opengl;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import bitman.ay27.watchdog.opengl.GL_Property.BufferUtil;
import bitman.ay27.watchdog.opengl.GL_Property.GL_Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.lang.Override;import java.nio.FloatBuffer;

import static android.opengl.GLU.gluLookAt;

/**
 * Created by Spartan on 2015/5/2.
 */

public class AttitudeRender implements GLSurfaceView.Renderer {
    private final static float CULL_SIZE = 10.0f;
    private final static float VIEW_RADUIS = 5.0f;
    private float pit = 0;  //X
    private float yaw = 0;  //Y
    private float roll = 0; //Z

    private static final float BOX_CENTER_X = 0.0f;
    private static final float BOX_CENTER_Y = 0.0f;
    private static final float BOX_CENTER_Z = 0.0f;

    private static final float BOX_WIDTH = 4.0f;
    private static final float BOX_LONG = BOX_WIDTH * 0.618f;
    private static final float BOX_HEIGHT = 0.5f;


//    private float[] mBoxAroundVtxArray = {
//            BOX_CENTER_X-BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//            BOX_CENTER_X+BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//            BOX_CENTER_X-BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z-BOX_HEIGHT/2,
//            BOX_CENTER_X+BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z-BOX_HEIGHT/2,
//            BOX_CENTER_X-BOX_WIDTH/2, BOX_CENTER_Y-BOX_LONG/2, BOX_CENTER_Z-BOX_HEIGHT/2,
//            BOX_CENTER_X+BOX_WIDTH/2, BOX_CENTER_Y-BOX_LONG/2, BOX_CENTER_Z-BOX_HEIGHT/2,
//            BOX_CENTER_X-BOX_WIDTH/2, BOX_CENTER_Y-BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//            BOX_CENTER_X+BOX_WIDTH/2, BOX_CENTER_Y-BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//            BOX_CENTER_X-BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//            BOX_CENTER_X+BOX_WIDTH/2, BOX_CENTER_Y+BOX_LONG/2, BOX_CENTER_Z+BOX_HEIGHT/2,
//    };

    private float[] mBoxUpVtxArray = {
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2
    };
    private float[] mBoxDownVtxArray = {
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
    };
    private float[] mBoxFrontVtxArray = {
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
    };
    private float[] mBoxBackVtxArray = {
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
    };

    private float[] mBoxLeftVtxArray = {
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X - BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2
    };

    private float[] mBoxRightVtxArray = {
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z + BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y + BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
            BOX_CENTER_X + BOX_WIDTH / 2, BOX_CENTER_Y - BOX_LONG / 2, BOX_CENTER_Z - BOX_HEIGHT / 2,
    };

    private float[] mButtomFaceArray = {
            -10, -5, 10,
            10, -5, 10,
            -10, -5, -10,
            10, -5, -10
    };

    private FloatBuffer mBoxUpVtxBuffer;
    private FloatBuffer mBoxDownVtxBuffer;
    private FloatBuffer mBoxFrontVtxBuffer;
    private FloatBuffer mBoxBackVtxBuffer;
    private FloatBuffer mBoxLeftVtxBuffer;
    private FloatBuffer mBoxRightVtxBuffer;
    private FloatBuffer mButtomFaceBuffer;

    public void SetAttitudeDeg(float pit, float yaw, float roll) {
        this.pit = pit;
        this.yaw = yaw;
        this.roll = roll;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        gl10.glShadeModel(GL10.GL_SMOOTH);
        gl10.glLightModelx(GL10.GL_LIGHT_MODEL_AMBIENT, GL10.GL_TRUE);
        gl10.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        gl10.glClearDepthf(1.0f);
        gl10.glEnable(GL10.GL_DEPTH_TEST);
        gl10.glDepthFunc(GL10.GL_LEQUAL);
        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl10.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
        gl10.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);

        mBoxUpVtxBuffer = BufferUtil.floatToBuffer(mBoxUpVtxArray);
        mBoxDownVtxBuffer = BufferUtil.floatToBuffer(mBoxDownVtxArray);
        mBoxFrontVtxBuffer = BufferUtil.floatToBuffer(mBoxFrontVtxArray);
        mBoxBackVtxBuffer = BufferUtil.floatToBuffer(mBoxBackVtxArray);
        mBoxRightVtxBuffer = BufferUtil.floatToBuffer(mBoxRightVtxArray);
        mBoxLeftVtxBuffer = BufferUtil.floatToBuffer(mBoxLeftVtxArray);
        mButtomFaceBuffer = BufferUtil.floatToBuffer(mButtomFaceArray);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Redraw background color
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();
        /*gluLookAt(gl10,
                (float) (VIEW_RADUIS * Math.cos(pit) * Math.sin(yaw)),
                (float) (VIEW_RADUIS * Math.sin(pit)),
                (float) (VIEW_RADUIS * Math.cos(pit) * Math.cos(yaw)),
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);*/
        gl10.glEnable(GL10.GL_LIGHT1);
        GL_Utils.SetLightParam(gl10, GL10.GL_LIGHT1, GL_Utils.getPureWhiteLight());
        GL_Utils.SetSpotLightDirection(gl10, GL10.GL_LIGHT1, GL_Utils.getLight1Pos());
        gl10.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, GL_Utils.getLight1Pos().source, 0);

        gluLookAt(gl10,
                0.0f,
                2.5f,
                10.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);


        gl10.glColor4f(0.2f, 0.5f, 0.3f, 1.0f);
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mButtomFaceBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();
        gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        DrawBox(gl10);
        DrawCoordinate(gl10, 3);

        gl10.glFlush();
    }


    private void DrawBox(GL10 gl10) {
        gl10.glPushMatrix();
        gl10.glEnable(GL10.GL_LIGHTING);
        gl10.glEnable(GL10.GL_NORMALIZE);


        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //gl10.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl10.glRotatef(pit + 90, 1.0f, 0.0f, 0.0f);
        gl10.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        gl10.glRotatef(roll, 0.0f, 0.0f, 1.0f);

        GL_Utils.SetMaterials(gl10, GL_Utils.getDarkBlueMatMaterial());

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(0.0f, 1.0f, 0.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxUpVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(0.0f, -1.0f, 0.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxDownVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(0.0f, 0.0f, 1.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxFrontVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(0.0f, 0.0f, -1.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxBackVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(1.0f, 0.0f, 0.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxRightVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        //gl10.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl10.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, mBoxLeftVtxBuffer);
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl10.glFinish();

        gl10.glFlush();

        gl10.glDisable(GL10.GL_NORMALIZE);
        gl10.glDisable(GL10.GL_LIGHTING);
        gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //gl10.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl10.glPopMatrix();
    }

    private void DrawCoordinate(GL10 gl10, int length) {
        gl10.glPushMatrix();
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glLineWidth(2.0f);
        float vectorDir[] = new float[3];
        float vectorColor[] = new float[4];

        gl10.glRotatef(pit + 90, 1.0f, 0.0f, 0.0f);
        gl10.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        gl10.glRotatef(roll, 0.0f, 0.0f, 1.0f);

        //X Axis
        vectorDir[0] = 1f;
        vectorDir[1] = 0f;
        vectorDir[2] = 0f;
        vectorColor[0] = 1f;
        vectorColor[1] = 0f;
        vectorColor[2] = 0f;
        vectorColor[3] = 1f;
        GL_Utils.DrawVector(gl10, vectorDir, length, vectorColor);

        //Y Axis
        vectorDir[0] = 0f;
        vectorDir[1] = 1f;
        vectorDir[2] = 0f;
        vectorColor[0] = 0f;
        vectorColor[1] = 1f;
        vectorColor[2] = 0f;
        vectorColor[3] = 1f;
        GL_Utils.DrawVector(gl10, vectorDir, length, vectorColor);

        //Z Axis
        vectorDir[0] = 0f;
        vectorDir[1] = 0f;
        vectorDir[2] = -1f;
        vectorColor[0] = 0f;
        vectorColor[1] = 0f;
        vectorColor[2] = 1f;
        vectorColor[3] = 1f;
        GL_Utils.DrawVector(gl10, vectorDir, length, vectorColor);

        gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glPopMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        gl10.glViewport(0, 0, w, h);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        GLU.gluPerspective(gl10, 70f, (float) w / (float) h, 1f, 30f);
//        if (w <= h)
//            gl10.glOrthof(-CULL_SIZE * ((float) w / (float) h), CULL_SIZE * ((float) w / (float) h), -CULL_SIZE, CULL_SIZE, -CULL_SIZE, CULL_SIZE);
//        else
//            gl10.glOrthof(-CULL_SIZE, CULL_SIZE, -CULL_SIZE * ((float) h / (float) w), CULL_SIZE * ((float) h / (float) w), -CULL_SIZE, CULL_SIZE);
    }


}

