package com.example.chatsooketphp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {


    private WebSocket webSocket;
    TextView send;
    EditText messsageBox;
    MessageAdapter adapter;
    ListView messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        InstanceChat();

        adapter = new MessageAdapter();
        messageList.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message =  messsageBox.getText().toString();
                if (!message.isEmpty()){
                    webSocket.send(message);
                    messsageBox.setText("");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message" , message);
                        jsonObject.put("byServer", false);
                        adapter.addItem(jsonObject);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        

    }

    private void InstanceChat() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.1.1").build();
        SoketListener soketListener = new SoketListener(this);
        webSocket = client.newWebSocket(request, soketListener);

    }
    public class SoketListener extends WebSocketListener {
        public MainActivity activity;

        public SoketListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "conection published", Toast.LENGTH_SHORT).show(); 
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", text);
                        jsonObject.put("byServer", true);

                        adapter.addItem(jsonObject);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
        }
    }
    public class MessageAdapter extends BaseAdapter{
        List<JSONObject> messageList = new ArrayList<>();

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int position) {
            return messageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView=getLayoutInflater().inflate(R.layout.message_list_item,parent,false);
                TextView receiveMessage=convertView.findViewById(R.id.receiveMessage);
                TextView setMessage=convertView.findViewById(R.id.setMessage);

                JSONObject item = messageList.get(position);
                try {
                    if(item.getBoolean("byServer")) {
                        receiveMessage.setVisibility(View.VISIBLE);
                        receiveMessage.setText(item.getString("message"));
                        setMessage.setVisibility(View.INVISIBLE);
                    }else {
                        receiveMessage.setVisibility(View.VISIBLE);
                        setMessage.setText(item.getString("message"));
                        receiveMessage.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return convertView;
        }
        void addItem(JSONObject item ){
            messageList.add(item);
            notifyDataSetChanged();

        }
    }

    private void findView() {
        send = findViewById(R.id.send);
        messsageBox = findViewById(R.id.messsageBox);
        messageList = findViewById(R.id.messageList);


    }
}