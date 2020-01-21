package no.hiof.geire.coursesapp.model;

public class PersonHarEmne {

    private int idPerson;
    private String emnekode;
    private boolean tilgangTilEmne;

    public PersonHarEmne(int idPerson, String emnekode, boolean tilgangTilEmne) {
        this.idPerson = idPerson;
        this.emnekode = emnekode;
        this.tilgangTilEmne = tilgangTilEmne;
    }

    public PersonHarEmne() {
    }


    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public String getEmnekode() {
        return emnekode;
    }

    public void setEmnekode(String emnekode) {
        this.emnekode = emnekode;
    }

    public boolean isTilgangTilEmne() {
        return tilgangTilEmne;
    }

    public void setTilgangTilEmne(boolean tilgangTilEmne) {
        this.tilgangTilEmne = tilgangTilEmne;
    }
}
