package modelo;

public class Marca implements java.io.Serializable{
    private int idMarca;
    private String nombre;
    private boolean activo;
    private String notas;

    public Marca() {}
    public Marca(int idMarca, String nombre, boolean activo, String notas) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.activo = activo;
        this.notas = notas;
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
