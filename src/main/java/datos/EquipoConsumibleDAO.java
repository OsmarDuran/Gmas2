package datos;

import modelo.EquipoConsumible;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoConsumibleDAO {

    // =========================
    // CREATE
    // =========================
    /** Crea el registro de subtipo consumible para un equipo dado. */
    public boolean crear(EquipoConsumible ec) {
        String sql = "INSERT INTO equipo_consumible (id_equipo, id_color) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, ec.getIdEquipo());
            ps.setInt(2, ec.getIdColor());
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dupOrFk) {
            // Puede ser PK (id_equipo ya tiene consumible) o FK (equipo / color inexistente)
            throw new IllegalArgumentException("No se pudo crear consumible: ya existe para el equipo o referencias inválidas.", dupOrFk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear equipo_consumible", ex);
        }
    }

    // =========================
    // READ
    // =========================
    /** Obtiene el consumible por id_equipo (PK). */
    public EquipoConsumible obtenerPorIdEquipo(int idEquipo) {
        String sql = "SELECT id_equipo, id_color FROM equipo_consumible WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener equipo_consumible por id_equipo", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<EquipoConsumible> listarTodos(int limit, int offset) {
        String sql = "SELECT id_equipo, id_color FROM equipo_consumible ORDER BY id_equipo DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar equipo_consumible", ex);
        }
    }

    public List<EquipoConsumible> listarPorColor(int idColor, int limit, int offset) {
        String sql = "SELECT id_equipo, id_color FROM equipo_consumible " +
                "WHERE id_color = ? ORDER BY id_equipo DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idColor);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar equipo_consumible por color", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    /** Cambia el color del consumible de un equipo. */
    public boolean actualizarColor(int idEquipo, int idColor) {
        String sql = "UPDATE equipo_consumible SET id_color = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idColor);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalArgumentException("Color inexistente o equipo no válido.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar color de equipo_consumible", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM equipo_consumible WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar equipo_consumible", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM equipo_consumible";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar equipo_consumible", ex);
        }
    }

    public int contarPorColor(int idColor) {
        String sql = "SELECT COUNT(*) FROM equipo_consumible WHERE id_color = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idColor);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar equipo_consumible por color", ex);
        }
    }

    /** Verifica si ya existe registro de consumible para un equipo. */
    public boolean existeParaEquipo(int idEquipo) {
        String sql = "SELECT COUNT(*) FROM equipo_consumible WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar existencia de equipo_consumible", ex);
        }
    }

    // =========================
    // Helpers de mapeo
    // =========================
    private EquipoConsumible mapRow(ResultSet rs) throws SQLException {
        EquipoConsumible ec = new EquipoConsumible();
        ec.setIdEquipo(rs.getInt("id_equipo"));
        ec.setIdColor(rs.getInt("id_color"));
        return ec;
    }

    private List<EquipoConsumible> mapList(ResultSet rs) throws SQLException {
        List<EquipoConsumible> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
