package my.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.view.annotation.ContentView;
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
    @ViewInject(R.id.chat_refreshLayout)
    RefreshLayout refreshLayout;
    @ViewInject(R.id.edittext_chat)
    private EditText input_edittext;
    @ViewInject(R.id.button_send)
    private Button button_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(ChatActivity.this);
        Intent intent = getIntent();
        int isChatroom = intent.getIntExtra("isChatroom",-1);
        String nameOfChatroomOrFri = intent.getStringExtra("nameOfChatroomOrFri");
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

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
            }
        });

        myDataset.add(new ChatMessageAdapter.ChatMessage("root","2015","test",1));
        myDataset.add(new ChatMessageAdapter.ChatMessage("lbw","2015","test",0));
        myDataset.add(new ChatMessageAdapter.ChatMessage("root","2015","test",1));
        myDataset.add(new ChatMessageAdapter.ChatMessage("lbw","2015","test",0));
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}