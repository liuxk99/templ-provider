package com.sj4a.utils;

import java.util.Random;

public class DbStrategy {
    final static String[] dbFiles = {"a.txt", "b.txt"};
    final static Random mRandom = new Random();

    public static String getDbName() {
        int index = mRandom.nextInt(dbFiles.length) % dbFiles.length;
        return dbFiles[index];
    }
}
