package com.example.sahni.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sahni on 1/3/18.
 */

public class TagView {
    private int id;
    private String title;
    Context context;
    public TagView(Context context,String title,int id) {
        this.context=context;
        this.title=title;
        this.id=id;
    }
    public void addTag(LinearLayout layout, View.OnClickListener onClick, View.OnLongClickListener onLongClick)
    {
        TextView view=new TextView(context);
        view.setText(title);
        view.setBackgroundResource(R.drawable.tags);
        view.setPadding(10,10,10,10);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,10,10);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(onClick);
        view.setOnLongClickListener(onLongClick);
        view.setTag(id);
        layout.addView(view);
    }
    public static void addMultipleTags(Context context, LinearLayout layout, int id, View.OnClickListener onClick, View.OnLongClickListener onLongClick)
    {
        layout.removeAllViews();
        ItemOpenHelper openHelper=ItemOpenHelper.getInstance(context);
        SQLiteDatabase database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.TagAssignment.TABLE_NAME,null,Contract.TagAssignment.ITEM_ID+"=?", new String[]{id + ""},null,null,null);
        while (cursor.moveToNext())
        {
            int tagId=cursor.getInt(cursor.getColumnIndex(Contract.TagAssignment.TAG_ID));
            Cursor cursor1=database.query(Contract.TagsList.TABLE_NAME,null,Contract.TagsList.ID+"=?", new String[]{tagId + ""},null,null,null);
            cursor1.moveToNext();
            TagView view=new TagView(context,cursor1.getString(cursor1.getColumnIndex(Contract.TagsList.TAG)),tagId);
            view.addTag(layout,onClick,onLongClick);
        }
    }

}
