package com.tjkcht.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tjkcht.wulingapp.R;

public class MyDialog extends Dialog {
    private TextView title;//标题
    private TextView message; //信息
    private Button positive;//确实按钮
    private Button nagivate;//取消按钮
    //我们可以从外部设置控件的标题，以及信息，所以我们要定义字符串变量来接收外部的输入
    private String str_title;//接收标题的
    private String str_message; //接收信息的
    private String str_positive_btn;//确定按钮的
    private String str_navigate_btn;//取消按钮的
    //给确定跟取消按钮设置监听器
    private onPositiveListener positiveListener;
    private onNagivateListener nagivateListener;

    //定义确定，取消按钮的接口
    public interface onPositiveListener {
        void onPositiveClick();
    }

    public interface onNagivateListener {
        void onNagivateClick();
    }

    //定义public方法供外部给确定，取消按钮设置内容以及监听器
    public void setPositiveListener(String str_positive_btn, onPositiveListener onPositiveListener) {
        if (str_positive_btn != null) {
            this.str_positive_btn = str_positive_btn;
        }
        this.positiveListener = onPositiveListener;
    }

    public void setNagivateListener(String str_navigate_btn, onNagivateListener onNagivateListener) {
        if (str_navigate_btn != null) {
            this.str_navigate_btn = str_navigate_btn;
        }
        this.nagivateListener = onNagivateListener;
    }

    public void setTitle(String title) {
        this.str_title = title;
    }

    public void setMessage(String message) {
        this.str_message = message;
    }

    //该构造器一定要有，给自定义的Dialig引入我们刚刚创建的风格
    public MyDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    //像Activity一样Dialog创建时也会调用onCreate方法，
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载我们刚刚定义好的Dialog的布局
        setContentView(R.layout.my_dialog);
        //设置点击Dialog以外的地方不能让Dialog消失
        setCanceledOnTouchOutside(false);
        //初始化控件
        initView();
        //给控件设置初始的数据
        initData();
        //给控件添加各种事件
        initEvent();
    }

    //初始化我们一会会用到的控件
    public void initView() {
        positive = findViewById(R.id.positive);
        nagivate = findViewById(R.id.nagivate);
        message = findViewById(R.id.message);
        title = findViewById(R.id.title);
    }

    //设置标题，消息，确定，取消按钮的内容
    public void initData() {
        if (str_message != null) {
            message.setText(str_message);
        }

        if (str_title != null) {
            title.setText(str_title);
        }

        if (str_positive_btn != null) {
            positive.setText(str_positive_btn);
        }

        if (str_navigate_btn != null) {
            nagivate.setText(str_navigate_btn);
        }
    }

    //给按钮设置点击事件
    public void initEvent() {
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveListener != null) {
                    positiveListener.onPositiveClick();
                }
            }
        });
        nagivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nagivate != null) {
                    nagivateListener.onNagivateClick();
                }
            }
        });
    }
}
