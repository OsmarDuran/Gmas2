package datos;

import modelo.Lider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LiderDAO {

    // CREATE
    public int crear(Lider l) {
        String sql = "INSERT INTO lider (nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, l.getNombre());
            ps.setString(2, l.getApellidoPaterno());
            ps.setString(3, l.getApellidoMaterno());
            ps.setString(4, l.getEmail());
            ps.setString(5, l.getTelefono());
            ps.setInt(6, l.getIdCentro());
            ps.setInt(7, l.getIdPuesto());
            ps.setInt(8, l.getIdEstatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de líder.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("El email ya está registrado.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear líder", ex);
        }
    }

    // READ
    public Lider obtenerPorId(int idLider) {
        String sql = "SELECT id_lider, nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus " +
                "FROM lider WHERE id_lider = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idLider);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener líder por id", ex);
        }
    }

    public Lider obtenerPorEmail(String email) {
        String sql = "SELECT id_lider, nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus " +
                "FROM lider WHERE email = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener líder por email", ex);
        }
    }

    // LISTAR
    public List<Lider> listarTodos(int limit, int offset) {
        String sql = "SELECT id_lider, nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus " +
                "FROM lider ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar líderes", ex);
        }
    }

    public List<Lider> buscarPorNombre(String query, int limit, int offset) {
        String sql = "SELECT id_lider, nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus " +
                "FROM lider WHERE nombre LIKE ? OR apellido_paterno LIKE ? OR apellido_materno LIKE ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        String like = "%" + query + "%";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar líderes", ex);
        }
    }

    // UPDATE
    public boolean actualizar(Lider l) {
        String sql = "UPDATE lider SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, email = ?, telefono = ?, id_centro = ?, id_puesto = ?, id_estatus = ? " +
                "WHERE id_lider = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, l.getNombre());
            ps.setString(2, l.getApellidoPaterno());
            ps.setString(3, l.getApellidoMaterno());
            ps.setString(4, l.getEmail());
            ps.setString(5, l.getTelefono());
            ps.setInt(6, l.getIdCentro());
            ps.setInt(7, l.getIdPuesto());
            ps.setInt(8, l.getIdEstatus());
            ps.setInt(9, l.getIdLider());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("El email ya está registrado.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar líder", ex);
        }
    }

    // DELETE
    public boolean eliminar(int idLider) {
        String sql = "DELETE FROM lider WHERE id_lider = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idLider);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar líder", ex);
        }
    }

    // Helpers
    private Lider mapRow(ResultSet rs) throws SQLException {
        Lider l = new Lider();
        l.setIdLider(rs.getInt("id_lider"));
        l.setNombre(rs.getString("nombre"));
        l.setApellidoPaterno(rs.getString("apellido_paterno"));
        l.setApellidoMaterno(rs.getString("apellido_materno"));
        l.setEmail(rs.getString("email"));
        l.setTelefono(rs.getString("telefono"));
        l.setIdCentro(rs.getInt("id_centro"));
        l.setIdPuesto(rs.getInt("id_puesto"));
        l.setIdEstatus(rs.getInt("id_estatus"));
        return l;
    }

    private List<Lider> mapList(ResultSet rs) throws SQLException {
        List<Lider> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
