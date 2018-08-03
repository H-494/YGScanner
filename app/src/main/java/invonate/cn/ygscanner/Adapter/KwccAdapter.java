package invonate.cn.ygscanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import invonate.cn.ygscanner.Entry.Kwcc;
import invonate.cn.ygscanner.R;

public class KwccAdapter extends BaseAdapter{
    private List<Kwcc.DataBean> data;
    private Context context;

    public KwccAdapter(List<Kwcc.DataBean> data, Context context) {
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
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.items_kwcc, viewGroup, false);
            holder.Key = view.findViewById(R.id.key);
            holder.Value = view.findViewById(R.id.value);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }
        holder.Key.setText(data.get(i).getKey());
        holder.Value.setText(data.get(i).getValue());
        return view;
    }

    class ViewHolder{
        TextView Key;
        TextView Value;
    }
}
