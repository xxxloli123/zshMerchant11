package com.example.xxxloli.zshmerchant.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.http.OkHttpCallback;
import com.example.xxxloli.zshmerchant.objectmodel.BillCommodity;
import com.example.xxxloli.zshmerchant.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/15.
 */

public class BillAdapter extends BaseAdapter implements OkHttpCallback.Impl, View.OnClickListener {

    private ArrayList<BillCommodity> billCommodities;
    private Context context;
    private String orderId;
    private Callback mCallback;

    public BillAdapter(Context context, ArrayList<BillCommodity> billCommodities, String id) {
        this.billCommodities = billCommodities;
        this.context = context;
        orderId = id;
    }

    public BillAdapter(Context context, ArrayList<BillCommodity> billCommodities, String id, Callback callback) {
        this.billCommodities = billCommodities;
        this.context = context;
        orderId = id;
        mCallback = callback;
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

    @Override
    public int getCount() {
        return billCommodities == null ? 0 : billCommodities.size();
    }

    @Override
    public Object getItem(int i) {
        return billCommodities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bill, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(billCommodities.get(i).getGoodsName());
        if (billCommodities.get(i).getProductcategory()!=null)
        holder.specificationTv.setText(billCommodities.get(i).getProductcategory() + "　 " +
                billCommodities.get(i).getStandardName());
        else holder.specificationTv.setText("");
        holder.quantityText.setText( billCommodities.get(i).getGoodsnum()+"份");
        holder.priceText.setText(billCommodities.get(i).getGoodsPrice() + "");
        if (billCommodities.get(i).getGoodsrealweight()>0)
            holder.weightTv.setText("重量  "+billCommodities.get(i).getGoodsrealweight() );
        else {
            double quantity= billCommodities.get(i).getGoodsnum();
            double weight= billCommodities.get(i).getGoodsweight();
            holder.weightTv.setText("重量  "+(quantity *weight) );
        }
        holder.weightTv.setOnClickListener(this);
        holder.weightTv.setTag(R.id.billCommodityId, billCommodities.get(i).getId());
        holder.weightTv.setTag(R.id.orderId, orderId);
        holder.weightTv.setTag(R.id.billCommodityPosition, i);
        holder.weightTv.setTag(R.id.billCommodityName, billCommodities.get(i).getGoodsName());
        return view;
    }

    @Override
    public void onClick(final View v) {
        if (mCallback != null) mCallback.click(v);
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        ToastUtil.showToast(context, json.getString("message"));
    }

    @Override
    public void fail(Object tag, int code, JSONObject json) throws JSONException {

    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    static class ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.specification_tv)
        TextView specificationTv;
        @BindView(R.id.quantity_text)
        TextView quantityText;
        @BindView(R.id.weight_tv)
        TextView weightTv;
        @BindView(R.id.price_text)
        TextView priceText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
