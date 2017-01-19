package com.tanoshi.nojiko.animequiz;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.PersoQuestion;
import com.tanoshi.nojiko.animequiz.model.Ranking;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    private List<Button> rdmLettersBtn = new ArrayList<>();
    private List<Integer> indexTable = new ArrayList<>(); //get random letters button indexes to display them
    private int nbLettersWritten = 0;
    private int cpt = 0;
    private List<Button> answerLetters = new ArrayList<>();
    private List<Button> lastnameLetters = new ArrayList<>();
    private List<Button> firstnameLetters = new ArrayList<>();
    private String answer = "";

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

        /*File database = getApplicationContext().getDatabasePath(DbHelper.DB_NAME);
        if(false == database.exists()) {
            db.getReadableDatabase();
            if(copyDataBase(this)) {
                Log.i("DATABASE", "SUCCESS");
            } else {
                Log.i("DATABASE", "FAILED");
                return;
            }
        } else {
            Log.i("DATABASE", "NOT EXIST");
        }*/

        /*persoQuestionList = db.getAllPersoQuestion();
        totalPerso = persoQuestionList.size();

        showGame(index);*/

    }

    public boolean copyDataBase(Context context) {
        try {
            InputStream myInput = context.getAssets().open(DbHelper.DB_NAME);
            String outputFileName = DbHelper.DB_PATH+DbHelper.DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
            perso_img.setImageResource(imageId);

            lastname = persoQuestion.getLastName();
            //firstname = "";
            firstname = persoQuestion.getFirstName();

            setAnswerCaseLetters();
            Log.i("LASTNAME", lastname);
            Log.i("FIRSTNAME", firstname);
            setAnswer();
            displayRandomLetters();
        }

        /*lastname = "misaki";
        firstname = "";
        Log.i("LASTNAME", lastname);
        Log.i("FIRSTNAME", firstname);
        setAnswerCaseLetters();
        setAnswer();
        displayRandomLetters();*/
    }

    //show cases to write answer
    private void setAnswerCaseLetters() {
        //Log.i("LASTNAME 1", lastname);
        //Log.i("FIRSTNAME 2", firstname);
        if(lastname != null && firstname != null) {
            int nbLettersLN = lastname.length();
            int nbLettersFN = firstname.length();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0); //left, top, right, bottom
            Log.i("SIZE", nbLettersLN+"");
            for(int i=0; i<nbLettersLN; i++) {
                Button myButton = new Button(this);
                //String s = String.valueOf(lastname.charAt(i));
                //myButton.setText(s);
                myButton.setText("");
                myButton.setTextSize(20);
                myButton.setTextColor(Color.parseColor("#ffffff"));
                myButton.setBackgroundResource(R.drawable.answer_letters);
                myButton.setLayoutParams(params);
                lastname_layout.addView(myButton);
                lastnameLetters.add(myButton);
            }
            if(nbLettersFN > 0) {
                for(int i=0; i<nbLettersFN; i++) {
                    Button myButton = new Button(this);
                    //String s = String.valueOf(firstname.charAt(i));
                    //myButton.setText(s);
                    myButton.setText("");
                    myButton.setTextSize(20);
                    myButton.setTextColor(Color.parseColor("#ffffff"));
                    myButton.setBackgroundResource(R.drawable.answer_letters);
                    myButton.setLayoutParams(params);
                    firstname_layout.addView(myButton);
                    firstnameLetters.add(myButton);
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
                rdmLettersBtn.add(v);
            }
            int nbSecondRangeLetters = second_random_letters.getChildCount();
            for(int i=0; i < nbSecondRangeLetters; i++) {
                v = (Button) second_random_letters.getChildAt(i);
                v.setText(String.valueOf(randomLetters.charAt(i+9)));
                rdmLettersBtn.add(v);
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

    private void setAnswer() {
        for(int i=0; i < rdmLettersBtn.size(); i++) {
            final Button current_btn = rdmLettersBtn.get(i);
            final int j = i;
            current_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nbLettersWritten < lastname_layout.getChildCount()) {
                        indexTable.add(nbLettersWritten,j);
                        Log.e("INDEX TABLE", j+"");
                        //Button current_answer_letter = (Button) lastname_layout.getChildAt(nbLettersWritten);
                        Button current_answer_letter = (Button) lastname_layout.getChildAt(writeAnswerLetter(lastnameLetters));
                        //writeAnswerLetter(lastnameLetters);
                        Log.e("WRITE ANSWER LETTER", writeAnswerLetter(lastnameLetters)+"");
                        Log.e("IS EMPTY", isEmpty()+"");
                        current_answer_letter.setText(current_btn.getText());
                        answer += current_btn.getText();
                        current_btn.setVisibility(View.INVISIBLE);
                        current_btn.setEnabled(false);
                        nbLettersWritten++;
                        Log.e("BUTTON LETTER 1", (String) current_btn.getText());
                        Log.e("NB LETTER 1", nbLettersWritten+"");
                        Log.e("ANSWER", answer);
                        Log.e("NB EMPTY", writeAnswerLetter(lastnameLetters)+"");
                        if(checkAnswer()) {
                            Log.i("CHECK ANSWER", "GOOD");
                            eraseAnswerCase();
                            showGame(++index);
                        }
                    } else if(nbLettersWritten <= lastname_layout.getChildCount() + firstname_layout.getChildCount()){
                        indexTable.add(nbLettersWritten,j);
                        Log.e("INDEX TABLE", j+"");
                        //Button current_answer_letter = (Button) firstname_layout.getChildAt(cpt);
                        Button current_answer_letter = (Button) firstname_layout.getChildAt(writeAnswerLetter(firstnameLetters));
                        //writeAnswerLetter(firstnameLetters);
                        if(current_answer_letter != null) {
                            Log.e("NB EMPTY", writeAnswerLetter(firstnameLetters)+"");
                            Log.e("WRITE ANSWER LETTER", writeAnswerLetter(firstnameLetters)+"");
                            current_answer_letter.setText(current_btn.getText());
                            answer += current_btn.getText();
                            current_btn.setVisibility(View.INVISIBLE);
                            current_btn.setEnabled(false);
                            nbLettersWritten++;
                            cpt++;
                            Log.e("BUTTON LETTER 2", (String) current_btn.getText());
                            Log.e("NB LETTER 2", nbLettersWritten+"");
                            Log.e("ANSWER", answer);
                            if(checkAnswer()) {
                                Log.i("CHECK ANSWER", "GOOD");
                                eraseAnswerCase();
                                showGame(++index);
                            }
                        }
                    }
                }
            });
        }
    }

    //remove answer letter and display random letter
    public void displayRandomLetters() {

        int nbLetters = lastname_layout.getChildCount();
        Button v = null;
        for(int i=0; i < nbLetters; i++) {
            v = (Button) lastname_layout.getChildAt(i);
            answerLetters.add(v);
        }
        int nb = firstname_layout.getChildCount();
        for(int j=0; j < nb; j++) {
            v = (Button) firstname_layout.getChildAt(j);
            answerLetters.add(v);
        }

        for(int k=0; k < answerLetters.size(); k++) {
            Button current_btn = answerLetters.get(k);

            final int l = k;
            if(current_btn != null) {
                current_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(indexTable.size() > 0) {
                            if(!TextUtils.isEmpty(answerLetters.get(l).getText())) {
                                if (indexTable.get(l) != null) {
                                    Button current_rdm_btn = rdmLettersBtn.get(indexTable.get(l));
                                    current_rdm_btn.setVisibility(View.VISIBLE);
                                    current_rdm_btn.setEnabled(true);

                                    Button current_answer_btn = (Button) v;
                                    current_answer_btn.setText("");
                                    nbLettersWritten--;
                                    indexTable.remove(l);
                                    //indexTable.add(l, null);
                                    writeAnswerLetter(lastnameLetters);
                                    Log.e("NB EMPTY", writeAnswerLetter(lastnameLetters) + "");
                                }
                            }
                        }
                    }
                });
            }

        }
    }

    //return index where letter must to be written into answer
    private int writeAnswerLetter(List<Button> list){
        int i = 0;
        Button answerLetter = null;
        if(i < list.size()) {
            answerLetter = list.get(i);
        }

        if(answerLetter != null) {
            while(i < list.size()-1 && !TextUtils.isEmpty(answerLetter.getText())) {
                i++;
                answerLetter = list.get(i);
            }
        }

        return i;
    }

    public boolean isEmpty() {
        boolean b = true;
        for(int i = 0; i < lastnameLetters.size(); i++) {
            b = !TextUtils.isEmpty(lastnameLetters.get(i).getText());
            return b;
            //Log.e("IS EMPTY", b+"");
        }

        return b;
    }

    public boolean checkAnswer() {
        //answer written by the player
        String goodAnswer = lastname+firstname;
        Log.i("GOOD ANSWER", goodAnswer);
        String playerAnswer = "";
        for(int i=0; i < lastname_layout.getChildCount(); i++) {
            Button answer_btn = (Button) lastname_layout.getChildAt(i);
            playerAnswer += answer_btn.getText();
        }

        for(int i=0; i < firstname_layout.getChildCount(); i++) {
            Button answer_btn = (Button) firstname_layout.getChildAt(i);
            playerAnswer += answer_btn.getText();
        }
        Log.i("PLAYER ANSWER", playerAnswer);
        return (playerAnswer.toLowerCase().equals(goodAnswer.toLowerCase()));
    }

    public void eraseAnswerCase() {
        lastname_layout.removeAllViews();
        firstname_layout.removeAllViews();

        Log.i("LASTNAME LAYOUT", lastname_layout.getChildCount()+"");
        Log.i("FIRSTNAME LAYOUT", firstname_layout.getChildCount()+"");

        removeList(lastnameLetters);
        removeList(firstnameLetters);
        removeList(answerLetters);

        Log.i("LASTNAME LETTERS ", lastnameLetters.size()+"");
        Log.i("FIRSTNAME LETTERS ", firstnameLetters.size()+"");
        Log.i("ANSWER LETTERS", answerLetters.size()+"");
        //removeList(indexTable);

        Iterator<Integer> iterator = indexTable.iterator();
        while(iterator.hasNext()) {
            Integer integer = iterator.next();
            iterator.remove();
        }

        Log.i("INDEX TABLE", indexTable.size()+"");

        for(int i=0; i < rdmLettersBtn.size(); i++) {
            final Button current_btn = rdmLettersBtn.get(i);
            if(current_btn.getVisibility() == View.INVISIBLE) {
                current_btn.setVisibility(View.VISIBLE);
                current_btn.setEnabled(true);
            }

        }

        removeList(rdmLettersBtn);
        Log.i("RANDOM LETTERS", rdmLettersBtn.size()+"");
        nbLettersWritten = 0;
        answer = "";

        Log.i("NB LETTERS", nbLettersWritten+"");
    }

    public void removeList(List list) {
        Iterator<Button> iterator = list.iterator();
        while(iterator.hasNext()) {
            Button button = iterator.next();
            iterator.remove();
        }
    }

}
