package modelo;

public class Ubicacion implements java.io.Serializable{
    private int idUbicacion;
    private String nombre;
    private int idEstatus;
    private String notas;

    public Ubicacion() {}
    public Ubicacion(int idUbicacion, String nombre, int idEstatus, String notas) {
        this.idUbicacion = idUbicacion;
        this.nombre = nombre;
        this.idEstatus = idEstatus;
        this.notas = notas;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }
    
}
