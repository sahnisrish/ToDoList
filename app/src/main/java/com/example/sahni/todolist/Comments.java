package com.example.sahni.todolist;

/**
 * Created by sahni on 3/3/18.
 */

public class Comments {
    private int id;
    private String comment;
    private int item_id;
    Comments(int id,String comment,int item_id)
    {
        this.id=id;
        this.comment=comment;
        this.item_id=item_id;
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
