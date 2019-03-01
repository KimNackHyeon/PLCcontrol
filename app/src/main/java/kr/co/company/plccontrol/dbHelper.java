package kr.co.company.plccontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {
    private static final  String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 2;

    public dbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE value (_id INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, THI float, Dry float, Wet float, Rel float);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS value");
        onCreate(db);
    }
}
