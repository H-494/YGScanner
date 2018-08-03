package invonate.cn.ygscanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import invonate.cn.ygscanner.Entry.Kkwcx;
import invonate.cn.ygscanner.R;

public class KkwxxAdapter extends BaseAdapter {
    private Context context;
    List<Kkwcx.DataBean> data;
    private OnButtonClickListener onItmeClickListener;

    public KkwxxAdapter(Context context, List<Kkwcx.DataBean> data) {
        this.context = context;
        this.data = data;
    }

    public void setOnButtonClickListener(OnButtonClickListener onItmeClickListener) {
        this.onItmeClickListener = onItmeClickListener;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        KkwxxAdapter.ViewHolder holder = null;
        if (view == null) {
            holder = new KkwxxAdapter.ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_kkwcx, viewGroup, false);
            holder.kubie = view.findViewById(R.id.kubie);
            holder.ku = view.findViewById(R.id.ku);
            holder.qv = view.findViewById(R.id.qv);
            holder.view = view.findViewById(R.id.view);
            view.setTag(holder);
        }else {
            holder = (KkwxxAdapter.ViewHolder)view.getTag();
        }
        holder.kubie.setText(data.get(i).getWAREHOUSENO());
        holder.ku.setText(data.get(i).getWARENO());
        holder.qv.setText(data.get(i).getARENO());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItmeClickListener.onCLick(view,i);
            }
        });
        return view;
    }

    class ViewHolder{
        LinearLayout view;
        TextView kubie;
        TextView ku;
        TextView qv;
    }

    /**
     * 点击事件
     */
    public interface OnButtonClickListener {
        void onCLick(View view, int position);
    }
}
