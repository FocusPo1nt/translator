package com.focuspoint.translator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.TranslationScreenContract;
import com.focuspoint.translator.screen.fragment.HistoryFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class HistoryAdapter extends RecyclerView.Adapter {

    private List<Translation> list;
//    private HistoryScreenContract.View fragment;

    private HistoryScreenContract.Presenter presenter;

    public HistoryAdapter (List<Translation> list, HistoryScreenContract.Presenter presenter){
        this.list = list;
        this.presenter = presenter;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_view, parent, false);
        return new TranslationViewHolder(root) {};

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((TranslationViewHolder)holder).setTranslation(list.get(position));
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


    public class TranslationViewHolder extends RecyclerView.ViewHolder {



        @BindView(R.id.favorite_frame) FrameLayout favoriteFrame;
        @BindView(R.id.translation_frame) FrameLayout translationFrame;
        @BindView(R.id.star_image) ImageView starImageView;
        @BindView(R.id.input_text_view) TextView inputTextView;
        @BindView(R.id.output_text_view) TextView outputTextView;
        @BindView(R.id.direction_text_view) TextView directionTextView;


        public TranslationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void setTranslation(Translation translation){



            starImageView.setImageResource(
                    translation.isFavorite() ? R.drawable.star_a : R.drawable.star_p);

            inputTextView.setText(translation.getInput());
            outputTextView.setText(translation.getOutput());
            directionTextView.setText(translation.getDirection().toUpperCase());

            favoriteFrame.setOnClickListener(v -> {

                //TODO do with presenter
                System.out.println(translation);
                starImageView.setImageResource(
                        translation.isFavorite() ?  R.drawable.star_p : R.drawable.star_a);
                presenter.setFavorite(translation, !translation.isFavorite());
            });

            translationFrame.setOnClickListener(v -> presenter.choose(translation));

        }
    }



}
