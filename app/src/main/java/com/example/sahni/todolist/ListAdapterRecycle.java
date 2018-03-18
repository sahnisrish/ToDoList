package com.example.sahni.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sahni on 18/3/18.
 */

public class ListAdapterRecycle extends RecyclerView.Adapter<ListAdapterRecycle.ViewHolder> {
    ArrayList<ListItem> list;
    Context context;
    CheckedListener checkedListener;
    ItemLongClickedListener itemLongClickedListener;
    TagClickedListener tagClickedListener;
    ItemClickedListener itemClickedListener;
    interface CheckedListener {
        void onCheckChanged(int position,boolean isChecked);
    }
    interface ItemClickedListener {
        void onItemClick(int position);
    }
    interface ItemLongClickedListener {
        void onItemLongClick(int position);
    }
    interface TagClickedListener extends View.OnClickListener{

    }

    ListAdapterRecycle(Context context, ArrayList<ListItem> list, CheckedListener checkedListener, ItemClickedListener itemClickedListener,ItemLongClickedListener itemLongClickedListener,TagClickedListener tagClickedListener)
    {
        this.context=context;
        this.list=list;
        this.checkedListener=checkedListener;
        this.itemClickedListener=itemClickedListener;
        this.itemLongClickedListener=itemLongClickedListener;
        this.tagClickedListener=tagClickedListener;
    }
    @Override
    public ListAdapterRecycle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListAdapterRecycle.ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getItemName());
        holder.deadline.setText(list.get(position).getDeadLine());
        holder.completed.setChecked(false);
        holder.completed.setTag(list.get(position).getId());
        holder.tagsBar.removeAllViews();

        holder.completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedListener.onCheckChanged(holder.getAdapterPosition(),isChecked);
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClickedListener.onItemLongClick(holder.getAdapterPosition());
                return true;
            }
        });

        if((list.get(position).getDeadLineLong()<System.currentTimeMillis())&&(!Constant.format.format(list.get((position)).getDeadLineLong()).equals(Constant.format.format(System.currentTimeMillis()))))
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.deselectedColor));
        else
        {
            int colorId;
            switch (list.get(position).getPriority())
            {
                case Constant.PRIORITY.HIGH:
                    colorId=R.color.priorityHigh;
                    break;
                case Constant.PRIORITY.MED:
                    colorId=R.color.priorityMedium;
                    break;
                case Constant.PRIORITY.LOW:
                    colorId=R.color.priorityLow;
                    break;
                default:
                    colorId=R.color.itemBackground;
            }
            holder.item.setBackgroundColor(holder.item.getResources().getColor(colorId));
        }

        if(list.get(position).getPriority()!= Constant.PRIORITY.NONE) {
            TagView priority = new TagView(context, list.get(position).getPriority());
            priority.addTag(holder.tagsBar, tagClickedListener, null);
        }
        TagView.addMultipleTags(context,holder.tagsBar,list.get(position).getId(),tagClickedListener,null);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View item;
        TextView name;
        TextView deadline;
        CheckBox completed;
        LinearLayout tagsBar;
        public ViewHolder(View itemView) {
            super(itemView);
            this.item=itemView;
            name=itemView.findViewById(R.id.item_name);
            deadline=itemView.findViewById(R.id.item_deadline);
            completed=itemView.findViewById(R.id.Completed);
            tagsBar=itemView.findViewById(R.id.tagsBar);
        }
    }
}
