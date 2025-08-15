package datos;

import modelo.EquipoSim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoSimDAO {

    // =========================
    // CREATE
    // =========================
    /** Crea el registro SIM para un equipo. id_equipo es PK (1:1 con equipo). */
    public boolean crear(EquipoSim sim) {
        String sql = "INSERT INTO equipo_sim (id_equipo, numero_asignado, imei) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, sim.getIdEquipo());
            ps.setString(2, sim.getNumeroAsignado());
            ps.setString(3, sim.getImei());
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dupOrFk) {
            // Puede ser: PK duplicada (ya hay SIM para ese equipo),
            // UNIQUE(numero_asignado) duplicado, UNIQUE(imei) duplicado o FK inválida (equipo inexistente).
            throw new IllegalArgumentException("No se pudo crear SIM: ya existe para el equipo o número/IMEI duplicados o equipo inválido.", dupOrFk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear equipo_sim", ex);
        }
    }

    // =========================
    // READ
    // =========================
    /** Obtiene el SIM por PK (id_equipo). */
    public EquipoSim obtenerPorIdEquipo(int idEquipo) {
        String sql = "SELECT id_equipo, numero_asignado, imei FROM equipo_sim WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener SIM por id_equipo", ex);
        }
    }

    public EquipoSim obtenerPorNumeroAsignado(String numero) {
        String sql = "SELECT id_equipo, numero_asignado, imei FROM equipo_sim WHERE numero_asignado = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener SIM por número asignado", ex);
        }
    }

    public EquipoSim obtenerPorImei(String imei) {
        String sql = "SELECT id_equipo, numero_asignado, imei FROM equipo_sim WHERE imei = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, imei);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener SIM por IMEI", ex);
        }
    }

    // =========================
    // LISTAR / BUSCAR
    // =========================
    public List<EquipoSim> listarTodos(int limit, int offset) {
        String sql = "SELECT id_equipo, numero_asignado, imei FROM equipo_sim " +
                "ORDER BY id_equipo DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar SIMs", ex);
        }
    }

    public List<EquipoSim> buscarPorNumeroOIMEI(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = "SELECT id_equipo, numero_asignado, imei FROM equipo_sim " +
                "WHERE numero_asignado LIKE ? OR imei LIKE ? " +
                "ORDER BY id_equipo DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar SIMs", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    /** Actualiza ambos campos (útil cuando cambian número e IMEI a la vez). */
    public boolean actualizar(EquipoSim sim) {
        String sql = "UPDATE equipo_sim SET numero_asignado = ?, imei = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, sim.getNumeroAsignado());
            ps.setString(2, sim.getImei());
            ps.setInt(3, sim.getIdEquipo());
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Número asignado o IMEI duplicados.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar equipo_sim", ex);
        }
    }

    /** Actualiza solo el número asignado. */
    public boolean actualizarNumero(int idEquipo, String nuevoNumero) {
        String sql = "UPDATE equipo_sim SET numero_asignado = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevoNumero);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Número asignado duplicado.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar número asignado", ex);
        }
    }

    /** Actualiza solo el IMEI. */
    public boolean actualizarImei(int idEquipo, String nuevoImei) {
        String sql = "UPDATE equipo_sim SET imei = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevoImei);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("IMEI duplicado.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar IMEI", ex);
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM equipo_sim WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar equipo_sim", ex);
        }
    }

    // =========================
    // CONTAR / VALIDAR
    // =========================
    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM equipo_sim";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar SIMs", ex);
        }
    }

    /** ¿Ya existe un SIM para este equipo? (PK 1:1) */
    public boolean existeParaEquipo(int idEquipo) {
        String sql = "SELECT COUNT(*) FROM equipo_sim WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar existencia de SIM para equipo", ex);
        }
    }

    /** ¿Existe ya ese número asignado? Útil para validación previa. */
    public boolean existeNumeroAsignado(String numero) {
        String sql = "SELECT COUNT(*) FROM equipo_sim WHERE numero_asignado = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de número asignado", ex);
        }
    }

    /** ¿Existe ya ese IMEI? Útil para validación previa. */
    public boolean existeImei(String imei) {
        String sql = "SELECT COUNT(*) FROM equipo_sim WHERE imei = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, imei);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al verificar duplicado de IMEI", ex);
        }
    }

    // =========================
    // Helpers de mapeo
    // =========================
    private EquipoSim mapRow(ResultSet rs) throws SQLException {
        EquipoSim sim = new EquipoSim();
        sim.setIdEquipo(rs.getInt("id_equipo"));
        sim.setNumeroAsignado(rs.getString("numero_asignado"));
        sim.setImei(rs.getString("imei"));
        return sim;
    }

    private List<EquipoSim> mapList(ResultSet rs) throws SQLException {
        List<EquipoSim> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}
