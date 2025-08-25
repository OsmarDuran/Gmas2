package datos;

import modelo.BitacoraMovimiento;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitacoraMovimientoDAO {

    // =========================
    // CREATE
    // =========================

    /* Inserta un movimiento completo. Si realizadoEn es null, usa NOW(). Devuelve el id generado. */
    public int registrar(BitacoraMovimiento mov) {
        String sql = "INSERT INTO bitacora_movimiento " +
                "(id_equipo, id_usuario, accion, estatus_origen, estatus_destino, realizado_por, realizado_en, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, mov.getIdEquipo());
            setNullableInt(ps, 2, mov.getIdUsuario()); // si 0 => NULL
            ps.setString(3, mov.getAccion());
            setNullableInt(ps, 4, mov.getEstatusOrigen());
            setNullableInt(ps, 5, mov.getEstatusDestino());
            ps.setInt(6, mov.getRealizadoPor());
            ps.setTimestamp(7, Timestamp.valueOf(mov.getRealizadoEn() != null ? mov.getRealizadoEn() : LocalDateTime.now()));
            ps.setString(8, mov.getNotas());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de bitácora.");
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar movimiento", ex);
        }
    }

    /** Conveniencia: inserta con parámetros sueltos. Usa NOW() si realizadoEn es null. */
    public int registrar(int idEquipo,
                         Integer idUsuario,        // puede ser null
                         String accion,            // 'ASIGNAR','DEVOLVER','CAMBIO_ESTATUS','REPARACION_IN','REPARACION_OUT'
                         Integer estatusOrigen,    // puede ser null
                         Integer estatusDestino,   // puede ser null
                         int realizadoPor,
                         LocalDateTime realizadoEn,
                         String notas) {
        String sql = "INSERT INTO bitacora_movimiento " +
                "(id_equipo, id_usuario, accion, estatus_origen, estatus_destino, realizado_por, realizado_en, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idEquipo);
            if (idUsuario != null) ps.setInt(2, idUsuario); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, accion);
            if (estatusOrigen != null) ps.setInt(4, estatusOrigen); else ps.setNull(4, Types.INTEGER);
            if (estatusDestino != null) ps.setInt(5, estatusDestino); else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, realizadoPor);
            ps.setTimestamp(7, Timestamp.valueOf(realizadoEn != null ? realizadoEn : LocalDateTime.now()));
            ps.setString(8, notas);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de bitácora.");
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar movimiento", ex);
        }
    }

    // =========================
    // READ
    // =========================

    public BitacoraMovimiento obtenerPorId(int idMovimiento) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento WHERE id_mov = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener movimiento", ex);
        }
    }

    public List<BitacoraMovimiento> listarUltimosDeEquipo(int idEquipo, int topN) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento WHERE id_equipo = ? " +
                "ORDER BY realizado_en DESC, id_mov DESC LIMIT ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, topN);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar últimos movimientos por equipo", ex);
        }
    }

    public List<BitacoraMovimiento> listarPorUsuarioInvolucrado(Integer idUsuario, int limit, int offset) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento " +
                "WHERE (? IS NULL OR id_usuario = ?) " +
                "ORDER BY realizado_en DESC, id_mov DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (idUsuario == null) {
                ps.setNull(1, Types.INTEGER);
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idUsuario);
            }
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar por usuario involucrado", ex);
        }
    }

    public List<BitacoraMovimiento> listarPorAccion(String accion, int limit, int offset) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento WHERE accion = ? " +
                "ORDER BY realizado_en DESC, id_mov DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, accion);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar por acción", ex);
        }
    }

    public List<BitacoraMovimiento> listarPorRangoFechas(LocalDateTime desde, LocalDateTime hasta, int limit, int offset) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento " +
                "WHERE realizado_en >= ? AND realizado_en < ? " +
                "ORDER BY realizado_en DESC, id_mov DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar por rango de fechas", ex);
        }
    }

    public List<BitacoraMovimiento> listarTodos(int limit, int offset) {
        String sql = "SELECT id_mov, id_equipo, id_usuario, accion, estatus_origen, estatus_destino, " +
                "       realizado_por, realizado_en, notas " +
                "FROM bitacora_movimiento " +
                "ORDER BY realizado_en DESC, id_mov DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar bitácora", ex);
        }
    }

    // =========================
    // DELETE (opcional)
    // =========================
    public boolean eliminar(int idMovimiento) {
        String sql = "DELETE FROM bitacora_movimiento WHERE id_mov = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar movimiento", ex);
        }
    }

    // =========================
    // Helpers de mapeo
    // =========================
    private BitacoraMovimiento mapRow(ResultSet rs) throws SQLException {
        BitacoraMovimiento b = new BitacoraMovimiento();
        b.setIdMovimiento(rs.getInt("id_mov"));
        b.setIdEquipo(rs.getInt("id_equipo"));

        int idUsuario = rs.getInt("id_usuario");
        b.setIdUsuario(rs.wasNull() ? 0 : idUsuario);  // si DB trae NULL => 0

        b.setAccion(rs.getString("accion"));

        int estOri = rs.getInt("estatus_origen");
        b.setEstatusOrigen(rs.wasNull() ? 0 : estOri);

        int estDes = rs.getInt("estatus_destino");
        b.setEstatusDestino(rs.wasNull() ? 0 : estDes);

        b.setRealizadoPor(rs.getInt("realizado_por"));

        Timestamp t = rs.getTimestamp("realizado_en");
        b.setRealizadoEn(t != null ? t.toLocalDateTime() : null);

        b.setNotas(rs.getString("notas"));
        return b;
    }

    private List<BitacoraMovimiento> mapList(ResultSet rs) throws SQLException {
        List<BitacoraMovimiento> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private static void setNullableInt(PreparedStatement ps, int idx, int value) throws SQLException {
        // Convención: si 0 => tratar como NULL en DB
        if (value == 0) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value);
    }
}
