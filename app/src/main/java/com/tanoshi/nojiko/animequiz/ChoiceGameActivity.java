package com.tanoshi.nojiko.animequiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChoiceGameActivity extends AppCompatActivity {

    private Button easy_persoquiz_btn;
    private Button medium_persoquiz_btn;
    private Button hard_persoquiz_btn;

    private ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_game);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        this.easy_persoquiz_btn = (Button) findViewById(R.id.easy_persoquiz_btn);
        this.medium_persoquiz_btn = (Button) findViewById(R.id.medium_persoquiz_btn);
        this.hard_persoquiz_btn = (Button) findViewById(R.id.hard_persoquiz_btn);

        this.back_btn = (ImageButton) findViewById(R.id.back_btn);

        this.easy_persoquiz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EasyPersoQuizActivity.class);
                startActivity(intent);
            }
        });

        this.medium_persoquiz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MediumPersoQuizActivity.class);
                startActivity(intent);
            }
        });

        this.hard_persoquiz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
