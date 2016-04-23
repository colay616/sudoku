package cc.klsf.sudoku;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kuaileshifu on 2016/1/7.
 * Email:815856515@qq.com
 */
public class GameActivity extends Activity {
    private Chronometer chronometer;
    private SudokuView sudokuView;
    protected int sudokId;
    private DbManage db;
    private boolean isFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        sudokuView = (SudokuView) findViewById(R.id.sudokuView);
        setOnClickListener();
        init();
    }
    private void init(){
        sudokId = getIntent().getExtras().getInt("id");
        db = new DbManage(this);
        Cursor cursor = db.select("select level,sudoku,used,userdo,finish from sudoku where id = ? limit 1",new String[]{String.valueOf(sudokId)});
        String sudokuStr = "";
        String userdo = "";
        int level = 1;
        int used = 0;
        int finish = 0;
        if (cursor.moveToNext()) {
            level = cursor.getInt(0);
            sudokuStr = cursor.getString(1);
            used = cursor.getInt(2);
            userdo = cursor.getString(3);
            finish = cursor.getInt(4);
            db.close();
        }else{
            Toast.makeText(this,"数独不存在",Toast.LENGTH_SHORT).show();
            finish();
        }
        //设置界面标题
        int levelID = getResources().getIdentifier("level_"+level, "string", getPackageName());
        ((TextView)findViewById(R.id.sudoku_level)).setText(getResources().getString(levelID));
        //初始化计时器
        chronometer = (Chronometer)findViewById(R.id.chronometer);

        //设置数独
        if(used > 0 && !userdo.equals("")){
            sudokuView.setSudokuStr(sudokuStr,userdo);
            chronometer.setBase(SystemClock.elapsedRealtime() - used*1000);
        }else{
            sudokuView.setSudokuStr(sudokuStr);
        }
        if(finish == 0) {
            chronometer.start();
            //记录play时间
            db.getWritableDb().execSQL("UPDATE `sudoku` SET `lastplay`=datetime('now','localtime') WHERE (`id`='"+sudokId+"')");
            db.close();
        }else{
            isFinish = true;
        }

    }

    private void setOnClickListener(){
        //添加重新游戏按钮监听
        findViewById(R.id.btn_restartGame).setOnClickListener(new BtnOnClickListener());
        //添加返回按钮监听
        findViewById(R.id.btn_back).setOnClickListener(new BtnOnClickListener());


        //游戏完成监听
        sudokuView.setOnFinishListener(new SudokuView.OnFinishListener() {
            @Override
            public void onFinish() {
                chronometer.stop();
                if(!isFinish) {
                    //记录完成时间和状态
                    db.getWritableDb().execSQL("UPDATE `sudoku` SET `used`='" + str2Second(chronometer.getText()) + "',userdo='" + sudokuView.getUserSudoku() + "',finish='" + str2Second(chronometer.getText()) + "',`lastplay`=datetime('now','localtime') WHERE (`id`='" + sudokId + "')");
                    db.close();

                    View view = LayoutInflater.from(GameActivity.this).inflate(R.layout.dialog_finish, null, false);
                    String usedTime = "用时:" + chronometer.getText();
                    ((TextView) view.findViewById(R.id.used_time)).setText(usedTime);
                    ViewDialog finishDialog = new ViewDialog(GameActivity.this, view);
                    finishDialog.show();
                    finishDialog.setOnClicklistener("btn_finish_back", new BtnOnClickListener());
                    finishDialog.setOnClicklistener("btn_finish_again", new BtnOnClickListener());
                }
            }

            @Override
            public void onClick() {
                new ViewDialog.Builder(GameActivity.this).setTitle("你已经完成此数独")
                        .setMessage("你已经成功填写完此数独，共用时："+chronometer.getText()+"\n\n想要再尝试一次？" )
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sudokuView.reload();
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private int str2Second(CharSequence str){
        String[] t = str.toString().split(":");
        int m = Integer.valueOf(t[0]);
        int s = Integer.valueOf(t[1]);
        return m*60 + s;
    }


    //按钮监听
    private class BtnOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.btn_restartGame:
                    restartGame();
                    break;
                case R.id.btn_finish_back:
                    finish();
                    break;
                case R.id.btn_finish_again:
                    Toast.makeText(GameActivity.this,"暂时没有更多的数独",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
    private void restartGame(){
        new ViewDialog.Builder(this).setTitle("确认要放弃重新开始？")
                .setMessage("重新开始会清楚你填写的所有内容，并重新计时。\n\n确定要重新开始吗？" )
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sudokuView.reload();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void finish() {
        if(!isFinish) {
            //记录上次用时和用户填写内容
            db.getWritableDb().execSQL("UPDATE `sudoku` SET `used`='" + str2Second(chronometer.getText()) + "',userdo='" + sudokuView.getUserSudoku() + "' WHERE (`id`='" + sudokId + "')");
            db.close();
        }
        super.finish();
    }
}
