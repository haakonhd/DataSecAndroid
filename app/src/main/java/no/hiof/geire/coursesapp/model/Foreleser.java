package no.hiof.geire.coursesapp.model;

import java.io.Serializable;

public class Foreleser extends Person implements Serializable {

    private int idForeleser;
    private String bildeURL;

    public Foreleser(String idPerson, String epost, String navn, String passord, boolean godkjentBruker, int idForeleser, String bildeURL) {
        super(idPerson, epost, navn, passord, godkjentBruker);
        this.idForeleser = idForeleser;
        this.bildeURL = bildeURL;
    }

    public Foreleser() { }

    public int getIdForeleser() {
        return idForeleser;
    }

    public void setIdForeleser(int idForeleser) {
        this.idForeleser = idForeleser;
    }

    public String getBildeURL() {
        return bildeURL;
    }

    public void setBildeURL(String bildeURL) {
        this.bildeURL = bildeURL;
    }
}
