package com.example.sharan.knowyourgovernment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PhotoActivity extends AppCompatActivity {

    TextView locationTextView;
    TextView officeTextView;
    TextView officialNameTextView;
    ImageView officialImageView;
    String location;
    Official obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        locationTextView = findViewById(R.id.locationTextView);
        officeTextView = findViewById(R.id.officeTextView);
        officialNameTextView = findViewById(R.id.officialTextView);
        officialImageView = findViewById(R.id.officialImageView);

        Intent intent = getIntent();
        if(intent.hasExtra("location")){
            location = intent.getStringExtra("location");
        }
        if (intent.hasExtra("officialData")) {
            obj = (Official) intent.getExtras().getSerializable("officialData");
        }

        initializeData();

    }

    private void initializeData() {
        locationTextView.setText(location);

        if(obj != null){
            officeTextView.setText(obj.getOffice() != null ? obj.getOffice() : "No Data Provided");
            officialNameTextView.setText(obj.getName() != null ? obj.getName() : "No Data Provided");

            //set background color
            if(obj.getParty().equalsIgnoreCase("republican")){
                //red
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorRed));
            }
            else if(obj.getParty().equalsIgnoreCase("democratic")){
                //blue
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
            }
            else{
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorBlack));
            }


            if (obj.getImageURL() != null)
            {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String changedUrl = obj.getImageURL().replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(officialImageView);
                    }
                }).build();
                picasso.load(obj.getImageURL())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(officialImageView);
            }
            else {
                Picasso.get().load(obj.getImageURL())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(officialImageView);
            }
        }
    }
}
