package com.example.sahni.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sahni on 3/3/18.
 */

public class CommentsAdapter extends BaseAdapter {
    ArrayList<Comments> comments;
    Context context;
    onDeleteClick deleteClick;
    CommentsAdapter(Context context,ArrayList<Comments> comments,onDeleteClick deleteClick)
    {
        this.comments=comments;
        this.context=context;
        this.deleteClick=deleteClick;
    }
    interface onDeleteClick extends View.OnClickListener{

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
            Tag tag=new Tag();
            tag.comment=view.findViewById(R.id.comment);
            tag.date=view.findViewById(R.id.date);
            tag.delete=view.findViewById(R.id.delete);
            view.setTag(tag);
        }
        final Tag tag= (Tag) view.getTag();
        tag.comment.setTag(comments.get(position).getId());
        tag.comment.setText(comments.get(position).getComment());
        tag.date.setText(comments.get(position).getDate());
        tag.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,tag.comment.getText(),Toast.LENGTH_SHORT).show();
            }
        });
        if(deleteClick!=null) {
            tag.delete.setVisibility(View.VISIBLE);
            tag.delete.setTag(position);
            tag.delete.setOnClickListener(deleteClick);
        }
        else
            tag.delete.setVisibility(View.GONE);
        return view;
    }
    class Tag{
        TextView comment;
        TextView date;
        Button delete;
    }
}
