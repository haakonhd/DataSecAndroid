package no.hiof.geire.coursesapp.model;

public class Studieretning {

    private String idStudieretning;
    private String studieretningNavn;

    public Studieretning(String idStudieretning, String studieretningNavn) {
        this.idStudieretning = idStudieretning;
        this.studieretningNavn = studieretningNavn;
    }

    public Studieretning() {
    }


    public String getIdStudieretning() {
        return idStudieretning;
    }

    public void setIdStudieretning(String idStudieretning) {
        this.idStudieretning = idStudieretning;
    }

    public String getStudieretningNavn() {
        return studieretningNavn;
    }

    public void setStudieretningNavn(String studieretningNavn) {
        this.studieretningNavn = studieretningNavn;
    }
}
