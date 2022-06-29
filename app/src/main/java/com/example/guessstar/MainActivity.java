package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonPlayGame = findViewById(R.id.buttonPlayGame);
        context = getApplicationContext();
        
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
    }
    public static class NetworkManager {
        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}

