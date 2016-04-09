package com.jay.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnEasy).setOnClickListener(this);
        findViewById(R.id.btnGroup).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEasy:
                startActivity(new Intent(this,EasyRecyclerViewActivity.class));
                break;
            case R.id.btnGroup:
                startActivity(new Intent(this,GroupRecyclerViewActivity.class));
                break;
        }
    }
}
