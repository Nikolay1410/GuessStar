package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonPlayGame = findViewById(R.id.buttonPlayGame);
        Context context = getApplicationContext();
        Button buttonExit = findViewById(R.id.buttonExit);
        
        if (NetworkManager.isNetworkAvailable(context)) {
            buttonPlayGame.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(),PlayGame.class);
                startActivity(intent);
            });
        } else {
            Intent intent = new Intent(this, MainNoInternet.class);
            startActivity(intent);
            finish();
        }

        buttonPlayGame.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),PlayGame.class);
            startActivity(intent);
        });
        buttonExit.setOnClickListener(view -> finish());
    }
    public static class NetworkManager {
        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}

