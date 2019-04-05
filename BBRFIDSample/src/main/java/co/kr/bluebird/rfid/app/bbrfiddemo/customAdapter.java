package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class customAdapter extends BaseAdapter {
    private static final String TAG = customAdapter.class.getSimpleName();

    private static final boolean D = false;


    private transaction_listView main;
    private Context mContext;
    private int position;


    public customAdapter(transaction_listView main, Context mContext) {
        this.main = main;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return main.transElements.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "getItem");
        return main.transElements.get(position);
    }

    public void selectedItem(int position) {
        this.position = position; //position must be a global variable
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (D) Log.d(TAG, "getView");
        ViewHolderItem holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.transactions, null);
            holder = new ViewHolderItem();
            holder.transName = (TextView) convertView.findViewById(R.id.transName);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        holder.transName.setText(main.transElements.get(position).getEname());


        return convertView;
    }

    public void removeAllItem() {
        if (D) Log.d(TAG, "removeAllItem");
        main.transElements.clear();
        notifyDataSetChanged();
    }

    public int getTotalCount() {
        if (D) Log.d(TAG, "getTotalCount");
        return main.transElements.size();
    }

    static class ViewHolderItem {
        TextView transName;

    }
}