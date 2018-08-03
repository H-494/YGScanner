package invonate.cn.ygscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import invonate.cn.ygscanner.Adapter.KccxAdapter;
import invonate.cn.ygscanner.Entry.Ku;
import invonate.cn.ygscanner.Entry.Statistics;
import invonate.cn.ygscanner.Util.IpConfig;

public class KccxActivity extends AppCompatActivity {

    @BindView(R.id.sp_ku)
    AppCompatSpinner spKu;
    @BindView(R.id.sp_pai)
    AppCompatSpinner spPai;
    @BindView(R.id.sp_qu)
    AppCompatSpinner spQu;
    @BindView(R.id.list_goods)
    ListView listGoods;
    @BindView(R.id.sum)
    TextView sum;
    @BindView(R.id.sum_weight)
    TextView sumWeight;

    ArrayList<Ku.DataBean.KubieBean> data;
    @BindView(R.id.sp_kubie)
    AppCompatSpinner spKubie;
    private List<String> list_pai = new ArrayList<>();

    private String kubie;
    private String ku;
    private String qu;
    private String pai;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    listGoods.setAdapter(null);
                    sum.setText("");
                    sumWeight.setText("");
                    Toast.makeText(KccxActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    break;

                case 1:
//                    Toast.makeText(KccxActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    List<Statistics> list = (List<Statistics>) msg.getData().getSerializable("goods");
                    if (list != null) {
                        double weight = 0;
                        int size = 0;
                        for (Statistics s : list) {
                            BigDecimal w = new BigDecimal(s.getRealWeight());
                            w = w.add(new BigDecimal(weight));
                            weight = w.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

                            BigDecimal z = new BigDecimal(s.getSum());
                            z = z.add(new BigDecimal(size));
                            size = z.intValue();
                        }
                        sumWeight.setText(String.format("%s", weight));
                        sum.setText(String.format("%d", size));
                        KccxAdapter adapter = new KccxAdapter(list, KccxActivity.this);
                        listGoods.setAdapter(adapter);
                    }

                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kccx);
        ButterKnife.bind(this);
        setTitle("库存查询");
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
                ArrayAdapter<Ku.DataBean.KubieBean.KuBean> adapter1 = new ArrayAdapter<>(KccxActivity.this, android.R.layout.simple_spinner_item, list_ku);
                spKu.setAdapter(adapter1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spKu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ku = list_ku.get(i).getName();
                        final List<Ku.DataBean.KubieBean.KuBean.QvBean> list_qv = list_ku.get(i).getQv();
                        ArrayAdapter<Ku.DataBean.KubieBean.KuBean.QvBean> adapter2 = new ArrayAdapter<>(KccxActivity.this, android.R.layout.simple_spinner_item, list_qv);
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
                if (kubie == null || "".equals(kubie)) {
                    Toast.makeText(this, "请选择库别", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (ku == null || "".equals(ku)) {
                    Toast.makeText(this, "请选择库区", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (qu == null || "".equals(qu)) {
                    Toast.makeText(this, "请选择区", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new querykcddinfoHandler()).start();
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

    class querykcddinfoHandler implements Runnable {

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
            data = String.format("%-10s", "SHHL45") + "A"
                    + String.format("%-15s", ku)
                    + String.format("%-15s", qu)
                    + String.format("%-15s", pai == null ? "" : pai) + "*";
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
                    Log.i("kccx", result1);
                    String num = result1.substring(11, 14).trim();
                    int sum = Integer.parseInt(num);
                    if (sum == 0) {
                        mHandler.sendEmptyMessage(0);
                    } else {
                        ArrayList<Statistics> list = new ArrayList<>();
                        for (int i = 0; i < sum; i++) {
                            Statistics goods = new Statistics();
                            goods.setCode(result1.substring(70 + i * 81, 70 + i * 81 + 20).trim());
                            goods.setSum(Integer.parseInt(result1.substring(90 + i * 81, 90 + i * 81 + 5).trim()));
                            goods.setMaterial(result1.substring(14 + i * 81, 14 + i * 81 + 20).trim());
                            goods.setType(result1.substring(34 + i * 81, 34 + i * 81 + 9).trim());
                            goods.setRealWeight(result1.substring(52 + i * 81, 52 + i * 81 + 9).trim());
                            list.add(goods);
                        }
                        Log.i("Statistics", JSON.toJSONString(list));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods", list);
                        Message msg = mHandler.obtainMessage();
                        msg.setData(bundle);
                        msg.what = 1;
                        msg.sendToTarget();
                    }
                } else {
                    // 请求失败
                    // Toast.makeText(SmccrkActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
