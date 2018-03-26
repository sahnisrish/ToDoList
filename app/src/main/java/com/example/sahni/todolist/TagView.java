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

public class TagView extends android.support.v7.widget.AppCompatTextView {
    private int id;
    private String title;
    Context context;
    private boolean isPriority;
    public TagView(Context context, String title, int id) {
        super(context);
        this.context = context;
        this.title = title;
        this.id = id;
        this.isPriority = false;
    }

    public TagView(Context context, int id) {
        super(context);
        this.context = context;
        this.id = id;
        switch (id)
        {
            case Constant.PRIORITY.HIGH:
                title="high";
                break;
            case Constant.PRIORITY.LOW:
                title="low";
                break;
            case Constant.PRIORITY.MED:
                title="medium";
                break;
        }
        this.isPriority=true;
    }
    public TagView(Context context,TagView tagView)
    {
        super(context);
        this.title=tagView.title;
        this.id=tagView.id;
        this.isPriority=tagView.isPriority;
    }

    public void addTag(LinearLayout layout, View.OnClickListener onClick, View.OnLongClickListener onLongClick) {
        setText(title);
        if (isPriority())
            setBackgroundResource(R.drawable.button);
        else
            setBackgroundResource(R.drawable.tags);
        setPadding(10, 10, 10, 10);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        setLayoutParams(layoutParams);
        setOnClickListener(onClick);
        setOnLongClickListener(onLongClick);
        layout.addView(this);
    }

    public static void addMultipleTags(Context context, LinearLayout layout, int id, View.OnClickListener onClick, View.OnLongClickListener onLongClick) {
        ItemOpenHelper openHelper = ItemOpenHelper.getInstance(context);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.TagAssignment.TABLE_NAME, null, Contract.TagAssignment.ITEM_ID + "=?", new String[]{id + ""}, null, null, null);
        while (cursor.moveToNext()) {
            int tagId = cursor.getInt(cursor.getColumnIndex(Contract.TagAssignment.TAG_ID));
            Cursor cursor1 = database.query(Contract.TagsList.TABLE_NAME, null, Contract.TagsList.ID + "=?", new String[]{tagId + ""}, null, null, null);
            cursor1.moveToNext();
            TagView view = new TagView(context, cursor1.getString(cursor1.getColumnIndex(Contract.TagsList.TAG)), tagId);
            view.addTag(layout, onClick, onLongClick);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public String getTitle() {
        return title;
    }
}
