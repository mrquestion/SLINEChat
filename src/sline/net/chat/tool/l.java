package sline.net.chat.tool;

import android.util.Log;

public class l {
    public static void og(Object... o) {
        StringBuilder sb = new StringBuilder("*");
        for (int i=0; i<o.length; i++) {
            sb.append(" ");
            sb.append(String.valueOf(o[i]));
        }
        Log.d("myLogd", sb.toString());
    }
}