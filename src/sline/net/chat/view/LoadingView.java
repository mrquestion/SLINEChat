package sline.net.chat.view;

import sline.net.chat.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LoadingView extends SurfaceView implements SurfaceHolder.Callback {
    private Handler handler = null;
    private Runnable runnable = null;
    
    private BitmapDrawable bd = null;
    private FadeRunnable fr = null;
    private boolean finish = false;
    
    public LoadingView(Context context, Handler handler, Runnable runnable) {
        super(context);
        
        this.handler = handler;
        this.runnable = runnable;
        
        getHolder().addCallback(this);
    }
    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bd = (BitmapDrawable)getResources().getDrawable(R.drawable.loading_logo);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder != null && finish == false) {
            fr = new FadeRunnable(holder, width, height);
            handler.post(fr);
            finish = true;
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        handler.removeCallbacks(fr);
    }
    
    private class FadeRunnable implements Runnable {
        private SurfaceHolder sh = null;
        private Bitmap bitmap = null;
        private int width = 0, height = 0;
        private int opacity = 0;
        private boolean fadeout = false;
        
        private FadeRunnable(SurfaceHolder sh, double width, double height) {
            this.sh = sh;
            this.width = (int)width;
            this.height = (int)height;
            
            bitmap = bd.getBitmap();
            if (bitmap.getWidth() > width) {
                int scaledWidth = (int)(width - 100);
                double ratio = (double)bitmap.getWidth() / scaledWidth;
                int scaledHeight = (int)(bitmap.getHeight() / ratio);
                bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
            }
        }
        @Override
        public void run() {
            Canvas canvas = sh.lockCanvas();
            if (canvas != null) {
                Paint paint = new Paint();
                paint.setColor(Color.rgb(0, 0, 0));
                canvas.drawRect(0, 0, width, height, paint);
                
                paint.setColor(Color.rgb(255, 255, 255));
                paint.setAlpha(opacity);
                canvas.drawRect(0, 0, width, height, paint);
                
                int left = (width - bitmap.getWidth()) / 2;
                int top = (height - bitmap.getHeight()) / 2;
                canvas.drawBitmap(bitmap, left, top-100, null);
                
                sh.unlockCanvasAndPost(canvas);
                
                if (fadeout) opacity -= 20;
                else opacity += 10;
                
                if (opacity >= 255) {
                    opacity = 255;
                    fadeout = true;
                }
                
                if (fadeout) {
                    if (opacity <= 0) handler.post(runnable);
                    else if (opacity >= 255) handler.postDelayed(this, 500);
                    else handler.postDelayed(this, 10);
                }
                else handler.postDelayed(this, 10);
            }
        }
    }
}
