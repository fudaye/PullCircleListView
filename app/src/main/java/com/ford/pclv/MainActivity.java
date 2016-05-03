package com.ford.pclv;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ford.pullcirclelibrary.DensityUtility;
import com.ford.pullcirclelibrary.PullToRefreshCircleView;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PullToRefreshCircleView mListView;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (PullToRefreshCircleView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(getData());
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new RefreshListener());
        mListView.setPullLoadEnable(false);
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Blurry.with(MainActivity.this)
                        .radius(20)
                        .sampling(2)
                        .async()
                        .capture(mListView.getHeadView().findViewById(R.id.blurred_image))
                        .into((ImageView) mListView.getHeadView().findViewById(R.id.blurred_image));
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setting1) {
            mListView.setPullLoadEnable(true);
            return true;
        }else if(id == R.id.setting2){
            mListView.setPullLoadEnable(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RefreshListener implements PullToRefreshCircleView.OnRefreshListener {

        @SuppressLint("HandlerLeak")
        @Override
        public void onRefresh() {
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.stopRefresh();
                    ArrayList<String> data = new ArrayList<>();
                    data.add("pull refresh item");
                    mAdapter.setData(data);
                }
            }, 3000);
        }

        @Override
        public void onLoadMore() {
            Log.d(TAG,"onLoadMore");
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.stopLoadMore();
                    ArrayList<String> data = new ArrayList<>();
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    data.add("load more item");
                    mAdapter.addData(data);
                }
            }, 3000);
        }
    }


    class MyAdapter extends BaseAdapter {

        private ArrayList<String> mData;

        public MyAdapter(ArrayList<String> data) {
            this.mData = data;
        }


        public void setData(ArrayList<String> data) {
            mData.addAll(0,data);
            notifyDataSetChanged();
        }

        public void addData(ArrayList<String> data){
            mData.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtility.dip2px(MainActivity.this, 30)));
            textView.setText(mData.get(position));
            return textView;
        }
    }

    public ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add("item"+i);
        }
        return data;
    }
}
