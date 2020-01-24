package no.hiof.geire.coursesapp.model;

public class Student extends Person{

    private int idStudent;
    private String studieretning;
    private int kull;

    public Student(int idPerson, String epost, String navn, String passord, boolean godkjentBruker, int idStudent, String studieretning, int kull) {
        super(idPerson, epost, navn, passord, godkjentBruker);
        this.idStudent = idStudent;
        this.studieretning = studieretning;
        this.kull = kull;
    }

    public Student() {}

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public String getStudieretning() {
        return studieretning;
    }

    public void setStudieretning(String studieretning) {
        this.studieretning = studieretning;
    }

    public int getKull() {
        return kull;
    }

    public void setKull(int kull) {
        this.kull = kull;
    }
}
