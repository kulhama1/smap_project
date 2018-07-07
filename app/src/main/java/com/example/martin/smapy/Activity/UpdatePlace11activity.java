package com.example.martin.smapy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.R;

public class UpdatePlace11activity extends AppCompatActivity {
    EditText etName, etDescription;
    TextView tvTemperature, tvWeather, tvKilometers, tvCity, tvEvalution;
    Button btnUlozitZmeny;
    ImageView ivPhoto;
    int id;

    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place11activity);

        etName = findViewById(R.id.editTextUpdateName);
        etDescription = findViewById(R.id.editTextUpdateDescription);

        tvTemperature = findViewById(R.id.textViewUpdateTemperature);
        tvCity = findViewById(R.id.textViewUpdateCity);
        tvEvalution = findViewById(R.id.textViewUpdateHodnoceni);
        tvWeather = findViewById(R.id.textViewUpdateWeather);
        tvKilometers = findViewById(R.id.textViewUpdateKilometers);

        ivPhoto = findViewById(R.id.imageViewUpdatePhoto);
        btnUlozitZmeny = findViewById(R.id.bbtnUpdateUlozitZmeny);

        Intent i = getIntent();

        etName.setText(i.getExtras().getString("name"));
        etDescription.setText(i.getExtras().getString("description"));

        tvTemperature.setText(i.getExtras().getString("temperature"));
        tvWeather.setText(i.getExtras().getString("weather"));
        tvKilometers.setText(i.getExtras().getString("kilometers"));
        tvCity.setText(i.getExtras().getString("city"));
        tvEvalution.setText(i.getExtras().getString("hodnoceni"));
        ivPhoto.setImageURI(Uri.parse(i.getExtras().getString("adresa")));
        id = i.getExtras().getInt("id");

        final Editable name = etName.getText();
        final Editable description = etDescription.getText();

        btnUlozitZmeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteHelper.queryData("UPDATE TABULKANAVSTEVA SET NAME = '" + name+"' WHERE ID =" +id);
                sqLiteHelper.queryData("UPDATE TABULKANAVSTEVA SET DESCRIPTION = '" + description+"' WHERE ID =" +id);
                Toast.makeText(getApplicationContext(), "Data úspěšně změněny",
                        Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), DetailNavstiveneActivity.class);
                i.putExtra("name",name.toString());
                startActivity(i);
            }
        });
    }

}
