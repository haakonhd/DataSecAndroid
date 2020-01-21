package no.hiof.geire.coursesapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Emne {

    private String emnekode;
    private String emnenavn;
    private int pinNr;
    private int foreleser;

    public Emne(String emnekode, String emnenavn, int pinNr, int foreleser) {
        this.emnekode = emnekode;
        this.emnenavn = emnenavn;
        this.pinNr = pinNr;
        this.foreleser = foreleser;
    }

    public Emne() {
    }


    public String getEmnekode() {
        return emnekode;
    }

    public void setEmnekode(String emnekode) {
        this.emnekode = emnekode;
    }

    public String getEmnenavn() {
        return emnenavn;
    }

    public void setEmnenavn(String emnenavn) {
        this.emnenavn = emnenavn;
    }

    public int getPinNr() {
        return pinNr;
    }

    public void setPinNr(int pinNr) {
        this.pinNr = pinNr;
    }

    public int getForeleser() {
        return foreleser;
    }

    public void setForeleser(int foreleser) {
        this.foreleser = foreleser;
    }


    public ArrayList<Emne> getEmneArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        ArrayList<Emne> courses = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Emne course = new Emne(obj.getString("emnekode"), obj.getString("emnenavn"), obj.getInt("pinNr"), obj.getInt("foreleser"));
            courses.add(course);
        }

        return courses;
    }
}
