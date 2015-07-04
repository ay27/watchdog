package bitman.ay27.watchdog.opengl.GL_Property;

/**
 * Created by Spartan on 2015/5/2.
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtil {
    public static FloatBuffer floatToBuffer(float[] a){
        FloatBuffer floatBuffer;
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
        mbb.order(ByteOrder.nativeOrder());
        floatBuffer = mbb.asFloatBuffer();
        floatBuffer.put(a);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static IntBuffer intToBuffer(int[] a){
        IntBuffer intBuffer;
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
        mbb.order(ByteOrder.nativeOrder());
        intBuffer = mbb.asIntBuffer();
        intBuffer.put(a);
        intBuffer.position(0);
        return intBuffer;
    }
}