package com.example.xxxloli.zshmerchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.objectmodel.OrderEntity;
import com.example.xxxloli.zshmerchant.view.MyListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/15.
 */

public class OrderListAdapter1 extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ArrayList<OrderEntity> orderEntities;
    private Context mContext;
    private Callback mCallback;
    private String lineOrderType;
    private BillAdapter.Callback callback2;
    private boolean isRefresh = false;

    //响应按钮点击事件,调用子定义接口，并传入View
    @Override
    public void onClick(View v) {
        mCallback.click(v);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     *
     * @author Ivan Xu
     *         2014-11-26
     */
    public interface Callback {
        public void click(View v);
    }

    public OrderListAdapter1(Context mContext, ArrayList<OrderEntity> orderEntities, Callback callback,
                             String lineOrderType, BillAdapter.Callback callback2) {
        this.orderEntities = orderEntities;
        this.mContext = mContext;
        mCallback = callback;
        this.lineOrderType = lineOrderType;
        this.callback2 = callback2;
    }

    //刷新Adapter
    public void refresh(ArrayList<OrderEntity> orderEntities) {
        this.orderEntities = orderEntities;//传入list，然后调用notifyDataSetChanged方法
        isRefresh = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderEntities == null ? 0 : orderEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return orderEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_handle_order, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.money.setText(intercept(orderEntities.get(i).getUserActualFee() + ""));
        holder.orderMoney.setText(orderEntities.get(i).getUserActualFee() + "");
        holder.downOrderTime.setText(orderEntities.get(i).getCreateDate());
        holder.orderNumber.setText(orderEntities.get(i).getOrderNumber());
//        所有新订单 All，ReserveOrder(预定单),NormalOrder(配送到家),ShopConsumption(到店消费单)UnPayed(待付款),
        if (orderEntities.get(i).getLineOrder().equals("ReserveOrder")) {
            holder.psdjText.setText("预定单");
            holder.psdjText.setBackgroundResource(R.drawable.round_yellow_background);
        } else if (orderEntities.get(i).getLineOrder().equals("ShopConsumption")) {
            holder.psdjText.setText("到店消费");
            holder.psdjText.setBackgroundResource(R.drawable.round_red_background);
            holder.money.setText(orderEntities.get(i).getTableNumber() + " 桌");
            holder.identifyingMoneyTv.setVisibility(View.GONE);
            holder.address.setVisibility(View.GONE);
        } else if (orderEntities.get(i).getStatus().equals("UnPayed")) {
            holder.psdjText.setText("未付款");
            holder.psdjText.setBackgroundResource(R.drawable.round_red_background);
            holder.reject.setVisibility(View.GONE);
            holder.receivingOrder.setText("修改价格");
        } else if (orderEntities.get(i).getLineOrder().equals("NormalOrder") && lineOrderType.equals("NormalOrder")) {
            if (orderEntities.get(i).getStatus().equals("UnDeliverGoods")) {
                holder.receivingOrder.setText("确认发货");
                holder.receivingOrder.setVisibility(View.VISIBLE);
            } else holder.receivingOrder.setVisibility(View.GONE);
            holder.reject.setText("打印订单");
            holder.deliveryTv.setText((orderEntities.get(i).getShopTransport().equals("no"))?"惠递配送":"商家配送");
            holder.infoLl.setVisibility(View.VISIBLE);
                holder.infoTv.setText(orderEntities.get(i).getStatus_value()+""+
                        ((orderEntities.get(i).getDeliveryStatesExplain() ==null)?
                                "":orderEntities.get(i).getDeliveryStatesExplain()));
        }
        String phone = (orderEntities.get(i).getReceiverPhone() != null) ? "" + orderEntities.get(i).getReceiverPhone() : " ";
        holder.namePhone.setText(((orderEntities.get(i).getReceiverName() != null) ?
                "" + orderEntities.get(i).getReceiverName() : " ") + phone);
        holder.address.setText(orderEntities.get(i).getEndStreet() + ""+orderEntities.get(i).getEndHouseNumber());
        holder.distance.setText(orderEntities.get(i).getShopUserDistance() + "km");

        if (!isRefresh) {
            if (orderEntities.size() < 4) {
                holder.remark.setVisibility(View.VISIBLE);
                holder.billList.setVisibility(View.VISIBLE);
                holder.packagingDispatchingRL.setVisibility(View.VISIBLE);
                holder.orderMoneyRL.setVisibility(View.VISIBLE);
                holder.line.setVisibility(View.VISIBLE);
            } else {
                holder.packagingDispatchingRL.setVisibility(View.GONE);
                holder.remark.setVisibility(View.GONE);
                holder.billList.setVisibility(View.GONE);
                holder.orderMoneyRL.setVisibility(View.GONE);
                holder.line.setVisibility(View.GONE);
            }
        }

        holder.packagingMoney.setText(orderEntities.get(i).getPackingprice() + "");
        holder.dispatchingMoney.setText(orderEntities.get(i).getDeliveryFee() + "");

        BillAdapter billAdapter = new BillAdapter(mContext, orderEntities.get(i).getGoods(),
                orderEntities.get(i).getId(), callback2);
        holder.billList.setAdapter(billAdapter);
//        holder.billList.setOnItemClickListener(this);
        holder.remark.setText("备注: " + orderEntities.get(i).getComment());
        if (orderEntities.get(i).getPayStatus_value().equals("已付款"))
            holder.isPayment.setVisibility(View.VISIBLE);
        else holder.isPayment.setVisibility(View.GONE);
        final ViewHolder finalHolder = holder;
        holder.LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder.remark.getVisibility() == View.GONE) {
                    finalHolder.remark.setVisibility(View.VISIBLE);
                    finalHolder.billList.setVisibility(View.VISIBLE);
                    finalHolder.packagingDispatchingRL.setVisibility(View.VISIBLE);
                    finalHolder.orderMoneyRL.setVisibility(View.VISIBLE);
                    finalHolder.line.setVisibility(View.VISIBLE);
                } else {
                    finalHolder.packagingDispatchingRL.setVisibility(View.GONE);
                    finalHolder.remark.setVisibility(View.GONE);
                    finalHolder.billList.setVisibility(View.GONE);
                    finalHolder.orderMoneyRL.setVisibility(View.GONE);
                    finalHolder.line.setVisibility(View.GONE);
                }
            }
        });
        holder.reject.setOnClickListener(this);
        holder.reject.setTag(i);
        holder.receivingOrder.setOnClickListener(this);
        holder.receivingOrder.setTag(i);
        //设置宽度和高度
        if (i == orderEntities.size() - 1) {
            finalHolder.remark.setVisibility(View.VISIBLE);
            finalHolder.billList.setVisibility(View.VISIBLE);
            finalHolder.packagingDispatchingRL.setVisibility(View.VISIBLE);
            finalHolder.orderMoneyRL.setVisibility(View.VISIBLE);
            finalHolder.line.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private String intercept(String s) {
        int p = s.lastIndexOf(".") + 3;
        return (s.length() > p) ? s.substring(0, p) + "..." : s;
    }

    public static class ViewHolder {
        @BindView(R.id.delivery_tv)
        TextView deliveryTv;
        @BindView(R.id.identifying_money_tv)
        TextView identifyingMoneyTv;
        @BindView(R.id.money)
        public TextView money;
        @BindView(R.id.down_order_time)
        TextView downOrderTime;
        @BindView(R.id.order_number)
        TextView orderNumber;
        @BindView(R.id.psdj_text)
        TextView psdjText;
        @BindView(R.id.name_phone)
        TextView namePhone;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.remark)
        public TextView remark;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.bill_list)
        MyListView billList;
        @BindView(R.id.packaging_money)
        TextView packagingMoney;
        @BindView(R.id.dispatching_money)
        TextView dispatchingMoney;
        @BindView(R.id.packaging_dispatchingRL)
        RelativeLayout packagingDispatchingRL;
        @BindView(R.id.t)
        TextView t;
        @BindView(R.id.red_packet_deduction)
        TextView redPacketDeduction;
        @BindView(R.id.discountsRL)
        RelativeLayout discountsRL;
        @BindView(R.id.isPayment)
        TextView isPayment;
        @BindView(R.id.order_money)
        public TextView orderMoney;
        @BindView(R.id.order_moneyRL)
        RelativeLayout orderMoneyRL;
        @BindView(R.id.info_tv)
        TextView infoTv;
        @BindView(R.id.info_ll)
        LinearLayout infoLl;
        @BindView(R.id.reject)
        Button reject;
        @BindView(R.id.receiving_order)
        Button receivingOrder;
        @BindView(R.id.LL)
        LinearLayout LL;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
