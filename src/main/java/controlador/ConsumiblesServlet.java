package controlador;

import com.google.gson.Gson;
import datos.*;
import modelo.BitacoraMovimiento;
import modelo.Equipo;
import modelo.EquipoDetalle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/** Servlet para administrar equipos de tipo CONSUMIBLE (+ tabla equipo_consumible). */
@WebServlet("/consumibles")
public class ConsumiblesServlet extends HttpServlet {
    private static final int UI_LIMIT = 1000;
    private static final int PAGE_SIZE = 12;
    private static final int STATUS_ASIGNADO = 2;
    private static final int STATUS_ELIMINADO = 11;

    private EquipoDAO equipoDAO;
    private EquipoConsumibleDAO consumibleDAO;
    private ColorDAO colorDAO;
    private MarcaDAO marcaDAO;
    private ModeloDAO modeloDAO;
    private UbicacionDAO ubicacionDAO;
    private EstatusDAO estatusDAO;
    private AsignacionDAO asignacionDAO;
    private TipoEquipoDAO tipoEquipoDAO;
    private BitacoraMovimientoDAO bitacoraDAO;

    // cache de id_tipo CONSUMIBLE
    private volatile Integer idTipoConsumibleCache = null;

    @Override
    public void init() {
        this.equipoDAO      = new EquipoDAO();
        this.consumibleDAO  = new EquipoConsumibleDAO();
        this.colorDAO       = new ColorDAO();
        this.marcaDAO       = new MarcaDAO();
        this.modeloDAO      = new ModeloDAO();
        this.ubicacionDAO   = new UbicacionDAO();
        this.estatusDAO     = new EstatusDAO();
        this.asignacionDAO  = new AsignacionDAO();
        this.tipoEquipoDAO  = new TipoEquipoDAO();
        this.bitacoraDAO    = new BitacoraMovimientoDAO();
    }

    // ===== GET =====
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        // --- endpoint JSON para modal de edición/detalles ---
        if ("get".equals(action)) {
            resp.setContentType("application/json");
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
                    resp.getWriter().write("{\"error\":\"Not found\"}");
                    return;
                }
                Map<String,Object> out = new HashMap<>();
                out.put("idEquipo", e.getIdEquipo());
                out.put("idMarca", e.getIdMarca());
                out.put("idModelo", e.getIdModelo());
                out.put("idUbicacion", e.getIdUbicacion());
                out.put("idEstatus", e.getIdEstatus());
                out.put("numeroSerie", e.getNumeroSerie());
                out.put("notas", e.getNotas());
                try {
                    modelo.EquipoConsumible ec = consumibleDAO.obtenerPorIdEquipo(id);
                    if (ec != null) {
                        out.put("idColor", ec.getIdColor());
                        out.put("colorNombre", colorDAO.obtenerNombrePorId(ec.getIdColor()));
                    }
                } catch (Exception ignore) {}
                resp.getWriter().write(new Gson().toJson(out));
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid id format\"}");
            } catch (Exception ex) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Internal server error\"}");
            }
            return;
        }

        // ===== Listado con filtros =====
        Integer idMarca     = parseIntOrNull(req.getParameter("idMarca"));
        Integer idModelo    = parseIntOrNull(req.getParameter("idModelo"));
        Integer idEstatus   = parseIntOrNull(req.getParameter("idEstatus"));
        Integer idUbicacion = parseIntOrNull(req.getParameter("idUbicacion"));
        Integer idColor     = parseIntOrNull(req.getParameter("idColor"));
        String q            = trimToNull(req.getParameter("q"));

        int pageReq = Math.max(1, parseIntOrDefault(req.getParameter("page"), 1));
        int limit   = PAGE_SIZE;

        Integer idTipoCons = resolveIdTipoConsumible(); // puede ser null si no existe

        // Conteo total (excluyendo Eliminado cuando idEstatus es null)
        int total = 0;
        if (idTipoCons == null) {
            // Si no existe el tipo CONSUMIBLE, no listamos ningún equipo.
            total = 0;
        } else if (idEstatus != null && idEstatus == STATUS_ELIMINADO) {
            total = 0;
        } else {
            if (idEstatus == null) {
                int raw  = equipoDAO.contarConFiltrosIncluyendoConsumible(idTipoCons, idMarca, idModelo, null, idUbicacion, idColor, q);
                int elim = equipoDAO.contarConFiltrosIncluyendoConsumible(idTipoCons, idMarca, idModelo, STATUS_ELIMINADO, idUbicacion, idColor, q);
                total = Math.max(0, raw - elim);
            } else {
                total = equipoDAO.contarConFiltrosIncluyendoConsumible(idTipoCons, idMarca, idModelo, idEstatus, idUbicacion, idColor, q);
            }
        }

        int totalPages = Math.max(1, (int)Math.ceil(total / (double)limit));
        int page   = Math.min(pageReq, totalPages);
        int offset = (page - 1) * limit;

        // Traer página, saltando Eliminados cuando no se filtró por estatus
        List<EquipoDetalle> equipos = new ArrayList<>();
        if (!(idEstatus != null && idEstatus == STATUS_ELIMINADO) && total > 0) {
            int batchOffset = offset;
            int guard = 0;
            final int MAX_BATCHES = 50;
            while (equipos.size() < limit && guard++ < MAX_BATCHES) {
                List<EquipoDetalle> lote = equipoDAO.listarConDetalleIncluyendoConsumible(
                        idTipoCons, idMarca, idModelo, idEstatus, idUbicacion, idColor, q, limit, batchOffset);
                if (lote == null || lote.isEmpty()) break;
                for (EquipoDetalle d : lote) {
                    // Asegurar que solo se agreguen equipos del tipo CONSUMIBLE
                    if (d.getIdEstatus() != STATUS_ELIMINADO && idTipoCons != null && d.getIdTipo() == idTipoCons) {
                        equipos.add(d);
                        if (equipos.size() >= limit) break;
                    }
                }
                batchOffset += limit;
            }
        }

        // Map para la tabla
        List<Map<String,Object>> consumibles = new ArrayList<>();
        for (EquipoDetalle d : equipos) {
            Map<String,Object> row = new HashMap<>();
            row.put("idEquipo", d.getIdEquipo());
            row.put("numeroSerie", d.getNumeroSerie());
            row.put("idMarca", d.getIdMarca());
            row.put("marcaNombre", d.getMarcaNombre());
            row.put("idModelo", d.getIdModelo());
            row.put("modeloNombre", d.getModeloNombre());
            row.put("idUbicacion", d.getIdUbicacion());
            row.put("ubicacionNombre", d.getUbicacionNombre());
            row.put("idEstatus", d.getIdEstatus());
            row.put("estatusNombre", d.getEstatusNombre());
            row.put("notas", d.getNotas());
            try {
                modelo.EquipoConsumible ec = consumibleDAO.obtenerPorIdEquipo(d.getIdEquipo());
                if (ec != null) {
                    row.put("idColor", ec.getIdColor());
                    row.put("colorNombre", colorDAO.obtenerNombrePorId(ec.getIdColor()));
                }
            } catch (Exception ignore) {}
            consumibles.add(row);
        }

        // Catálogos
        req.setAttribute("marcas",      marcaDAO.listarActivas(UI_LIMIT, 0));
        req.setAttribute("modelos",     modeloDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("ubicaciones", ubicacionDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("estatuses",   estatusDAO.listarPorTipo("EQUIPO"));
        req.setAttribute("colores",     colorDAO.listarTodos(UI_LIMIT, 0));

        // Vista
        req.setAttribute("consumibles", consumibles);
        req.setAttribute("page", page);
        req.setAttribute("limit", limit);
        req.setAttribute("total", total);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("q", q);
        req.setAttribute("idMarca", idMarca);
        req.setAttribute("idModelo", idModelo);
        req.setAttribute("idEstatus", idEstatus);
        req.setAttribute("idUbicacion", idUbicacion);
        req.setAttribute("idColor", idColor);

        req.getRequestDispatcher("consumibles.jsp").forward(req, resp);
    }

    // ===== POST =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if ("delete".equals(action)) { handleDelete(req, resp); return; }
        if ("create".equals(action)) { handleCreate(req, resp); return; }
        if ("save".equals(action))   { handleSave(req, resp);   return; }

        resp.sendRedirect(req.getContextPath() + "/consumibles");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("idEquipo");
            if (idParam == null || idParam.trim().isEmpty()) {
                req.getSession().setAttribute("flashError", "Missing equipment ID.");
                resp.sendRedirect(req.getContextPath() + "/consumibles");
                return;
            }
            int id = Integer.parseInt(idParam.trim());
            // snapshot
            Equipo previo = null;
            try { previo = equipoDAO.obtenerPorId(id); } catch (Exception ignore) {}

            // cerrar asignaciones activas si sale de ASIGNADO
            int cerradas = 0;
            try {
                List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(id, false, 1000, 0);
                for (modelo.Asignacion a : activas) {
                    if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) cerradas++;
                }
            } catch (Exception exAsg) {
                req.getSession().setAttribute("flashError","Error al cerrar asignaciones activas antes de eliminar (lógico).");
            }

            boolean ok = equipoDAO.actualizarEstatus(id, STATUS_ELIMINADO);
            if (ok) {
                String msg = "Consumible marcado como Eliminado.";
                if (cerradas > 0) msg += " Asignaciones cerradas: " + cerradas + ".";
                req.getSession().setAttribute("flashOk", msg);
                // bitácora
                Integer userId = getCurrentUserId(req);
                Integer estOrigen = (previo != null) ? previo.getIdEstatus() : null;
                String notasDel = "Borrado lógico de Consumible." + (cerradas>0 ? (" Asignaciones cerradas: "+cerradas+".") : "");
                logMovimiento(id, null, "ELIMINAR", estOrigen, STATUS_ELIMINADO, userId, notasDel);
            } else {
                req.getSession().setAttribute("flashError", "No se pudo marcar el consumible como Eliminado.");
            }
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Invalid equipment ID format.");
        } catch (Exception ex) {
            req.getSession().setAttribute("flashError", "Error al eliminar (lógico) consumible.");
        }
        resp.sendRedirect(req.getContextPath() + "/consumibles");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer idTipo = resolveIdTipoConsumible();
            if (idTipo == null) throw new IllegalStateException("No existe un tipo 'CONSUMIBLE' en catálogos.");

            Equipo e = new Equipo();
            e.setIdTipo(idTipo);
            e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
            e.setNumeroSerie(emptyToNull(req.getParameter("numeroSerie")));
            e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
            e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
            e.setIdEstatus(Integer.parseInt(req.getParameter("idEstatus").trim()));
            e.setIpFija(null);
            e.setPuertoEthernet(null);
            e.setNotas(emptyToNull(req.getParameter("notas")));

            int newId = equipoDAO.crear(e);
            e.setIdEquipo(newId);

            // Consumible (color opcional, pero lo solemos capturar)
            Integer idColor = parseIntOrNull(req.getParameter("idColor"));
            if (idColor != null) {
                modelo.EquipoConsumible ec = new modelo.EquipoConsumible();
                ec.setIdEquipo(newId);
                ec.setIdColor(idColor);
                if (!consumibleDAO.crear(ec)) throw new RuntimeException("No se pudo crear registro de consumible.");
            }

            // Bitácora
            Integer userId = getCurrentUserId(req);
            String notasCrear = String.format(
                    "Alta Consumible: modelo=%s, marca=%s, serie=%s, ubicacion=%s, estatus=%s, color=%s",
                    s(modeloDAO.obtenerNombrePorId(e.getIdModelo())),
                    s(marcaDAO.obtenerNombrePorId(e.getIdMarca())),
                    s(e.getNumeroSerie()),
                    s(ubicacionDAO.obtenerNombrePorId(e.getIdUbicacion())),
                    s(estatusDAO.obtenerNombrePorId(e.getIdEstatus())),
                    (idColor==null ? "-" : s(colorDAO.obtenerNombrePorId(idColor)))
            );
            logMovimiento(newId, null, "CREAR", null, null, userId, notasCrear);

            req.getSession().setAttribute("flashOk", "Consumible creado correctamente (ID " + newId + ").");
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Formato numérico inválido en los datos del formulario.");
        } catch (IllegalArgumentException iae) {
            req.getSession().setAttribute("flashError", iae.getMessage());
        } catch (Exception ex) {
            req.getSession().setAttribute("flashError", "Error al crear consumible.");
        }
        String rq = req.getParameter("returnQuery");
        String to = req.getContextPath() + "/consumibles" + ((rq != null && !rq.trim().isEmpty()) ? ("?" + rq.trim()) : "");
        resp.sendRedirect(to);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idEquipo = Integer.parseInt(req.getParameter("idEquipo").trim());
            Equipo previo = equipoDAO.obtenerPorId(idEquipo);
            if (previo == null) throw new IllegalArgumentException("Consumible inexistente.");

            Equipo e = new Equipo();
            e.setIdEquipo(idEquipo);
            e.setIdTipo(previo.getIdTipo()); // preservar tipo
            e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
            e.setNumeroSerie(emptyToNull(req.getParameter("numeroSerie")));
            e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
            e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
            int nuevoEstatus = Integer.parseInt(req.getParameter("idEstatus").trim());
            e.setIdEstatus(nuevoEstatus);
            e.setIpFija(null);
            e.setPuertoEthernet(null);
            e.setNotas(emptyToNull(req.getParameter("notas")));

            // snapshot consumible previo
            modelo.EquipoConsumible consPrev = null;
            try { consPrev = consumibleDAO.obtenerPorIdEquipo(idEquipo); } catch (Exception ignore) {}

            boolean fromAsignadoToOtro = (previo.getIdEstatus() == STATUS_ASIGNADO) && (nuevoEstatus != STATUS_ASIGNADO);

            boolean ok = equipoDAO.actualizar(e);
            if (!ok) throw new RuntimeException("No se pudo actualizar consumible.");

            // Actualizar consumible (color)
            Integer nuevoColor = parseIntOrNull(req.getParameter("idColor"));
            boolean consExiste  = consumibleDAO.existeParaEquipo(idEquipo);
            if (nuevoColor != null) {
                if (consExiste) consumibleDAO.actualizarColor(idEquipo, nuevoColor);
                else {
                    modelo.EquipoConsumible ec = new modelo.EquipoConsumible();
                    ec.setIdEquipo(idEquipo);
                    ec.setIdColor(nuevoColor);
                    consumibleDAO.crear(ec);
                }
            } else {
                if (consExiste) consumibleDAO.eliminar(idEquipo);
            }

            // snapshot nuevo
            modelo.EquipoConsumible consNow = null;
            try { consNow = consumibleDAO.obtenerPorIdEquipo(idEquipo); } catch (Exception ignore) {}

            if (ok && fromAsignadoToOtro) {
                try {
                    List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(idEquipo, false, 1000, 0);
                    int cerradas = 0;
                    for (modelo.Asignacion a : activas) {
                        if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) cerradas++;
                    }
                    req.getSession().setAttribute("flashOk",
                            "Consumible actualizado." + (cerradas>0 ? (" Se marcaron " + cerradas + " asignaciones como devueltas.") : ""));
                } catch (Exception cleanEx) {
                    req.getSession().setAttribute("flashError",
                            "Consumible actualizado, pero ocurrió un error al cerrar asignaciones activas.");
                }
            } else {
                req.getSession().setAttribute(ok ? "flashOk" : "flashError",
                        ok ? "Consumible actualizado." : "No se actualizó el consumible.");
            }

            // Bitácora: MODIFICACION
            Integer userId = getCurrentUserId(req);
            String notas = buildNotasModificacionConsumible(previo, e, consPrev, consNow);
            int estOrigen = previo.getIdEstatus();
            Integer estDestino = (e.getIdEstatus() != estOrigen) ? e.getIdEstatus() : null;
            logMovimiento(idEquipo, null, "MODIFICACION",
                    (estDestino != null ? estOrigen : null),
                    estDestino, userId, notas);

        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Invalid number format in form data.");
        } catch (IllegalArgumentException iae) {
            req.getSession().setAttribute("flashError", iae.getMessage());
        } catch (Exception ex) {
            req.getSession().setAttribute("flashError", "Error al actualizar consumible.");
        }
        String rq = req.getParameter("returnQuery");
        String to = req.getContextPath() + "/consumibles" + ((rq != null && !rq.trim().isEmpty()) ? ("?" + rq.trim()) : "");
        resp.sendRedirect(to);
    }

    // ===== Helpers =====
    private Integer resolveIdTipoConsumible() {
        if (idTipoConsumibleCache != null) return idTipoConsumibleCache;
        try {
            List<modelo.TipoEquipo> tipos = tipoEquipoDAO.listarTodos(UI_LIMIT, 0);
            if (tipos != null) {
                for (modelo.TipoEquipo t : tipos) {
                    String n = (t.getNombre() == null) ? "" : t.getNombre().trim().toUpperCase();
                    if (n.contains("CONSUM")) { // "CONSUMIBLE", "CONSUMIBLES", etc.
                        idTipoConsumibleCache = t.getIdTipo();
                        break;
                    }
                }
            }
        } catch (Exception ignore) {}
        return idTipoConsumibleCache;
    }

    private Integer getCurrentUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object u = s.getAttribute("userId");
        if (u instanceof Number) return ((Number) u).intValue();
        if (u instanceof String) {
            try { return Integer.parseInt((String) u); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void logMovimiento(Integer idEquipo, Integer idUsuarioInvolucrado,
                               String accion, Integer estatusOrigen, Integer estatusDestino,
                               Integer realizadoPor, String notas) {
        try {
            BitacoraMovimiento bm = new BitacoraMovimiento();
            bm.setIdEquipo(idEquipo);
            if (idUsuarioInvolucrado != null) bm.setIdUsuario(idUsuarioInvolucrado);
            bm.setAccion(accion);
            if (estatusOrigen != null)  bm.setEstatusOrigen(estatusOrigen);
            if (estatusDestino != null) bm.setEstatusDestino(estatusDestino);
            bm.setRealizadoPor(realizadoPor != null ? realizadoPor : 0);
            bm.setNotas(notas);
            bitacoraDAO.registrar(bm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String buildNotasModificacionConsumible(Equipo oldE, Equipo newE,
                                                    modelo.EquipoConsumible consPrev, modelo.EquipoConsumible consNow) {
        StringBuilder sb = new StringBuilder("Cambios: ");
        diff(sb, "Modelo",   s(modeloDAO.obtenerNombrePorId(oldE.getIdModelo())), s(modeloDAO.obtenerNombrePorId(newE.getIdModelo())));
        diff(sb, "Marca",    s(marcaDAO.obtenerNombrePorId(oldE.getIdMarca())),   s(marcaDAO.obtenerNombrePorId(newE.getIdMarca())));
        diff(sb, "Ubicacion",s(ubicacionDAO.obtenerNombrePorId(oldE.getIdUbicacion())), s(ubicacionDAO.obtenerNombrePorId(newE.getIdUbicacion())));
        diff(sb, "Estatus",  s(estatusDAO.obtenerNombrePorId(oldE.getIdEstatus())), s(estatusDAO.obtenerNombrePorId(newE.getIdEstatus())));
        diff(sb, "NumeroSerie", s(oldE.getNumeroSerie()), s(newE.getNumeroSerie()));
        diff(sb, "Notas",    s(oldE.getNotas()), s(newE.getNotas()));
        String colorA = (consPrev==null) ? "-" : s(colorDAO.obtenerNombrePorId(consPrev.getIdColor()));
        String colorB = (consNow ==null) ? "-" : s(colorDAO.obtenerNombrePorId(consNow.getIdColor()));
        diff(sb, "Consumible.Color", colorA, colorB);
        String result = sb.toString().trim();
        return result.equals("Cambios:") ? "Sin diferencias detectadas" : result;
    }

    private static Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception ex) { return null; }
    }
    private static int parseIntOrDefault(String s, int d) {
        try { return (s == null || s.trim().isEmpty()) ? d : Integer.parseInt(s.trim()); }
        catch (Exception ex) { return d; }
    }
    private static String emptyToNull(String s) { return (s == null) ? null : (s.trim().isEmpty() ? null : s.trim()); }
    private static String trimToNull(String s) { return emptyToNull(s); }
    private static String s(Object o) { return (o == null) ? "-" : String.valueOf(o); }
    private static void diff(StringBuilder sb, String tag, String a, String b) {
        String A = s(a), B = s(b);
        if (!A.equals(B)) sb.append("[").append(tag).append(": '").append(A).append("' → '").append(B).append("'] ");
    }
}
