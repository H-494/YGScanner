package invonate.cn.ygscanner.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import invonate.cn.ygscanner.Adapter.StatisticsAdapter;
import invonate.cn.ygscanner.Entry.Statistics;
import invonate.cn.ygscanner.R;
import invonate.cn.ygscanner.Util.DatabaseHelper;

public class Smrkqr extends Fragment {
    @BindView(R.id.list_goods)
    ListView listGoods;
    Unbinder unbinder;

    SQLiteDatabase db;
    List<Statistics> list_statistics = new ArrayList<>();
    StatisticsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_smccrqr, container, false);
        unbinder = ButterKnife.bind(this, view);
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        db = helper.getReadableDatabase();
        getData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getData() {
        if (db != null) {
            list_statistics.clear();
            Cursor cursor = db.rawQuery("SELECT schdno,count(*) as js,sum(realwtg) as realwtg ,orderthick,prodspecno,sum(theowgt) as theowgt FROM YG_RKXXM group by schdno", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Statistics g = new Statistics();
                    g.setCode(cursor.getString(0));
                    g.setSum(cursor.getInt(1));
                    g.setRealWeight(cursor.getString(2));
                    g.setType(cursor.getString(3));
                    g.setMaterial(cursor.getString(4));
                    g.setWeight(cursor.getString(5));
                    list_statistics.add(g);
                }
            }
            Log.i("list_goods", JSON.toJSONString(list_statistics));
            adapter = new StatisticsAdapter(list_statistics, getActivity());
            listGoods.setAdapter(adapter);
        }
    }

    public void clear() {
        list_statistics.clear();
        adapter.notifyDataSetChanged();
    }
}
