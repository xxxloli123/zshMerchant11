package com.example.xxxloli.zshmerchant.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.adapter.ShoppingCardAdapter;
import com.example.xxxloli.zshmerchant.greendao.Shop;
import com.example.xxxloli.zshmerchant.objectmodel.ShoppingCard;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.view.MyListView;
import com.google.gson.Gson;
import com.interfaceconfig.Config;
import com.sgrape.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;

public class ShoppingCrardActivity extends BaseActivity implements ShoppingCardAdapter.Callback {

    @BindView(R.id.no_data_tv)
    TextView noDataTv;
    @BindView(R.id.shopping_card_list)
    MyListView shoppingCardList;
    @BindView(R.id.ptr_frame_layout)
    PtrClassicFrameLayout ptrFrameLayout;
    @BindView(R.id.filling_v)
    View fillingV;

    private Shop shop = GreenDaoHelp.GetShop(this);
    private int page = 0;
    private ArrayList<ShoppingCard> shoppingCards;
    private ShoppingCardAdapter shoppingCardAdapter;

    @Override
    protected void init() {
        if (shop.getSpecial() == null) {
            noDataTv.setVisibility(View.VISIBLE);
            return;
        }
        switch (shop.getSpecial()) {
            case "self":
                loadData();
                break;
            case "headquarters":
                loadData();
                break;
            default:
                noDataTv.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadData() {
        Map<String, Object> map1 = new HashMap<>();
//        ,[shopId : ][ status: yes(已激活 ), no(未激活), Stop(暂停使用)][ startPage:][ pageSize: ]
        map1.put("shopId", shop.getId());
        map1.put("startPage", ++page);
        map1.put("pageSize", "20");
        map1.put("status", "");
        newCall(Config.Url.getUrl(Config.GetShoppingCard), map1);
    }

    @Override
    protected void initListener() {
        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(this);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(this);
        ptrFrameLayout.setFooterView(footer);
        ptrFrameLayout.addPtrUIHandler(footer);
        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        // default is false
        ptrFrameLayout.setPullToRefresh(false);

        // default is true
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);
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
    protected int getLayoutId() {
        return R.layout.activity_shopping_crard;
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        switch (tag.toString()) {
            case Config.GetShoppingCard:
                Log.e("GetShoppingCard", json + "");
                if (ptrFrameLayout != null) ptrFrameLayout.refreshComplete();
                JSONArray arr = json.getJSONObject("shoppingcards").getJSONArray("aaData");
                if (arr.length() == 0) return;
                if (shoppingCards == null) shoppingCards = new ArrayList<>();
                if (page == 1 && !shoppingCards.isEmpty()) shoppingCards.clear();
                Gson gson = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    shoppingCards.add(gson.fromJson(arr.getString(i), ShoppingCard.class));
                }
                if (shoppingCardAdapter != null) {
                    shoppingCardAdapter.refresh(shoppingCards);
                    return;
                }
                shoppingCardAdapter = new ShoppingCardAdapter(this, shoppingCards, this);
                shoppingCardList.setAdapter(shoppingCardAdapter);
                break;
            case Config.GetShoppingCardPwd:
                Toast.makeText(this, "密码已发送短信到创建者手机上了", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                shoppingCardAdapter.refresh(shoppingCards);
                break;
        }
    }

    @OnClick({R.id.back_rl, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.add:
                startActivity(new Intent(this, AddShoppingCardActivity.class));
                break;
        }
    }

    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.delete_bt:
                View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_sure, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog).create();
                TextView title = view1.findViewById(R.id.title);
                Button sure = view1.findViewById(R.id.sure_bt);
                Button cancel = view1.findViewById(R.id.cancel_bt);
                title.setText("确认删除吗");
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> params = new HashMap<>();
//                        参数：[userId, cardId]
                        params.put("cardId", shoppingCards.get((Integer) v.getTag()).getId());
                        params.put("userId", shop.getShopkeeperId());
                        newCall(Config.Url.getUrl(Config.DeleteShoppingCard), params);
                        shoppingCards.remove(shoppingCards.get((Integer) v.getTag()));
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
                break;
            case R.id.pwd_bt:
                Map<String, Object> params = new HashMap<>();
//                         cardId]
                params.put("cardId", shoppingCards.get((Integer) v.getTag()).getId());
                newCall(Config.Url.getUrl(Config.GetShoppingCardPwd), params);
                break;
        }
    }

}
