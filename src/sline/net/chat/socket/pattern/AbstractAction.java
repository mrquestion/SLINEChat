package sline.net.chat.socket.pattern;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public abstract class AbstractAction extends Thread {
    private Handler handler = null;
    private List<AbstractAfterAction> list = null;
    
    public AbstractAction(Handler handler) {
        this.handler = handler;
        
        list = new ArrayList<AbstractAfterAction>();
    }
    @Override
    public void run() {
        for (int i=0, max=size(); i<max; i++) {
            AbstractAfterAction aaa = get(i);
            handler.postDelayed(aaa, aaa.getDelay());
        }
    }
    
    public void add(AbstractAfterAction aaa) {
        list.add(aaa);
    }
    public AbstractAfterAction get(int i) {
        AbstractAfterAction aaa = null;
        if (i >= 0 && i < list.size()) aaa = list.get(i);
        return aaa;
    }
    public int size() {
        return list.size();
    }
    
    public Handler getHandler() {
        return handler;
    }
}