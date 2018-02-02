package com.example.xxxloli.zshmerchant.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xxxloli.zshmerchant.Activity.AccountManageActivity;
import com.example.xxxloli.zshmerchant.Activity.ResetPasswordActivity;
import com.example.xxxloli.zshmerchant.MainActivity;
import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.greendao.Account;
import com.example.xxxloli.zshmerchant.greendao.DBManagerAccount;
import com.example.xxxloli.zshmerchant.greendao.DBManagerShop;
import com.example.xxxloli.zshmerchant.greendao.DBManagerUser;
import com.example.xxxloli.zshmerchant.greendao.Shop;
import com.example.xxxloli.zshmerchant.greendao.User;
import com.example.xxxloli.zshmerchant.onepush.HelloOnePushReceiver;
import com.example.xxxloli.zshmerchant.util.Common;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.example.xxxloli.zshmerchant.view.TimeButton;
import com.google.gson.Gson;
import com.interfaceconfig.Config;
import com.sgrape.BaseFragment;
import com.sgrape.http.OkHttpCallback;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by sgrape on 2017/5/24.
 * e-mail: sgrape1153@gmail.com
 */

public class FragLoginSms extends BaseFragment {

    @BindView(R.id.phone_edit)
    EditText phoneEdit;
    @BindView(R.id.verification_code_edit)
    EditText verificationCodeEdit;
    @BindView(R.id.verification_code)
    TimeButton verificationCode;
    private String smsId = "";
    private String token,phone;
    private DBManagerUser dbManagerUser;
    private DBManagerAccount dbManagerAccount;
    private DBManagerShop dbManagerShop;
    private static final String TAG = FragLoginSms.class.getSimpleName();
    private String hint="\n可能会影响聊天功能\n如聊天功能异常请重新登录";

    public FragLoginSms() {
        super();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_login_sms;
    }

    @Override
    protected void init() {
        verificationCode.setEnabled(false);
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

    @Override
    protected void initListener() {
        super.initListener();
        verificationCode.setOnClickListener(this);
        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Common.matchePhone(phoneEdit.getText().toString().trim())) {
                    verificationCode.setEnabled(true);
                } else verificationCode.setEnabled(false);
            }
        });


    }

    @OnClick({ R.id.login_bt, R.id.login_resetPwd})

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_bt:
                login();
                break;
            case R.id.verification_code:
                final String phone = phoneEdit.getText().toString().trim();
                if (!Common.matchePhone(phone)) {
                    Toast.makeText(getContext(), "手机号格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                verificationCode.setTextAfters("").setTextAfter("秒后重发").setTextBefore("获取验证码").setLenght(60 * 1000);
                verificationCode.start();
                newCall(Config.Url.getUrl(Config.SMS_CODE), Common.getCode(phone, "0"));
                break;
            case R.id.login_resetPwd:
                startActivity(new Intent(getContext(), ResetPasswordActivity.class));
                break;
        }
    }

    private void login() {
        if (isEmpty(smsId)) {
            Toast.makeText(getContext(), "请先获取验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        phone = phoneEdit.getText().toString().trim();
        if (!Common.matchePhone(phone)) {
            Toast.makeText(getContext(), "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        String code = verificationCodeEdit.getText().toString().trim();
        if (isEmpty(code)) {
            Toast.makeText(getContext(), verificationCodeEdit.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }
//       用户登录   appuser/userlogin  参数{"userStr","smsStr" }, 用户登录：userStr：包含phone,password,type[Shopkeeper],phoneType(Andr
// oid-安卓  Ios-IOS),token(推送唯一标示)")
        //smsStr:id(必填)、code(必填)、phone(必填)

        try {
            JSONObject userJson = new JSONObject();
            userJson.put("phone", phone);
            userJson.put("type", "Shopkeeper");
            userJson.put("phoneType", "Android");
            userJson.put("token", token);
            userJson.put("gttoken", HelloOnePushReceiver.getGtToken());
            JSONObject smsJson = new JSONObject();
            smsJson.put("phone", phone);
            smsJson.put("code", code);
            smsJson.put("id", smsId);
            Map<String, Object> map = new HashMap<>();
            map.put("userStr", userJson);
            map.put("smsStr", smsJson);
            newCall(Config.Url.getUrl(Config.LOGIN), map);
            Log.e("LOGIN",map+"");
        } catch (JSONException e) {
            Toast.makeText(getContext(), "解析数据失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        switch (tag.toString()) {
            case Config.SMS_CODE:
                this.smsId = json.getJSONObject("sms").getString("id");
                break;
            case Config.LOGIN:
                if (json.getInt("statusCode")==200) {
                    dbManagerUser = DBManagerUser.getInstance(getActivity());
                    User user = new Gson().fromJson(json.getString("user"), User.class);
                    user.setWritId((long) 2333);
                    dbManagerShop=DBManagerShop.getInstance(getActivity());
                    if (dbManagerShop.queryById((long) 2333).size()!=0){
                        dbManagerShop.deleteById((long) 2333);
                    }
                    Log.e("LOGIN","丢了个雷姆"+json);
                    if (!dbManagerUser.queryById((long) 2333).isEmpty())dbManagerUser.deleteById((long) 2333);
                    Shop shop = new Gson().fromJson(json.getString("shop"), Shop.class);
                    shop.setWritId((long) 2333);
                    dbManagerUser.insertUser(user);
                    dbManagerShop.insertShop(shop);

                    dbManagerAccount = DBManagerAccount.getInstance(getActivity());
                    if (dbManagerAccount.queryByPhone(phone).isEmpty()) {
                        Account account=new Account();
                        account.setHead(shop.getShopImage());
                        account.setName(shop.getShopName());
                        account.setPhone(phone);
                        dbManagerAccount.insertAccount(account);
                    }else {
                        Account account=dbManagerAccount.queryByPhone(phone).get(0);
                        account.setPwd(null);
                        dbManagerAccount.updateUser(account);
                    }
                    start();
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
//                            ToastUtil.showToast(getActivity(),"openIM login success");
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

                }
                break;
        }
    }

    private void start() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        if (getActivity() != null) getActivity().finish();
    }
}
