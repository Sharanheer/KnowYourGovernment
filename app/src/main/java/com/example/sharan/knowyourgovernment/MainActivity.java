package com.example.sharan.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;

    private ArrayList<Official> officialList;

    private OfficialAdapter officialAdapter;

    private Locator locator;

    private TextView areaTextView;

    private int pos;

    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        areaTextView = findViewById(R.id.areaTextView);

        officialList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        officialAdapter = new OfficialAdapter(this, officialList);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        for(int i=0; i<10; i++){
//            Official temp = new Official();
//            temp.setName("Politican "+ i);
//            temp.setOffice("Office "+ i);
//            temp.setParty("Party "+ i);
//            officialList.add(temp);
//        }

        if(doNetCheck())
            locator = new Locator(this);
        else{
            noConnectionDialog();
        }



    }

    @Override
    public void onClick(View view) {
//        Toast.makeText(this, "Single Click", Toast.LENGTH_SHORT).show();
        pos = recyclerView.getChildLayoutPosition(view);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("area", location);
        intent.putExtra("officialData", officialList.get(pos));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
//        Toast.makeText(this, "Double Click", Toast.LENGTH_SHORT).show();
        onClick(view);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
                return true;
            case R.id.search:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Create an edittext and set it to be the builder's view
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setTextColor(getResources().getColor(R.color.colorBlack));

                //To allow only characters and numbers
                InputFilter[] editFilters = et.getFilters();
                InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
                System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                newFilters[editFilters.length] =  new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                        if(charSequence.equals("")){ // for backspace
                            return charSequence;
                        }
                        if(charSequence.toString().matches("[a-zA-Z 0-9]+")){
                            return charSequence;
                        }
                        return "";
                    }
                };

                et.setFilters(newFilters);


                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);

                builder.setPositiveButton(Html.fromHtml("<font color='#000000'>OK</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Toast.makeText(MainActivity.this, "Searching...!", Toast.LENGTH_SHORT).show();
                        if(doNetCheck())
//                            doLocationName(et.getText().toString());
                            new GoogleCivicInformation(MainActivity.this).execute(et.getText().toString());
                        else{
                            noConnectionDialog();
                        }

                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#000000'>CANCEL</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setTitle(Html.fromHtml("<font color='#000000'>Enter a City, State or a Zip Code:</font>"));

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setData(double lat, double lon) {

        List<Address> addresses;
        for (int times = 0; times < 1; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {

                addresses = geocoder.getFromLocation(lat, lon, 1);

                displayAddresses(addresses);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 5) {

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();

                    }
                }
            }
        }

    }

//    public void doLocationName(String input) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
////        try {
//
//            new GoogleCivicInformation(this).execute(input);
////            List<Address> addresses;
//
////            addresses = geocoder.getFromLocationName(input, 1);
////            if(addresses != null && addresses.size() != 0 && addresses.get(0).getPostalCode() == null){
////                setData(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
////            }
////            else {
////                displayAddresses(addresses);
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////            Toast.makeText(this, "Please try again with a valid location", Toast.LENGTH_SHORT).show();
////        }
//    }

    private void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        if (addresses == null || addresses.size() == 0) {
            areaTextView.setText(R.string.nothing_found);
            officialList.removeAll(officialList);
            officialAdapter.notifyDataSetChanged();
            Toast.makeText(this, "No data available for this location", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Address ad : addresses) {

            String city = String.format("%s",
                    (ad.getLocality() == null ? "" : ad.getLocality()));

            String zip = String.format("%s",
                    (ad.getPostalCode() == null ? "" : ad.getPostalCode()));

            String a = zip == "" ? city : zip;

            if (!a.trim().isEmpty())
                sb.append(a.trim());

//            sb.append("\n");
        }
//        areaTextView.setText(sb.toString());
//        if(!sb.toString().isEmpty())
            //check network connectivity
            if(doNetCheck())
                new GoogleCivicInformation(this).execute(sb.toString());
            else{
                noConnectionDialog();
            }

//        else
//            Toast.makeText(this, "No data available for this location", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    public void updateOfficals(String loc, ArrayList<Official> offList) {
        location = loc;
        areaTextView.setText(location);
        officialList.removeAll(officialList);
        officialList.addAll(offList);
        officialAdapter.notifyDataSetChanged();
    }

    public void invalidInput(String message) {
        areaTextView.setText(R.string.nothing_found);
        officialList.removeAll(officialList);
        officialAdapter.notifyDataSetChanged();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void noConnectionDialog() {
        // Simple Ok & Cancel dialog - no view used.

        areaTextView.setText(R.string.nothing_found);
        officialList.removeAll(officialList);
        officialAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(Html.fromHtml("<font color='#000000'>No Network Connection</font>"));
        builder.setMessage(Html.fromHtml("<font color='#000000'>Data cannot be accessed/loaded without an internet connection.</font>"));


        AlertDialog dialog = builder.create();
        dialog.show();


    }

}
