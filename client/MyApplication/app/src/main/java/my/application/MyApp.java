package my.application;

import android.app.Application;

import org.xutils.x;

public class MyApp extends Application {
    // the user's name
    private String name;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
        setName("");
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

}
