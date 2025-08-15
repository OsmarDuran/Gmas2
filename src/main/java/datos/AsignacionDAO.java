package datos;

import modelo.Asignacion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    // =========================
    // SQL base (JOIN enriquecido)
    // =========================
    private static final String BASE_SELECT =
            "SELECT a.id_asignacion, a.id_equipo, a.id_usuario, a.asignado_por, " +
                    "       a.asignado_en, a.devuelto_en, a.ruta_pdf, " +
                    "       u.nombre AS u_nombre, u.apellido_paterno AS u_ap, u.apellido_materno AS u_am, " +
                    "       ap.nombre AS ap_nombre, ap.apellido_paterno AS ap_ap, ap.apellido_materno AS ap_am, " +
                    "       e.numero_serie, e.id_estatus AS e_estatus_id, " +
                    "       te.nombre AS tipo_nombre, m.nombre AS modelo_nombre, ma.nombre AS marca_nombre, " +
                    "       es.nombre AS estatus_equipo " +
                    "FROM asignacion a " +
                    "JOIN usuario u   ON u.id_usuario = a.id_usuario " +
                    "JOIN usuario ap  ON ap.id_usuario = a.asignado_por " +
                    "JOIN equipo e    ON e.id_equipo = a.id_equipo " +
                    "LEFT JOIN modelo m ON m.id_modelo = e.id_modelo " +
                    "LEFT JOIN marca  ma ON ma.id_marca = e.id_marca " +
                    "JOIN tipo_equipo te ON te.id_tipo = e.id_tipo " +
                    "LEFT JOIN estatus es ON es.id_estatus = e.id_estatus ";

    // =========================
    // CREATE
    // =========================
    public int crearAsignacion(int idEquipo, int idUsuario, int asignadoPor, String rutaPdf) {
        String sql = "INSERT INTO asignacion (id_equipo, id_usuario, asignado_por, asignado_en, ruta_pdf) " +
                "VALUES (?, ?, ?, NOW(), ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, asignadoPor);
            ps.setString(4, rutaPdf);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de asignacion.");
        } catch (SQLException ex) {
            if ("45000".equals(ex.getSQLState())) {
                throw new IllegalStateException(ex.getMessage(), ex); // SIGNAL desde triggers/reglas
            }
            if (ex instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalArgumentException("Violación de integridad al crear asignación.", ex);
            }
            throw new RuntimeException("Error creando asignación", ex);
        }
    }

    // =========================
    // RETURN (devolver equipo)
    // =========================
    public boolean marcarDevuelto(int idAsignacion, LocalDateTime fechaDevolucion) {
        String sql = "UPDATE asignacion SET devuelto_en = ? " +
                "WHERE id_asignacion = ? AND devuelto_en IS NULL";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(fechaDevolucion != null ? fechaDevolucion : LocalDateTime.now()));
            ps.setInt(2, idAsignacion);
            int updated = ps.executeUpdate();
            return updated > 0;
        } catch (SQLException ex) {
            if ("45000".equals(ex.getSQLState())) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
            throw new RuntimeException("Error al marcar devolución", ex);
        }
    }

    // =========================
    // READ: Obtener por id
    // =========================
    public Asignacion obtenerPorId(int idAsignacion) {
        String sql = BASE_SELECT + "WHERE a.id_asignacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener asignación", ex);
        }
    }

    // =========================
    // LISTADOS
    // =========================
    public List<Asignacion> listarTodas(int limit, int offset) {
        String sql = BASE_SELECT + "ORDER BY a.asignado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar asignaciones", ex);
        }
    }

    public List<Asignacion> listarActivas(int limit, int offset) {
        String sql = BASE_SELECT + "WHERE a.devuelto_en IS NULL " +
                "ORDER BY a.asignado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar asignaciones activas", ex);
        }
    }

    public List<Asignacion> listarPorUsuario(int idUsuario, boolean soloActivas, int limit, int offset) {
        String sql = BASE_SELECT +
                "WHERE a.id_usuario = ? " +
                (soloActivas ? "AND a.devuelto_en IS NULL " : "") +
                "ORDER BY a.asignado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar por usuario", ex);
        }
    }

    public List<Asignacion> listarPorEquipo(int idEquipo, boolean incluirHistorial, int limit, int offset) {
        String sql = BASE_SELECT +
                "WHERE a.id_equipo = ? " +
                (incluirHistorial ? "" : "AND a.devuelto_en IS NULL ") +
                "ORDER BY a.asignado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar por equipo", ex);
        }
    }

    // =========================
    // DELETE (opcional: histórico)
    // =========================
    public boolean eliminar(int idAsignacion) {
        String sql = "DELETE FROM asignacion WHERE id_asignacion = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar asignación", ex);
        }
    }

    // =========================
    // Mapeo ResultSet -> Bean
    // =========================
    private Asignacion mapRow(ResultSet rs) throws SQLException {
        Asignacion a = new Asignacion();
        a.setIdAsignacion(rs.getInt("id_asignacion"));
        a.setIdEquipo(rs.getInt("id_equipo"));
        a.setIdUsuario(rs.getInt("id_usuario"));
        a.setAsignadoPor(rs.getInt("asignado_por"));

        Timestamp tAsig = rs.getTimestamp("asignado_en");
        Timestamp tDev  = rs.getTimestamp("devuelto_en");
        a.setAsignadoEn(tAsig != null ? tAsig.toLocalDateTime() : null);
        a.setDevueltoEn(tDev  != null ? tDev.toLocalDateTime()  : null);

        a.setRutaPdf(rs.getString("ruta_pdf"));

        // Derivados para UI
        String uNombre = rs.getString("u_nombre");
        String uAp     = rs.getString("u_ap");
        String uAm     = rs.getString("u_am");
        a.setUsuarioNombre(joinNombre(uNombre, uAp, uAm));

        String apNombre = rs.getString("ap_nombre");
        String apAp     = rs.getString("ap_ap");
        String apAm     = rs.getString("ap_am");
        a.setAsignadorNombre(joinNombre(apNombre, apAp, apAm));

        a.setEquipoNumeroSerie(rs.getString("numero_serie"));

        String modelo = rs.getString("modelo_nombre");
        String marca  = rs.getString("marca_nombre");
        String tipo   = rs.getString("tipo_nombre");
        String desc = (modelo != null ? modelo : (marca != null ? marca : "")) +
                (tipo != null ? " · " + tipo : "");
        a.setEquipoDescripcion(desc.trim().replaceAll("^ · ", ""));

        int eStatusId = rs.getInt("e_estatus_id");
        if (!rs.wasNull()) a.setEquipoEstatusId(eStatusId);
        a.setEquipoEstatusNombre(rs.getString("estatus_equipo"));

        return a;
    }

    private List<Asignacion> mapList(ResultSet rs) throws SQLException {
        List<Asignacion> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private static String joinNombre(String n, String ap, String am) {
        StringBuilder sb = new StringBuilder();
        if (n  != null && !n.isEmpty())  sb.append(n).append(" ");
        if (ap != null && !ap.isEmpty()) sb.append(ap).append(" ");
        if (am != null && !am.isEmpty()) sb.append(am);
        return sb.toString().trim();
    }

    /**
     * Elimina (hard delete) todas las asignaciones asociadas a un equipo.
     * Devuelve el número de filas eliminadas.
     */
    public int eliminarPorEquipo(int idEquipo) {
        String sql = "DELETE FROM asignacion WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar asignaciones por equipo", ex);
        }
    }
}
