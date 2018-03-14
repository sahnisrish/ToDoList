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
        String createItem="CREATE TABLE "+Contract.ItemList.TABLE_NAME+" ( "+
                Contract.ItemList.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.ItemList.ITEM+" TEXT, "+
                Contract.ItemList.DESCRIPTION+" TEXT, "+
                Contract.ItemList.PRIORITY+" INTEGER, "+
                Contract.ItemList.DEADLINE+" INTEGER)";
        db.execSQL(createItem);
        String createTags="CREATE TABLE "+Contract.TagsList.TABLE_NAME+" ("+
                Contract.TagsList.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.TagsList.TAG+" TEXT)";
        db.execSQL(createTags);
        String createTagAssign="CREATE TABLE "+Contract.TagAssignment.TABLE_NAME+" ("+
                Contract.TagAssignment.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.TagAssignment.ITEM_ID+" INTEGER, "+
                Contract.TagAssignment.TAG_ID+" INTEGER, "+
                "FOREIGN KEY ("+Contract.TagAssignment.ITEM_ID+") REFERENCES "+Contract.ItemList.TABLE_NAME+" ("+Contract.ItemList.ID+") , "+
                "FOREIGN KEY ("+Contract.TagAssignment.TAG_ID+") REFERENCES "+Contract.TagsList.TABLE_NAME+" ("+Contract.TagsList.ID+") )";
        db.execSQL(createTagAssign);
        String createComments="CREATE TABLE "+Contract.Comments.TABLE_NAME+" ("+
                Contract.Comments.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.Comments.COMMENT+" TEXT, "+
                Contract.Comments.ITEM_ID+" INTEGER, "+
                "FOREIGN KEY ("+Contract.Comments.ITEM_ID+") REFERENCES "+Contract.ItemList.TABLE_NAME+" ("+Contract.ItemList.ID+") ON DELETE CASCADE )";
        db.execSQL(createComments);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
