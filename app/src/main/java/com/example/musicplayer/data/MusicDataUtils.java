package com.example.musicplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.musicplayer.entity.Music;

import java.util.ArrayList;

public class MusicDataUtils {

    private static final String TAG = "MusicDataUtils";
    public static ArrayList<Music> listAll(Context context) {
        return query(context,null);
    }
    public static synchronized ArrayList<Music> query(Context context,String condition) {
        SQLiteDatabase sqliteDatabase=null;
        Cursor cursor=null;
        try {
            MyDBHelper myDBHelper = new MyDBHelper(context);
            sqliteDatabase = myDBHelper.getReadableDatabase();
            String sql = "select id,name,albumId,albumName,singerId,singerName,timeLength,timeLengthText,fileUrl,filePath,fileSize,fileSizeText,description,albumPictureUrl,timeStamp from " + MyDBHelper.TABLE_NAME;
            if (!TextUtils.isEmpty(condition)) {
                sql += " WHERE " + condition;
            }
            sql += " ORDER BY collect_timeStamp DESC";
            Log.d(TAG, "query sql: " + sql);
            ArrayList<Music> infoArray = new ArrayList<Music>();
            cursor = sqliteDatabase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Music info = new Music();
                info.setId(cursor.getInt(0));
                info.setName(cursor.getString(1));
                info.setAlbumId(cursor.getInt(2));
                info.setAlbumName(cursor.getString(3));
                info.setSingerId(cursor.getInt(4));
                info.setSingerName(cursor.getString(5));
                info.setTimeLength(cursor.getInt(6));
                info.setTimeLengthText(cursor.getString(7));
                info.setFileUrl(cursor.getString(8));
                info.setFilePath(cursor.getString(9));
                info.setFileSize(cursor.getInt(10));
                info.setFileSizeText(cursor.getString(11));
                info.setDescription(cursor.getString(12));
                info.setAlbumPictureUrl(cursor.getString(13));
                info.setTimeStamp(cursor.getString(14));

                infoArray.add(info);
            }
            cursor.close();
            cursor=null;
            sqliteDatabase.close();
            sqliteDatabase=null;
            return infoArray;
        }catch (Throwable tr){
            tr.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
            if (sqliteDatabase!=null){
                sqliteDatabase.close();
            }
        }
        return null;
    }

    public static synchronized int delete(Context context,String whereClause, String[] whereArgs) {
        SQLiteDatabase sqliteDatabase=null;
        try {
            MyDBHelper myDBHelper = new MyDBHelper(context);
            sqliteDatabase = myDBHelper.getWritableDatabase();

            int count = sqliteDatabase.delete(MyDBHelper.TABLE_NAME, whereClause,whereArgs);
            return count;
        }catch (Throwable tr){
            tr.printStackTrace();
        }finally {
            if (sqliteDatabase!=null){
                sqliteDatabase.close();
            }
        }
        return 0;
    }

    public static int deleteAll(Context context) {
        return delete(context,null,null);
    }

    public static int delete(Context context,int id) {
        return delete(context,"id=?", new String[]{String.valueOf(id)});
    }

    public static synchronized long insertOrUpdate(Context context,Music info) {
        SQLiteDatabase sqliteDatabase=null;
        Cursor cursor=null;
        try {
            MyDBHelper myDBHelper = new MyDBHelper(context);
            sqliteDatabase = myDBHelper.getWritableDatabase();
            String sql = "select id from " + MyDBHelper.TABLE_NAME + " WHERE id=?";
            cursor = sqliteDatabase.rawQuery(sql, new String[]{String.valueOf(info.getId())});
            boolean exists=false;
            if (cursor.moveToFirst()){
                exists=true;
            }
            cursor.close();
            cursor=null;
            ContentValues cv = new ContentValues();
            cv.put("collect_timeStamp", System.currentTimeMillis());
            cv.put("name", info.getName());
            cv.put("albumId", info.getAlbumId());
            cv.put("albumName", info.getAlbumName());
            cv.put("singerId", info.getSingerId());
            cv.put("singerName", info.getSingerName());
            cv.put("timeLength", info.getTimeLength());
            cv.put("timeLengthText", info.getTimeLengthText());
            cv.put("fileUrl", info.getFileUrl());
            cv.put("filePath", info.getFilePath());
            cv.put("fileSize", info.getFileSize());
            cv.put("fileSizeText", info.getFileSizeText());
            cv.put("description", info.getDescription());
            cv.put("albumPictureUrl", info.getAlbumPictureUrl());
            cv.put("timeStamp", info.getTimeStamp());
            if (exists){
                int result = sqliteDatabase.update(MyDBHelper.TABLE_NAME, cv,"id=?", new String[]{String.valueOf(info.getId())});
                return result;
            }
            cv.put("id", info.getId());

            long rowId = sqliteDatabase.insert(MyDBHelper.TABLE_NAME, null, cv);
            return rowId;
        }catch (Throwable tr){
            tr.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
            if (sqliteDatabase!=null){
                sqliteDatabase.close();
            }
        }
        return 0L;
    }


    public static synchronized int update(Context context,Music info) {
        SQLiteDatabase sqliteDatabase=null;
        Cursor cursor=null;
        try {
            MyDBHelper myDBHelper = new MyDBHelper(context);
            sqliteDatabase = myDBHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", info.getName());
            cv.put("albumId", info.getAlbumId());
            cv.put("albumName", info.getAlbumName());
            cv.put("singerId", info.getSingerId());
            cv.put("singerName", info.getSingerName());
            cv.put("timeLength", info.getTimeLength());
            cv.put("timeLengthText", info.getTimeLengthText());
            cv.put("fileUrl", info.getFileUrl());
            cv.put("filePath", info.getFilePath());
            cv.put("fileSize", info.getFileSize());
            cv.put("fileSizeText", info.getFileSizeText());
            cv.put("description", info.getDescription());
            cv.put("albumPictureUrl", info.getAlbumPictureUrl());
            cv.put("timeStamp", info.getTimeStamp());;
            int count = sqliteDatabase.update(MyDBHelper.TABLE_NAME, cv, "id=?", new String[]{String.valueOf(info.getId())});
            return count;
        }catch (Throwable tr){
            tr.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
            if (sqliteDatabase!=null){
                sqliteDatabase.close();
            }
        }
        return 0;
    }

    private static class MyDBHelper extends SQLiteOpenHelper {
        private static final String TAG = "MyDBHelper";
        private static final String DB_NAME = "music_store.db";
        private static final int DB_VERSION = 1;
        private static final String TABLE_NAME = "tb_music";

        public MyDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate");
            String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "id INTEGER PRIMARY KEY NOT NULL,"
                    + "name VARCHAR NOT NULL,"
                    + "albumId INTEGER NOT NULL,"
                    + "albumName VARCHAR NOT NULL,"
                    + "singerId INTEGER NOT NULL,"
                    + "singerName VARCHAR NOT NULL,"
                    + "timeLength INTEGER NOT NULL,"
                    + "timeLengthText VARCHAR NOT NULL,"
                    + "fileUrl VARCHAR NOT NULL,"
                    + "filePath VARCHAR NOT NULL,"
                    + "fileSize INTEGER NOT NULL,"
                    + "fileSizeText VARCHAR NOT NULL,"
                    + "description VARCHAR NOT NULL,"
                    + "albumPictureUrl VARCHAR NOT NULL,"
                    + "timeStamp VARCHAR,"
                    + "collect_timeStamp LONG"
                    + ");";
            Log.d(TAG, "create_sql:" + create_sql);
            db.execSQL(create_sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
            Log.d(TAG, "drop_sql:" + drop_sql);
            db.execSQL(drop_sql);
            onCreate(db);
        }

    }
}
