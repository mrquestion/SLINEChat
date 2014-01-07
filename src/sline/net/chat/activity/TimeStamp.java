package sline.net.chat.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeStamp {
    private static final TimeZone tz = TimeZone.getTimeZone("GMT+9");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    static { sdf.setTimeZone(tz); }
    
    public static String get() {
        return sdf.format(new Date());
    }
    public static String format(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(s, Locale.US);
        sdf.setTimeZone(tz);
        return sdf.format(new Date());
    }
}