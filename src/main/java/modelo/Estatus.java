package modelo;

public class Estatus implements java.io.Serializable{
    private int idEstatus;
    private String tipoEstatus;
    private String nombre;

    public Estatus() {}
    public Estatus(int idEstatus, String tipoEstatus, String nombre) {
        this.idEstatus = idEstatus;
        this.tipoEstatus = tipoEstatus;
        this.nombre = nombre;
    }

    public int getIdEstatus() {
        return idEstatus;
    }
    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }
    public String getTipoEstatus() {
        return tipoEstatus;
    }
    public void setTipoEstatus(String tipoEstatus) {
        this.tipoEstatus = tipoEstatus;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
