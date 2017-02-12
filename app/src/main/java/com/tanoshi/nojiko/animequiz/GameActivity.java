package com.tanoshi.nojiko.animequiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.tanoshi.nojiko.animequiz.dbHelper.DbHelper;
import com.tanoshi.nojiko.animequiz.model.PersoQuestion;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private TextView score_txtview;

    private List<PersoQuestion> persoQuestionList = new ArrayList<>();
    private DbHelper db;
    private int index = 0;
    private int totalPerso;
    private String lastname = null;
    private String firstname = null;
    private String anime = null;
    private String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String randomLetters = null;
    private List<Button> rdmLettersBtn = new ArrayList<>();
    //dictionnary key: index of lastname or firstname position - value: index of random letter position
    private HashMap<Integer, Integer> hashMapL = new HashMap<>();
    private HashMap<Integer, Integer> hashMapF = new HashMap<>();
    private List<Integer> indexTable = new ArrayList<>(); //get random letters button indexes to display them
    private int nbLettersWritten = 0;
    private int cpt = 0;
    private List<Button> answerLetters = new ArrayList<>();
    private List<Button> lastnameLetters = new ArrayList<>();
    private List<Button> firstnameLetters = new ArrayList<>();
    private List<Button> nameLetters = new ArrayList<>();
    private String answer = "";
    private int onigiri = 10;
    private int score = 0;
    private boolean bonusUsed = false;
    private int nb_persoFounded = 0;
    private HashMap<Integer, String> bonusLettersL = new HashMap<>();
    private HashMap<Integer, String> bonusLettersF = new HashMap<>();

    public static final String PREFS_NAME = "SaveGame";

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
        score_txtview = (TextView) findViewById(R.id.score);

        db = new DbHelper(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        restoreGame();

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

        Log.i("BONUS 1", bonusUsed+"");
        showGame(index);
        updateBonusBtn();
        if(onigiri < 0) {
            onigiri = 0;
        }
        Log.i("BONUS 2", bonusUsed+"");
        cpt_index.setText((index+1) + "/"+ totalPerso);
        nb_onigiri.setText(onigiri+"");
        score_txtview.setText(score+"");
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

    @Override
    protected void onStop() {
        super.onStop();
        saveGame();
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
                        //indexTable.add(nbLettersWritten, j);
                        hashMapL.put(writeAnswerLetter(lastnameLetters), j);
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
                        Log.i("BONUS 6", bonusUsed+"");
                        checkAnswer();
                        //} else if(nbLettersWritten <= lastname_layout.getChildCount() + firstname_layout.getChildCount()){
                    } else if(isEmpty(firstnameLetters)){
                        //indexTable.add(nbLettersWritten,j);
                        hashMapF.put(writeAnswerLetter(firstnameLetters), j);
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
                            checkAnswer();
                        }
                    }
                }
            });
        }
    }

    //remove answer letter and display random letter
    public void displayRandomLetters() {

        int nbLettersL = lastname_layout.getChildCount();
        Button v = null;

        for(int i = 0; i < lastnameLetters.size(); i++) {
            Button button = lastnameLetters.get(i);
            if(button != null) {
                final int j = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button current_btn = (Button) v;
                        if(!TextUtils.isEmpty(current_btn.getText())) {
                            int index = 0;
                            Iterator<Integer> iterator = hashMapL.keySet().iterator();
                            while(iterator.hasNext()) {
                                Integer key = iterator.next();
                                Integer value = hashMapL.get(key);
                                if(key == j) {
                                    index = value;
                                }
                            }
                            current_btn.setText("");
                            Button rdmButton = rdmLettersBtn.get(index);
                            rdmButton.setVisibility(View.VISIBLE);
                            rdmButton.setEnabled(true);
                            nbLettersWritten--;
                        }
                    }
                });
            }
        }

        for(int i = 0; i < firstnameLetters.size(); i++) {
            Button button = firstnameLetters.get(i);
            if(button != null) {
                final int j = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button current_btn = (Button) v;
                        if(!TextUtils.isEmpty(current_btn.getText())) {
                            int index = 0;
                            Iterator<Integer> iterator = hashMapF.keySet().iterator();
                            while(iterator.hasNext()) {
                                Integer key = iterator.next();
                                Integer value = hashMapF.get(key);
                                if(key == j) {
                                    index = value;
                                }
                            }
                            current_btn.setText("");
                            Button rdmButton = rdmLettersBtn.get(index);
                            rdmButton.setVisibility(View.VISIBLE);
                            rdmButton.setEnabled(true);
                            nbLettersWritten--;
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
        int i = 0;
        while (i < list.size() && !TextUtils.isEmpty(list.get(i).getText())) {
            i++;
        }
        Log.i("IS EMPTY", "i: " + i);
        return i < list.size();
    }

    public void checkAnswer() {
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
        if (playerAnswer.toLowerCase().equals(goodAnswer.toLowerCase())) {
            showSuccessDialog(true);
        } else if(playerAnswer.length() == goodAnswer.length()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Mauvaise réponse", Toast.LENGTH_SHORT);
            toast.show();
        }
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
        Log.i("BONUS 3", bonusUsed+"");
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
            cpt_index.setText((index+1) + "/"+ totalPerso);
        }

        perso_name.setText(lastname + " " + firstname);
        perso_manga.setText(anime);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseAnswerCase();
                dialog.dismiss();
                if(success) {
                    onigiri +=5;
                    nb_onigiri.setText(onigiri+"");
                    if(bonusUsed) {
                        score += 1;
                    } else {
                        score += 3;
                    }
                    Log.i("BONUS 4", bonusUsed+"");
                    nb_persoFounded++;
                    score_txtview.setText(score+"");
                }
                ++index;
                //index += 24;
                cpt_index.setText((index+1) + "/"+ totalPerso);
                bonusUsed = false;
                if(index < totalPerso) {
                    showGame(index);
                } else {
                    Intent intent = new Intent(getApplicationContext(), DoneActivity.class);
                    intent.putExtra("SCORE", score);
                    intent.putExtra("NB_PERSO", nb_persoFounded);
                    intent.putExtra("NB_PERSO_TOTAL", totalPerso);
                    startActivity(intent);
                    finish();
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
                bonusUsed = true;
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

    //write the clue letter where the first letter is empty into the text of last or firstname buttons
    public void writeClueLetter() {
        if(isEmpty(lastnameLetters)) {
            int index = writeAnswerLetter(lastnameLetters);
            Button current_answer_letter = (Button) lastname_layout.getChildAt(index);
            current_answer_letter.setText(lastname.charAt(index)+"");
            bonusLettersL.put(index, lastname.charAt(index)+"");
        } else {
            int index = writeAnswerLetter(firstnameLetters);
            Button current_answer_letter = (Button) firstname_layout.getChildAt(index);
            current_answer_letter.setText(firstname.charAt(index)+"");
            bonusLettersF.put(index, firstname.charAt(index)+"");
        }
        onigiri -=5;
        Log.i("BONUS 5", bonusUsed+"");
        nb_onigiri.setText(onigiri+"");
        checkAnswer();
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

    public void saveGame() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("SCORE", score);
        editor.putInt("PROGRESS", index);
        editor.putInt("NB_PERSO", nb_persoFounded);
        editor.putInt("ONIGIRI", onigiri);

        editor.commit();
    }

    public void restoreGame() {
        // Restore preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        score = preferences.getInt("SCORE", 0);
        index = preferences.getInt("PROGRESS", 0);
        nb_persoFounded = preferences.getInt("NB_PERSO", 0);
        onigiri = preferences.getInt("ONIGIRI", 10);
    }
}
