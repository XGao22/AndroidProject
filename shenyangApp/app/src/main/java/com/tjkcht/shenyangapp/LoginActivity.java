package com.tjkcht.shenyangapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.content.Intent;

import com.tjkcht.wulingapp.R;

/**
 * Created by APPLE on 2019/1/31.
 */

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

public void login(View v){
  Intent intent=  new Intent(getApplicationContext(),MainActivity.class);
   startActivity(intent);
    finish();
}
}
