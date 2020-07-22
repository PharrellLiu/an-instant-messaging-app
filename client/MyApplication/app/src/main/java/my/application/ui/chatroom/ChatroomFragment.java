package my.application.ui.chatroom;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import my.application.ButtonAdapterOfChatroomAndPrivateChat;
import my.application.CreateChatroomActivity;
import my.application.MyApp;
import my.application.R;
import my.application.URLCollection;

public class ChatroomFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> myDataset = new ArrayList<String>();
    private Context mContext;
    private ChatroomViewModel mViewModel;

    public static ChatroomFragment newInstance() {
        return new ChatroomFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatroom_fragment, container, false);

        mContext = getActivity();
        // init recycler view
        recyclerView = view.findViewById(R.id.chatroom_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        myDataset.add("default_chatroom");
        mAdapter = new ButtonAdapterOfChatroomAndPrivateChat(myDataset,mContext,1);
        recyclerView.setAdapter(mAdapter);

        MyApp myApp = (MyApp) getActivity().getApplication();
        String userName = myApp.getName();

        String url = URLCollection.GET_CHATROOM_LIST + userName;
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    int len = jsonArray.length();
                    for (int i = 0; i<len; i++){
                        myDataset.add(jsonArray.getJSONObject(i).getString("chatroom"));
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

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_chatroom_actionbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 设置Create Chatroom按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_chatroom) {
            Intent intent = new Intent();
            intent.setClass(mContext, CreateChatroomActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatroomViewModel.class);
        // TODO: Use the ViewModel
    }

}