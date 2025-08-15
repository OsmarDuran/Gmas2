package datos;

import modelo.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Rol r) {
        String sql = "INSERT INTO rol (id_rol, nombre) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, r.getIdRol());          // tu esquema no es AUTO_INCREMENT
            ps.setString(2, r.getNombreRol());
            ps.executeUpdate();
            return r.getIdRol();
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Puede ser PK duplicada o nombre UNIQUE duplicado
            throw new IllegalArgumentException("ID de rol o nombre ya existen.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear rol", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Rol obtenerPorId(int idRol) {
        String sql = "SELECT id_rol, nombre FROM rol WHERE id_rol = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener rol por id", ex);
        }
    }

    public Rol obtenerPorNombreExacto(String nombreRol) {
        String sql = "SELECT id_rol, nombre FROM rol WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener rol por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Rol> listarTodos(int limit, int offset) {
        String sql = "SELECT id_rol, nombre FROM rol ORDER BY id_rol ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar roles", ex);
        }
    }

    public List<Rol> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_rol, nombre FROM rol WHERE nombre LIKE ? ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar roles", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Rol r) {
        String sql = "UPDATE rol SET nombre = ? WHERE id_rol = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, r.getNombreRol());
            ps.setInt(2, r.getIdRol());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar rol", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idRol) {
        String sql = "DELETE FROM rol WHERE id_rol = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // Hay usuarios que referencian este rol
            throw new IllegalStateException("No se puede eliminar: hay usuarios usando este rol.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar rol", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM rol";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar roles", ex);
        }
    }

    public boolean existeNombre(String nombreRol, Integer excluirIdRol) {
        String sql = "SELECT COUNT(*) FROM rol WHERE nombre = ? " +
                (excluirIdRol != null ? "AND id_rol <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreRol);
            if (excluirIdRol != null) ps.setInt(2, excluirIdRol);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de rol", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Rol mapRow(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setIdRol(rs.getInt("id_rol"));
        r.setNombreRol(rs.getString("nombre"));
        return r;
    }

    private List<Rol> mapList(ResultSet rs) throws SQLException {
        List<Rol> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
