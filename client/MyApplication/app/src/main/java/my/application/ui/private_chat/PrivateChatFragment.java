package my.application.ui.private_chat;

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

import java.util.ArrayList;
import java.util.List;

import my.application.ButtonAdapterOfChatroomAndPrivateChat;
import my.application.MyApp;
import my.application.R;
import my.application.URLCollection;

import org.xutils.x;

public class PrivateChatFragment extends Fragment {
    /**
     * this is the fragment of private chat, show all users here
     **/

    private PrivateChatViewModel mViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> myDataset = new ArrayList<String>();
    private Context mContext;

    public static PrivateChatFragment newInstance() {
        return new PrivateChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.private_chat_fragment, container, false);

        mContext = getActivity();

        // init recycler view
        recyclerView = view.findViewById(R.id.private_chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ButtonAdapterOfChatroomAndPrivateChat(myDataset,mContext,0);
        recyclerView.setAdapter(mAdapter);

        // get the user's name
        MyApp myApp = (MyApp) getActivity().getApplication();
        final String userName = myApp.getName();

        // init the users list
        RequestParams params = new RequestParams(URLCollection.GET_FRI_LIST);
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
                            myDataset.add(name);
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrivateChatViewModel.class);
        // TODO: Use the ViewModel
    }

}