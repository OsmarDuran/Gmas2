package modelo;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Asignacion implements Serializable {
    private int idAsignacion;
    private int idEquipo;
    private int idUsuario;
    private int asignadoPor;
    private LocalDateTime asignadoEn;
    private LocalDateTime devueltoEn;
    private String rutaPdf;

    // ---- Opcionales para UI (rellenar vía JOIN en el DAO) ----
    private String usuarioNombre;       // nombre completo del asignado
    private String asignadorNombre;     // nombre completo de quien asigna
    private String equipoNumeroSerie;   // e.g., "DELL-LAT5420-0001"
    private String equipoDescripcion;   // modelo/marca resumido
    private Integer equipoEstatusId;    // estatus actual del equipo (si haces JOIN)
    private String equipoEstatusNombre;

    public Asignacion() {}

    // ---------- Getters/Setters ----------
    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getAsignadoPor() { return asignadoPor; }
    public void setAsignadoPor(int asignadoPor) { this.asignadoPor = asignadoPor; }

    public LocalDateTime getAsignadoEn() { return asignadoEn; }
    public void setAsignadoEn(LocalDateTime asignadoEn) { this.asignadoEn = asignadoEn; }

    public LocalDateTime getDevueltoEn() { return devueltoEn; }
    public void setDevueltoEn(LocalDateTime devueltoEn) { this.devueltoEn = devueltoEn; }

    public String getRutaPdf() { return rutaPdf; }
    public void setRutaPdf(String rutaPdf) { this.rutaPdf = rutaPdf; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getAsignadorNombre() { return asignadorNombre; }
    public void setAsignadorNombre(String asignadorNombre) { this.asignadorNombre = asignadorNombre; }

    public String getEquipoNumeroSerie() { return equipoNumeroSerie; }
    public void setEquipoNumeroSerie(String equipoNumeroSerie) { this.equipoNumeroSerie = equipoNumeroSerie; }

    public String getEquipoDescripcion() { return equipoDescripcion; }
    public void setEquipoDescripcion(String equipoDescripcion) { this.equipoDescripcion = equipoDescripcion; }

    public Integer getEquipoEstatusId() { return equipoEstatusId; }
    public void setEquipoEstatusId(Integer equipoEstatusId) { this.equipoEstatusId = equipoEstatusId; }

    public String getEquipoEstatusNombre() { return equipoEstatusNombre; }
    public void setEquipoEstatusNombre(String equipoEstatusNombre) { this.equipoEstatusNombre = equipoEstatusNombre; }

    // ---------- Helpers ----------
    /** true si la asignación sigue activa (no se ha devuelto). */
    public boolean isActiva() {
        return devueltoEn == null;
    }

    /** Duración de la asignación hasta ahora (si activa) o total (si devuelta). */
    public Duration getDuracion() {
        if (asignadoEn == null) return Duration.ZERO;
        LocalDateTime fin = (devueltoEn != null) ? devueltoEn : LocalDateTime.now();
        return Duration.between(asignadoEn, fin);
    }

    /** Días (redondeo hacia abajo) de la asignación. */
    public long getDuracionDias() {
        return getDuracion().toDays();
    }

    @Override
    public String toString() {
        return "Asignacion{" +
                "idAsignacion=" + idAsignacion +
                ", idEquipo=" + idEquipo +
                ", idUsuario=" + idUsuario +
                ", asignadoPor=" + asignadoPor +
                ", asignadoEn=" + asignadoEn +
                ", devueltoEn=" + devueltoEn +
                ", rutaPdf='" + rutaPdf + '\'' +
                ", activa=" + isActiva() +
                '}';
    }
}
