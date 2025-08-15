package datos;

import modelo.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // =========================
    // SELECT base con JOINs
    // =========================
    private static final String BASE_SELECT =
            "SELECT u.id_usuario, u.nombre, u.apellido_paterno, u.apellido_materno, " +
                    "       u.email, u.telefono, u.id_lider, u.id_puesto, u.id_centro, u.id_rol, " +
                    "       u.hash_password, u.activo, u.ultimo_login, u.creado_en, " +
                    "       r.nombre AS rol_nombre, p.nombre AS puesto_nombre, c.nombre AS centro_nombre, " +
                    "       CONCAT(l.nombre, ' ', IFNULL(l.apellido_paterno,''), ' ', IFNULL(l.apellido_materno,'')) AS lider_nombre " +
                    "FROM usuario u " +
                    "JOIN rol r     ON r.id_rol = u.id_rol " +
                    "JOIN puesto p  ON p.id_puesto = u.id_puesto " +
                    "JOIN centro c  ON c.id_centro = u.id_centro " +
                    "JOIN lider l   ON l.id_lider  = u.id_lider ";

    // =========================
    // CREATE
    // =========================
    public int crear(Usuario u) {
        String sql = "INSERT INTO usuario " +
                "(nombre, apellido_paterno, apellido_materno, email, telefono, " +
                " id_lider, id_puesto, id_centro, id_rol, hash_password, activo, ultimo_login, creado_en) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidoPaterno());
            ps.setString(3, u.getApellidoMaterno());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getIdLider());
            ps.setInt(7, u.getIdPuesto());
            ps.setInt(8, u.getIdCentro());
            ps.setInt(9, u.getIdRol());
            ps.setString(10, u.getHashPassword());
            ps.setBoolean(11, u.isActivo());
            if (u.getUltimoLogin() != null)
                ps.setTimestamp(12, Timestamp.valueOf(u.getUltimoLogin()));
            else
                ps.setNull(12, Types.TIMESTAMP);
            if (u.getCreadoEn() != null)
                ps.setTimestamp(13, Timestamp.valueOf(u.getCreadoEn()));
            else
                ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No se obtuvo id generado de usuario.");
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear usuario", ex);
        }
    }

    // =========================
    // READ
    // =========================
    public Usuario obtenerPorId(int idUsuario) {
        String sql = BASE_SELECT + "WHERE u.id_usuario = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener usuario por id", ex);
        }
    }

    public Usuario obtenerPorEmail(String email) {
        String sql = BASE_SELECT + "WHERE u.email = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener usuario por email", ex);
        }
    }

    /** Devuelve solo hash + id + rol + activo para flujo de login. */
    public LoginInfo obtenerLoginInfo(String email) {
        String sql = "SELECT id_usuario, hash_password, id_rol, activo FROM usuario WHERE email = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoginInfo info = new LoginInfo();
                    info.idUsuario = rs.getInt("id_usuario");
                    info.hashPassword = rs.getString("hash_password");
                    info.idRol = rs.getInt("id_rol");
                    info.activo = rs.getBoolean("activo");
                    return info;
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener login info", ex);
        }
    }

    // =========================
    // UPDATE
    // =========================
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET " +
                "nombre=?, apellido_paterno=?, apellido_materno=?, email=?, telefono=?, " +
                "id_lider=?, id_puesto=?, id_centro=?, id_rol=?, activo=?, ultimo_login=? " +
                "WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidoPaterno());
            ps.setString(3, u.getApellidoMaterno());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getIdLider());
            ps.setInt(7, u.getIdPuesto());
            ps.setInt(8, u.getIdCentro());
            ps.setInt(9, u.getIdRol());
            ps.setBoolean(10, u.isActivo());
            if (u.getUltimoLogin() != null)
                ps.setTimestamp(11, Timestamp.valueOf(u.getUltimoLogin()));
            else
                ps.setNull(11, Types.TIMESTAMP);
            ps.setInt(12, u.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar usuario", ex);
        }
    }

    public boolean actualizarPassword(int idUsuario, String nuevoHash) {
        String sql = "UPDATE usuario SET hash_password=? WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar contraseña", ex);
        }
    }

    public boolean actualizarUltimoLogin(int idUsuario, LocalDateTime fecha) {
        String sql = "UPDATE usuario SET ultimo_login=? WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fecha != null ? fecha : LocalDateTime.now()));
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar último login", ex);
        }
    }

    public boolean activar(int idUsuario) {
        String sql = "UPDATE usuario SET activo=TRUE WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al activar usuario", ex);
        }
    }

    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE usuario SET activo=FALSE WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al desactivar usuario", ex);
        }
    }

    // =========================
    // DELETE (opcional)
    // =========================
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario=?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            // En producción quizá prefieras baja lógica (activo=false)
            throw new RuntimeException("Error al eliminar usuario", ex);
        }
    }

    // =========================
    // LISTADOS / FILTROS
    // =========================
    public List<Usuario> listarTodos(int limit, int offset) {
        String sql = BASE_SELECT + "ORDER BY u.creado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar usuarios", ex);
        }
    }

    public List<Usuario> listarActivos(int limit, int offset) {
        String sql = BASE_SELECT + "WHERE u.activo = TRUE ORDER BY u.creado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar usuarios activos", ex);
        }
    }

    public List<Usuario> listarPorRol(int idRol, int limit, int offset) {
        String sql = BASE_SELECT + "WHERE u.id_rol = ? ORDER BY u.creado_en DESC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar usuarios por rol", ex);
        }
    }

    public List<Usuario> buscarPorNombreOCorreo(String query, int limit, int offset) {
        String like = "%" + query + "%";
        String sql = BASE_SELECT +
                "WHERE u.nombre LIKE ? OR u.apellido_paterno LIKE ? OR u.apellido_materno LIKE ? OR u.email LIKE ? " +
                "ORDER BY u.nombre ASC, u.apellido_paterno ASC LIMIT ? OFFSET ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setInt(5, limit);
            ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar usuarios", ex);
        }
    }

    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar usuarios", ex);
        }
    }

    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE activo=TRUE";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al contar usuarios activos", ex);
        }
    }

    // =========================
    // Helpers de mapeo
    // =========================
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidoPaterno(rs.getString("apellido_paterno"));
        u.setApellidoMaterno(rs.getString("apellido_materno"));
        u.setEmail(rs.getString("email"));
        u.setTelefono(rs.getString("telefono"));
        u.setIdLider(rs.getInt("id_lider"));
        u.setIdPuesto(rs.getInt("id_puesto"));
        u.setIdCentro(rs.getInt("id_centro"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setHashPassword(rs.getString("hash_password"));
        u.setActivo(rs.getBoolean("activo"));

        Timestamp tLogin = rs.getTimestamp("ultimo_login");
        u.setUltimoLogin(tLogin != null ? tLogin.toLocalDateTime() : null);

        Timestamp tCreado = rs.getTimestamp("creado_en");
        u.setCreadoEn(tCreado != null ? tCreado.toLocalDateTime() : null);

        // Campos derivados para UI
        u.setNombreRol(rs.getString("rol_nombre"));
        u.setNombrePuesto(rs.getString("puesto_nombre"));
        u.setNombreCentro(rs.getString("centro_nombre"));
        u.setNombreLider(rs.getString("lider_nombre"));

        return u;
    }

    private List<Usuario> mapList(ResultSet rs) throws SQLException {
        List<Usuario> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // Clase helper para login
    public static class LoginInfo {
        public int idUsuario;
        public String hashPassword;
        public int idRol;
        public boolean activo;
    }
}
