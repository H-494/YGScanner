package invonate.cn.ygscanner.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "YGFH";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("DbHelper", "DbHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.i("onCreate", "onCreate");
        String sql = " CREATE TABLE YG_FHXXM  ( "//产出入库
                + " FORMID VARCHAR(40),"
                + " INPUTCODE VARCHAR(50),"
                + " LOADNO VARCHAR(50),"
                + " DELITYPE VARCHAR(500),"
                + " CARNO VARCHAR(500),"
                + " CONFIRMTIME VARCHAR(500),"
                + " INVID VARCHAR(500),"
                + " PAPERNO VARCHAR(500),"
                + " ORDERNO VARCHAR(500),"
                + " ORDERITEM VARCHAR(500),"
                + " GRADE VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " SCHDNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " ORDERLENGTH VARCHAR(500),"
                + " QTY numeric(8, 3) DEFAULT ( 0 ),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ),"
                + " RESULT VARCHAR(500),"
                + " KUBIE VARCHAR(10),"
                + " KU VARCHAR(10),"
                + " QU VARCHAR(10),"
                + " PAI VARCHAR(10))";

        db.execSQL(sql);

        String sql_ck = " CREATE TABLE YG_CKXXM  ( "//出库
                + " FORMID VARCHAR(40),"
                + " INPUTCODE VARCHAR(50),"
                + " LOADNO VARCHAR(50),"
                + " DELITYPE VARCHAR(500),"
                + " CARNO VARCHAR(500),"
                + " CONFIRMTIME VARCHAR(500),"
                + " INVID VARCHAR(500),"
                + " PAPERNO VARCHAR(500),"
                + " ORDERNO VARCHAR(500),"
                + " ORDERITEM VARCHAR(500),"
                + " GRADE VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " SCHDNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " ORDERLENGTH VARCHAR(500),"
                + " QTY numeric(8, 3) DEFAULT ( 0 ),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ),"
                + " RESULT VARCHAR(500),"
                + " OKUBIE VARCHAR(10),"
                + " IKUBIE VARCHAR(10),"
                + " OKU VARCHAR(10),"
                + " IKU VARCHAR(10))";

        db.execSQL(sql_ck);

        String sql_rk = " CREATE TABLE YG_RKXXM  ( "//入库
                + " FORMID VARCHAR(40),"
                + " INPUTCODE VARCHAR(50),"
                + " LOADNO VARCHAR(50),"
                + " DELITYPE VARCHAR(500),"
                + " CARNO VARCHAR(500),"
                + " CONFIRMTIME VARCHAR(500),"
                + " INVID VARCHAR(500),"
                + " PAPERNO VARCHAR(500),"
                + " ORDERNO VARCHAR(500),"
                + " ORDERITEM VARCHAR(500),"
                + " GRADE VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " SCHDNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " ORDERLENGTH VARCHAR(500),"
                + " QTY numeric(8, 3) DEFAULT ( 0 ),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ),"
                + " RESULT VARCHAR(500),"
                + " KUBIE VARCHAR(10),"
                + " KU VARCHAR(10),"
                + " QU VARCHAR(10),"
                + " PAI VARCHAR(10))";

        db.execSQL(sql_rk);

        String sql_dd = " CREATE TABLE YG_DDXXM  ( "//倒跺
                + " FORMID VARCHAR(40),"
                + " INPUTCODE VARCHAR(50),"
                + " LOADNO VARCHAR(50),"
                + " DELITYPE VARCHAR(500),"
                + " CARNO VARCHAR(500),"
                + " CONFIRMTIME VARCHAR(500),"
                + " INVID VARCHAR(500),"
                + " PAPERNO VARCHAR(500),"
                + " ORDERNO VARCHAR(500),"
                + " ORDERITEM VARCHAR(500),"
                + " GRADE VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " SCHDNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " ORDERLENGTH VARCHAR(500),"
                + " QTY numeric(8, 3) DEFAULT ( 0 ),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ),"
                + " RESULT VARCHAR(500),"
                + " KUBIE VARCHAR(10),"
                + " KU VARCHAR(10),"
                + " QU VARCHAR(10),"
                + " PAI VARCHAR(10))";

        db.execSQL(sql_dd);

        String sql_xc = " CREATE TABLE YG_XCXXM  ( "//线材产出入库
                + " FORMID VARCHAR(40),"
                + " INPUTCODE VARCHAR(50),"
                + " LOADNO VARCHAR(50),"
                + " DELITYPE VARCHAR(500),"
                + " CARNO VARCHAR(500),"
                + " CONFIRMTIME VARCHAR(500),"
                + " INVID VARCHAR(500),"
                + " PAPERNO VARCHAR(500),"
                + " ORDERNO VARCHAR(500),"
                + " ORDERITEM VARCHAR(500),"
                + " GRADE VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " SCHDNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " ORDERLENGTH VARCHAR(500),"
                + " QTY numeric(8, 3) DEFAULT ( 0 ),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ),"
                + " RESULT VARCHAR(500),"
                + " KUBIE VARCHAR(10),"
                + " KU VARCHAR(10),"
                + " QU VARCHAR(10),"
                + " PAI VARCHAR(10))";

        db.execSQL(sql_xc);

        String sql1 = " CREATE TABLE YG_FHMX  ( "
                + " SCHDNO VARCHAR(500),"
                + " PRODSPECNO VARCHAR(500),"
                + " ORDERTHICK VARCHAR(500),"
                + " THEOWGT numeric(8, 3) DEFAULT ( 0 ),"
                + " REALWTG numeric(8, 3) DEFAULT ( 0 ))";
        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

}
