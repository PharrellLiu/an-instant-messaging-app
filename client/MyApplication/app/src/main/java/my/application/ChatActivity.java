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

    private int page;
    private String url;
    private int isChatroom;
    private String nameOfChatroomOrFri;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(ChatActivity.this);
        Intent intent = getIntent();
        isChatroom = intent.getIntExtra("isChatroom",-1);
        nameOfChatroomOrFri = intent.getStringExtra("nameOfChatroomOrFri");
        this.setTitle(nameOfChatroomOrFri);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatMessageAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        page = 1;
        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();
        if (isChatroom == 1) {
            url = URLCollection.GET_CHATROOM_MESSAGES + page + "&chatroom=" + nameOfChatroomOrFri;
        } else {
            url = URLCollection.GET_PRIVATE_CHAT_MESSAGES + page + "&name1=" + nameOfChatroomOrFri + "&name2=" + userName;
        }

        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        if (setChatMessage(result,isChatroom,userName)){
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }
                @Override
                public void onFinished() {
                }
        });

        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                page++;
                if (isChatroom == 1) {
                    url = URLCollection.GET_CHATROOM_MESSAGES + page + "&chatroom=" + nameOfChatroomOrFri;
                } else {
                    url = URLCollection.GET_PRIVATE_CHAT_MESSAGES + page + "&name1=" + nameOfChatroomOrFri + "&name2=" + userName;
                }
                RequestParams params = new RequestParams(url);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            if (setChatMessage(result,isChatroom,userName)){
                                mAdapter.notifyDataSetChanged();
                            } else {
                                page--;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                    }
                });
                refreshlayout.finishRefresh();
            }
        });



    }

    public boolean setChatMessage(String result, int isChatroom, String username) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        String status = jsonObject.getString("status");
        if (status.equals("ok")){
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
            return true;
        }
        return false;
    }

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
            if (isChatroom == 1) {
                params.addBodyParameter("chatroom",nameOfChatroomOrFri);
                params.addBodyParameter("name",userName);
            } else {
                params.addBodyParameter("receivename",nameOfChatroomOrFri);
                params.addBodyParameter("sendname",userName);
            }
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {

                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }
                @Override
                public void onFinished() {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}