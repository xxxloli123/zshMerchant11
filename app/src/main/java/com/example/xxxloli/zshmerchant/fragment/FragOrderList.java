package com.example.xxxloli.zshmerchant.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xxxloli.zshmerchant.Activity.EditCommodityActivity;
import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.adapter.BillAdapter;
import com.example.xxxloli.zshmerchant.adapter.Classify2Adapter;
import com.example.xxxloli.zshmerchant.adapter.OrderListAdapter;
import com.example.xxxloli.zshmerchant.adapter.OrderListAdapter1;
import com.example.xxxloli.zshmerchant.base.AppInfo;
import com.example.xxxloli.zshmerchant.greendao.DBManagerShop;
import com.example.xxxloli.zshmerchant.greendao.Shop;
import com.example.xxxloli.zshmerchant.objectmodel.OrderEntity;
import com.example.xxxloli.zshmerchant.print.PrintQueue;
import com.example.xxxloli.zshmerchant.printutil.PrintOrderDataMaker;
import com.example.xxxloli.zshmerchant.printutil.PrinterWriter;
import com.example.xxxloli.zshmerchant.printutil.PrinterWriter58mm;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.util.OkHttp;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.example.xxxloli.zshmerchant.view.MyListView;
import com.google.gson.Gson;
import com.interfaceconfig.Config;
import com.sgrape.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;

/**
 * Created by sgrape on 2017/5/25.
 * e-mail: sgrape1153@gmail.com
 */
@SuppressLint("ValidFragment")
public class FragOrderList extends BaseFragment implements OrderListAdapter1.Callback,BillAdapter.Callback{

    @BindView(R.id.no_order)
    LinearLayout noOrder;
    @BindView(R.id.ptr_frame_layout)
    PtrClassicFrameLayout ptrFrameLayout;
    @BindView(R.id.listview)
    MyListView listview;
    Unbinder unbinder;

    private int page = 0;
    protected OrderListAdapter adapter;
    protected String lineOrderType;
    private DBManagerShop dbManagerShop;
    private Shop shop;
    private ArrayList<OrderEntity> orders;
    private OrderListAdapter1 orderListAdapter1;
    private BluetoothAdapter bluetoothAdapter;
    private int quantityP,priceP;
    private String edit;

    /**
     * bluetooth adapter
     */
    BluetoothAdapter mAdapter;

    public FragOrderList() {
    }

    public FragOrderList(String lineOrderType) {
        this(lineOrderType, true);
    }

    public FragOrderList(String lineOrderType, boolean enable) {
        super();
        this.lineOrderType = lineOrderType;
    }

    @Override
    protected void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        dbManagerShop = DBManagerShop.getInstance(getActivity());
        shop = dbManagerShop.queryById((long) 2333).get(0);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (listview.getAdapter() == null)
            listview.setAdapter(adapter);
        if (listview.getCount() > 0) {
            noOrder.setVisibility(View.GONE);
        } else {
            noOrder.setVisibility(View.VISIBLE);
        }
        initView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView != null && isVisibleToUser) {
            ptrFrameLayout=rootView.findViewById(R.id.ptr_frame_layout);
            ptrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrFrameLayout.autoRefresh(true);
                    loadData();
                    Log.e("page", "丢了个雷姆" + "  setUserVisibleHint加载");
                }
            }, 150);
        }
    }

    @Override
    public void loadData() {
        //订单处理 lineOrderType： 所有新订单All，ReserveOrder(预定单),NormalOrder(配送到家)
        // UnPayed(待付款),ShopConsumption(到店消费单)
//        if (!firstLoad && srl != null && !srl.isRefreshing()) return;
        firstLoad = false;
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", shop.getId());
        map.put("userId", shop.getShopkeeperId());
        map.put("startPage", ++page);
        map.put("pageSize", "15");
        map.put("lineOrderType", lineOrderType);
        newCall(Config.Url.getUrl(Config.GET_HandleOrder), map);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void fail(Object tag, int code, JSONObject json) throws JSONException {
        super.fail(tag, code, json);
//        Toast.makeText(getContext(), json.getString("message") + "code" + code, Toast.LENGTH_SHORT).show();
        assert ptrFrameLayout != null;
        ptrFrameLayout=rootView.findViewById(R.id.ptr_frame_layout);
        ptrFrameLayout.refreshComplete();
    }

    private void initView() {
         PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getActivity());
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        final PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(getActivity());
        ptrFrameLayout.setFooterView(footer);
        ptrFrameLayout.addPtrUIHandler(footer);
        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        // default is false
        ptrFrameLayout.setPullToRefresh(false);

        // default is true
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);
    }

    @Override
    protected void initListener() {
        ptrFrameLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, content, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 1500);

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 0;
                        loadData();
                    }
                }, 1500);
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
    public void click(final View v) {
        switch (v.getId()) {
            case R.id.reject://confirmShip(orders.get((Integer) v.getTag()));
                if (lineOrderType.equals("NormalOrder")&&orders.get(0).getLineOrder().equals("NormalOrder"))
                    PrintOrder(orders.get((Integer) v.getTag()));
                else reject(orders.get((Integer) v.getTag()));
                break;
            case R.id.receiving_order:
                //订单处理 lineOrderType： 所有新订单 All，ReserveOrder(预定单),NormalOrder(配送到家)
                // UnPayed(待付款),ShopConsumption(到店消费单)
                if (orders.get(0).getLineOrder().equals("NormalOrder")&&lineOrderType.equals("All"))
                    receive(orders.get((Integer) v.getTag()));
                else if (orders.get(0).getStatus().equals("UnPayed")){
                    editPrice(orders.get((Integer) v.getTag()));
                    priceP= (int) v.getTag();
                }else if (lineOrderType.equals("NormalOrder")){
                    confirmShip(orders.get((Integer) v.getTag()));
                }
                else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("receivedorder", "yes");
                    map.put("huidi", "no");
                    map.put("orderId", orders.get((Integer) v.getTag()).getId());
                    map.put("userId", shop.getShopkeeperId());
                    map.put("cause", "");
                    newCall(Config.Url.getUrl(Config.Receive_Reject), map);
                    PrintOrder( orders.get((Integer) v.getTag()));
                    //List移除某Item
                    orders.remove(orders.get((Integer) v.getTag()));
                    orderListAdapter1.notifyDataSetChanged();
                }
                break;
            case R.id.weight_tv:
                if (!lineOrderType.equals("NormalOrder")){
                    return;
                }
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_receive_or_reject, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
                Button sure = view.findViewById(R.id.sure_bt);
                Button cancel = view.findViewById(R.id.cancel_bt);
                TextView nameTv = view.findViewById(R.id.name_tv);
                nameTv.setVisibility(View.VISIBLE);
                nameTv.setText(v.getTag(R.id.billCommodityName)+"");
                final EditText causeEdit = view.findViewById(R.id.cause_edit);
                causeEdit.setHint("输入修改重量,不能为 0");
                causeEdit.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (causeEdit.getText().toString().isEmpty()) {
                            return;
                        }
                        if (Double.parseDouble(causeEdit.getText().toString())==0){
                            ToastUtil.showToast(getActivity(),"不能为 0");
                            return;
                        }
//                      参数：[orderId, userId, goodsId, goodsnum]
                        Map<String, Object> map = new HashMap<>();
                        map.put("orderId", v.getTag(R.id.orderId));
                        map.put("userId", shop.getShopkeeperId());
                        map.put("goodsId", v.getTag(R.id.billCommodityId));
                        map.put("goodsnum", causeEdit.getText().toString());
                        newCall(Config.Url.getUrl(Config.Edit_OrderQuantity), map);
                        edit=causeEdit.getText().toString();
                        quantityP= (int) v.getTag(R.id.billCommodityPosition);
                        alertDialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
                break;
        }
    }

    private void confirmShip(final OrderEntity orderEntity) {
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sure, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
        TextView title = view1.findViewById(R.id.title);
        Button sure = view1.findViewById(R.id.sure_bt);
        Button cancel = view1.findViewById(R.id.cancel_bt);
        title.setText("确认发货吗");
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> params = new HashMap<>();
//                [orderId, userId]
                params.put("orderId", orderEntity.getId());
                params.put("userId", shop.getShopkeeperId());
                newCall(Config.Url.getUrl(Config.Confirm_Ship), params);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view1);
        alertDialog.show();
    }

    private void editPrice(final OrderEntity orderEntity) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_receive_or_reject, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
        Button sure = view.findViewById(R.id.sure_bt);
        Button cancel = view.findViewById(R.id.cancel_bt);
        final EditText causeEdit = view.findViewById(R.id.cause_edit);
        causeEdit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        causeEdit.setHint("输入修改价格");
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(causeEdit.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入修改价格", Toast.LENGTH_SHORT).show();
                    return;
                }
//                参数：[orderId, cost, userId]
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderEntity.getId());
                map.put("userId", shop.getShopkeeperId());
                map.put("cost", causeEdit.getText());
                newCall(Config.Url.getUrl(Config.Edit_Price), map);
                edit=causeEdit.getText().toString();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void PrintOrder(OrderEntity orderEntity) {
        if (TextUtils.isEmpty(AppInfo.btAddress)||!bluetoothAdapter.isEnabled()) {
            ToastUtil.showToast(getActivity(), "如需打印订单请设置打印机功能");
            return;
        }
        if (mAdapter.getState() == BluetoothAdapter.STATE_OFF) {//蓝牙被关闭时强制打开
            mAdapter.enable();
            ToastUtil.showToast(getActivity(), "蓝牙被关闭请打开...");
        } else {
            PrintOrderDataMaker printOrderDataMaker = new PrintOrderDataMaker(getActivity(), "",
                    PrinterWriter58mm.TYPE_58, PrinterWriter.HEIGHT_PARTING_DEFAULT, orderEntity);
            ArrayList<byte[]> printData = (ArrayList<byte[]>) printOrderDataMaker
                    .getPrintData(PrinterWriter58mm.TYPE_58);
            PrintQueue.getQueue(getActivity()).add(printData);
        }
    }

    //                接单
    private void receive(final OrderEntity orderEntity) {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_receive_or_reject, null);
//        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
//        Button sure = view.findViewById(R.id.sure_bt);
//        Button cancel = view.findViewById(R.id.cancel_bt);
//        final RadioButton huidi = view.findViewById(R.id.huidi);
//        final RadioButton shopRb = view.findViewById(R.id.shop);
//        EditText causeEdit = view.findViewById(R.id.cause_edit);
//        causeEdit.setVisibility(View.GONE);
//        shopRb.setVisibility(View.VISIBLE);
//        huidi.setVisibility(View.VISIBLE);
//        sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////        商家接单 或拒单 receivedorder yes 接单 no 拒单 ；cause拒单原因;huidi配送yes no
//                Map<String, Object> map = new HashMap<>();
//                map.put("receivedorder", "yes");
//                if (huidi.isChecked()) map.put("huidi", "yes");
//                else map.put("huidi", "no");
//                map.put("orderId", orderEntity.getId());
//                map.put("userId", shop.getShopkeeperId());
//                map.put("cause", "");
//                newCall(Config.Url.getUrl(Config.Receive_Reject), map);
//                //List移除某Item
//                orders.remove(orderEntity);
//                orderListAdapter1.notifyDataSetChanged();
//                alertDialog.dismiss();
//                PrintOrder(orderEntity);
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog.setView(view);
//        alertDialog.show();
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sure, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
        TextView title = view1.findViewById(R.id.title);
        TextView messageTv = view1.findViewById(R.id.message_tv);
        Button sure = view1.findViewById(R.id.sure_bt);
        Button cancel = view1.findViewById(R.id.cancel_bt);
        title.setText("确认接单吗");
        messageTv.setVisibility(View.VISIBLE);
        messageTv.setText((orderEntity.getShopTransport().equals("no"))?"惠递配送":"商家配送");
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("receivedorder", "yes");
                if (orderEntity.getShopTransport().equals("no")) map.put("huidi", "yes");
                else map.put("huidi", "no");
                map.put("orderId", orderEntity.getId());
                map.put("userId", shop.getShopkeeperId());
                map.put("cause", "");
                newCall(Config.Url.getUrl(Config.Receive_Reject), map);
                PrintOrder(orderEntity);
                //List移除某Item
                orders.remove(orderEntity);
                orderListAdapter1.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view1);
        alertDialog.show();
    }

    //                    接毛线单啊
    private void reject(final OrderEntity orderEntity) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_receive_or_reject, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog).create();
        Button sure = view.findViewById(R.id.sure_bt);
        Button cancel = view.findViewById(R.id.cancel_bt);
        final EditText causeEdit = view.findViewById(R.id.cause_edit);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(causeEdit.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入拒绝原因", Toast.LENGTH_SHORT).show();
                    return;
                }
//        商家接单 或拒单 receivedorder yes 接单 no 拒单 ；cause拒单原因;huidi配送yes no
                Map<String, Object> map = new HashMap<>();
                map.put("receivedorder", "no");
                map.put("huidi", "yes");
                map.put("orderId", orderEntity.getId());
                map.put("userId", shop.getShopkeeperId());
                map.put("cause", causeEdit.getText().toString());
                newCall(Config.Url.getUrl(Config.Receive_Reject), map);
                //List移除某Item
                orders.remove(orderEntity);
                orderListAdapter1.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        shop=GreenDaoHelp.GetShop(getActivity());
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        switch (tag.toString()) {
            case Config.GET_HandleOrder:
                firstLoad = false;
                if (orders==null)orders = new ArrayList<>();
                Log.e("page", "丢了个雷姆" + lineOrderType);
                if (ptrFrameLayout!=null) ptrFrameLayout.refreshComplete();
                if (page == 1 && !orders.isEmpty()) orders.clear();
                JSONArray arr = json.getJSONObject("ordersInfo").getJSONArray("aaData");
                if (arr.length()==0)return;
                Log.e("GET_HandleOrder", "丢了个雷姆" + arr.get(0));
                Gson gson = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    final JSONObject cache = arr.getJSONObject(i);
                    orders.add(orders.size(), gson.fromJson(cache.toString(), OrderEntity.class));
                }
                noOrder=rootView.findViewById(R.id.no_order);
                if (orders.isEmpty()) noOrder.setVisibility(View.VISIBLE);
                else noOrder.setVisibility(View.GONE);
                if (orderListAdapter1!=null){
                    orderListAdapter1.refresh(orders);
                    return;
                }
                orderListAdapter1=new OrderListAdapter1(getActivity(),orders,this,lineOrderType,this);
                listview.setAdapter(orderListAdapter1);
                break;
            case Config.Edit_Price:
            case Config.Edit_OrderQuantity:
            case Config.Confirm_Ship:
            case Config.Receive_Reject:
                page = 0;
                loadData();
                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

//    //    getChildAt(position)方法获取到的是当前可见的第position项，获取的时候还需要做一个位置计算
//    public  void updataItem(int position){
//        int firstvisible = listview.getFirstVisiblePosition();
//        int lastvisibale = listview.getLastVisiblePosition();
//        if(position>=firstvisible&&position<=lastvisibale){
//            View view = listview.getChildAt(position - firstvisible);
//            OrderListAdapter1.ViewHolder viewHolder = (OrderListAdapter1.ViewHolder) view.getTag();
//            viewHolder.remark.setVisibility(View.VISIBLE);
//            //然后使用viewholder去更新需要更新的view。
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
