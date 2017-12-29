package com.example.xxxloli.zshmerchant.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xxxloli.zshmerchant.Activity.ResetPasswordActivity;
import com.example.xxxloli.zshmerchant.MainActivity;
import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.greendao.Account;
import com.example.xxxloli.zshmerchant.greendao.DBManagerAccount;
import com.example.xxxloli.zshmerchant.greendao.DBManagerShop;
import com.example.xxxloli.zshmerchant.greendao.DBManagerUser;
import com.example.xxxloli.zshmerchant.greendao.Shop;
import com.example.xxxloli.zshmerchant.greendao.User;
import com.example.xxxloli.zshmerchant.util.Common;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.example.xxxloli.zshmerchant.view.ShSwitchView;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.interfaceconfig.Config;
import com.sgrape.BaseFragment;
import com.slowlife.lib.MD5;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by sgrape on 2017/5/24.
 * e-mail: sgrape1153@gmail.com
 */

public class FragLoginPwd extends BaseFragment {
    @BindView(R.id.phone_edit)
    EditText phoneEdit;
    @BindView(R.id.verification_code_edit)
    EditText verificationCodeEdit;
    @BindView(R.id.login_bt)
    Button loginBt;
    @BindView(R.id.login_resetPwd)
    TextView loginResetPwd;
    @BindView(R.id.switch_view)
    ShSwitchView switchView;
    Unbinder unbinder;
    @BindView(R.id.show)
    TextView show;
    private static final String TAG = FragLoginPwd.class.getSimpleName();

    private String token;
    private DBManagerUser dbManagerUser;
    private DBManagerAccount dbManagerAccount;
    private DBManagerShop dbManagerShop;
    private String phone,pwd;


    public FragLoginPwd() {
        super();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_login_pwd;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbManagerUser = DBManagerUser.getInstance(getActivity());
        dbManagerShop=DBManagerShop.getInstance(getActivity());
        dbManagerAccount = DBManagerAccount.getInstance(getActivity());
        switchView.setOn(false);
        switchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (isOn) {
                    //选中状态 显示明文--设置为可见的密码
                    verificationCodeEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    verificationCodeEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

/**
 *         //开启信鸽日志输出
 */
        XGPushConfig.enableDebug(getActivity(), true);

        //信鸽注册代码

        XGPushManager.registerPush(getActivity(), new XGIOperateCallback() {

            @Override
            public void onSuccess(Object data, int flag) {
                token = data.toString();
                Log.d("TPush", "注册成功，设备token为：" + data);

            }

            @Override
            public void onFail(Object data, int errCode, String msg) {

                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);

            }
        });
    }

    @OnClick({R.id.login_bt, R.id.login_resetPwd})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_resetPwd:
                startActivity(new Intent(getContext(), ResetPasswordActivity.class));
                break;
            case R.id.login_bt:
                login();
                break;
        }
    }

    private void login() {
        phone = phoneEdit.getText().toString().trim();
         pwd = verificationCodeEdit.getText().toString().trim();
        if (!Common.matchePhone(phone)) {
            Toast.makeText(getContext(), "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(pwd)) {
            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        JSONObject user = new JSONObject();
        try {
//            userStr：包含phone,password,type,phoneType(Android-安卓 Ios-IOS),token(推送唯一标示)
            user.put("phone", phone);
            user.put("password", MD5.md5Pwd(pwd));
            user.put("type", "Shopkeeper");
            user.put("phoneType", "Android");
            user.put("token", token);
            map.put("userStr", user);
            newCall(Config.Url.getUrl(Config.LOGIN), map);
            Log.e("token","丢了个雷姆"+token);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "解析数据失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        switch (tag.toString()) {
            case Config.LOGIN:
//                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                if (json.getInt("statusCode") == 200) {
                    if (dbManagerShop.queryById((long) 2333).size()!=0){
                        dbManagerShop.deleteById((long) 2333);
                    }
                    Log.e("LOGIN","丢了个雷姆"+json);
                    if (!dbManagerUser.queryById((long) 2333).isEmpty())dbManagerUser.deleteById((long) 2333);
                    User user = new Gson().fromJson(json.getString("user"), User.class);
                    user.setWritId((long) 2333);
                    Shop shop = new Gson().fromJson(json.getString("shop"), Shop.class);
                    shop.setWritId((long) 2333);
                    dbManagerUser.insertUser(user);
                    dbManagerShop.insertShop(shop);

                    if (dbManagerAccount.queryByPhone(phone).isEmpty()) {
                        EMClient.getInstance().logout(true);
                        Account account=new Account();
                        account.setHead(shop.getShopImage());
                        account.setName(shop.getShopName());
                        account.setPhone(phone);
                        account.setPwd(pwd);
                        dbManagerAccount.insertAccount(account);
                    }else {
                        Account account=dbManagerAccount.queryByPhone(phone).get(0);
                        if (!account.getPwd().equals(pwd))account.setPwd(pwd);
                    }
                    EMClient.getInstance().logout(true);

                    loginEase(user.getId(),user.getId());
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
//                    //OpenIM 开始登录
//                    String userid = shop.getShopkeeperId();
//                    String password = shop.getShopkeeperId();
//                    //初始化imkit
//                    LoginSampleHelper.getInstance().initIMKit(userid, "24663803");
//                    //通知栏相关的初始化
//                    NotificationInitSampleHelper.init();
//                    LoginSampleHelper loginHelper = LoginSampleHelper.getInstance();
//                    loginHelper.login_Sample(userid, password, "24663803", new IWxCallback() {
//
//                        @Override
//                        public void onSuccess(Object... arg0) {
////                            ToastUtil.showToast(getActivity(),"openIM login success");
////						YWIMKit mKit = LoginSampleHelper.getInstance().getIMKit();
////						EServiceContact contact = new EServiceContact("qn店铺测试账号001:找鱼");
////						LoginActivity.this.startActivity(mKit.getChattingActivityIntent(contact));
////                        mockConversationForDemo();
//                        }
//
//                        @Override
//                        public void onProgress(int arg0) {
//
//                        }
//
//                        @Override
//                        public void onError(int errorCode, String errorMessage) {
//                            if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登陆
//                                IMNotificationUtils.getInstance().showToast(getContext(), "游客");
//                            } else {
//                                YWLog.w(TAG, "登录失败，错误码：" + errorCode + "  错误信息：" + errorMessage);
//                                IMNotificationUtils.getInstance().showToast(getContext(), errorMessage);
//                            }
//                        }
//                    });

                    startActivity(new Intent(getActivity(), MainActivity.class));
                    if (getActivity() != null) getActivity().finish();
                }
                break;
        }
    }

    private void loginEase(String userName, String password) {
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }
            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int i, final String s) {
                Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(getActivity(), "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(getActivity(), "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(getActivity(), "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(getActivity(), "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(getActivity(), "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(getActivity(), "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(getActivity(), "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(getActivity(), "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(getActivity(), "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
