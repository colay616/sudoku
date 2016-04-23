package cc.klsf.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuaileshifu on 2016/1/9.
 * Email:815856515@qq.com
 */
public class ListActivity extends Activity {
    protected DbManage db;
    private int level = 0;
    private List<Map<String,Object>> sudokuList = new ArrayList<>();
    protected int page = 0;
    private SimpleAdapter adapter;
    private PullToRefreshGridView pullToRefreshGridView;
    private boolean isNomore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudokulist);
        init();
    }

    private void init(){
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.sudoku_list);
        //获取难度等级
        level = getIntent().getExtras().getInt("level");
        //设置界面标题
        int levelID = getResources().getIdentifier("level_"+level, "string", getPackageName());
        ((TextView)findViewById(R.id.sudoku_level)).setText(getResources().getString(levelID));
        GridView gridView = pullToRefreshGridView.getRefreshableView();
        switch (level){
            case 4:
                gridView.setBackgroundColor(getResources().getColor(R.color.danger));
                break;
            case 3:
                gridView.setBackgroundColor(getResources().getColor(R.color.warning));
                break;
            case 2:
                gridView.setBackgroundColor(getResources().getColor(R.color.success));
                break;
            case 1:
                gridView.setBackgroundColor(getResources().getColor(R.color.info));
                break;
            default:
                gridView.setBackgroundColor(Color.GRAY);
        }

        //初始化adapter
        adapter = new SimpleAdapter(this,sudokuList,R.layout.item_sudoku,
                new String[]{"state","sudoku"},
                new int[]{R.id.sudoku_state,R.id.sudokuImage});
        //调整数独缩略图
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof SudokuImage) {
                    ((SudokuImage)view).setSudokuStr(data.toString());
                    return true;
                }else if(view instanceof TextView){
                    TextView tv = (TextView) view;
                    if(view.getId() == R.id.sudoku_state){
                        switch ((int) data) {
                            case 1:
                                tv.setText("未完成");
                                tv.setTextColor(Color.BLUE);
                                break;
                            case 0:
                                tv.setText("未开始");
                                tv.setTextColor(Color.GRAY);
                                break;
                            default:
                                tv.setText(changeSecond((int)data));
                                tv.setTextColor(Color.GREEN);
                                break;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        pullToRefreshGridView.setAdapter(adapter);
        //初始化数据库管理类
        db = new DbManage(this);
        setOnClickListener();
        loadSudoku();
    }
    private void loadSudoku(){
        int pageSzie = 15;
        int start = pageSzie*page;
        Cursor cursor = db.select("select id,sudoku,used,finish,lastplay from sudoku where level = ? order by id desc limit ?,?",new String[]{String.valueOf(level),String.valueOf(start),String.valueOf(pageSzie)});
        int add = 0;
        while (cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            int state = 0;
            if(cursor.getString(4) != null){
                int finish = cursor.getInt(3);
                if(finish > 0){
                    state = finish;
                }else{
                    state = 1;
                }
            }
            map.put("id",cursor.getInt(0));
            map.put("state",state);
            map.put("sudoku",cursor.getString(1));
            sudokuList.add(map);
            add++;
        }
        if(add < 15){
            isNomore = true;
        }else{
            page++;
        }
        db.close();
        adapter.notifyDataSetChanged();
    }
    private void refreshList(){
        Cursor cursor;
        for (Map<String,Object> map : sudokuList){
            int id = (int)map.get("id");
            cursor = db.select("select id,sudoku,used,finish,lastplay from sudoku where id = ? limit 1",new String[]{String.valueOf(id)});
            if (cursor.moveToNext()){
                int state = 0;
                if(cursor.getString(4) != null){
                    int finish = cursor.getInt(3);
                    if(finish > 0){
                        state = finish;
                    }else{
                        state = 1;
                    }
                }
                map.put("id",cursor.getInt(0));
                map.put("state",state);
                map.put("sudoku",cursor.getString(1));
            }
            cursor.close();
        }
        db.close();
        adapter.notifyDataSetChanged();
    }
    private String changeSecond(int t){
        int m = t/60;
        int s = t%60;
        return m + ":" + s;
    }

    private void setOnClickListener() {
        //添加返回按钮监听
        findViewById(R.id.btn_back).setOnClickListener(new BtnOnClickListener());
        findViewById(R.id.btn_refresh).setOnClickListener(new BtnOnClickListener());

        //ListView监听
        pullToRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, GameActivity.class);
                Map<String, Object> item = sudokuList.get(position);
                int sudokuId = (int)item.get("id");
                intent.putExtra("id", sudokuId);
                startActivity(intent);
            }
        });
        //上拉下拉设置
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.BOTH);
        // 设置PullRefreshListView上提加载时的加载提示
        pullToRefreshGridView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多...");
        pullToRefreshGridView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        pullToRefreshGridView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开加载更多...");
        // 设置PullRefreshListView下拉加载时的加载提示
        pullToRefreshGridView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        pullToRefreshGridView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        pullToRefreshGridView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开刷新...");
        pullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                refreshList();
                pullToRefreshGridView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                if(isNomore){
                    Toast.makeText(ListActivity.this,"没有更多的数独了",Toast.LENGTH_SHORT).show();
                }else {
                    loadSudoku();
                }
                pullToRefreshGridView.onRefreshComplete();
            }
        });
    }
    //按钮监听
    private class BtnOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.btn_refresh:
                    refreshList();
                    break;
            }

        }
    }


}
