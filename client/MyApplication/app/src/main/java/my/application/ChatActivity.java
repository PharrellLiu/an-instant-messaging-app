package my.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {
    /**
     * this is the chat activity, both chatroom and private chat use it
     * therefore, there may be a lot of conditional branch
     */

    @ViewInject(R.id.chat_recycler_view)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ChatMessageAdapter.ChatMessage> myDataset = new ArrayList<ChatMessageAdapter.ChatMessage>();

    @ViewInject(R.id.edittext_chat)
    private EditText input_edittext;
    @ViewInject(R.id.chat_refreshLayout)
    private RefreshLayout mRefreshLayout;
    @ViewInject(R.id.button_send)
    private Button button_send;

    private String messageTimeLine;
    private String url;
    private int isChatroom;
    private String nameOfChatroomOrFri;
    private String userName;
    // if it is the init of a new chatroom, when we press the back button, it should not back to create new chatroom activity but home activity
    private int isNewChatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(ChatActivity.this);

        // init actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get the information, if now is chatroom or private chat, and the name of friend or chatroom
        Intent intent = getIntent();
        isNewChatroom = intent.getIntExtra("isNewChatroom",-1);
        isChatroom = intent.getIntExtra("isChatroom",-1);
        nameOfChatroomOrFri = intent.getStringExtra("nameOfChatroomOrFri");
        this.setTitle(nameOfChatroomOrFri);

        // send message to the home activity for the socketio function
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new EventBusMsg.ChatActivityStart(isChatroom,nameOfChatroomOrFri));

        // init recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatMessageAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        // init the messageTimeLine, as there is no messages here
        // messageTimeLine is uesd to load more messages, based on the messageTimeLine, server selects the previous messages that have not been loaded
        messageTimeLine = "0";

        // get the user's name
        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

        // init the url, to chatroom or private chat
        if (isChatroom == 1) {
            url = URLCollection.GET_CHATROOM_MESSAGES;
        } else {
            url = URLCollection.GET_PRIVATE_CHAT_MESSAGES;
        }

        // post's content
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("messageTimeLine",messageTimeLine);
        if (isChatroom == 1) {
            params.addBodyParameter("chatroom",nameOfChatroomOrFri);
        } else {
            params.addBodyParameter("name1",nameOfChatroomOrFri);
            params.addBodyParameter("name2",userName);
        }

        // acquire initial messages
        x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        setChatMessage(result,isChatroom,userName);
                        mAdapter.notifyDataSetChanged();
                        messageTimeLine = myDataset.get(0).getTime(); // record the last message's time
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) { Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show(); }
                @Override
                public void onCancelled(CancelledException cex) {}
                @Override
                public void onFinished() {}
        });

        // the load more function
        // since the refresh layout has named the scrolling up function as refresh
        // therefore, although the name of the method is refresh, the real function is to load more messages
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("messageTimeLine",messageTimeLine);
                if (isChatroom == 1) {
                    params.addBodyParameter("chatroom",nameOfChatroomOrFri);
                } else {
                    params.addBodyParameter("name1",nameOfChatroomOrFri);
                    params.addBodyParameter("name2",userName);
                }
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            setChatMessage(result,isChatroom,userName);
                            mAdapter.notifyDataSetChanged();
                            messageTimeLine = myDataset.get(0).getTime(); // record the last message's time
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) { Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show(); }
                    @Override
                    public void onCancelled(CancelledException cex) {}
                    @Override
                    public void onFinished() {}
                });
                refreshlayout.finishRefresh();
            }
        });

    }

    // this method is to set the message, filter the content we need and set to the messages' list
    public void setChatMessage(String result, int isChatroom, String username) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        int len = jsonArray.length();
        if (isChatroom == 1) {
            for (int i = 0; i<len; i++){
                JSONObject temp = jsonArray.getJSONObject(i);
                String name = temp.getString("name");
                if (name.equals(username)){
                    myDataset.add(0, new ChatMessageAdapter.ChatMessage(name,temp.getString("message_time"),
                            temp.getString("message"),1));
                } else {
                    myDataset.add(0, new ChatMessageAdapter.ChatMessage(name,temp.getString("message_time"),
                            temp.getString("message"),0));
                }
            }
        } else {
            for (int i = 0; i<len; i++){
                JSONObject temp = jsonArray.getJSONObject(i);
                String name = temp.getString("sendname");
                if (name.equals(username)){
                    myDataset.add(0, new ChatMessageAdapter.ChatMessage(name,temp.getString("message_time"),
                            temp.getString("message"),1));
                } else {
                    myDataset.add(0, new ChatMessageAdapter.ChatMessage(name,temp.getString("message_time"),
                            temp.getString("message"),0));
                }
            }
        }
    }

    // to receive the message from socketio
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgToChat(EventBusMsg.PushMsgToChat pushMsgToChat) {
        myDataset.add(new ChatMessageAdapter.ChatMessage(pushMsgToChat.getSendName(),pushMsgToChat.getMessageTime(),
                pushMsgToChat.getMessage(),0));
        mAdapter.notifyDataSetChanged();
    }

    // send message
    @Event(value = R.id.button_send)
    private void onClickSendButton(View view) {
        final String message = input_edittext.getText().toString();
        if (message.length() == 0){
            Toast.makeText(x.app(), "input something",Toast.LENGTH_SHORT).show();
        } else {
            String url = "";
            if (isChatroom == 1) {
                url = URLCollection.POST_CHATROOM_MESSAGE;
            } else {
                url = URLCollection.POST_PRIVATE_CHAT_MESSAGE;
            }
            RequestParams params = new RequestParams(url);
            params.addBodyParameter("message",message);
            params.addBodyParameter("sendname",userName);
            if (isChatroom == 1) {
                params.addBodyParameter("chatroom",nameOfChatroomOrFri);
            } else {
                params.addBodyParameter("receivename",nameOfChatroomOrFri);
            }
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) { // succeed
                            String messageTime = jsonObject.getString("message_time");
                            myDataset.add(new ChatMessageAdapter.ChatMessage(userName, messageTime,message,1));
                            mAdapter.notifyDataSetChanged();
                            input_edittext.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) { Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show(); }
                @Override
                public void onCancelled(CancelledException cex) {}
                @Override
                public void onFinished() {}
            });
        }
    }

    // the return button on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventBus.getDefault().post(new EventBusMsg.ChatActivityEnd());
            if (isNewChatroom == 1){
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new EventBusMsg.ChatActivityEnd());
        EventBus.getDefault().unregister(this);
    }

}