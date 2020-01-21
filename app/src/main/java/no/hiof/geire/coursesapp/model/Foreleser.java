package no.hiof.geire.coursesapp.model;

public class Foreleser extends Person{

    private String bildeURL;

    public Foreleser(int idPerson, String epost, String navn, String passord, boolean godkjentBruker, String bildeURL) {
        super(idPerson, epost, navn, passord, godkjentBruker);
        this.bildeURL = bildeURL;
    }

    //public Foreleser() { }


    public String getBildeURL() {
        return bildeURL;
    }

    public void setBildeURL(String bildeURL) {
        this.bildeURL = bildeURL;
    }
}
