package com.example.xxxloli.zshmerchant.onepush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.xxxloli.zshmerchant.MainActivity;
import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.app.MyApplication;
import com.example.xxxloli.zshmerchant.chat.SmgYWHelp;
import com.example.xxxloli.zshmerchant.greendao.DaoUtil;
import com.example.xxxloli.zshmerchant.http.OkHttpCallback;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.util.OkHttp;
import com.example.xxxloli.zshmerchant.util.SoundUtils;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.interfaceconfig.Config;
import com.peng.one.push.OnePush;
import com.peng.one.push.entity.OnePushCommand;
import com.peng.one.push.entity.OnePushMsg;
import com.peng.one.push.receiver.BaseOnePushReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.umeng.socialize.utils.ContextUtil.getPackageName;

/**
 * Created by xxxloli on 2018/1/3.
 */

public class HelloOnePushReceiver extends BaseOnePushReceiver {

    public static String gtToken="";
    public static String getGtToken(){
        return gtToken;
    }

    @Override
    public void onReceiveNotificationClick(Context context, OnePushMsg msg) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

//    @Override
////    这是方法不总是调用，如果你的应用程序是死的，当你点击通知时，这个方法是不会调用的，所以在这个方法中不要做重要的事情
//    public void onReceiveNotification(Context context, OnePushMsg msg) {
//        super.onReceiveNotification(context, msg);
//        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
////        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.getContext());
//////        Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.mm);
////        PendingIntent pendingIntent=new PendingIntent();
////        mBuilder
//////                .setContentTitle("测试标题")//设置通知栏标题
//////                .setContentText(msg.g) //设置通知栏显示内容
//////                .setContentIntent(intent) //设置通知栏点击意图
//////
//////                //  .setNumber(number) //设置通知集合的数量
//////                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
//////                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
////                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
////                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
////                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
////                .setDefaults(R.raw))//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
////                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
////                .setAutoCancel(true)
////                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
////        assert mNotificationManager != null;
//        Notification notification = new Notification();
//        notification.sound=Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.a);
//        assert mNotificationManager != null;
//        ToastUtil.showToast(context,"推送到了");
//        mNotificationManager.notify(0, notification);
//    }

    @Override
    public void onReceiveMessage(Context context, OnePushMsg msg) {
        SoundUtils.playSoundByMedia(context, R.raw.a);
    }

    @Override
    public void onCommandResult(Context context, OnePushCommand onePushCommand) {
        //注册消息推送失败，再次注册
        if (onePushCommand.getType() == OnePush.TYPE_REGISTER && onePushCommand.getResultCode() == OnePush.RESULT_ERROR) {
            OnePush.register();
        }
        if (onePushCommand.getResultCode() == OnePushCommand.RESULT_OK){
            gtToken=onePushCommand.getToken();
            if (DaoUtil.isSaveShop(context)&&DaoUtil.isSaveShop(context)){
                Map<String, Object> params = new HashMap<>();
//            [userId, token, gttoken]
                params.put("userId", GreenDaoHelp.GetShop(context).getShopkeeperId());
                params.put("token", "");
                params.put("gttoken", gtToken);
                OkHttp.Call(Config.Url.getUrl(Config.UpdateToken), params, new OkHttpCallback.Impl() {
                    @Override
                    public void onSuccess(Object tag, JSONObject json) throws JSONException {
                        ToastUtil.showToast(context,"更新gtToken");
                    }

                    @Override
                    public void fail(Object tag, int code, JSONObject json) throws JSONException {
                    }

                    @NonNull
                    @Override
                    public Context getContext() {
                        return context;
                    }
                });
                SmgYWHelp.initYWIMKit(GreenDaoHelp.GetShop(context).getShopkeeperId());
                SmgYWHelp.loginYW(GreenDaoHelp.GetShop(context).getShopkeeperId(), context);
                SmgYWHelp.getYWIMKit().setEnableNotification(true);
            }
        }
        Log.e("onCommandResult",onePushCommand.getResultCode() == OnePushCommand.RESULT_OK ? "成功"+onePushCommand.getToken() :
                "code: "+onePushCommand.getResultCode()+" msg:失败");
    }

}
