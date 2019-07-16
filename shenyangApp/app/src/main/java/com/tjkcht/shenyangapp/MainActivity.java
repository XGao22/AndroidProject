package com.tjkcht.shenyangapp;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.content.Context;
import java.io.Serializable;

import com.tjkcht.pojo.Material;
import com.tjkcht.rfid.sdk.ContainerEntity;
import com.tjkcht.rfid.sdk.IWulingRfidHandsetSdk;
import com.tjkcht.rfid.sdk.RfidDeviceException;
import com.tjkcht.rfid.sdk.RfidSdkConfig;
import com.tjkcht.rfid.sdk.RfidTag;
import com.tjkcht.rfid.sdk.WulingRfidSdk;
import  com.tjkcht.pojo.rInfo;
import com.tjkcht.shenyangService.shenyangService;
import com.tjkcht.wulingapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
public class MainActivity extends Activity {
    private static final int FILE_SELECT_CODE = 0;
    private List<Material> mList;
    private IWulingRfidHandsetSdk wlSdk = WulingRfidSdk.getInstance();
    private List<rInfo> rList;
    private long firstTime = 0;
    ListView lv_info;
    ListView lv_material;
    LinearLayout ll_servicemanage;
    EditText etUrl;
    Button bt_closeservice;
    Button bt_openservice;
    String fileUrl;
    private static final String TAG = "ChooseFile";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_info= findViewById(R.id.lv_info);
        lv_material= findViewById(R.id.lv_material);
        ll_servicemanage=findViewById(R.id.ll_servicemanage);
        bt_closeservice=findViewById(R.id.bt_closeservice);
        bt_closeservice.setEnabled(false);
        bt_openservice=findViewById(R.id.bt_openservice);
        initSdkClickAction();
        /*
     Boolean isServiceRunning=   isServiceRunning("com.tjkcht.shenyangService.shenyangService",MainActivity.this);
  if (isServiceRunning==true){
           Toast.makeText(MainActivity.this,"开启了服务",Toast.LENGTH_SHORT).show();

       }else if(isServiceRunning==false){
            Toast.makeText(MainActivity.this,"关闭了服务",Toast.LENGTH_SHORT).show();
       }
*/


        lv_material.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override


            //通过点击物料框，查看其中的标签
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Material material= mList.get(i);
                Set<RfidTag> RfidTagSet=   material.getRfidTagSet();
                String materialId= material.getMaterialId();
                List<rInfo> rInfoList= new ArrayList<rInfo>();
                for (RfidTag r :RfidTagSet ){
                    rInfo  rinfo=   new rInfo();
                    rinfo.setrId("标签："+r.getEpcHex());
                    rinfo.setFindTime("发现时间："+r.getDiscoverTime().toString());
                    rinfo.setMinRssi(r.getMinRssi());
                    rinfo.setMaxRssi(r.getMaxRssi());
                    rinfo.setrCheckNum("清点次数："+r.getInventoryCounts());
                    rInfoList.add(rinfo);
                }
                Intent intent= new Intent();
                intent.putExtra("rInfoList",(Serializable) rInfoList);
                intent.putExtra("materialId",materialId+"");
                intent.setClass(MainActivity.this,MaterialDetailedActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }
    //打开管理服务页面
    public void servicemanage(View view){
        lv_info.setVisibility(View.GONE);
        lv_material.setVisibility(View.GONE);

        ll_servicemanage.setVisibility(View.VISIBLE);

        Boolean isServiceRunning=   isServiceRunning("com.tjkcht.shenyangService.shenyangService",MainActivity.this);
        if (isServiceRunning==true){
            bt_openservice.setEnabled(false);
            bt_closeservice.setEnabled(true);
            bt_openservice.setBackgroundColor(0xFFA8A8A8);
            bt_closeservice.setBackgroundColor(0xffff0000);

        }else if(isServiceRunning==false){
            bt_openservice.setEnabled(true);
            bt_closeservice.setEnabled(false);
            bt_openservice.setBackgroundColor(0xFF4D4DFF);
            bt_closeservice.setBackgroundColor(0xFFA8A8A8);
        }



    }
    //开启服务
    public void openservice(View view){
        bt_openservice.setEnabled(false);
        bt_closeservice.setEnabled(true);
        bt_openservice.setBackgroundColor(0xFFA8A8A8);
        bt_closeservice.setBackgroundColor(0xffff0000);
        System.out.println("开启服务");
        Intent intent=new Intent(this,shenyangService.class);
        startService(intent);
       /* Boolean isServiceRunning=   isServiceRunning("shenyangService",MainActivity.this);
        if (isServiceRunning==true){
            Toast.makeText(MainActivity.this,"开启了服务",Toast.LENGTH_SHORT).show();

        }else if(isServiceRunning==false){
            Toast.makeText(MainActivity.this,"关闭了服务",Toast.LENGTH_SHORT).show();
        }*/
        //  Toast.makeText(MainActivity.this,"成功开启服务",Toast.LENGTH_SHORT).show();


    }
    //关闭服务
    public void closeservice(View view){
        bt_openservice.setEnabled(true);
        bt_closeservice.setEnabled(false);
        bt_openservice.setBackgroundColor(0xFF4D4DFF);
        bt_closeservice.setBackgroundColor(0xFFA8A8A8);
        System.out.println("关闭服务");
        Intent intent =new Intent(MainActivity.this,shenyangService.class);
        stopService(intent);

    }
    ///双击退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
                Boolean isServiceRunning=   isServiceRunning("com.tjkcht.shenyangService.shenyangService",MainActivity.this);
                if (isServiceRunning==true){
                    System.out.println("退出了，服务这时打开了，SDK就不关闭了");

                }else if(isServiceRunning==false){
                    closeSdkAction();
                    System.out.println("退出了，服务这时关闭了，SDK也关闭了");
                }

            }
        }

        return super.onKeyUp(keyCode, event);
    }
    // activity销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    //点击“清点标签”，将获取到的标签显示在页面/保存到临时共享参数
    public void checkRFID(View v){


        lv_material.setVisibility(View.GONE);
        lv_info.setVisibility(View.VISIBLE);
        ll_servicemanage.setVisibility(View.GONE);
        try {
            Set<RfidTag> tags = wlSdk.inventoryTags();
            rList=    new ArrayList<rInfo>();
            for (RfidTag tag : tags) {
                rInfo r=   new rInfo();
                r.setrId("标签："+tag.getEpcHex());
                r.setFindTime("发现时间："+tag.getDiscoverTime().toString());
                r.setMaxRssi(tag.getMaxRssi());
                r.setMinRssi(tag.getMinRssi());
                r.setrCheckNum("清点次数："+tag.getInventoryCounts());
                rList.add(r);
            }
            if(rList.size()>0) {
                Toast.makeText(this, "识别到" + rList.size() + "个标签", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_info.setAdapter(new RfidAdaper());
                    }
                });
            }else {
                Toast.makeText(this, "未识别到标签", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_info.setAdapter(new RfidAdaper());
                    }
                });
            }
        } catch (RfidDeviceException e) {
            Toast.makeText(this,"识别标签失败", Toast.LENGTH_SHORT).show();
        }


    }

//初始化sdk
    public void initSdkClickAction() {

        try {
            RfidSdkConfig cfg = new RfidSdkConfig();
            cfg.rfPower = 10;
            wlSdk.initSdk(cfg);
            Toast.makeText(this, "初始化SDK成功", Toast.LENGTH_SHORT).show();
            // logView.setText("初始化SDK成功");
        } catch (RfidDeviceException e) {
            //logView.setText("初始化SDK失败:"+e);
            Toast.makeText(this, "初始化SDK失败", Toast.LENGTH_SHORT).show();
        }
    };
    public void  closeSdkAction(){
        wlSdk.destorySdk();
        Toast.makeText(this, "关闭SDK", Toast.LENGTH_SHORT).show();
    };



    public class RfidAdaper extends BaseAdapter{
        @Override
        public int getCount() {
            return rList.size();
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

            tv_rid.setText(rList.get(i).getrId());
            tv_findTime.setText(rList.get(i).getFindTime());
            tv_rlimit.setText("范围："+rList.get(i).getMinRssi()+"~"+ rList.get(i).getMaxRssi());
            tv_rchecknum.setText(rList.get(i).getrCheckNum());

            return view;
        }
    }
    //判断服务是否开启
    public static boolean isServiceRunning(String servicename,Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>  infos = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info: infos){
            System.out.println(info.service.getClassName());
            if(servicename.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    //点击查看料框
    public void checkmaterial( View view){
        lv_info.setVisibility(View.GONE);
        lv_material.setVisibility(View.VISIBLE);
        ll_servicemanage.setVisibility(View.GONE);
        try {
            Set<ContainerEntity> tags = wlSdk.inventoryContainer();
            // getContainerEntity(tags);
            mList=    new ArrayList<Material>();
            for (ContainerEntity tag : tags){
                Material m=  new Material();
                m.setMaterialId("盛具编码："+tag.getContainerId());
                m.setRfidNum("共包含"+tag.getTags().size()+"个标签");
                m.setRfidTagSet(tag.getTags());
                mList.add(m);

            }
            if(mList.size()>0) {
                Toast.makeText(this, "识别到" + mList.size() + "个盛具", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        lv_material.setAdapter(new MaterialAdaper());


                    }
                });
            }else {
                Toast.makeText(this, "未识别到盛具", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_material.setAdapter(new MaterialAdaper());
                    }
                });
            }
        } catch (RfidDeviceException e) {
            Toast.makeText(this, "未识别到盛具", Toast.LENGTH_SHORT).show();
        }


    }

    //遍历料框显示到页面
    public class MaterialAdaper extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
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
                view=View.inflate(getApplicationContext(),R.layout.material_item,null);
            }else{
                view=convertView;

            }
            TextView tv_materialid=      view.findViewById(R.id.tv_materialid);
            TextView tv_rfidNum=      view.findViewById(R.id.tv_rfidNum);

            tv_materialid.setText(mList.get(i).getMaterialId());
            tv_rfidNum.setText(mList.get(i).getRfidNum());
            return view;
        }

    }





}

