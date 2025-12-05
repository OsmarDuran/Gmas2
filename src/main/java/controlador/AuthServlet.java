package controlador;

import seguridad.PasswordUtil;
import datos.UsuarioDAO;
import datos.LiderDAO;
import datos.PuestoDAO;
import datos.CentroDAO;
import modelo.Usuario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import util.EmailService;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    // Rol por defecto para registro
    private static final int ROLE_EMPLOYEE_ID = 1;

    private UsuarioDAO usuarioDAO;
    private LiderDAO liderDAO;
    private PuestoDAO puestoDAO;
    private CentroDAO centroDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioDAO = new UsuarioDAO();
        this.liderDAO   = new LiderDAO();
        this.puestoDAO  = new PuestoDAO();
        this.centroDAO  = new CentroDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession s = req.getSession(false);
            if (s != null) s.invalidate();
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        if ("catalogs".equalsIgnoreCase(action)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try {
                Map<String, Object> out = new HashMap<>();
                out.put("centros", centroDAO.listarTodos(1000, 0));
                out.put("puestos", puestoDAO.listarTodos(1000, 0));
                out.put("lideres", liderDAO.listarTodos(1000, 0));
                String json = new GsonBuilder().create().toJson(out);
                resp.getWriter().write(json);
            } catch (Exception ex) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"No fue posible cargar catálogos\"}");
            }
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(req, resp);
            return;
        }
        if ("login".equalsIgnoreCase(action)) {
            handleLogin(req, resp);
            return;
        }

        if ("forgot-start".equalsIgnoreCase(action)) {
            handleForgotStart(req, resp);
            return;
        }
        if ("forgot-complete".equalsIgnoreCase(action)) {
            handleForgotComplete(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nombre  = trim(req.getParameter("nombre"));
        String apPat   = trim(req.getParameter("apellidoPaterno"));
        String apMat   = trim(req.getParameter("apellidoMaterno"));
        String email   = normalizeEmail(req.getParameter("email"));
        String tel     = trim(req.getParameter("telefono"));
        String pass1   = req.getParameter("password");
        String pass2   = req.getParameter("password2");

        Integer idCentro = parseInt(req.getParameter("idCentro"));
        Integer idPuesto = parseInt(req.getParameter("idPuesto"));
        Integer idLider  = parseInt(req.getParameter("idLider"));

        // Helper para persistir datos del formulario en sesión (sin contraseñas)
        Runnable saveRegData = () -> {
            HttpSession s = req.getSession(true);
            s.setAttribute("regOpen", Boolean.TRUE);
            s.setAttribute("reg_nombre", nombre);
            s.setAttribute("reg_apellidoPaterno", apPat);
            s.setAttribute("reg_apellidoMaterno", apMat);
            s.setAttribute("reg_email", email);
            s.setAttribute("reg_telefono", tel);
            s.setAttribute("reg_idCentro", idCentro);
            s.setAttribute("reg_idPuesto", idPuesto);
            s.setAttribute("reg_idLider",  idLider);
        };


        if (isEmpty(nombre) || isEmpty(email) || isEmpty(pass1) || isEmpty(pass2)
                || idCentro == null || idPuesto == null || idLider == null) {
            saveRegData.run();
            flash(req, "regMsg", "Completa los campos obligatorios (incluye Centro, Puesto y Líder).");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        if (!pass1.equals(pass2)) {
            saveRegData.run();
            flash(req, "regMsg", "Las contraseñas no coinciden.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        if (!isStrong(pass1)) {
            saveRegData.run();
            flash(req, "regMsg", "La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula y número.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Verificar duplicado de email
        UsuarioDAO.LoginInfo existing = usuarioDAO.obtenerLoginInfo(email);
        if (existing != null) {
            saveRegData.run();
            flash(req, "regMsg", "El email ya está registrado.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Hash y alta
        String hash = PasswordUtil.hash(pass1.toCharArray());
        try {
            boolean creado = crearUsuarioEnBD(nombre, apPat, apMat, email, tel, hash, idCentro, idPuesto, idLider);
            if (!creado) {
                saveRegData.run();
                flash(req, "regMsg", "No fue posible crear la cuenta.");
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }

            // Éxito: mensaje para login y limpiar datos de registro en sesión
            clearRegData(req.getSession(true));
            flash(req, "flashOk", "Cuenta creada. Ya puedes iniciar sesión.");
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (Exception ex) {
            saveRegData.run();
            flash(req, "regMsg", "Error al registrar. Intenta más tarde.");
            resp.sendRedirect(req.getContextPath() + "/");
        }

    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = normalizeEmail(req.getParameter("email"));
        String pass  = req.getParameter("password");

        if (isEmpty(email) || isEmpty(pass)) {
            flash(req, "flashError", "Ingresa email y contraseña.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            UsuarioDAO.LoginInfo info = usuarioDAO.obtenerLoginInfo(email);
            if (info == null) {
                flash(req, "flashError", "Credenciales inválidas.");
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }
            if (!info.activo) {
                flash(req, "flashError", "La cuenta está inactiva. Contacta al administrador.");
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }
            boolean ok = PasswordUtil.verify(pass.toCharArray(), info.hashPassword);
            if (!ok) {
                flash(req, "flashError", "Credenciales inválidas.");
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }

            usuarioDAO.actualizarUltimoLogin(info.idUsuario, LocalDateTime.now());
            HttpSession s = req.getSession(true);
            s.setAttribute("userId", info.idUsuario);
            s.setAttribute("userEmail", email);
            s.setAttribute("userRolId", info.idRol);
            resp.sendRedirect(req.getContextPath() + "/equipos.jsp");
        } catch (Exception ex) {
            flash(req, "flashError", "Error al iniciar sesión. Intenta más tarde.");
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    // Reglas mínimas de complejidad
    private boolean isStrong(String p) {
        if (p == null || p.length() < 8) return false;
        boolean up = false, low = false, dig = false;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (Character.isUpperCase(c)) up = true;
            else if (Character.isLowerCase(c)) low = true;
            else if (Character.isDigit(c)) dig = true;
            if (up && low && dig) return true;
        }
        return false;
    }

    private static Integer parseInt(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }
    private static String normalizeEmail(String s) { return s == null ? null : s.trim().toLowerCase(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }

    private static void flash(HttpServletRequest req, String key, String msg) {
        HttpSession session = req.getSession(true);
        session.setAttribute(key, msg);
    }

    private static void clearRegData(HttpSession s) {
        if (s == null) return;
        s.removeAttribute("regOpen");
        s.removeAttribute("regMsg");
        s.removeAttribute("reg_nombre");
        s.removeAttribute("reg_apellidoPaterno");
        s.removeAttribute("reg_apellidoMaterno");
        s.removeAttribute("reg_email");
        s.removeAttribute("reg_telefono");
        s.removeAttribute("reg_idCentro");
        s.removeAttribute("reg_idPuesto");
        s.removeAttribute("reg_idLider");
    }


    // Inserción real usando UsuarioDAO (sin defaults de FK; vienen del formulario)
    private boolean crearUsuarioEnBD(String nombre, String apPat, String apMat, String email, String tel,
                                     String hashPassword, int idCentro, int idPuesto, int idLider) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellidoPaterno(apPat);
        u.setApellidoMaterno(apMat);
        u.setEmail(email);
        u.setTelefono(tel);
        u.setIdCentro(idCentro);
        u.setIdPuesto(idPuesto);
        u.setIdLider(idLider);
        u.setIdRol(ROLE_EMPLOYEE_ID);
        u.setHashPassword(hashPassword);
        u.setActivo(true);
        u.setUltimoLogin(null);
        u.setCreadoEn(LocalDateTime.now());

        int newId = usuarioDAO.crear(u);
        return newId > 0;
    }

    private void handleForgotStart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(true);
        String email = normalizeEmail(req.getParameter("email"));
        if (isEmpty(email)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Email requerido\"}");
            return;
        }

        // No revelar existencia de la cuenta
        UsuarioDAO.LoginInfo info = usuarioDAO.obtenerLoginInfo(email);
        if (info == null) {
            resp.getWriter().write("{\"ok\":true}");
            return;
        }

        String code = String.format("%06d", new java.security.SecureRandom().nextInt(1_000_000));
        long expiresAt = System.currentTimeMillis() + (10 * 60 * 1000L);

        s.setAttribute("fp_email", email);
        s.setAttribute("fp_code", code);
        s.setAttribute("fp_expires", expiresAt);

        boolean debug = Boolean.parseBoolean(System.getenv().getOrDefault("FORGOT_DEBUG", "false"));

        try {
            String subject = "Código de recuperación";
            String body = "Hola,\n\n" +
                    "Tu código para recuperar la contraseña es: " + code + "\n" +
                    "Este código expira en 10 minutos.\n\n" +
                    "Si no solicitaste este código, ignora este correo.\n";
            EmailService.send(email, subject, body);
            resp.getWriter().write("{\"ok\":true}");
        } catch (Exception ex) {
            if (debug) {
                // Expón el error y el stack más relevante en logs y JSON para diagnosticar
                ex.printStackTrace();
                String msg = (ex.getMessage() == null ? "Fallo SMTP" : ex.getMessage()).replace("\"","'");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"ok\":false,\"error\":\"" + msg + "\"}");
            } else {
                // En producción: no filtrar detalles
                resp.getWriter().write("{\"ok\":true}");
            }
        }
    }


    private void handleForgotComplete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(false);
        if (s == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Sesión expirada\"}");
            return;
        }

        String code = req.getParameter("code") != null ? req.getParameter("code").trim() : "";
        String pass = req.getParameter("password");

        String email = (String) s.getAttribute("fp_email");
        String storedCode = (String) s.getAttribute("fp_code");
        Long expiresAt = (Long) s.getAttribute("fp_expires");

        if (email == null || storedCode == null || expiresAt == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Proceso no iniciado o expirado\"}");
            return;
        }
        if (System.currentTimeMillis() > expiresAt) {
            clearForgotSession(s);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"El código ha expirado\"}");
            return;
        }
        if (!storedCode.equals(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Código incorrecto\"}");
            return;
        }
        if (!isStrong(pass)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"error\":\"La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula y número\"}");
            return;
        }

        try {
            String hash = PasswordUtil.hash(pass.toCharArray());
            UsuarioDAO.LoginInfo info = usuarioDAO.obtenerLoginInfo(email);
            // Aunque no exista (cuenta borrada), “éxito” para no filtrar información
            if (info != null) usuarioDAO.actualizarPassword(info.idUsuario, hash);
            clearForgotSession(s);
            resp.getWriter().write("{\"ok\":true}");
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Error interno al actualizar contraseña\"}");
        }
    }

    private static void clearForgotSession(HttpSession s) {
        if (s == null) return;
        s.removeAttribute("fp_email");
        s.removeAttribute("fp_code");
        s.removeAttribute("fp_expires");
    }


}
