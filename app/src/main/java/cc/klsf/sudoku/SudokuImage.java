package cc.klsf.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by kuaileshifu on 2016/1/9.
 * Email:815856515@qq.com
 */
public class SudokuImage extends ImageView {
    private int[] sudoku = new int[9*9];
    private float sudokuLength = 80f;
    private Paint paint;
    private float textX;
    private float textY;

    public SudokuImage(Context context) {
        super(context);
        init();
    }

    public SudokuImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.sudokuLength = (MeasureSpec.getSize(widthMeasureSpec))/94f * 10;
        paint.setTextSize(sudokuLength * 0.75f);
        //设置文字水平垂直居中
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        textX = sudokuLength/2f + sudokuLength/5f;
        textY = sudokuLength/2f - (fontMetrics.ascent + fontMetrics.descent)/2f + sudokuLength/5f;

        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                //绘制数字
                canvas.drawText(getTileString(i, j), i * sudokuLength + textX, j * sudokuLength + textY, paint);
            }
        }

    }

    public void setSudokuStr(String str){
        sudoku = null;
        sudoku = str2sudoku(str);
        invalidate();
    }

    private int[] str2sudoku(String str){
        int[] sudoku = new int[str.length()];
        for (int i=0;i<str.length();i++){
            sudoku[i] = str.charAt(i) - '0';
        }
        return sudoku;
    }
    private String getTileString(int x,int y){
        int v = sudoku[x + y*9];
        if(v==0){
            return "";
        }else
            return String.valueOf(v);
    }

}
