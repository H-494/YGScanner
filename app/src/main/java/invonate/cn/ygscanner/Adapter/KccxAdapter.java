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
import invonate.cn.ygscanner.Entry.Statistics;
import invonate.cn.ygscanner.R;

public class KccxAdapter extends BaseAdapter {
    private List<Statistics> data;
    private Context context;

    public KccxAdapter(List<Statistics> data, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_kccx, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.id.setText(data.get(i).getCode());
        holder.sum.setText(String.format("%d", data.get(i).getSum()));
        holder.type.setText(data.get(i).getType());
        holder.material.setText(data.get(i).getMaterial());
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.id)
        TextView id;
        @BindView(R.id.sum)
        TextView sum;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.material)
        TextView material;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
