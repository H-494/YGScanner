package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import invonate.cn.ygscanner.Adapter.KwccAdapter;
import invonate.cn.ygscanner.Entry.Kwcc;
import invonate.cn.ygscanner.Util.IpConfig;

public class SmkwcxActivity extends AppCompatActivity {

    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.list)
    ListView list;

    ProgressDialog dialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(SmkwcxActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    String data = msg.getData().getString("data");
                    Kwcc kwcc = JSON.parseObject(data, Kwcc.class);
                    list.setAdapter(new KwccAdapter(kwcc.getData(),SmkwcxActivity.this));
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smkwcx);
        ButterKnife.bind(this);
        setTitle("扫描库位信息查询");
        dialog = new ProgressDialog(this);
        dialog.setMessage("查询中");
        code.addTextChangedListener(new TextWatcher() {

            boolean isExecute = false;//是否扫描

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isExecute = i == 0 && i2 > 1;// true 为 扫描  false为输入
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isExecute && code.getText().length() > 10) {
                    new Thread(new Querykwcx(code.getText().toString().trim())).start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    class Querykwcx implements Runnable {
        private String invId;

        public Querykwcx(String invId) {
            this.invId = invId;
        }

        @Override
        public void run() {
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
            data = String.format("%-10s", "SHHL30") + "A"
                    + String.format("%-15s", invId) + "*";
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
                    Log.i("kwcq", result1);
                        Bundle bundle = new Bundle();
                        bundle.putString("data",result1);
                        Message msg = mHandler.obtainMessage();
                        msg.setData(bundle);
                        msg.what = 1;
                        msg.sendToTarget();
//                    }
                }
            }
        }
    }

    @OnClick(R.id.finish)
    public void onViewClicked() {
        finish();
    }
}
