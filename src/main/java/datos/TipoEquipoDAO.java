package datos;

import modelo.TipoEquipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(TipoEquipo t) {
        String sql = "INSERT INTO tipo_equipo (id_tipo, nombre) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, t.getIdTipo());     // NO auto-increment
            ps.setString(2, t.getNombre());
            ps.executeUpdate();
            return t.getIdTipo();
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Puede ser PK duplicada o UNIQUE(nombre)
            throw new IllegalArgumentException("ID o nombre de tipo de equipo ya existen.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear tipo de equipo", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public TipoEquipo obtenerPorId(int idTipo) {
        String sql = "SELECT id_tipo, nombre FROM tipo_equipo WHERE id_tipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener tipo de equipo por id", ex);
        }
    }

    public TipoEquipo obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_tipo, nombre FROM tipo_equipo WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener tipo de equipo por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<TipoEquipo> listarTodos(int limit, int offset) {
        String sql = "SELECT id_tipo, nombre FROM tipo_equipo ORDER BY id_tipo ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar tipos de equipo", ex);
        }
    }

    public List<TipoEquipo> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_tipo, nombre FROM tipo_equipo WHERE nombre LIKE ? ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar tipos de equipo", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(TipoEquipo t) {
        String sql = "UPDATE tipo_equipo SET nombre = ? WHERE id_tipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setInt(2, t.getIdTipo());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe un tipo de equipo con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar tipo de equipo", ex);
        }
    }

    // =========================
    // DELETE (cuidado con FKs desde equipo)
    // =========================
    public boolean eliminar(int idTipo) {
        String sql = "DELETE FROM tipo_equipo WHERE id_tipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalStateException("No se puede eliminar: hay equipos que usan este tipo. Considera mantener catálogo cerrado.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar tipo de equipo", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM tipo_equipo";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar tipos de equipo", ex);
        }
    }

    public boolean existeNombre(String nombre, Integer excluirIdTipo) {
        String sql = "SELECT COUNT(*) FROM tipo_equipo WHERE nombre = ? " +
                (excluirIdTipo != null ? "AND id_tipo <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            if (excluirIdTipo != null) ps.setInt(2, excluirIdTipo);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de tipo de equipo", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private TipoEquipo mapRow(ResultSet rs) throws SQLException {
        TipoEquipo t = new TipoEquipo();
        t.setIdTipo(rs.getInt("id_tipo"));
        t.setNombre(rs.getString("nombre"));
        return t;
    }

    private List<TipoEquipo> mapList(ResultSet rs) throws SQLException {
        List<TipoEquipo> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
