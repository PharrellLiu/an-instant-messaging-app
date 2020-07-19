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

import java.util.ArrayList;
import java.util.List;

import my.application.ButtonAdapterOfChatroomAndPrivateChat;
import my.application.R;

public class PrivateChatFragment extends Fragment {

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
        recyclerView = view.findViewById(R.id.private_chat_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        myDataset.add("123");
        myDataset.add("222");

        mAdapter = new ButtonAdapterOfChatroomAndPrivateChat(myDataset,mContext);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrivateChatViewModel.class);
        // TODO: Use the ViewModel
    }

}