package com.example.abdallah.jsondemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abdallah.jsondemo.models.MovieModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public String selected ;
    private ListView repoteslist;
    Spinner spinner ;
// to send id for list items
    public final static String ID_EXTRA = "com.example.abdallah.jsondemo._ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// to display image
       // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
//cuontry drop down list
        spinner = (Spinner)findViewById(R.id.cuntry);
//drop down lest
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.countries,android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
//call list
        repoteslist = (ListView)findViewById(R.id.repoteslist);
        //call drop down list
        spinner.setOnItemSelectedListener(this);

        repoteslist.setOnItemClickListener(onListclik);
    }
// action  click on list item
    private AdapterView.OnItemClickListener onListclik = new AdapterView.OnItemClickListener(){
// send data to the second activity
        public void onItemClick(AdapterView<?> parent , View view , int position , long id ){
            Intent i  = new Intent(MainActivity.this , single_article.class );

            i.putExtra(ID_EXTRA , String.valueOf(id)+"/"+selected);
            startActivity(i);

        }
    };

// action on select from drop dowen list
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView cuontry = (TextView) view ;
        //localhost maybe chang any time  must run on the same network(pute the json files <egypt.php,qatar.php,soudi.php,dubai.php> in folder named JSON change network ip to 192.168.1.103)
        selected = cuontry.getText().toString();
        new JSONTask().execute("http://192.168.1.103/JSON/" + selected + ".php");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //to make code work in background and connect
  public class JSONTask extends AsyncTask<String, String, List<MovieModel>> {


    @Override
    protected List<MovieModel> doInBackground(String... params) {
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
            // Take a copy from buffer
            String finalJeson = buffer.toString();
            // take the json opject
            JSONObject parntOpject = new JSONObject(finalJeson);
            //take the array with name movies from the json opject
            JSONArray parentArray = parntOpject.getJSONArray("reports");

            List<MovieModel> movieModelList = new ArrayList<>();

            for (int i=0 ; i<parentArray.length() ; i++ ) {
                //take the opject from the array
                JSONObject finalopject = parentArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel();
                movieModel.setTitle(finalopject.getString("Title"));
                movieModel.setImage(finalopject.getString("image"));

                //adding final list
                movieModelList.add(movieModel);
            }
            return movieModelList;

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
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
    protected void onPostExecute(List<MovieModel> result ) {
        super.onPostExecute(result);
        MovieAdapter adapter = new MovieAdapter(getApplicationContext() , R.layout.row , result);
        repoteslist.setAdapter(adapter);
    //TODO need to set data to list
    }

    }

    public class MovieAdapter extends ArrayAdapter{

        private List<MovieModel> movieModelList ;
        private int resource;
        private LayoutInflater inflater ;
        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects ;
            this.resource = resource ;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null ){
                convertView = inflater.inflate(resource , null);
            }

            ImageView image ;
            TextView Title ;

            image = (ImageView)convertView.findViewById(R.id.image);
            Title = (TextView)convertView.findViewById(R.id.reporttitle);


            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage() , image);
            // Default options will be used

            Title.setText(movieModelList.get(position).getTitle());
            return convertView;
        }//getview
    }//move adptor
}//main activity
