package cc.klsf.sudoku;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kuaileshifu on 2016/1/9.
 * Email:815856515@qq.com
 */
class DbManage {
    protected final int db_version = 3;
    protected Context context;
    protected final String db_name = "sudoku";
    public DBOpenHelper dbHelper;
    private Cursor cursor;
    private SQLiteDatabase db;

    public DbManage(Context context) {
        this.context = context;
        dbHelper = new DBOpenHelper(context,db_name,db_version);
    }

    public Cursor select(String sql){
        return select(sql,null);
    }
    public Cursor select(String sql,String[] args){
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(sql,args);
        return cursor;
    }

    public SQLiteDatabase  getWritableDb(){
        return dbHelper.getWritableDatabase();
    }

    public void close(){
        if(cursor != null){
            cursor.close();
        }
        if(db != null){
            db.close();
        }
    }

}
