package controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
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

/* Servlet para administrar equipos de tipo SIM (+ tabla equipo_sim). */
@WebServlet("/sims")
public class SimsServlet extends HttpServlet {
    private static final int UI_LIMIT = 1000;
    private static final int PAGE_SIZE = 12;
    private static final int STATUS_ASIGNADO = 2;
    private static final int STATUS_ELIMINADO = 11;

    private EquipoDAO equipoDAO;
    private EquipoSimDAO equipoSimDAO;
    private MarcaDAO marcaDAO;
    private ModeloDAO modeloDAO;
    private UbicacionDAO ubicacionDAO;
    private EstatusDAO estatusDAO;
    private AsignacionDAO asignacionDAO;
    private TipoEquipoDAO tipoEquipoDAO;
    private BitacoraMovimientoDAO bitacoraDAO;

    // cache de id_tipo SIM
    private volatile Integer idTipoSimCache = null;

    @Override
    public void init() {
        this.equipoDAO     = new EquipoDAO();
        this.equipoSimDAO  = new EquipoSimDAO();
        this.marcaDAO      = new MarcaDAO();
        this.modeloDAO     = new ModeloDAO();
        this.ubicacionDAO  = new UbicacionDAO();
        this.estatusDAO    = new EstatusDAO();
        this.asignacionDAO = new AsignacionDAO();
        this.tipoEquipoDAO = new TipoEquipoDAO();
        this.bitacoraDAO   = new BitacoraMovimientoDAO();
    }

    // ===== GET =====
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

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
                // Adjuntar SIM
                try {
                    modelo.EquipoSim sim = equipoSimDAO.obtenerPorIdEquipo(id);
                    if (sim != null) {
                        out.put("simNumeroAsignado", sim.getNumeroAsignado());
                        out.put("simImei", sim.getImei());
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

        // ===== Listado con filtros, paginado (solo SIM) =====
        Integer idMarca     = parseIntOrNull(req.getParameter("idMarca"));
        Integer idModelo    = parseIntOrNull(req.getParameter("idModelo"));
        Integer idEstatus   = parseIntOrNull(req.getParameter("idEstatus"));
        Integer idUbicacion = parseIntOrNull(req.getParameter("idUbicacion"));
        String q            = trimToNull(req.getParameter("q"));

        int pageReq = Math.max(1, parseIntOrDefault(req.getParameter("page"), 1));
        int limit   = PAGE_SIZE;

        Integer idTipoSIM = resolveIdTipoSim(); // puede ser null si no existe

        // Conteo total (excluyendo Eliminado cuando idEstatus es null)
        int total = 0;
        if (idEstatus != null && idEstatus == STATUS_ELIMINADO) {
            total = 0;
        } else {
            if (idEstatus == null) {
                int raw  = equipoDAO.contarConFiltrosIncluyendoSim(idTipoSIM, idMarca, idModelo, null, idUbicacion, q);
                int elim = equipoDAO.contarConFiltrosIncluyendoSim(idTipoSIM, idMarca, idModelo, STATUS_ELIMINADO, idUbicacion, q);
                total = Math.max(0, raw - elim);
            } else {
                total = equipoDAO.contarConFiltrosIncluyendoSim(idTipoSIM, idMarca, idModelo, idEstatus, idUbicacion, q);
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
                List<EquipoDetalle> lote = equipoDAO.listarConDetalleIncluyendoSim(
                        idTipoSIM, idMarca, idModelo, idEstatus, idUbicacion, q, limit, batchOffset);
                if (lote == null || lote.isEmpty()) break;
                for (EquipoDetalle d : lote) {
                    if (d.getIdEstatus() != STATUS_ELIMINADO) {
                        equipos.add(d);
                        if (equipos.size() >= limit) break;
                    }
                }
                batchOffset += limit;
            }
        }

        // Enriquecer con datos SIM para la tabla (Map<String,Object> funciona con EL)
        List<Map<String,Object>> sims = new ArrayList<>();
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
                modelo.EquipoSim sim = equipoSimDAO.obtenerPorIdEquipo(d.getIdEquipo());
                if (sim != null) {
                    row.put("simNumeroAsignado", sim.getNumeroAsignado());
                    row.put("simImei", sim.getImei());
                }
            } catch (Exception ignore) {}
            sims.add(row);
        }

        // Catálogos
        req.setAttribute("marcas",      marcaDAO.listarActivas(UI_LIMIT, 0));
        req.setAttribute("modelos",     modeloDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("ubicaciones", ubicacionDAO.listarTodos(UI_LIMIT, 0));
        req.setAttribute("estatuses",   estatusDAO.listarPorTipo("EQUIPO"));

        // Vista
        req.setAttribute("sims", sims);
        req.setAttribute("page", page);
        req.setAttribute("limit", limit);
        req.setAttribute("total", total);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("q", q);
        req.setAttribute("idMarca", idMarca);
        req.setAttribute("idModelo", idModelo);
        req.setAttribute("idEstatus", idEstatus);
        req.setAttribute("idUbicacion", idUbicacion);

        req.getRequestDispatcher("sims.jsp").forward(req, resp);
    }

    // ===== POST =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            handleDelete(req, resp);
            return;
        }
        if ("create".equals(action)) {
            handleCreate(req, resp);
            return;
        }
        if ("save".equals(action)) {
            handleSave(req, resp);
            return;
        }
        // default
        resp.sendRedirect(req.getContextPath() + "/sims");
    }


    // ===== Handlers =====
    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("idEquipo");
            if (idParam == null || idParam.trim().isEmpty()) {
                req.getSession().setAttribute("flashError", "Missing equipment ID.");
                resp.sendRedirect(req.getContextPath() + "/sims");
                return;
            }
            int id = Integer.parseInt(idParam.trim());
            // Snapshot previo
            Equipo previo = null;
            try { previo = equipoDAO.obtenerPorId(id); } catch (Exception ignore) {}

            // Cerrar asignaciones activas
            int cerradas = 0;
            try {
                List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(id, false, 1000, 0);
                for (modelo.Asignacion a : activas) {
                    if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) cerradas++;
                }
            } catch (Exception exAsg) {
                req.getSession().setAttribute("flashError","Error al cerrar asignaciones activas antes de eliminar (lógico).");
            }

            // Borrado lógico
            boolean ok = equipoDAO.actualizarEstatus(id, STATUS_ELIMINADO);
            if (ok) {
                String msg = "SIM marcada como Eliminada.";
                if (cerradas > 0) msg += " Asignaciones cerradas: " + cerradas + ".";
                req.getSession().setAttribute("flashOk", msg);
                // Bitácora
                Integer userId = getCurrentUserId(req);
                Integer estOrigen = (previo != null) ? previo.getIdEstatus() : null;
                String notasDel = "Borrado lógico de SIM." + (cerradas>0 ? (" Asignaciones cerradas: "+cerradas+".") : "");
                logMovimiento(id, null, "ELIMINAR", estOrigen, STATUS_ELIMINADO, userId, notasDel);
            } else {
                req.getSession().setAttribute("flashError", "No se pudo marcar la SIM como Eliminada.");
            }
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Invalid equipment ID format.");
        } catch (Exception ex) {
            req.getSession().setAttribute("flashError", "Error al eliminar (lógico) SIM.");
        }
        resp.sendRedirect(req.getContextPath() + "/sims");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer idTipoSim = resolveIdTipoSim();
            if (idTipoSim == null) throw new IllegalStateException("No existe un tipo 'SIM' en catálogos.");

            Equipo e = new Equipo();
            e.setIdTipo(idTipoSim);
            e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
            e.setNumeroSerie(emptyToNull(req.getParameter("numeroSerie"))); // opcional en SIM
            e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
            e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
            e.setIdEstatus(Integer.parseInt(req.getParameter("idEstatus").trim()));
            e.setIpFija(null);
            e.setPuertoEthernet(null);
            e.setNotas(emptyToNull(req.getParameter("notas")));

            int newId = equipoDAO.crear(e);
            e.setIdEquipo(newId);


            // SIM
            String num  = emptyToNull(req.getParameter("simNumeroAsignado"));
            String imei = emptyToNull(req.getParameter("simImei"));
            if (num != null || imei != null) {
                modelo.EquipoSim sim = new modelo.EquipoSim();
                sim.setIdEquipo(newId);
                sim.setNumeroAsignado(num);
                sim.setImei(imei);
                if (!equipoSimDAO.crear(sim)) throw new RuntimeException("No se pudo crear registro SIM.");
            }

            // Bitácora
            Integer userId = getCurrentUserId(req);
            String notasCrear = String.format(
                    "Alta SIM: modelo=%s, marca=%s, serie=%s, ubicacion=%s, estatus=%s, numero=%s, imei=%s",
                    s(modeloDAO.obtenerNombrePorId(e.getIdModelo())),
                    s(marcaDAO.obtenerNombrePorId(e.getIdMarca())),
                    s(e.getNumeroSerie()),
                    s(ubicacionDAO.obtenerNombrePorId(e.getIdUbicacion())),
                    s(estatusDAO.obtenerNombrePorId(e.getIdEstatus())),
                    s(num), s(imei)
            );
            logMovimiento(newId, null, "CREAR", null, null, userId, notasCrear);

            req.getSession().setAttribute("flashOk", "SIM creada correctamente (ID " + newId + ").");
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Formato numérico inválido en los datos del formulario.");
        } catch (IllegalArgumentException iae) {
            req.getSession().setAttribute("flashError", iae.getMessage());
        } catch (Exception ex) {
            req.getSession().setAttribute("flashError", "Error al crear SIM.");
        }
        String rq = req.getParameter("returnQuery");
        String to = req.getContextPath() + "/sims" + ((rq != null && !rq.trim().isEmpty()) ? ("?" + rq.trim()) : "");
        resp.sendRedirect(to);
    }


    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idEquipo = Integer.parseInt(req.getParameter("idEquipo").trim());
            Equipo previo = equipoDAO.obtenerPorId(idEquipo);
            if (previo == null) throw new IllegalArgumentException("SIM inexistente.");

            Equipo e = new Equipo();
            e.setIdEquipo(idEquipo);
            e.setIdTipo(previo.getIdTipo()); // preservar tipo
            e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
            e.setNumeroSerie(emptyToNull(req.getParameter("numeroSerie"))); // opcional en SIM
            e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
            e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
            int nuevoEstatus = Integer.parseInt(req.getParameter("idEstatus").trim());
            e.setIdEstatus(nuevoEstatus);
            e.setIpFija(null);
            e.setPuertoEthernet(null);
            e.setNotas(emptyToNull(req.getParameter("notas")));

            // Snapshots SIM previos
            modelo.EquipoSim simPrev = null;
            try { simPrev = equipoSimDAO.obtenerPorIdEquipo(idEquipo); } catch (Exception ignore) {}

            boolean fromAsignadoToOtro = (previo.getIdEstatus() == STATUS_ASIGNADO) && (nuevoEstatus != STATUS_ASIGNADO);
            System.out.println("[SimsServlet] handleCreate OK");
            System.out.println("[SimsServlet] handleSave OK id=" + idEquipo);
            boolean ok = equipoDAO.actualizar(e);
            if (!ok) throw new RuntimeException("No se pudo actualizar SIM.");

            // Actualizar SIM
            String nuevoNumero = emptyToNull(req.getParameter("simNumeroAsignado"));
            String nuevoImei   = emptyToNull(req.getParameter("simImei"));
            boolean simExiste  = equipoSimDAO.existeParaEquipo(idEquipo);
            if (nuevoNumero != null || nuevoImei != null) {
                modelo.EquipoSim sim = new modelo.EquipoSim();
                sim.setIdEquipo(idEquipo);
                sim.setNumeroAsignado(nuevoNumero);
                sim.setImei(nuevoImei);
                if (simExiste) equipoSimDAO.actualizar(sim); else equipoSimDAO.crear(sim);
            } else {
                if (simExiste) equipoSimDAO.eliminar(idEquipo);
            }

            // Snapshots SIM nuevos
            modelo.EquipoSim simNow = null;
            try { simNow = equipoSimDAO.obtenerPorIdEquipo(idEquipo); } catch (Exception ignore) {}

            // Cerrar asignaciones si salió de Asignado
            if (ok && fromAsignadoToOtro) {
                try {
                    List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(idEquipo, false, 1000, 0);
                    int cerradas = 0;
                    for (modelo.Asignacion a : activas) {
                        if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) cerradas++;
                    }
                    req.getSession().setAttribute("flashOk",
                            "SIM actualizada." + (cerradas>0 ? (" Se marcaron " + cerradas + " asignaciones como devueltas.") : ""));
                } catch (Exception cleanEx) {
                    req.getSession().setAttribute("flashError",
                            "SIM actualizada, pero ocurrió un error al cerrar asignaciones activas.");
                }
            } else {
                req.getSession().setAttribute(ok ? "flashOk" : "flashError",
                        ok ? "SIM actualizada." : "No se actualizó la SIM.");
            }

            // Bitácora: MODIFICACION
            Integer userId = getCurrentUserId(req);
            String notas = buildNotasModificacionSIM(previo, e, simPrev, simNow);
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
            req.getSession().setAttribute("flashError", "Error al actualizar SIM.");
        }
        String rq = req.getParameter("returnQuery");
        String to = req.getContextPath() + "/sims" + ((rq != null && !rq.trim().isEmpty()) ? ("?" + rq.trim()) : "");
        resp.sendRedirect(to);
    }


    // ===== Helpers =====
    private Integer resolveIdTipoSim() {
        if (idTipoSimCache != null) return idTipoSimCache;
        try {
            List<modelo.TipoEquipo> tipos = tipoEquipoDAO.listarTodos(UI_LIMIT, 0);
            if (tipos != null) {
                for (modelo.TipoEquipo t : tipos) {
                    String n = (t.getNombre() == null) ? "" : t.getNombre().trim().toUpperCase();
                    if (n.contains("SIM")) {
                        idTipoSimCache = t.getIdTipo();
                        break;
                    }
                }
            }
        } catch (Exception ignore) {}
        return idTipoSimCache;
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
            bm.setRealizadoPor(realizadoPor);
            bm.setNotas(notas);
            bitacoraDAO.registrar(bm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String buildNotasModificacionSIM(Equipo oldE, Equipo newE,
                                             modelo.EquipoSim simPrev, modelo.EquipoSim simNow) {
        StringBuilder sb = new StringBuilder("Cambios: ");
        diff(sb, "Modelo",   s(modeloDAO.obtenerNombrePorId(oldE.getIdModelo())), s(modeloDAO.obtenerNombrePorId(newE.getIdModelo())));
        diff(sb, "Marca",    s(marcaDAO.obtenerNombrePorId(oldE.getIdMarca())),   s(marcaDAO.obtenerNombrePorId(newE.getIdMarca())));
        diff(sb, "Ubicacion",s(ubicacionDAO.obtenerNombrePorId(oldE.getIdUbicacion())), s(ubicacionDAO.obtenerNombrePorId(newE.getIdUbicacion())));
        diff(sb, "Estatus",  s(estatusDAO.obtenerNombrePorId(oldE.getIdEstatus())), s(estatusDAO.obtenerNombrePorId(newE.getIdEstatus())));
        diff(sb, "NumeroSerie", s(oldE.getNumeroSerie()), s(newE.getNumeroSerie()));
        diff(sb, "Notas",    s(oldE.getNotas()), s(newE.getNotas()));
        String simNumA = s(simPrev==null ? null : simPrev.getNumeroAsignado());
        String simNumB = s(simNow ==null ? null : simNow.getNumeroAsignado());
        String simImeiA= s(simPrev==null ? null : simPrev.getImei());
        String simImeiB= s(simNow ==null ? null : simNow.getImei());
        diff(sb, "SIM.Numero", simNumA, simNumB);
        diff(sb, "SIM.IMEI",   simImeiA, simImeiB);
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
    private static String emptyToNull(String s) {
        return (s == null) ? null : (s.trim().isEmpty() ? null : s.trim());
    }
    private static String trimToNull(String s) { return emptyToNull(s); }
    private static String s(Object o) { return (o == null) ? "-" : String.valueOf(o); }
    private static void diff(StringBuilder sb, String tag, String a, String b) {
        String A = s(a), B = s(b);
        if (!A.equals(B)) sb.append("[").append(tag).append(": '").append(A).append("' → '").append(B).append("'] ");
    }


}
