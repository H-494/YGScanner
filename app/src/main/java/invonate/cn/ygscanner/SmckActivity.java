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
import android.widget.Button;
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

public class SmckActivity extends AppCompatActivity {

    @BindView(R.id.sp_outkubie)
    AppCompatSpinner spOutkubie;
    @BindView(R.id.sp_inkubie)
    AppCompatSpinner spInkubie;
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
    @BindView(R.id.finish)
    Button finish;

    private String name;// 工号
    private String bzmc;// 班组
    private String zlmc;// 类别

    SQLiteDatabase db;

    String okubie;
    String ikubie;

    int old_iku = 0;
    int old_oku = 0;

    private boolean sure;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smck);
        ButterKnife.bind(this);
        setTitle("扫描出库");
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        DatabaseHelper database = new DatabaseHelper(this);
        db = database.getReadableDatabase();
        data = (ArrayList<Ku.DataBean.KubieBean>) getIntent().getExtras().getSerializable("data");
        ArrayAdapter<Ku.DataBean.KubieBean> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        spOutkubie.setAdapter(adapter0);
        spInkubie.setAdapter(adapter0);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spOutkubie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                okubie = data.get(i).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spInkubie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ikubie = data.get(i).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        SharedPreferences share = getApplicationContext().getSharedPreferences("share", Context.MODE_PRIVATE);
        bzmc = share.getString("bzmc", "NORE");
        zlmc = share.getString("zlmc", "NORE");
        name = share.getString("name", "NORE");

//        code.addTextChangedListener(new TextWatcher() {
//            boolean isExecute = false;//是否扫描
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                isExecute = i == 0 && i2 > 1;
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (sure) {
//                    if (isExecute && code.getText().length() > 25) {
//                        new Thread(new querykcddinfoHandler()).start();
////                    } else {
////                        if (code.getText().toString().endsWith("\n")) {
////                            new Thread(new querykcddinfoHandler()).start();
////                        }
//                    }
//                } else {
//                    Toast.makeText(SmckActivity.this, "请先确认移出和移入库", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        code.addTextChangedListener(watcher);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            sure = true;
            List<String> list = bundle.getStringArrayList("list");
            if (list != null) {
                String list_ikubie = list.get(0);
                String list_okubie = list.get(1);

                for (int i = 0; i < data.size(); i++) {
                    if (list_ikubie.equals(data.get(i).getName())) {
                        spInkubie.setSelection(i);
                        break;
                    }

                }
                for (int i = 0; i < data.size(); i++) {
                    if (list_okubie.equals(data.get(i).getName())) {
                        spOutkubie.setSelection(i);
                        break;
                    }
                }
            }
        }

    }

    private TextWatcher watcher = new TextWatcher() {
        int location = 0;
        boolean isExcute = false;//是否为扫描

        @SuppressLint("NewApi")
        @Override
        public void afterTextChanged(Editable arg0) {

            if (arg0.length() > 0) {
                if (!isExcute) {
                    return;
                }
                if (location > 0) {
                    arg0.delete(0, location);
                }
                if (sure) {
                    if (code.getText().length() > 25) {
                        new Thread(new querykcddinfoHandler()).start();
                    }
                } else {
                    Toast.makeText(SmckActivity.this, "请先确认库区", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int start, int count, int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            // 扫描时，start从0开始，count>1
            // 手动输入时，start递增,count=1
            // 手动删除时，start递减，count=0
            location = start;

            isExcute = false;
            if (count > 1) {
                isExcute = true;
            }

        }
    };

    @OnClick({R.id.sure, R.id.finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sure:
                if (!sure) {
                    if (okubie == null || "".equals(okubie)) {
                        Toast.makeText(this, "请选择移出库别", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (ikubie == null || "".equals(ikubie)) {
                        Toast.makeText(this, "请选择移入库别", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    spInkubie.setEnabled(false);
                    spOutkubie.setEnabled(false);
                    sure = true;
                }
                break;
            case R.id.finish:
                finish();
                break;
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
            handler.sendEmptyMessage(-1);
            String scannerhwtmtext = code.getText().toString();
            String invId = "";
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
                data = String.format("%-10s", "SHHL40") + "C"
                        + String.format("%-20s", e) + "*";
            } else {
                data = String.format("%-10s", "SHHL40") + "C"
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
//
//                        String query = "select * from  YG_FHXXM where INVID = "+invId;
                        Cursor cursor = db.rawQuery("SELECT * FROM YG_CKXXM WHERE INVID=?", new String[]{invId});
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

                        String insert = "insert into YG_CKXXM(INVID,SCHDNO,ORDERLENGTH,ORDERTHICK,THEOWGT,REALWTG,PRODSPECNO,iKUBIE,OKUBIE) "
                                + " values ('" + invId + "'" + ",'" + schdNo + "','"
                                + orderLength + "','" + orderThick + "','" + theoWgt + "' ,'"
                                + realWtg + "','" + prodSpecNo + "','" + ikubie + "','" + okubie + "')";
                        try {
                            db.execSQL(insert);
                        } catch (Exception w) {
                            w.printStackTrace();
                        }

                        Cursor cur1 = db.rawQuery(
                                "SELECT ifnull(sum(realwtg),0) as realwtg ,ifnull(sum(theoWgt),0) as realwtg,count(*) as js,ifnull(loadno,' ') FROM YG_CKXXM ",
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
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "获取失败");
                }
            }
        }
    }

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmckActivity.this);
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
                    break;
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 241) {
//            code.setText("");
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
