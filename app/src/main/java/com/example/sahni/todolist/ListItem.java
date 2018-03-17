package com.example.sahni.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sahni on 16/2/18.
 */

public class ListItem {
    private String itemName;
    private Long deadLine;
    private int priority;
    int id;

    ListItem(String itemName, Long deadLine, int priority, int id) {
        this.itemName = itemName;
        this.deadLine = deadLine;
        this.id = id;
        this.priority=priority;

    }

    public String getDeadLine() {
        Date date=new Date(deadLine);
        return Constant.format.format(date);
    }
    public Long getDeadLineLong(){
        return deadLine;
    }
    public boolean liesAbove(ListItem li) {
        if(priority>li.getPriority())
            return true;
        else if(priority==li.getPriority()&&li.getDeadLineLong()>deadLine)
            return true;
        else
            return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPriority() {
        return priority;
    }
}
