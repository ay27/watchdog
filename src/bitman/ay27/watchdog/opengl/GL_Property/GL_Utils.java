package bitman.ay27.watchdog.opengl.GL_Property;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Spartan on 2015/5/2.
 */
public class GL_Utils {
    private static Materials instanceDarkBlueMatMaterial = null;
    private static LightColor instancePureWhiteLight = null;
    private static LightPos instanceLight0Pos = null;
    public final static float RAD2DEG = (float) (180.0 / Math.PI);
    public final static float DEG2RAD = (float) (Math.PI / 180.0);

    public static void SetMaterials(GL10 gl10, Materials materials) {
        gl10.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, materials.ambient, 0);
        gl10.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, materials.diffuse, 0);
        gl10.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, materials.specular, 0);
        gl10.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, materials.emission, 0);
        gl10.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, materials.shininess);
    }

    public static void SetSpotLightDirection(GL10 gl10, int light, final LightPos pos) {
        float dirVec[] = new float[3];
        for (int i = 0; i < 3; ++i)
            dirVec[i] = pos.direct[i] - pos.source[i];
        gl10.glLightfv(light, GL10.GL_SPOT_DIRECTION, dirVec, 0);
        gl10.glLightf(light, GL10.GL_SPOT_CUTOFF, pos.cutoff);
    }

    public static void SetLightParam(GL10 gl10, int light, final LightColor param) {
        gl10.glLightfv(light, GL10.GL_AMBIENT, param.ambient, 0);
        gl10.glLightfv(light, GL10.GL_DIFFUSE, param.diffuse, 0);
        gl10.glLightfv(light, GL10.GL_SPECULAR, param.specular, 0);
    }

    public static void CalcNorm3fv(final float[] vec1, final float[] vec2, float[] result) {
        result[0] = vec1[1] * vec2[2] - vec1[2] * vec2[1];
        result[1] = vec1[2] * vec2[0] - vec1[0] * vec2[2];
        result[2] = vec1[0] * vec2[1] - vec1[1] * vec2[0];
    }

    public static void NormalizeVector3fv(final float[] src, float[] dst) {
        float lenDirVec = (float) Math.sqrt(src[0] * src[0] + src[1] * src[1] + src[2] * src[2]);
        dst[0] = (src[0] / lenDirVec);
        dst[1] = (src[1] / lenDirVec);
        dst[2] = (src[2] / lenDirVec);
    }

    public static float CalcAngle3fv(final float[] vec1, final float[] vec2) {
        float lenVec1 = (float) Math.sqrt(vec1[0] * vec1[0] + vec1[1] * vec1[1] + vec1[2] * vec1[2]);
        float lenVec2 = (float) Math.sqrt(vec2[0] * vec2[0] + vec2[1] * vec2[1] + vec2[2] * vec2[2]);
        float scalar = vec1[0] * vec2[0] + vec1[1] * vec2[1] + vec1[2] * vec2[2];
        return (float) Math.acos(scalar / (lenVec1 * lenVec2));
    }


    private static FloatBuffer vectorMainLineBuffer;
    private static FloatBuffer vectorArrow1Buffer;
    private static FloatBuffer vectorArrow2Buffer;
    private static final float TRI_MARK_W_HALF = 0.2f;
    private static final float TRI_MARK_H = 0.5f;

    static {
        ByteBuffer mbb;
        mbb = ByteBuffer.allocateDirect(3 * 2 * 4);
        mbb.order(ByteOrder.nativeOrder());
        vectorMainLineBuffer = mbb.asFloatBuffer();

        mbb = ByteBuffer.allocateDirect(3 * 3 * 4);
        mbb.order(ByteOrder.nativeOrder());
        vectorArrow1Buffer = mbb.asFloatBuffer();

        mbb = ByteBuffer.allocateDirect(3 * 3 * 4);
        mbb.order(ByteOrder.nativeOrder());
        vectorArrow2Buffer = mbb.asFloatBuffer();
    }


    // need gl10.glDisable(GL10.GL_LIGHTING) and gl10.glDisableClientState(GL10.GL_COLOR_ARRAY);
    public static void DrawVector(GL10 gl10, final float[] direction, float length, final float[] color) {
        if (length == 0) {
            length = (float) Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1] + direction[2] * direction[2]);
        }
        float vecOrigin[] = new float[]{0.0f, length, 0.0f};
        float vecAxis[] = new float[3];
        float theta = (float) Math.acos((direction[1] * length) / (length * Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1] + direction[2] * direction[2])));
        CalcNorm3fv(vecOrigin, direction, vecAxis);
        //NormalizeVector(vecAxis, vecAxis);

        gl10.glColor4f(color[0], color[1], color[2], color[3]);

        gl10.glPushMatrix();
        gl10.glRotatef(theta * RAD2DEG, vecAxis[0], vecAxis[1], vecAxis[2]);

        vectorMainLineBuffer.clear();
        vectorMainLineBuffer.put(new float[]{0f, 0f, 0f});
        vectorMainLineBuffer.put(vecOrigin);
        vectorMainLineBuffer.position(0);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, vectorMainLineBuffer);
        gl10.glDrawArrays(GL10.GL_LINES, 0, 2);

        gl10.glLineWidth(1.0f);
        vectorArrow1Buffer.clear();
        vectorArrow1Buffer.put(new float[]{-TRI_MARK_W_HALF, length, 0.0f});
        vectorArrow1Buffer.put(new float[]{TRI_MARK_W_HALF, length, 0.0f});
        vectorArrow1Buffer.put(new float[]{0.0f, length + TRI_MARK_H, 0.0f});
        vectorArrow1Buffer.position(0);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, vectorArrow1Buffer);
        gl10.glDrawArrays(GL10.GL_LINE_LOOP, 0, 3);

        vectorArrow2Buffer.clear();
        vectorArrow2Buffer.put(new float[]{0.0f, length, -TRI_MARK_W_HALF});
        vectorArrow2Buffer.put(new float[]{0.0f, length, TRI_MARK_W_HALF});
        vectorArrow2Buffer.put(new float[]{0.0f, length + TRI_MARK_H, 0.0f});
        vectorArrow2Buffer.position(0);
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, vectorArrow2Buffer);
        gl10.glDrawArrays(GL10.GL_LINE_LOOP, 0, 3);

        gl10.glPopMatrix();
    }

    public static synchronized LightPos getLight1Pos() {
        if (instanceLight0Pos == null) {
            instanceLight0Pos = new LightPos();
            instanceLight0Pos.source = new float[]{-10f, 10f, 10f, 1};
            instanceLight0Pos.direct = new float[]{0, 0, 0};
            instanceLight0Pos.cutoff = 180;
        }
        return instanceLight0Pos;
    }

    public static synchronized LightColor getPureWhiteLight() {
        if (instancePureWhiteLight == null) {
            instancePureWhiteLight = new LightColor();
            instancePureWhiteLight.ambient = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
            instancePureWhiteLight.diffuse = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
            instancePureWhiteLight.specular = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        }
        return instancePureWhiteLight;
    }

    public static synchronized Materials getDarkBlueMatMaterial() {
        if (instanceDarkBlueMatMaterial == null) {
            instanceDarkBlueMatMaterial = new Materials();
            instanceDarkBlueMatMaterial.ambient = new float[]{0.11f, 0.13f, 0.12f, 1.0f};
            instanceDarkBlueMatMaterial.diffuse = new float[]{0.0f, 0.25f, 0.8f, 1.0f};
            instanceDarkBlueMatMaterial.specular = new float[]{0.06f, 0.08f, 0.17f, 1.0f};
            instanceDarkBlueMatMaterial.emission = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
            instanceDarkBlueMatMaterial.shininess = 2;
        }
        return instanceDarkBlueMatMaterial;
    }
}
