package com.darrenmleith.snapchat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewSnapActivity extends AppCompatActivity {

    TextView _messageTextView;
    ImageView _snapImageView;
    private FirebaseAuth _mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        _messageTextView = findViewById(R.id.messageTextView);
        _snapImageView = findViewById(R.id.snapImageView);
        _messageTextView.setText(getIntent().getStringExtra("message"));
        _mAuth = FirebaseAuth.getInstance();

        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        try {
            myImage = task.execute(getIntent().getStringExtra("imageURL")).get();
            _snapImageView.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //custom class to download an image
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    //functionality to delete a snap from both Storage and from Realtime Database at the snaps>UID level when the backbutton pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //delete UID from snaps directory....love that .removeValue()
        FirebaseDatabase.getInstance().getReference().child("users").child(_mAuth.getCurrentUser().getUid()).child("snaps").child(getIntent().getStringExtra("snapKey")).removeValue();
        //delete from storage...love that .delete()
        FirebaseStorage.getInstance().getReference().child("images").child(getIntent().getStringExtra("imageName")).delete();
    }
}
