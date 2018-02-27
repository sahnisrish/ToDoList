package com.example.sahni.todolist;

import java.util.ArrayList;

/**
 * Created by sahni on 16/2/18.
 */

public class ListItem {
    private String itemName;
    private Integer deadLine;
    int id;

    ListItem(String itemName, Integer deadLine, int id) {
        this.itemName = itemName;
        this.deadLine = deadLine;
        this.id = id;

    }

    public String getDeadLine() {
        String string = deadLine.toString();
        int month = Integer.parseInt(deadLine.toString().substring(4, 6));
        string = string.substring(6, 8) + "/" + Constant.MONTHS.get(month) + "/" + string.substring(0, 4);
        return string;
    }
    public Integer getDeadLineInt(){
        return deadLine;
    }
    public boolean liesAbove(ListItem li) {
        return li.deadLine >= deadLine;
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

}
