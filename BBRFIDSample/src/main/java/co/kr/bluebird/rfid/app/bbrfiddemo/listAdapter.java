package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.CopyOnWriteArrayList;

import co.kr.bluebird.rfid.app.bbrfiddemo.control.TagListAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BRReplFragment;


public class listAdapter extends BaseAdapter {





    private static final String TAG = TagListAdapter.class.getSimpleName();

    private static final boolean D = false;

    private static final int MAX_LIST_COUNT = 50000;

    private int mListCycleCount = 0;

    private CopyOnWriteArrayList <repl_elements> mItemList;

    private CopyOnWriteArrayList<String> mTagList;

    public String idi;
    private BRReplFragment main;
    private Context mContext;
    private int position;


    public listAdapter(BRReplFragment main, Context mContext)
    {
        this.main = main;
        this.mContext= mContext;
    }

    @Override
    public int getCount() {
        return  main.elements.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "getItem");
        return main.elements.get(position);
    }

    public void selectedItem(int position)
    {
        this.position = position; //position must be a global variable
    }
    public int getItemDupCount(int position) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "getItem");
        return main.elements.get(position).mDupCount;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "getItemId");
        return position;
    }
    static class ViewHolderItem {
        TextView style,size,color,it_id;
        Button pick,unpick;
        RelativeLayout rowsdimmed;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (D) Log.d(TAG, "getView");
        ViewHolderItem holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.table_rows, null);
             holder = new ViewHolderItem();
            holder.style = (TextView) convertView.findViewById(R.id.style);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.color = (TextView) convertView.findViewById(R.id.color);
            holder.it_id = (TextView) convertView.findViewById(R.id.code);
            holder.pick = (Button) convertView.findViewById(R.id.pickButton);
            holder.rowsdimmed = (RelativeLayout) convertView.findViewById(R.id.rowsdimmed);
            holder.unpick = (Button)convertView.findViewById(R.id.unpickButton);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolderItem) convertView.getTag();
        }

        holder.style.setText(main.elements.get(position).getStyle());
        holder.size.setText(main.elements.get(position).getSize());
        holder.color.setText(main.elements.get(position).getColor());
        holder.it_id.setText(main.elements.get(position).getIt_id());



            holder.pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rowsdimmed.setVisibility(View.VISIBLE);
                    holder.unpick.setVisibility(View.VISIBLE);
                    holder.pick.setVisibility(View.GONE);

                }
            });

            holder.unpick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rowsdimmed.setVisibility(View.GONE);
                    holder.unpick.setVisibility(View.GONE);
                    holder.pick.setVisibility(View.VISIBLE);
                }
            });



        return convertView;
    }

    public void addItem(int img, String upText, String downText, boolean hasPC, boolean filter) {
        if (D) Log.d(TAG, "addItem " + filter);
        if (filter) {
            if (mTagList.contains(upText)) {
                if (D) Log.d(TAG, "count++ " + filter);
                int idx = mTagList.indexOf(upText);
                main.elements.get(idx).mDupCount = (main.elements.get(idx).mDupCount) + 1;
                this.notifyDataSetInvalidated();
                return;
            }
            if (mItemList.size() == MAX_LIST_COUNT) {
                mTagList.clear();
                mItemList.clear();
                notifyDataSetChanged();
                mListCycleCount++;
            }
            repl_elements item = new repl_elements();
            item.mIv = img;
            item.items = upText;// + Long.toString(mItemList.size() + 1);
            item.mDt = downText;
            item.it_id= downText;
            item.mHasPc = hasPC;
            item.mDupCount = 1;

            mTagList.add(upText);
            main.elements.add(item);
            notifyDataSetChanged();
        }
        else {
            if (main.elements.size() == MAX_LIST_COUNT) {
                mTagList.clear();
                main.elements.clear();
                notifyDataSetChanged();
                mListCycleCount++;
            }
            repl_elements item = new repl_elements();
            item.mIv = img;
            item.items = upText;// + Long.toString(mItemList.size() + 1);
            item.mDt = downText;
            item.it_id= downText;
            item.mHasPc = hasPC;
            item.mDupCount = 1;
            mItemList.add(item);
            notifyDataSetChanged();
        }
    }
    public void removeAllItem() {
        if (D) Log.d(TAG, "removeAllItem");
        main.elements.clear();
        mListCycleCount = 0;
        notifyDataSetChanged();
    }

    public int getTotalCount() {
        if (D) Log.d(TAG, "getTotalCount");
        return (mListCycleCount * MAX_LIST_COUNT) + main.elements.size();
    }
}