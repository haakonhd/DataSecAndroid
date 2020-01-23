package no.hiof.geire.coursesapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Emne {

    private String emnekode;
    private String emnenavn;
    private int PIN;
    private int foreleser;

    public Emne(String emnekode, String emnenavn, int PIN, int foreleser) {
        this.emnekode = emnekode;
        this.emnenavn = emnenavn;
        this.PIN = PIN;
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
        return PIN;
    }

    public void setPinNr(int PIN) {
        this.PIN = PIN;
    }

    public int getForeleser() {
        return foreleser;
    }

    public void setForeleser(int foreleser) {
        this.foreleser = foreleser;
    }

}
