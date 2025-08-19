package datos;

import modelo.Equipo;
import modelo.EquipoDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    // =========================
    // CREATE
    // =========================
    public int crear(Equipo e) {
        String sql = "INSERT INTO equipo " +
                "(id_tipo, id_modelo, numero_serie, id_marca, id_ubicacion, id_estatus, ip_fija, puerto_ethernet, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, e.getIdTipo());
            setNullableInt(ps, 2, e.getIdModelo());
            ps.setString(3, e.getNumeroSerie());
            setNullableInt(ps, 4, e.getIdMarca());
            setNullableInt(ps, 5, e.getIdUbicacion());
            ps.setInt(6, e.getIdEstatus());
            ps.setString(7, e.getIpFija());
            ps.setString(8, e.getPuertoEthernet());
            ps.setString(9, e.getNotas());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de equipo.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Número de serie duplicado o referencia inválida.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear equipo", ex);
        }
    }

    // =========================
    // READ (básico)
    // =========================
    public Equipo obtenerPorId(int idEquipo) {
        String sql = "SELECT id_equipo, id_tipo, id_modelo, numero_serie, id_marca, id_ubicacion, " +
                "       id_estatus, ip_fija, puerto_ethernet, notas " +
                "FROM equipo WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowEquipo(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener equipo por id", ex);
        }
    }

    public Equipo obtenerPorNumeroSerie(String numeroSerie) {
        String sql = "SELECT id_equipo, id_tipo, id_modelo, numero_serie, id_marca, id_ubicacion, " +
                "       id_estatus, ip_fija, puerto_ethernet, notas " +
                "FROM equipo WHERE numero_serie = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numeroSerie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowEquipo(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener equipo por número de serie", ex);
        }
    }

    // =========================
    // READ con DETALLE (JOINs)
    // =========================
    private static final String BASE_JOIN =
            "FROM equipo e " +
                    "JOIN tipo_equipo te ON te.id_tipo = e.id_tipo " +
                    "LEFT JOIN modelo mo ON mo.id_modelo = e.id_modelo " +
                    "LEFT JOIN marca ma ON ma.id_marca = e.id_marca " +
                    "LEFT JOIN ubicacion u ON u.id_ubicacion = e.id_ubicacion " +
                    "LEFT JOIN estatus es ON es.id_estatus = e.id_estatus ";

    private static final String SELECT_DETALLE =
            "SELECT e.id_equipo, e.numero_serie, e.id_tipo, te.nombre AS tipo_nombre, " +
                    "       e.id_modelo, mo.nombre AS modelo_nombre, " +
                    "       e.id_marca,  ma.nombre AS marca_nombre, " +
                    "       e.id_ubicacion, u.nombre AS ubicacion_nombre, " +
                    "       e.id_estatus, es.nombre AS estatus_nombre, " +
                    "       e.ip_fija, e.puerto_ethernet, e.notas ";

    public EquipoDetalle obtenerDetallePorId(int idEquipo) {
        String sql = SELECT_DETALLE + BASE_JOIN + "WHERE e.id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowDetalle(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener detalle de equipo", ex);
        }
    }

    public List<EquipoDetalle> listarConDetalle(
            Integer idTipo, Integer idMarca, Integer idModelo,
            Integer idEstatus, Integer idUbicacion,
            String textoLibre, // busca en numero_serie / notas
            int limit, int offset) {

        StringBuilder sb = new StringBuilder(SELECT_DETALLE + BASE_JOIN + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (idTipo != null)      { sb.append("AND e.id_tipo = ? ");      params.add(idTipo); }
        if (idMarca != null)     { sb.append("AND e.id_marca = ? ");     params.add(idMarca); }
        if (idModelo != null)    { sb.append("AND e.id_modelo = ? ");    params.add(idModelo); }
        if (idEstatus != null)   { sb.append("AND e.id_estatus = ? ");   params.add(idEstatus); }
        if (idUbicacion != null) { sb.append("AND e.id_ubicacion = ? "); params.add(idUbicacion); }
        if (textoLibre != null && !textoLibre.isEmpty()) {
            sb.append("AND (e.numero_serie LIKE ? OR e.notas LIKE ?) ");
            String like = "%" + textoLibre + "%";
            params.add(like); params.add(like);
        }
        sb.append("ORDER BY e.id_equipo DESC LIMIT ? OFFSET ?");
        params.add(limit); params.add(offset);

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<EquipoDetalle> list = new ArrayList<>();
                while (rs.next()) list.add(mapRowDetalle(rs));
                return list;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar equipos con detalle", ex);
        }
    }

    // Algunos atajos comunes:
    public List<EquipoDetalle> listarDisponibles(int limit, int offset) {
        // En tu seed pusiste 1=Disponible, 2=Asignado, 3=En reparación (tipo EQUIPO)
        return listarConDetalle(null, null, null, 1, null, null, limit, offset);
    }
    public List<EquipoDetalle> listarAsignados(int limit, int offset) {
        return listarConDetalle(null, null, null, 2, null, null, limit, offset);
    }
    public List<EquipoDetalle> listarEnReparacion(int limit, int offset) {
        return listarConDetalle(null, null, null, 3, null, null, limit, offset);
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipo SET id_tipo=?, id_modelo=?, numero_serie=?, id_marca=?, id_ubicacion=?, " +
                "id_estatus=?, ip_fija=?, puerto_ethernet=?, notas=? WHERE id_equipo=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, e.getIdTipo());
            setNullableInt(ps, 2, e.getIdModelo());
            ps.setString(3, e.getNumeroSerie());
            setNullableInt(ps, 4, e.getIdMarca());
            setNullableInt(ps, 5, e.getIdUbicacion());
            ps.setInt(6, e.getIdEstatus());
            ps.setString(7, e.getIpFija());
            ps.setString(8, e.getPuertoEthernet());
            ps.setString(9, e.getNotas());
            ps.setInt(10, e.getIdEquipo());

            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Número de serie duplicado o referencia inválida.", dup);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar equipo", ex);
        }
    }

    public boolean actualizarEstatus(int idEquipo, int idEstatus) {
        String sql = "UPDATE equipo SET id_estatus = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEstatus);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar estatus de equipo", ex);
        }
    }

    public boolean actualizarUbicacion(int idEquipo, Integer idUbicacion) {
        String sql = "UPDATE equipo SET id_ubicacion = ? WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            setNullableInt(ps, 1, idUbicacion);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar ubicación de equipo", ex);
        }
    }

    // =========================
    // DELETE (cuidado con FKs)
    // =========================
    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM equipo WHERE id_equipo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            throw new IllegalStateException("No se puede eliminar: el equipo tiene dependencias (asignaciones/bitácora/subtipos).", fk);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar equipo", ex);
        }
    }

    public int contarConFiltros(Integer idTipo, Integer idMarca, Integer idModelo, Integer idEstatus, Integer idUbicacion, String textoLibre) {
        StringBuilder sb = new StringBuilder(
                "SELECT COUNT(*) " +
                        "FROM equipo e " +
                        "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (idTipo != null)      { sb.append("AND e.id_tipo = ? ");      params.add(idTipo); }
        if (idMarca != null)     { sb.append("AND e.id_marca = ? ");     params.add(idMarca); }
        if (idModelo != null)    { sb.append("AND e.id_modelo = ? ");    params.add(idModelo); }
        if (idEstatus != null)   { sb.append("AND e.id_estatus = ? ");   params.add(idEstatus); }
        if (idUbicacion != null) { sb.append("AND e.id_ubicacion = ? "); params.add(idUbicacion); }
        if (textoLibre != null && !textoLibre.isEmpty()) {
            sb.append("AND (e.numero_serie LIKE ? OR e.notas LIKE ?) ");
            String like = "%" + textoLibre + "%";
            params.add(like); params.add(like);
        }
        try (Connection cn = Conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar equipos con filtros", ex);
        }
    }


    // =========================
    // Helpers de mapeo
    // =========================
    private Equipo mapRowEquipo(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("id_equipo"));

        e.setIdTipo(rs.getInt("id_tipo"));

        int idModelo = rs.getInt("id_modelo");
        e.setIdModelo(rs.wasNull() ? null : idModelo);

        e.setNumeroSerie(rs.getString("numero_serie"));

        int idMarca = rs.getInt("id_marca");
        e.setIdMarca(rs.wasNull() ? null : idMarca);

        int idUbic = rs.getInt("id_ubicacion");
        e.setIdUbicacion(rs.wasNull() ? null : idUbic);

        e.setIdEstatus(rs.getInt("id_estatus"));
        e.setIpFija(rs.getString("ip_fija"));
        e.setPuertoEthernet(rs.getString("puerto_ethernet"));
        e.setNotas(rs.getString("notas"));
        return e;
    }

    private EquipoDetalle mapRowDetalle(ResultSet rs) throws SQLException {
        EquipoDetalle d = new EquipoDetalle();
        d.setIdEquipo(rs.getInt("id_equipo"));
        d.setNumeroSerie(rs.getString("numero_serie"));

        d.setIdTipo(rs.getInt("id_tipo"));
        d.setTipoNombre(rs.getString("tipo_nombre"));

        int idModelo = rs.getInt("id_modelo");
        d.setIdModelo(rs.wasNull() ? null : idModelo);
        d.setModeloNombre(rs.getString("modelo_nombre"));

        int idMarca = rs.getInt("id_marca");
        d.setIdMarca(rs.wasNull() ? null : idMarca);
        d.setMarcaNombre(rs.getString("marca_nombre"));

        int idUbic = rs.getInt("id_ubicacion");
        d.setIdUbicacion(rs.wasNull() ? null : idUbic);
        d.setUbicacionNombre(rs.getString("ubicacion_nombre"));

        d.setIdEstatus(rs.getInt("id_estatus"));
        d.setEstatusNombre(rs.getString("estatus_nombre"));

        d.setIpFija(rs.getString("ip_fija"));
        d.setPuertoEthernet(rs.getString("puerto_ethernet"));
        d.setNotas(rs.getString("notas"));
        return d;
    }

    // =========================
    // Otros helpers
    // =========================
    private static void setNullableInt(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value);
    }
}
