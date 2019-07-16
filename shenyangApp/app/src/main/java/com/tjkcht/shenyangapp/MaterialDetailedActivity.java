package com.tjkcht.shenyangapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tjkcht.pojo.rInfo;
import com.tjkcht.wulingapp.R;

import android.widget.TextView;
import java.util.List;

/**
 * Created by APPLE on 2019/1/31.
 */

public class MaterialDetailedActivity extends Activity {
    TextView lv_materialdetailed;
    List<rInfo> rInfoList;
    String materislId;
    ListView lv_info;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_materialdetailed);

     Intent intent= getIntent();
        //获取接收到的数据
        materislId =  intent.getStringExtra("materialId");
        rInfoList=   (List<rInfo>) intent.getSerializableExtra("rInfoList");
        //Toast测试
        //Toast.makeText(getApplicationContext(),materislId,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),""+rInfoList,Toast.LENGTH_SHORT).show();
        //获取控件
        lv_materialdetailed=   findViewById(R.id.lv_materialdetailed);
        lv_info= this.findViewById(R.id.lv_info);
        //控件赋值
        lv_materialdetailed.setText(materislId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv_info.setAdapter(new RfidAdaper());
            }
        });

    }
    public class RfidAdaper extends BaseAdapter {
        @Override
        public int getCount() {
            return rInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view ;
            if (convertView==null){
                view=View.inflate(getApplicationContext(),R.layout.rfid_item,null);
            }else{
                view=convertView;

            }
            TextView tv_rid= view.findViewById(R.id.tv_rid);
            TextView tv_findTime= view.findViewById(R.id.tv_findTime);
            TextView tv_rlimit= view.findViewById(R.id.tv_rlimit);
            TextView tv_rchecknum= view.findViewById(R.id.tv_rchecknum);

            tv_rid.setText(rInfoList.get(i).getrId());
            tv_findTime.setText(rInfoList.get(i).getFindTime());
            tv_rlimit.setText("范围："+rInfoList.get(i).getMinRssi()+"~"+ rInfoList.get(i).getMaxRssi());
            tv_rchecknum.setText(rInfoList.get(i).getrCheckNum());

            return view;
        }
    }

}
