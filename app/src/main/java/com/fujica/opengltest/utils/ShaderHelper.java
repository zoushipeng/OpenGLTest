package com.fujica.opengltest.utils;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by zzp on 2020/5/15.
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER ,shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode){
        final int shaderObjectId = glCreateShader(type);
        if(shaderObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "could not create new shaderObject");
            }
            return 0;
        }
        // 添加source 代码
        glShaderSource(shaderObjectId, shaderCode);
        // 编译shader
        glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        // 获取编译结果
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        if(LoggerConfig.ON){
            Log.i(TAG, "Results of compiling source:\n" + shaderCode
                    + "\n" + glGetShaderInfoLog(shaderObjectId));
        }
        if(compileStatus[0] == 0){
            glDeleteShader(shaderObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG, "Compilation of shader failed.");
            }
            return 0;
        }
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId){
        final int programId = glCreateProgram();
        if(programId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not create new Program");
            }
            return 0;
        }
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        // 链接程序
        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if(LoggerConfig.ON){
            Log.i(TAG, "Results of Linking Program:\n" + glGetProgramInfoLog(programId));
        }
        if(linkStatus[0] == 0){
            glDeleteProgram(programId);
            if(LoggerConfig.ON){
                Log.w(TAG, "Linking program failed");
            }
            return 0;
        }
        return programId;
    }

    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        if(LoggerConfig.ON){
            Log.i(TAG, "Results of Validate Program: " + validateStatus[0]
                    + "\n" + glGetProgramInfoLog(programObjectId));
        }
        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource){
        int program;

        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        program = linkProgram(vertexShader, fragmentShader);
        if(LoggerConfig.ON){
            validateProgram(program);
        }
        return program;
    }
}
