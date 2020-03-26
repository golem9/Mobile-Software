package com.example.layout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn_linearlayout;
    Button btn_constrainlayout;
    Button btn_tablelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_linearlayout = (Button)findViewById(R.id.linearlayout);
        btn_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_linear_layout);
            }
        });
        btn_constrainlayout = (Button) findViewById(R.id.constrainlayout);
        btn_constrainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_constrain_layout);
            }
        });
        btn_tablelayout = (Button) findViewById(R.id.tablelayout);
        btn_tablelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_table_layout);
            }
        });
    }
}