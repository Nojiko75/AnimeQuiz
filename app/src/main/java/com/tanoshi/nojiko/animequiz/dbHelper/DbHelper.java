package com.tanoshi.nojiko.animequiz.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tanoshi.nojiko.animequiz.model.PersoQuestion;
import com.tanoshi.nojiko.animequiz.model.Ranking;

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

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        //DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        Log.e("DBPATH", DB_PATH+DB_NAME);
        this.mContext = context;
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
