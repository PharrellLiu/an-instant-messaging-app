package my.application;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_send_moment)
public class SendMomentActivity extends AppCompatActivity {
    /**
     * send moment is here!
     */

    @ViewInject(R.id.input_moment_content)
    private EditText input_moment_content;
    @ViewInject(R.id.choose_picture)
    private TextView choose_picture;
    @ViewInject(R.id.picture_chosen)
    private ImageView picture_chosen;

    private String mediaURl;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(SendMomentActivity.this);

        this.setTitle("Send Moment");

        // if there is a image or video, we need the url (uri) as a part of the moment
        mediaURl = "";

        // get the user's name
        MyApp myApp = (MyApp) getApplication();
        userName = myApp.getName();

        // init the actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // to choose image or video
        choose_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector.create(SendMomentActivity.this)
                        .openGallery(PictureMimeType.ofAll())
                        .loadImageEngine(GlideEngine.createGlideEngine())
                        .isCamera(true)
                        .isWeChatStyle(true)
                        .previewImage(true)
                        .previewVideo(true)
                        .previewEggs(true)
                        .maxSelectNum(1)
                        .isMaxSelectEnabledMask(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });

    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    // get the result of choosing image or video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                mediaURl = selectList.get(0).getPath();
                Glide.with(this)
                        .load(selectList.get(0).getPath())
                        .into(picture_chosen);
                Uri uri = Uri.parse((String) mediaURl);
                mediaURl = getRealPathFromURI(this, uri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_moment_actionbar, menu);
        return true;
    }

    // if the file chosen is an image?
    // it would be used when sending moment
    public boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outWidth == -1) {
            return false;
        }
        return true;
    }

    // button of send moment is here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_send_moment:
                String content = input_moment_content.getText().toString();
                if (content.length()>0){
                    RequestParams params = new RequestParams(URLCollection.POST_MOMENT);
                    params.setMultipart(true);
                    params.addBodyParameter("content", content);
                    params.addBodyParameter("name", userName);
                    if (mediaURl.length()>0){
                        if (isImageFile(mediaURl)){
                            params.addBodyParameter("type", "image");
                        } else {
                            params.addBodyParameter("type", "video");
                        }
                        File file = new File(mediaURl);
                        params.addBodyParameter("file",file);
                    } else {
                        params.addBodyParameter("type", "text");
                    }
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.getString("status");
                                if (status.equals("ok")) { // succeed
                                    SendMomentActivity.this.finish();
                                    EventBus.getDefault().post(new EventBusMsg.PostNewMoment());
                                }
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
                } else {
                    Toast.makeText(x.app(), "input something", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}