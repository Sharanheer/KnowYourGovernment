package com.example.sharan.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoogleCivicInformation extends AsyncTask<String, Void, String> {

//    private String API_KEY = "AIzaSyBc2xRiM6APw3xrn9y5rALtmsLulA8r4Zg";
    private String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBc2xRiM6APw3xrn9y5rALtmsLulA8r4Zg&address=";

    private MainActivity mainActivity;

    public GoogleCivicInformation(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        //connect to the api and parse the json array
        String API_URL = DATA_URL + strings[0];
//        System.out.println("API_URL: "+ API_URL);
        Uri dataUri = Uri.parse(API_URL);
        String urlToUse = dataUri.toString();
        int responseCode = 0;

        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            responseCode = conn.getResponseCode();
            if(responseCode == 400){
                //send an empty string
                return "";
            }

            InputStream is = conn.getInputStream();

//          System.out.print("test");
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {

            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        // This method is almost always used - handles the results of doInBackground
        if(s != null && !s.isEmpty()) {
            super.onPostExecute(s);

            ArrayList<Official> officialsList = parseJSON(s);
            String location = null;

            try {
                JSONObject jObjMain = new JSONObject(s);
                JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");
                String city = normalizedInput.getString("city");
                String state = normalizedInput.getString("state");
                String zip = normalizedInput.getString("zip");
                if(city != null && state != null)
                    location = city + ", " + state + " " + zip;
                else if(city != null)
                    location = city + " " + zip;
                else if(state != null)
                    location = state + " " + zip;
            } catch (Exception e) {
                e.printStackTrace();
            }


            mainActivity.updateOfficals(location, officialsList);
        }
        else{
            if(s == null){
                mainActivity.invalidInput("Civic Info not available");
            }
            else if(s.isEmpty()){
                mainActivity.invalidInput("No Data is available for the specified location");
            }
        }

    }

    private ArrayList<Official> parseJSON(String s) {

        ArrayList<Official> officialList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONArray officesArr = jObjMain.getJSONArray("offices");
            JSONArray officialsArr = jObjMain.getJSONArray("officials");

            for(int i=0; i< officesArr.length(); i++){
                JSONObject offObj = officesArr.getJSONObject(i);
                String officeName = offObj.getString("name");
                JSONArray index = offObj.getJSONArray("officialIndices");
                for(int j=0; j< index.length(); j++){
                    Official newOffObj = new Official();
                    newOffObj.setOffice(officeName);
                    int officialsIndex = index.getInt(j);
                    //Fetch details from the officials array
                    JSONObject officialsObj = officialsArr.getJSONObject(officialsIndex);
                    if(officialsObj.has("name")){
                        newOffObj.setName(officialsObj.getString("name"));
                    }
                    if(officialsObj.has("party")){
                        newOffObj.setParty(officialsObj.getString("party"));
                    }


                    if(officialsObj.has("photoUrl"))
                        newOffObj.setImageURL(officialsObj.getString("photoUrl"));

                    //Parsing address

                    if(officialsObj.has("address")) {

                        JSONObject addressObj = officialsObj.getJSONArray("address").getJSONObject(0);
                        String line1 = "";
                        String line2 = "";
                        String line3 = "";
                        String city = "";
                        String state = "";
                        String zip = "";
                        String newOffObjAddress = "";
                        if(addressObj.has("line1")){
                            line1 = addressObj.getString("line1");
                            newOffObjAddress += line1;
                        }
                        if(addressObj.has("line2")) {
                            line2 = addressObj.getString("line2");
                            newOffObjAddress += ", " +line2;
                        }
                        if(addressObj.has("line3")){
                            line3 = addressObj.getString("line3");
                            newOffObjAddress += ", " +line3;
                        }
                        if(addressObj.has("city")){
                            city = addressObj.getString("city");
                            newOffObjAddress += ", " +city;
                        }
                        if(addressObj.has("state")){
                            state = addressObj.getString("state");
                            newOffObjAddress += ", " +state;
                        }
                        if(addressObj.has("zip")){
                            zip = addressObj.getString("zip");
                            newOffObjAddress += ", " +zip;
                        }

                        newOffObj.setAddress(newOffObjAddress);
                    }

                    //Parsing phone number
                    if(officialsObj.has("phones"))
                        newOffObj.setPhone(officialsObj.getJSONArray("phones").getString(0));

                    //Parsing email address
                    if(officialsObj.has("emails"))
                        newOffObj.setEmail(officialsObj.getJSONArray("emails").getString(0));

                    //Parsing website
                    if(officialsObj.has("urls"))
                        newOffObj.setWebsite(officialsObj.getJSONArray("urls").getString(0));

                    //parsing Social Media channels
                    if(officialsObj.has("channels")){
                        JSONArray channelsArr = officialsObj.getJSONArray("channels");
                        HashMap<String, String> offChannels = new HashMap<>();
                        for(int k=0; k< channelsArr.length(); k++){
                            JSONObject channelsObj = channelsArr.getJSONObject(k);
                            String type = channelsObj.getString("type");
                            String id = channelsObj.getString("id");
                            offChannels.put(type,id);
                        }
                        newOffObj.setChannels(offChannels);
                    }


                    //add the newOffObj to the officialsList
                    officialList.add(newOffObj);
                }
            }

            return officialList;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }


}
