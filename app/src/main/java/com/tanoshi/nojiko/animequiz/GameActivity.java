package com.tanoshi.nojiko.animequiz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.PersoQuestion;
import com.tanoshi.nojiko.animequiz.model.Ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ImageView perso_img;
    private LinearLayout lastname_layout;
    private LinearLayout firstname_layout;
    private LinearLayout first_random_letters;
    private LinearLayout second_random_letters;

    private List<PersoQuestion> persoQuestionList = new ArrayList<>();
    private List<Ranking> rankingList = new ArrayList<>();
    private DbHelper db;
    private int index = 0;
    private int totalPerso;
    private String lastname = null;
    private String firstname = null;
    private String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String randomLetters = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        perso_img = (ImageView) findViewById(R.id.perso_img);
        lastname_layout = (LinearLayout) findViewById(R.id.lastname_layout);
        firstname_layout = (LinearLayout) findViewById(R.id.firstname_layout);
        first_random_letters = (LinearLayout) findViewById(R.id.first_random_letters);
        second_random_letters = (LinearLayout) findViewById(R.id.second_random_letters);

        db = new DbHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        persoQuestionList = db.getAllPersoQuestion();
        totalPerso = persoQuestionList.size();

        showGame(index);
    }

    private void showGame(int index) {
        if(index < totalPerso) {
            PersoQuestion persoQuestion = persoQuestionList.get(index);
            int imageId = this.getResources().getIdentifier(persoQuestion.getImage().toLowerCase(), "drawable", getPackageName());
            perso_img.setBackgroundResource(imageId);

            //lastname = persoQuestion.getLastName();
            //firstname = persoQuestion.getFirstName();

            setAnswerCaseLetters();
        }

        lastname = "misaki";
        firstname = "ayusawa";

        Log.i("LASTNAME", lastname);
        Log.i("FIRSTNAME", firstname);
        setAnswerCaseLetters();
    }

    //show cases to write answer
    private void setAnswerCaseLetters() {
        Log.i("LASTNAME 1", lastname);
        Log.i("FIRSTNAME 2", firstname);
        if(lastname != null && firstname != null) {
            int nbLettersLN = lastname.length();
            int nbLettersFN = firstname.length();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0); //left, top, right, bottom
            Log.i("SIZE", nbLettersLN+"");
            for(int i=0; i<nbLettersLN; i++) {
                Button myButton = new Button(this);
                String s = String.valueOf(lastname.charAt(i));
                myButton.setText(s);
                myButton.setTextSize(20);
                myButton.setTextColor(Color.parseColor("#ffffff"));
                myButton.setBackgroundResource(R.drawable.answer_letters);
                myButton.setLayoutParams(params);
                lastname_layout.addView(myButton);
            }
            if(nbLettersFN > 0) {
                for(int i=0; i<nbLettersFN; i++) {
                    Button myButton = new Button(this);
                    String s = String.valueOf(firstname.charAt(i));
                    myButton.setText(s);
                    myButton.setTextSize(20);
                    myButton.setTextColor(Color.parseColor("#ffffff"));
                    myButton.setBackgroundResource(R.drawable.answer_letters);
                    myButton.setLayoutParams(params);
                    firstname_layout.addView(myButton);
                }
            }

            //set random letters to write perso name
            setRandomLetters();
            Log.e("RANDOM LETTERS", randomLetters);
            Log.e("RANDOM LETTERS LENGTH", randomLetters.length()+"");

            int nbFirstRangeLetters = first_random_letters.getChildCount();
            Button v = null;
            for(int i=0; i < nbFirstRangeLetters; i++) {
                v = (Button) first_random_letters.getChildAt(i);
                v.setText(String.valueOf(randomLetters.charAt(i)));
            }
            int nbSecondRangeLetters = second_random_letters.getChildCount();
            for(int i=0; i < nbSecondRangeLetters; i++) {
                v = (Button) second_random_letters.getChildAt(i);
                v.setText(String.valueOf(randomLetters.charAt(i+9)));
            }
        }
    }

    private void setRandomLetters() {
        int length = 20 - (lastname.length() + firstname.length());
        Log.e("RANDOM LENGTH", length+"");
        String randomStr = lastname + firstname;
        for(int i=0; i < length; i++) {
            randomStr += selectAChar(alpha);
        }

        Log.e("RANDOM STR", randomStr);
        Log.e("RANDOM LENGTH", randomStr.length()+"");

        randomLetters = shuffle(randomStr);
    }

    //select random letters in alphabet
    public static char selectAChar(String s){
        Random random = new Random();
        int index = random.nextInt(s.length());
        return s.charAt(index);
    }

    //mix letters in randomStr to obtain randomLetters string
    private static String shuffle(String sentence) {
        String[] words = sentence.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            List<Character> letters = new ArrayList<Character>();
            for (char letter : word.toCharArray()) {
                letters.add(letter);
            }
            if (letters.size() > 2) {
                Collections.shuffle(letters.subList(1, letters.size() - 1));
            }
            for (char letter : letters) {
                builder.append(letter);
            }
            builder.append(" ");
        }
        return builder.toString();
    }

}
