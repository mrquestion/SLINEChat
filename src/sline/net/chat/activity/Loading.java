package sline.net.chat.activity;

import sline.net.chat.R;
import sline.net.chat.view.AbstractActivity;
import sline.net.chat.view.LoadingView;
import android.os.Bundle;
import android.widget.FrameLayout;

public class Loading extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        
        IntentRunnable ir = new IntentRunnable();
        LoadingView lv = new LoadingView(Loading.this, getHandler(), ir);
        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addContentView(lv, fllp);
    }
    
    private class IntentRunnable implements Runnable {
        @Override
        public void run() {
            finish(RESULT_OK, null);
        }
    }
}