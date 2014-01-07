package sline.net.chat.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class SocketManager {
    private static final SocketManager instance = new SocketManager();
    public static final SocketManager getInstance() {
        return instance;
    }
    
    private static final String HOST = "sline.net";
    private static final String URI = "index.php";
    private static final int TIMEOUT = 5000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    
    private String cookie = null;
    
    private SocketManager() {
        
    }
    
    public String getHttpRequestGet(Map<String, Object> map) {
        String s = null;
        
        HttpClient hc = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(hc.getParams(), TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(hc.getParams(), TIMEOUT);
        
        StringBuilder sb = new StringBuilder();
        if (map != null) {
            boolean first = true;
            for (String key: map.keySet()) {
                if (first) {
                    first = false;
                    sb.append("?");
                }
                else sb.append("&");
                sb.append(String.format("%s=%s", key, String.valueOf(map.get(key))));
//                try {
//                    sb.append(String.format("%s=%s", key, URLEncoder.encode(String.valueOf(map.get(key)), "utf-8")));
//                } catch (UnsupportedEncodingException e) {
//                    sb.append(String.format("%s=%s", key, String.valueOf(map.get(key))));
//                }
            }
        }
        
        HttpGet hg = new HttpGet(String.format("http://%s/%s%s", HOST, URI, sb.toString()));
        hg.setHeader("User-Agent", USER_AGENT);
        if (cookie != null) hg.setHeader("Cookie", cookie);
        
        try {
            HttpResponse hr = hc.execute(hg);
            
            if (cookie == null) cookie = hr.getLastHeader("Set-Cookie").getValue();
            
            HttpEntity he = hr.getEntity();
            s = EntityUtils.toString(he);
        } catch (ClientProtocolException e) {
            s = null;
        } catch (IOException e) {
            s = null;
        }
        
        return s;
    }
    public String getHttpRequestPost(Map<String, Object> map) {
        String s = null;
        
        HttpClient hc = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(hc.getParams(), TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(hc.getParams(), TIMEOUT);
        
        HttpPost hp = new HttpPost(String.format("http://%s/%s", HOST, URI));
        hp.setHeader("User-Agent", USER_AGENT);
        if (cookie != null) hp.setHeader("Cookie", cookie);
        
        if (map != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (String key: map.keySet()) {
                list.add(new BasicNameValuePair(key, String.valueOf(map.get(key))));
//                try {
//                    list.add(new BasicNameValuePair(key, URLEncoder.encode(String.valueOf(map.get(key)), "utf-8")));
//                } catch (UnsupportedEncodingException e) {
//                    list.add(new BasicNameValuePair(key, String.valueOf(map.get(key))));
//                }
            }
            try {
                hp.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
            } catch (UnsupportedEncodingException e1) {
                hp = null;
            }
        }
        
        if (hp != null) {
            try {
                HttpResponse hr = hc.execute(hp);
                
                if (cookie == null) cookie = hr.getFirstHeader("Set-Cookie").getValue();
                
                HttpEntity he = hr.getEntity();
                s = EntityUtils.toString(he);
            } catch (ClientProtocolException e) {
                s = null;
            } catch (IOException e) {
                s = null;
            }
        }
        
        return s;
    }
}