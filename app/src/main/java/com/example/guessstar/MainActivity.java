package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonPlayGame = findViewById(R.id.buttonPlayGame);

        buttonPlayGame.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),PlayGame.class);
            startActivity(intent);
        });
    }
}

