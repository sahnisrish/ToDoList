package com.example.sahni.todolist;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sahni on 17/2/18.
 */

public class Constant {
    public static final int REQUEST_ADD=1;
    public static final int RSULT_ADD=1;
    public static final int REQUEST_EDIT=2;
    public static final int RSULT_EDIT=2;
    public static final String ID_KEY="id";
    public static final String REQUEST_KEY="request";
    public static final String EDIT="edit";
    public static final String  TAG_ID="tag_id";
    public static final String TAG="tag";
    public static final String HAS_TAG="hasTag";
    public static final SimpleDateFormat format=new SimpleDateFormat("dd/MMM/yyyy");
    public static final java.lang.String HAS_PRIORITY = "hasPriority";
    public static java.lang.String Priority="priority";

    static class MenuID {
        public static final int ADD=1;
        public static final int SAVE=2;
        public static final int EDIT=3;
    }
    class PRIORITY{
        public static final int HIGH=3;
        public static final int MED=2;
        public static final int LOW=1;
        public static final int NONE=0;
    }
    public static ArrayList<String> setMonth() {
        ArrayList<String> month=new ArrayList<>();
        month.add("Jan");
        month.add("Feb");
        month.add("Mar");
        month.add("Apr");
        month.add("May");
        month.add("Jun");
        month.add("Jul");
        month.add("Aug");
        month.add("Sep");
        month.add("Oct");
        month.add("Nov");
        month.add("Dec");
        return month;
    }
    public static ArrayList<String> IntSet(int start, int end)
    {
        ArrayList<String> strings=new ArrayList<>();
        for(int i=start;i<=end;i++) {
            if(i>9)
                strings.add(i + "");
            else
                strings.add("0"+i);
        }
        return strings;
    }
}
