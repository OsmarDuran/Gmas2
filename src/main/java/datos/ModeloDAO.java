package datos;

import modelo.Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModeloDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Modelo m) {
        String sql = "INSERT INTO modelo (id_marca, nombre, activo, notas) VALUES (?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getIdMarca());
            ps.setString(2, m.getNombre());
            ps.setBoolean(3, m.isActivo());
            ps.setString(4, m.getNotas());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de modelo.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Puede ser por FK (id_marca inexistente) o por UNIQUE (id_marca, nombre)
            throw new IllegalArgumentException("Modelo duplicado para la marca o marca inexistente.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear modelo", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Modelo obtenerPorId(int idModelo) {
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas FROM modelo WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener modelo por id", ex);
        }
    }

    public Modelo obtenerPorMarcaYNombre(int idMarca, String nombre) {
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas FROM modelo WHERE id_marca = ? AND nombre = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setString(2, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener modelo por (marca, nombre)", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<Modelo> listarTodos(int limit, int offset) {
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas " +
                "FROM modelo ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar modelos", ex);
        }
    }

    public List<Modelo> listarPorMarca(int idMarca, int limit, int offset) {
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas " +
                "FROM modelo WHERE id_marca = ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar modelos por marca", ex);
        }
    }

    public List<Modelo> listarActivosPorMarca(int idMarca, int limit, int offset) {
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas " +
                "FROM modelo WHERE id_marca = ? AND activo = TRUE " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar modelos activos por marca", ex);
        }
    }

    public List<Modelo> buscarPorNombreEnMarca(int idMarca, String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_modelo, id_marca, nombre, activo, notas " +
                "FROM modelo WHERE id_marca = ? AND nombre LIKE ? " +
                "ORDER BY nombre ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setString(2, like);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar modelos por nombre en marca", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Modelo m) {
        String sql = "UPDATE modelo SET id_marca = ?, nombre = ?, activo = ?, notas = ? WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, m.getIdMarca());
            ps.setString(2, m.getNombre());
            ps.setBoolean(3, m.isActivo());
            ps.setString(4, m.getNotas());
            ps.setInt(5, m.getIdModelo());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Modelo duplicado para la marca o marca inexistente.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar modelo", ex);
        }
    }

    public boolean activar(int idModelo) {
        String sql = "UPDATE modelo SET activo = TRUE WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al activar modelo", ex);
        }
    }

    public boolean desactivar(int idModelo) {
        String sql = "UPDATE modelo SET activo = FALSE WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al desactivar modelo", ex);
        }
    }

    // =========================
    // DELETE (cuidado con FKs desde equipo)
    // =========================
    public boolean eliminar(int idModelo) {
        String sql = "DELETE FROM modelo WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalStateException("No se puede eliminar el modelo: está referenciado por equipos. Desactívelo en su lugar.", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar modelo", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM modelo";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar modelos", ex);
        }
    }

    public int contarPorMarca(int idMarca) {
        String sql = "SELECT COUNT(*) FROM modelo WHERE id_marca = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar modelos por marca", ex);
        }
    }

    public int contarActivosPorMarca(int idMarca) {
        String sql = "SELECT COUNT(*) FROM modelo WHERE id_marca = ? AND activo = TRUE";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar modelos activos por marca", ex);
        }
    }

    /** Valida la unicidad (id_marca, nombre). */
    public boolean existeNombreEnMarca(String nombre, int idMarca, Integer excluirIdModelo) {
        String sql = "SELECT COUNT(*) FROM modelo WHERE id_marca = ? AND nombre = ? " +
                (excluirIdModelo != null ? "AND id_modelo <> ?" : "");
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setString(2, nombre);
            if (excluirIdModelo != null) ps.setInt(3, excluirIdModelo);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al validar duplicado de modelo en marca", ex);
        }
    }

    // =========================
    // Mapeo
    // =========================
    private Modelo mapRow(ResultSet rs) throws SQLException {
        Modelo m = new Modelo();
        m.setIdModelo(rs.getInt("id_modelo"));
        m.setIdMarca(rs.getInt("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));
        m.setNotas(rs.getString("notas"));
        return m;
    }

    private List<Modelo> mapList(ResultSet rs) throws SQLException {
        List<Modelo> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public String obtenerNombrePorId(Integer idModelo) {
        if (idModelo == null) return null;
        final String sql = "SELECT nombre FROM modelo WHERE id_modelo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idModelo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("nombre") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener nombre de modelo por id", ex);
        }
    }

}
