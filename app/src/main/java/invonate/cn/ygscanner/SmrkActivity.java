package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import invonate.cn.ygscanner.Entry.Goods;
import invonate.cn.ygscanner.Entry.Ku;
import invonate.cn.ygscanner.Util.DatabaseHelper;
import invonate.cn.ygscanner.Util.IpConfig;

public class SmrkActivity extends AppCompatActivity {

    @BindView(R.id.sp_kubie)
    AppCompatSpinner spKubie;
    @BindView(R.id.sp_ku)
    AppCompatSpinner spKu;
    @BindView(R.id.sp_pai)
    AppCompatSpinner spPai;
    @BindView(R.id.sp_qu)
    AppCompatSpinner spQu;
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
    @BindView(R.id.sum)
    TextView sum;
    @BindView(R.id.sum_real_weight)
    TextView sumRealWeight;
    @BindView(R.id.sum_weight)
    TextView sumWeight;

    ArrayList<Ku.DataBean.KubieBean> data;

    private List<String> list_pai = new ArrayList<>();

    private String kubie;
    private String ku;
    private String qu;
    private String pai;

    private boolean sure;

    private String name;// 工号
    private String bzmc;// 班组
    private String zlmc;// 类别

    int old_ku = 0;
    int old_qu = 0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            switch (msg.what) {
                case 0:
                    type.setText("");
                    made.setText("");
                    weight.setText("");
                    length.setText("");
                    String str = msg.getData().getString("msg");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmrkActivity.this);
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

                    sumRealWeight.setText(msg.getData().getString("realwtg", ""));
                    sumWeight.setText(msg.getData().getString("theowgt", ""));
                    sum.setText(String.format("%d", msg.getData().getInt("sum")));
                    break;
                case -1:
                    dialog.show();
            }

        }
    };

    SQLiteDatabase db;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smrk);
        setTitle("扫描入库");
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        DatabaseHelper database = new DatabaseHelper(this);
        db = database.getReadableDatabase();
        data = (ArrayList<Ku.DataBean.KubieBean>) getIntent().getExtras().getSerializable("data");
        list_pai = JSON.parseArray(getJson("pai.json", this), String.class);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_pai);
        spPai.setAdapter(adapter3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                pai = list_pai.get(position).trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<Ku.DataBean.KubieBean> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        spKubie.setAdapter(adapter0);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKubie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kubie = data.get(i).getName();
                final List<Ku.DataBean.KubieBean.KuBean> list_ku = data.get(i).getKu();
                ArrayAdapter<Ku.DataBean.KubieBean.KuBean> adapter1 = new ArrayAdapter<>(SmrkActivity.this, android.R.layout.simple_spinner_item, list_ku);
                spKu.setAdapter(adapter1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spKu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ku = list_ku.get(i).getName();
                        final List<Ku.DataBean.KubieBean.KuBean.QvBean> list_qv = list_ku.get(i).getQv();
                        ArrayAdapter<Ku.DataBean.KubieBean.KuBean.QvBean> adapter2 = new ArrayAdapter<>(SmrkActivity.this, android.R.layout.simple_spinner_item, list_qv);
                        spQu.setAdapter(adapter2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spQu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                qu = list_qv.get(i).getValue();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (old_qu != 0) {
                            spQu.setSelection(old_qu);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                if (old_ku != 0) {
                    spKu.setSelection(old_ku);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SharedPreferences share = getApplicationContext().getSharedPreferences("share", Context.MODE_PRIVATE);
        bzmc = share.getString("bzmc", "NORE");
        zlmc = share.getString("zlmc", "NORE");
        name = share.getString("name", "NORE");

        code.addTextChangedListener(new TextWatcher() {
            boolean isExecute = false;//是否扫描

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isExecute = i == 0 && i2 > 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (sure) {
                    if (isExecute && code.getText().toString().length() > 25) {
                        new Thread(new querykcddinfoHandler()).start();
//                    } else {
//                        if (code.getText().toString().endsWith("\n")) {
//                            new Thread(new querykcddinfoHandler()).start();
//                        }
                    }
                } else {
                    Toast.makeText(SmrkActivity.this, "请先确认库区", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            List<String> list = bundle.getStringArrayList("list");
            if (list != null) {
                String list_ku = list.get(0);
                String list_qv = list.get(1);
                String list_kubie = list.get(3);
                List<Ku.DataBean.KubieBean.KuBean> ku = null;
                List<Ku.DataBean.KubieBean.KuBean.QvBean> qv = null;
                for (int i = 0; i < data.size(); i++) {
                    if (list_kubie.equals(data.get(i).getName())) {
                        ku = data.get(i).getKu();
                        spKubie.setSelection(i);
                        break;
                    }
                }
                if (ku != null) {
                    for (int n = 0; n < ku.size(); n++) {
                        if (list_ku.equals(ku.get(n).getName())) {
                            qv = ku.get(n).getQv();
                            old_ku = n;
                            spKu.setSelection(n);
                            break;
                        }
                    }
                    if (qv != null) {
                        for (int m = 0; m < qv.size(); m++) {
                            if (list_qv.equals(qv.get(m).getValue())) {
                                old_qu = m;
                                spQu.setSelection(m);
                                break;
                            }
                        }

                    }

                }
                String string_pai = list.get(2);
                for (int i = 0; i < list_pai.size(); i++) {
                    if (string_pai.equals(list_pai.get(i))) {
                        spPai.setSelection(i);
                    }
                }
            }
        }
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
                data = String.format("%-10s", "SHHL41") + "A"
                        + String.format("%-20s", e) + "*";
            } else {
                data = String.format("%-10s", "SHHL41") + "A"
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
                    Log.i("smccrk", result1);
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
                    } else {
                        Goods goods = new Goods();
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

                        String schdNo = invId.substring(0, invId.length() - 3);

//                        String query = "select * from  YG_RKXXM where INVID = "+invId;
                        Cursor cursor = db.rawQuery("SELECT * FROM YG_RKXXM WHERE INVID=?", new String[]{invId});
                        if (cursor.getCount() != 0) {
                            Message message = handler.obtainMessage();
                            message.what = 0;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", "材料号已扫描");
                            message.setData(bundle);
                            message.sendToTarget();
                            return;
                        }

                        Message message = handler.obtainMessage();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods", goods);

                        String insert = "insert into YG_RKXXM(INVID,SCHDNO,ORDERLENGTH,ORDERTHICK,THEOWGT,REALWTG,PRODSPECNO,KUBIE,KU,QU,PAI) "
                                + " values ('" + invId + "'" + ",'" + schdNo + "','"
                                + orderLength + "','" + orderThick + "','" + theoWgt + "' ,'"
                                + realWtg + "','" + prodSpecNo + "','" + kubie + "','" + ku + "','" + qu + "','" + pai + "')";
                        try {
                            db.execSQL(insert);
                        } catch (Exception w) {
                            w.printStackTrace();
                        }

                        Cursor cur1 = db.rawQuery(
                                "SELECT ifnull(sum(realwtg),0) as realwtg ,ifnull(sum(theoWgt),0) as realwtg,count(*) as js,ifnull(loadno,' ') FROM YG_RKXXM ",
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

                            int qty2 = cur1.getInt(2);

                            bundle.putString("realwtg", realwtg4);
                            bundle.putString("theowgt", theowgt4);
                            bundle.putInt("sum", qty2);
                        }
                        cur1.close();
                        message.setData(bundle);
                        message.sendToTarget();
                    }
                } else {
                    // 请求失败
                    // Toast.makeText(SmccrkActivity.this, "", Toast.LENGTH_SHORT).show();
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "获取失败");
                }
            }
        }
    }

    @OnClick({R.id.sure, R.id.finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sure:
                if (!sure) {
                    if (ku == null || "".equals(ku)) {
                        Toast.makeText(this, "请选择库区", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (qu == null || "".equals(qu)) {
                        Toast.makeText(this, "请选择区", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    spKubie.setEnabled(false);
                    spKu.setEnabled(false);
                    spQu.setEnabled(false);
                    spPai.setEnabled(false);
                    sure = true;
                }
                break;
            case R.id.finish:
                finish();
                break;
        }
    }

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
