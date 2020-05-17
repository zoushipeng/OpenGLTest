package com.fujica.opengltest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fujica.opengltest.objects.Mallet;
import com.fujica.opengltest.objects.Table;
import com.fujica.opengltest.programs.ColorShaderProgram;
import com.fujica.opengltest.programs.TextureShaderProgram;
import com.fujica.opengltest.utils.LoggerConfig;
import com.fujica.opengltest.utils.ShaderHelper;
import com.fujica.opengltest.utils.TextResourceReader;
import com.fujica.opengltest.utils.TextureHelper;
import com.fujica.opengltest.utils.tests.CPUComandReSort;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.ShutdownChannelGroupException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.fujica.opengltest.utils.Constants.BYTES_PER_FLOAT;

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

        private final Context context;

        private final float[] projectionMatrix = new float[16];
        private final float[] modelMatrix = new float[16];

        private Table table;
        private Mallet mallet;

        private TextureShaderProgram textureProgram;
        private ColorShaderProgram colorProgram;

        private int texture;

        public MyRender(Context mContext){
            context = mContext;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            table = new Table();
            mallet = new Mallet();

            textureProgram = new TextureShaderProgram(this.context);
            colorProgram = new ColorShaderProgram(this.context);

            texture = TextureHelper.loadTexture(this.context, R.drawable.air_hockey_surface);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.e("TAG", "width: " + width + " height:" + height);
            glViewport(0, 0, width, height);
            perspectiveM(projectionMatrix, 0,45, (float) width / (float) height, 1f, 10f);
            setIdentityM(modelMatrix, 0);
            translateM(modelMatrix, 0, 0f, 0f, -3.5f);
            rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
            final float[] temp = new float[16];
            multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
            System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
        }

        /**
         * 60 fps
         * @param gl10 unused
         */
        @Override
        public void onDrawFrame(GL10 gl10) {
            glClear(GL_COLOR_BUFFER_BIT);

            textureProgram.useProgram();
            textureProgram.setUniforms(projectionMatrix, texture);
            table.bindData(textureProgram);
            table.draw();

            colorProgram.useProgram();
            colorProgram.setUniforms(projectionMatrix);
            mallet.bindData(colorProgram);
            mallet.draw();
        }
    }
}
