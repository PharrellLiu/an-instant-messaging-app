package my.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.net.URISyntaxException;

public class HomeActivity extends AppCompatActivity {

    private String userName;

    private int isChatActivityStart;
    private int isChatroom;
    private String nameOfChatroomOrFri;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(URLCollection.SOCKETIO);
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chatroom, R.id.navigation_private_chat, R.id.navigation_moment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

        EventBus.getDefault().register(this);

        isChatActivityStart = 0;
        isChatroom = -1;
        nameOfChatroomOrFri = "";

        mSocket.on("push", onNewMessage);
        mSocket.connect();
        mSocket.emit("join", userName);

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                int is_chatroom = Integer.parseInt(jsonObject.getString("is_chatroom"));
                String chatroom_or_receivename = jsonObject.getString("chatroom_or_receivename");
                String message = jsonObject.getString("message");
                String message_time = jsonObject.getString("message_time");
                String sendname = jsonObject.getString("sendname");
                if (isChatActivityStart == 1){
                    if (is_chatroom == 1 && isChatroom == 1
                            && chatroom_or_receivename.equals(nameOfChatroomOrFri)){
                        EventBus.getDefault().post(new EventBusMsg.PushMsgToChat(sendname, message,message_time));
                    } else if (is_chatroom == 0 && isChatroom == 0 && sendname.equals(nameOfChatroomOrFri)){
                        EventBus.getDefault().post(new EventBusMsg.PushMsgToChat(sendname, message,message_time));
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(x.app(), "You got a new message", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(x.app(), "You got a new message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatActivityStart(EventBusMsg.ChatActivityStart chatActivityStart){
        isChatActivityStart = 1;
        isChatroom = chatActivityStart.getIsChatroom();
        nameOfChatroomOrFri = chatActivityStart.getNameOfChatroomOrFri();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatActivityEnd(EventBusMsg.ChatActivityEnd chatActivityEnd){
        isChatActivityStart = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}