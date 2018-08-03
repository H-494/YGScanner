package invonate.cn.ygscanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import invonate.cn.ygscanner.Entry.Goods;
import invonate.cn.ygscanner.R;

public class CcrkqrAdapter extends BaseAdapter {

    private List<Goods> data;
    private Context context;

    public CcrkqrAdapter(List<Goods> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ccrkqr, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Goods g = data.get(i);
        holder.id.setText(String.format("标牌条码编号：%s", g.getCode()));
        holder.code1.setText(String.format("炉批号：%s", g.getCode().substring(0, g.getCode().length() - 3)));
        holder.code2.setText(String.format("卷：%s", g.getCode().substring(g.getCode().length() - 3, g.getCode().length())));
        holder.type.setText(String.format("规格：%s", g.getType()));
        holder.material.setText(String.format("材质：%s", g.getMaterial()));
        holder.weight.setText(String.format("理重：%s", g.getWeight()));
        holder.realWeight.setText(String.format("实磅：%s", g.getRealWeight()));
        return view;
    }


    static class ViewHolder {
        @BindView(R.id.code1)
        TextView code1;
        @BindView(R.id.code2)
        TextView code2;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.material)
        TextView material;
        @BindView(R.id.weight)
        TextView weight;
        @BindView(R.id.realWeight)
        TextView realWeight;
        @BindView(R.id.id)
        TextView id;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
