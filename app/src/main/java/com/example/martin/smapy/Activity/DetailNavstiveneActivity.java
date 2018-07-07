package com.example.martin.smapy.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.R;

public class DetailNavstiveneActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);
    String name;
    Button btnUpravit, btnSmazat, btnMapa;
    TextView textViewName, textViewDescription, textViewTemperature, textViewWeather, textViewCity, textViewKilometers, textViewHodnoceni;
    ImageView imageViewPhotoNavstivene;
    boolean isImageFitToScreen;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_navstivene_activity);

        imageViewPhotoNavstivene = findViewById(R.id.imageViewPhotoNavstivene);
        textViewCity = findViewById(R.id.textViewDetailNavstiveneCity);
        textViewName = findViewById(R.id.textViewDetailNavstiveneName);
        textViewDescription = findViewById(R.id.textViewDetailNavstiveneDescription);
        textViewWeather = findViewById(R.id.textViewDetailNavstiveneWeather);
        textViewTemperature = findViewById(R.id.textViewDetailNavstiveneTemperature);
        textViewKilometers = findViewById(R.id.textViewDetailNavstiveneKilometers);
        textViewHodnoceni = findViewById(R.id.textViewDetailNavstiveneHodnoceni);

        btnUpravit = findViewById(R.id.btnNavstiveneUpravit);
        btnSmazat = findViewById(R.id.btnNavstiveneSmazat);
        btnMapa = findViewById(R.id.btnZpetNaMapu);

        Intent i = getIntent();

        name = i.getExtras().getString("name");

        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TABULKANAVSTEVA WHERE NAME ='"+ name+"'");

        cursor.moveToFirst();
        textViewName.setText(cursor.getString(1));
        textViewHodnoceni.setText("Hodnocení: " +Integer.toString(cursor.getInt(5)));
        textViewCity.setText("Místo pořízení fotografie: " +cursor.getString(7));
        textViewKilometers.setText("Ujetá vzdálenost: " +Double.toString(cursor.getDouble(10)));
        textViewTemperature.setText("Teplota na místě: " +Double.toString(cursor.getDouble(9)));
        textViewWeather.setText("Počasí na místě: " +cursor.getString(8));
        textViewDescription.setText(cursor.getString(2));
        imageViewPhotoNavstivene.setImageURI(Uri.parse(cursor.getString(6)));
        final String adresa = cursor.getString(6);
        id = cursor.getInt(0);

        btnSmazat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteHelper.queryData("DELETE FROM TABULKANAVSTEVA WHERE NAME ='"+ name+"'");
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);

                startActivity(i);
            }
        });
        btnUpravit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UpdatePlace11activity.class);
                i.putExtra("name", textViewName.getText());
                i.putExtra("hodnoceni", textViewHodnoceni.getText());
                i.putExtra("city", textViewCity.getText());
                i.putExtra("kilometers", textViewKilometers.getText());
                i.putExtra("temperature", textViewTemperature.getText());
                i.putExtra("weather", textViewWeather.getText());
                i.putExtra("description", textViewDescription.getText());
                i.putExtra("adresa", adresa);
                i.putExtra("id", id);
                startActivity(i);

            }
        });
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("casovac", 1);
                startActivity(i);

            }
        });

        imageViewPhotoNavstivene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                    i.putExtra("name", name);
                    startActivity(i);
                    //imageViewPhotoNavstivene.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    //imageViewPhotoNavstivene.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    imageViewPhotoNavstivene.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageViewPhotoNavstivene.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });
    }
}
