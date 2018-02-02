package com.example.xxxloli.zshmerchant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.mobileim.aop.custom.IMConversationListUI;
import com.alibaba.mobileim.kit.OpenConversationFragment;
import com.alibaba.mobileim.utility.UserContext;
import com.example.xxxloli.zshmerchant.chat.SmgYWHelp;
import com.example.xxxloli.zshmerchant.fragment.ManageFragment;
import com.example.xxxloli.zshmerchant.fragment.OrderHandleFragment;
import com.example.xxxloli.zshmerchant.fragment.OrderInquireFragment;
import com.example.xxxloli.zshmerchant.util.CacheActivity;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.util.InstallUtils;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.interfaceconfig.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.menus_frame)
    FrameLayout menusFrame;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.order_handle)
    RadioButton orderHandle;
    @BindView(R.id.order_inquire)
    RadioButton orderInquire;
    @BindView(R.id.manage)
    RadioButton manage;
    @BindView(R.id.group)
    RadioGroup group;
    @BindView(R.id.chat)
    RadioButton chat;
    @BindView(R.id.id_et)
    EditText idEt;


    private Button selectButton;
    private RadioButton[] rButton;

    private final static String TAG_KEY = "TAG_KEY";
    private final static String HANDLE = "HANDLE";
    private final static String INQUIRE = "INQUIRE";
    private final static String MANAGE = "MANAGE";
    private final static String SHOP = "SHOP";

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            button((Button) v);
        }
    };

    private OrderHandleFragment orderHandleFragment;
    private OrderInquireFragment orderInquireFragment;
    private ManageFragment manageFragment;
    //    private ShopFragment shopFragment;
    private OpenConversationFragment shopFragment;
    private String s;
    private String hint = "\n可能会影响聊天功能\n如聊天功能异常请重新登录";
    private IMConversationListUI imConversationListUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CacheActivity.activityList.contains(this)) {
            CacheActivity.addActivity(this);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        update(getVersionCode(this));
        init(savedInstanceState);
//        PushManager.getInstance().initialize(this.getApplicationContext(), KDPushService.class);
//        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
//        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
//        // IntentService, 必须在 AndroidManifest 中声明)
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), KDIntentService.class);
//        initEM();
    }

//    private void initEM() {
//        loginEase(GreenDaoHelp.GetShop(this).getShopkeeperId(),GreenDaoHelp.GetShop(this).getShopkeeperId());
//        Timer timer=new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                EMClient.getInstance().chatManager().addMessageListener(messageListener);
//            }
//        },1000);
//    }

//    EMMessageListener messageListener=new EMMessageListener() {
//        @Override
//        public void onMessageReceived(List<EMMessage> list) {
//            EaseUI.getInstance().getNotifier().onNewMesg(list);
//        }
//
//        @Override
//        public void onCmdMessageReceived(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageRead(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageDelivered(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageRecalled(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageChanged(EMMessage emMessage, Object o) {
//
//        }
//    };

//    private void loginEase(String userName, String password) {
//        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
//            @Override
//            public void onSuccess() {
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
//                Log.d("main", "登录聊天服务器成功！");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToast(MainActivity.this, "登录成功");
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//            }
//
//            @Override
//            public void onError(final int i, final String s) {
//                Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        switch (i) {
//                            //用户已登录 200
//                            case EMError.USER_ALREADY_LOGIN:
//                                Toast.makeText(MainActivity.this, "用户已登录", Toast.LENGTH_LONG).show();
//                                break;
//                            // 网络异常 2
//                            case EMError.NETWORK_ERROR:
//                                Toast.makeText(MainActivity.this, "网络错误 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 无效的用户名 101
//                            case EMError.INVALID_USER_NAME:
//                                Toast.makeText(MainActivity.this, "无效的用户名 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 无效的密码 102
//                            case EMError.INVALID_PASSWORD:
//                                Toast.makeText(MainActivity.this, "无效的密码 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 用户认证失败，用户名或密码错误 202
//                            case EMError.USER_AUTHENTICATION_FAILED:
//                                Toast.makeText(MainActivity.this, "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 用户不存在 204
//                            case EMError.USER_NOT_FOUND:
//                                Toast.makeText(MainActivity.this, "用户不存在 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 无法访问到服务器 300
//                            case EMError.SERVER_NOT_REACHABLE:
//                                Toast.makeText(MainActivity.this, "无法访问到服务器 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 等待服务器响应超时 301
//                            case EMError.SERVER_TIMEOUT:
//                                Toast.makeText(MainActivity.this, "等待服务器响应超时 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 服务器繁忙 302
//                            case EMError.SERVER_BUSY:
//                                Toast.makeText(MainActivity.this, "服务器繁忙 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            // 未知 Server 异常 303 一般断网会出现这个错误
//                            case EMError.SERVER_UNKNOWN_ERROR:
//                                Toast.makeText(MainActivity.this, "未知的服务器异常 code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                            default:
//                                Toast.makeText(MainActivity.this, "ml_sign_in_failed code: " + i + ", message:" + s + hint, Toast.LENGTH_LONG).show();
//                                break;
//                        }
//                    }
//                });
//            }
//        });
//    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void update(final int versionCode) {
        Request req = new Request.Builder()
                .tag("")
                .url(Config.Url.getUrl(Config.UPDATE)).build();
        new OkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    int ver = json.getJSONObject("appVersion").getInt("version");
                    if (ver > versionCode) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                upData2();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private static Handler handler = new Handler();

    public void upData2() {
        Toast.makeText(this, "检测到新的版本，自动为您下载。。。", Toast.LENGTH_SHORT).show();
        new InstallUtils(this, Config.Url.getUrl("/slowlife/share/appdownload?type=android_zsh_shop"), "掌升活(商家端)",
                new InstallUtils.DownloadCallBack() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(String path) {
                        if (orderHandle != null) orderHandle.setText("订单处理");
                        InstallUtils.installAPK(MainActivity.this, path, getPackageName() + ".provider", new InstallUtils.InstallCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this, "正在安装程序", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                Toast.makeText(MainActivity.this, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e("onFail", "安装失败:" + e.toString());
                            }
                        });
                    }

                    @Override
                    public void onLoading(long total, long current) {
                        if (orderHandle != null)
                            orderHandle.setText((int) (current * 100 / total) + "%");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(MainActivity.this, "下载失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("onFail", "下载失败:" + e.toString());
                    }
                }).downloadAPK();
    }

    /*
     * onSaveInstanceState方法会在什么时候被执行，有这么几种情况：
    1、当用户按下HOME键时。
    这是显而易见的，系统不知道你按下HOME后要运行多少其他的程序，自然也不知道activity A是否会被销毁，故系统会调用onSaveInstanceState，让用户有机会保存某些非永久性的数据。以下几种情况的分析都遵循该原则
    2、长按HOME键，选择运行其他的程序时。
    3、按下电源按键（关闭屏幕显示）时。
    4、从activity A中启动一个新的activity时。
    5、屏幕方向切换时，例如从竖屏切换到横屏时。
    onSaveInstanceState的调用遵循一个重要原则，即当系统“未经你许可”时销毁了你的activity，则onSaveInstanceState会被系统调用，这是系统的责任，因为它必须要提供一个机会让你保存你的数据
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_KEY, getIndex());
    }

    private int getIndex() {
        int index = -1;
        for (int i = 0; i < rButton.length; i++) {
            if (selectButton == rButton[i]) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 初始化控件
     */
    private void init(Bundle savedInstanceState) {
        orderHandle.setOnClickListener(onClickListener);
        orderInquire.setOnClickListener(onClickListener);
        manage.setOnClickListener(onClickListener);
        chat.setOnClickListener(onClickListener);
        rButton = new RadioButton[]{orderHandle, orderInquire, manage, chat};

        int index = 0;
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(TAG_KEY, 0);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                index = bundle.getInt(TAG_KEY, 0);
            }
        }

        if (index < 0 || index > rButton.length - 1) {
            index = 0;
        }
        button(rButton[index]);
    }

    /**
     * 点击时需要切换相应的页面
     * @param b 当前需要传入的按钮
     */
    private void button(Button b) {
        if (selectButton != null && selectButton == b) {
            return;
        }
        selectButton = b;
        Bundle bundle = new Bundle();
        if (selectButton == orderHandle) {
            if (orderHandleFragment == null) {
                orderHandleFragment = addFragment(OrderHandleFragment.class, HANDLE, bundle);
            }
            changeFragment(orderHandleFragment);
        } else if (selectButton == orderInquire) {
            if (orderInquireFragment == null) {
                orderInquireFragment = addFragment(OrderInquireFragment.class, INQUIRE, bundle);
            }
            changeFragment(orderInquireFragment);
        } else if (selectButton == manage) {
            if (manageFragment == null) {
                manageFragment = addFragment(ManageFragment.class, MANAGE, bundle);
            }
            changeFragment(manageFragment);
        } else if (selectButton == chat) {
            if (shopFragment == null) {
                bundle.putSerializable(UserContext.EXTRA_USER_CONTEXT_KEY, SmgYWHelp.getYWIMKit().getUserContext());
                shopFragment = addFragment(SmgYWHelp.getYWIMKit().getConversationFragmentClass(), SHOP, bundle);
            }
            changeFragment(shopFragment);
        }
    }

    /**
     * 添加管理fragment 并返回
     * @param fragmentClass 传入的fragment类
     * @param tag           fragment标识
     * @param bundle
     * @return
     */
    private <T> T addFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle bundle) {
        //Fragment 管理者
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentClass.getName(), bundle);
            if (bundle != null) {
                bundle.putString("fragment", fragmentClass.getName());
                fragment.setArguments(bundle);
            }

            transaction.add(R.id.menus_frame, fragment, tag);
            transaction.commit();
        }
        return (T) fragment;
    }

    /**
     * 切换 fragment
     * @param fragment 传入当前切换的 fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment homeFragment = manager.findFragmentByTag(HANDLE);
        if (homeFragment != null && homeFragment != fragment) {
            transaction.hide(homeFragment);
            homeFragment.setUserVisibleHint(false);
        }

        Fragment findFragment = manager.findFragmentByTag(INQUIRE);
        if (findFragment != null && findFragment != fragment) {
            transaction.detach(findFragment);
            findFragment.setUserVisibleHint(false);
        }

        Fragment orderFragment = manager.findFragmentByTag(MANAGE);
        if (orderFragment != null && orderFragment != fragment) {
            transaction.detach(orderFragment);
            orderFragment.setUserVisibleHint(false);
        }

        Fragment orderFragment1 = manager.findFragmentByTag(SHOP);
        if (orderFragment1 != null && orderFragment1 != fragment) {
            transaction.detach(orderFragment1);
            orderFragment1.setUserVisibleHint(false);
        }

        if (fragment != null) {
            if (fragment != homeFragment && fragment.isDetached()) {
                transaction.attach(fragment);
            } else if (fragment == homeFragment && fragment.isHidden()) {
                transaction.show(fragment);
            }
            fragment.setUserVisibleHint(true);
        }
        transaction.commit();
    }

    public void start(View view) {
         String target = "2c9ad84360d5e2d20160d998657f032a"; //消息接收者ID
        if (!idEt.getText().toString().equals(""))target=idEt.getText().toString();
        final String appkey = "24663801"; //消息接收者 appKey
        Intent intent = SmgYWHelp.getYWIMKit().getChattingActivityIntent(target, appkey);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmgYWHelp.initYWIMKit(GreenDaoHelp.GetShop(this).getShopkeeperId());
        SmgYWHelp.loginYW(GreenDaoHelp.GetShop(this).getShopkeeperId(), this);
        SmgYWHelp.getYWIMKit().setEnableNotification(true);
    }
}
