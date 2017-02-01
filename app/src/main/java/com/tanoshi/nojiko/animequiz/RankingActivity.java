package com.tanoshi.nojiko.animequiz;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tanoshi.nojiko.animequiz.adapter.ListRankingAdapter;
import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.Ranking;

import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private ListView listView;
    private ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        listView = (ListView) findViewById(R.id.list_ranking);
        back_btn = (ImageButton) findViewById(R.id.back_btn);
        DbHelper db = new DbHelper(this);

        List<Ranking> rankingList = db.getRanking();
        if(rankingList.size() > 0) {
            ListRankingAdapter adapter = new ListRankingAdapter(this, rankingList);
            listView.setAdapter(adapter);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
