package com.example.user.tkpm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 27/02/2018.
 */

public class Adapter_RecyclerViewScript extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Script> mData;

    LayoutInflater layoutInflater;
    EventClick eventClick;

    public Adapter_RecyclerViewScript(Context mContext, List<Script> datas, EventClick eventClick) {
        this.mContext = mContext;
        this.mData = datas;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eventClick=eventClick;
    }


    public void addDataTop(Script item) {
        if (item == null)
            return;
        this.mData.add(0, item);
        notifyItemInserted(0);
    }

    public void removeData(int position) {
        this.mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void appendData(List<Script> items) {
        if (items.isEmpty() || items.size() == 0)
            return;
        int index = this.mData.size();
        this.mData.addAll(items);
        notifyItemRangeInserted(index, items.size());
    }

    public void refreshData() {
        //final int index = this.mData.size();
        this.mData.clear();
        notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_list_script, parent, false);

        return new ViewHolderItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderItem holderItem = (ViewHolderItem) holder;
        final Script item = mData.get(position);

        holderItem.txt_script.setText(item.getScript());
        final int mil=(int)Float.parseFloat(item.getStart())*1000;

        holderItem.txt_script.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventClick.onClick(mil);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView txt_script;


        public ViewHolderItem(View itemView) {
            super(itemView);

            txt_script = (TextView) itemView.findViewById(R.id.txt_script);

        }
    }


}
