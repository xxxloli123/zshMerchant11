package com.example.xxxloli.zshmerchant;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.xxxloli.zshmerchant.base.AppInfo;
import com.example.xxxloli.zshmerchant.http.OkHttpCallback;
import com.example.xxxloli.zshmerchant.objectmodel.OrderEntity;
import com.example.xxxloli.zshmerchant.print.PrintQueue;
import com.example.xxxloli.zshmerchant.printutil.PrintOrderDataMaker;
import com.example.xxxloli.zshmerchant.printutil.PrinterWriter;
import com.example.xxxloli.zshmerchant.printutil.PrinterWriter58mm;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.util.OkHttp;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.google.gson.Gson;
import com.interfaceconfig.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PrintService extends Service implements OkHttpCallback.Impl {

    private  Timer timer;

    BluetoothAdapter mAdapter;

    public PrintService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                商家自动打印：获取待打印订单
//                参数：[shopId]
                Map<String, Object> params = new HashMap<>();
                params.put("shopId", GreenDaoHelp.GetShop(PrintService.this).getId());
                OkHttp.Call(Config.Url.getUrl(Config.GET_AutoOrder), params,PrintService.this);
//                Message message = new Message( );
//                message.what = 1;
//                handler.sendMessage(message);
            }
        },0,1000*3);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static boolean isServiceRunning(Context mContext) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClass().equals(PrintService.class) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        ToastUtil.showToast(this,json.getString("message"));
        JSONArray arr = json.getJSONArray("listorder");
        if (arr.length() == 0) return;
        if (TextUtils.isEmpty(AppInfo.btAddress)){
            ToastUtil.showToast(this,"已自动接单。。。\n如需打印订单请开启打印机功能");
            return;
        }
        for (int i=0;i<arr.length();i++){
            if (mAdapter.getState()== BluetoothAdapter.STATE_OFF ){//蓝牙被关闭时强制打开
                mAdapter.enable();
                ToastUtil.showToast(this,"蓝牙被关闭请打开...");
            }else {
                OrderEntity orderEntity=new Gson().fromJson(arr.getString(arr.length() - i - 1), OrderEntity.class);
                PrintOrderDataMaker printOrderDataMaker = new PrintOrderDataMaker(this,"",
                        PrinterWriter58mm.TYPE_58, PrinterWriter.HEIGHT_PARTING_DEFAULT,orderEntity);
                ArrayList<byte[]> printData = (ArrayList<byte[]>) printOrderDataMaker
                        .getPrintData(PrinterWriter58mm.TYPE_58);
                PrintQueue.getQueue(this).add(printData);
            }
        }
    }

    @Override
    public void fail(Object tag, int code, JSONObject json) throws JSONException {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    // 停止定时器
    public  void stopTimer(){
        if(timer != null){
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }
}
