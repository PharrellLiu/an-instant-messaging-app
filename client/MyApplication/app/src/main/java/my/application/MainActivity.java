package my.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(MainActivity.this);
    }

    @Event(value = R.id.button_login)
    private void onClickLoginButton(View view) {
        final String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (name.length() == 0 || password.length() == 0){
            Toast.makeText(x.app(), "input something",Toast.LENGTH_LONG).show();
        } else {
            RequestParams params = new RequestParams(URLCollection.LOGIN_URL);
            params.addBodyParameter("name", name);
            params.addBodyParameter("password", password);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")){
                            Toast.makeText(x.app(), "welcome", Toast.LENGTH_LONG).show();
                            MyApp myApp = (MyApp) getApplication();
                            myApp.setName(name);
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(x.app(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }
                @Override
                public void onFinished() {
                }
            });
        }
    }

    @Event(value = R.id.button_register)
    private void onClickRegisterButton(View view) {
        final String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (name.length() == 0 || password.length() == 0){
            Toast.makeText(x.app(), "input something",Toast.LENGTH_LONG).show();
        } else {
            RequestParams params = new RequestParams(URLCollection.REGISTER_URL);
            params.addBodyParameter("name", name);
            params.addBodyParameter("password", password);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")){
                            Toast.makeText(x.app(), "welcome", Toast.LENGTH_LONG).show();
                            MyApp myApp = (MyApp) getApplication();
                            myApp.setName(name);
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(x.app(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }
                @Override
                public void onFinished() {
                }
            });
        }
    }

}