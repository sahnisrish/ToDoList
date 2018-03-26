package com.example.sahni.todolist;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {
    Bundle bundle;
    int id;
    ListItem item;
    ItemOpenHelper openHelper;
    ListView Comments;
    ArrayList<Comments> comments;
    CommentsAdapter adapter;
    SQLiteDatabase database;
    View rootView;
    Context activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=context;
    }

    public DisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_display, container, false);
        openHelper=ItemOpenHelper.getInstance(activity);
        bundle=getArguments();
        setData();
        return rootView;
    }

    private void setData() {
        id=bundle.getInt(Constant.ID_KEY);
        database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.ItemList.TABLE_NAME,null,Contract.ItemList.ID+"=?", new String[]{id + ""},null,null,null);
        cursor.moveToNext();
        item=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.PRIORITY)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
        LinearLayout dataBar=rootView.findViewById(R.id.dataBar);
        TextView ToDo=dataBar.findViewById(R.id.todo);
        TextView Description=dataBar.findViewById(R.id.description);
        TextView Date=dataBar.findViewById(R.id.date);
        ToDo.setText(item.getItemName());
        Description.setText(cursor.getString(cursor.getColumnIndex(Contract.ItemList.DESCRIPTION)));
        Date.setText(item.getDeadLine());
        LinearLayout tagsBar=rootView.findViewById(R.id.tags);
        tagsBar.removeAllViews();
        if(item.getPriority()!=Constant.PRIORITY.NONE)
        {
            TagView priority = new TagView(activity, item.getPriority());
            priority.addTag(tagsBar, null, null);
        }
        TagView.addMultipleTags(activity,tagsBar,id,null,null);
        setComments();
    }

    private void setComments() {
        Comments=rootView.findViewById(R.id.comments);
        comments=new ArrayList<>();
        database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.Comments.TABLE_NAME,null,Contract.Comments.ITEM_ID+"=?", new String[]{id + ""},null,null,Contract.Comments.DATE);
        while (cursor.moveToNext()){
            Comments comment=new Comments(cursor.getInt(cursor.getColumnIndex(Contract.Comments.ID)),
                    cursor.getString(cursor.getColumnIndex(Contract.Comments.COMMENT)),
                    cursor.getInt(cursor.getColumnIndex(Contract.Comments.ITEM_ID)),
                    cursor.getLong(cursor.getColumnIndex(Contract.Comments.DATE)));
            comments.add(comment);
        }
        adapter=new CommentsAdapter(activity,comments,null);
        Comments.setAdapter(adapter);
    }


    public void result(int requestCode, int resultCode, Intent data) {
        if(resultCode==Constant.RESULT_EDIT) {
            setData();
        }
    }
}
