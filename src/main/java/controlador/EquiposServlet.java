package controlador;

import datos.EquipoDAO;
import datos.TipoEquipoDAO;
import datos.MarcaDAO;
import datos.ModeloDAO;
import datos.UbicacionDAO;
import datos.EstatusDAO;
import datos.AsignacionDAO;
import datos.EquipoSimDAO;
import datos.EquipoConsumibleDAO;
import datos.ColorDAO;

import modelo.Equipo;
import modelo.EquipoDetalle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/equipos")
public class EquiposServlet extends HttpServlet {

    private static final int UI_LIMIT = 1000; // para cargar catálogos en selects
    private static final int PAGE_SIZE = 50;  // para la tabla
    private static final int STATUS_ASIGNADO = 2; // ID para estatus "Asignado"

    private EquipoDAO equipoDAO;
    private TipoEquipoDAO tipoEquipoDAO;
    private MarcaDAO marcaDAO;
    private ModeloDAO modeloDAO;
    private UbicacionDAO ubicacionDAO;
    private EstatusDAO estatusDAO;
    private AsignacionDAO asignacionDAO;
    private EquipoSimDAO equipoSimDAO;
    private EquipoConsumibleDAO equipoConsumibleDAO;
    private ColorDAO colorDAO;

    @Override
    public void init() {
        this.equipoDAO     = new EquipoDAO();
        this.tipoEquipoDAO = new TipoEquipoDAO();
        this.marcaDAO      = new MarcaDAO();
        this.modeloDAO     = new ModeloDAO();
        this.ubicacionDAO  = new UbicacionDAO();
        this.estatusDAO    = new EstatusDAO();
        this.asignacionDAO = new AsignacionDAO();
        this.equipoSimDAO  = new EquipoSimDAO();
        this.equipoConsumibleDAO = new EquipoConsumibleDAO();
        this.colorDAO = new ColorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        System.out.println("[EquiposServlet] action=" + action + " id=" + idParam);

        if ("get".equals(action)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id parameter\"}");
                return;
            }
            try {
                int id = Integer.parseInt(idParam.trim());
                Equipo e = equipoDAO.obtenerPorId(id);
                if (e == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Equipment not found\"}");
                    return;
                }
                resp.getWriter().write(new Gson().toJson(e));
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid id format\"}");
            } catch (Exception ex) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Internal server error\"}");
            }
            return;
        }

        // Devuelve asignaciones del equipo (historial completo)
        if ("asignaciones".equals(action)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id parameter\"}");
                return;
            }
            try {
                int id = Integer.parseInt(idParam.trim());
                // Historial completo, limitar a 1000 por seguridad
                List<modelo.Asignacion> list = asignacionDAO.listarPorEquipo(id, true, 1000, 0);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class,
                                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                        .create();
                resp.getWriter().write(gson.toJson(list));
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid id format\"}");
            } catch (Exception ex) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Internal server error\"}");
            }
            return;
        }

        // ===== Listado con filtros =====
        Integer idTipo      = parseIntOrNull(req.getParameter("idTipo"));
        Integer idMarca     = parseIntOrNull(req.getParameter("idMarca"));
        Integer idModelo    = parseIntOrNull(req.getParameter("idModelo"));
        Integer idEstatus   = parseIntOrNull(req.getParameter("idEstatus"));
        Integer idUbicacion = parseIntOrNull(req.getParameter("idUbicacion"));
        String q            = trimToNull(req.getParameter("q"));

        int page   = Math.max(1, parseIntOrDefault(req.getParameter("page"), 1));
        int limit  = PAGE_SIZE;
        int offset = (page - 1) * limit;

        List<EquipoDetalle> equipos = equipoDAO.listarConDetalle(
                idTipo, idMarca, idModelo, idEstatus, idUbicacion, q, limit, offset);

        // ===== Catálogos usando TU API =====
        req.setAttribute("tipos",       tipoEquipoDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("marcas",      marcaDAO.listarActivas(UI_LIMIT, 0));   // o listarTodos(...)
        req.setAttribute("modelos",     modeloDAO.listarTodos(UI_LIMIT, 0));    // o filtrar por marca vía AJAX si luego quieres
        req.setAttribute("ubicaciones", ubicacionDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("estatuses",   estatusDAO.listarPorTipo("EQUIPO"));    // ajusta el literal si tu BD usa otro valor
        req.setAttribute("colores",     colorDAO.listarTodos(UI_LIMIT, 0));

        // ===== Atributos de vista =====
        req.setAttribute("equipos", equipos);
        req.setAttribute("page", page);
        req.setAttribute("q", q);
        req.setAttribute("idTipo", idTipo);
        req.setAttribute("idMarca", idMarca);
        req.setAttribute("idModelo", idModelo);
        req.setAttribute("idEstatus", idEstatus);
        req.setAttribute("idUbicacion", idUbicacion);

        // JSP (ajusta la ruta si está en /WEB-INF/views/)
        req.getRequestDispatcher("equipos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            try {
                String idParam = req.getParameter("idEquipo");
                if (idParam == null || idParam.trim().isEmpty()) {
                    req.getSession().setAttribute("flashError", "Missing equipment ID.");
                    resp.sendRedirect(req.getContextPath() + "/equipos");
                    return;
                }
                int id = Integer.parseInt(idParam.trim());
                equipoDAO.eliminar(id);
                req.getSession().setAttribute("flashOk", "Equipo eliminado.");
            } catch (NumberFormatException ex) {
                req.getSession().setAttribute("flashError", "Invalid equipment ID format.");
            } catch (IllegalStateException fk) {
                req.getSession().setAttribute("flashError", "No se puede eliminar: el equipo tiene dependencias.");
            } catch (Exception ex) {
                req.getSession().setAttribute("flashError", "Error al eliminar equipo.");
            }
            resp.sendRedirect(req.getContextPath() + "/equipos");
            return;
        }

        if ("create".equals(action)) {
            try {
                Equipo e = new Equipo();
                e.setIdTipo(Integer.parseInt(req.getParameter("idTipo").trim()));
                e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
                e.setNumeroSerie(req.getParameter("numeroSerie"));
                e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
                e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
                e.setIdEstatus(Integer.parseInt(req.getParameter("idEstatus").trim()));
                e.setIpFija(emptyToNull(req.getParameter("ipFija")));
                e.setPuertoEthernet(emptyToNull(req.getParameter("puertoEthernet")));
                e.setNotas(emptyToNull(req.getParameter("notas")));

                int newId = equipoDAO.crear(e);
                e.setIdEquipo(newId);

                String tipoNombre = req.getParameter("tipoNombre");
                String tn = tipoNombre != null ? tipoNombre.trim().toUpperCase() : "";

                // SIM
                if (tn.contains("SIM")) {
                    String num = emptyToNull(req.getParameter("simNumeroAsignado"));
                    String imei = emptyToNull(req.getParameter("simImei"));
                    if (num != null || imei != null) {
                        modelo.EquipoSim sim = new modelo.EquipoSim();
                        sim.setIdEquipo(newId);
                        sim.setNumeroAsignado(num);
                        sim.setImei(imei);
                        boolean okSim = equipoSimDAO.crear(sim);
                        if (!okSim) throw new RuntimeException("No se pudo crear registro SIM.");
                    }
                }
                // Consumible
                if (tn.contains("CONSUM")) {
                    Integer idColor = parseIntOrNull(req.getParameter("idColorConsumible"));
                    if (idColor != null) {
                        modelo.EquipoConsumible ec = new modelo.EquipoConsumible();
                        ec.setIdEquipo(newId);
                        ec.setIdColor(idColor);
                        boolean okEc = equipoConsumibleDAO.crear(ec);
                        if (!okEc) throw new RuntimeException("No se pudo crear registro Consumible.");
                    }
                }

                req.getSession().setAttribute("flashOk", "Equipo creado correctamente (ID " + newId + ").");
            } catch (NumberFormatException ex) {
                req.getSession().setAttribute("flashError", "Formato numérico inválido en los datos del formulario.");
            } catch (IllegalArgumentException iae) {
                req.getSession().setAttribute("flashError", iae.getMessage());
            } catch (Exception ex) {
                req.getSession().setAttribute("flashError", "Error al crear equipo.");
            }
            resp.sendRedirect(req.getContextPath() + "/equipos");
            return;
        }

        if ("save".equals(action)) {
            try {
                // Obtener estatus previo
                int idEquipo = Integer.parseInt(req.getParameter("idEquipo").trim());
                Equipo previo = equipoDAO.obtenerPorId(idEquipo);

                // Construir el objeto actualizado
                Equipo e = new Equipo();
                e.setIdEquipo(idEquipo);
                e.setIdTipo(Integer.parseInt(req.getParameter("idTipo").trim()));
                e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
                e.setNumeroSerie(req.getParameter("numeroSerie")); // requerido por tu DAO
                e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
                e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
                int nuevoEstatus = Integer.parseInt(req.getParameter("idEstatus").trim());
                e.setIdEstatus(nuevoEstatus);
                e.setIpFija(emptyToNull(req.getParameter("ipFija")));
                e.setPuertoEthernet(emptyToNull(req.getParameter("puertoEthernet")));
                e.setNotas(emptyToNull(req.getParameter("notas")));

                // Determinar si cambió de ASIGNADO -> otro
                boolean fromAsignadoToOtro = previo != null
                        && previo.getIdEstatus() == STATUS_ASIGNADO
                        && nuevoEstatus != STATUS_ASIGNADO;

                boolean ok = equipoDAO.actualizar(e);
                if (ok && fromAsignadoToOtro) {
                    try {
                        int eliminadas = asignacionDAO.eliminarPorEquipo(e.getIdEquipo());
                        req.getSession().setAttribute("flashOk",
                                "Equipo actualizado." + (eliminadas > 0 ? (" Se eliminó la asignación.") : ""));
                    } catch (Exception cleanEx) {
                        req.getSession().setAttribute("flashError",
                                "Equipo actualizado, pero ocurrió un error al eliminar asignaciones.");
                    }
                } else {
                    req.getSession().setAttribute(ok ? "flashOk" : "flashError",
                            ok ? "Equipo actualizado." : "No se actualizó el equipo.");
                }
            } catch (NumberFormatException ex) {
                req.getSession().setAttribute("flashError", "Invalid number format in form data.");
            } catch (IllegalArgumentException iae) {
                req.getSession().setAttribute("flashError", "Número de serie duplicado o referencia inválida.");
            } catch (Exception ex) {
                req.getSession().setAttribute("flashError", "Error al actualizar equipo.");
            }
            resp.sendRedirect(req.getContextPath() + "/equipos");
        }
    }

    // ===== Helpers Java 8 =====
    private static Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception ex) { return null; }
    }

    private static int parseIntOrDefault(String s, int d) {
        try { return (s == null || s.trim().isEmpty()) ? d : Integer.parseInt(s.trim()); }
        catch (Exception ex) { return d; }
    }

    private static String emptyToNull(String s) {
        return (s == null) ? null : (s.trim().isEmpty() ? null : s.trim());
    }

    private static String trimToNull(String s) {
        return emptyToNull(s);
    }
}
