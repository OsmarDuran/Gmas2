package controlador;

import com.google.gson.Gson;
import datos.AsignacionDAO;
import datos.EquipoDAO;
import datos.UsuarioDAO;
import modelo.EquipoDetalle;
import modelo.Usuario;
import modelo.Asignacion;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "AsignacionesUsuariosServlet", urlPatterns = {"/asignaciones_usuarios"})
public class AsignacionesUsuariosServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AsignacionDAO asignacionDAO = new AsignacionDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO();
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
                listarUsuariosConAsignaciones(request, response);
                break;
            case "datos":
                obtenerDatosUsuarioEquipos(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
        }
    }

    /** Carga la tabla principal de usuarios + conteo de asignaciones (vista JSP). */
    private void listarUsuariosConAsignaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int limit = 500;
        int offset = 0;

        List<Usuario> usuarios = usuarioDAO.listarActivos(limit, offset);

        Map<Integer, Integer> conteoAsignaciones = new HashMap<>();
        for (Usuario u : usuarios) {
            int total = asignacionDAO.contarAsignacionesActivasPorUsuario(u.getIdUsuario());
            conteoAsignaciones.put(u.getIdUsuario(), total);
        }

        request.setAttribute("listaUsuarios", usuarios);
        request.setAttribute("conteoAsignaciones", conteoAsignaciones);

        request.getRequestDispatcher("/asignaciones_usuarios.jsp")
                .forward(request, response);
    }

    /** Devuelve JSON para llenar el modal (equipos asignados / disponibles). */
    private void obtenerDatosUsuarioEquipos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("idUsuario");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(idStr);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "idUsuario inválido");
            return;
        }

        try {
            // Equipos asignados al usuario
            List<EquipoDetalle> asignadosDet =
                    equipoDAO.listarAsignadosAUsuario(idUsuario, 500, 0); // tu método

            // Equipos disponibles (estatus = 1)
            List<EquipoDetalle> disponiblesDet =
                    equipoDAO.listarDisponibles(500, 0);

            List<Map<String, Object>> asignados = new ArrayList<>();
            for (EquipoDetalle d : asignadosDet) {
                Map<String,Object> m = new HashMap<>();
                m.put("idEquipo", d.getIdEquipo());
                m.put("tipoNombre",  d.getTipoNombre()      != null ? d.getTipoNombre()      : "—");
                m.put("numeroSerie", d.getNumeroSerie()     != null ? d.getNumeroSerie()     : "—");
                m.put("ubicacionNombre", d.getUbicacionNombre() != null ? d.getUbicacionNombre() : "—");
                asignados.add(m);
            }

            List<Map<String, Object>> disponibles = new ArrayList<>();
            for (EquipoDetalle d : disponiblesDet) {
                Map<String,Object> m = new HashMap<>();
                m.put("idEquipo", d.getIdEquipo());
                m.put("tipoNombre",  d.getTipoNombre()      != null ? d.getTipoNombre()      : "—");
                m.put("numeroSerie", d.getNumeroSerie()     != null ? d.getNumeroSerie()     : "—");
                m.put("ubicacionNombre", d.getUbicacionNombre() != null ? d.getUbicacionNombre() : "—");
                disponibles.add(m);
            }

            Map<String, Object> json = new HashMap<>();
            json.put("asignados", asignados);
            json.put("disponibles", disponibles);

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(json));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener datos de asignaciones");
        }
    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("guardar".equalsIgnoreCase(accion)) {
            guardarCambiosAsignaciones(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
        }
    }

    /**
     * Guarda los cambios de asignaciones (asignar/desasignar equipos a un usuario).
     * Recibe un JSON con: {idUsuario, asignar:[], desasignar:[]}.
     * Devuelve JSON con resultados por equipo.
     */
    private void guardarCambiosAsignaciones(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        // 1. Obtener usuario de la sesión (quien realiza la acción)
        Integer idUsuarioAccion = (Integer) request.getSession().getAttribute("userId");
        if (idUsuarioAccion == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "No hay sesión activa. Por favor, inicia sesión nuevamente.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // 2. Parsear el payload JSON del body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "Error al leer el cuerpo de la petición.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        String jsonBody = jsonBuffer.toString();
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "El cuerpo de la petición está vacío.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // Parsear JSON
        Map<String, Object> payload;
        try {
            payload = gson.fromJson(jsonBody, Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "JSON inválido.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        // 3. Extraer datos del payload
        Object idUsuarioObj = payload.get("idUsuario");
        if (idUsuarioObj == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "Falta el campo idUsuario en el JSON.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        int idUsuario;
        try {
            // Gson puede parsear números como Double
            if (idUsuarioObj instanceof Double) {
                idUsuario = ((Double) idUsuarioObj).intValue();
            } else {
                idUsuario = Integer.parseInt(idUsuarioObj.toString());
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorGeneral", "idUsuario inválido.");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(error));
            }
            return;
        }

        List<Integer> asignar = parseIntList(payload.get("asignar"));
        List<Integer> desasignar = parseIntList(payload.get("desasignar"));

        // 4. Procesar asignaciones y desasignaciones
        List<Map<String, Object>> resultados = new ArrayList<>();

        // Procesar ASIGNACIONES (crear nuevas)
        for (Integer idEquipo : asignar) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("idEquipo", idEquipo);

            try {
                // Verificar si el equipo ya tiene una asignación activa
                List<Asignacion> asignacionesActivas = asignacionDAO.listarPorEquipo(idEquipo, false, 1, 0);

                if (!asignacionesActivas.isEmpty()) {
                    // El equipo ya está asignado a alguien
                    Asignacion asigActual = asignacionesActivas.get(0);
                    resultado.put("ok", false);
                    resultado.put("mensaje", "El equipo ya está asignado a " + asigActual.getUsuarioNombre());
                } else {
                    // Crear nueva asignación
                    asignacionDAO.crearAsignacion(idEquipo, idUsuario, idUsuarioAccion, null);
                    resultado.put("ok", true);
                }
            } catch (IllegalStateException e) {
                // Error de negocio desde triggers/reglas
                resultado.put("ok", false);
                resultado.put("mensaje", e.getMessage());
            } catch (Exception e) {
                resultado.put("ok", false);
                resultado.put("mensaje", "Error al asignar: " + e.getMessage());
            }

            resultados.add(resultado);
        }

        // Procesar DESASIGNACIONES (marcar como devueltas)
        for (Integer idEquipo : desasignar) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("idEquipo", idEquipo);

            try {
                // Buscar la asignación activa del equipo al usuario específico
                List<Asignacion> asignacionesActivas = asignacionDAO.listarPorEquipo(idEquipo, false, 10, 0);

                // Buscar la que corresponde al usuario específico
                Asignacion asignacionUsuario = null;
                for (Asignacion a : asignacionesActivas) {
                    if (a.getIdUsuario() == idUsuario) {
                        asignacionUsuario = a;
                        break;
                    }
                }

                if (asignacionUsuario == null) {
                    // No existe asignación activa del equipo a este usuario
                    resultado.put("ok", false);
                    resultado.put("mensaje", "El equipo no está asignado a este usuario");
                } else {
                    // Marcar como devuelta
                    boolean devuelto = asignacionDAO.marcarDevuelto(asignacionUsuario.getIdAsignacion(), null);
                    if (devuelto) {
                        resultado.put("ok", true);
                    } else {
                        resultado.put("ok", false);
                        resultado.put("mensaje", "No se pudo marcar como devuelto");
                    }
                }
            } catch (IllegalStateException e) {
                // Error de negocio desde triggers/reglas
                resultado.put("ok", false);
                resultado.put("mensaje", e.getMessage());
            } catch (Exception e) {
                resultado.put("ok", false);
                resultado.put("mensaje", "Error al desasignar: " + e.getMessage());
            }

            resultados.add(resultado);
        }

        // 5. Construir respuesta JSON
        Map<String, Object> respuestaJson = new HashMap<>();
        respuestaJson.put("resultados", resultados);

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(respuestaJson));
        }
    }

    /**
     * Helper para parsear listas de enteros desde el JSON (Gson puede devolverlas como List<Double>).
     */
    private List<Integer> parseIntList(Object obj) {
        List<Integer> result = new ArrayList<>();
        if (obj == null) return result;

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                try {
                    if (item instanceof Double) {
                        result.add(((Double) item).intValue());
                    } else if (item instanceof Integer) {
                        result.add((Integer) item);
                    } else {
                        result.add(Integer.parseInt(item.toString()));
                    }
                } catch (Exception e) {
                    // Ignorar elementos inválidos
                }
            }
        }
        return result;
    }
}
