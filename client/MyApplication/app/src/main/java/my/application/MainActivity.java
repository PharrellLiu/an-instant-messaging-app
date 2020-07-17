package my.application;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.xutils.x;
import androidx.appcompat.app.AppCompatActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewInject(R.id.button_login)
    Button loginButton;
    @ViewInject(R.id.button_register)
    Button registerButton;
    @ViewInject(R.id.edittext_password)
    EditText passwordEditText;
    @ViewInject(R.id.edittext_name)
    EditText nameEditText;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(MainActivity.this);

        context = getApplicationContext();



    }
    @Event(value = R.id.button_login)
    private void clickLoginButton(View view) {
        Toast.makeText(context, "Welcome",Toast.LENGTH_SHORT).show();

    }
}