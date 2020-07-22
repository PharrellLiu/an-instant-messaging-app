package my.application;

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

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CreateChatroomAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

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

    @Event(value = R.id.button_select_all)
    private void onClickSelectAllButton(View view) {
        int len = myDataset.size();
        for (int i = 0; i < len; i++){
            myDataset.get(i).setIsEnter2(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Event(value = R.id.button_deselect_all)
    private void onClickDeselectAllButton(View view) {
        int len = myDataset.size();
        for (int i = 0; i < len; i++){
            myDataset.get(i).setIsEnter2(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Event(value = R.id.button_cancel)
    private void onClickCancelButton(View view) {
        this.finish();
    }

    @Event(value = R.id.button_confirm)
    private void onClickConfirmButton(View view) {
        int len = myDataset.size();
        if (len == 0){
            Toast.makeText(x.app(), "choose someone", Toast.LENGTH_SHORT).show();
        } else {
            String chatroomName = edittext_create_chatroom_name.getText().toString();
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
                        System.out.println(result);
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