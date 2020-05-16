package com.fujica.opengltest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.fujica.opengltest.utils.ShaderHelper;
import com.fujica.opengltest.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if(supportEs2){
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new MyRender(this));
            rendererSet = true;
        }
        else{
            Toast.makeText(this, "不支持ES2, 退出", Toast.LENGTH_SHORT);
            return;
        }
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rendererSet){
            glSurfaceView.onResume();
        }
    }

    static class MyRender implements GLSurfaceView.Renderer {

        private static final int POSITION_COMPONENT_COUNT = 2;

        private static final int BYTES_PER_FLOAT = 4;
        private final FloatBuffer vertexData;
        private final Context context;

        public MyRender(Context mContext){
            context = mContext;

            // 逆时针定义，卷曲顺序。使用一致的卷曲顺序，可优化性能。
            float[] tableVerticesWithTriangles = {
                0f, 0f,
                9f, 14f,
                0f, 14f,

                0f, 0f,
                9f, 0f,
                9f, 14f,

                0f, 7f,
                9f, 7f,

                4.5f, 2f,
                4.5f, 12f,
            };

            vertexData = ByteBuffer.allocate(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            vertexData.put(tableVerticesWithTriangles);
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
            String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
            String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
            int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
            int fragmentShader = ShaderHelper.compileVertexShader(fragmentShaderSource);


        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {
            glViewport(0, 0, i, i1);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            glClear(GL_COLOR_BUFFER_BIT);
        }
    }
}
