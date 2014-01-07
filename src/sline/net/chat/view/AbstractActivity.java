package sline.net.chat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public abstract class AbstractActivity extends Activity {
    public static final int REQUEST_FIRST = 0;
    
    public static final String INTENT_FROM = "INTENT_FROM";
    public static final String INTENT_TO = "INTENT_TO";
    
    private Handler handler = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ke) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(RESULT_CANCELED, null);
        }
        return super.onKeyDown(keyCode, ke);
    }
    
    public void finish(int resultCode, Intent intent) {
        if (getCallingActivity() != null) {
            if (intent == null) intent = new Intent();
            intent.putExtra(INTENT_FROM, getComponentName().getClassName());
            intent.putExtra(INTENT_TO, getCallingActivity().getClassName());
            setResult(resultCode, intent);
        }
        super.finish();
    }
    public Intent getNewIntent(Context contextFrom, Class<?> classTo) {
        Intent intent = new Intent(contextFrom, classTo);
        Activity activity = (Activity)contextFrom;
        intent.putExtra(INTENT_FROM, activity.getComponentName().getClassName());
        intent.putExtra(INTENT_TO, classTo.getName());
        return intent;
    }
    
    public Handler getHandler() {
        return handler;
    }
}