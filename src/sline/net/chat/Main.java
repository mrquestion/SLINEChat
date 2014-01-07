package sline.net.chat;

import sline.net.chat.dialog.ErrorDialog;
import sline.net.chat.view.AbstractActivity;
import android.content.Intent;
import android.os.Bundle;

public class Main extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getNewIntent(Main.this, Handle.class);
        startActivityForResult(intent, Handle.REQUEST_FIRST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String intentFrom = data.getStringExtra(INTENT_FROM);
            if (intentFrom == null) ErrorDialog.exit(Main.this, "애플리케이션 오류", "잘못된 접근입니다.");
            else if (intentFrom.equals(Handle.class.getName())) {
                finish();
                System.exit(0);
            }
            else ErrorDialog.exit(Main.this, "애플리케이션 오류", "잘못된 접근입니다.");
        }
    }
}