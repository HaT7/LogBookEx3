package com.example.logbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper {
    private static String DatabaseName= "UrlsDb.db";
    private static String tableName= "UrlsDb";
    private static String Url = "Url";

    public DbHelper(Context context){
        super(context, DatabaseName, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE UrlsDb(Id INTEGER PRIMARY KEY AUTOINCREMENT, Url TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP table if exists " + tableName);
    }

    public boolean insert(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Url, url);
        Long result = db.insert(tableName, null, contentValues);
        if (result == -1 ){
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor getAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + tableName , null);
        return cursor;
    }
}
