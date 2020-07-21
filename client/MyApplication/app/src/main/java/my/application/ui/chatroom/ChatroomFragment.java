package my.application.ui.chatroom;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
        myDataset.add("default chatroom");
        mContext = getActivity();
        recyclerView = view.findViewById(R.id.chatroom_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatroomViewModel.class);
        // TODO: Use the ViewModel
    }

}