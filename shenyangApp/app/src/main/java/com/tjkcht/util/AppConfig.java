package com.tjkcht.util;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    public static final String APP_CONFIG = "appConfig.properties";

    private static AppConfig instance;
    private Context mContext;

    private AppConfig(Context context) {
        mContext = context.getApplicationContext();
    }

    public static AppConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig(context);
                }
            }
        }
        return instance;
    }

    private Properties get() {
        InputStream fis = null;
        Properties props = new Properties();
        try {
            fis = mContext.openFileInput(APP_CONFIG);
            props.load(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * 对外提供的get方法
     * @param key
     * @return
     */
    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            fos  = mContext.openFileOutput(APP_CONFIG,Context.MODE_PRIVATE);
            p.store(fos, null);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对外提供的保存key value方法
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }
}
