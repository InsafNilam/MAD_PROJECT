package com.example.tonemelody;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context,"Song.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("Create Table favourite(name TEXT primary key,url TEXT)");
        DB.execSQL("Create Table playlist(name TEXT primary key,favName TEXT,url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("Drop Table if exists favourite");
        DB.execSQL("Drop Table if exists playlist");
    }

    public boolean insertFavourite(String name,String url){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("url",url);

        Cursor cursor = DB.rawQuery("select * from favourite where name LIKE ?",new String[]{name});
        if(cursor.getCount()==0){
            DB.insert("favourite", null, contentValues);
            return true;
        }
        return false;
    }
    public boolean deleteFavourite(String name){
        SQLiteDatabase DB = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery("select * from favourite where name LIKE ?", new String[]{name});
            if (cursor.getCount() > 0) {
                long result = DB.delete("favourite", "name = ?", new String[]{name});
                return result != -1;
            }
        return false;
    }

    public Cursor getData(){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor =DB.rawQuery("select * from favourite",null);
        return cursor;
    }


    public boolean insertPlaylist(String name,String desc,String url){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("favName",desc);
        contentValues.put("url",url);

        Cursor cursor = DB.rawQuery("select * from playlist where name LIKE ?",new String[]{name});
        if(cursor.getCount()==0){
            DB.insert("playlist", null, contentValues);
            return true;
        }return false;
    }
    public boolean updatePlaylist(String name, String desc,String url){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("favName",desc);
        contentValues.put("name",name);
        contentValues.put("url",url);
        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery("select * from playlist where name LIKE ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.update("playlist",contentValues,"name = ?", new String[]{name});
            return result != -1;
        }return false;
    }
    public boolean deletePlaylist(String name){
        SQLiteDatabase DB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery("select * from playlist where name LIKE ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.delete("playlist", "name = ?", new String[]{name});
            return result != -1;
        }return false;
    }
    public Cursor getPlaylistData(){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor =DB.rawQuery("select * from playlist",null);

        return cursor;
    }
}
