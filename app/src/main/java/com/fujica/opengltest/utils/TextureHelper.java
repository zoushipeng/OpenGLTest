package com.fujica.opengltest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    private static final String TAG = "TextureHelper";
    
    public static int loadTexture(Context context, int resourceId){
        final int[] textureObjectId = new int[1];
        glGenTextures(1, textureObjectId, 0);
        if(textureObjectId[0] == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object");
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if(bitmap == null){
            if(LoggerConfig.ON){
                Log.w(TAG, "Resource ID:" + resourceId + " could not be decoded.");
            }
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectId[0]);
        // 设置过滤器 缩小时，使用三线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_LINEAR);
        // 设置过滤器 放大时，使用双线性过滤  放大值支持最近邻过滤和双线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        // 生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        // 解除纹理绑定
        glBindTexture(GL_TEXTURE_2D,0);

        return textureObjectId[0]; // 返回纹理对象
    }
}
