package com.example.abdallah.jsondemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

public class MainActivity extends AppCompatActivity {


    private ListView lvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvMovies = (ListView)findViewById(R.id.lvMovies);
//        new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt");
        new JSONTask().execute("http://192.168.1.15/json.php");

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
            JSONArray parentArray = parntOpject.getJSONArray("movies");
            //stor all data in it
            //StringBuffer finalbuffereddata = new StringBuffer();

            List<MovieModel> movieModelList = new ArrayList<>();

            for (int i=0 ; i<parentArray.length() ; i++ ) {
                //take the opject from the array
                JSONObject finalopject = parentArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel();
                movieModel.setMovie(finalopject.getString("movie"));
                movieModel.setYear(finalopject.getInt("year"));
                movieModel.setRating((float) finalopject.getDouble("rating"));
                movieModel.setDirector(finalopject.getString("director"));
                movieModel.setDuration(finalopject.getString("duration"));
                movieModel.setTagline(finalopject.getString("tagline"));
                movieModel.setImage(finalopject.getString("image"));
                movieModel.setStory(finalopject.getString("story"));

                List<MovieModel.Cast> castList = new ArrayList<>();
                for(int j=0 ; j<finalopject.getJSONArray("cast").length(); j++)
                {
                    JSONObject castObject = finalopject.getJSONArray("cast").getJSONObject(j);
                    MovieModel.Cast cast = new MovieModel.Cast();
                    cast.setName(castObject.getString("name"));
                    castList.add(cast);
                }
                movieModel.setCastList(castList);


                //test
                //String moviename = finalopject.getString("movie");
                //Integer year = finalopject.getInt("year");
                //finalbuffereddata.append(moviename + " -> " + year + "\n");

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
        lvMovies.setAdapter(adapter);
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

            ImageView ivMovieIcon ;
            TextView tvMovie ;
            TextView tvTagline ;
            TextView tvYear ;
            TextView tvDuration ;
            TextView tvDirector ;
            RatingBar rbMovieRating;
            TextView tvCast ;
            TextView tvStory ;

            ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            tvMovie = (TextView)convertView.findViewById(R.id.tvMovie);
            tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
            tvYear = (TextView)convertView.findViewById(R.id.tvYear);
            tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
            tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
            rbMovieRating = (RatingBar)convertView.findViewById(R.id.rbMovie);
            tvCast = (TextView)convertView.findViewById(R.id.tvCast);
            tvStory = (TextView)convertView.findViewById(R.id.tvStory);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage() , ivMovieIcon);
            // Default options will be used

            tvMovie.setText(movieModelList.get(position).getMovie());
            tvTagline.setText(movieModelList.get(position).getTagline());
            tvYear.setText("year: " + movieModelList.get(position).getYear());
            tvDuration.setText("Duration: " + movieModelList.get(position).getDuration());
            tvDirector.setText("Directory: " + movieModelList.get(position).getDirector());
            //Rating bar
            rbMovieRating.setRating(movieModelList.get(position).getRating() / 2);

            StringBuffer stringBuffer = new StringBuffer();
            for (MovieModel.Cast cast : movieModelList.get(position).getCastList()){
                stringBuffer.append(cast.getName() + ", ");
            }

            tvCast.setText(stringBuffer);
            tvStory.setText(movieModelList.get(position).getStory());
            return convertView;
        }
    }
}
