package cc.klsf.sudoku;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kuaiLeshifu on 2016/1/7.
 * Email:815856515@qq.com
 */
public class Sudoku {
    private int[] sudoku = new int[9*9];
    public int[] userSudoku = new int[9*9];
    public Set<Integer> repetTile = new HashSet<>();
    public int[] finishStatus = new int[9];

    public Sudoku(String str){
        sudoku = str2sudoku(str);
        userSudoku = sudoku.clone();
    }
    public Sudoku(String str,String userdo){
        sudoku = str2sudoku(str);
        userSudoku = str2sudoku(userdo);
    }


    /**
     * 将字符串数独转换为int数组
     * @param str 数独字符串
     * @return int[]
     */
    public int[] str2sudoku(String str){
        int[] sudoku = new int[str.length()];
        for (int i=0;i<str.length();i++){
            sudoku[i] = str.charAt(i) - '0';
        }
        return sudoku;
    }

    /**
     * 获取数独中某个格的数字
     * @param x  格子横向格数
     * @param y 格子纵向格数
     * @return int
     */
    public int getTile(int x,int y){
        return userSudoku[x + y*9];
    }

    /**
     * 获取数独中某个格的数字
     * @param x  格子横向格数
     * @param y  格子纵向格数
     * @return String
     */
    public String getTileString(int x,int y){
        int v = getTile(x,y);
        if(v==0){
            return "";
        }else
            return String.valueOf(v);
    }

    /**
     * 判断此格是不是数独初始格子
     * @param x  格子横向格数
     * @param y  格子纵向格数
     * @return boolean
     */
    public boolean isInitialTile(int x,int y){
        return (sudoku[x + y*9]!=0);
    }
    /**
     * 判断此格是不是数独初始格子
     * @param num  格子编号
     * @return boolean
     */
    public boolean isInitialTile(int num){
        return (sudoku[num] != 0);
    }

    public void refresh(){
        checkRepet();
        checkStatus();
    }

    private void checkStatus(){
        finishStatus = new int[9];
        for (int x=0;x<9;x++){
            for (int y=0;y<9;y++){
                int i = getTile(x,y);
                if(i > 0){
                    --i;
                    finishStatus[i]++;
                }
            }
        }
    }
    private void checkRepet(){
        repetTile.clear();
        for (int i=0;i<9;i++){
            checkX(i);
            checkY(i);
            checkBox(i);
        }
    }

    private void checkX(int x){
        int[] intX = new int[9];
        int[] intTile = new int[9];
        for (int i=0;i<9;i++){
            intX[i] = getTile(x,i);
            intTile[i] = x+i*9;
        }
        addRepet(intX,intTile);
    }
    private void checkY(int y){
        int[] intY = new int[9];
        int[] intTile = new int[9];
        for (int i=0;i<9;i++){
            intY[i] = getTile(i,y);
            intTile[i] = i+y*9;
        }
        addRepet(intY,intTile);
    }
    private void checkBox(int num){
        int x = ((num%3))*3;
        int y =((num/3))*3;
        int[] intBox = new int[9];
        int[] intTile = new int[9];
        int index = 0;
        for (int i=x;i<x+3;i++){
            for (int j=y;j<y+3;j++){
                intBox[index] = getTile(i,j);
                intTile[index] = i+j*9;
                index++;
            }
        }
        addRepet(intBox,intTile);
    }

    private void addRepet(int[] num,int[] tile){
        for(int i=0;i<9;i++){
            if(num[i] == 0){
                continue;
            }
            for (int j=i+1;j<9;j++){
                if(num[j] == 0){
                    continue;
                }
                if(num[j] == num[i]){
                    repetTile.add(tile[i]);
                    repetTile.add(tile[j]);
                }
            }
        }
    }

    public boolean checkFinish(){
        if(repetTile.size() > 0){
            return  false;
        }else {
            for(int i:userSudoku){
                if(i == 0){
                    return false;
                }
            }
            return true;
        }
    }

    public String getUserSudoku(){
        String str = "";
        for(int x:userSudoku){
            str += x;
        }
        return str;
    }



}
