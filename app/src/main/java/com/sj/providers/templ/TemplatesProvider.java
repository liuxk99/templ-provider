package com.sj.providers.templ;

import java.util.HashMap;
 
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
 
public class TemplatesProvider extends ContentProvider {
        private static final String TAG = TemplatesProvider.class.getSimpleName();
        private static final String KEYWORD = TemplatesProvider.class.getSimpleName();

        private static final String PREFIX = Templates.class.getSimpleName();
        private static final String DB_NAME = PREFIX + ".db";
        private static final String DB_TABLE = PREFIX + "Table";
        private static final int DB_VERSION = 1;
 
        private static final String DB_CREATE = "create table " + DB_TABLE +
                                " (" + Templates.ID + " integer primary key autoincrement, " +
                                Templates.TITLE + " text not null, " +
                                Templates.ABSTRACT + " text not null, " +
                                Templates.URL + " text not null);";
 
        private static final UriMatcher uriMatcher;
        static {
                uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
                uriMatcher.addURI(Templates.AUTHORITY, "item", Templates.ITEM);
                uriMatcher.addURI(Templates.AUTHORITY, "item/#", Templates.ITEM_ID);
                uriMatcher.addURI(Templates.AUTHORITY, "pos/#", Templates.ITEM_POS);
        }
 
        private static final HashMap<String, String> articleProjectionMap;
        static {
                articleProjectionMap = new HashMap<String, String>();
                articleProjectionMap.put(Templates.ID, Templates.ID);
                articleProjectionMap.put(Templates.TITLE, Templates.TITLE);
                articleProjectionMap.put(Templates.ABSTRACT, Templates.ABSTRACT);
                articleProjectionMap.put(Templates.URL, Templates.URL);
        }
 
        private DBHelper dbHelper = null;
        private ContentResolver resolver = null;
 
        @Override
        public boolean onCreate() {
                Context context = getContext();
                resolver = context.getContentResolver();
                dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
 
                Log.i(TAG, KEYWORD + "Create");
 
                return true;
        }
 
        @Override
        public String getType(Uri uri) {
                switch (uriMatcher.match(uri)) {
                case Templates.ITEM:
                        return Templates.CONTENT_TYPE;
                case Templates.ITEM_ID:
                case Templates.ITEM_POS:
                        return Templates.CONTENT_ITEM_TYPE;
                default:
                        throw new IllegalArgumentException("Error Uri: " + uri);
                }
        }
 
        @Override
        public Uri insert(Uri uri, ContentValues values) {
                if(uriMatcher.match(uri) != Templates.ITEM) {
                        throw new IllegalArgumentException("Error Uri: " + uri);
                }
 
                SQLiteDatabase db = dbHelper.getWritableDatabase();
 
                long id = db.insert(DB_TABLE, Templates.ID, values);
                if(id < 0) {
                        throw new SQLiteException("Unable to insert " + values + " for " + uri);
                }
 
                Uri newUri = ContentUris.withAppendedId(uri, id);
                resolver.notifyChange(newUri, null);
 
                return newUri;
        }
 
        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int count = 0;
 
                switch(uriMatcher.match(uri)) {
                case Templates.ITEM: {
                        count = db.update(DB_TABLE, values, selection, selectionArgs);
                        break;
                }
                case Templates.ITEM_ID: {
                        String id = uri.getPathSegments().get(1);
                        count = db.update(DB_TABLE, values, Templates.ID + "=" + id
                                        + (!TextUtils.isEmpty(selection) ? " and (" + selection + ')' : ""), selectionArgs);
                        break;
                }
                default:
                        throw new IllegalArgumentException("Error Uri: " + uri);
                }
 
                resolver.notifyChange(uri, null);
 
                return count;
        }
 
        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int count = 0;
 
                switch(uriMatcher.match(uri)) {
                case Templates.ITEM: {
                        count = db.delete(DB_TABLE, selection, selectionArgs);
                        break;
                }
                case Templates.ITEM_ID: {
                        String id = uri.getPathSegments().get(1);
                        count = db.delete(DB_TABLE, Templates.ID + "=" + id
                                        + (!TextUtils.isEmpty(selection) ? " and (" + selection + ')' : ""), selectionArgs);
                        break;
                }
                default:
                        throw new IllegalArgumentException("Error Uri: " + uri);
                }
 
                resolver.notifyChange(uri, null);
 
                return count;
        }
 
        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                Log.i(TAG, KEYWORD + ".query: " + uri);
 
                SQLiteDatabase db = dbHelper.getReadableDatabase();
 
                SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
                String limit = null;
 
                switch (uriMatcher.match(uri)) {
                case Templates.ITEM: {
                        sqlBuilder.setTables(DB_TABLE);
                        sqlBuilder.setProjectionMap(articleProjectionMap);
                        break;
                }
                case Templates.ITEM_ID: {
                        String id = uri.getPathSegments().get(1);
                        sqlBuilder.setTables(DB_TABLE);
                        sqlBuilder.setProjectionMap(articleProjectionMap);
                        sqlBuilder.appendWhere(Templates.ID + "=" + id);
                        break;
                }
                case Templates.ITEM_POS: {
                        String pos = uri.getPathSegments().get(1);
                        sqlBuilder.setTables(DB_TABLE);
                        sqlBuilder.setProjectionMap(articleProjectionMap);
                        limit = pos + ", 1";
                        break;
                }
                default:
                        throw new IllegalArgumentException("Error Uri: " + uri);
                }
 
                Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, TextUtils.isEmpty(sortOrder) ? Templates.DEFAULT_SORT_ORDER : sortOrder, limit);
                cursor.setNotificationUri(resolver, uri);
 
                return cursor;
        }
  
        @Override
        public Bundle call(String method, String request, Bundle args) {
                Log.i(TAG, KEYWORD + ".call: " + method);
 
                if(method.equals(Templates.METHOD_GET_ITEM_COUNT)) {
                        return getItemCount();
                }
 
                throw new IllegalArgumentException("Error method call: " + method);
        }
 
        private Bundle getItemCount() {
                Log.i(TAG, KEYWORD + ".getItemCount");
 
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select count(*) from " + DB_TABLE, null);
 
                int count = 0;
                if (cursor.moveToFirst()) {
                        count = cursor.getInt(0);
                }
 
                Bundle bundle = new Bundle();
                bundle.putInt(Templates.KEY_ITEM_COUNT, count);
 
                cursor.close();
                db.close();
 
                return bundle;
        }
 
        private static class DBHelper extends SQLiteOpenHelper {
                public DBHelper(Context context, String name, CursorFactory factory, int version) {
                        super(context, name, factory, version);
                }
 
                @Override
                public void onCreate(SQLiteDatabase db) {
                        db.execSQL(DB_CREATE);
                }
 
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
                        onCreate(db);
                }
        }
}