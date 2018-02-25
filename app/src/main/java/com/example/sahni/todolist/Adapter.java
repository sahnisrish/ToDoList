package com.example.sahni.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sahni on 16/2/18.
 */

public class Adapter extends BaseAdapter {
    ArrayList<ListItem> list;
    Context context;
    CheckedListener checkedListener;
    ItemClickedListener itemClickedListener;
    interface CheckedListener extends CompoundButton.OnCheckedChangeListener {

        @Override
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
    interface ItemClickedListener extends View.OnClickListener{

    }
    Adapter(Context context, ArrayList<ListItem> list, CheckedListener checkedListener, ItemClickedListener itemClickedListener){
        this.context=context;
        this.list=list;
        this.checkedListener=checkedListener;
        this.itemClickedListener=itemClickedListener;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, parent, false);
            ViewHolder holder=new ViewHolder();
            holder.item=view.findViewById(R.id.ToDoLayout);
            holder.name=view.findViewById(R.id.item_name);
            holder.deadline=view.findViewById(R.id.item_deadline);
            holder.completed=view.findViewById(R.id.Completed);
            holder.completed.setOnCheckedChangeListener(checkedListener);
            holder.item.setOnClickListener(itemClickedListener);
            view.setTag(holder);
        }
        ViewHolder holder=(ViewHolder)view.getTag();
        holder.item.setBackgroundResource(R.drawable.list_item);
        holder.item.setTag(list.get(position).getId());
        holder.name.setText(list.get(position).getItemName());
        holder.deadline.setText(list.get(position).getDeadLine());
        holder.completed.setChecked(false);
        Tag tag=new Tag(list.get(position).getId(),holder.item);
        holder.completed.setTag(tag);
        return view;
    }
    class ViewHolder{
        TextView name;
        TextView deadline;
        CheckBox completed;
        LinearLayout item;
    }
    class Tag{
        int id;
        LinearLayout item;
        Tag(int id,LinearLayout item)
        {
            this.id=id;
            this.item=item;
        }
    }
}