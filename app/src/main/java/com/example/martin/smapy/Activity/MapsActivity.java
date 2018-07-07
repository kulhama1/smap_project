package com.example.martin.smapy.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.martin.smapy.Database.Place;
import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.GPS.GPS;
import com.example.martin.smapy.Parser.HttpHandler;
import com.example.martin.smapy.R;
import com.example.martin.smapy.Zdroje.NotificationService;
import com.example.martin.smapy.Zdroje.PlaceSource;
import com.example.martin.smapy.Zdroje.Weather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.XMLFormatter;

import static com.google.android.gms.maps.GoogleMap.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Spinner typMapy;
    private Location mLocation;
    private RadioButton radioButtonVybrane;
    private RadioGroup radioGroupMarkery, radioGroupVzdalenost;
    private Button btnStatistika;
    private GPS gps;
    private double longitude;
    private double latitude;
    private String LOG_TAG = "XML";
    SharedPreferences prefs = null;
    private int UpdateFlag = 0;
    ArrayList adresy = new ArrayList();
    PlaceSource placeSource = new PlaceSource();
    public String adresaURL;
    public static final int REQUEST_PERMISSION = 200;
    SQLiteHelper sqLiteHelper;
    String city;
    LatLng sydney;
    int pocet = 1;
    RadioButton celaCR;
    int podminka, casovac = 0;

    //GetXMLFromServer getXMLFromServer = new GetXMLFromServer();

    String name, name1 = "", description = "", gps_x = "", id_z_webu = "", gps_y = "", adresa_obrazku = "";
    double gps_x1 = 0, gps_x2 = 0, gps_y1 = 0, gps_y2 = 0, gpsX_z_database, gpsY_z_database;
    int idMista;

    private int typMapyList[] = {
            MAP_TYPE_NORMAL,
            MAP_TYPE_SATELLITE,
            MAP_TYPE_HYBRID,
            MAP_TYPE_TERRAIN
    };

    @Override
    protected void onStop() {
        startService(new Intent(this, NotificationService.class));
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);

        if (isFirstTime()) {
            //Toast.makeText(getApplicationContext(), "first time", Toast.LENGTH_LONG).show();
            adresy = placeSource.naplnAdresy();
            sqLiteHelper.queryData("DROP TABLE IF EXISTS TABULKA1");
            sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TABULKA1(ID INTEGER PRIMARY KEY AUTOINCREMENT, id_z_webu INTEGER, name VARCHAR, description VARCHAR, gps_x DOUBLE, gps_y DOUBLE, adresa_obrazku VARCHAR)");
            sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TABULKANAVSTEVA(ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, description VARCHAR, gps_x DOUBLE, gps_y DOUBLE, evaluation INTEGER, link VARCHAR, city VARCHAR, weather VARCHAR, temperature DOUBLE, kilometers DOUBLE)");
            XML getXML = new XML();
            getXML.execute();

        }else{
            podminka = 1;
            Intent i = getIntent();
            casovac = i.getExtras().getInt("casovac");

        }

        sqLiteHelper.queryData("DROP TABLE IF EXISTS TABULKA2");
        sqLiteHelper.queryData("DROP TABLE IF EXISTS TABULKA3");

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TABULKA2(ID INTEGER PRIMARY KEY AUTOINCREMENT, id_z_webu INTEGER, name VARCHAR, description VARCHAR, gps_x DOUBLE, gps_y DOUBLE, adresa_obrazku VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TABULKA3(ID INTEGER PRIMARY KEY AUTOINCREMENT, id_z_webu INTEGER, name VARCHAR, description VARCHAR, gps_x DOUBLE, gps_y DOUBLE, adresa_obrazku VARCHAR)");

        gps = new GPS(getApplicationContext());
        mLocation = gps.getLocation();
        try {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        } catch (NullPointerException e) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle("GPS nenalezena")
                    .setMessage("Zapněte na mobilním zařízení GPS a Internet, jinak nebude aplikace fungovat správně!")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            i.putExtra("casovac", 0);
                            startActivity(i);
                        }
                    }).show();
        }

        radioGroupVzdalenost = findViewById(R.id.radioGroupVyberVzdalenosti);

        typMapy = (Spinner) findViewById(R.id.spiner_vyber_typu_mapy);
        typMapy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.setMapType(typMapyList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMap.setMapType(typMapyList[1]);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        if (addresses.size() > 0) {
            city = addresses.get(0).getLocality();
            //Toast.makeText(getApplicationContext(), city, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getApplicationContext(), "nelze lokalizovat", Toast.LENGTH_LONG).show();
        }
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        radioGroupMarkery = findViewById(R.id.radioGroupVyberMarkeru);
        btnStatistika = findViewById(R.id.buttonUkazka);
        btnStatistika.setVisibility(View.INVISIBLE);
        celaCR = findViewById(R.id.radioButtonCelaCR);
        celaCR.setChecked(true);
        XML getXML = new XML();
        getXML.execute();

        if (casovac < 1) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    zobrazMarkeryProCelouCR();
                }
            }, 5000);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    zobrazMarkeryProCelouCR();
                }
            }, 100);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        sydney = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(6.0f);

        btnStatistika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StatistikaActivity.class);
                startActivity(i);
            }
        });
        radioGroupVzdalenost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton10nejblizsich){
                    pocet = 3;

                }
                if (checkedId == R.id.radioButton100nejblizssich){
                    pocet = 2;

                }
                if (checkedId == R.id.radioButtonCelaCR){
                    pocet = 1;

                }
                zobrazMarkeryProCelouCR();
            }
        });

        radioGroupMarkery.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Toast.makeText(getApplicationContext(),"idRadioButton" + Integer.toString(checkedId),Toast.LENGTH_SHORT).show();
                if (checkedId == R.id.radioButtonVylety) {
                    pocet = 1;
                    zobrazMarkeryProCelouCR();
                    celaCR.setChecked(true);
                    btnStatistika.setVisibility(View.INVISIBLE);

                } else if (checkedId == R.id.radioButtonNavstivene) {
                    zobrazMarkeryVyletu();
                    btnStatistika.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Děkujeme za udělené povolení", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class XML extends AsyncTask {

        URL url;
        ArrayList<String> headlines = new ArrayList();
        ArrayList<String> links = new ArrayList();
        @Override
        protected Object doInBackground(Object[] objects) {

            if (podminka < 1 ){
                for (int i = 0; i < adresy.size(); i++) {

                    try {
                        url = new URL(String.valueOf(adresy.get(i)));
                        Log.d("adresa", String.valueOf(adresy.get(i)));

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(false);
                        XmlPullParser xpp = factory.newPullParser();

                        // We will get the XML from an input stream
                        xpp.setInput(getInputStream(url), "UTF_8");

                        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
                         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
                         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
                         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
                         * and take in consideration only "<title>" tag which is a child of "<item>"
                         *
                         * In order to achieve this, we will make use of a boolean variable.
                         */
                        boolean insideItem = false;

                        // Returns the type of current event: START_TAG, END_TAG, etc..
                        int eventType = xpp.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {

                                if (xpp.getName().equalsIgnoreCase("item")) {
                                    insideItem = true;
                                } else if (xpp.getName().equalsIgnoreCase("id")) {
                                    if (insideItem)
                                        id_z_webu = (xpp.nextText()); //extract the headline
                                } else if (xpp.getName().equalsIgnoreCase("name")) {
                                    if (insideItem)
                                        name1 = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("description")) {
                                    if (insideItem)
                                        description = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("gps_x")) {
                                    if (insideItem)
                                        gps_x = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("gps_y")) {
                                    if (insideItem)
                                        gps_y = (xpp.nextText()); //extract the link of article
                                }
                            } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                                sqLiteHelper.insertData(Integer.parseInt(id_z_webu), name1, description, Double.valueOf(gps_x), Double.valueOf(gps_y), "");
                            }

                            eventType = xpp.next(); //move to next element
                        } //zobrazMarkeryProCelouCR();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}
                if (podminka > 0){

                    try {

                        url = new URL("http://www.tixik.com/api/nearby?lat="+latitude+"&lng="+longitude+"&limit=100&key=demo%22");

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(false);
                        XmlPullParser xpp = factory.newPullParser();

                        // We will get the XML from an input stream
                        xpp.setInput(getInputStream(url), "UTF_8");

                        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
                         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
                         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
                         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
                         * and take in consideration only "<title>" tag which is a child of "<item>"
                         *
                         * In order to achieve this, we will make use of a boolean variable.
                         */
                        boolean insideItem = false;

                        // Returns the type of current event: START_TAG, END_TAG, etc..
                        int eventType = xpp.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {

                                if (xpp.getName().equalsIgnoreCase("item")) {
                                    insideItem = true;
                                } else if (xpp.getName().equalsIgnoreCase("id")) {
                                    if (insideItem)
                                        id_z_webu = (xpp.nextText()); //extract the headline
                                } else if (xpp.getName().equalsIgnoreCase("name")) {
                                    if (insideItem)
                                        name1 = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("description")) {
                                    if (insideItem)
                                        description = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("gps_x")) {
                                    if (insideItem)
                                        gps_x = (xpp.nextText()); //extract the link of article
                                } else if (xpp.getName().equalsIgnoreCase("gps_y")) {
                                    if (insideItem)
                                        gps_y = (xpp.nextText()); //extract the link of article
                                }
                            } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                                    sqLiteHelper.insertData2(Integer.parseInt(id_z_webu), name1, description, Double.valueOf(gps_x), Double.valueOf(gps_y), "");
                                }

                            eventType = xpp.next(); //move to next element
                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            try {

                    url = new URL("http://www.tixik.com/api/nearby?lat="+latitude+"&lng="+longitude+"&limit=10&key=demo%22");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // We will get the XML from an input stream
                xpp.setInput(getInputStream(url), "UTF_8");

                /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
                 * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
                 * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
                 * so we should skip the "<title>" tag which is a child of "<channel>" tag,
                 * and take in consideration only "<title>" tag which is a child of "<item>"
                 *
                 * In order to achieve this, we will make use of a boolean variable.
                 */
                boolean insideItem = false;

                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("id")) {
                            if (insideItem)
                                id_z_webu = (xpp.nextText()); //extract the headline
                        } else if (xpp.getName().equalsIgnoreCase("name")) {
                            if (insideItem)
                                name1 = (xpp.nextText()); //extract the link of article
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem)
                                description = (xpp.nextText()); //extract the link of article
                        } else if (xpp.getName().equalsIgnoreCase("gps_x")) {
                            if (insideItem)
                                gps_x = (xpp.nextText()); //extract the link of article
                        } else if (xpp.getName().equalsIgnoreCase("gps_y")) {
                            if (insideItem)
                                gps_y = (xpp.nextText()); //extract the link of article
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                            sqLiteHelper.insertData3(Integer.parseInt(id_z_webu), name1, description, Double.valueOf(gps_x), Double.valueOf(gps_y), "");

                    }

                    eventType = xpp.next(); //move to next element
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }}

                return headlines;

        }


        public InputStream getInputStream(URL url) {
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }

        public ArrayList<String> heads()
        {
            return headlines;
        }
    }
    private void zobrazMarkeryProCelouCR(){
        mMap.clear();
        radioGroupVzdalenost.setVisibility(View.VISIBLE);
        Cursor cursor = null;
        if(pocet < 2) {
            cursor = sqLiteHelper.getData("SELECT * FROM TABULKA1");
        }
        else if(pocet > 2) {
            cursor = sqLiteHelper.getData("SELECT * FROM TABULKA3");
            Log.d("nacitame3","nacitame3");
        }
        else {
            cursor = sqLiteHelper.getData("SELECT * FROM TABULKA2");
            Log.d("nacitame 2", "nacitame 2");
        }
        while (cursor.moveToNext()) {
            gpsX_z_database = cursor.getDouble(4);
            gpsY_z_database = cursor.getDouble(5);
            name = cursor.getString(2);
            idMista = cursor.getInt(1);

            LatLng place = new LatLng(gpsX_z_database, gpsY_z_database);

            mMap.addMarker(new MarkerOptions().position(place)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {


                    if (marker.getTitle().equals("Vaše pozice")) {

                    } else {
                        Intent i = new Intent(getApplicationContext(), DetailPlaceActivity1.class);
                        i.putExtra("name", marker.getTitle());
                        i.putExtra("cisloTabulky", pocet);
                        i.putExtra("city", city);
                        startActivity(i);
                    }
                }
            });
        }
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title(String.valueOf("Vaše pozice"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }
    private void zobrazMarkeryVyletu() {
        mMap.clear();
        radioGroupVzdalenost.setVisibility(View.INVISIBLE);
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TABULKANAVSTEVA");
        while (cursor.moveToNext()) {
            double gpsX = cursor.getDouble(3);
            double gpsY = cursor.getDouble(4);
            String name = cursor.getString(1);
            final int id = cursor.getInt(0);

            LatLng place = new LatLng(gpsX, gpsY);

            mMap.addMarker(new MarkerOptions().position(place)
                    .title(String.valueOf(name))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getTitle().equals("Vaše pozice")) {

                    } else {
                        Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                        i.putExtra("name", marker.getTitle());
                        startActivity(i);
                    }
                }
            });
        }

        mMap.addMarker(new MarkerOptions().position(sydney)
                .title(String.valueOf("Vaše pozice"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

}
