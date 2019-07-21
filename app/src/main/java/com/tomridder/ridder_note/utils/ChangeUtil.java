package com.tomridder.ridder_note.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeUtil
{

    public static String longToString(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string=format1.format(date);
        return  string;
    }

    public static long stringToLong(String strTime)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        try
        {
         date=formatter.parse(strTime);
        }catch (ParseException e)
        {
            e.printStackTrace();
        }
        if(date!=null)
        {
            return date.getTime();
        }
        return 0;
    }
}
