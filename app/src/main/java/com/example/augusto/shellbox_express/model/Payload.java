package com.example.augusto.shellbox_express.model;

public class Payload {

    String text;
    Object context;
    String lat;
    String lng;
    String output;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
