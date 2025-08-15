package datos;

import modelo.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Marca m) {
        String sql = "INSERT INTO marca (nombre, activo, notas) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNombre());
            ps.setBoolean(2, m.isActivo());
            ps.setString(3, m.getNotas());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de marca.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear marca", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Marca obtenerPorId(int idMarca) {
        String sql = "SELECT id_marca, nombre, activo, notas FROM marca WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener marca por id", ex);
        }
    }

    public Marca obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_marca, nombre, activo, notas FROM marca WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener marca por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Marca> listarTodos(int limit, int offset) {
        String sql = "SELECT id_marca, nombre, activo, notas FROM marca " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar marcas", ex);
        }
    }

    public List<Marca> listarActivas(int limit, int offset) {
        String sql = "SELECT id_marca, nombre, activo, notas FROM marca " +
                "WHERE activo = TRUE ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar marcas activas", ex);
        }
    }

    public List<Marca> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_marca, nombre, activo, notas FROM marca " +
                "WHERE nombre LIKE ? ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar marcas", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Marca m) {
        String sql = "UPDATE marca SET nombre = ?, activo = ?, notas = ? WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, m.getNombre());
            ps.setBoolean(2, m.isActivo());
            ps.setString(3, m.getNotas());
            ps.setInt(4, m.getIdMarca());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar marca", ex);
        }
    }

    public boolean activar(int idMarca) {
        String sql = "UPDATE marca SET activo = TRUE WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMarca);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al activar marca", ex);
        }
    }

    public boolean desactivar(int idMarca) {
        String sql = "UPDATE marca SET activo = FALSE WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMarca);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al desactivar marca", ex);
        }
    }

    // =========================
    // DELETE (cuidado con FKs)
    // =========================
    public boolean eliminar(int idMarca) {
        String sql = "DELETE FROM marca WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // Hay modelos/equipos que referencian la marca
            throw new IllegalStateException("No se puede eliminar la marca: está referenciada por otros registros. Desactívela en su lugar.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar marca", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM marca";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar marcas", ex);
        }
    }

    public int contarActivas() {
        String sql = "SELECT COUNT(*) FROM marca WHERE activo = TRUE";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar marcas activas", ex);
        }
    }

    public boolean existeNombre(String nombre, Integer excluirIdMarca) {
        String sql = "SELECT COUNT(*) FROM marca WHERE nombre = ? " +
                (excluirIdMarca != null ? "AND id_marca <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            if (excluirIdMarca != null) ps.setInt(2, excluirIdMarca);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de marca", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Marca mapRow(ResultSet rs) throws SQLException {
        Marca m = new Marca();
        m.setIdMarca(rs.getInt("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));
        m.setNotas(rs.getString("notas"));
        return m;
    }

    private List<Marca> mapList(ResultSet rs) throws SQLException {
        List<Marca> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
