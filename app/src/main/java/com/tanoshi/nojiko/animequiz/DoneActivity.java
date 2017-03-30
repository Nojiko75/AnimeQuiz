package com.tanoshi.nojiko.animequiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;

public class DoneActivity extends AppCompatActivity {

    private TextView nb_perso;
    private TextView score_txtView;
    private Button retry_btn;
    private Button back_home_btn;
    private String level;

    public static final String PREFS_NAME = "SaveGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        DbHelper db = new DbHelper(this);

        nb_perso = (TextView) findViewById(R.id.nb_perso);
        score_txtView = (TextView) findViewById(R.id.score);
        retry_btn = (Button) findViewById(R.id.retry_btn);
        back_home_btn = (Button) findViewById(R.id.back_home_btn);

        //Get data from GameActivity
        Bundle bundle = getIntent().getExtras();
        Log.i("LEVEL 0", bundle.getString("LEVEL"));
        if(bundle != null) {
            int score = bundle.getInt("SCORE");
            int nb_persoFounded = bundle.getInt("NB_PERSO");
            int perso_total = bundle.getInt("NB_PERSO_TOTAL");
            level = bundle.getString("LEVEL");
            Log.i("LEVEL 1", bundle.getString("LEVEL"));
            Log.i("LEVEL 2", level);
            score_txtView.setText(score + "/" + perso_total*3);
            nb_perso.setText(nb_persoFounded + "/" + perso_total);
            db.insertScore(score);
        }

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LEVEL 3", level);
                Log.i("DoneActivity", "RETRY");
                if(level.equals("EasyPersoQuizActivity")) {
                    startActivity(new Intent(getApplicationContext(), EasyPersoQuizActivity.class));
                } else if(level.equals("MediumPersoQuizActivity")) {
                    startActivity(new Intent(getApplicationContext(), MediumPersoQuizActivity.class));
                } else if(level.equals("GameActivity")) {
                    startActivity(new Intent(getApplicationContext(), GameActivity.class));
                }
                restartGame();
                finish();
            }
        });

        back_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DoneActivity", "BACK");
                restartGame();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void restartGame() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("SCORE");
        editor.remove("PROGRESS");
        editor.remove("NB_PERSO");
        editor.commit();
        preferences.getInt("SCORE", 0);
    }
}
