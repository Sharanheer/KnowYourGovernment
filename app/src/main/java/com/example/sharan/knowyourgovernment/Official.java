package com.example.sharan.knowyourgovernment;

import java.io.Serializable;
import java.util.HashMap;

public class Official implements Serializable {

    String office = null;
    String name = null;
    String party = null;
    String imageURL = null;
    String address = null;
    String phone = null;
    String email = null;
    String website = null;
    HashMap<String, String> channels;

    public Official() {
    }

    public Official(String office, String name, String party, String imageURL, String address, String phone, String email, String website) {
        this.office = office;
        this.name = name;
        this.party = party;
        this.imageURL = imageURL;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

    public void setChannels(HashMap<String, String> channels) {
        this.channels = channels;
    }
}
