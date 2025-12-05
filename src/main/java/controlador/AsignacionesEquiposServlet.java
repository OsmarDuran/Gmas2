package controlador;

import com.google.gson.Gson;
import datos.AsignacionDAO;
import datos.EquipoDAO;
import datos.UsuarioDAO;
import modelo.Asignacion;
import modelo.EquipoDetalle;
import modelo.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "AsignacionesEquiposServlet", urlPatterns = {"/asignaciones_equipos"})
public class AsignacionesEquiposServlet extends HttpServlet {

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final AsignacionDAO asignacionDAO = new AsignacionDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "lista";
        }

        switch (accion) {
            case "lista":
                listarEquiposConAsignaciones(request, response);
                break;
            case "historial":
                obtenerHistorialEquipo(request, response);
                break;
            case "asignacion_actual":
                obtenerAsignacionActual(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
        }
    }

    /**
     * Carga la tabla principal de equipos + información de asignación actual (vista JSP).
     */
    private void listarEquiposConAsignaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int limit = 1000;
        int offset = 0;

        // Obtener todos los equipos con detalles (sin filtros = todos)
        List<EquipoDetalle> equipos = equipoDAO.listarConDetalle(null, null, null, null, null, null, limit, offset);

        // Para cada equipo, obtener la asignación actual (si existe)
        Map<Integer, String> asignacionActual = new HashMap<>();
        for (EquipoDetalle eq : equipos) {
            List<Asignacion> asignaciones = asignacionDAO.listarPorEquipo(eq.getIdEquipo(), false, 1, 0);
            if (!asignaciones.isEmpty()) {
                Asignacion asig = asignaciones.get(0);
                asignacionActual.put(eq.getIdEquipo(), asig.getUsuarioNombre());
            } else {
                asignacionActual.put(eq.getIdEquipo(), null);
            }
        }

        request.setAttribute("listaEquipos", equipos);
        request.setAttribute("asignacionActual", asignacionActual);

        request.getRequestDispatcher("/asignaciones_equipos.jsp")
                .forward(request, response);
    }

    /**
     * Devuelve JSON con el historial completo de asignaciones del equipo.
     */
    private void obtenerHistorialEquipo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("idEquipo");
        int idEquipo;
        try {
            idEquipo = Integer.parseInt(idStr);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "idEquipo inválido");
            return;
        }

        try {
            // Obtener historial completo (incluye activas y devueltas)
            List<Asignacion> historial = asignacionDAO.listarPorEquipo(idEquipo, true, 100, 0);

            // Convertir a formato JSON simple
            List<Map<String, Object>> asignaciones = new ArrayList<>();
            for (Asignacion a : historial) {
                Map<String, Object> m = new HashMap<>();
                m.put("idAsignacion", a.getIdAsignacion());
                m.put("idUsuario", a.getIdUsuario());
                m.put("usuarioNombre", a.getUsuarioNombre() != null ? a.getUsuarioNombre() : "—");
                m.put("asignadoPor", a.getAsignadoPor());
                m.put("asignadorNombre", a.getAsignadorNombre() != null ? a.getAsignadorNombre() : "—");
                m.put("asignadoEn", a.getAsignadoEn() != null ? a.getAsignadoEn().toString() : null);
                m.put("devueltoEn", a.getDevueltoEn() != null ? a.getDevueltoEn().toString() : null);
                m.put("rutaPdf", a.getRutaPdf());
                asignaciones.add(m);
            }

            Map<String, Object> json = new HashMap<>();
            json.put("asignaciones", asignaciones);

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(json));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener historial de asignaciones");
        }
    }

    /**
     * Devuelve JSON con la asignación actual del equipo + lista de usuarios disponibles.
     */
    private void obtenerAsignacionActual(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("idEquipo");
        int idEquipo;
        try {
            idEquipo = Integer.parseInt(idStr);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "idEquipo inválido");
            return;
        }

        try {
            // Obtener asignación activa (si existe)
            List<Asignacion> asignaciones = asignacionDAO.listarPorEquipo(idEquipo, false, 1, 0);

            Map<String, Object> asignacionActual = null;
            if (!asignaciones.isEmpty()) {
                Asignacion a = asignaciones.get(0);
                asignacionActual = new HashMap<>();
                asignacionActual.put("idAsignacion", a.getIdAsignacion());
                asignacionActual.put("idUsuario", a.getIdUsuario());
                asignacionActual.put("usuarioNombre", a.getUsuarioNombre() != null ? a.getUsuarioNombre() : "—");
                asignacionActual.put("asignadoEn", a.getAsignadoEn() != null ? a.getAsignadoEn().toString() : null);
            }

            // Obtener lista de usuarios activos
            List<Usuario> usuarios = usuarioDAO.listarActivos(500, 0);
            List<Map<String, Object>> usuariosDisponibles = new ArrayList<>();
            for (Usuario u : usuarios) {
                Map<String, Object> m = new HashMap<>();
                m.put("idUsuario", u.getIdUsuario());
                String nombreCompleto = u.getNombre() + " " + u.getApellidoPaterno() + 
                        (u.getApellidoMaterno() != null ? " " + u.getApellidoMaterno() : "");
                m.put("nombreCompleto", nombreCompleto.trim());
                usuariosDisponibles.add(m);
            }

            Map<String, Object> json = new HashMap<>();
            json.put("asignacionActual", asignacionActual);
            json.put("usuariosDisponibles", usuariosDisponibles);

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(json));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener datos de asignación");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("asignar".equalsIgnoreCase(accion)) {
            asignarEquipo(request, response);
        } else if ("devolver".equalsIgnoreCase(accion)) {
            devolverEquipo(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
        }
    }

    /**
     * Asigna un equipo a un usuario.
     */
    private void asignarEquipo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        // Obtener usuario de la sesión
        Integer idUsuarioAccion = (Integer) request.getSession().getAttribute("userId");
        if (idUsuarioAccion == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "No hay sesión activa. Por favor, inicia sesión nuevamente.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // Parsear JSON del body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Error al leer el cuerpo de la petición.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        String jsonBody = jsonBuffer.toString();
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "El cuerpo de la petición está vacío.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        Map<String, Object> payload;
        try {
            payload = gson.fromJson(jsonBody, Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "JSON inválido.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // Extraer datos
        int idEquipo;
        int idUsuario;
        try {
            Object idEquipoObj = payload.get("idEquipo");
            Object idUsuarioObj = payload.get("idUsuario");

            if (idEquipoObj instanceof Double) {
                idEquipo = ((Double) idEquipoObj).intValue();
            } else {
                idEquipo = Integer.parseInt(idEquipoObj.toString());
            }

            if (idUsuarioObj instanceof Double) {
                idUsuario = ((Double) idUsuarioObj).intValue();
            } else {
                idUsuario = Integer.parseInt(idUsuarioObj.toString());
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Datos inválidos.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        try {
            // Verificar que el equipo no esté ya asignado
            List<Asignacion> asignaciones = asignacionDAO.listarPorEquipo(idEquipo, false, 1, 0);
            if (!asignaciones.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("ok", false);
                error.put("error", "El equipo ya está asignado a " + asignaciones.get(0).getUsuarioNombre());
                try (PrintWriter out = response.getWriter()) {
                    out.print(gson.toJson(error));
                }
                return;
            }

            // Crear asignación
            asignacionDAO.crearAsignacion(idEquipo, idUsuario, idUsuarioAccion, null);

            Map<String, Object> success = new HashMap<>();
            success.put("ok", true);
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(success));
            }

        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Error al asignar el equipo: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
        }
    }

    /**
     * Devuelve un equipo (marca como devuelta su asignación activa).
     */
    private void devolverEquipo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        // Obtener usuario de la sesión
        Integer idUsuarioAccion = (Integer) request.getSession().getAttribute("userId");
        if (idUsuarioAccion == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "No hay sesión activa. Por favor, inicia sesión nuevamente.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // Parsear JSON del body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Error al leer el cuerpo de la petición.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        String jsonBody = jsonBuffer.toString();
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "El cuerpo de la petición está vacío.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        Map<String, Object> payload;
        try {
            payload = gson.fromJson(jsonBody, Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "JSON inválido.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // Extraer idEquipo
        int idEquipo;
        try {
            Object idEquipoObj = payload.get("idEquipo");
            if (idEquipoObj instanceof Double) {
                idEquipo = ((Double) idEquipoObj).intValue();
            } else {
                idEquipo = Integer.parseInt(idEquipoObj.toString());
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Datos inválidos.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        try {
            // Buscar asignación activa del equipo
            List<Asignacion> asignaciones = asignacionDAO.listarPorEquipo(idEquipo, false, 1, 0);
            if (asignaciones.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("ok", false);
                error.put("error", "El equipo no tiene una asignación activa.");
                try (PrintWriter out = response.getWriter()) {
                    out.print(gson.toJson(error));
                }
                return;
            }

            Asignacion asignacion = asignaciones.get(0);
            boolean devuelto = asignacionDAO.marcarDevuelto(asignacion.getIdAsignacion(), null);

            if (devuelto) {
                Map<String, Object> success = new HashMap<>();
                success.put("ok", true);
                try (PrintWriter out = response.getWriter()) {
                    out.print(gson.toJson(success));
                }
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("ok", false);
                error.put("error", "No se pudo marcar como devuelto.");
                try (PrintWriter out = response.getWriter()) {
                    out.print(gson.toJson(error));
                }
            }

        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Error al devolver el equipo: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
        }
    }
}
