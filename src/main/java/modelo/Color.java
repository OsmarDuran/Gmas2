package modelo;

public class Color implements java.io.Serializable {
    private int idColor;
    private String nombre;
    private String hex; // Nuevo campo para código HEX (#FFFFFF, etc.)

    public Color() {}

    public Color(int idColor, String nombre, String hex) {
        this.idColor = idColor;
        this.nombre = nombre;
        this.hex = hex;
    }

    public int getIdColor() {
        return idColor;
    }
    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHex() {
        return hex;
    }
    public void setHex(String hex) {
        this.hex = hex;
    }
}
