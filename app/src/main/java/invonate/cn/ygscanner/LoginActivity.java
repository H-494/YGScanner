package invonate.cn.ygscanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.lang.ref.WeakReference;

import invonate.cn.ygscanner.Permission.PermissionsActivity;
import invonate.cn.ygscanner.Permission.PermissionsChecker;
import invonate.cn.ygscanner.Util.DownLoadRunnable;
import invonate.cn.ygscanner.Util.IpConfig;
import invonate.cn.ygscanner.Util.MyProvide;
import invonate.cn.ygscanner.Util.MyUtils;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0x999; // 请求码

    private EditText view_userName;
    private EditText view_password;
    private CheckBox view_rememberMe;
    private Button view_loginSubmit;

    private Spinner spinnerbz;
    private Spinner spinnerzl;

    private static final String[] m = {" ", "甲", "乙", "丙"};
    private static final String[] n = {" ", "棒材", "线材"};


    /**
     * 用来操作SharePreferences的标识
     */
    private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

    /**
     * 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

    /**
     * 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

    /**
     * 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_BZ = "MAP_LOGIN_BZ";

    /**
     * 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_ZL = "MAP_LOGIN_ZL";

    /**
     * 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误
     */
    private boolean isNetError;

    /**
     * 登录loading提示框
     */
    private ProgressDialog proDialog;
    final MyHandler mHandler = new MyHandler(this);

    /**
     * 登录是否成功
     */
    private boolean loginState = false;

    /**
     * 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面
     */
    private class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                if (activity.proDialog != null) {
                    activity.proDialog.dismiss();
                }
                switch (msg.what) {
                    case 0:
                        activity.isNetError = msg.getData()
                                .getBoolean("isNetError");
                        if (activity.isNetError) {
                            Toast.makeText(activity,
                                    "登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // 用户名和密码错误
                        else {
                            Toast.makeText(activity, "登陆失败,请输入正确的用户名和密码!",
                                    Toast.LENGTH_SHORT).show();
                            // 清除以前的SharePreferences密码
                            activity.clearSharePassword();
                        }
                        break;
                    case 1:
//                        int newVerCode = msg.getData().getInt("newVerCode");
//                        int vercode = ConfigUtil.getVerCode(activity); // 获取老的version
//
//                        if (newVerCode > vercode) {
//                            activity.updateManager = new UpdateAppManager(activity);
//                            activity.updateManager.checkUpdateInfo();
//                        } else {
//                            // notNewVersionShow(); // 提示当前为最新版本
//                        }
                        Toast.makeText(activity, "登录成功!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(activity, "请输入班组或线别!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("监测到有新版本更新，是否下载最新版本？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String url = msg.getData().getString("url");
//                                        String url = "gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";
                                        new Thread(new DownLoadRunnable(LoginActivity.this, "http://" + url, "永钢扫描发货管理软件", 0, handler)).start();
                                    }
                                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                        break;
                    default:
                        break;
                }

            }
        }
    }

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    /**
     * 清除密码
     */
    private void clearSharePassword() {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
        share = null;
    }

    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPermissionsChecker = new PermissionsChecker(this);
        view_userName = (EditText) findViewById(R.id.loginUserNameEdit);
        view_password = (EditText) findViewById(R.id.loginPasswordEdit);
        spinnerbz = (Spinner) findViewById(R.id.Spinnerbz);
        spinnerzl = (Spinner) findViewById(R.id.Spinnerzl);
        view_rememberMe = (CheckBox) findViewById(R.id.loginRememberMeCheckBox);
        view_loginSubmit = (Button) findViewById(R.id.loginSubmit);

        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, m);
        ArrayAdapter adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, n);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinnerbz.setAdapter(adapter);
        spinnerzl.setAdapter(adapter2);

        initView(false);

        view_loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proDialog = ProgressDialog.show(LoginActivity.this, "连接中..",
                        "连接中..请稍后....", true, true);
                // 开一个线程进行登录验证,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
                Thread loginThread = new Thread(new LoginFailureHandler());
                loginThread.start();
            }
        });

        view_rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (view_rememberMe.isChecked()) {
                    Toast.makeText(LoginActivity.this, "如果登录成功,以后用户名和密码会自动输入!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        new Thread(new UpdateThread()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    /**
     *
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
        Log.i("onActivityResult", "requestCode=" + requestCode + "resultCode" + resultCode);
        if (requestCode == 26) {
            checkO();
        }
    }

    /**
     * 初始化界面
     *
     * @param isRememberMe 如果当时点击了RememberMe,并且登陆成功过一次,则saveSharePreferences(true,ture)后,
     *                     则直接进入
     */
    private void initView(boolean isRememberMe) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        String userName = share.getString(SHARE_LOGIN_USERNAME, "");
        String password = share.getString(SHARE_LOGIN_PASSWORD, "");

        if (!"".equals(userName)) {
            view_userName.setText(userName);
        }
        if (!"".equals(password)) {
            view_password.setText(password);
            view_rememberMe.setChecked(true);
        }
        // 如果密码也保存了,则直接让登陆按钮获取焦点
        if (view_password.getText().toString().length() > 0) {
            // view_loginSubmit.requestFocus();
            // view_password.requestFocus();
        }
        share = null;
    }


    /**
     * 在线登录
     *
     * @author MXW
     */
    class LoginFailureHandler implements Runnable {
        @Override
        public void run() {
            boolean loadstate = false;
            String loadstatemessage = "";
            String userName = view_userName.getText().toString();
            String password = view_password.getText().toString();
            String bzmc = (String) spinnerbz.getSelectedItem();
            String zlmc = (String) spinnerzl.getSelectedItem();
            if (" ".equals(bzmc)) {
                loadstatemessage = "请选择班组！";
                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                bundle.putString("loadstatemessage", loadstatemessage);// 信息
                message.setData(bundle);
                mHandler.sendMessage(message);
                clearSharePassword();
                return;
            }
            if (" ".equals(zlmc)) {
                loadstatemessage = "请选择类型！";
                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                bundle.putString("loadstatemessage", loadstatemessage);// 信息
                message.setData(bundle);
                mHandler.sendMessage(message);
                clearSharePassword();
                return;
            }
            String uname = userName;
            String bz = bzmc;
            String zl = zlmc;
            // 验证需要返回用户名姓名 如果是NORE 说明用户名不存在或者 权限不够

            String result = "";
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            // EndPoint
            //String endPoint = "http://10.10.4.210:9081/erp/sh/Scanning.ws";//测试
            String endPoint = "http://" + IpConfig.ip + "/erp/sh/Scanning.ws";//正式
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            String bz1 = "";
            if ("甲".equals(bzmc)) {
                bz1 = "A";
            }
            if ("乙".equals(bzmc)) {
                bz1 = "B";
            }
            if ("丙".equals(bzmc)) {
                bz1 = "C";
            }
            if (zl.equals("线材")) {
                zl = "XC";
            }
            if (zl.equals("棒材")) {
                zl = "BC";
            }
            String data = String.format("%-10s", "SHHL31L")
                    + String.format("%-10s", uname) + String.format("%-10s", password)
                    + String.format("%-10s", bz1) + String.format("%-5s", zl) + "*";

            Log.i("params", data);
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
                // 调用WebService
                transport.call(soapAction, envelope);
                loadstate = true;
                Object object = (Object) envelope.getResponse();
                // 获取返回的结果
                result = object.toString();
                Log.i("data",result);
                String result1 = result.substring(10, 11).trim();
                if ("F".equals(result1)) {
                    loadstate = false;
                    loadstatemessage = "登录失败！";
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("loadstatemessage", loadstatemessage);// 信息
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    clearSharePassword();
                    return;
                } else {
                    loadstate = true;
                    loadstatemessage = "登录成功！";
                    Message message = new Message();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("loadstatemessage", loadstatemessage);// 信息
                    bundle.putString("XB",zlmc);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 登陆成功  要删
            if (loadstate) {

                // 成功后，将用户名放入SharedPreferences
                SharedPreferences preferencesDefault = getSharedPreferences(
                        "share", Context.MODE_PRIVATE);
                // 先清空
                SharedPreferences.Editor editorDefault = preferencesDefault.edit();
                editorDefault.clear();
                editorDefault.putString("name", "");
                editorDefault.putString("bzmc", "");
                editorDefault.putString("zlmc", "");
                // editorDefault1.putInt("login", -1);
                editorDefault.commit();

                // 再放入
                SharedPreferences.Editor editorDefault1 = preferencesDefault.edit();
                editorDefault1.putString("bzmc", bz);
                editorDefault1.putString("zlmc", zl);
                editorDefault1.putString("name", uname);
                editorDefault1.commit();
                //editorDefault.putInt("login", 0); // 在线 是0

                if (isRememberMe()) {
                    saveSharePreferences(true, true);
                } else {
                    saveSharePreferences(true, false);
                }

                if (!view_rememberMe.isChecked()) {
                    clearSharePassword();
                }

                Bundle bundle = new Bundle();
                bundle.putString("data",result);
                bundle.putString("XB",zl);
//                bundle.putString("data", result);
                // 需要传输数据到登陆后的界面,
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(LoginActivity.this, MainActivity.class);
                // 转向登陆后的页面
                startActivity(intent);
                // 结束该登陆Activity
                proDialog.dismiss();
                LoginActivity.this.finish();

            } else {
                // 通过调用handler来通知UI主线程更新UI,
                Message message = new Message();
                message.what = 0;
                Bundle bundle = new Bundle();
                bundle.putBoolean("isNetError", isNetError);
                message.setData(bundle);
                mHandler.sendMessage(message);
                clearSharePassword();
            }
        }
    }

    /**
     * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
     *
     * @param saveUserName 是否将用户名保存到SharePreferences
     * @param savePassword 是否将密码保存到SharePreferences
     */
    private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        if (saveUserName) {
            share.edit()
                    .putString(SHARE_LOGIN_USERNAME,
                            view_userName.getText().toString()).commit();
        }
        if (savePassword) {
            share.edit()
                    .putString(SHARE_LOGIN_PASSWORD,
                            view_password.getText().toString()).commit();
        }
        share = null;
		/*if (savePassword) {
			share.edit()
					.putString(SHARE_LOGIN_PASSWORD,
							view_password.getText().toString()).commit();
		}*/
		/*Editor edit=share.edit();
		 edit.putString("name", userName2);
	     edit.putString("password", password2);
	     edit.putString("bzmc", bzmc2);
	     edit.putString("zlmc", zlmc2);
	     edit.commit();*/
    }

    /**
     * 记住我的选项是否勾选
     */
    private boolean isRememberMe() {
        return view_rememberMe.isChecked();
    }


    class UpdateThread implements Runnable {
        @Override
        public void run() {
            String result = "";
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            // EndPoint
            //String endPoint = "http://10.10.4.210:9081/erp/sh/Scanning.ws";//测试
            String endPoint = "http://" + IpConfig.ip + "/erp/sh/Scanning.ws";//正式
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);
            String data = String.format("%-10s", "SHHL46GX") + "A*";
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
                // 调用WebService
                transport.call(soapAction, envelope);
                Object object = (Object) envelope.getResponse();
                // 获取返回的结果
                result = object.toString();
                Log.i("update", result.toString());
                String result1 = result.substring(10, 11).trim();
                if ("A".equals(result1)) {
                    int version = Integer.parseInt(result.substring(11, 14).trim());
                    if (version > GetVersion(LoginActivity.this)) {
                        String url = result.substring(14, result.length() - 1).trim();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", url);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 3;
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //handler更新ui
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(LoginActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    checkO();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(LoginActivity.this, "更新出错", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
            }
            return false;
        }
    });


    // 取得版本号
    public static int GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 检查安卓8.0
     */
    private void checkO() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {//有权限
                install();
            } else { // 没有权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10010);
            }
        } else {
            install();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10010:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    install();
                } else {
                    startInstallPermissionSettingActivity();
                }
                break;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, 26);
    }

    public void install() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "/" + MyUtils.PACKAGE_NAME + "/" + MyUtils.APP_NAME);
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri data = MyProvide.getUriForFile(this, "invonate.cn.ygscanner.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
        Log.i("install", "finish");
    }
}
