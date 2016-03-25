package com.example.abdallah.jsondemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private TextView tvDate ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnHit = (Button)findViewById(R.id.btnHit);
        tvDate = (TextView)findViewById(R.id.tvJsonItem);
        new JSONTask().execute("http://www.w3schools.com/appml/appml.php?model=model_cd_from_json");

    }


//to make code work in background and connect
  public class JSONTask extends AsyncTask< String ,String , String >{


    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();

            String line = "";

            while ((line = reader.readLine()) != null ) {
                buffer.append(line);
            }

            return buffer.toString();

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection !=null){
                connection.disconnect();}
            try{
                if (reader != null ){
                    reader.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result ) {
    super.onPostExecute(result);

        tvDate.setText(result);
    }

}
}
