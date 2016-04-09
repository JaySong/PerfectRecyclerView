package com.jay.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.widget.EasyViewHolder;
import com.jay.widget.GroupRecyclerView;

public class MainActivity extends AppCompatActivity implements GroupRecyclerView.OnGroupItemClickListener, GroupRecyclerView.OnChildItemClickListener {

    private MyAdapter mAdapter;
    private GroupRecyclerView grv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grv = (GroupRecyclerView) findViewById(R.id.grv);

        grv.setLayoutManager(new GridLayoutManager(this, 3));

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter = new MyAdapter();
                grv.setAdapter(mAdapter);
            }
        });


        grv.setOnGroupItemClickListener(this);
        grv.setOnChildItemClickListener(this);
    }

    @Override
    public void onGroupItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition) {
        Toast.makeText(this,"GroupItemClick || Group = " +groupPosition +" click",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChildItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition, int childPosition) {
        Toast.makeText(this,"ChildItemClick || Group = " +groupPosition +" || Child = "+childPosition,Toast.LENGTH_LONG).show();
    }


    class MyAdapter extends GroupRecyclerView.GroupAdapter {

        @Override
        public EasyViewHolder onCreateEasyViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
            return new EasyViewHolder(itemView);
        }

        @Override
        public EasyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item, parent, false);
            return new EasyViewHolder(itemView);
        }

        @Override
        public void onBindEasyViewHolder(EasyViewHolder holder, int position) {
            TextView view = holder.getView(R.id.tvGroup);
            view.setText("Group Item " + position);
        }

        @Override
        public void onBindChildViewHolder(EasyViewHolder holder, int groupPosition, int childPosition) {
            TextView view = holder.getView(R.id.tvChild);
            view.setText("Group = "+groupPosition+"Child = " + childPosition);
        }

        @Override
        public int getGroupCount() {
            return 10;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 10;
        }
    }
}
