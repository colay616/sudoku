package cc.klsf.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends Activity {
    private ViewDialog levelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start).setOnClickListener(new BtnOnClickListener());
    }


    private void showLevelDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_level, null,false);
        levelDialog = new ViewDialog(this,view);
        levelDialog.show();
        for(int i =1;i<5;i++){
            levelDialog.setOnClicklistener("btn_level_"+i,new BtnOnClickListener());
        }
    }

    //按钮监听
    private class BtnOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start:
                    showLevelDialog();
                    break;
                case R.id.btn_level_1:
                    startSudoku(1);
                    break;
                case R.id.btn_level_2:
                    startSudoku(2);
                    break;
                case R.id.btn_level_3:
                    startSudoku(3);
                    break;
                case R.id.btn_level_4:
                    startSudoku(4);
                    break;
            }
        }
    }

    private void startSudoku(int level){
        levelDialog.dismiss();
        Intent intent = new Intent(this,ListActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }

}
