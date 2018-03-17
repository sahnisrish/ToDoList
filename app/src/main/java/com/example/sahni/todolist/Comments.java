package com.example.sahni.todolist;

import java.util.Date;

/**
 * Created by sahni on 3/3/18.
 */

public class Comments {
    private int id;
    private String comment;
    private int item_id;
    private Long date;
    Comments(int id,String comment,int item_id,Long date)
    {
        this.id=id;
        this.comment=comment;
        this.item_id=item_id;
        this.date=date;
    }
    Comments(String comment,Long date)
    {
        this.id=Constant.NOT_IN_DB;
        this.comment=comment;
        this.item_id=Constant.NOT_IN_DB;
        this.date=date;
    }
    public Long getDateLong() {
        return date;
    }

    public String getDate(){
        Date date=new Date(this.date);
        return Constant.format.format(date);
    }
    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public int getItem_id() {
        return item_id;
    }
}
