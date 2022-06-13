package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ImageView imageViewStar;

    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<Button> buttons;

    private int numberOfQuestion;
    private int numberOfRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }
    private void getContent(){
        DownloadContentTask task = new DownloadContentTask();
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
        DownloadImageTask task = new DownloadImageTask();
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

    public void onClickAnswer(View view) {
        playGame();
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if(Integer.parseInt(tag) == numberOfRightAnswer){
            Toast toast = Toast.makeText(this, "Верно!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else {
            Toast toast = Toast.makeText(this, "Неверно. Правильный ответ: "+names.get(numberOfQuestion), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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

