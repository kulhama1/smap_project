package com.example.martin.smapy.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatistikaActivity extends AppCompatActivity {

    TextView tvPocetVyletu, tvPocetKM, tvNejviceNavstObec, tvDruhaNavsObec, tvTretiNavsObec, tvNejvetsiHodnoceni, tvDruheNejvetsiHodnoceni, tvTretiNejvetsiHodnoceni, tvPrumernaTeplota, tvPrumernyPocetKM, tvPrumerneHodnoceni;
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);
    Button detail1, detail2, detail3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistika);

        tvPocetVyletu = findViewById(R.id.tvPocetVyletu);
        tvPocetKM = findViewById(R.id.tvPocetKilometru);
        tvNejviceNavstObec = findViewById(R.id.tvNejviceNavstevovanaObec);
        tvNejvetsiHodnoceni = findViewById(R.id.tvNejvetsiHodnoceni);
        tvPrumerneHodnoceni = findViewById(R.id.tvPrumerneHodnoceni);
        tvPrumernaTeplota = findViewById(R.id.tvPrumernaTeplota);
        tvPrumernyPocetKM = findViewById(R.id.tvPrumernyPocetKM);
        tvDruheNejvetsiHodnoceni = findViewById(R.id.tvNejvetsiDruheHodnoceni);
        tvTretiNejvetsiHodnoceni = findViewById(R.id.tvNejvetsiTretiHodnoceni);
        tvDruhaNavsObec = findViewById(R.id.tvDruhaNejviceNavstevovanaObec);
        tvTretiNavsObec = findViewById(R.id.tvTretiNejviceNavstevovanaObec);
        detail1 = findViewById(R.id.btnDetail);
        detail2 = findViewById(R.id.btnDetail1);
        detail3 = findViewById(R.id.btnDetail2);

        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TABULKANAVSTEVA");
        List<String> mesta = new ArrayList<>();
        List<Integer> hodnoceni = new ArrayList<>();
        List<String> nameOfPlace = new ArrayList<>();
        int pocetID = 0;
        double pocetKM = 0;
        double prumHodnoceni = 0;
        double prumTeplota = 0;

        while (cursor.moveToNext()) {

            mesta.add(cursor.getString(7));
            pocetID = pocetID + 1;
            pocetKM = pocetKM + (cursor.getDouble(10));
            prumHodnoceni = prumHodnoceni + (cursor.getInt(5));
            prumTeplota = prumTeplota + (cursor.getDouble(9));
            hodnoceni.add(cursor.getInt(5));
            nameOfPlace.add(cursor.getString(1));
        }
        final List<String> hodnoceAMisto = new ArrayList<>();

        if(hodnoceni.size()>0) {
            for (int i = 0; i < hodnoceni.size(); i++) {
                hodnoceAMisto.add(hodnoceni.get(i) + " - " + nameOfPlace.get(i));
            }
        }

        Collections.sort(hodnoceAMisto);
        Collections.sort(mesta);

        int pocetMest = 1;
        String nazev, nazev2 = "";
        List<String> mesta2 = new ArrayList<>();
        List<Integer> pocetMesta2 = new ArrayList<>();
        List<String> pocetMestAMesto = new ArrayList<>();
        String konec = "no data";

        mesta.add(konec);

        if(mesta.size() > 1) {
            for (int i = 0; i < mesta.size() - 1; i++) {
                nazev = mesta.get(i);
                nazev2 = mesta.get(i + 1);

                if (nazev.equals(nazev2)) {
                    pocetMest = pocetMest + 1;

                } else {
                    pocetMesta2.add(pocetMest);
                    mesta2.add(nazev);
                    pocetMest = 1;
                }
            }
            for (int i = 0; i < pocetMesta2.size(); i++) {
                pocetMestAMesto.add(pocetMesta2.get(i) + " - " + mesta2.get(i));
            }
            Collections.sort(pocetMestAMesto);
        }

        tvPocetVyletu.setText("Počet výletů: \r\n " + Integer.toString(pocetID));
        tvPocetKM.setText("Počet nacestovaných km: \r\n " + Double.toString(pocetKM));
        tvPrumernyPocetKM.setText("Průměrné počet KM: \r\n" + Double.toString((pocetKM/pocetID)));
        tvPrumernaTeplota.setText("Průměrná teplota: \r\n" + Double.toString((prumTeplota/pocetID)));
        tvPrumerneHodnoceni.setText("Průměrné hodnocení: \r\n" + Double.toString((prumHodnoceni/pocetID)));
        if(pocetMestAMesto.size() > 0) {
            tvNejviceNavstObec.setText("Nejvíce navštívená obec: \r\n"+pocetMestAMesto.get(pocetMestAMesto.size() - 1));
            if(pocetMestAMesto.size() > 1){
                tvDruhaNavsObec.setText("Druhá nejvíce navštěvovaná obec: \r\n "+pocetMestAMesto.get(pocetMestAMesto.size() - 2));
                if(pocetMestAMesto.size() > 2){
                    tvTretiNavsObec.setText("Třetí nejvíce navštívená obec: \r\n "+pocetMestAMesto.get(pocetMestAMesto.size() - 3));
                }else{
                    tvTretiNavsObec.setText("no data");
                }
            }else{
                tvDruhaNavsObec.setText("no data");
                tvTretiNavsObec.setText("no data");
            }
        }
        else{
            tvNejviceNavstObec.setText("no data");
            tvDruhaNavsObec.setText("no data");
            tvTretiNavsObec.setText("no data");
        }
        if(hodnoceAMisto.size() > 0) {
            tvNejvetsiHodnoceni.setText("Nejlépe hodnocené místo:\r\n"+hodnoceAMisto.get(hodnoceAMisto.size() - 1));
            if(hodnoceAMisto.size() > 1) {
                tvDruheNejvetsiHodnoceni.setText("Druhé nejlépe hodnocené místo:\r\n" +hodnoceAMisto.get(hodnoceAMisto.size() - 2));
                if(hodnoceAMisto.size() > 2) {
                    tvTretiNejvetsiHodnoceni.setText("Třetí nejlépe hodnocené místo:\r\n" +hodnoceAMisto.get(hodnoceAMisto.size() - 3));
                }else{
                    tvTretiNejvetsiHodnoceni.setText("no data");
                    detail3.setVisibility(View.INVISIBLE);
                }
            }else{
                tvDruheNejvetsiHodnoceni.setText("no data");
                tvTretiNejvetsiHodnoceni.setText("no data");
                detail2.setVisibility(View.INVISIBLE);
                detail3.setVisibility(View.INVISIBLE);
            }
        }
        else{
            tvNejvetsiHodnoceni.setText("no data");
            tvDruheNejvetsiHodnoceni.setText("no data");
            tvTretiNejvetsiHodnoceni.setText("no data");
            detail1.setVisibility(View.INVISIBLE);
            detail2.setVisibility(View.INVISIBLE);
            detail3.setVisibility(View.INVISIBLE);
        }
        detail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detailString1 = hodnoceAMisto.get(hodnoceAMisto.size() - 1);
                String vysledek = upravRetezec(detailString1);
                //Toast.makeText(getApplicationContext(),vysledek,Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                i.putExtra("name", vysledek);
                startActivity(i);
            }
        });

        detail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detailString1 = hodnoceAMisto.get(hodnoceAMisto.size() - 2);
                String vysledek = upravRetezec(detailString1);
                //Toast.makeText(getApplicationContext(),vysledek,Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                i.putExtra("name", vysledek);
                startActivity(i);

            }
        });
        detail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detailString1 = hodnoceAMisto.get(hodnoceAMisto.size() - 3);
                String vysledek = upravRetezec(detailString1);
                //Toast.makeText(getApplicationContext(),vysledek,Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                i.putExtra("name", vysledek);
                startActivity(i);

            }
        });

    }
    private String upravRetezec(String retezec){

        String detailString1 = retezec;

        detailString1 = detailString1.replace("0", "");
        detailString1 = detailString1.replace("1", "");
        detailString1 = detailString1.replace("2", "");
        detailString1 = detailString1.replace("3", "");
        detailString1 = detailString1.replace("4", "");
        detailString1 = detailString1.replace("5", "");
        detailString1 = detailString1.replace("6", "");
        detailString1 = detailString1.replace("7", "");
        detailString1 = detailString1.replace("8", "");
        detailString1 = detailString1.replace("9", "");
        detailString1 = detailString1.replace("-", "");

        char charArray[] = new char[0];
        for (int i = 0; i < detailString1.length(); i++) {
            if (detailString1.charAt(i) != ' ') {
                charArray = new char[detailString1.length() - i];
                detailString1.getChars(i, detailString1.length(), charArray, 0);
                i = detailString1.length();

            }
        }
        detailString1 = String.copyValueOf(charArray);

        return detailString1;
    }
}
