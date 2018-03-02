package com.example.sahni.todolist;

import java.util.ArrayList;

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
    static class MenuID {
        public static final int ADD=1;
        public static final int SAVE=2;
        public static final int EDIT=3;
    }
    public static final ArrayList<String> MONTHS=setMonth();
    private static ArrayList<String> setMonth() {
        ArrayList<String> month=new ArrayList<>();
        month.add("JAN");
        month.add("FEB");
        month.add("MARCH");
        month.add("APRIL");
        month.add("MAY");
        month.add("JUNE");
        month.add("JULY");
        month.add("AUG");
        month.add("SEPT");
        month.add("OCT");
        month.add("NOV");
        month.add("DEC");
        return month;
    }

    public static final ArrayList<String> DAYS=IntSet(1,31);
    public static final ArrayList<String> YEARS=IntSet(2018,2025);
    private static ArrayList<String> IntSet(int start, int end)
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
