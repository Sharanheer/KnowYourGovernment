package com.example.sharan.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class OfficialActivity extends AppCompatActivity {

    private Official obj;
    private String area;
    private TextView officeTextView;
    private TextView nameTextView;
    private TextView partyTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView websiteTextView;
    private ImageView officialImageView;
    private ImageView flink;
    private ImageView tlink;
    private ImageView glink;
    private ImageView ylink;
    private String facbookId;
    private String twitterId;
    private String googlePlusId;
    private String youtubeId;


    private TextView areaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        officeTextView = findViewById(R.id.officeTextView);
        nameTextView = findViewById(R.id.nameTextView);
        partyTextView = findViewById(R.id.partyTextView);
        addressTextView = findViewById(R.id.addressInfoTextView);
        phoneTextView = findViewById(R.id.phoneInfoTextView);
        emailTextView = findViewById(R.id.emailInfoTextView);
        websiteTextView = findViewById(R.id.websiteInfoTextView);
        areaTextView = findViewById(R.id.locationTextView);
        officialImageView = findViewById(R.id.officialImageView);
        flink = findViewById(R.id.flink);
        tlink = findViewById(R.id.tlink);
        glink = findViewById(R.id.glink);
        ylink = findViewById(R.id.ylink);

        Intent intent = getIntent();
        if(intent.hasExtra("area")){
            area = intent.getStringExtra("area");
        }
        if (intent.hasExtra("officialData")) {
            obj = (Official) intent.getExtras().getSerializable("officialData");
        }

        initializeData();
    }

    private void initializeData() {

        areaTextView.setText(area);

        if(obj != null){
            officeTextView.setText(obj.getOffice() != null ? obj.getOffice() : "No Data Provided");
            nameTextView.setText(obj.getName() != null ? obj.getName() : "No Data Provided");
            partyTextView.setText(obj.getParty() != null ? "("+obj.getParty()+")" : "Unknown");

            //set background color
            if(obj.getParty() != null) {
                if (obj.getParty().equalsIgnoreCase("republican")) {
                    //red
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                } else if (obj.getParty().equalsIgnoreCase("democratic")) {
                    //blue
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                } else {
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorBlack));
                }
            }
            else{
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorBlack));
            }


            addressTextView.setText(obj.getAddress() != null ? obj.getAddress() : "No Data Provided");
            if(obj.getAddress() != null) Linkify.addLinks(addressTextView , Linkify.MAP_ADDRESSES);
            phoneTextView.setText(obj.getPhone() != null ? obj.getPhone() : "No Data Provided");
            if(obj.getPhone() != null) Linkify.addLinks(phoneTextView , Linkify.PHONE_NUMBERS);
            emailTextView.setText(obj.getEmail() != null ? obj.getEmail() : "No Data Provided");
            if(obj.getEmail() != null) Linkify.addLinks(emailTextView , Linkify.EMAIL_ADDRESSES);
            websiteTextView.setText(obj.getWebsite() != null ? obj.getWebsite() : "No Data Provided");
            if(obj.getWebsite() != null) Linkify.addLinks(websiteTextView , Linkify.WEB_URLS);

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

            //Social Links
            HashMap<String, String> socialLinks = obj.getChannels();
            if(socialLinks != null) {
                if (socialLinks.get("Twitter") != null) {
                    twitterId = socialLinks.get("Twitter");
                    tlink.setVisibility(View.VISIBLE);
                } else {
                    tlink.setVisibility(View.INVISIBLE);
                }
                if (socialLinks.get("Facebook") != null) {
                    facbookId = socialLinks.get("Facebook");
                    flink.setVisibility(View.VISIBLE);
                } else {
                    flink.setVisibility(View.INVISIBLE);
                }
                if (socialLinks.get("GooglePlus") != null) {
                    googlePlusId = socialLinks.get("GooglePlus");
                    glink.setVisibility(View.VISIBLE);
                } else {
                    glink.setVisibility(View.INVISIBLE);
                }
                if (socialLinks.get("YouTube") != null) {
                    youtubeId = socialLinks.get("YouTube");
                    ylink.setVisibility(View.VISIBLE);
                } else {
                    ylink.setVisibility(View.INVISIBLE);
                }
            }
            else{
                tlink.setVisibility(View.INVISIBLE);
                flink.setVisibility(View.INVISIBLE);
                glink.setVisibility(View.INVISIBLE);
                ylink.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void openPhotoActivity(View v){
        if (obj.getImageURL() != null) {
            //Call photoActivity and pass obj as extra, area as extra
            Intent intent = new Intent(OfficialActivity.this, PhotoActivity.class);
            intent.putExtra("location", area);
            intent.putExtra("officialData", obj);
            startActivity(intent);
        }
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = twitterId;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        catch (Exception e)
        {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }


    public void facebookClicked(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + facbookId;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try   {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana",
                    0      ).versionCode;
            if (versionCode >= 3002850)      {
                //newer versions of fb app
                 urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else{
                //older versions of fb app

                 urlToUse = "fb://page/" + facbookId;
            }
        }
        catch (PackageManager.NameNotFoundException e)   {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void googlePlusClicked(View v) {
        String name = googlePlusId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v){
        String name = youtubeId;
        Intent intent = null;
        try   {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)   {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }



}
