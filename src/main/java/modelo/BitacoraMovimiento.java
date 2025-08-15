package modelo;


import java.time.LocalDateTime;

public class BitacoraMovimiento implements java.io.Serializable{
    private int idMovimiento;
    private int idEquipo;
    private int idUsuario;
    private String accion;
    private int estatusOrigen;
    private int estatusDestino;
    private int realizadoPor;
    private LocalDateTime realizadoEn;
    private String notas;

    public BitacoraMovimiento() {}

    public BitacoraMovimiento(int idMovimiento, int idEquipo, int idUsuario, String accion, int estatusOrigen, int estatusDestino, int realizadoPor, LocalDateTime realizadoEn, String notas) {
        this.idMovimiento = idMovimiento;
        this.idEquipo = idEquipo;
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.estatusOrigen = estatusOrigen;
        this.estatusDestino = estatusDestino;
        this.realizadoPor = realizadoPor;
        this.realizadoEn = realizadoEn;
        this.notas = notas;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public int getEstatusOrigen() {
        return estatusOrigen;
    }

    public void setEstatusOrigen(int estatusOrigen) {
        this.estatusOrigen = estatusOrigen;
    }

    public int getEstatusDestino() {
        return estatusDestino;
    }

    public void setEstatusDestino(int estatusDestino) {
        this.estatusDestino = estatusDestino;
    }

    public int getRealizadoPor() {
        return realizadoPor;
    }

    public void setRealizadoPor(int realizadoPor) {
        this.realizadoPor = realizadoPor;
    }

    public LocalDateTime getRealizadoEn() {
        return realizadoEn;
    }

    public void setRealizadoEn(LocalDateTime realizadoEn) {
        this.realizadoEn = realizadoEn;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

}
