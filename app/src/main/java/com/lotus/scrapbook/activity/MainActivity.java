package com.lotus.scrapbook.activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lotus.scrapbook.R;
import com.lotus.scrapbook.module.SQLiteControl;

public class MainActivity extends AppCompatActivity {
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setView();
        SQLiteControl.loadScrap(MainActivity.this);
    }
    private void setView() {
        addBtn=findViewById(R.id.add_btn);
        addBtn.setOnClickListener(v -> SQLiteControl.addScrap(MainActivity.this));
    }

}