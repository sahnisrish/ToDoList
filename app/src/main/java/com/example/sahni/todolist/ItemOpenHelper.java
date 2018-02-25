package com.example.sahni.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sahni on 17/2/18.
 */

public class ItemOpenHelper extends SQLiteOpenHelper {
    private static ItemOpenHelper openHelper;
    public static ItemOpenHelper getInstance(Context context){
        if (openHelper==null){
            openHelper=new ItemOpenHelper(context.getApplicationContext());
        }
        return openHelper;
    }
    private ItemOpenHelper(Context context) {
        super(context, Contract.DATABASE_NAME, null, Contract.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create="CREATE TABLE "+Contract.ItemList.TABLE_NAME+" ( "+
                Contract.ItemList.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.ItemList.ITEM+" TEXT, "+
                Contract.ItemList.DESCRIPTION+" TEXT, "+
                Contract.ItemList.DEADLINE+" INTEGER)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
