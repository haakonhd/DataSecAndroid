package no.hiof.geire.coursesapp.model;

public class Melding {

    private int idMelding;
    private String innhold_melding;
    private int idForfatter;
    private String emnekode;
    private boolean rappotert;
    private int foreleser;
    private String innholdSvar;

    public Melding(int idMelding, String innhold_melding, int idForfatter, String emnekode, boolean rappotert, int foreleser, String innholdSvar) {
        this.idMelding = idMelding;
        this.innhold_melding = innhold_melding;
        this.idForfatter = idForfatter;
        this.emnekode = emnekode;
        this.rappotert = rappotert;
        this.foreleser = foreleser;
        this.innholdSvar = innholdSvar;
    }

    public Melding() {
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

    public int getForeleser() {
        return foreleser;
    }

    public void setForeleser(int foreleser) {
        this.foreleser = foreleser;
    }

    public String getInnholdSvar() {
        return innholdSvar;
    }

    public void setInnholdSvar(String innholdSvar) {
        this.innholdSvar = innholdSvar;
    }
}
