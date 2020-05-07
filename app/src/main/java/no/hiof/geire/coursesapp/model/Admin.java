package no.hiof.geire.coursesapp.model;

public class Admin extends Person{

    private String idAdmin;

    public Admin(String idAdmin) {
        this.idAdmin = idAdmin;
    }

    public Admin() {
    }


    public String getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }
}
