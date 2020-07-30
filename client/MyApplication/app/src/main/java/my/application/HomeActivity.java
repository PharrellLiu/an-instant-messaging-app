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
    /**
     * after login, we would be here
     * there are 3 fragments here, private chat, chatroom and moment
     * also, the socketio is here too, to receive the message from server
     */

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

        // init fragments
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chatroom, R.id.navigation_private_chat, R.id.navigation_moment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // get the user's name
        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

        // init event bus, we use event bus to communicate to the ChatActvity
        // to know whether the user is in a chatroom or in a private chat
        // to determine if push the message to the ChatActivity of just provide a notification
        EventBus.getDefault().register(this);

        // record the state of the ChatActivity
        isChatActivityStart = 0;
        isChatroom = -1;
        nameOfChatroomOrFri = "";

        mSocket.on("push", onNewMessage);
        mSocket.connect();
        mSocket.emit("join", userName);

    }

    // to receive the message from server and determine how to present it
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                int is_chatroom = Integer.parseInt(jsonObject.getString("is_chatroom"));
                final String chatroom_or_receivename = jsonObject.getString("chatroom_or_receivename");
                final String message = jsonObject.getString("message");
                String message_time = jsonObject.getString("message_time");
                final String sendname = jsonObject.getString("sendname");
                if (isChatActivityStart == 1){
                    if (is_chatroom == 1 && isChatroom == 1 && chatroom_or_receivename.equals(nameOfChatroomOrFri)){
                        EventBus.getDefault().post(new EventBusMsg.PushMsgToChat(sendname, message,message_time));
                    } else if (is_chatroom == 0 && isChatroom == 0 && sendname.equals(nameOfChatroomOrFri)){
                        EventBus.getDefault().post(new EventBusMsg.PushMsgToChat(sendname, message,message_time));
                    } else {
                        if (is_chatroom == 1){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(x.app(), sendname + "said" + message + "in" + chatroom_or_receivename, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(x.app(), sendname + "said" + message + "to you", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    if (is_chatroom == 1){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(x.app(), sendname + "said" + message + "in" + chatroom_or_receivename, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(x.app(), sendname + "said" + message + "to you", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // to know whether the user is in a chatroom or in a private chat
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatActivityStart(EventBusMsg.ChatActivityStart chatActivityStart){
        isChatActivityStart = 1;
        isChatroom = chatActivityStart.getIsChatroom();
        nameOfChatroomOrFri = chatActivityStart.getNameOfChatroomOrFri();
    }

    // to know whether the user is in a chatroom or in a private chat
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