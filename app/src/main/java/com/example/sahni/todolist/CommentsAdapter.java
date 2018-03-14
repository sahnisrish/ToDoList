package com.example.sahni.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sahni on 3/3/18.
 */

public class CommentsAdapter extends BaseAdapter {
    ArrayList<Comments> comments;
    Context context;
    onLongClick longClick;
    CommentsAdapter(Context context,ArrayList<Comments> comments,onLongClick longClick)
    {
        this.comments=comments;
        this.context=context;
        this.longClick=longClick;
    }
    interface onLongClick extends View.OnLongClickListener{

    }
    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.comment_item, parent, false);
            TextView comment=view.findViewById(R.id.comment);
            view.setTag(comment);
        }
        final TextView comment= (TextView) view.getTag();
        comment.setTag(comments.get(position).getId());
        comment.setText(comments.get(position).getComment());
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,comment.getText(),Toast.LENGTH_SHORT).show();
            }
        });
        comment.setOnLongClickListener(longClick);
        return view;
    }
}
