package com.tanoshi.nojiko.animequiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.MediumPersoQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediumPersoQuizActivity extends AppCompatActivity {

    private ImageButton back_btn;
    private TextView cpt_index;
    private TextView nb_onigiri;
    private Button clueBonus_btn;
    private Button nextBonus_btn;
    private TextView score_txtview;
    private ImageView perso_img;
    private String goodAnswer = "";
    private Button answerA;
    private Button answerB;
    private Button answerC;
    private Button answerD;

    private List<MediumPersoQuestion> mediumPersoQuestionList = new ArrayList<>();
    private DbHelper db;
    private int index = 0;
    private int totalPerso;
    private int onigiri = 10;
    private int score = 0;
    private boolean bonusUsed = false;
    private int nb_persoFounded = 0;

    List<Button> answers_btn = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium_perso_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = new DbHelper(this);

        this.back_btn = (ImageButton) findViewById(R.id.back_btn);
        this.score_txtview = (TextView) findViewById(R.id.score);
        this.cpt_index = (TextView) findViewById(R.id.index);
        this.nb_onigiri = (TextView) findViewById(R.id.nb_onigiri);

        this.clueBonus_btn = (Button) findViewById(R.id.clueBonus_btn);
        this.nextBonus_btn = (Button) findViewById(R.id.nextBonus_btn);

        this.perso_img = (ImageView) findViewById(R.id.perso_img);

        this.answerA = (Button) findViewById(R.id.answerA);
        this.answerB = (Button) findViewById(R.id.answerB);
        this.answerC = (Button) findViewById(R.id.answerC);
        this.answerD = (Button) findViewById(R.id.answerD);

        this.back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.answers_btn.add(this.answerA);
        this.answers_btn.add(this.answerB);
        this.answers_btn.add(this.answerC);
        this.answers_btn.add(this.answerD);

        this.mediumPersoQuestionList = db.getAllMediumPersoQuestion();
        totalPerso = mediumPersoQuestionList.size();

        showGame(index);
        cpt_index.setText((index + 1) + "/" + totalPerso);
        nb_onigiri.setText(onigiri+"");
        score_txtview.setText(score+"");
        checkAnswer();
    }

    private void showGame(int index) {
        if(index < totalPerso) {
            MediumPersoQuestion mediumPersoQuestion = mediumPersoQuestionList.get(index);
            int imageId = this.getResources().getIdentifier(mediumPersoQuestion.getImage().toLowerCase(), "drawable", getPackageName());
            perso_img.setImageResource(imageId);
            this.goodAnswer = mediumPersoQuestion.getGoodAnswer();
            String[] answers = {this.goodAnswer,
                    mediumPersoQuestion.getAnswerA(),
                    mediumPersoQuestion.getAnswerB(),
                    mediumPersoQuestion.getAnswerC()};

            shuffleAnswerArray(answers);
            for(int i=0; i<this.answers_btn.size(); i++) {
                this.answers_btn.get(i).setText(answers[i]);
            }
        }
    }

    public void checkAnswer() {
        for(int i=0; i < this.answers_btn.size(); i++) {
            final Button current_btn = answers_btn.get(i);
            current_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int answer_color = 0;
                    if(goodAnswer.equals(current_btn.getText())) {
                        answer_color = R.drawable.qcm_good_answer_btn;
                        onigiri +=5;
                        nb_onigiri.setText(onigiri+"");
                        score += 3;
                        score_txtview.setText(score+"");
                        nb_persoFounded++;
                    } else {
                        answer_color = R.drawable.qcm_wrong_answer_btn;
                    }
                    current_btn.setBackgroundResource(answer_color);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(index+1 < totalPerso) {
                                showGame(++index);
                                cpt_index.setText((index+1) + "/"+ totalPerso);
                                current_btn.setBackgroundResource(R.drawable.qcm_btn);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), DoneActivity.class);
                                intent.putExtra("SCORE", score);
                                intent.putExtra("NB_PERSO", nb_persoFounded);
                                intent.putExtra("NB_PERSO_TOTAL", totalPerso);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, 500);
                }
            });

        }
    }

    static void shuffleAnswerArray(String[] ar)
    {
        Random rnd = new Random();
        //Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
