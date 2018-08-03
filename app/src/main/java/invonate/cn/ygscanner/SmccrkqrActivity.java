package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yonggang.liyangyang.lazyviewpagerlibrary.LazyFragmentPagerAdapter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbs.android.segmentcontrolview.library.SegmentControlView;
import invonate.cn.ygscanner.Entry.Ku;
import invonate.cn.ygscanner.Fragment.Smccrqr;
import invonate.cn.ygscanner.Fragment.SmccrqrDelete;
import invonate.cn.ygscanner.Util.CustomViewPager;
import invonate.cn.ygscanner.Util.DatabaseHelper;
import invonate.cn.ygscanner.Util.IpConfig;

public class SmccrkqrActivity extends AppCompatActivity {

    @BindView(R.id.tab)
    SegmentControlView tab;
    @BindView(R.id.pager)
    CustomViewPager pager;

    Fragment[] fragments = new Fragment[2];
    @BindView(R.id.sum)
    TextView sum;
    @BindView(R.id.sum_real_weight)
    TextView sumRealWeight;
    @BindView(R.id.sum_weight)
    TextView sumWeight;

    SQLiteDatabase db;

    String bz;
    String bc;

    String bzmc;
    String zlmc;
    String name;

    String total;

    ArrayList<String> info = new ArrayList<>();

    ArrayList<Ku.DataBean.KubieBean> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smccrkqr);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();
        ButterKnife.bind(this);
        setTitle("扫描产出入库确认");
        data = (ArrayList<Ku.DataBean.KubieBean>) getIntent().getExtras().getSerializable("data");
        tab.setTexts(new String[]{"统计信息", "明细信息"});
        initFragment();
        pager.setScanScroll(false);
        pager.setAdapter(new TaskPagerAdapter(getSupportFragmentManager()));

        tab.setOnSegmentChangedListener(new SegmentControlView.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                pager.setCurrentItem(newSelectedIndex);
            }
        });
        tab.setViewPager(pager);
        getSum();
        info = getSmccrk();

        SharedPreferences share = getSharedPreferences("share",
                Context.MODE_PRIVATE);
        bzmc = share.getString("bzmc", "NORE");
        zlmc = share.getString("zlmc", "NORE");
        name = share.getString("name", "NORE");
        if (bzmc.equals("甲")) {
            bz = "A";
            bc = "1";
        }
        if (bzmc.equals("乙")) {
            bz = "B";
            bc = "2";
        }
        if (bzmc.equals("丙")) {
            bz = "C";
            bc = "3";
        }

    }

    private void initFragment() {
        Smccrqr s = new Smccrqr();
        fragments[0] = s;
        SmccrqrDelete sd = new SmccrqrDelete();
        fragments[1] = sd;
    }

    @OnClick({R.id.confirm, R.id.delete_all, R.id.forward, R.id.finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                new Thread(new qurenkcddinfoHandler()).start();
                break;
            case R.id.delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("是否全部删除已扫描的条码")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAll();
                            }
                        }).setNegativeButton("否", null)
                        .create().show();
                break;
            case R.id.forward:
                Intent intent = new Intent(SmccrkqrActivity.this, SmccrkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("list", info);
                bundle.putSerializable("data",data);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.finish:
                finish();
                break;
        }
    }

    class TaskPagerAdapter extends LazyFragmentPagerAdapter {

        public TaskPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        protected Fragment getItem(ViewGroup container, int position) {
            return fragments[position];
        }
    }

    /**
     * 获取库存汇总信息
     */
    public void getSum() {
        Cursor cur1 = db.rawQuery(
                "SELECT ifnull(sum(realwtg),0) as realwtg ,ifnull(sum(theoWgt),0) as realwtg,count(*) as js,ifnull(loadno,' ') FROM YG_FHXXM ",
                null);
        int x = cur1.getCount();
        Log.d("查询结果:", x + "");
        if (x != 0) {
            cur1.moveToFirst();
            String realwtg2 = String.valueOf(cur1.getDouble(0));
            BigDecimal bd11 = new BigDecimal(Double.parseDouble(realwtg2));
            Double realwtg3 = bd11.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            String realwtg4 = String.valueOf(realwtg3);

            String theowgt2 = String.valueOf(cur1.getDouble(1));
            BigDecimal bd12 = new BigDecimal(Double.parseDouble(theowgt2));
            Double theowgt3 = bd12.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            String theowgt4 = String.valueOf(theowgt3);

            String qty2 = String.valueOf(cur1.getInt(2));

            total = realwtg4;

            sum.setText(qty2);
            sumRealWeight.setText(realwtg4);
            sumWeight.setText(theowgt4);
        }
        cur1.close();
        ((Smccrqr) fragments[0]).getData();
    }

    /**
     * 全部删除
     */
    private void deleteAll() {
        db.execSQL("Delete FROM YG_FHXXM");
        ((Smccrqr) fragments[0]).clear();
        ((SmccrqrDelete) fragments[1]).clear();
    }

    class qurenkcddinfoHandler implements Runnable {
        @Override
        public void run() {
            String messageStr = "";
            boolean loadstate = false;
            Cursor cur = db.rawQuery("SELECT invid FROM YG_FHXXM ", null);
            String hhhh1 = "";
            int i = 0;
            if (cur.getCount() != 0) {
                while (cur.moveToNext()) {
                    String bh = cur.getString(0);
                    hhhh1 = hhhh1 + String.format("%-20s", bh);
                    i++;
                }
            } else {
                messageStr = "没有材料可确认！";
                loadstate = false;
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.what = 0;
                bundle.putString("messageStr", messageStr);// 信息

                message.setData(bundle);
                handler.sendMessage(message);
                return;
            }

            String result = "";
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            // EndPoint
            String endPoint = "http://" + IpConfig.ip + "/erp/sh/Scanning.ws";
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            Date date = new Date();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            String time2 = format1.format(date);

            Date date2 = new Date();
            SimpleDateFormat format2 = new SimpleDateFormat("HHmmss");
            String time3 = format2.format(date2);
            int w = 20 * i;
            List<String> list = getSmccrk();
            String data = String.format("%-10s", "SHHL35") + "A"
                    + String.format("%-10s", name) + String.format("%-1s", bz)
                    + String.format("%-1s", bc)
                    + String.format("%-8s", time2)
                    + String.format("%-6s", time3)
                    + String.format("%-20s", list.get(0))
                    + String.format("%-20s", list.get(1))
                    + String.format("%-20s", list.get(2))
                    + String.format("%-10s", total)
                    + String.format("%-3s", String.valueOf(i))
                    + String.format("%-" + w + "s", hhhh1) + "*";

            Log.i("入库参数", data);
            // 设置需调用WebService接口需要传入的参数
            rpc.addProperty("date", data);

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = false;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                transport.call(soapAction, envelope);
                loadstate = true;
                Object object = (Object) envelope.getResponse();
                result = object.toString();
                Log.i("入库确认", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loadstate) {
                String inputCode = result.substring(10, 11).trim();
                if (inputCode.equals("F")) {
                    messageStr = result.substring(131, result.length() - 1);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    message.what = 0;
                    bundle.putString("msg", messageStr);// 信息

                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    db.execSQL("Delete FROM YG_FHXXM");
                    messageStr = result.substring(11, 21);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    message.what = 1;
                    bundle.putString("msg", messageStr);// 信息
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String loadstatemessage = msg.getData().getString("msg");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmccrkqrActivity.this);
                    builder.setTitle("提示")
                            .setMessage(loadstatemessage)
                            .setPositiveButton("确认", null)
                            .create().show();
                    break;
                case 1:
                    String loadstatemessage1 = msg.getData().getString("msg");
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SmccrkqrActivity.this);
                    builder1.setTitle("提示")
                            .setMessage("产出入库成功" + "\n" + "入库单号为：" + loadstatemessage1)
                            .setPositiveButton("确认", null)
                            .create().show();
                    ((SmccrqrDelete) fragments[1]).clear();
                    getSum();
                    break;
            }
        }
    };

    private ArrayList<String> getSmccrk() {
        ArrayList<String> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KU,QU,PAI,KUBIE FROM YG_FHXXM", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
            Log.i("hhh",cursor.getString(3));
        }
        return list;
    }


}
