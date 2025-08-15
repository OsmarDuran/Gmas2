package modelo;

public class EquipoSim implements java.io.Serializable{
    private int idEquipo;
    private String numeroAsignado;
    private String imei;

    public EquipoSim() {}
    public EquipoSim(int idEquipo, String numeroAsignado, String imei) {
        this.idEquipo = idEquipo;
        this.numeroAsignado = numeroAsignado;
        this.imei = imei;
    }

    public int getIdEquipo() {
        return idEquipo;
    }
    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }
    public String getNumeroAsignado() {
        return numeroAsignado;
    }
    public void setNumeroAsignado(String numeroAsignado) {
        this.numeroAsignado = numeroAsignado;
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
}
