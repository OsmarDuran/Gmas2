package modelo;

public class EquipoConsumible implements java.io.Serializable{
    private int idEquipo;
    private int idColor;

    public EquipoConsumible() {}
    public EquipoConsumible(int idEquipo, int idColor) {
        this.idEquipo = idEquipo;
        this.idColor = idColor;
    }

    public int getIdEquipo() {
        return idEquipo;
    }
    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getIdColor() {
        return idColor;
    }
    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }
    @Override
    public String toString() {
        return "EquipoConsumible{" + "idEquipo=" + idEquipo + ", idColor=" + idColor + '}';
    }
}
