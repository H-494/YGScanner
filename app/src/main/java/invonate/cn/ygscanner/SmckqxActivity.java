package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import invonate.cn.ygscanner.Entry.Goods;
import invonate.cn.ygscanner.Util.DatabaseHelper;
import invonate.cn.ygscanner.Util.IpConfig;

public class SmckqxActivity extends AppCompatActivity {

    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.made)
    TextView made;
    @BindView(R.id.weight)
    TextView weight;
    @BindView(R.id.length)
    TextView length;

    SQLiteDatabase db;

    private String name;// 工号
    private String bzmc;// 班组
    private String zlmc;// 类别

    String bz;
    String bc;

    Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smckqx);
        setTitle("扫描出库取消");
        ButterKnife.bind(this);
        DatabaseHelper database = new DatabaseHelper(this);
        db = database.getReadableDatabase();
        code.addTextChangedListener(new TextWatcher() {
            boolean isExecute = false;//是否扫描

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isExecute = i == 0 && i2 > 1;// true 为 扫描  false为输入
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isExecute && code.getText().length() > 25) {
                    new Thread(new querykcddinfoHandler()).start();
//                } else {
//                    if (code.getText().toString().endsWith("\n")) {
//                        new Thread(new querykcddinfoHandler()).start();
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        SharedPreferences share = getApplicationContext().getSharedPreferences("share", Context.MODE_PRIVATE);
        bzmc = share.getString("bzmc", "NORE");
        zlmc = share.getString("zlmc", "NORE");
        name = share.getString("name", "NORE");

        if (bzmc.equals("甲")) {
            bz = "A";
        }
        if (bzmc.equals("乙")) {
            bz = "B";
        }
        if (bzmc.equals("丙")) {
            bz = "C";
        }
    }

    @OnClick({R.id.cancel, R.id.finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                if (goods != null) {
                    new Thread(new CancelHandler()).start();
                } else {
                    Toast.makeText(this, "请先扫码查询", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.finish:
                finish();
                break;
        }
    }

    // 根据货物条码查规格材质等
    class querykcddinfoHandler implements Runnable {

        @Override
        public void run() {
            String scannerhwtmtext = code.getText().toString();
            String invId = "";
            // String qfd = smfh_scannerqfdhEdit.getText().toString();
            /*
             * if(qfd=="") { Toast.makeText(getApplicationContext(), "请输入签发单号！",
             * Toast.LENGTH_LONG) .show(); }
             */
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
            String e = " ";
            if ("线材".equals(zlmc)) {
                if (!(scannerhwtmtext.contains("#")))// 老的二维码格式不存在#字符（轧制批号件号重量）19位
                {
                    e = scannerhwtmtext;
                } else // 新的二维码格式（轧制批号件号重量#牌号#口径#长度）存在三个#字符
                {
                    String[] bxc = scannerhwtmtext.split("#");
                    if (bxc.length != 4) {
                        messageStr = "二维码错误！";
                        return;
                    }
                    invId = bxc[0];
                    String tradeNo = bxc[1];
                    String orderThick = bxc[2];
                    String orderLength = bxc[3];
                    e = String.format("%-20s", invId) + String.format("%-20s", tradeNo)
                            + String.format("%-6s", orderThick) + String.format("%-5s", orderLength);
                }
            } else {
                if (!(scannerhwtmtext.contains("#")))// 老的二维码格式不存在#字符（轧制批号件号）15位
                {
                    if (scannerhwtmtext.length() > 20)// 二维码 改成20
                    {
                        e = scannerhwtmtext.substring(scannerhwtmtext.indexOf('<') + 1,
                                (scannerhwtmtext.indexOf('>') - scannerhwtmtext.indexOf('<') - 1));
                    } else {
                        e = scannerhwtmtext;
                    }
                } else // 新的二维码格式（轧制批号件号#牌号#口径#长度）存在三个#字符
                {
                    String[] bbc = scannerhwtmtext.split("#");
                    if (bbc.length != 4) {
                        messageStr = "二维码错误！";
                        return;
                    }
                    invId = bbc[0];
                    String tradeNo1 = bbc[1];
                    String orderThick1 = bbc[2];
                    String orderLength1 = bbc[3];
                    e = String.format("%-20s", invId) + String.format("%-20s", tradeNo1)
                            + String.format("%-6s", orderThick1) + String.format("%-5s", orderLength1);
                }
            }

            String data = " ";
            if (!(scannerhwtmtext.contains("#"))) {
                data = String.format("%-10s", "SHHL40") + "A"
                        + String.format("%-20s", e) + "*";
            } else {
                data = String.format("%-10s", "SHHL40") + "A"
                        + String.format("%-51s", e) + "*";
            }
            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("date", data);

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
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
                    // 如果服务器返回的是byte[]类型：
                    // SoapObject object = (SoapObject) envelope.bodyIn;
                    // 如果服务器返回的是String类型：
                    Object object1 = null;
                    try {
                        object1 = (Object) envelope1.getResponse();
                    } catch (SoapFault e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    // 获取返回的结果
                    String result1 = object1.toString();
                    Log.i("smck", result1);
                    String inputCode = result1.substring(10, 11);
                    invId = result1.substring(11, 26).trim();

                    /*
                     * if(invId.equals(loadNo)) { messageStr = "签发单不匹配！"; return; }
                     */

                    if ("F".equals(inputCode)) {
                        String msg = result1.substring(131, result1.length() - 1).trim();
                        Message message = handler.obtainMessage();
                        message.what = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", msg);
                        message.setData(bundle);
                        message.sendToTarget();
                        goods = null;
                    } else {
                        goods = new Goods();
                        goods.setCode(invId);
                        String prodSpecNo = result1.substring(49, 69).trim();//材质
                        goods.setType(prodSpecNo);
                        String orderThick = result1.substring(89, 98).trim();//规格
                        goods.setMaterial(orderThick);
                        String orderLength = result1.substring(98, 99).trim();
                        goods.setLength(orderLength);
                        String theoWgt = result1.substring(112, 121).trim();
                        goods.setWeight(theoWgt);
                        String realWtg = result1.substring(121, 130).trim();
                        goods.setRealWeight(realWtg);
                        Log.i("Goods", JSON.toJSONString(goods));

                        Message message = handler.obtainMessage();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods", goods);

                        message.setData(bundle);
                        message.sendToTarget();
                    }
                } else {
                    // 请求失败
                    // Toast.makeText(SmccrkActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class CancelHandler implements Runnable {
        @Override
        public void run() {
            String messageStr = "";
            boolean loadstate = false;

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
            String data = String.format("%-10s", "SHHL39C") + "A"
                    + String.format("%-10s", name) + String.format("%-1s", bz)
                    + String.format("%-8s", time2)
                    + String.format("%-6s", time3)
                    + String.format("%-20s", goods.getCode())
                    + String.format("%-1s", "*");
            Log.i("出库取消参数", data);
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
                Log.i("出库取消", result);
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
                    messageStr = "取消成功！";
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    message.what = 2;
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
                    String str = msg.getData().getString("msg");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmckqxActivity.this);
                    builder.setTitle("提示")
                            .setMessage(str)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                    break;
                case 1:
                    Goods goods = (Goods) msg.getData().getSerializable("goods");
                    type.setText(goods.getType());
                    made.setText(goods.getMaterial());
                    weight.setText(String.format("%s（%s）", goods.getRealWeight(), goods.getWeight()));
                    length.setText(goods.getLength());
                    break;
                case 2:
                    String s = msg.getData().getString("msg");
                    type.setText("");
                    made.setText("");
                    weight.setText("");
                    length.setText("");
                    Toast.makeText(SmckqxActivity.this, s, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 241) {
            code.setText("");
            code.requestFocus();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            new Thread(new querykcddinfoHandler()).start();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
