package datos;

import modelo.Estatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstatusDAO {

    // CREATE
    public int crear(Estatus e) {
        String sql = "INSERT INTO estatus (tipo_estatus, nombre) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getTipoEstatus());
            ps.setString(2, e.getNombre());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de estatus.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("El estatus ya existe para ese tipo.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear estatus", ex);
        }
    }

    // READ
    public Estatus obtenerPorId(int idEstatus) {
        String sql = "SELECT id_estatus, tipo_estatus, nombre FROM estatus WHERE id_estatus = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEstatus);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener estatus por id", ex);
        }
    }

    public List<Estatus> listarPorTipo(String tipoEstatus) {
        String sql = "SELECT id_estatus, tipo_estatus, nombre FROM estatus WHERE tipo_estatus = ? ORDER BY nombre ASC";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, tipoEstatus);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar estatus por tipo", ex);
        }
    }

    public List<Estatus> listarTodos() {
        String sql = "SELECT id_estatus, tipo_estatus, nombre FROM estatus ORDER BY tipo_estatus, nombre ASC";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return mapList(rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar todos los estatus", ex);
        }
    }

    // UPDATE
    public boolean actualizar(Estatus e) {
        String sql = "UPDATE estatus SET tipo_estatus = ?, nombre = ? WHERE id_estatus = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getTipoEstatus());
            ps.setString(2, e.getNombre());
            ps.setInt(3, e.getIdEstatus());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe un estatus con ese nombre para el tipo indicado.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar estatus", ex);
        }
    }

    // DELETE
    public boolean eliminar(int idEstatus) {
        String sql = "DELETE FROM estatus WHERE id_estatus = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEstatus);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar estatus", ex);
        }
    }

    // Helpers
    private Estatus mapRow(ResultSet rs) throws SQLException {
        Estatus e = new Estatus();
        e.setIdEstatus(rs.getInt("id_estatus"));
        e.setTipoEstatus(rs.getString("tipo_estatus")); // ENUM leído como String
        e.setNombre(rs.getString("nombre"));
        return e;
    }

    private List<Estatus> mapList(ResultSet rs) throws SQLException {
        List<Estatus> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
    public String obtenerNombrePorId(Integer idEstatus) {
        if (idEstatus == null) return null;
        final String sql = "SELECT nombre FROM estatus WHERE id_estatus = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEstatus);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("nombre") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener nombre de estatus por id", ex);
        }
    }

}
