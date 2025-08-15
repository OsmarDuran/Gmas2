package modelo;

import java.io.Serializable;

public class Equipo implements Serializable {
    private int idEquipo;       // <— corregido
    private int idTipo;
    private Integer idModelo;   // puede ser null en DB
    private String numeroSerie;
    private Integer idMarca;    // puede ser null
    private Integer idUbicacion;// puede ser null
    private int idEstatus;
    private String ipFija;
    private String puertoEthernet;
    private String notas;

    public Equipo() {}

    public Equipo(int idEquipo, int idTipo, Integer idModelo, String numeroSerie, Integer idMarca,
                  Integer idUbicacion, int idEstatus, String ipFija, String puertoEthernet, String notas) {
        this.idEquipo = idEquipo;
        this.idTipo = idTipo;
        this.idModelo = idModelo;
        this.numeroSerie = numeroSerie;
        this.idMarca = idMarca;
        this.idUbicacion = idUbicacion;
        this.idEstatus = idEstatus;
        this.ipFija = ipFija;
        this.puertoEthernet = puertoEthernet;
        this.notas = notas;
    }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public Integer getIdModelo() { return idModelo; }
    public void setIdModelo(Integer idModelo) { this.idModelo = idModelo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }

    public Integer getIdUbicacion() { return idUbicacion; }
    public void setIdUbicacion(Integer idUbicacion) { this.idUbicacion = idUbicacion; }

    public int getIdEstatus() { return idEstatus; }
    public void setIdEstatus(int idEstatus) { this.idEstatus = idEstatus; }

    public String getIpFija() { return ipFija; }
    public void setIpFija(String ipFija) { this.ipFija = ipFija; }

    public String getPuertoEthernet() { return puertoEthernet; }
    public void setPuertoEthernet(String puertoEthernet) { this.puertoEthernet = puertoEthernet; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
