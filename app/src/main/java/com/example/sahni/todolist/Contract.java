package com.example.sahni.todolist;

import android.arch.lifecycle.LifecycleRegistry;

/**
 * Created by sahni on 17/2/18.
 */

public class Contract {
    public static final String DATABASE_NAME="ToDoList";
    public static final int VERSION=1;
    static class ItemList{
        public static final String TABLE_NAME="list";
        public static final String ITEM="item";
        public static final String ID="id";
        public static final String DESCRIPTION="description";
        public static final String DEADLINE="deadline";
        public static final String PRIORITY="priority";
    }
    static class TagsList{
        public static final String TABLE_NAME="tag_list";
        public static final String TAG="tag";
        public static final String ID="id";
    }
    static class TagAssignment{
        public static final String TABLE_NAME="tag_assignment";
        public static final String ID="id";
        public static final String TAG_ID="tag_id";
        public static final String ITEM_ID="item_id";
    }
    static class Comments{
        public static final String TABLE_NAME="comments";
        public static final String ID="id";
        public static final String COMMENT="comment";
        public static final String DATE="date";
        public static final String ITEM_ID="item_id";
    }
}
