package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayGame extends AppCompatActivity {
    private ImageView imageViewStar;
    private TextView textViewTimer;
    private TextView textViewScore;

    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<Button> buttons;

    private int numberOfQuestion;
    private int numberOfRightAnswer;

    private int countOfQuestions = 0;
    private int countOfRightAnswer = 0;

    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        Button button0 = findViewById(R.id.button0);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        imageViewStar = findViewById(R.id.imageViewStar);
        urls = new ArrayList<>();
        names = new ArrayList<>();
        getContent();
        playGame();
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(getTime(l));
                if (l < 10000){
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswer >= max){
                    preferences.edit().putInt("max", countOfRightAnswer).apply();
                }
                Intent intent = new Intent(PlayGame.this, ResultGame.class);
                intent.putExtra("result", countOfRightAnswer);
                startActivity(intent);
            }
        };
        timer.start();
    }
    private void getContent(){
        PlayGame.DownloadContentTask task = new PlayGame.DownloadContentTask();
        try {
            String url = "https://forbes.kz//process/expertise/100_samyih_vliyatelnyih_znamenitostey_po_versii_forbes/";
            String content = task.execute(url).get();
            String start = "<a href=\"//forbes.kz/img/articles/9f585efd1f4e2236b6eaf5bfcd66ae41-big.jpg\" rel=\"lightbox-article\">";
            String finish = "<a class=\"social-follow\" href=\"https";
            Pattern pattern = Pattern.compile(start+"(.*?)"+finish);
            Matcher matcher = pattern.matcher(content);
            String splitContent = "";
            while (matcher.find()){
                splitContent = matcher.group(1);
            }
            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\" width=");
            Pattern patternName = Pattern.compile("<h2>(.*?)</h2>");
            assert splitContent != null;
            Matcher matcherImg = patternImg.matcher(splitContent);
            Matcher matcherName = patternName.matcher(splitContent);
            while (matcherImg.find()){
                String ur = "https:";
                urls.add(ur+matcherImg.group(1));
            }
            while (matcherName.find()){
                String str = "(\\d+\\.|&nbsp;|&laquo;|&raquo;)";
                String strName = Objects.requireNonNull(matcherName.group(1)).replaceAll(str,"");
                names.add(strName);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playGame() {
        generateQuestions();
        PlayGame.DownloadImageTask task = new PlayGame.DownloadImageTask();
        try {
            Bitmap bitmap = task.execute(urls.get(numberOfQuestion)).get();
            if(bitmap!=null){
                imageViewStar.setImageBitmap(bitmap);
                for(int i=0; i<buttons.size(); i++){
                    if(i == numberOfRightAnswer){
                        buttons.get(i).setText(names.get(numberOfQuestion));
                    }else {
                        int wrongAnswer = generateWrongAnswer();
                        buttons.get(i).setText(names.get(wrongAnswer));
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        String score = String.format("%s / %s", countOfRightAnswer, countOfQuestions);
        textViewScore.setText(score);
    }
    //Generating a question
    private void generateQuestions() {
        numberOfQuestion = (int) (Math.random()*names.size());
        numberOfRightAnswer = (int) (Math.random()*buttons.size());
    }
    //Generating wrong answers
    private int generateWrongAnswer(){
        return (int) (Math.random()*names.size());
    }

    private String getTime(long millis){
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds);
    }


    public void onClickAnswer(View view) {
        if (!gameOver) {
            Button button = (Button) view;
            String tag = button.getTag().toString();
            if (Integer.parseInt(tag) == numberOfRightAnswer) {
                countOfRightAnswer++;
                button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

            } else {
                button.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            countOfQuestions++;
            playGame();
        }
    }
    private static class DownloadContentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null){
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}