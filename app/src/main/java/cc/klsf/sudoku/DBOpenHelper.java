package cc.klsf.sudoku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by kuaileshifu on 2016/1/8.
 * Email:815856515@qq.com
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private Context context;
    public DBOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeAssetsSQL(db,"sudoku.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private void executeAssetsSQL(SQLiteDatabase db, String fileName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(fileName)));
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
            Log.e("DbError","IOException",e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("DbError","IOException",e);
            }
        }
    }
}
