package modelo;

public class Centro implements java.io.Serializable{
    private int idCentro;
    private String nombre;
    private int idUbicacion;
    private String notas;

    public Centro() {}

    public Centro(int idCentro, String nombre, int idUbicacion, String notas) {
        this.idCentro = idCentro;
        this.nombre = nombre;
        this.idUbicacion = idUbicacion;
        this.notas = notas;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(int idCentro) {
        this.idCentro = idCentro;
    }
}
