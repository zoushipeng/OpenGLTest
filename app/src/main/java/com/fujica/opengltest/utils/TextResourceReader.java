package com.fujica.opengltest.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zzp on 2020/5/15.
 */
public class TextResourceReader {
    public static String readTextFileFromResource(Context context, int resourceId){
        StringBuilder sb = new StringBuilder();
        try{
            InputStream is = context.getResources().openRawResource(resourceId);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String nextLine;
            while((nextLine = bufferedReader.readLine()) != null){
                sb.append(nextLine);
                sb.append("\n");
            }
        }
        catch (IOException e){
            throw new RuntimeException("Could not open resource:" + resourceId, e);
        }
        catch (Resources.NotFoundException ex){
            throw new RuntimeException("Resource not found:" + resourceId, ex);
        }
        return sb.toString();
    }
}
