package invonate.cn.ygscanner.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import invonate.cn.ygscanner.Adapter.CcrkqrAdapter;
import invonate.cn.ygscanner.Entry.Goods;
import invonate.cn.ygscanner.R;
import invonate.cn.ygscanner.SmccrkqrActivity;
import invonate.cn.ygscanner.Util.DatabaseHelper;

public class SmxcrqDelete extends Fragment {
    @BindView(R.id.list_goods)
    SwipeMenuListView listGoods;
    Unbinder unbinder;

    SQLiteDatabase db;
    List<Goods> list_goods = new ArrayList<>();
    CcrkqrAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_smccrqr, container, false);
        unbinder = ButterKnife.bind(this, view);
        DatabaseHelper database = new DatabaseHelper(getActivity());
        db = database.getReadableDatabase();
        getData();
        return view;
    }

    private void getData() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(180);
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(18);
//                deleteItem.setTitleColor(Color.RED);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listGoods.setMenuCreator(creator);

        listGoods.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // delete item
                deleteItem(position, list_goods.get(position).getCode());
                return false;
            }
        });

        Cursor cursor = db.rawQuery("SELECT INVID,REALWTG,ORDERTHICK,PRODSPECNO,THEOWGT FROM YG_XCXXM ", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Goods g = new Goods();
                g.setCode(cursor.getString(0));
                g.setRealWeight(cursor.getString(1));
                g.setType(cursor.getString(2));
                g.setMaterial(cursor.getString(3));
                g.setWeight(cursor.getString(4));
                list_goods.add(g);
            }
        }
        Log.i("list_goods", JSON.toJSONString(list_goods));
        adapter = new CcrkqrAdapter(list_goods, getActivity());
        listGoods.setAdapter(adapter);
    }

    private void deleteItem(final int position, final String invId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("确认删除【" + invId + "】吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int result = db.delete("YG_XCXXM", "INVID=?", new String[]{invId});
                        if (result > 0) {
                            list_goods.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        ((SmccrkqrActivity) getActivity()).getSum();
                    }
                }).setNegativeButton("否", null)
                .create().show();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void clear() {
        list_goods.clear();
        adapter.notifyDataSetChanged();
    }
}
