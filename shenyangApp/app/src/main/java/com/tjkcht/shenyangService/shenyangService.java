package com.tjkcht.shenyangService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by APPLE on 2019/2/18.
 */

public class shenyangService extends Service {
    MyServer myServer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("....创建服务啦");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("....开始服务啦");
        myServer = new MyServer(8888);
        try {
            myServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        myServer.stop();

        System.out.println("....终止服务啦");
        super.onDestroy();
    }
}
