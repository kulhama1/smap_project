package com.example.martin.smapy.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.smapy.Database.SQLiteHelper;
import com.example.martin.smapy.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NavstiveneActivity extends AppCompatActivity {
    String name;
    Editable description;
    EditText etDescription;
    TextView tvName, tvHodnoceni;
    ImageView imgViewPhoto;
    Button btnFotografie, btnUlozit;
    SeekBar sbHodnoceni;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "PlacesDB.sqlite", null, 1);
    double gps_x, gps_y;
    String city, weather;
    Double kilometres,temperature;
    boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navstivene);

        Intent i = getIntent();

        name = i.getExtras().getString("name");
        gps_x = i.getExtras().getDouble("gps_x");
        gps_y = i.getExtras().getDouble("gps_y");
        city = i.getExtras().getString("city");
        weather = i.getExtras().getString("oblacnost");
        kilometres = i.getExtras().getDouble("vzdalenost");
        temperature = i.getExtras().getDouble("teplota");

        tvName = findViewById(R.id.textViewNameDetail);
        tvName.setText(name);

        btnFotografie = findViewById(R.id.btnPoriditFotografii);
        btnUlozit = findViewById(R.id.btnNavstiveneMisto);

        etDescription = findViewById(R.id.editTextPopisek);
        description =  etDescription.getText();

        tvHodnoceni = findViewById(R.id.textViewHodnoceniHodnota);
        imgViewPhoto = findViewById(R.id.imageViewPhoto);

        sbHodnoceni = findViewById(R.id.seekBarHodnoceni);
        sbHodnoceni.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvHodnoceni.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
        //final String[] sCamera = {""};
        btnFotografie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
                //sCamera[0] = "a";
            }
        });
        btnUlozit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sDescription = etDescription.getText().toString();
                String sHodnoceni = tvHodnoceni.getText().toString();
                if (sDescription.matches("") || sHodnoceni.matches("") || imageFilePath.matches("")) {
                    Toast.makeText(getApplicationContext(), "Chybí některé povinné údaje: foto, popisek, hodnocení", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Úspěšně uloženo do databáze", Toast.LENGTH_SHORT).show();
                    sqLiteHelper.insertData1(name, String.valueOf(etDescription.getText()), gps_x, gps_y, Integer.valueOf((String) tvHodnoceni.getText()), imageFilePath, city, weather, temperature, kilometres);
                    Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                    i.putExtra("casovac", 1);
                    startActivity(i);
                }
            }
        });

    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                imgViewPhoto.setImageURI(Uri.parse(imageFilePath));
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

}
