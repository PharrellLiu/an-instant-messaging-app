package my.application.ui.moment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import my.application.ChatMessageAdapter;
import my.application.MomentAdapter;
import my.application.R;
import my.application.SendMomentActivity;
import my.application.URLCollection;

public class MomentFragment extends Fragment {

    private MomentViewModel mViewModel;

    private Context mContext;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MomentAdapter.Moment> myDataset = new ArrayList<MomentAdapter.Moment>();

    private RefreshLayout mRefreshLayout;

    private String momentTimeLine;

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

        recyclerView = view.findViewById(R.id.moment_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MomentAdapter(myDataset,mContext);
        recyclerView.setAdapter(mAdapter);

        momentTimeLine = "0";

        getMoments(momentTimeLine);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                myDataset.clear();
                momentTimeLine = "0";
                getMoments(momentTimeLine);
                mRefreshLayout.finishRefresh();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getMoments(momentTimeLine);
                mRefreshLayout.finishLoadMore();
            }
        });



        return view;
    }

    public void getMoments(String momentTimeLine1){
        RequestParams params = new RequestParams(URLCollection.GET_MOMENTS);
        params.addBodyParameter("momentTimeLine",momentTimeLine1);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    int len = jsonArray.length();
                    for (int i = 0; i<len; i++) {
                        JSONObject temp = jsonArray.getJSONObject(i);
                        myDataset.add(new MomentAdapter.Moment(URLCollection.DOWNLOAD_RESOURCE + temp.getString("file_name"),
                                temp.getString("content"),temp.getString("name"),
                                temp.getString("moment_time"),temp.getString("type")));
                    }
                    mAdapter.notifyDataSetChanged();
                    momentTimeLine = myDataset.get(myDataset.size()-1).getTime();
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