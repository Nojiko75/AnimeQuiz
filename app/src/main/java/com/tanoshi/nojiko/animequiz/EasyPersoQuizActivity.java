package com.tanoshi.nojiko.animequiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.EasyPersoQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EasyPersoQuizActivity extends AppCompatActivity {

    private ImageButton back_btn;
    private TextView perso_name;
    private TextView cpt_index;
    private TextView nb_onigiri;
    private Button clueBonus_btn;
    private Button nextBonus_btn;
    private TextView score_txtview;
    private ImageView perso_answerA;
    private ImageView perso_answerB;
    private ImageView perso_answerC;
    private ImageView perso_answerD;

    private List<EasyPersoQuestion> easyPersoQuestionList = new ArrayList<>();
    private DbHelper db;
    private int index = 0;
    private int totalPerso;
    private int onigiri = 10;
    private int score = 0;
    private boolean bonusUsed = false;
    private int nb_persoFounded = 0;

    private String goodAnswer = "";

    List<ImageView> answers_img = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_perso_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = new DbHelper(this);

        this.back_btn = (ImageButton) findViewById(R.id.back_btn);
        this.score_txtview = (TextView) findViewById(R.id.score);
        this.cpt_index = (TextView) findViewById(R.id.index);
        this.nb_onigiri = (TextView) findViewById(R.id.nb_onigiri);
        this.perso_name = (TextView) findViewById(R.id.perso_name);

        this.clueBonus_btn = (Button) findViewById(R.id.clueBonus_btn);
        this.nextBonus_btn = (Button) findViewById(R.id.nextBonus_btn);

        this.perso_answerA = (ImageView) findViewById(R.id.perso_answerA);
        this.perso_answerB = (ImageView) findViewById(R.id.perso_answerB);
        this.perso_answerC = (ImageView) findViewById(R.id.perso_answerC);
        this.perso_answerD = (ImageView) findViewById(R.id.perso_answerD);

        this.back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.i("LEVEL: ", this.getClass().getSimpleName());
        answers_img.add(perso_answerA);
        answers_img.add(perso_answerB);
        answers_img.add(perso_answerC);
        answers_img.add(perso_answerD);

        this.easyPersoQuestionList = db.getAllEasyPersoQuestion();
        totalPerso = easyPersoQuestionList.size();

        showGame(index);
        cpt_index.setText((index + 1) + "/" + totalPerso);
        nb_onigiri.setText(onigiri+"");
        score_txtview.setText(score+"");
        checkAnswer();
    }

    private void showGame(int index) {
        if(index < totalPerso) {
            EasyPersoQuestion easyPersoQuestion = easyPersoQuestionList.get(index);
            goodAnswer = easyPersoQuestion.getImage();
            this.perso_name.setText(easyPersoQuestion.getGoodAnswer());
            String[] answers = {easyPersoQuestion.getImage(),
                                easyPersoQuestion.getAnswerB(),
                                easyPersoQuestion.getAnswerC(),
                                easyPersoQuestion.getAnswerD()};

            shuffleAnswerArray(answers);
            for(int i=0; i< answers.length; i++) {
                int id = this.getResources().getIdentifier(answers[i].toLowerCase(), "drawable", getPackageName());
                answers_img.get(i).setImageResource(id);
                answers_img.get(i).setTag(answers[i]);
            }
        }
    }

    public void checkAnswer() {
        for(int i=0; i < answers_img.size(); i++) {
            final ImageView current_img = answers_img.get(i);
            current_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int answer_color = 0;
                    if(goodAnswer.equals(current_img.getTag())) {
                        answer_color = R.drawable.good_answer;
                        onigiri +=5;
                        nb_onigiri.setText(onigiri+"");
                        score += 3;
                        score_txtview.setText(score+"");
                        nb_persoFounded++;
                    } else {
                        answer_color = R.drawable.wrong_answer;
                    }
                    final RelativeLayout layout = (RelativeLayout) current_img.getParent();
                    layout.setBackgroundResource(answer_color);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(index+1 < totalPerso) {
                                showGame(++index);
                                cpt_index.setText((index+1) + "/"+ totalPerso);
                                layout.setBackgroundResource(R.drawable.img_bkg);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), DoneActivity.class);
                                String level = this.getClass().getSimpleName();
                                intent.putExtra("SCORE", score);
                                intent.putExtra("NB_PERSO", nb_persoFounded);
                                intent.putExtra("NB_PERSO_TOTAL", totalPerso);
                                intent.putExtra("LEVEL", "EasyPersoQuizActivity");
                                Log.i("LEVEL intent", level);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, 500);
                }
            });

        }
    }

    // Implementing Fisher–Yates shuffle
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
