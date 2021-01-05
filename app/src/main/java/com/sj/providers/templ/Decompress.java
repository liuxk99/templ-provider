package com.sj.providers.templ;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
    private static final int BUFFER_SIZE = 1024 * 10;
    private static final String TAG = Decompress.class.getSimpleName();

    public static void unzipFromAssets(Context context, String zipFile, String destination) {
        try {
            if (destination == null || destination.length() == 0)
                destination = context.getFilesDir().getAbsolutePath();
            InputStream stream = context.getAssets().open(zipFile);
            unzip(stream, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(String zipFile, String location) {
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            unzip(fin, location);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void unzip(InputStream stream, String destination) {
        dirChecker(destination, "");
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            ZipInputStream zipIs = new ZipInputStream(stream);
            ZipEntry zipEntry;

            while ((zipEntry = zipIs.getNextEntry()) != null) {
                Log.v(TAG, "Unzipping " + zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    dirChecker(destination, zipEntry.getName());
                } else {
                    FileOutputStream fos = null;

                    File f = new File(destination, zipEntry.getName());
                    if (!f.exists()) {
                        boolean success = f.createNewFile();
                        if (!success) {
                            Log.w(TAG, "Failed to create file " + f.getName());
                            continue;
                        }
                    }
                    fos = new FileOutputStream(f);
                    int count;
                    while ((count = zipIs.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                    zipIs.closeEntry();
                    fos.close();
                }

            }
            zipIs.close();
        } catch (Exception e) {
            Log.e(TAG, "unzip", e);
        }

    }

    private static void dirChecker(String destination, String dir) {
        File f = new File(destination, dir);

        if (!f.isDirectory()) {
            boolean success = f.mkdirs();
            if (!success) {
                Log.w(TAG, "Failed to create folder " + f.getName());
            }
        }
    }
}