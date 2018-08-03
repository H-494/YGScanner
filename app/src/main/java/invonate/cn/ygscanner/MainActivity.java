package invonate.cn.ygscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;

import invonate.cn.ygscanner.Entry.Ku;
import invonate.cn.ygscanner.Util.DatabaseHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String data = "";

    String XB = "";

    Ku ku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.smccrk).setOnClickListener(this);
        findViewById(R.id.smccrkqr).setOnClickListener(this);
        findViewById(R.id.smccrkqx).setOnClickListener(this);
        findViewById(R.id.smck).setOnClickListener(this);
        findViewById(R.id.smckqr).setOnClickListener(this);
        findViewById(R.id.smckqx).setOnClickListener(this);
        findViewById(R.id.smrk).setOnClickListener(this);
        findViewById(R.id.smrkqr).setOnClickListener(this);
        findViewById(R.id.smdd).setOnClickListener(this);
        findViewById(R.id.smddqr).setOnClickListener(this);
        findViewById(R.id.kccx).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
        findViewById(R.id.kongkwcx).setOnClickListener(this);
        findViewById(R.id.xcccsmrk).setOnClickListener(this);
        findViewById(R.id.xcccsmrkqr).setOnClickListener(this);
        findViewById(R.id.kwxxcx).setOnClickListener(this);
        data = getIntent().getExtras().getString("data");
        XB = getIntent().getExtras().getString("XB");
        ku = JSON.parseObject(data, Ku.class);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.smccrk:
                final ArrayList<String> list = getSmccrk();
                if (!list.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("已存在【" + list.get(0) + "】库区的提货信息，是否继续本次扫描")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, SmccrkActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("list", list);
                                    if (ku != null && !ku.getResult().equals("f")) {
                                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                                    }
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                } else {
                    intent = new Intent(this, SmccrkActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }
                break;
            case R.id.smccrkqr:
                ArrayList<String> list_smccrk = getSmccrk();
                if (list_smccrk.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("还没有产出入库记录")
                            .create().show();
                } else {
                    intent = new Intent(this, SmccrkqrActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }

                break;
            case R.id.smccrkqx:
                intent = new Intent(this, SmccrkqxActivity.class);
                break;
            case R.id.smck:
                final ArrayList<String> list2 = getSmck();
                if (!list2.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("已存在【" + list2.get(0) + "】库区的出库信息，是否继续本次扫描")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, SmckActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("list", list2);
                                    if (ku != null && !ku.getResult().equals("f")) {
                                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                                    }
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                } else {
                    intent = new Intent(this, SmckActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }
                break;
            case R.id.smckqr:
                ArrayList<String> list_ck = getSmck();
                if (list_ck.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("还没有出库记录")
                            .create().show();
                } else {
                    intent = new Intent(this, SmckqrActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }
                break;
            case R.id.smckqx:
                intent = new Intent(this, SmckqxActivity.class);
                break;
            case R.id.smrk:
                final ArrayList<String> list_rk = getSmrk();
                if (!list_rk.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("已存在【" + list_rk.get(0) + "】库区的入库信息，是否继续本次扫描")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, SmrkActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("list", list_rk);
                                    if (ku != null && !ku.getResult().equals("f")) {
                                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                                    }
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                } else {
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent = new Intent(this, SmrkActivity.class);
                    intent.putExtras(bundle);
                }
                break;
            case R.id.smrkqr:
                ArrayList<String> list_rkqr = getSmrk();
                if (list_rkqr.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("还没有入库记录")
                            .create().show();
                } else {
                    intent = new Intent(this, SmrkqrActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }
                break;
            case R.id.smdd:
                final ArrayList<String> list_dd = getSmdd();
                if (!list_dd.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("已存在【" + list_dd.get(0) + "】库区的倒剁信息，是否继续本次扫描")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, SmddActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("list", list_dd);
                                    if (ku != null && !ku.getResult().equals("f")) {
                                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                                    }
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                } else {
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent = new Intent(this, SmddActivity.class);
                    intent.putExtras(bundle);
                }

                break;
            case R.id.smddqr:
                ArrayList<String> list_ddqr = getSmdd();
                if (list_ddqr.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("还没有倒剁记录")
                            .create().show();
                } else {
                    intent = new Intent(this, SmddqrActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                }
                break;
            case R.id.kongkwcx:
                if (!XB.equals("XC")) {
                    Toast.makeText(this, "请返回登录界面选择线材", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    intent = new Intent(this, SmkkwcxActivity.class);
                    Bundle bundle = new Bundle();
                    if (ku != null && !ku.getResult().equals("f")) {
                        bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                    }
                    intent.putExtras(bundle);
                    break;
                }
            case R.id.xcccsmrk:
                if (!XB.equals("XC")) {
                    Toast.makeText(this, "请返回登录界面选择线材", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    final ArrayList<String> list_xcrq = getSmxcccrk();
                    if (!list_xcrq.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示")
                                .setMessage("已存在【" + list_xcrq.get(0) + "】库区的提货信息，是否继续本次扫描")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MainActivity.this, SmxcccActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putStringArrayList("list", list_xcrq);
                                        if (ku != null && !ku.getResult().equals("f")) {
                                            bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                                        }
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
                    } else {
                        intent = new Intent(this, SmxcccActivity.class);
                        Bundle bundle = new Bundle();
                        if (ku != null && !ku.getResult().equals("f")) {
                            bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                        }
                        intent.putExtras(bundle);
                    }
                    break;
                }
            case R.id.xcccsmrkqr:
                if (!XB.equals("XC")) {
                    Toast.makeText(this, "请返回登录界面选择线材", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    ArrayList<String> list_smxcccqr = getSmxcccrk();
                    if (list_smxcccqr.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示")
                                .setMessage("还没有线材产出入库记录")
                                .create().show();
                    } else {
                        intent = new Intent(this, SmxcccqrActivity.class);
                        Bundle bundle = new Bundle();
                        if (ku != null && !ku.getResult().equals("f")) {
                            bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                            bundle.putSerializable("chehao", (Serializable) ku.getData().get(0).getChehao());
                        }
                        intent.putExtras(bundle);
                    }
                    break;
                }
            case R.id.kwxxcx:
                intent = new Intent(this,SmkwcxActivity.class);
                break;
            case R.id.kccx:
                intent = new Intent(this, KccxActivity.class);
                Bundle bundle = new Bundle();
                if (ku != null && !ku.getResult().equals("f")) {
                    bundle.putSerializable("data", (Serializable) ku.getData().get(0).getKubie());
                }
                intent.putExtras(bundle);
                break;
            case R.id.exit:
                finish();
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 查询产出入库信息
     *
     * @return
     */

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
        }
        return list;
    }

    /**
     * 获取出库信息
     *
     * @return
     */
    private ArrayList<String> getSmck() {
        ArrayList<String> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT IKU,OKU,IKUBIE,OKUBIE FROM YG_CKXXM", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
        }
        return list;
    }

    /**
     * 获取入库记录
     *
     * @return
     */
    private ArrayList<String> getSmrk() {
        ArrayList<String> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KU,QU,PAI,KUBIE FROM YG_RKXXM", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
        }
        return list;
    }

    /**
     * 获取倒剁记录
     *
     * @return
     */
    private ArrayList<String> getSmdd() {
        ArrayList<String> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KU,QU,PAI,KUBIE FROM YG_DDXXM", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
        }
        return list;
    }

    /**
     * 查询产出入库信息
     *
     * @return
     */

    private ArrayList<String> getSmxcccrk() {
        ArrayList<String> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KU,QU,PAI,KUBIE FROM YG_XCXXM", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
        }
        return list;
    }

}
