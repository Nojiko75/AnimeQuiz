package com.tanoshi.nojiko.animequiz;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EasyPersoQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_perso_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
