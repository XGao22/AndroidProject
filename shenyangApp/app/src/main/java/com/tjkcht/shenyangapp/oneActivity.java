package com.tjkcht.shenyangapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.tjkcht.adapter.TableAdapter;
import com.tjkcht.pojo.EpcNo;
import com.tjkcht.pojo.MaterialsBarCode;
import com.tjkcht.pojo.ProductMaterial;
import com.tjkcht.pojo.Result;
import com.tjkcht.pojo.rInfo;
import com.tjkcht.rfid.sdk.IWulingRfidHandsetSdk;
import com.tjkcht.rfid.sdk.RfidDeviceException;
import com.tjkcht.rfid.sdk.RfidSdkConfig;
import com.tjkcht.rfid.sdk.RfidTag;
import com.tjkcht.rfid.sdk.WulingRfidSdk;
import com.tjkcht.shenyangService.MyServer;
import com.tjkcht.util.AppConfig;
import com.tjkcht.util.ResultGenerator;
import com.tjkcht.util.SoundUtil;
import com.tjkcht.util.T;
import com.tjkcht.shenyangService.shenyangService;
import com.tjkcht.wulingapp.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;

import cn.pda.scan.ScanThread;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import dmax.dialog.SpotsDialog;

public class oneActivity extends Activity {
    public static final int EPCNO_INFO = 0X123;
    public static final int PRODUCT_INFO = 0X124;
    public static final int MATERIAL_INFO = 0X125;
    public static final int PRODUCT_SUBMIT = 0X126;
    private oneActivity me;
    private IWulingRfidHandsetSdk wlSdk = WulingRfidSdk.getInstance();
    private List<rInfo> rList;
    private long firstTime = 0;
    public static ScanThread scanThread;
    private boolean mIsPressed = false;
    private List<MaterialsBarCode> listMaterialsBarCode = new ArrayList<MaterialsBarCode>();
    private EditText et_label;
    private EditText et_barCode;
    private EditText et_RfPower;
    private TextView tv_productUniqueIdentifier;
    private TextView tv_productCode;
    private TextView tv_productName;
    private TextView tv_plan_goOnline;
    private TextView tv_checkRFID;
    private TextView tv_scanBarCode;
    private LinearLayout ll_servicemanage;
    private RelativeLayout rl_mainpage;
    private Button bt_closeservice;
    private Button bt_openservice;
    private Button bt_submit;
    private Button bt_regainProductInfo;
    private ListView tableListView;
    private TableAdapter adapter;
    private AlertDialog dialog;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private Timer scanTimer = null;
    private KeyReceiver keyReceiver;
    private EpcNo epcNo;
    private ProductMaterial productMaterial;
    private MaterialsBarCode materialsBarCode;
    private AppConfig appConfig;
    private String rfPower;
    private String tid;

    @SuppressLint("HandlerLeak")
    private Handler netHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CallbackHandle((Result) msg.obj);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data").replaceAll("\r|\n", "");
                et_barCode.requestFocus();//获取焦点 光标出现
                et_barCode.setText(data);
                et_barCode.setSelection(data.length());//将光标移至文字末尾
                MaterialsBarCode m = new MaterialsBarCode(data, null, null);
                getMaterialInfoHandle1(m);
                MyServer.barcode = data;
                SoundUtil.play(1, 0);
            }
        }

        ;
    };

    private void getMaterialInfoHandle1(MaterialsBarCode materialsBarCode) {
        //new MyHttpPostAsyncTask(this, netHandler).execute(MATERIAL_INFO, "http://192.168.11.8:18080/rfid/getMaterialInfo", materialsBarCode);
    }

    private void CallbackHandle(Result r) {
        switch (r.getCode()) {
            case 200:
                String s = (String) r.getData();
                switch (r.getMsgType()) {
                    case EPCNO_INFO:
                        epcNo = JSON.parseObject(s, EpcNo.class);
                        getEpcNoCallback(epcNo);
                        break;
                    case PRODUCT_INFO:
                        productMaterial = JSON.parseObject(s, ProductMaterial.class);
                        getProductInfoCallback(productMaterial);
                        break;
                    case MATERIAL_INFO:
                        materialsBarCode = JSON.parseObject(s, MaterialsBarCode.class);
                        getMaterialInfoCallback(materialsBarCode);
                        break;
                    case PRODUCT_SUBMIT:
                        productSubmitCallback();
                        break;
                    default:
                        break;
                }
                break;
            case -1:
//                Toast.makeText(this, "网络服务异常：" + r.getMessage(), Toast.LENGTH_SHORT).show();
                mIsPressed = false;
                settingsButtonClickable();
                break;
            default:
//                Toast.makeText(this, "网络服务错误：" + r.getMessage(), Toast.LENGTH_SHORT).show();
                mIsPressed = false;
                settingsButtonClickable();
                break;
        }
    }

    private void readDataFromAssets() {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int byteread = 0;
        File dir = getFilesDir();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File file = new File(dir, AppConfig.APP_CONFIG);
            if (!file.exists()) {
                file.createNewFile();
                inputStream = getAssets().open(AppConfig.APP_CONFIG);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                byte[] bytes = new byte[1024];
                while ((byteread = inputStream.read(bytes)) > 0) {
                    bos.write(bytes, 0, byteread);
                }

                inputStream.close();
                bos.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shenyang);
        me = this;
        tv_scanBarCode = findViewById(R.id.scanBarCode);
        tv_checkRFID = findViewById(R.id.checkRFID);
        rl_mainpage = findViewById(R.id.mainPage);
        et_barCode = findViewById(R.id.editText3);
        et_label = findViewById(R.id.editText);
        bt_submit = findViewById(R.id.button2);
        bt_regainProductInfo = findViewById(R.id.regainProductInfo);
        ll_servicemanage = findViewById(R.id.ll_servicemanage);
        bt_closeservice = findViewById(R.id.bt_closeservice);
        bt_closeservice.setEnabled(false);
        bt_openservice = findViewById(R.id.bt_openservice);
        tv_productUniqueIdentifier = findViewById(R.id.textView6);
        tv_productCode = findViewById(R.id.textView7);
        tv_productName = findViewById(R.id.textView8);
        tv_plan_goOnline = findViewById(R.id.textView9);
        et_RfPower = findViewById(R.id.et_rfPower);

        readDataFromAssets();
        appConfig = AppConfig.getInstance(getApplicationContext());
        rfPower = appConfig.get("rfPower");
        initSdkClickAction(Integer.parseInt(rfPower));
        dialog = new SpotsDialog.Builder().setContext(me).build();
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            // 出现异常
            Toast.makeText(getApplicationContext(), "serialport init fail", Toast.LENGTH_SHORT).show();
            return;
        }
        scanThread.start();
        SoundUtil.initSoundPool(this);
        //注册按键广播接收者
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        registerReceiver(keyReceiver, filter);
        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.parseColor("#BBDEF4"));
        tableListView = (ListView) findViewById(R.id.list);
        adapter = new TableAdapter(this, listMaterialsBarCode);
        tableListView.setAdapter(adapter);
        tableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final NormalDialog dialog = new NormalDialog(me);
                dialog.content("确定删除吗?").style(NormalDialog.STYLE_TWO).titleTextSize(23).showAnim(mBasIn).dismissAnim(mBasOut).show();
                //设置点击Dialog以外的地方不能让Dialog消失
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                T.showShort(me, "left");
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                if (listMaterialsBarCode.remove(position) != null) {
                                    System.out.println("success");
                                } else {
                                    System.out.println("failed");
                                }
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                return false;
            }
        });
        //new MyHttpGetAsyncTask(this, netHandler).execute(PRODUCT_INFO, "http://192.168.11.8:18080/rfid/getProductInfo");
    }

    //打开管理服务页面
    public void servicemanage(View view) {
        ll_servicemanage.setVisibility(View.VISIBLE);
        rl_mainpage.setVisibility(View.GONE);
        SpannableString ss = new SpannableString(rfPower);
        et_RfPower.setHint(ss);
        Boolean isServiceRunning = isServiceRunning("com.tjkcht.shenyangService.shenyangService", oneActivity.this);
        if (isServiceRunning == true) {
            bt_openservice.setEnabled(false);
            bt_closeservice.setEnabled(true);
            bt_openservice.setBackgroundColor(0xFFA8A8A8);
            bt_closeservice.setBackgroundColor(0xffff0000);

        } else if (isServiceRunning == false) {
            bt_openservice.setEnabled(true);
            bt_closeservice.setEnabled(false);
            bt_openservice.setBackgroundColor(0xFF4D4DFF);
            bt_closeservice.setBackgroundColor(0xFFA8A8A8);
        }
    }

    //开启服务
    public void openservice(View view) {
        String hint = et_RfPower.getHint().toString();
        String s = et_RfPower.getText().toString();

        if (s.isEmpty()) {
            s = hint;
        }

        if (!rfPower.equals(s)) {
            appConfig.set("rfPower", s);
            rfPower = s;
        }

        if (validationRfPower(Integer.parseInt(s))) {
            bt_openservice.setEnabled(false);
            bt_closeservice.setEnabled(true);
            bt_openservice.setBackgroundColor(0xFFA8A8A8);
            bt_closeservice.setBackgroundColor(0xffff0000);
            System.out.println("开启服务");
            initSdkClickAction(Integer.parseInt(s));
            Intent intent = new Intent(this, shenyangService.class);
            startService(intent);
        }
    }

    //关闭服务
    public void closeservice(View view) {
        bt_openservice.setEnabled(true);
        bt_closeservice.setEnabled(false);
        bt_openservice.setBackgroundColor(0xFF4D4DFF);
        bt_closeservice.setBackgroundColor(0xFFA8A8A8);
        System.out.println("关闭服务");
        Intent intent = new Intent(oneActivity.this, shenyangService.class);
        stopService(intent);
        ll_servicemanage.setVisibility(View.GONE);
        rl_mainpage.setVisibility(View.VISIBLE);
    }

    ///双击退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(oneActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
                Boolean isServiceRunning = isServiceRunning("com.tjkcht.shenyangService.shenyangService", oneActivity.this);
                if (isServiceRunning == true) {
                    System.out.println("退出了，服务这时打开了，SDK就不关闭了");

                } else if (isServiceRunning == false) {
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
        if (scanTimer != null) {
            scanTimer.cancel();
        }
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }
        //注销广播接收者
        unregisterReceiver(keyReceiver);
        super.onDestroy();
    }

    //点击“清点标签”，将获取到的标签显示在页面/保存到临时共享参数
    public void checkRFID(View v) {
        mIsPressed = true;
        settingsButtonNotClickable();
        ll_servicemanage.setVisibility(View.GONE);
        rl_mainpage.setVisibility(View.VISIBLE);
        //new MyHttpGetAsyncTask(this, netHandler).execute(EPCNO_INFO, "http://192.168.11.8:18080/rfid/getEpcNo");
    }

    private void settingsButtonNotClickable() {
        bt_regainProductInfo.setEnabled(false);
        bt_submit.setEnabled(false);
        tv_scanBarCode.setEnabled(false);
        tv_checkRFID.setEnabled(false);
    }

    private void settingsButtonClickable() {
        bt_regainProductInfo.setEnabled(true);
        bt_submit.setEnabled(true);
        tv_scanBarCode.setEnabled(true);
        tv_checkRFID.setEnabled(true);
    }

    private void productSubmitCallback() {
//        if(true){
        tv_productUniqueIdentifier.setText("");
        tv_productCode.setText("");
        tv_productName.setText("");
        tv_plan_goOnline.setText("");
        listMaterialsBarCode.clear();
        et_label.setText("");
        et_barCode.setText("");
        materialsBarCode = null;
        productMaterial = null;
        epcNo = null;
        Toast.makeText(this, "提交操作成功！", Toast.LENGTH_SHORT).show();
        mIsPressed = false;
        settingsButtonClickable();
/*        }else {
            Toast.makeText(this, "提交操作失败，与后台通讯异常，稍后请重新提交！", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void getProductInfoCallback(ProductMaterial productMaterial) {
        if (Objects.nonNull(productMaterial)) {
            tv_productUniqueIdentifier.setText(productMaterial.getProductUniqueIdentifier());
            tv_productCode.setText(productMaterial.getProductCode());
            tv_productName.setText(productMaterial.getProductName());
            tv_plan_goOnline.setText(productMaterial.getPlan_goOnline());
        }
        mIsPressed = false;
        settingsButtonClickable();
    }

    private void getMaterialInfoCallback(MaterialsBarCode materialsBarCode) {
        if (Objects.nonNull(materialsBarCode)) {
            Optional<MaterialsBarCode> whetherToAdds = listMaterialsBarCode.stream().filter(mb -> mb.getBarCode().equals(materialsBarCode.getBarCode())).findFirst();
            if (!whetherToAdds.isPresent()) {
                listMaterialsBarCode.add(materialsBarCode);
                adapter.notifyDataSetChanged();
            }
        }
        mIsPressed = false;
        settingsButtonClickable();
    }

    private void getEpcNoCallback(EpcNo epcNo) {
        if (Objects.nonNull(epcNo) && !("".equals(epcNo.getEpc()))) {
            try {
                String newPassword = null;
                Set<RfidTag> tags = wlSdk.inventoryTags();
                rList = new ArrayList<rInfo>();
                for (RfidTag tag : tags) {
                    rInfo r = new rInfo();
                    r.setrId(tag.getEpcHex());
                    r.setFindTime("发现时间：" + tag.getDiscoverTime().toString());
                    r.setMaxRssi(tag.getMaxRssi());
                    r.setMinRssi(tag.getMinRssi());
                    r.setrCheckNum("清点次数：" + tag.getInventoryCounts());
                    rList.add(r);
                }
                if (rList.size() > 0) {
                    Toast.makeText(this, "识别到" + rList.size() + "个标签", Toast.LENGTH_SHORT).show();
                    try {
                        newPassword = wlSdk.generateCardPassword(rList.get(0).getrId());
                        wlSdk.issueAccessPassword(rList.get(0).getrId(), newPassword);
                        tid = wlSdk.getTID(rList.get(0).getrId(), newPassword);
                        wlSdk.writeEPC(rList.get(0).getrId(), newPassword, epcNo.getEpc());
                        et_label.requestFocus();//获取焦点 光标出现
                        et_label.setText(epcNo.getEpc());
                        et_label.setSelection(epcNo.getEpc().length());//将光标移至文字末尾
                        Toast.makeText(this, "写卡成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(e.getClass().getSimpleName(), e.getMessage());
                    } finally {
                        mIsPressed = false;
                        settingsButtonClickable();
                    }
                } else {
                    Toast.makeText(this, "未识别到标签", Toast.LENGTH_SHORT).show();
                    et_label.setText("");
                }
            } catch (RfidDeviceException e) {
                Toast.makeText(this, "识别标签失败", Toast.LENGTH_SHORT).show();
            } finally {
                mIsPressed = false;
                settingsButtonClickable();
            }
        }
    }

    //初始化sdk
    public void initSdkClickAction() {
        initSdkClickAction(18);
    }

    public void initSdkClickAction(int rfPower) {
        try {
            if (validationRfPower(rfPower)) {
                RfidSdkConfig cfg = new RfidSdkConfig();
                cfg.rfPower = rfPower;
                wlSdk.initSdk(cfg);
//                Toast.makeText(this, "初始化SDK成功", Toast.LENGTH_SHORT).show();
            }
        } catch (RfidDeviceException e) {
            Toast.makeText(this, "初始化SDK失败", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validationRfPower(int rfPower) {
        Boolean ret = true;
        if ((16 > rfPower) || (rfPower > 30)) {
            Toast.makeText(this, "RFID功率设置错误，不得小于16，大于30", Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    public void closeSdkAction() {
        wlSdk.destorySdk();
        Toast.makeText(this, "关闭SDK", Toast.LENGTH_SHORT).show();
    }

    ;

    //判断服务是否开启
    public static boolean isServiceRunning(String servicename, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            System.out.println(info.service.getClassName());
            if (servicename.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void scanBarCode(View view) {
        mIsPressed = true;
        settingsButtonNotClickable();
        scanThread.scan();
    }

    /**
     * 按键广播接收者 用于接受按键广播 触发扫描
     */
    private class KeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            // 为兼容早期版本机器
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown && !mIsPressed) {
                // 根据需要在对应的按键的键值中开启扫描,
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:
                        //重新获取产品信息
                        regainProductInfo(bt_regainProductInfo);
                        break;
                    case KeyEvent.KEYCODE_F2:
                        //提交数据
                        submit(bt_submit);
                        break;
                    case KeyEvent.KEYCODE_F3:
                        break;
                    case KeyEvent.KEYCODE_F4:
                        //清点标签
                        scanBarCode(null);
                        break;
                    case KeyEvent.KEYCODE_F6:
                        //扫描条码
                        checkRFID(null);
                        break;
                    default:
                        break;
                }
            }/*else {
                mIsPressed = false;
            }*/
        }
    }

    public void regainProductInfo(View view) {
        mIsPressed = true;
        settingsButtonNotClickable();
        new MyHttpGetAsyncTask(this, netHandler).execute(PRODUCT_INFO, "http://192.168.11.8:18080/rfid/getProductInfo");
    }

    public void submit(View view) {
        //成功后，清空物料条码list数据，清空标签 EditText ，清空条码 EditText
        mIsPressed = true;
        settingsButtonNotClickable();
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });*/
        if (Objects.isNull(productMaterial)) {
            Toast.makeText(this, "产品信息，产品唯一标识不能为空", Toast.LENGTH_SHORT).show();
        } else if (Objects.isNull(epcNo)) {
            Toast.makeText(this, "RFID标签不能为空", Toast.LENGTH_SHORT).show();
        } else if (Objects.isNull(materialsBarCode)) {
            Toast.makeText(this, "物料条码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            productMaterial.setEpc(epcNo.getEpc());
            productMaterial.setMaterials(listMaterialsBarCode);
            productMaterial.setTid(tid);
            //new MyHttpPostAsyncTask(this, netHandler).execute(PRODUCT_SUBMIT, "http://192.168.11.8:18080/rfid/submitProductInfo", productMaterial);
        }
        //dialog.dismiss();
    }

    /**
     * 定义一个类，让其继承AsyncTask这个类
     * Params: String类型，表示传递给异步任务的参数类型是String，通常指定的是URL路径
     * Progress: Integer类型，进度条的单位通常都是Integer类型
     * Result：byte[]类型，表示我们下载好的图片以字节数组返回
     *
     * @author
     */
    public class MyHttpGetAsyncTask extends AsyncTask<Object, Void, Result> {
        WeakReference<oneActivity> link;
        Handler mHandler;

        MyHttpGetAsyncTask(oneActivity ma, Handler handler) {
            super();
            this.link = new WeakReference<>(ma);
            this.mHandler = handler;
        }

        @Override
        protected Result doInBackground(Object... params) {
            String ret = null;
            CloseableHttpResponse chr = null;
            Result result = ResultGenerator.genOkResult();
            CloseableHttpClient chc = HttpClients.createDefault();
            try {
                HttpGet httpGet = new HttpGet((String) params[1]);
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).build();
                httpGet.setConfig(requestConfig);
                chr = chc.execute(httpGet);
                int status = chr.getStatusLine().getStatusCode();
                HttpEntity entity = chr.getEntity();
                ret = entity != null ? EntityUtils.toString(entity) : null;
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    result = ResultGenerator.genOkResult((Integer) params[0], ret);
                } else {
                    result = ResultGenerator.genFailedResult(status, chr.getStatusLine().toString());
                }
                chr.close();
                chc.close();
            } catch (Exception e) {
                result = ResultGenerator.genFailedResult(-1, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (this.link.get() != null) {
                Message msg = mHandler.obtainMessage();
                msg.what = result.getMsgType();
                msg.obj = result;
                mHandler.sendMessage(msg);
                this.link.clear();
            }
        }
    }

    public class MyHttpPostAsyncTask extends AsyncTask<Object, Void, Result> {
        WeakReference<oneActivity> link;
        Handler mHandler;

        MyHttpPostAsyncTask(oneActivity ma, Handler handler) {
            super();
            this.link = new WeakReference<>(ma);
            this.mHandler = handler;
        }

        @Override
        protected Result doInBackground(Object... params) {
            String ret = null;
            CloseableHttpResponse chr = null;
            Result result = ResultGenerator.genOkResult();
            CloseableHttpClient chc = HttpClients.createDefault();
            try {
                HttpPost httpPost = new HttpPost((String) params[1]);
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).build();
                httpPost.setConfig(requestConfig);
                StringEntity reqEntity = new StringEntity(JSON.toJSONString(params[2]), StandardCharsets.UTF_8);
                reqEntity.setContentType("application/json");
                httpPost.setEntity(reqEntity);
                chr = chc.execute(httpPost);
                int status = chr.getStatusLine().getStatusCode();
                HttpEntity resEntity = chr.getEntity();
                ret = resEntity != null ? EntityUtils.toString(resEntity) : null;
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    result = ResultGenerator.genOkResult((Integer) params[0], ret);
                } else {
                    result = ResultGenerator.genFailedResult(status, chr.getStatusLine().toString());
                }
                chr.close();
                chc.close();
            } catch (Exception e) {
                result = ResultGenerator.genFailedResult(-1, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (this.link.get() != null) {
                Message msg = mHandler.obtainMessage();
                msg.what = result.getMsgType();
                msg.obj = result;
                mHandler.sendMessage(msg);
                this.link.clear();
            }
        }
    }
}
