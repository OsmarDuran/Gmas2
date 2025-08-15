package modelo;

public class Puesto implements java.io.Serializable{
    private int idPuesto;
    private String nombre;
    private String notas;

    public Puesto() {}
    public Puesto(int idPuesto, String nombre, String notas) {
        this.idPuesto = idPuesto;
        this.nombre = nombre;
        this.notas = notas;
    }

    public int getIdPuesto() {
        return idPuesto;
    }
    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNotas() {
        return notas;
    }
    public void setNotas(String notas) {
        this.notas = notas;
    }

}
