package sline.net.chat.socket.pattern.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import sline.net.chat.socket.SocketManager;
import sline.net.chat.socket.pattern.AbstractAction;
import android.os.Handler;

public class UpdateAction extends AbstractAction {
    private String lastID = null;
    private Document document = null;
    private boolean run = false;
    
    public UpdateAction(Handler handler, String lastID) {
        super(handler);
        
        this.lastID = lastID;
    }
    @Override
    public void run() {
        SocketManager sm = SocketManager.getInstance();
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ajax", true);
        
        run = true;
        while (run) {
            map.put("lastID", lastID);
            String xml = sm.getHttpRequestGet(map);
            
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputStream is = new ByteArrayInputStream(xml.getBytes());
                document = db.parse(is);
                is.close();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            super.run();
            
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
    
    public void exit() {
        run = false;
    }
    
    public void setLastID(String lastID) {
        this.lastID = lastID;
    }
    public Document getDocument() {
        return document;
    }
}