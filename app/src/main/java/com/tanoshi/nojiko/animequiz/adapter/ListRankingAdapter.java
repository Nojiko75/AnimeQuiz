package com.tanoshi.nojiko.animequiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanoshi.nojiko.animequiz.R;
import com.tanoshi.nojiko.animequiz.model.Ranking;

import java.util.List;

/**
 * Created by nojiko on 31/01/2017.
 */
public class ListRankingAdapter extends BaseAdapter {

    private Context context;
    private List<Ranking> rankingList;

    public ListRankingAdapter(Context context, List<Ranking> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    @Override
    public int getCount() {
        return rankingList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_ranking_items, null);
        ImageView imgTop = (ImageView) view.findViewById(R.id.img_top);
        TextView score_txt = (TextView) view.findViewById(R.id.score_txt);

        if(position == 0) {
            imgTop.setImageResource(R.drawable.top_1);
        } else if (position == 1) {
            imgTop.setImageResource(R.drawable.top_2);
        } else {
            imgTop.setImageResource(R.drawable.top_3);
        }

        score_txt.setText(String.valueOf(rankingList.get(position).getScore()));
        return view;
    }
}
