package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game);
        Button buttonGoTitle = findViewById(R.id.buttonGoTitle);
        TextView textViewResult = findViewById(R.id.textViewResult);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("result")){
            int result = intent.getIntExtra("result", 0);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int max = preferences.getInt("max", 0);
            String score = String.format("Ваш результат: %s\nМаксимальный результат: %s", result, max);
            textViewResult.setText(score);
        }
        buttonGoTitle.setOnClickListener(view -> {
            Intent intentExit = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentExit);
            finishAndRemoveTask();
        });
    }

    public void onClickRestart(View view) {
        Intent intentRestart = new Intent(getApplicationContext(), PlayGame.class);
        startActivity(intentRestart);
        finishAndRemoveTask();
    }
}