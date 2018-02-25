package com.example.sahni.todolist;

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
    }
}
