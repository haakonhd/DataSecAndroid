package no.hiof.geire.coursesapp.model;

public class Melding {

    private int idMelding;
    private String innhold_melding;
    private int idForfatter;
    private String emnekode;
    private boolean rappotert;
    private int idForeleser;
    private String innhold_svar;
    private int rapportert_av;

    public Melding(String innhold_melding, int idForfatter, String emnekode, boolean rappotert, int idForeleser, String innhold_svar) {
        this.innhold_melding = innhold_melding;
        this.idForfatter = idForfatter;
        this.emnekode = emnekode;
        this.rappotert = rappotert;
        this.idForeleser = idForeleser;
        this.innhold_svar = innhold_svar;
    }

    public Melding() {
    }

    public Melding(int idMelding, String innhold_melding, int idForfatter, String emnekode, boolean rappotert, Integer idForeleser, String innhold_svar, int rapportert_av) {
        this.idMelding = idMelding;
        this.innhold_melding = innhold_melding;
        this.idForfatter = idForfatter;
        this.emnekode = emnekode;
        this.rappotert = rappotert;
        this.idForeleser = idForeleser;
        this.innhold_svar = innhold_svar;
        this.rapportert_av = rapportert_av;
    }

    public int getIdMelding() {
        return idMelding;
    }

    public void setIdMelding(int idMelding) {
        this.idMelding = idMelding;
    }

    public String getInnhold_melding() {
        return innhold_melding;
    }

    public void setInnhold_melding(String innhold_melding) {
        this.innhold_melding = innhold_melding;
    }

    public int getIdForfatter() {
        return idForfatter;
    }

    public void setIdForfatter(int idForfatter) {
        this.idForfatter = idForfatter;
    }

    public String getEmnekode() {
        return emnekode;
    }

    public void setEmnekode(String emnekode) {
        this.emnekode = emnekode;
    }

    public boolean isRappotert() {
        return rappotert;
    }

    public void setRappotert(boolean rappotert) {
        this.rappotert = rappotert;
    }

    public int getIdForeleser() {
        return idForeleser;
    }

    public void setIdForeleser(int idForeleser) {
        this.idForeleser = idForeleser;
    }

    public String getInnhold_svar() {
        return innhold_svar;
    }

    public void setInnhold_svar(String innholdSvar) {
        this.innhold_svar = innhold_svar;
    }

    public int getRapportert_av() {
        return rapportert_av;
    }

    public void setRapportert_av(int rapportert_av) {
        this.rapportert_av = rapportert_av;
    }
}
