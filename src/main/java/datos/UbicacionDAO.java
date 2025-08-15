package datos;

import modelo.Ubicacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UbicacionDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Ubicacion u) {
        String sql = "INSERT INTO ubicacion (nombre, id_estatus, notas) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getIdEstatus());
            ps.setString(3, u.getNotas());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de ubicación.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Puede ser por UNIQUE(nombre) o FK id_estatus inválido
            throw new IllegalArgumentException("Nombre de ubicación duplicado o estatus inexistente.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear ubicación", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Ubicacion obtenerPorId(int idUbicacion) {
        String sql = "SELECT id_ubicacion, nombre, id_estatus, notas FROM ubicacion WHERE id_ubicacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUbicacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener ubicación por id", ex);
        }
    }

    public Ubicacion obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_ubicacion, nombre, id_estatus, notas FROM ubicacion WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener ubicación por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Ubicacion> listarTodos(int limit, int offset) {
        String sql = "SELECT id_ubicacion, nombre, id_estatus, notas " +
                "FROM ubicacion ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar ubicaciones", ex);
        }
    }

    public List<Ubicacion> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_ubicacion, nombre, id_estatus, notas " +
                "FROM ubicacion WHERE nombre LIKE ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar ubicaciones", ex);
        }
    }

    public List<Ubicacion> listarPorEstatus(int idEstatus, int limit, int offset) {
        String sql = "SELECT id_ubicacion, nombre, id_estatus, notas " +
                "FROM ubicacion WHERE id_estatus = ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEstatus);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar ubicaciones por estatus", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Ubicacion u) {
        String sql = "UPDATE ubicacion SET nombre = ?, id_estatus = ?, notas = ? WHERE id_ubicacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getIdEstatus());
            ps.setString(3, u.getNotas());
            ps.setInt(4, u.getIdUbicacion());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Nombre duplicado o estatus inexistente.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar ubicación", ex);
        }
    }

    public boolean actualizarEstatus(int idUbicacion, int idEstatus) {
        String sql = "UPDATE ubicacion SET id_estatus = ? WHERE id_ubicacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEstatus);
            ps.setInt(2, idUbicacion);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalArgumentException("Estatus inexistente.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar estatus de ubicación", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idUbicacion) {
        String sql = "DELETE FROM ubicacion WHERE id_ubicacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUbicacion);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // Puede estar referenciada por centro o equipo
            throw new IllegalStateException("No se puede eliminar: la ubicación está referenciada por otros registros.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar ubicación", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM ubicacion";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar ubicaciones", ex);
        }
    }

    public boolean existeNombre(String nombre, Integer excluirIdUbicacion) {
        String sql = "SELECT COUNT(*) FROM ubicacion WHERE nombre = ? " +
                (excluirIdUbicacion != null ? "AND id_ubicacion <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            if (excluirIdUbicacion != null) ps.setInt(2, excluirIdUbicacion);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de ubicación", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Ubicacion mapRow(ResultSet rs) throws SQLException {
        Ubicacion u = new Ubicacion();
        u.setIdUbicacion(rs.getInt("id_ubicacion"));
        u.setNombre(rs.getString("nombre"));
        u.setIdEstatus(rs.getInt("id_estatus"));
        u.setNotas(rs.getString("notas"));
        return u;
    }

    private List<Ubicacion> mapList(ResultSet rs) throws SQLException {
        List<Ubicacion> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
