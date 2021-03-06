package com.tanoshi.nojiko.animequiz.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tanoshi.nojiko.animequiz.model.EasyPersoQuestion;
import com.tanoshi.nojiko.animequiz.model.MediumPersoQuestion;
import com.tanoshi.nojiko.animequiz.model.PersoQuestion;
import com.tanoshi.nojiko.animequiz.model.Ranking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nojiko on 14/01/2017.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "AnimeQuiz.db";
    public static String DB_PATH = "/data/data/com.tanoshi.nojiko.animequiz/databases/";
    private SQLiteDatabase mDataBase;
    private Context mContext;
    private static final int DATABASE_VERSION = 2;
    private static final String SP_KEY_DB_VER = "db_ver";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        //DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        Log.e("DBPATH", DB_PATH+DB_NAME);
        //openDataBase();
        this.mContext = context;
        initialize();
    }

    public void openDataBase() {
        String myPath = mContext.getDatabasePath(DB_NAME).getPath(); //DB_PATH+DB_NAME;
        if(mDataBase != null && mDataBase.isOpen()) return;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void copyDataBase() throws IOException {
        try {
            InputStream myInput = mContext.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH+DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase tempDB = null;
        try {
            String myPath = mContext.getDatabasePath(DB_NAME).getPath(); //DB_PATH+DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if(tempDB != null) {
            tempDB.close();
        }
        return  tempDB != null ? true : false;
    }

    public void createDataBase() throws IOException {
        boolean isDBExists = checkDataBase();
        if(!isDBExists) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes database. Creates database if doesn't exist.
     */
    private void initialize() {
        if (databaseExists()) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            if (DATABASE_VERSION != dbVersion) {
                File dbFile = mContext.getDatabasePath(DB_NAME);
                if (!dbFile.delete()) {
                    Log.w("DB", "Unable to update database");
                }
            }
        }
        if (!databaseExists()) {
            try {
                createDataBase();
            }catch (IOException e) {
                Log.i("DB", "error: " + e);
            }
        }
    }

    /**
     * Returns true if database file exists, false otherwise.
     * @return
     */
    private boolean databaseExists() {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //CRUD For Table
    public List<PersoQuestion> getAllPersoQuestion() {
        List<PersoQuestion> listPersoQuestion = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            //c = db.rawQuery("SELECT * FROM PersoQuestion ORDER BY Random()", null);
            c = db.rawQuery("SELECT * FROM PersoQuestion", null);
            if(c == null) return null;
            c.moveToFirst();

            do{
                int id = c.getInt(c.getColumnIndex("Id"));
                String image = c.getString(c.getColumnIndex("Image"));
                String lastName = c.getString(c.getColumnIndex("Lastname"));
                String firstName = c.getString(c.getColumnIndex("Firstname"));
                String anime = c.getString(c.getColumnIndex("Anime"));
                String type = c.getString(c.getColumnIndex("Type"));
                String themes = c.getString(c.getColumnIndex("Themes"));
                String kind = c.getString(c.getColumnIndex("Kind"));

                PersoQuestion persoQuestion = new PersoQuestion(id, image, lastName, firstName, anime, type, themes, kind);
                listPersoQuestion.add(persoQuestion);
            }while(c.moveToNext());
            c.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        db.close();
        return listPersoQuestion;
    }

    public List<EasyPersoQuestion> getAllEasyPersoQuestion() {
        List<EasyPersoQuestion> listEasyPersoQuestion = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM EasyPersoQuestion", null);
            if(c == null) return null;
            c.moveToFirst();

            do{
                int id = c.getInt(c.getColumnIndex("Id"));
                String goodAnswer = c.getString(c.getColumnIndex("GoodAnswer"));
                String image = c.getString(c.getColumnIndex("Image"));
                String answerA = c.getString(c.getColumnIndex("AnswerA"));
                String answerB = c.getString(c.getColumnIndex("AnswerB"));
                String answerC = c.getString(c.getColumnIndex("AnswerC"));

                EasyPersoQuestion easyPersoQuestion = new EasyPersoQuestion(id, goodAnswer, image, answerA, answerB, answerC);
                listEasyPersoQuestion.add(easyPersoQuestion);
            }while(c.moveToNext());
            c.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        db.close();
        return listEasyPersoQuestion;
    }

    public List<MediumPersoQuestion> getAllMediumPersoQuestion() {
        List<MediumPersoQuestion> listMediumPersoQuestion = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM MediumPersoQuestion", null);
            if(c == null) return null;
            c.moveToFirst();

            do{
                int id = c.getInt(c.getColumnIndex("Id"));
                String goodAnswer = c.getString(c.getColumnIndex("GoodAnswer"));
                String image = c.getString(c.getColumnIndex("Image"));
                String answerA = c.getString(c.getColumnIndex("AnswerA"));
                String answerB = c.getString(c.getColumnIndex("AnswerB"));
                String answerC = c.getString(c.getColumnIndex("AnswerC"));

                MediumPersoQuestion mediumPersoQuestion = new MediumPersoQuestion(id, goodAnswer, image, answerA, answerB, answerC);
                listMediumPersoQuestion.add(mediumPersoQuestion);
            }while(c.moveToNext());
            c.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        db.close();
        return listMediumPersoQuestion;
    }

    //Insert Score to Ranking Table
    public void insertScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Score", score);
        db.insert("Ranking", null, contentValues);
    }

    //Get score and sort ranking
    public List<Ranking> getRanking() {
        List<Ranking> listRanking = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM Ranking ORDER BY Score DESC;", null);
            if(c == null) return null;
            c.moveToNext();

            do{
                int id = c.getInt(c.getColumnIndex("Id"));
                int score = c.getInt(c.getColumnIndex("Score"));

                Ranking ranking = new Ranking(id, score);
                listRanking.add(ranking);
            }while(c.moveToNext());
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return listRanking;
    }

}
