package com.jay.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.widget.EasyViewHolder;
import com.jay.widget.GroupRecyclerView;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class GroupRecyclerViewActivity extends AppCompatActivity implements GroupRecyclerView.OnChildItemClickListener, GroupRecyclerView.OnChildItemLongClickListener, GroupRecyclerView.OnGroupItemClickListener, GroupRecyclerView.OnGroupItemLongClickListener {

    private GroupRecyclerView groupReView;
    private MyAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_recycler_view_activity);

        groupReView = (GroupRecyclerView) findViewById(R.id.groupReView);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 3);

        groupReView.setLayoutManager(linearLayoutManager);


        mAdapter = new MyAdapter();//这里的Adapter必须extends GroupRecyclerView.GroupAdapter
        groupReView.setAdapter(mAdapter);

        groupReView.setOnChildItemClickListener(this);
        groupReView.setOnChildItemLongClickListener(this);
        groupReView.setOnGroupItemClickListener(this);
        groupReView.setOnGroupItemLongClickListener(this);
    }

    @Override
    public void onChildItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition, int childPosition) {
        Toast.makeText(this,String.format("第 %d 个组条目的第 %d 子条目被点击了",groupPosition,childPosition),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildItemLongClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition, int childPosition) {
        Toast.makeText(this,String.format("第 %d 个组条目的第 %d 子条目被长按了",groupPosition,childPosition),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition) {
        Toast.makeText(this,String.format("第 %d 个组条目被点击了",groupPosition),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupItemLongClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition) {
        Toast.makeText(this,String.format("第 %d 个组条目被长按了",groupPosition),Toast.LENGTH_SHORT).show();
    }


    class MyAdapter extends GroupRecyclerView.GroupAdapter{

        @Override
        public EasyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View groupItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item,parent,false);
            return new EasyViewHolder(groupItemView);
        }

        @Override
        public EasyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View childItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item,parent,false);
            return new EasyViewHolder(childItemView);
        }

        @Override
        public void onBindGroupViewHolder(EasyViewHolder holder, int groupPosition) {
            TextView tvGroup = holder.getView(R.id.tvGroup);
            tvGroup.setText("Group"+groupPosition);
        }

        @Override
        public void onBindChildViewHolder(EasyViewHolder holder, int groupPosition, int childPosition) {
            TextView tvChild = holder.getView(R.id.tvChild);
            tvChild.setText("Child"+childPosition);
        }

        @Override
        public int getGroupCount() {
            return 5;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 14;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                groupReView.setLayoutManager(linearLayoutManager);
                return true;
            case R.id.menu2:
                groupReView.setLayoutManager(gridLayoutManager);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
