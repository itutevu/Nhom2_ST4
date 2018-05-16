package com.example.user.tkpm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

/**
 * Created by USER on 27/02/2018.
 */

public class Adapter_RecyclerViewMyVideos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<NameVideoModel> mData;

    LayoutInflater layoutInflater;
    EventClickPlayMyVideos eventClickPlay;
    EventClickRemoveMyVideos eventClickRemove;

    public Adapter_RecyclerViewMyVideos(Context mContext, List<NameVideoModel> datas, EventClickPlayMyVideos eventClickPlay, EventClickRemoveMyVideos eventClickRemove) {
        this.mContext = mContext;
        this.mData = datas;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eventClickPlay = eventClickPlay;
        this.eventClickRemove = eventClickRemove;
    }


    public void addDataTop(NameVideoModel item) {
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

    public void appendData(List<NameVideoModel> items) {
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
        View v = layoutInflater.inflate(R.layout.item_list_hscript, parent, false);

        return new ViewHolderItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderItem holderItem = (ViewHolderItem) holder;
        final NameVideoModel item = mData.get(position);

        holderItem.txt_script.setText(Html.fromHtml(item.getTitle()));

        holderItem.img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventClickPlay.onClickPlay(item.getId(), position);
            }
        });

        holderItem.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventClickRemove.onClickRemove(item.getId(), position);
            }
        });

    }

    /*public String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json"
                );

                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }*/

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
        ImageView img_play;
        ImageView img_remove;

        public ViewHolderItem(View itemView) {
            super(itemView);

            txt_script = (TextView) itemView.findViewById(R.id.txt_script);
            img_play = itemView.findViewById(R.id.img_play);
            img_remove = itemView.findViewById(R.id.img_remove);

        }
    }


}
