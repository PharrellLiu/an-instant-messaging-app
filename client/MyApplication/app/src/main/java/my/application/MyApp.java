package my.application;

import android.app.Application;

import org.xutils.x;

public class MyApp extends Application {
    /**
     * this class is used to init the xutils and set the user's name as a global variable
     *
     */

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
