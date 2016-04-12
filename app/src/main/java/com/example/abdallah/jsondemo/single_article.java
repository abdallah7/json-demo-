package com.example.abdallah.jsondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class single_article extends AppCompatActivity {

    String passed = null ;
    private TextView Titleviwe  ;
    private TextView descriptionviwe  ;
    private ImageView imageView ;
    private String id ;
    private String cuontry ;
    RequestQueue requestQueue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        requestQueue = Volley.newRequestQueue(this);

        Titleviwe = (TextView)findViewById(R.id.Title);

        descriptionviwe = (TextView)findViewById(R.id.description);

        imageView = (ImageView)findViewById(R.id.image);


        passed = getIntent().getStringExtra(MainActivity.ID_EXTRA);

        String CurrentString = passed;
        String[] separated = CurrentString.split("/");
        id = separated[0]; // this will contain "Fruit"
        cuontry = separated[1]; // this will contain " they taste good"

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.1.103/JSON/"+ cuontry +".php",null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("reports");

                                JSONObject report = jsonArray.getJSONObject(Integer.parseInt(id));

                                String Title = report.getString("Title");
                                String description = report.getString("description");
                                String image = report.getString("image");


                            Titleviwe.setText(Title);
                            descriptionviwe.setText(description);
                            ImageLoader.getInstance().displayImage( image , imageView );


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        requestQueue.add(jsonObjectRequest);

    }
}
