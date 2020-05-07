package no.hiof.geire.coursesapp.model;

import java.io.Serializable;

public class Person implements Serializable {

    private String idPerson;
    private String epost;
    private String navn;
    private String passord;
    private boolean godkjentBruker;

    public Person(String idPerson, String epost, String navn, String passord, boolean godkjentBruker) {
        this.idPerson = idPerson;
        this.epost = epost;
        this.navn = navn;
        this.passord = passord;
        this.godkjentBruker = godkjentBruker;
    }

    public Person() {
    }


    public String getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(String idPerson) {
        this.idPerson = idPerson;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getPassord() {
        return passord;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }

    public boolean isGodkjentBruker() {
        return godkjentBruker;
    }

    public void setGodkjentBruker(boolean godkjentBruker) {
        this.godkjentBruker = godkjentBruker;
    }
}
