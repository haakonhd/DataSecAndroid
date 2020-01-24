package no.hiof.geire.coursesapp.model;

public class Foreleser extends Person{

    private int idForeleser;
    private String bildeURL;

    public Foreleser(int idPerson, String epost, String navn, String passord, boolean godkjentBruker, int idForeleser, String bildeURL) {
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
