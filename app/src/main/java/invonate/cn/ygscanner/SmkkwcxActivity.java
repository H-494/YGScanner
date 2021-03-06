package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import invonate.cn.ygscanner.Adapter.KkwxxAdapter;
import invonate.cn.ygscanner.Entry.Kkwcx;
import invonate.cn.ygscanner.Entry.Ku;
import invonate.cn.ygscanner.Util.IpConfig;

public class SmkkwcxActivity extends AppCompatActivity {

    @BindView(R.id.sp_kubie)
    AppCompatSpinner spKubie;
    @BindView(R.id.sp_ku)
    AppCompatSpinner spKu;
    @BindView(R.id.sure)
    Button sure;

    ProgressDialog dialog;
    @BindView(R.id.list)
    ListView list;

    private String kubie = "";
    private String ku = "";

    ArrayList<Ku.DataBean.KubieBean> data;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(SmkkwcxActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    final String listdata = msg.getData().getString("data");
                    final Kkwcx kkwcx = JSON.parseObject(listdata, Kkwcx.class);
                    KkwxxAdapter adapter = new KkwxxAdapter(SmkkwcxActivity.this, kkwcx.getData());
                    if(kkwcx.getResult().equals("F")){
                        Toast.makeText(SmkkwcxActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        if (dialog.isShowing())
                            dialog.dismiss();
                        return;
                    }
                    adapter.setOnButtonClickListener(new KkwxxAdapter.OnButtonClickListener() {
                        @Override
                        public void onCLick(View view, int position) {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(kkwcx.getData().get(position).getWARENO());
                            list.add(kkwcx.getData().get(position).getARENO());
                            list.add(kkwcx.getData().get(position).getWAREHOUSENO());
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("list", list);
                            bundle.putSerializable("data", data);
                            Intent intent = new Intent(SmkkwcxActivity.this, SmxcccActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    list.setAdapter(adapter);
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case -1:
                    dialog.show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smkkwcx);
        ButterKnife.bind(this);
        setTitle("线材空库位查询");
        dialog = new ProgressDialog(this);
        dialog.setMessage("查询中");
        data = (ArrayList<Ku.DataBean.KubieBean>) getIntent().getExtras().getSerializable("data");
        ArrayAdapter<Ku.DataBean.KubieBean> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        spKubie.setAdapter(adapter0);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKubie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kubie = data.get(i).getName();
                final List<Ku.DataBean.KubieBean.KuBean> list_ku = data.get(i).getKu();
                ArrayAdapter<Ku.DataBean.KubieBean.KuBean> adapter1 = new ArrayAdapter<>(SmkkwcxActivity.this, android.R.layout.simple_spinner_item, list_ku);
                spKu.setAdapter(adapter1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spKu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ku = list_ku.get(i).getName();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @OnClick({R.id.sure, R.id.finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sure:
                if (kubie.equals("") || kubie == null ){
                    Toast.makeText(this, "请选择库别", Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    new Thread(new Querykkw(kubie, ku)).start();
                    break;
                }
            case R.id.finish:
                finish();
                break;
        }
    }

    class Querykkw implements Runnable {

        private String SeriesNo;
        private String wareNo;

        public Querykkw(String seriesNo, String wareNo) {
            SeriesNo = seriesNo;
            this.wareNo = wareNo;
        }

        @Override
        public void run() {
            mHandler.sendEmptyMessage(-1);
            boolean loadstate = false;// 查询结果 的 成功和失败
            String messageStr = "";
            // 命名空间
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            String endPoint = "http://" + IpConfig.ip + "/erp/sh/Scanning.ws";
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);
            String data = " ";
            data = String.format("%-10s", "SHHL61") + "A"
                    + String.format("%-10s", SeriesNo)
                    + String.format("%-10s", wareNo)
                    + "*";
            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("date", data);
            Log.i("params", data);
            // 用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope1.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope1.dotNet = true;
            envelope1.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope1);
                loadstate = true;
            } catch (Exception w) {
                w.printStackTrace();
            } finally {
                // 查询成功
                if (loadstate) {
                    Object object1 = null;
                    try {
                        object1 = (Object) envelope1.getResponse();
                    } catch (SoapFault e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    // 获取返回的结果
                    String result1 = object1.toString();
                    Log.i("kkwcq", result1);
                    Bundle bundle = new Bundle();
                    bundle.putString("data", result1);
                    ;
                    Message msg = mHandler.obtainMessage();
                    msg.setData(bundle);
                    msg.what = 1;
                    msg.sendToTarget();
                }
            }
        }
    }

}
