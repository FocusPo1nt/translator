package com.focuspoint.translator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Adapter for show language item from list(Language);
 */

public class LanguageAdapter extends RecyclerView.Adapter {
    private List<Language> list;


    public LanguageAdapter(List<Language> list){
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_view, parent, false);
        return new RecyclerView.ViewHolder(root) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView languageText = (TextView) (holder.itemView.findViewById(R.id.language_text_view));
        TextView codeText =  (TextView) (holder.itemView.findViewById(R.id.code_text_view));
        Language language = list.get(position);

        languageText.setText(language.getDescription());
        codeText.setText(language.getCode());
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void replaceData(List<Language> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    private final PublishSubject<Language> onClickSubject = PublishSubject.create();


    public Observable<Language> getPositionClicks(){
        return onClickSubject.asObservable();
    }
}
