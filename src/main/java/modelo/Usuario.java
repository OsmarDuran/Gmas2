package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {
    private int idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
    private int idLider;      // FK a tabla lider
    private int idPuesto;     // FK a tabla puesto
    private int idCentro;     // FK a tabla centro
    private int idRol;        // FK a tabla rol
    private String hashPassword;
    private boolean activo;
    private LocalDateTime ultimoLogin;
    private LocalDateTime creadoEn;

    // Campos opcionales para mostrar datos relacionados sin más consultas
    private String nombreCompleto;   // nombre + apellidos
    private String nombreRol;
    private String nombrePuesto;
    private String nombreCentro;
    private String nombreLider;

    public Usuario() {}

    // ========================
    // Getters & Setters
    // ========================
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
        updateNombreCompleto();
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }
    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
        updateNombreCompleto();
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
        updateNombreCompleto();
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdLider() {
        return idLider;
    }
    public void setIdLider(int idLider) {
        this.idLider = idLider;
    }

    public int getIdPuesto() {
        return idPuesto;
    }
    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }

    public int getIdCentro() {
        return idCentro;
    }
    public void setIdCentro(int idCentro) {
        this.idCentro = idCentro;
    }

    public int getIdRol() {
        return idRol;
    }
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getHashPassword() {
        return hashPassword;
    }
    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }
    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
    public String getNombreRol() {
        return nombreRol;
    }
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    public String getNombrePuesto() {
        return nombrePuesto;
    }
    public void setNombrePuesto(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }
    public String getNombreCentro() {
        return nombreCentro;
    }
    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }
    public String getNombreLider() {
        return nombreLider;
    }
    public void setNombreLider(String nombreLider) {
        this.nombreLider = nombreLider;
    }

    // ========================
    // Métodos utilitarios
    // ========================
    private void updateNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (nombre != null) sb.append(nombre).append(" ");
        if (apellidoPaterno != null) sb.append(apellidoPaterno).append(" ");
        if (apellidoMaterno != null) sb.append(apellidoMaterno);
        this.nombreCompleto = sb.toString().trim();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", idRol=" + idRol +
                ", activo=" + activo +
                '}';
    }
}
