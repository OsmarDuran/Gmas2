package modelo;

public class Modelo implements java.io.Serializable{
    private int idModelo;
    private int idMarca;
    private String nombre;
    private boolean activo;
    private String notas;

    public Modelo() {}
    public Modelo(int idModelo, int idMarca, String nombre, boolean activo, String notas) {
        this.idModelo = idModelo;
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.activo = activo;
        this.notas = notas;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
