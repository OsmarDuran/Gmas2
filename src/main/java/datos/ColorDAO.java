package datos;

import modelo.Color;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO {

    // CREATE
    public int crear(Color c) {
        String sql = "INSERT INTO color (nombre, hex) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getHex());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de color.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("El color ya existe.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear color", ex);
        }
    }

    // READ
    public Color obtenerPorId(int idColor) {
        String sql = "SELECT id_color, nombre, hex FROM color WHERE id_color = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idColor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener color por id", ex);
        }
    }

    public Color obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_color, nombre, hex FROM color WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener color por nombre", ex);
        }
    }

    // LISTAR / BUSCAR
    public List<Color> listarTodos(int limit, int offset) {
        String sql = "SELECT id_color, nombre, hex FROM color ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar colores", ex);
        }
    }

    public List<Color> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_color, nombre, hex FROM color WHERE nombre LIKE ? ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar colores", ex);
        }
    }

    // UPDATE
    public boolean actualizar(Color c) {
        String sql = "UPDATE color SET nombre = ?, hex = ? WHERE id_color = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getHex());
            ps.setInt(3, c.getIdColor());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("El nombre de color ya existe.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar color", ex);
        }
    }

    // DELETE
    public boolean eliminar(int idColor) {
        String sql = "DELETE FROM color WHERE id_color = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idColor);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar color", ex);
        }
    }

    // CONTAR
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM color";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar colores", ex);
        }
    }

    // VALIDAR DUPLICADO
    public boolean existeNombre(String nombre, Integer excluirId) {
        String sql = "SELECT COUNT(*) FROM color WHERE nombre = ? " +
                (excluirId != null ? "AND id_color <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            if (excluirId != null) ps.setInt(2, excluirId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de color", ex);
        }
    }

    // Helpers
    private Color mapRow(ResultSet rs) throws SQLException {
        Color c = new Color();
        c.setIdColor(rs.getInt("id_color"));
        c.setNombre(rs.getString("nombre"));
        c.setHex(rs.getString("hex"));
        return c;
    }

    private List<Color> mapList(ResultSet rs) throws SQLException {
        List<Color> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
