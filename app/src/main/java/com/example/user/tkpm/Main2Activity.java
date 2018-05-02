package com.example.user.tkpm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle extras = getIntent().getExtras();
        String value1 = extras.getString(Intent.EXTRA_TEXT);
        Log.d("sdadas",value1);

        Intent intent=new Intent(Main2Activity.this,MainActivity.class);
        intent.putExtra("url",value1);
        startActivity(intent);

    }
}
