package com.example.martin.smapy.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.GPS.GPS;
import com.example.martin.smapy.Parser.MapHttpConnection;
import com.example.martin.smapy.Parser.PathJSONParser;
import com.example.martin.smapy.Zdroje.Weather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.martin.smapy.R;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DetailPlaceActivity1 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView tvName, tvDescription, tvTeplota, tvOblacnost;
    MapView mapaDetailu;
    Button sdiletMisto, hledatTrasu, navstiveneMisto;
    String name, description, oblacnost, vlhkost, misto, casPorizeni, tlak, city;
    double gpsX, gpsY, teplota;
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);
    int cisloTabulky = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();

        name = i.getExtras().getString("name");
        cisloTabulky = i.getExtras().getInt("cisloTabulky");
        city = i.getExtras().getString("city");

        tvName = findViewById(R.id.textViewName);
        tvDescription = findViewById(R.id.textViewDescription);
        tvOblacnost = findViewById(R.id.textViewOblacnost);
        tvTeplota = findViewById(R.id.textViewStupne);
        tvName.setText(name);

        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TABULKA"+cisloTabulky+" WHERE NAME ='"+ name+"'");

        cursor.moveToFirst();

        name = cursor.getString(2);
        description = cursor.getString(3);
        gpsX = cursor.getDouble(4);
        gpsY = cursor.getDouble(5);

        tvName.setText(name);
        tvDescription.setText(description);
        Weather.placeIdTask asyncTask = new Weather.placeIdTask(new Weather.AsyncResponse() {
            @Override
            public void processFinish(String output1, String output2, String output3, String output4, String output5, String output6) {

                //Toast.makeText(getApplicationContext(), "Místo:" + output1, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Oblačnost:" + output2, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Teplota:" + output3, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Vlhkost:" + output4, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Tlak:" + output5, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Datum zveřejnění:" + output6, Toast.LENGTH_LONG).show();

                tvOblacnost.setText("Počasí na místě: " +output2);
                tvTeplota.setText("Teplota na místě: " +output3);
                misto = output1;
                oblacnost = output2;
                teplota = Double.valueOf(output3);
                vlhkost = output4;
                tlak = output5;
                casPorizeni = output6;
            }
        });

        asyncTask.execute(String.valueOf(gpsX), String.valueOf(gpsY));

        //mapaDetailu = (MapView) findViewById(R.id.mapViewDetail);
        sdiletMisto = (Button) findViewById(R.id.btnSdiletMisto);
        navstiveneMisto = (Button) findViewById(R.id.btnNavstivMisto);

        sdiletMisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Rozhodl jsem se navštívit místo:\n" +name +" "+ "\nPíše se o tom následující:\n" + description
                        +" "+ "\nAktuální počasí na místě:\n" +  oblacnost +" "+ "\nAktuální teplota na místě:\n" + teplota
                        +"\nPřidáš se?";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        final LatLng place = new LatLng(gpsX, gpsY);
        mMap.addMarker(new MarkerOptions().position(place).title(name));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.setMinZoomPreference(6.0f);
        GPS gps = new GPS(getApplicationContext());
        Location mLocation = gps.getLocation();
        final LatLng actualPlace = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(actualPlace).title("Vaše pozice"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.setMinZoomPreference(6.0f);
        final double[] vzdalenostKM = new double[1];
        //hledání trasy a pocitani vzdalenosti
        String url = getMapsApiDirectionsUrl(place, actualPlace);
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        float vzdalenost = distance((float)place.latitude, (float)place.longitude, (float)actualPlace.latitude, (float)actualPlace.longitude);
        //Toast.makeText(getApplicationContext(), Float.toString(vzdalenost /1000) + "km",Toast.LENGTH_SHORT).show();
        TextView tvVzdalenost = findViewById(R.id.tvVzdalenostNaMisto);
        tvVzdalenost.setText("Vzdálenost na místo: " + Float.toString(vzdalenost /1000) + "km");

        vzdalenostKM[0] = (double) vzdalenost/1000;

        navstiveneMisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), NavstiveneActivity.class);
                i.putExtra("name", name);
                i.putExtra("description", description);
                i.putExtra("teplota", teplota);
                i.putExtra("oblacnost", oblacnost);
                i.putExtra("gps_x", gpsX);
                i.putExtra("gps_y", gpsY);
                i.putExtra("vzdalenost", vzdalenostKM[0]);
                i.putExtra("city", city);
                //Toast.makeText(getApplicationContext(),String.valueOf(teplota),Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });
    }


    private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;

    }

private class ReadTask extends AsyncTask<String, Void , String> {

    @Override
    protected String doInBackground(String... url) {
        // TODO Auto-generated method stub
        String data = "";
        try {
            MapHttpConnection http = new MapHttpConnection();
            data = http.readUr(url[0]);


        } catch (Exception e) {
            // TODO: handle exception
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        new ParserTask().execute(result);
    }

}

private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {
        // TODO Auto-generated method stub
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            PathJSONParser parser = new PathJSONParser();
            routes = parser.parse(jObject);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();;
        PolylineOptions lineOptions = new PolylineOptions();;
        lineOptions.width(2);
        lineOptions.color(Color.RED);
        MarkerOptions markerOptions = new MarkerOptions();
        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);

        }
        // Drawing polyline in the Google Map for the i-th route
        if(points.size()!=0)mMap.addPolyline(lineOptions);//to avoid crash
    }}
    public float distance (float lat_a, float lng_a, float lat_b, float lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }
}
