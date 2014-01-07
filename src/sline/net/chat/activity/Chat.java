package sline.net.chat.activity;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sline.net.chat.R;
import sline.net.chat.dialog.ErrorDialog;
import sline.net.chat.socket.pattern.AbstractAction;
import sline.net.chat.socket.pattern.AbstractAfterAction;
import sline.net.chat.socket.pattern.action.InitializeAction;
import sline.net.chat.socket.pattern.action.SendAction;
import sline.net.chat.socket.pattern.action.UpdateAction;
import sline.net.chat.socket.pattern.property.MessageProperty;
import sline.net.chat.view.AbstractActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Chat extends AbstractActivity {
    private static int COLOR_EVEN = Color.parseColor("#4f4f4f");
    private static int COLOR_ODD = Color.parseColor("#5f5f5f");
    private static int COLOR_ERROR = Color.parseColor("#7f3f3f");
    
//    private ToggleButton tbScroll = null, tbSound = null;
    private ImageButton ibScroll = null, ibSound = null;
    private Spinner spinner = null;
    private SelectAdapter sa = null;
    private ListView lv = null;
    private ListAdapter la = null;
    private ImageView iv = null;
    private EditText et = null;
    private Button button = null;
    
    private boolean scroll = true, sound = true;
    private int max = 200;
    private String name = null;
    private String color = null;
    private String lastID = null;
    
    private UpdateAction ua = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
        ToggleListener tl = new ToggleListener();
        
        ibScroll = (ImageButton)findViewById(R.id.toggle_chat_scroll);
        ibScroll.setOnClickListener(tl);
        ibSound = (ImageButton)findViewById(R.id.toggle_chat_sound);
        ibSound.setOnClickListener(tl);
        
//        tbScroll = (ToggleButton)findViewById(R.id.toggle_chat_scroll);
//        tbScroll.setOnCheckedChangeListener(tl);
//        tbSound = (ToggleButton)findViewById(R.id.toggle_chat_sound);
//        tbSound.setOnCheckedChangeListener(tl);
        
        SelectListener sl = new SelectListener();
        
        spinner = (Spinner)findViewById(R.id.select_chat_count);
        spinner.setOnItemSelectedListener(sl);
        sa = new SelectAdapter(Chat.this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.list_max));
        spinner.setAdapter(sa);
        for (int i=0, max=spinner.getCount(); i<max; i++) {
            String s = String.valueOf(spinner.getItemAtPosition(i));
            if (s.equalsIgnoreCase(String.valueOf(this.max))) {
                spinner.setSelection(i);
            }
        }
        
        lv = (ListView)findViewById(R.id.list_chat_log);
        la = new ListAdapter(this, R.layout.chat_list);
        lv.setAdapter(la);
        lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        
        MessageProperty mp = new MessageProperty();
        mp.setUsername("SYSTEM");
        mp.setTime(TimeStamp.format("HH:mm"));
        mp.setText("메인 채널입니다.");
        la.add(mp);
        
        iv = (ImageView)findViewById(R.id.image_chat_color);
        iv.setBackgroundColor(Color.parseColor("#bfbfbf"));
        
        InputListener il = new InputListener();
        
        et = (EditText)findViewById(R.id.input_chat_message);
        et.setOnClickListener(il);
        
        button = (Button)findViewById(R.id.button_chat_send);
        button.setOnClickListener(il);
        
        AbstractAction aa = new InitializeAction(getHandler());
        aa.add(new InitializeAfterAction(aa));
        aa.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuListener ml = new MenuListener();
        menu.add(R.string.chat_color_button).setOnMenuItemClickListener(ml);
        menu.add(R.string.chat_name_button).setOnMenuItemClickListener(ml);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        if (scroll) lv.setSelection(la.getCount()-1);
    }
    @Override
    protected void onDestroy() {
        ua.exit();
        try { ua.join(5000); } catch (InterruptedException e) { }
        super.onDestroy();
    }
    
//    private class ToggleListener implements CompoundButton.OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        }
//    }
    private class ToggleListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v instanceof ImageButton) {
                ImageButton ib = (ImageButton)v;
                if (ib.getId() == R.id.toggle_chat_scroll) {
                    scroll = !scroll;
                    if (scroll) ib.setImageResource(R.drawable.scroll_on);
                    else ib.setImageResource(R.drawable.scroll_off);
                }
                else if (ib.getId() == R.id.toggle_chat_sound) {
                    sound = !sound;
                    if (sound) ib.setImageResource(R.drawable.sound_on);
                    else ib.setImageResource(R.drawable.sound_off);
                }
            }
        }
    }
    private class SelectAdapter extends ArrayAdapter<String> {
        private int color = 0;
        
        private SelectAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            
            color = Color.parseColor("#ffffff");
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = getLayoutInflater().inflate(android.R.layout.simple_dropdown_item_1line, null);
            
            TextView tv = (TextView)view.findViewById(android.R.id.text1);
            tv.setText(getItem(position));
            tv.setTextColor(color);
            
            return view;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            
            TextView tv = (TextView)view.findViewById(android.R.id.text1);
            tv.setText(getItem(position));
            
            return view;
        }
    }
    private class SelectListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String s = sa.getItem(position);
            try {
                max = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                max = 200;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }
    private class ListAdapter extends ArrayAdapter<MessageProperty> {
        private ListAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = getLayoutInflater().inflate(R.layout.chat_list, null);
            
            MessageProperty mp = getItem(position);
            
//            int color = (position%2 == 0) ? Color.parseColor("#4f4f4f") : Color.parseColor("#5f5f5f");
            int color = 0;
            if (mp.getId() == null) color = COLOR_ERROR;
            else {
                int id = 0;
                try { id = Integer.parseInt(mp.getId()); } catch (NumberFormatException e) { }
                color = (id%2 == 0) ? COLOR_EVEN : COLOR_ODD;
            }
            
            TextView tv = (TextView)view.findViewById(R.id.text_chat_list_time);
            tv.setText(String.format("(%s)", mp.getTime()));
            tv.setBackgroundColor(color);
            
            tv = (TextView)view.findViewById(R.id.text_chat_list_name);
            tv.setText(mp.getUsername());
            tv.setBackgroundColor(color);
            if (mp.getUsername().equalsIgnoreCase("SexyBot") && mp.getUserid().equalsIgnoreCase("2147483647")) {
                tv.setTextColor(Color.parseColor("#ff0000"));
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            }
            else tv.setTextColor(Color.parseColor("#bfbfbf"));
            
            tv = (TextView)view.findViewById(R.id.text_chat_list_message);
            tv.setText(mp.getText());
            tv.setTextColor(mp.getColor());
            tv.setBackgroundColor(color);
            
            return view;
        }
    }
    private class InputListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String message = et.getText().toString();
            if (message.trim().length() > 0) {
                if (color != null) message = String.format("[color=%s]%s[/color]", color, message);
                AbstractAction aa = new SendAction(getHandler(), lastID, message);
                aa.add(new SendAfterAction(aa));
                aa.start();
            }
            et.setText(R.string.empty);
        }
    }
    private class MenuListener implements MenuItem.OnMenuItemClickListener {
        private EditText et = null;
        
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
            et = (EditText)view.findViewById(R.id.input_dialog_text);
            
            String title = String.valueOf(item.getTitle());
            if (title.equals(getString(R.string.chat_color_button))) {
                new AlertDialog.Builder(Chat.this)
                .setView(view)
                .setTitle("글자색 변경")
                .setMessage("변경할 글자색을 입력하세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        color = et.getText().toString();
                        if (color.matches("#[0-9A-Fa-f]{6}")) iv.setBackgroundColor(Color.parseColor(color));
                        else {
                            color = null;
                            ErrorDialog.show(Chat.this, "오류", "올바르지 않은 색상코드입니다.");
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
            }
            else if (title.equals(getString(R.string.chat_name_button))) {
                new AlertDialog.Builder(Chat.this)
                .setView(view)
                .setTitle("대화명 변경")
                .setMessage("변경할 대화명을 입력하세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = et.getText().toString();
                        AbstractAction aa = new SendAction(getHandler(), lastID, String.format("/nick %s", name));
                        aa.add(new SendAfterAction(aa));
                        aa.start();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
            }
            return false;
        }
    }
    private class InitializeAfterAction extends AbstractAfterAction {
        private InitializeAfterAction(AbstractAction aa) {
            super(aa);
        }
        @Override
        public void run() {
            InitializeAction ia = (InitializeAction)getAction();
            Document document = ia.getDocument();
            NodeList messages = document.getElementsByTagName("message");
            
            for (int i=0, max=messages.getLength(); i<max; i++) {
                Node message = messages.item(i);
                NamedNodeMap nnm = message.getAttributes();
                
                Node username = message.getFirstChild();
                Node text = username.getNextSibling();
//                Element messageElement = (Element)message;
//                NodeList nl = messageElement.getElementsByTagName("username");
//                Node username = nl.getLength() > 0 ? nl.item(0) : null;
//                nl = messageElement.getElementsByTagName("text");
//                Node text = nl.getLength() > 0 ? nl.item(0) : null;
                if (username != null && text != null) {
                    Node id = nnm.getNamedItem("id");
                    Node dateTime = nnm.getNamedItem("dateTime");
                    Matcher matcher = Pattern.compile("[A-Za-z]+, [0-9]+ [A-Za-z]+ [0-9]+ ([0-9]+):([0-9]+):[0-9]+ ([-0-9]+)").matcher(dateTime.getNodeValue());
                    String time = null;
                    if (matcher.find()) {
                        String hourString = matcher.group(1);
                        String minuteString = matcher.group(2);
                        String gmtString = matcher.group(3);
                        int hour = Integer.parseInt(hourString);
                        int minute = Integer.parseInt(minuteString);
                        int gmt = Integer.parseInt(gmtString);
                        hour += 9 - gmt / 100;
                        if (hour < 0) hour += 24;
                        time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                    }
                    String s = text.getTextContent(), color = "#bfbfbf";
                    if (s != null) {
                        matcher = Pattern.compile("^\\[color=(#[0-9A-Fa-f]+)\\](.*)\\[/color\\]$").matcher(s);
                        if (matcher.find()) {
                            color = matcher.group(1);
                            s = matcher.group(2);
                        }
                    }
                    s = StringEscapeUtils.unescapeHtml4(s);
                    Node userID = nnm.getNamedItem("userID");
                    MessageProperty mp = new MessageProperty();
                    mp.setId(id.getNodeValue());
                    mp.setTime(time);
                    mp.setUserid(userID.getNodeValue());
                    mp.setUsername(username.getTextContent());
                    mp.setText(s);
                    mp.setColor(Color.parseColor(color));
                    la.add(mp);
                    
                    lastID = mp.getId();
                }
            }
            la.notifyDataSetChanged();
            //la.notifyDataSetInvalidated();
            if (scroll) lv.setSelection(la.getCount()-1);
            
            ua = new UpdateAction(getHandler(), lastID);
            ua.add(new UpdateAfterAction(ua));
            ua.start();
        }
    }
    private class UpdateAfterAction extends AbstractAfterAction {
        public UpdateAfterAction(AbstractAction aa) {
            super(aa);
        }
        @Override
        public void run() {
            UpdateAction ua = (UpdateAction)getAction();
            Document document = ua.getDocument();
            NodeList messages = document.getElementsByTagName("message");
            
            boolean changed = false;
            
            for (int i=0, max=messages.getLength(); i<max; i++) {
                Node message = messages.item(i);
                NamedNodeMap nnm = message.getAttributes();
                
                Node username = message.getFirstChild();
                Node text = username.getNextSibling();
                if (username != null && text != null) {
                    Node id = nnm.getNamedItem("id");
                    Node dateTime = nnm.getNamedItem("dateTime");
                    Matcher matcher = Pattern.compile("[A-Za-z]+, [0-9]+ [A-Za-z]+ [0-9]+ ([0-9]+):([0-9]+):[0-9]+ ([-0-9]+)").matcher(dateTime.getNodeValue());
                    String time = null;
                    if (matcher.find()) {
                        String hourString = matcher.group(1);
                        String minuteString = matcher.group(2);
                        String gmtString = matcher.group(3);
                        int hour = Integer.parseInt(hourString);
                        int minute = Integer.parseInt(minuteString);
                        int gmt = Integer.parseInt(gmtString);
                        hour += 9 - gmt / 100;
                        if (hour < 0) hour += 24;
                        time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                    }
                    String s = text.getTextContent(), color = "#bfbfbf";
                    if (s != null) {
                        matcher = Pattern.compile("^\\[color=(#[0-9A-Fa-f]+)\\](.*)\\[/color\\]$").matcher(s);
                        if (matcher.find()) {
                            color = matcher.group(1);
                            s = matcher.group(2);
                        }
                    }
                    s = StringEscapeUtils.unescapeHtml4(s);
                    Node userID = nnm.getNamedItem("userID");
                    MessageProperty mp = new MessageProperty();
                    mp.setId(id.getNodeValue());
                    mp.setTime(time);
                    mp.setUserid(userID.getNodeValue());
                    mp.setUsername(username.getTextContent());
                    mp.setText(s);
                    mp.setColor(Color.parseColor(color));
                    la.add(mp);
                    
                    lastID = mp.getId();
                }
                
                changed = true;
            }
            
            while (la.getCount() > max) la.remove(la.getItem(0));
            
            la.notifyDataSetChanged();
            //la.notifyDataSetInvalidated();
            if (scroll && changed) lv.setSelection(la.getCount()-1);
            
            ua.setLastID(lastID);
        }
    }
    private class SendAfterAction extends AbstractAfterAction {
        public SendAfterAction(AbstractAction aa) {
            super(aa);
        }
        @Override
        public void run() {
            SendAction ua = (SendAction)getAction();
            Document document = ua.getDocument();
            NodeList messages = document.getElementsByTagName("message");
            
            for (int i=0, max=messages.getLength(); i<max; i++) {
                Node message = messages.item(i);
                NamedNodeMap nnm = message.getAttributes();
                
                Node username = message.getFirstChild();
                Node text = username.getNextSibling();
                if (username != null && text != null) {
                    Node id = nnm.getNamedItem("id");
                    Node dateTime = nnm.getNamedItem("dateTime");
                    Matcher matcher = Pattern.compile("[A-Za-z]+, [0-9]+ [A-Za-z]+ [0-9]+ ([0-9]+):([0-9]+):[0-9]+ ([-0-9]+)").matcher(dateTime.getNodeValue());
                    String time = null;
                    if (matcher.find()) {
                        String hourString = matcher.group(1);
                        String minuteString = matcher.group(2);
                        String gmtString = matcher.group(3);
                        int hour = Integer.parseInt(hourString);
                        int minute = Integer.parseInt(minuteString);
                        int gmt = Integer.parseInt(gmtString);
                        hour += 9 - gmt / 100;
                        if (hour < 0) hour += 24;
                        time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                    }
                    String s = text.getTextContent(), color = "#bfbfbf";
                    if (s != null) {
                        matcher = Pattern.compile("^\\[color=(#[0-9A-Fa-f]+)\\](.*)\\[/color\\]$").matcher(s);
                        if (matcher.find()) {
                            color = matcher.group(1);
                            s = matcher.group(2);
                        }
                    }
                    s = StringEscapeUtils.unescapeHtml4(s);
                    Node userID = nnm.getNamedItem("userID");
                    MessageProperty mp = new MessageProperty();
                    mp.setId(id.getNodeValue());
                    mp.setTime(time);
                    mp.setUserid(userID.getNodeValue());
                    mp.setUsername(username.getTextContent());
                    mp.setText(s);
                    mp.setColor(Color.parseColor(color));
                    la.add(mp);
                    
                    lastID = mp.getId();
                }
            }
            
            while (la.getCount() > max) la.remove(la.getItem(0));
            
            la.notifyDataSetChanged();
            //la.notifyDataSetInvalidated();
            if (scroll) lv.setSelection(la.getCount()-1);
        }
    }
}