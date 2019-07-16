package com.tjkcht.shenyangService;

import com.tjkcht.rfid.sdk.ContainerEntity;
import com.tjkcht.rfid.sdk.IWulingRfidHandsetSdk;
import com.tjkcht.rfid.sdk.RfidTag;
import com.tjkcht.rfid.sdk.WulingRfidSdk;
import com.tjkcht.shenyangapp.oneActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.pda.scan.ScanThread;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by APPLE on 2019/2/18.
 */

public class MyServer extends NanoHTTPD {

    private IWulingRfidHandsetSdk wlSdk = WulingRfidSdk.getInstance();
    public static String barcode;


    public MyServer(int port) {

        super(port);

    }

    @Override
    public Response serve(IHTTPSession session) {

        StringBuilder builder = new StringBuilder();

        if ("/inventoryTags".equals(session.getUri())) {
            Map<String, String> headers = session.getHeaders();
            JSONArray array = new JSONArray();
            String rfidInfoJSON = "";
            try {
                Set<RfidTag> tags = wlSdk.inventoryTags();
                for (RfidTag tag : tags) {
                    JSONObject obj = new JSONObject();
                    obj.put("EpcHex", "" + tag.getEpcHex());
                    obj.put("MinRessi", tag.getMinRssi());
                    obj.put("MaxRessi", tag.getMaxRssi());
                    obj.put("InventoryCounts", tag.getInventoryCounts());
                    array.put(obj);
                }
                rfidInfoJSON = array.toString();

                Response res = null;
                if (!isLocalRequest(session)) {
                    rfidInfoJSON = "获取RFID标签信息失败";
                    res = newFixedLengthResponse(rfidInfoJSON);
                } else {
                    res = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", rfidInfoJSON);
                }

                res.addHeader("Access-Control-Allow-Origin", "*");
                res.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
                res.addHeader("Access-Control-Allow-Credentials", "true");
                res.addHeader("Access-Control-Allow-Headers", headers.get("Access-Control-Request-Headers".toLowerCase()));
                res.addHeader("Access-Control-Max-Age", "" + 42 * 60 * 60);

                return res;

            } catch (Exception e) {
                rfidInfoJSON = "获取RFID信息失败";

                return newFixedLengthResponse(rfidInfoJSON);

            }

             //---------------------------------------------------------------------------------inventoryTags---------------------------------------------------------------
        } else if ("/scanBarcode".equals(session.getUri())) {

            /***
             *
             * GX
             *---------------------------------------------------------------------------------scanBarcode---------------------------------------------------------------
             */

            System.out.println("------------------------------------开始执行scanBarcode------------------------------------------");
            Map<String, String> headers = session.getHeaders();
            ScanThread scanThread;
            String barcodeInfo_Json = "";
            barcode = "";

            //获取oneActivity中静态的scanThread，调用scan方法打开红外模块，设置主线程睡眠2.5秒，保证能对焦成功
            try {
                oneActivity.scanThread.scan();
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //测试验证扫码结果
//            System.out.println("--------------------------------Barcode:" + barcode + "--------------------------------");

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("Barcode", barcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Response response = null;
            barcodeInfo_Json = jsonObject.toString();

            //判断非本机不能访问
            if (!isLocalRequest(session)) {
                barcodeInfo_Json = "No access!";
                response = newFixedLengthResponse(barcodeInfo_Json);
            } else {
                response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", barcodeInfo_Json);
            }
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers", headers.get("Access-Control-Request-Headers".toLowerCase()));
            response.addHeader("Access-Control-Max-Age", "" + 42 * 60 * 60);

            System.out.println("Headers:" + headers.get("Access-Control-Request-Headers".toLowerCase()));

            return response;

            //---------------------------------------------------------------------------------scanBarcode---------------------------------------------------------------
            //---------------------------------------------------------------------------------writeEPC---------------------------------------------------------------

        } else if ("/writeEPC".equals(session.getUri())) {

            System.out.println("----------------------------writeEPC-----------------------------");
            String containerEntityJSON = "";
            Map<String, String> headers = session.getHeaders();
            String beRecordedEpc = session.getParameters().get("epcNo").get(0);
            System.out.println("beEPC:" + beRecordedEpc);
            JSONObject obj = new JSONObject();

            //判断非本机不能访问
            if (!isLocalRequest(session)) {
                return newFixedLengthResponse("禁止写入");
            }

            try {
                String pattern = "^\\d{24}$";
                boolean isMatch = Pattern.matches(pattern, beRecordedEpc);
                if (isMatch) {
                    Set<RfidTag> tags = wlSdk.inventoryTags();
                    for (RfidTag tag : tags) {
                        String tagEpc = tag.getEpcHex();
                        String newPassword = wlSdk.generateCardPassword(tagEpc);
                        wlSdk.issueAccessPassword(tagEpc, newPassword);
                        String tid = wlSdk.getTID(tagEpc, newPassword);
                        wlSdk.writeEPC(tagEpc, newPassword, beRecordedEpc);
                        obj.put("BeRecordedEpc", beRecordedEpc);
                        obj.put("Tid", tid);
                        break;
                    }
                    containerEntityJSON = obj.toString();
                    Response response = null;
                    response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", containerEntityJSON);
                    response.addHeader("Access-Control-Allow-Origin", "*");
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
                    response.addHeader("Access-Control-Allow-Credentials", "true");
                    response.addHeader("Access-Control-Allow-Headers", headers.get("Access-Control-Request-Headers".toLowerCase()));
                    response.addHeader("Access-Control-Max-Age", "" + 42 * 60 * 60);

                    System.out.println("Headers:" + headers.get("Access-Control-Request-Headers".toLowerCase()));

                    return response;

                } else {
                    containerEntityJSON = "请输入24位纯数字的EPC码";

                    return newFixedLengthResponse(containerEntityJSON);

                }
            } catch (Exception e) {
                containerEntityJSON = "写入EPC编码失败";

                return newFixedLengthResponse(containerEntityJSON);

            }

        } else {
            builder.append("<!DOCTYPE html><html><body>");
            builder.append("请输入正确的URI");
            builder.append("</body></html>\n");

            return newFixedLengthResponse(builder.toString());

        }

    }

    //判断请求是否来自本机
    private Boolean isLocalRequest(IHTTPSession session) {
        //获取访问者IP地址
        String remoteHostIp = session.getRemoteIpAddress();
        System.out.println("RemoteHostIP:" + remoteHostIp);

        String remoteHostName = session.getRemoteHostName();
        System.out.println("RemoteHostName:" + remoteHostName);

        //获取本机IP地址（动态IP，非127.0.0.1）
        String localIpAddress = getLocalIpAddress();
        System.out.println("IP:" + localIpAddress);

        //判断非本机不能访问
        if ("".equals(remoteHostIp) | !"localhost".equals(remoteHostName) & !localIpAddress.equals(remoteHostIp) & !"127.0.0.1".equals(remoteHostIp)) {
            return false;
        }
        return true;
    }

    //获取本机IP地址 非127.0.0..1
    public String getLocalIpAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;

    }

}
