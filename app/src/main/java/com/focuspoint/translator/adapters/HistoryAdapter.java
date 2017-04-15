package com.focuspoint.translator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;

import java.util.List;

/**
 *
 */

public class HistoryAdapter extends RecyclerView.Adapter {

    private List<Translation> list;

    public HistoryAdapter (List<Translation> list){
        this.list = list;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(new TextView(parent.getContext())){};

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String direction = "";
        try{direction = list.get(position).getDirection();}catch (Exception e){};

        String result = list.get(position).getInput() +  " " +list.get(position).getOutput() + " " + direction;
        ((TextView) holder.itemView).setText(result);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void replaceData(List<Translation> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

}
