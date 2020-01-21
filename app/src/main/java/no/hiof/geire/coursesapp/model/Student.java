package no.hiof.geire.coursesapp.model;

public class Student extends Person{

    private int studieretning;
    private int kull;

    public Student(int idPerson, String epost, String navn, String passord, boolean godkjentBruker, int studieretning, int kull) {
        super(idPerson, epost, navn, passord, godkjentBruker);
        this.studieretning = studieretning;
        this.kull = kull;
    }

    //public Student() {}

    public int getStudieretning() {
        return studieretning;
    }

    public void setStudieretning(int studieretning) {
        this.studieretning = studieretning;
    }

    public int getKull() {
        return kull;
    }

    public void setKull(int kull) {
        this.kull = kull;
    }
}
