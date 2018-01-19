package com.example.xxxloli.zshmerchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.objectmodel.ShoppingCard;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/15.
 */

public class ShoppingCardAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<ShoppingCard> classifies;
    private Context mContext;
    private Callback mCallback;

    //响应按钮点击事件,调用子定义接口，并传入View
    @Override
    public void onClick(View v) {
        mCallback.click(v);
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

    public ShoppingCardAdapter(Context mContext, ArrayList<ShoppingCard> classifies, Callback callback) {
        this.classifies = classifies;
        this.mContext = mContext;
        mCallback = callback;
    }

    //刷新Adapter
    public void refresh(ArrayList<ShoppingCard> classifies) {
        this.classifies = classifies;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return classifies == null ? 0 : classifies.size();
    }

    @Override
    public Object getItem(int i) {
        return classifies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shopping_card_cool, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.nameTv.setText("" + classifies.get(i).getCardName());
        holder.statusTv.setText("" + classifies.get(i).getStatus_value());
        holder.dataTv.setText("" + classifies.get(i).getStartDate() + "至" + classifies.get(i).getEndDate());
        holder.moneyTv.setText("￥" + classifies.get(i).getBalance());
        holder.numberTv.setText("" + classifies.get(i).getCardNumber());
        holder.deleteBt.setOnClickListener(this);
        holder.deleteBt.setTag(i);
        if (classifies.get(i).getStatus().equals("no")) holder.deleteBt.setVisibility(View.VISIBLE);
        else holder.deleteBt.setVisibility(View.GONE);
        holder.pwdBt.setOnClickListener(this);
        holder.pwdBt.setTag(i);
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.status_tv)
        TextView statusTv;
        @BindView(R.id.data_tv)
        TextView dataTv;
        @BindView(R.id.money_tv)
        TextView moneyTv;
        @BindView(R.id.number_tv)
        TextView numberTv;
        @BindView(R.id.delete_bt)
        Button deleteBt;
        @BindView(R.id.pwd_bt)
        Button pwdBt;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
