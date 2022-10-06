package com.example.logbook;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.logbook.databinding.ActivityMainBinding;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;
    ImageView imageView;
    ArrayList<URLImage> ImageList;
    private Button back, next;
    private DbHelper dbHelper;
    private int currentPosition;
    private int first;
    private int last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        back = findViewById(R.id.Back);
        next = findViewById(R.id.Next);
        dbHelper = new DbHelper(this);

        ImageList = new ArrayList<URLImage>();
        imageView = (ImageView)findViewById(R.id.imageView);

        Cursor cursor = dbHelper.getAll();
        while (cursor.moveToNext()){
            URLImage imageModel = new URLImage();
            imageModel.SetValue(cursor.getInt(0), cursor.getString(1));
            ImageList.add(imageModel);
        }
        if (!ImageList.isEmpty()){
            first = ImageList.get(0).Id;
            last = ImageList.get(ImageList.size()-1).Id;
            currentPosition = 0;
            String url = ImageList.get(currentPosition).Url;
            new FetchImage(url).start();
        }

        binding.clearLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etURL.setText("");
            }
        });

        binding.addLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String WebUrl = "(https?:\\/\\/.*\\.(?:png|jpg))";

                if (binding.etURL.getText() != null && binding.etURL.getText().toString().matches(WebUrl)){
                    String url = binding.etURL.getText().toString();
                    if(dbHelper.insert(url)){
                        ImageList.clear();
                        Cursor cursor = dbHelper.getAll();
                        while (cursor.moveToNext()){
                            URLImage imageModel = new URLImage();
                            imageModel.SetValue(cursor.getInt(0), cursor.getString(1));
                            ImageList.add(imageModel);
                        }
                        if (!ImageList.isEmpty()){
                            first = ImageList.get(0).Id;
                            last = ImageList.get(ImageList.size()-1).Id;
                            currentPosition = 0;
                        }
                        Toast.makeText(MainActivity.this, "Add successfully", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Add fail", Toast.LENGTH_LONG).show();
                    }
                    new FetchImage(url).start();
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid input, please check again", Toast.LENGTH_LONG).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etURL.setText("");
                Cursor cursor = dbHelper.getAll();

                // Avoid Database empty => Back => ERR case
                if(cursor.getCount() > 0){
                    if (currentPosition > 0){
                        currentPosition = currentPosition - 1;

                        String url = ImageList.get(currentPosition).Url;
                        new FetchImage(url).start();
                    }
                    else {
                        String url = ImageList.get(currentPosition).Url;
                        new FetchImage(url).start();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "No data, please add data first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etURL.setText("");
                Cursor cursor = dbHelper.getAll();

                if(cursor.getCount() > 0){
                    currentPosition = currentPosition + 1;
                    if (currentPosition == last){
                        currentPosition = 0;
                    }
                    if (currentPosition <= last){
                        String url = ImageList.get(currentPosition).Url;
                        new FetchImage(url).start();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "No data, please add data first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class FetchImage extends Thread{
        String URL;
        Bitmap bitmap;
        FetchImage(String URL){
            this.URL = URL;
        }

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading image");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                }
            });

            InputStream inputStream = null;
            try {
                inputStream = new URL(URL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    binding.imageView.setImageBitmap(bitmap);
                }
            });
        }
    }
}