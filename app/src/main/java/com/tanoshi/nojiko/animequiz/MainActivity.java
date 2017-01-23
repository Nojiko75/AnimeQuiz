package com.tanoshi.nojiko.animequiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button game_btn;
    private Button ranking_btn;
    private Button quit_btn;

    private DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        this.game_btn = (Button) findViewById(R.id.game_btn);
        this.ranking_btn = (Button) findViewById(R.id.ranking_btn);
        this.quit_btn = (Button) findViewById(R.id.quit_btn);

        this.db = new DbHelper(this);
        try {
            this.db.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.game_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });

        this.ranking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(intent);
            }
        });

        this.quit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
