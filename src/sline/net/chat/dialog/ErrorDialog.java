package sline.net.chat.dialog;

import sline.net.chat.view.AbstractActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {
    public static void show(Context context, String title, String message) {
        new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton("확인", null)
        .setCancelable(false)
        .show();
    }
    public static void exit(Context context, String title, String message) {
        DialogListener dl = new DialogListener();
        dl.setContext(context);
        
        new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton("확인", dl)
        .setCancelable(false)
        .show();
    }
    private static class DialogListener implements DialogInterface.OnClickListener {
        private Context context = null;
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AbstractActivity aa = (AbstractActivity)context;
            aa.finish(AbstractActivity.RESULT_CANCELED, null);
            System.exit(0);
        }
        
        public void setContext(Context context) {
            this.context = context;
        }
    }
    /*
    public static void show(Context context, String title, String message) {
        show(context, title, message, false);
    }
    public static void show(final Context context, Handler handler, final String title, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                show(context, title, message, false);
            }
        });
    }
    public static void showFinish(Context context, Handler handler, String title, String message) {
        Activity activity = (Activity)context;
        showFinish(activity, handler, title, message);
    }
    public static void showFinish(Activity activity, String title, String message) {
        show(activity, title, message, true);
    }
    public static void showFinish(final Activity activity, Handler handler, final String title, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                show(activity, title, message, true);
            }
        });
    }
    private static void show(final Context context, String title, String message, final boolean exit) {
        new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (exit) {
                    AbstractActivity aa = (AbstractActivity)context;
                    aa.finish(AbstractActivity.RESULT_CANCELED, null);
                    System.exit(0);
                }
            }
        })
        .setCancelable(false)
        .show();
    }*/
}