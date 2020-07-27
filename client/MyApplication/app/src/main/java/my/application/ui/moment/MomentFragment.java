package my.application.ui.moment;

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

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import my.application.ButtonAdapterOfChatroomAndPrivateChat;
import my.application.CreateChatroomActivity;
import my.application.MomentAdapter;
import my.application.R;
import my.application.SendMomentActivity;

public class MomentFragment extends Fragment {

    private MomentViewModel mViewModel;

    private Context mContext;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MomentAdapter.Moment> myDataset = new ArrayList<MomentAdapter.Moment>();

    private RefreshLayout mRefreshLayout;

    public static MomentFragment newInstance() {
        return new MomentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moment_fragment, container, false);
        mContext = getActivity();
        setHasOptionsMenu(true);

        mRefreshLayout = view.findViewById(R.id.moment_refreshLayout);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });

        recyclerView = view.findViewById(R.id.moment_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MomentAdapter(myDataset,mContext);
        recyclerView.setAdapter(mAdapter);
        
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.send_moment_actionbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 设置Send Moment按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_moment) {
            Intent intent = new Intent();
            intent.setClass(mContext, SendMomentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MomentViewModel.class);
        // TODO: Use the ViewModel
    }

}