package com.jay.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.widget.EasyRecyclerView;
import com.jay.widget.EasyViewHolder;
import com.jay.widget.SimpleRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class EasyRecyclerViewActivity extends AppCompatActivity implements EasyRecyclerView.OnItemClickListener, EasyRecyclerView.OnItemLongClickListener, SimpleRecyclerView.OnLoadingListener {

    private EasyRecyclerView mEasyReView;
    private List<String> mListData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easy_recycler_view_activity);

        mEasyReView = (EasyRecyclerView) findViewById(R.id.easyReView);
        mEasyReView.setLayoutManager(new LinearLayoutManager(this));

        initData();

        mEasyReView.setAdapter(new MyAdapter());

        mEasyReView.setOnItemClickListener(this);//设置条目点击事件
        mEasyReView.setOnItemLongClickListener(this);//设置条目长按事件

        mEasyReView.setOnLoadingListener(this);//设置加载事件
    }

    private void initData() {
        mListData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mListData.add("item " + i);
        }
    }

    @Override
    public void onItemClick(EasyRecyclerView rv, RecyclerView.Adapter adapter, View view, int position) {
        Toast.makeText(this,String.format("第%d个条目被点击了",position),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(EasyRecyclerView rv, RecyclerView.Adapter adapter, View view, int position) {
        Toast.makeText(this,String.format("第%d个条目被长按了",position),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoading() {
        Toast.makeText(this,"加载事件被触发了",Toast.LENGTH_SHORT).show();
        mEasyReView.loadingComplete();//加载完成了调用此方法通知
    }

    class MyAdapter extends RecyclerView.Adapter<EasyViewHolder>{

        @Override
        public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.easy_recycler_view_item,parent,false);
            return new EasyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EasyViewHolder holder, int position) {
            TextView text = holder.getView(R.id.text);
            text.setText(mListData.get(position));
        }

        @Override
        public int getItemCount() {
            return mListData.size();
        }
    }
}
