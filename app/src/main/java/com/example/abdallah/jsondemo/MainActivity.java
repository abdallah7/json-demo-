package com.example.abdallah.jsondemo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.example.abdallah.jsondemo.models.ReportModel;
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
    public Spinner spinner ;
// to send id for list items
//    public final static String ID_EXTRA = "com.example.abdallah.jsondemo._ID";

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
//drop down list
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.countries,android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
//call list
        repoteslist = (ListView)findViewById(R.id.repoteslist);
        //call drop down list
        spinner.setOnItemSelectedListener(this);

        repoteslist.setOnItemClickListener(onListclick);
    }
// action  click on list item
    private AdapterView.OnItemClickListener onListclick = new AdapterView.OnItemClickListener(){
// send data to the second activity
        public void onItemClick(AdapterView<?> parent , View view , int position , long id ){
            Intent i  = new Intent(MainActivity.this , single_article.class );

            i.putExtra("id" , String.valueOf(id));
            i.putExtra("country" , selected);
            startActivity(i);

        }
    };

// action on select from drop dowen list
    //the frist in the app
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView cuontry = (TextView) view ;
        //localhost maybe chang any time  must run on the same network(pute the json files <egypt.php,qatar.php,soudi.php,dubai.php> in folder named JSON change network ip to 192.168.1.103)
        selected = cuontry.getText().toString();

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            new JSONTask().execute("http://192.168.1.124/JSON/" + selected + ".php");
        }else {
            Toast.makeText(MainActivity.this, "sorry internet not working", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //to make code work in background and connect
  public class JSONTask extends AsyncTask<String, String, List<ReportModel>> {


    @Override
    protected List<ReportModel> doInBackground(String... params) {
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
            JSONObject parntObject = new JSONObject(finalJeson);
            //take the array with name movies from the json opject
            JSONArray parentArray = parntObject.getJSONArray("reports");

            List<ReportModel> reportModelList = new ArrayList<>();

            for (int i=0 ; i<parentArray.length() ; i++ ) {
                //take the opject from the array
                JSONObject finalopject = parentArray.getJSONObject(i);
                ReportModel reportModel = new ReportModel();
                reportModel.setTitle(finalopject.getString("Title"));
                reportModel.setImage(finalopject.getString("image"));

                //adding final list
                reportModelList.add(reportModel);
            }
            return reportModelList;

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
    protected void onPostExecute(List<ReportModel> result ) {
        super.onPostExecute(result);
        ReportAdapter adapter = new ReportAdapter(getApplicationContext() , R.layout.row , result);
        repoteslist.setAdapter(adapter);
    //TODO need to set data to list
    }

    }

    public class ReportAdapter extends ArrayAdapter{

        private List<ReportModel> reportModelList ;
        private int resource;
        private LayoutInflater inflater ;
        public ReportAdapter(Context context, int resource, List<ReportModel> objects) {
            super(context, resource, objects);
            reportModelList = objects ;
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
            ImageLoader.getInstance().displayImage(reportModelList.get(position).getImage() , image);
            // Default options will be used

            Title.setText(reportModelList.get(position).getTitle());
            return convertView;
        }//getview
    }//ReportAdapter
}//main activity
