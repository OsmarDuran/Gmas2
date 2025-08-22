package datos;

import modelo.Centro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentroDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Centro c) {
        String sql = "INSERT INTO centro (nombre, id_ubicacion, notas) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getIdUbicacion());
            ps.setString(3, c.getNotas());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de centro.");
        } catch (SQLIntegrityConstraintViolationException fk) {
            // id_ubicacion no existe o hay otra restricción FK/UK en DB
            throw new IllegalArgumentException("No se pudo crear el centro: verifique la ubicación o unicidad.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear centro", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Centro obtenerPorId(int idCentro) {
        String sql = "SELECT id_centro, nombre, id_ubicacion, notas FROM centro WHERE id_centro = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCentro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener centro por id", ex);
        }
    }

    public Centro obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_centro, nombre, id_ubicacion, notas FROM centro WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener centro por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Centro> listarTodos(int limit, int offset) {
        String sql = "SELECT id_centro, nombre, id_ubicacion, notas FROM centro ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Centro> list = new ArrayList<>();
                while (rs.next()) {
                    Centro c = new Centro();
                    c.setIdCentro(rs.getInt("id_centro"));
                    c.setNombre(rs.getString("nombre"));
                    c.setIdUbicacion(rs.getInt("id_ubicacion"));
                    c.setNotas(rs.getString("notas"));
                    list.add(c);
                }
                return list;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar centros", ex);
        }
    }


    public List<Centro> listarPorUbicacion(int idUbicacion, int limit, int offset) {
        String sql = "SELECT id_centro, nombre, id_ubicacion, notas " +
                "FROM centro WHERE id_ubicacion = ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUbicacion);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar centros por ubicación", ex);
        }
    }

    public List<Centro> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_centro, nombre, id_ubicacion, notas " +
                "FROM centro WHERE nombre LIKE ? " +
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
            throw new RuntimeException("Error al buscar centros por nombre", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Centro c) {
        String sql = "UPDATE centro SET nombre = ?, id_ubicacion = ?, notas = ? WHERE id_centro = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getIdUbicacion());
            ps.setString(3, c.getNotas());
            ps.setInt(4, c.getIdCentro());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalArgumentException("No se pudo actualizar el centro: verifique la ubicación o unicidad.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar centro", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idCentro) {
        String sql = "DELETE FROM centro WHERE id_centro = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCentro);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            // Si prefieres "baja lógica", quita el DELETE y crea un campo activo en DB.
            throw new RuntimeException("Error al eliminar centro", ex);
        }
    }

    // =========================
    // HELPERS
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM centro";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar centros", ex);
        }
    }

    /** Verifica si ya existe un centro con el mismo nombre en una ubicación dada. */
    public boolean existeNombreEnUbicacion(String nombre, int idUbicacion, Integer excluirIdCentro) {
        String sql = "SELECT COUNT(*) FROM centro WHERE nombre = ? AND id_ubicacion = ? " +
                (excluirIdCentro != null ? "AND id_centro <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idUbicacion);
            if (excluirIdCentro != null) ps.setInt(3, excluirIdCentro);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al validar nombre de centro", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Centro mapRow(ResultSet rs) throws SQLException {
        Centro c = new Centro();
        c.setIdCentro(rs.getInt("id_centro"));
        c.setNombre(rs.getString("nombre"));
        c.setIdUbicacion(rs.getInt("id_ubicacion"));
        c.setNotas(rs.getString("notas"));
        return c;
    }

    private List<Centro> mapList(ResultSet rs) throws SQLException {
        List<Centro> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
