package com.example.xxxloli.zshmerchant.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xxxloli.zshmerchant.R;
import com.example.xxxloli.zshmerchant.util.CustomToast;
import com.example.xxxloli.zshmerchant.util.GreenDaoHelp;
import com.example.xxxloli.zshmerchant.util.ToastUtil;
import com.interfaceconfig.Config;
import com.sgrape.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddShoppingCardActivity extends BaseActivity {


    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.quantity_et)
    EditText quantityEt;
    @BindView(R.id.money_et)
    EditText moneyEt;
    @BindView(R.id.start_data_tv)
    TextView startDataTv;
    @BindView(R.id.end_data_tv)
    TextView endDataTv;
    @BindView(R.id.save_bt)
    Button saveBt;

    private int year, month, day;
    private int startYear = 0, startMonth = 0, startDay = 0;
    private int endYear = 0, endMonth = 0, endDay = 0;
    private Calendar cal;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_shopping_card;
    }

    @Override
    protected void init() {
        getDate();
    }

    @Override
    public void onSuccess(Object tag, JSONObject json) throws JSONException {
        ToastUtil.showToast(this, json.getString("message"));//
        finish();
    }

    private void start() {
        if (isEmpty(nameEt.getText().toString())) {
            ToastUtil.showToast(this,"请输入购物卡名称");
            return;
        }
        if (isEmpty(quantityEt.getText().toString())) {
            ToastUtil.showToast(this,"请输入购物卡数量");
            return;
        }
        if (isEmpty(moneyEt.getText().toString())) {
            ToastUtil.showToast(this,"请输入单张金额");
            return;
        }
        if (startDataTv.getText().toString().equals("设置开始日期")) {
            ToastUtil.showToast(this,"请设置开始日期");
            return;
        }
        if (endDataTv.getText().toString().equals("设置结束日期")) {
            ToastUtil.showToast(this,"请设置结束日期");
            return;
        }
//        ,[userId : ][ cardName:购物卡名称 ][ startDate:有效激活开始时间 ]
//        [ endDate:有效激活结束时间 ][ number:购物卡数量 ][ cost:单张金额 ]
        Map<String, Object> params = new HashMap<>();
        params.put("userId", GreenDaoHelp.GetShop(this).getShopkeeperId());
        params.put("cardName", nameEt.getText().toString());
        params.put("startDate", startDataTv.getText().toString());
        params.put("endDate", endDataTv.getText().toString());
        params.put("number", quantityEt.getText().toString());
        params.put("cost", moneyEt.getText().toString());
        newCall(Config.Url.getUrl(Config.AddShoppingCard), params);
        saveBt.setClickable(false);
        Log.e("AddShoppingCard",""+params);
    }

    @OnClick({R.id.back_rl, R.id.start_data_tv, R.id.end_data_tv, R.id.save_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.start_data_tv:
                selectStartData();
                break;
            case R.id.end_data_tv:
                selectEndData();
                break;
            case R.id.save_bt:
                start();
                break;
        }
    }

    private void selectEndData() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                endYear = year;
                endMonth = ++month;
                endDay = day;
                String m=(endMonth<10)? "0"+endMonth:""+endMonth;
                String d=(endDay<10)? "0"+endDay:""+endDay;
                endDataTv.setText(year + "-" + m + "-" + d);
            }
        };
        //后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
        DatePickerDialog dialog = new DatePickerDialog(this, 0, listener, year, month, day);
        DatePicker datePicker = dialog.getDatePicker();
        //获取到的月份是从0开始计数
        if (startYear!=0)cal.set(startYear,startMonth-1,startDay);

        long time = cal.getTimeInMillis();
        datePicker.setMinDate(time);
        dialog.show();
    }

    private void selectStartData() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, final int year, int month, int day) {
                //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                startYear = year;
                startMonth = ++month;
                startDay = day;
                String m=(startMonth<10)? "0"+startMonth:""+startMonth;
                String d=(startDay<10)? "0"+startDay:""+startDay;
                startDataTv.setText(year + "-" + m + "-" + d);

            }
        };
        //后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
        DatePickerDialog dialog = new DatePickerDialog(this, 0, listener, year, month, day);
        DatePicker datePicker = dialog.getDatePicker();
        //获取到的月份是从0开始计数
        cal = Calendar.getInstance();
        long time = cal.getTimeInMillis();
        datePicker.setMinDate(time);
        dialog.show();
    }

    //获取当前日期
    private void getDate() {
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy", "year" + year);
        month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void fail(Object tag, int code, JSONObject json) throws JSONException {
        super.fail(tag, code, json);
        saveBt.setClickable(true);
    }
}
