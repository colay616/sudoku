package cc.klsf.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kuaileshifu on 2016/1/7.
 * Email:815856515@qq.com
 */
public class SudokuView extends View {
    private String sudokuStr = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private Sudoku sudoku;
    private int viewHeight;
    private int sudokuLength;//数独边长
    private float tileLength;//数独每个小格边长
    private float keyWeight;//数字键盘单键宽度
    private float keyHeight;//数字键盘单键高度
    private int selectedTile = -1;//数独中被选中的格子,-1没有被选中的
    private int selectedKey = -1;//数字键盘被按下的格子
    private boolean isFinish = false;

    private Paint backgroundPaint;
    private Paint darkPaint;
    private Paint hilitePaint;
    private Paint lightPaint;
    private Paint numberPaint;
    private Paint editPaint;
    private Paint keyPaint;
    private Paint bgPaint;
    private Paint grayPaint;

    private OnFinishListener onFinishListener;//游戏完成监听

    public SudokuView(Context context) {
        super(context);
        init();
    }
    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.sudokuLength = w;

        //获取数独每个格子的宽度和高度
        this.tileLength = sudokuLength/9f;
        this.keyWeight = sudokuLength/5f;
        this.keyHeight = (viewHeight-sudokuLength)/2f;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(){
        //初始化数独内容
        sudoku = new Sudoku(sudokuStr);

        //初始化数独背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.shudu_background));

        //初始化数独格子所需画笔
        darkPaint = new Paint();
        darkPaint.setColor(getResources().getColor(R.color.shudu_dark));
        hilitePaint = new Paint();
        hilitePaint.setColor(getResources().getColor(R.color.shudu_hilite));
        lightPaint = new Paint();
        lightPaint.setColor(getResources().getColor(R.color.shudu_light));

        //初始化数字画笔
        numberPaint =new Paint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setStyle(Paint.Style.STROKE);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        //初始化键盘数字画笔
        keyPaint = new Paint();
        keyPaint.setStyle(Paint.Style.STROKE);
        keyPaint.setTextAlign(Paint.Align.CENTER);
        //初始化填写数字画笔
        editPaint =new Paint();
        editPaint.setColor(getResources().getColor(R.color.shudu_edit_num));
        editPaint.setStyle(Paint.Style.STROKE);
        editPaint.setTextAlign(Paint.Align.CENTER);

        //初始化键盘画笔
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.shudu_keyboard_bg));

        //初始化一只灰色画笔
        grayPaint = new Paint();
        grayPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //检查重复数字
        sudoku.refresh();

        //画出数独背景
        canvas.drawRect(0,0,sudokuLength,sudokuLength,backgroundPaint);

        //画出数独格子
        for (int i=0;i<9;i++){
            //画出横线
            canvas.drawLine(0,i*tileLength,sudokuLength,i*tileLength,lightPaint);
            canvas.drawLine(0,i*tileLength+1,sudokuLength,i*tileLength+1,hilitePaint);
            //画出竖线
            canvas.drawLine(i*tileLength,0,i*tileLength,sudokuLength,lightPaint);
            canvas.drawLine(i*tileLength+1,0,i*tileLength+1,sudokuLength,hilitePaint);

        }
        //画出9个大格子
        for (int i=3;i<9;i=i+3){
            canvas.drawLine(0,i*tileLength,sudokuLength,i*tileLength,darkPaint);
            canvas.drawLine(0,i*tileLength+1,sudokuLength,i*tileLength+1,hilitePaint);
            canvas.drawLine(i*tileLength,0,i*tileLength,sudokuLength,darkPaint);
            canvas.drawLine(i*tileLength+1,0,i*tileLength+1,sudokuLength,hilitePaint);
        }

        //绘制选中的格子背景
        changeTileBg(canvas);
        //绘制重复数字背景
        changeRepetBg(canvas);

        //设置文字水平垂直居中
        numberPaint.setTextSize(tileLength * 0.75f);
        editPaint.setTextSize(tileLength * 0.75f);
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float x = tileLength/2;
        float y = tileLength/2 - (fontMetrics.ascent + fontMetrics.descent)/2f;
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                //绘制数字
                if(sudoku.isInitialTile(i,j)) {
                    canvas.drawText(sudoku.getTileString(i, j), i * tileLength + x, j * tileLength + y, numberPaint);
                }else{
                    canvas.drawText(sudoku.getTileString(i, j), i * tileLength + x, j * tileLength + y, editPaint);
                }
            }
        }


        //绘制数字键盘
        canvas.drawRect(0,sudokuLength,sudokuLength,sudokuLength+keyHeight*2,bgPaint);
        canvas.drawLine(0,sudokuLength+keyHeight,sudokuLength,sudokuLength+keyHeight,hilitePaint);
        for (int i=0;i<5;i++){
            canvas.drawLine(i*keyWeight,sudokuLength,i*keyWeight,sudokuLength+keyHeight*2,hilitePaint);
        }
        //绘制选中的数字键盘背景
        changeKeyBg(canvas);
        //检测某个数字是否完成
        for(int i=0;i<9;i++){
            if(sudoku.finishStatus[i] > 8){
                if(selectedKey == i){
                    //如果选中的是此键则取消！
                    selectedKey = -1;
                }
                int ix = i%5;
                int iy = i/5;
                canvas.drawRect(keyWeight*ix,sudokuLength+keyHeight*iy,keyWeight*ix+keyWeight,sudokuLength+keyHeight*iy+keyHeight,grayPaint);
            }
        }
        //绘制数字键盘数字
        keyPaint.setTextSize(keyHeight*0.75f);
        fontMetrics = keyPaint.getFontMetrics();
        x = keyWeight/2;
        y = keyHeight/2 - (fontMetrics.ascent + fontMetrics.descent)/2;
        String keyNumber;
        for (int i=0;i<5;i++){
            for (int j=0;j<2;j++){
                int num = j*5 + i + 1;
                if(num != 10) {
                    keyPaint.setColor(getResources().getColor(R.color.shudu_keyboard_num));
                    keyNumber = String.valueOf(num);
                }else{
                    keyNumber = "X";
                    keyPaint.setColor(getResources().getColor(R.color.shudu_keyboard_clear));
                }
                canvas.drawText(String.valueOf(keyNumber),i*keyWeight+x,sudokuLength+j*keyHeight+y,keyPaint);
            }
        }


        //检测是否完成
        checkFinish();
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getSubbox(x, y)) {
                    //判断是否已经完成。已完成则不在重绘制
                    if(selectedKey > 0  && isFinish){
                        onFinishListener.onClick();
                        return true;
                    }
                    fillNumber();
                    this.invalidate();//重绘
                } else if (getKeybox(x, y)) {
                    //填数字
                    //fillNumber();
                    this.invalidate();
                }
                break;
            /*
            case MotionEvent.ACTION_UP:
                if (selectedKey >= 0){
                    selectedKey = -1;
                    this.invalidate();
                }
                break;
            */
        }
        return true;
    }
    public void setSudokuStr(String str){
        this.sudokuStr = str;
        reload();
    }
    public void setSudokuStr(String str,String userdo){
        this.sudokuStr = str;
        sudoku = null;
        sudoku = new Sudoku(sudokuStr,userdo);
        invalidate();
    }

    private Boolean getSubbox(float x,float y){
        int selectedX = (int)(x/tileLength);
        int selectedY = (int)(y/tileLength);
        if(selectedX<9 && selectedY<9){
            selectedTile = selectedY*9 + selectedX;
            return true;
        }else{
            return false;
        }
    }
    private Boolean getKeybox(float x,float y){
        if(y <= sudokuLength || y >= sudokuLength + keyWeight*2){
            return false;
        }
        int selectedX = (int)(x/keyWeight);
        int selectedY = (int)((y-sudokuLength)/keyHeight);
        selectedKey = selectedY*5 + selectedX;
        return true;
    }

    private void changeTileBg(Canvas canvas) {
        if(selectedTile >= 0){
            int i = selectedTile%9;
            int j = selectedTile/9;
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.shudu_tile_selected));
            canvas.drawRect(tileLength*i,tileLength*j,tileLength*i+tileLength,tileLength*j+tileLength,paint);
        }
    }

    private void changeKeyBg(Canvas canvas) {
        if(selectedKey >= 0){
            int i = selectedKey%5;
            int j = selectedKey/5;
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.shudu_keyboard_selected));
            canvas.drawRect(keyWeight*i,sudokuLength+keyHeight*j,keyWeight*i+keyWeight,sudokuLength+keyHeight*j+keyHeight,paint);
        }
    }

    private void changeRepetBg(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        for (Integer integer : sudoku.repetTile){
            if (!sudoku.isInitialTile(integer)) {
                int x = integer%9;
                int y = integer/9;
                canvas.drawRect(tileLength*x,tileLength*y,tileLength*x+tileLength,tileLength*y+tileLength,paint);
            }
        }

    }

    private void fillNumber(){
        if(selectedTile >= 0 && selectedTile<=80 && selectedKey >= 0) {
            if (!sudoku.isInitialTile(selectedTile)) {
                if (selectedKey == 9){
                    sudoku.userSudoku[selectedTile] = 0;
                    selectedKey = -1;
                }else{
                    sudoku.userSudoku[selectedTile] = selectedKey+1;
                }
            }
        }
    }

    /**
     * 重载数独view
     */
    public void reload(){
        isFinish = false;
        sudoku = null;
        sudoku = new Sudoku(sudokuStr);
        invalidate();
    }

    public String getUserSudoku(){
        return sudoku.getUserSudoku();
    }

    private void checkFinish(){
        if(sudoku.checkFinish()){
            if(onFinishListener != null){
                isFinish = true;
                onFinishListener.onFinish();
            }
        }
    }
    public void setOnFinishListener(OnFinishListener onFinishListener){
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener{
        void onFinish();
        void onClick();
    }

}
