package com.example.xxxloli.zshmerchant.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.slowlife.xgpush.XgReceiver;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/9/13.
 */

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    public static Context mContext;
    //云旺OpenIM的DEMO用到的Application上下文实例
    private static Context sContext;
    public static Context getContext(){
        return sContext;
    }

    // 记录是否已经初始化
    private boolean isInit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        // 初始化环信SDK
        initEasemob();

        if (isMainProcess()) {
            // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
            // 收到通知时，会调用本回调函数。
            // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
            // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
            XGPushManager.setNotifactionCallback(new XGPushNotifactionCallback() {

                @Override
                public void handleNotify(XGNotifaction xGNotifaction) {
                    Log.i("test", "处理信鸽通知：" + xGNotifaction);
                    // 获取标签、内容、自定义内容
                    String title = xGNotifaction.getTitle();
                    String content = xGNotifaction.getContent();
                    String customContent = xGNotifaction.getCustomContent();
                    XgReceiver.PushObserver.notifyPsuh("title=" + title +
                            "\ncontent=" + content +
                            "\ncustomContent=" + customContent);
                    // 其它的处理
                    // 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
                    xGNotifaction.doNotify();
                }
            });
        }

        //todo Application.onCreate中，首先执行这部分代码，以下代码固定在此处，不要改动，这里return是为了退出Application.onCreate！！！
//        if(mustRunFirstInsideApplicationOnCreate()){
//            //todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
//            return;
//        }
//        initYWSDK(this);
    }

    /**
     *
     */
    private void initEasemob() {
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
//        options.setAutoTransferMessageAttachments(true);
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
//        options.setAutoDownloadThumbnail(true);
//...
//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        EaseUI.getInstance().init(this, options);
    }

//
//    public static void initYWSDK(Application application){
//        //todo 只在主进程进行云旺SDK的初始化!!!
//        if(SysUtil.isMainProcess()){
//            //TODO 注意：--------------------------------------
//            //  以下步骤调用顺序有严格要求，请按照示例的步骤（todo step）
//            // 的顺序调用！
//            //TODO --------------------------------------------
//
//            // ------[todo step1]-------------
//            //［IM定制初始化］，如果不需要定制化，可以去掉此方法的调用
//            //todo 注意：由于增加全局初始化，该配置需最先执行！
//
//
//            // ------[todo step2]-------------
//            //SDK初始化
//            LoginSampleHelper.getInstance().initSDK_Sample(application);
//
//            //后期将使用Override的方式进行集中配置，请参照YWSDKGlobalConfigSample
//            YWAPI.enableSDKLogOutput(true);
//        }
//    }
//
//    private boolean mustRunFirstInsideApplicationOnCreate() {
//        //必须的初始化
//        SysUtil.setApplication(this);
//        sContext = getApplicationContext();
//        return SysUtil.isTCMSServiceProcess(sContext);
//    }

//    /**
//     *
//     */
//    private void initEasemob() {
//        // 获取当前进程 id 并取得进程名
//        int pid = android.os.Process.myPid();
//        String processAppName = getAppName(pid);
//        /**
//         * 如果app启用了远程的service，此application:onCreate会被调用2次
//         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
//         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
//         */
//        if (processAppName == null || !processAppName.equalsIgnoreCase(mContext.getPackageName())) {
//            // 则此application的onCreate 是被service 调用的，直接返回
//            return;
//        }
//        if (isInit) {
//            return;
//        }
//
//        // 调用初始化方法初始化sdk
//        EMClient.getInstance().init(mContext, initOptions());
//
//        // 设置开启debug模式
//        EMClient.getInstance().setDebugMode(true);
//
//        // 设置初始化已经完成
//        isInit = true;
//    }
//
//    /**
//     * SDK初始化的一些配置
//     * 关于 EMOptions 可以参考官方的 API 文档
//     * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
//     */
//    private EMOptions initOptions() {
//
//        EMOptions options = new EMOptions();
//        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
//        // options.setAppKey("lzan13#hxsdkdemo");
//        // 设置自动登录
//        options.setAutoLogin(true);
//        // 设置是否需要发送已读回执
//        options.setRequireAck(true);
//        // 设置是否需要发送回执，
//        options.setRequireDeliveryAck(true);
//        // 设置是否需要服务器收到消息确认
////        options.setRequireServerAck(true);
//        // 设置是否根据服务器时间排序，默认是true
//        options.setSortMessageByServerTime(false);
//        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
//        options.setAcceptInvitationAlways(false);
//        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
//        options.setAutoAcceptGroupInvitation(false);
//        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
//        options.setDeleteMessagesAsExitGroup(false);
//        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
//        options.allowChatroomOwnerLeave(true);
//        // 设置google GCM推送id，国内可以不用设置
//        // options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
//        // 设置集成小米推送的appid和appkey
//        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

//        return options;
//    }
//
//    /**
//     * 根据Pid获取当前进程的名字，一般就是当前app的包名
//     *
//     * @param pid 进程的id
//     * @return 返回进程的名字
//     */
//    private String getAppName(int pid) {
//        String processName = null;
//        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        List list = activityManager.getRunningAppProcesses();
//        Iterator i = list.iterator();
//        while (i.hasNext()) {
//            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
//            try {
//                if (info.pid == pid) {
//                    // 根据进程的信息获取当前进程的名字
//                    processName = info.processName;
//                    // 返回当前进程名
//                    return processName;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        // 没有匹配的项，返回为null
//        return null;
//    }

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
