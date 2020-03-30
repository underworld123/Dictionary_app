package com.example.dictionary_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="dictionary.db";
    public static final String TB_NAME="word_table";
    public static final String COL_1="ID";
    public static final String COL_2="Word";
    public static final String COL_3="Meaning";
    private final Context helperContext;

    public DatabaseHelper(@Nullable Context context) {
        //whenever DatabaseHelper object is created the database is created.
        super(context, DB_NAME ,null ,1);
        helperContext = context;
//        insertData("word","meanging");
//        try{
////            File file=new File("diction.txt"); //creating a new file instance
////            FileReader fr=new FileReader(file); //reads the file
//            InputStream in =
//                    getClass().getResourceAsStream("diction.txt");
//            Reader fr = new InputStreamReader(in, "utf-8");
//            BufferedReader br=new BufferedReader(fr);//creates a buffering chars input stream
//            String line;
//            while((line=br.readLine())!=null){
//                String x=line;
//                System.out.println(x);
//                String[] ar = x.split(":");
//                insertData(ar[0],ar[1]);
//            }
//            fr.close();
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        }
    }

    SQLiteDatabase db;
    private void loadDictionary() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadWords();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadWords() throws IOException {
        final Resources resources = helperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.diction);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("db",line);
                String[] strings = TextUtils.split(line, ":");
                if (strings.length < 2) continue;
                long id = addWord(strings[0].trim(), strings[1].trim());
                if (id < 0) {
                    Log.e("db", "unable to add word: " + strings[0].trim());
                }
            }
        } finally {
            reader.close();
        }
    }

    public long addWord(String word, String definition) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_2, word);
        initialValues.put(COL_3, definition);

        return db.insert(TB_NAME, null, initialValues);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL("create table "+TB_NAME+" (ID integer primary key autoincrement , Word text ,Meaning text)");
        loadDictionary();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists "+TB_NAME);
        onCreate(db);
    }

    public Cursor getdata(String s){
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("Select * from "+TB_NAME,null);
        Cursor res = db.rawQuery("Select * from "+TB_NAME+" where Word = ?",new String[]{s});
        return  res;
    }
    public Cursor getsuggestions(){
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("Select * from "+TB_NAME+" where Word LIKE  ?",new String[]{"%"+s+"%"});
        Cursor res = db.rawQuery("Select * from "+TB_NAME,null);
        return  res;
    }
}
