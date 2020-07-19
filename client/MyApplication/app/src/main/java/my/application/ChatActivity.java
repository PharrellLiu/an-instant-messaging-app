package my.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(ChatActivity.this);
        Intent intent = getIntent();
        int isChatroom = intent.getIntExtra("isChatroom",-1);
        String nameOfChatroomOrFri = intent.getStringExtra("nameOfChatroomOrFri");


    }
}