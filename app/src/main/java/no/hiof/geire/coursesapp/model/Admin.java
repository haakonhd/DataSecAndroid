package no.hiof.geire.coursesapp.model;

public class Admin extends Person{

    private int idAdmin;

    public Admin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public Admin() {
    }


    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }
}
