package com.sj4a.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyConf {
    private static final String TAG = MyConf.class.getSimpleName();
    static MyConf instance = null;
    private boolean mInitialized;

    public static MyConf getInstance() {
        if (instance == null) {
            instance = new MyConf();
        }
        return instance;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public void initConf(Context context) {
        if (!mInitialized) {
            doInit(context);
            mInitialized = true;
        }
    }

    private void doInit(Context context) {
        final File dstFile = context.getDatabasePath("xxx.db");
        if (!dstFile.exists()) {

            String srcFile = DbStrategy.getDbName();

            Log.d(TAG, "assets: " + srcFile);
            copyAssetsFile(context, srcFile, dstFile);
        }
    }

    public static void copyAssetsFile(Context context, String srcFile, File dstFile) {
        String msg = "copyAssetsFile(" + context + ", " + srcFile + ", " + dstFile + ")";
        Log.i(TAG, "=> " + msg);
        {
            final File dstDir = dstFile.getParentFile();

            Log.d(TAG, "apkDir: " + dstDir);
            if (!dstDir.exists()) {
                boolean res = dstDir.mkdirs();
                Log.d(TAG, res + " = mkdirs()");
            }

            try {
                InputStream myInput = context.getAssets().open(srcFile);

                //Open the empty db as the output stream
                OutputStream myOutput = new FileOutputStream(dstFile);
                //transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                //Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "<- " + msg);
    }
}
