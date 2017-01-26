package com.tanoshi.nojiko.animequiz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private ImageButton back_btn;
    private TextView cpt_index;
    private TextView nb_onigiri;
    private Button clueBonus_btn;
    private Button nextBonus_btn;

    private List<PersoQuestion> persoQuestionList = new ArrayList<>();
    private List<Ranking> rankingList = new ArrayList<>();
    private DbHelper db;
    private int index = 0;
    private int totalPerso;
    private String lastname = null;
    private String firstname = null;
    private String anime = null;
    private String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String randomLetters = null;
    private List<Button> rdmLettersBtn = new ArrayList<>();
    //dictionnary
    private List<Integer> indexTable = new ArrayList<>(); //get random letters button indexes to display them
    private int nbLettersWritten = 0;
    private int cpt = 0;
    private List<Button> answerLetters = new ArrayList<>();
    private List<Button> lastnameLetters = new ArrayList<>();
    private List<Button> firstnameLetters = new ArrayList<>();
    private List<Button> nameLetters = new ArrayList<>();
    private String answer = "";
    private int onigiri = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        perso_img = (ImageView) findViewById(R.id.perso_img);
        lastname_layout = (LinearLayout) findViewById(R.id.lastname_layout);
        firstname_layout = (LinearLayout) findViewById(R.id.firstname_layout);
        first_random_letters = (LinearLayout) findViewById(R.id.first_random_letters);
        second_random_letters = (LinearLayout) findViewById(R.id.second_random_letters);
        back_btn = (ImageButton) findViewById(R.id.back_btn);
        cpt_index = (TextView) findViewById(R.id.index);
        nb_onigiri = (TextView) findViewById(R.id.nb_onigiri);
        clueBonus_btn = (Button) findViewById(R.id.clueBonus_btn);
        nextBonus_btn = (Button) findViewById(R.id.nextBonus_btn);

        db = new DbHelper(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeback();
            }
        });

        clueBonus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonusDialog(false);
                updateBonusBtn();
            }
        });

        nextBonus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonusDialog(true);
                updateBonusBtn();
            }
        });

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

        persoQuestionList = db.getAllPersoQuestion();
        totalPerso = persoQuestionList.size();

        showGame(index);
        cpt_index.setText((index+1) + "/"+ totalPerso);
        nb_onigiri.setText(onigiri+"");
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
    }

    private void showGame(int index) {
        if(index < totalPerso) {
            PersoQuestion persoQuestion = persoQuestionList.get(index);
            int imageId = this.getResources().getIdentifier(persoQuestion.getImage().toLowerCase(), "drawable", getPackageName());
            perso_img.setImageResource(imageId);

            lastname = persoQuestion.getLastName();
            lastname = lastname.replace(".", "");
            //firstname = "";
            firstname = persoQuestion.getFirstName();
            anime = persoQuestion.getAnime();

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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0); //left, top, right, bottom
            LinearLayout.LayoutParams params_space = new LinearLayout.LayoutParams(130, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            params_space.setMargins(4, 0, 134, 0); //left, top, right, bottom
            Log.i("SIZE", nbLettersLN+"");
            for(int i=0; i<nbLettersLN; i++) {
                if(!Character.toString(lastname.charAt(i)).matches(" ")) {
                    Button myButton = new Button(this);
                    //String s = String.valueOf(lastname.charAt(i));
                    //myButton.setText(s);
                    myButton.setText("");
                    myButton.setTextSize(20);
                    myButton.setTextColor(Color.parseColor("#ffffff"));

                    if(i < nbLettersLN-1 && Character.toString(lastname.charAt(i+1)).matches(" ")) {
                        //myButton.setBackgroundColor(Color.parseColor("#1F2A36"));
                        //myButton.setBackgroundResource(R.drawable.random_letters);
                        myButton.setLayoutParams(params_space);
                        Log.i("i", "je suis un i");
                        //myButton.setEnabled(false);

                    } else {
                        myButton.setLayoutParams(params);
                    }

                    myButton.setBackgroundResource(R.drawable.answer_letters);
                    lastname_layout.addView(myButton);
                    lastnameLetters.add(myButton);
                    nameLetters.add(myButton);
                }

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
                    nameLetters.add(myButton);
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
        String clean_lastname = lastname.replace(" ", "");
        int length = 20 - (clean_lastname.length() + firstname.length());
        Log.e("RANDOM LENGTH", length+"");
        String randomStr = clean_lastname + firstname;
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
                    //if empty letter is in lastname
                    //if(nbLettersWritten < lastname_layout.getChildCount()) {
                    if(isEmpty(lastnameLetters)) {
                        indexTable.add(nbLettersWritten, j);
                        Log.e("INDEX TABLE", j + "");
                        //Button current_answer_letter = (Button) lastname_layout.getChildAt(nbLettersWritten);
                        Button current_answer_letter = (Button) lastname_layout.getChildAt(writeAnswerLetter(lastnameLetters));
                        Log.e("WRITE ANSWER LETTER", writeAnswerLetter(lastnameLetters) + "");
                        Log.e("IS EMPTY", isEmpty(lastnameLetters) + "");
                        current_answer_letter.setText(current_btn.getText());
                        answer += current_btn.getText();
                        current_btn.setVisibility(View.INVISIBLE);
                        current_btn.setEnabled(false);
                        nbLettersWritten++;
                        Log.e("BUTTON LETTER 1", (String) current_btn.getText());
                        Log.e("NB LETTER 1", nbLettersWritten + "");
                        Log.e("ANSWER", answer);
                        Log.e("NB EMPTY", writeAnswerLetter(lastnameLetters) + "");
                        if (checkAnswer()) {
                            Log.i("CHECK ANSWER", "GOOD");
                            showSuccessDialog(true);
                        }
                        //} else if(nbLettersWritten <= lastname_layout.getChildCount() + firstname_layout.getChildCount()){
                    }else {
                        indexTable.add(nbLettersWritten,j);
                        Log.e("INDEX TABLE", j+"");
                        //Button current_answer_letter = (Button) firstname_layout.getChildAt(cpt);
                        Button current_answer_letter = (Button) firstname_layout.getChildAt(writeAnswerLetter(firstnameLetters));
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
                                showSuccessDialog(true);
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

    public boolean isEmpty(List<Button> list) {
        boolean b = true;
        /*for(int i = 0; i < lastnameLetters.size(); i++) {
            b = !TextUtils.isEmpty(lastnameLetters.get(i).getText());
            Log.i("IS EMPTY 1", b+"");
            return b;
        }

        return b;*/
        int i = 0;
        while (i < list.size() && !TextUtils.isEmpty(list.get(i).getText())) {
            i++;
        }

        return i < list.size();
    }

    public boolean checkAnswer() {
        //answer written by the player
        String goodAnswer = lastname+firstname;
        goodAnswer = goodAnswer.replace(" ", "");
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

    public void showSuccessDialog(final boolean success) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = (TextView) dialog.findViewById(R.id.title) ;
        TextView result_st = (TextView) dialog.findViewById(R.id.result_st);
        ImageView result_img = (ImageView) dialog.findViewById(R.id.result_img);
        TextView perso_name = (TextView) dialog.findViewById(R.id.perso_name);
        TextView perso_manga = (TextView) dialog.findViewById(R.id.perso_manga);
        Button next_btn = (Button) dialog.findViewById(R.id.next_btn);

        if(!success) {
            title.setText("Dommage...");
            result_st.setText("Le personnage était:");
            int imageResource = getResources().getIdentifier("disappointed", "drawable", getPackageName());
            result_img.setImageResource(imageResource);
            onigiri-=10;
            nb_onigiri.setText(onigiri+"");
        }

        perso_name.setText(lastname + " " + firstname);
        perso_manga.setText(anime);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseAnswerCase();
                dialog.dismiss();
                showGame(++index);
                cpt_index.setText((index+1) + "/"+ totalPerso);
                if(success) {
                    onigiri +=5;
                    nb_onigiri.setText(onigiri+"");
                }
                updateBonusBtn();
            }
        });

        dialog.show();
    }

    public void comeback() {
        finish();
    }

    public void showBonusDialog(final boolean isNextBonus) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.bonus_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView question = (TextView) dialog.findViewById(R.id.question);
        TextView bonus_cost = (TextView) dialog.findViewById(R.id.bonus_cost);
        Button ok_btn = (Button) dialog.findViewById(R.id.ok_btn);
        Button close_btn = (Button) dialog.findViewById(R.id.close_btn);

        if(isNextBonus) {
            title.setText("Passer");
            question.setText("Passer au personnage suivant ?");
            bonus_cost.setText("10");
        }

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNextBonus) {
                    showSuccessDialog(false);
                    dialog.dismiss();
                } else {
                    writeClueLetter();
                    dialog.dismiss();
                }
                updateBonusBtn();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void writeClueLetter() {
        if(isEmpty(lastnameLetters)) {
            int index = writeAnswerLetter(lastnameLetters);
            Button current_answer_letter = (Button) lastname_layout.getChildAt(index);
            current_answer_letter.setText(lastname.charAt(index)+"");
        } else {
            int index = writeAnswerLetter(firstnameLetters);
            Button current_answer_letter = (Button) firstname_layout.getChildAt(index);
            current_answer_letter.setText(firstname.charAt(index)+"");
        }
        onigiri -=5;
        nb_onigiri.setText(onigiri+"");
        if(checkAnswer()) {
            Log.i("CHECK ANSWER", "GOOD");
            showSuccessDialog(true);
        }
    }

    public void updateBonusBtn() {
        if(onigiri < 5) {
            clueBonus_btn.setBackgroundResource(R.drawable.enabled_btn);
            clueBonus_btn.setPadding(40, 0, 0, 0);
            clueBonus_btn.setEnabled(false);
        } else {
            clueBonus_btn.setBackgroundResource(R.drawable.game_btn);
            clueBonus_btn.setPadding(40, 0, 0, 0);
            clueBonus_btn.setEnabled(true);
        }

        if(onigiri < 10) {
            nextBonus_btn.setBackgroundResource(R.drawable.enabled_btn);
            nextBonus_btn.setPadding(0, 0, 40, 0);
            nextBonus_btn.setEnabled(false);
        } else {
            nextBonus_btn.setBackgroundResource(R.drawable.game_btn);
            nextBonus_btn.setPadding(0, 0, 40, 0);
            nextBonus_btn.setEnabled(true);
        }
    }

}
