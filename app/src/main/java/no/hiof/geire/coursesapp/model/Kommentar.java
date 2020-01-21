package no.hiof.geire.coursesapp.model;

public class Kommentar {

    private int idKommentar;
    private String innhold;
    private int idMelding_kommentert;
    private int idForfatter;

    public Kommentar(int idKommentar, String innhold, int idMelding_kommentert, int idForfatter) {
        this.idKommentar = idKommentar;
        this.innhold = innhold;
        this.idMelding_kommentert = idMelding_kommentert;
        this.idForfatter = idForfatter;
    }

    public Kommentar() {
    }

    public int getIdKommentar() {
        return idKommentar;
    }

    public void setIdKommentar(int idKommentar) {
        this.idKommentar = idKommentar;
    }

    public String getInnhold() {
        return innhold;
    }

    public void setInnhold(String innhold) {
        this.innhold = innhold;
    }

    public int getIdMelding_kommentert() {
        return idMelding_kommentert;
    }

    public void setIdMelding_kommentert(int idMelding_kommentert) {
        this.idMelding_kommentert = idMelding_kommentert;
    }

    public int getIdForfatter() {
        return idForfatter;
    }

    public void setIdForfatter(int idForfatter) {
        this.idForfatter = idForfatter;
    }
}

