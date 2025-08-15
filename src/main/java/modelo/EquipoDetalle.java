package modelo;

import java.io.Serializable;

public class EquipoDetalle implements Serializable {
    private int idEquipo;
    private String numeroSerie;

    private int idTipo;
    private String tipoNombre;

    private Integer idModelo;
    private String modeloNombre;

    private Integer idMarca;
    private String marcaNombre;

    private Integer idUbicacion;
    private String ubicacionNombre;

    private int idEstatus;
    private String estatusNombre; // de tabla estatus(tipo = 'EQUIPO')

    private String ipFija;
    private String puertoEthernet;
    private String notas;

    // Getters/setters…
    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
    public Integer getIdModelo() { return idModelo; }
    public void setIdModelo(Integer idModelo) { this.idModelo = idModelo; }
    public String getModeloNombre() { return modeloNombre; }
    public void setModeloNombre(String modeloNombre) { this.modeloNombre = modeloNombre; }
    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }
    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
    public Integer getIdUbicacion() { return idUbicacion; }
    public void setIdUbicacion(Integer idUbicacion) { this.idUbicacion = idUbicacion; }
    public String getUbicacionNombre() { return ubicacionNombre; }
    public void setUbicacionNombre(String ubicacionNombre) { this.ubicacionNombre = ubicacionNombre; }
    public int getIdEstatus() { return idEstatus; }
    public void setIdEstatus(int idEstatus) { this.idEstatus = idEstatus; }
    public String getEstatusNombre() { return estatusNombre; }
    public void setEstatusNombre(String estatusNombre) { this.estatusNombre = estatusNombre; }
    public String getIpFija() { return ipFija; }
    public void setIpFija(String ipFija) { this.ipFija = ipFija; }
    public String getPuertoEthernet() { return puertoEthernet; }
    public void setPuertoEthernet(String puertoEthernet) { this.puertoEthernet = puertoEthernet; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
