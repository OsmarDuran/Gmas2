package datos;

import modelo.Puesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PuestoDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Puesto p) {
        String sql = "INSERT INTO puesto (nombre, notas) VALUES (?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getNotas());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de puesto.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Por si agregas UNIQUE(nombre) en el futuro
            throw new IllegalArgumentException("Ya existe un puesto con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear puesto", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Puesto obtenerPorId(int idPuesto) {
        String sql = "SELECT id_puesto, nombre, notas FROM puesto WHERE id_puesto = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPuesto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener puesto por id", ex);
        }
    }

    public Puesto obtenerPorNombreExacto(String nombre) {
        String sql = "SELECT id_puesto, nombre, notas FROM puesto WHERE nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener puesto por nombre", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Puesto> listarTodos(int limit, int offset) {
        String sql = "SELECT id_puesto, nombre, notas FROM puesto ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Puesto> list = new ArrayList<>();
                while (rs.next()) {
                    Puesto p = new Puesto();
                    p.setIdPuesto(rs.getInt("id_puesto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setNotas(rs.getString("notas"));
                    list.add(p);
                }
                return list;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar puestos", ex);
        }
    }


    public List<Puesto> buscarPorNombre(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_puesto, nombre, notas FROM puesto " +
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
            throw new RuntimeException("Error al buscar puestos", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Puesto p) {
        String sql = "UPDATE puesto SET nombre = ?, notas = ? WHERE id_puesto = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getNotas());
            ps.setInt(3, p.getIdPuesto());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe un puesto con ese nombre.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar puesto", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idPuesto) {
        String sql = "DELETE FROM puesto WHERE id_puesto = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPuesto);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // Referenciado por usuario/lider
            throw new IllegalStateException("No se puede eliminar: el puesto está referenciado por otros registros.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar puesto", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM puesto";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar puestos", ex);
        }
    }

    /** Verifica si existe un puesto con ese nombre (opcionalmente excluyendo un id). */
    public boolean existeNombre(String nombre, Integer excluirIdPuesto) {
        String sql = "SELECT COUNT(*) FROM puesto WHERE nombre = ? " +
                (excluirIdPuesto != null ? "AND id_puesto <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            if (excluirIdPuesto != null) ps.setInt(2, excluirIdPuesto);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de puesto", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Puesto mapRow(ResultSet rs) throws SQLException {
        Puesto p = new Puesto();
        p.setIdPuesto(rs.getInt("id_puesto"));
        p.setNombre(rs.getString("nombre"));
        p.setNotas(rs.getString("notas"));
        return p;
    }

    private List<Puesto> mapList(ResultSet rs) throws SQLException {
        List<Puesto> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
