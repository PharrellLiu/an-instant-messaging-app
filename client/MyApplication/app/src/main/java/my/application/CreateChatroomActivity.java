package my.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

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

import static my.application.URLCollection.CREATE_CHATROOM;
import static my.application.URLCollection.GET_FRI_LIST;

@ContentView(R.layout.activity_create_chatroom)
public class CreateChatroomActivity extends AppCompatActivity {
    /**
     * this is the create chatroom activity
     * input the chatroom name, choose the members,
     * then created!
     */

    @ViewInject(R.id.button_select_all)
    private Button button_select_all;
    @ViewInject(R.id.button_deselect_all)
    private Button button_deselect_all;
    @ViewInject(R.id.button_confirm)
    private Button button_confirm;
    @ViewInject(R.id.button_cancel)
    private Button button_cancel;
    @ViewInject(R.id.edittext_create_chatroom_name)
    private EditText edittext_create_chatroom_name;

    @ViewInject(R.id.create_chatroom_recycler_view)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<CreateChatroomAdapter.Member> myDataset = new ArrayList<CreateChatroomAdapter.Member>();

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(CreateChatroomActivity.this);

        // init recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CreateChatroomAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        // get the user's name
        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

        // to init the users list
        RequestParams params = new RequestParams(GET_FRI_LIST);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    int len = jsonArray.length();
                    for (int i = 0; i<len; i++){
                        String name = jsonArray.getJSONObject(i).getString("name");
                        if (!name.equals(userName)){
                            myDataset.add(new CreateChatroomAdapter.Member(name,false));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
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

    // button to select all users
    @Event(value = R.id.button_select_all)
    private void onClickSelectAllButton(View view) {
        int len = myDataset.size();
        for (int i = 0; i < len; i++){
            myDataset.get(i).setIsEnter2(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    // button to deselect all users
    @Event(value = R.id.button_deselect_all)
    private void onClickDeselectAllButton(View view) {
        int len = myDataset.size();
        for (int i = 0; i < len; i++){
            myDataset.get(i).setIsEnter2(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    // the quit button
    @Event(value = R.id.button_cancel)
    private void onClickCancelButton(View view) {
        this.finish();
    }

    // create chatroom
    @Event(value = R.id.button_confirm)
    private void onClickConfirmButton(View view) {
        int len = myDataset.size();
        if (len == 0){
            Toast.makeText(x.app(), "choose someone", Toast.LENGTH_SHORT).show();
        } else {
            final String chatroomName = edittext_create_chatroom_name.getText().toString();
            if (chatroomName.length() == 0){
                Toast.makeText(x.app(), "input chatroom name", Toast.LENGTH_SHORT).show();
            } else {
                List<String> users = new ArrayList<String>();
                for (int i = 0; i < len; i++){
                    CreateChatroomAdapter.Member member = myDataset.get(i);
                    if (member.getIsEnter()){
                        users.add(member.getName());
                    }
                }
                users.add(userName);
                Gson gson = new Gson();
                String chosenUsers = gson.toJson(users);
                RequestParams params = new RequestParams(CREATE_CHATROOM);
                params.addBodyParameter("chatroomName",chatroomName);
                params.addBodyParameter("chosenUsers",chosenUsers);
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")){
                                Intent intent = new Intent(CreateChatroomActivity.this,ChatActivity.class);
                                intent.putExtra("isNewChatroom",1);// if it is the init of a new chatroom, when we press the back button, it should not back to create new chatroom activity but home activity
                                intent.putExtra("isChatroom",1);
                                intent.putExtra("nameOfChatroomOrFri",chatroomName);
                                startActivity(intent);
                            } else {
                                Toast.makeText(x.app(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
    }

}