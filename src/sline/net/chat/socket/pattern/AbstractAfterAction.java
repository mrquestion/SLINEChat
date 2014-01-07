package sline.net.chat.socket.pattern;

public abstract class AbstractAfterAction implements Runnable {
    private AbstractAction aa = null;
    private int delay = 0;
    
    public AbstractAfterAction(AbstractAction aa) {
        this(aa, 0);
    }
    public AbstractAfterAction(AbstractAction aa, int delay) {
        this.aa = aa;
        this.delay = delay;
    }
    
    public AbstractAction getAction() {
        return aa;
    }
    public int getDelay() {
        return delay;
    }
}