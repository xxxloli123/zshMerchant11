package com.example.xxxloli.zshmerchant.chat;

import android.content.Context;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.YWUIAPI;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.example.xxxloli.zshmerchant.util.ToastUtil;

/**
 * Created by xxxloli on 2018/2/1.
 */

public class SmgYWHelp {

    private static YWIMKit mIMKit;

    public static void initYWIMKit(String shopkeeperId) {
        mIMKit = YWAPI.getIMKitInstance(shopkeeperId, "24663801");
//        addPushMessageListener();
//        //添加联系人通知和更新监听 todo 在初始化后、登录前添加监听，离线的联系人系统消息才能触发监听器
//        addContactListeners();
    }

    public static void loginYW(String shopkeeperId, Context context) {
        IYWLoginService loginService = mIMKit.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(shopkeeperId, shopkeeperId);
        loginService.login(loginParam, new IWxCallback() {
            @Override
            public void onSuccess(Object... arg0) {
//                ToastUtil.showToast(context,"登录成功？");
            }

            @Override   //进展
            public void onProgress(int arg0) {
                // TODO Auto-generated method stub 生成方法存根
            }

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
                ToastUtil.showToast(context,"登录YW失败？ 错误代码:"+errCode+";"+description);
            }
        });
    }

    public static YWIMKit getYWIMKit() {
        return mIMKit;
    }
}
