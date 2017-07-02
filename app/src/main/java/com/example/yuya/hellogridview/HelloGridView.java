package com.example.yuya.hellogridview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

public class HelloGridView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_grid_view);
        setTitle("News App");

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //GridView gridview = (GridView) parent;
                //String item = position;
                //String item = "aaa";

                Intent intent = new Intent(HelloGridView.this,MainActivity.class);
                //intent.putExtra("SELECTED_URL",item.get("url"));
                String url = Integer.toString(position);
                intent.putExtra("SELECTED_URL",url);
                startActivity(intent);
            }
        });
    }
}
