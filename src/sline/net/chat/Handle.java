package sline.net.chat;

import sline.net.chat.activity.Chat;
import sline.net.chat.activity.Loading;
import sline.net.chat.dialog.ErrorDialog;
import sline.net.chat.view.AbstractActivity;
import android.content.Intent;
import android.os.Bundle;

public class Handle extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getNewIntent(Handle.this, Loading.class);
        startActivityForResult(intent, Loading.REQUEST_FIRST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String intentFrom = data.getStringExtra(INTENT_FROM);
            if (intentFrom == null) ErrorDialog.exit(Handle.this, "애플리케이션 오류", "잘못된 접근입니다.");
            else if (intentFrom.equals(Loading.class.getName())) {
                switch (resultCode) {
                    case RESULT_OK:
                      Intent intent = getNewIntent(Handle.this, Chat.class);
                      startActivityForResult(intent, Chat.REQUEST_FIRST);
                        break;
                    case RESULT_CANCELED:
                        finish(resultCode, null);
                        break;
                }
            }
            else if (intentFrom.equals(Chat.class.getName())) {
                switch (resultCode) {
                    case RESULT_OK:
                        break;
                    case RESULT_CANCELED:
                        finish(resultCode, null);
                        break;
                }
            }
            else ErrorDialog.exit(Handle.this, "애플리케이션 오류", "잘못된 접근입니다.");
        }
    }
}