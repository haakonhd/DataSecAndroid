package no.hiof.geire.coursesapp.model;

public class Studieretning {

    private int idStudieretning;
    private String studieretningNavn;

    public Studieretning(int idStudieretning, String studieretningNavn) {
        this.idStudieretning = idStudieretning;
        this.studieretningNavn = studieretningNavn;
    }

    public Studieretning() {
    }


    public int getIdStudieretning() {
        return idStudieretning;
    }

    public void setIdStudieretning(int idStudieretning) {
        this.idStudieretning = idStudieretning;
    }

    public String getStudieretningNavn() {
        return studieretningNavn;
    }

    public void setStudieretningNavn(String studieretningNavn) {
        this.studieretningNavn = studieretningNavn;
    }
}
