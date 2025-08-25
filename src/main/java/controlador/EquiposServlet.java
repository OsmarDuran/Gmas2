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
import datos.UsuarioDAO;
import datos.BitacoraMovimientoDAO;
import modelo.BitacoraMovimiento;



import modelo.Equipo;
import modelo.EquipoDetalle;
import modelo.Usuario;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/equipos")
public class EquiposServlet extends HttpServlet {

    private static final int UI_LIMIT = 1000; // para cargar catálogos en selects
    private static final int PAGE_SIZE = 12;  // para la tabla
    private static final int STATUS_ASIGNADO = 2; // ID para estatus "Asignado"
    private static final int STATUS_ELIMINADO = 11; // ID para estatus "Eliminado"


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
    private UsuarioDAO usuarioDAO;
    private BitacoraMovimientoDAO bitacoraDAO;



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
        this.usuarioDAO = new UsuarioDAO();
        this.bitacoraDAO = new BitacoraMovimientoDAO();
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
                // Construir respuesta con datos base + subtipos (si existen)
                Map<String, Object> out = new HashMap<>();
                out.put("idEquipo", e.getIdEquipo());
                out.put("idTipo", e.getIdTipo());
                out.put("idModelo", e.getIdModelo());
                out.put("numeroSerie", e.getNumeroSerie());
                out.put("idMarca", e.getIdMarca());
                out.put("idUbicacion", e.getIdUbicacion());
                out.put("idEstatus", e.getIdEstatus());
                out.put("ipFija", e.getIpFija());
                out.put("puertoEthernet", e.getPuertoEthernet());
                out.put("notas", e.getNotas());
                // Adjuntar datos SIM si existen
                try {
                    modelo.EquipoSim sim = equipoSimDAO.obtenerPorIdEquipo(id);
                    if (sim != null) {
                        out.put("simNumeroAsignado", sim.getNumeroAsignado());
                        out.put("simImei", sim.getImei());
                    }
                } catch (Exception ignore) {}
                // Adjuntar datos Consumible si existen
                try {
                    modelo.EquipoConsumible cons = equipoConsumibleDAO.obtenerPorIdEquipo(id);
                    if (cons != null) {
                        out.put("idColorConsumible", cons.getIdColor());
                        out.put("idColor", cons.getIdColor()); // compatibilidad con el JS del modal
                    }
                } catch (Exception ignore) {}
                resp.getWriter().write(new Gson().toJson(out));
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

        // Obtener datos de un usuario por ID (para ver detalles desde las asignaciones)
        if ("usuario".equals(action)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            String idUsuarioParam = req.getParameter("idUsuario");
            if (idUsuarioParam == null || idUsuarioParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing idUsuario parameter\"}");
                return;
            }
            try {
                int idUsuario = Integer.parseInt(idUsuarioParam.trim());
                Usuario u = usuarioDAO.obtenerPorId(idUsuario);
                if (u == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"User not found\"}");
                    return;
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class,
                                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                        .create();
                resp.getWriter().write(gson.toJson(u));
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid idUsuario format\"}");
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

        int pageReq = Math.max(1, parseIntOrDefault(req.getParameter("page"), 1));
        int limit   = PAGE_SIZE;
        // Total de registros con filtros
        int total = equipoDAO.contarConFiltros(idTipo, idMarca, idModelo, idEstatus, idUbicacion, q);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) limit));
        int page = Math.min(pageReq, totalPages);
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
        req.setAttribute("limit", limit);
        req.setAttribute("total", total);
        req.setAttribute("totalPages", totalPages);
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
                // Obtener estatus previo para bitácora
                Equipo previoDelete = null;
                try {
                    previoDelete = equipoDAO.obtenerPorId(id);
                } catch (Exception ignore) {}

                // 1) Cerrar asignaciones activas (si existen)
                int cerradas = 0;
                try {
                    List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(id, false, 1000, 0);
                    for (modelo.Asignacion a : activas) {
                        if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) {
                            cerradas++;
                        }
                    }
                } catch (Exception exAsg) {
                    // No abortar la operación; solo reportar
                    req.getSession().setAttribute("flashError",
                            "Error al cerrar asignaciones activas antes de eliminar (borrado lógico).");
                }

                // 2) Borrado lógico: cambiar a estatus 11 = Eliminado
                boolean ok = equipoDAO.actualizarEstatus(id, STATUS_ELIMINADO);
                if (ok) {
                    String msg = "Equipo marcado como Eliminado.";
                    if (cerradas > 0) msg += " Se marcaron " + cerradas + " asignaciones como devueltas.";
                    req.getSession().setAttribute("flashOk", msg);
                    // Bitácora: ELIMINAR
                    Integer userId = getCurrentUserId(req);
                    Integer estOrigen = (previoDelete != null) ? previoDelete.getIdEstatus() : null;
                    String notasDel = "Borrado lógico del equipo."
                            + (cerradas > 0 ? (" Asignaciones cerradas: " + cerradas + ".") : "");
                    logMovimiento(id, null, "ELIMINAR", estOrigen, STATUS_ELIMINADO, userId, notasDel);
                } else {
                    req.getSession().setAttribute("flashError", "No se pudo marcar el equipo como Eliminado.");
                }

            } catch (NumberFormatException ex) {
                req.getSession().setAttribute("flashError", "Invalid equipment ID format.");
            } catch (Exception ex) {
                req.getSession().setAttribute("flashError", "Error al eliminar (lógico) equipo.");
            }
            resp.sendRedirect(req.getContextPath() + "/equipos");
            return;
        }


        if ("create".equals(action)) {
            try {
                Equipo e = new Equipo();
                e.setIdTipo(Integer.parseInt(req.getParameter("idTipo").trim()));
                e.setIdModelo(parseIntOrNull(req.getParameter("idModelo")));
                String tipoNombre = req.getParameter("tipoNombre");
                String tn = tipoNombre != null ? tipoNombre.trim().toUpperCase() : "";
                String numSerie = emptyToNull(req.getParameter("numeroSerie"));
                boolean tipoPermiteSinSerie = tn.contains("SIM") || tn.contains("CONSUM");
                if (!tipoPermiteSinSerie && (numSerie == null || numSerie.isEmpty())) {
                    throw new IllegalArgumentException("El número de serie es obligatorio para este tipo de equipo.");
                }
                e.setNumeroSerie(tipoPermiteSinSerie ? numSerie /* puede ser null */ : numSerie);
                e.setIdMarca(parseIntOrNull(req.getParameter("idMarca")));
                e.setIdUbicacion(parseIntOrNull(req.getParameter("idUbicacion")));
                e.setIdEstatus(Integer.parseInt(req.getParameter("idEstatus").trim()));
                e.setIpFija(emptyToNull(req.getParameter("ipFija")));
                e.setPuertoEthernet(emptyToNull(req.getParameter("puertoEthernet")));
                e.setNotas(emptyToNull(req.getParameter("notas")));

                int newId = equipoDAO.crear(e);
                e.setIdEquipo(newId);

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
                // === Bitácora: CREAR ===
                Integer userId = getCurrentUserId(req);
                String notasCrear = String.format(
                        "Alta equipo: tipo=%s, modelo=%s, marca=%s, serie=%s, ubicacion=%s, estatus=%s, ip=%s, puerto=%s",
                        s(tipoEquipoDAO.obtenerNombrePorId(e.getIdTipo())),
                        s(modeloDAO.obtenerNombrePorId(e.getIdModelo())),
                        s(marcaDAO.obtenerNombrePorId(e.getIdMarca())),
                        s(e.getNumeroSerie()),
                        s(ubicacionDAO.obtenerNombrePorId(e.getIdUbicacion())),
                        s(estatusDAO.obtenerNombrePorId(e.getIdEstatus())),
                        s(e.getIpFija()),
                        s(e.getPuertoEthernet())
                );

                logMovimiento(newId, null, "CREAR", null, null, userId, notasCrear);

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
                String tipoNombre = req.getParameter("tipoNombre");
                String tn = tipoNombre != null ? tipoNombre.trim().toUpperCase() : "";
                boolean tipoPermiteSinSerie = tn.contains("SIM") || tn.contains("CONSUM");
                String numSerie = emptyToNull(req.getParameter("numeroSerie"));
                if (!tipoPermiteSinSerie && (numSerie == null || numSerie.isEmpty())) {
                    throw new IllegalArgumentException("El número de serie es obligatorio para este tipo de equipo.");
                }
                e.setNumeroSerie(tipoPermiteSinSerie ? numSerie /* puede ser null */ : numSerie);
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
                if (!ok) throw new RuntimeException("No se pudo actualizar equipo");

                if (ok && fromAsignadoToOtro) {
                    try {
                        List<modelo.Asignacion> activas = asignacionDAO.listarPorEquipo(e.getIdEquipo(), false, 1000, 0);
                        int cerradas = 0;
                        for (modelo.Asignacion a : activas) {
                            if (asignacionDAO.marcarDevuelto(a.getIdAsignacion(), LocalDateTime.now())) cerradas++;
                        }
                        req.getSession().setAttribute("flashOk",
                                "Equipo actualizado." + (cerradas > 0 ? (" Se marcaron " + cerradas + " asignaciones como devueltas.") : ""));
                    } catch (Exception cleanEx) {
                        req.getSession().setAttribute("flashError",
                                "Equipo actualizado, pero ocurrió un error al cerrar asignaciones activas.");
                    }
                } else {
                    req.getSession().setAttribute(ok ? "flashOk" : "flashError",
                            ok ? "Equipo actualizado." : "No se actualizó el equipo.");
                }
                // === Bitácora: MODIFICACION ===
                Integer userId = getCurrentUserId(req);
                String notas = buildNotasModificacion(previo, e);

                int estOrigen = previo.getIdEstatus();
                Integer estDestino = (e.getIdEstatus() != estOrigen) ? e.getIdEstatus() : null;

                logMovimiento(idEquipo, null, "MODIFICACION",
                        (estDestino != null ? estOrigen : null),
                        estDestino,
                        userId, notas);

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

    private Integer getCurrentUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false); // no crear sesión nueva
        if (s == null) return null;

        Object u = s.getAttribute("userId");
        if (u instanceof Number) {
            return ((Number) u).intValue(); // soporta Integer/Long
        }
        if (u instanceof String) {
            try { return Integer.parseInt((String) u); } catch (NumberFormatException ignored) {}
        }
        return null;
    }


    private static String s(Object o) { return (o == null) ? "-" : String.valueOf(o); }

    private void logMovimiento(Integer idEquipo, Integer idUsuarioInvolucrado,
                               String accion, Integer estatusOrigen, Integer estatusDestino,
                               Integer realizadoPor, String notas) {
        try {
            BitacoraMovimiento bm = new BitacoraMovimiento();
            bm.setIdEquipo(idEquipo);
            // Evitar NPE por auto-unboxing cuando hay valores opcionales
            if (idUsuarioInvolucrado != null) {
                bm.setIdUsuario(idUsuarioInvolucrado);
            }
            bm.setAccion(accion);
            if (estatusOrigen != null) {
                bm.setEstatusOrigen(estatusOrigen);
            }
            if (estatusDestino != null) {
                bm.setEstatusDestino(estatusDestino);
            }
            bm.setRealizadoPor(realizadoPor);
            bm.setNotas(notas);
            bitacoraDAO.registrar(bm);
        } catch (Exception ex) {
            // No interrumpir el flujo por fallos de bitácora
            ex.printStackTrace();
        }
    }

    private String buildNotasModificacion(Equipo oldE, Equipo newE) {
        StringBuilder sb = new StringBuilder("Cambios: ");

        // Claves foráneas -> resolver nombres legibles
        String oldTipo = s(tipoEquipoDAO.obtenerNombrePorId(oldE.getIdTipo()));
        String newTipo = s(tipoEquipoDAO.obtenerNombrePorId(newE.getIdTipo()));
        diff(sb, "Tipo", oldTipo, newTipo);

        String oldModelo = s(modeloDAO.obtenerNombrePorId(oldE.getIdModelo()));
        String newModelo = s(modeloDAO.obtenerNombrePorId(newE.getIdModelo()));
        diff(sb, "Modelo", oldModelo, newModelo);

        String oldMarca = s(marcaDAO.obtenerNombrePorId(oldE.getIdMarca()));
        String newMarca = s(marcaDAO.obtenerNombrePorId(newE.getIdMarca()));
        diff(sb, "Marca", oldMarca, newMarca);

        String oldUbic = s(ubicacionDAO.obtenerNombrePorId(oldE.getIdUbicacion()));
        String newUbic = s(ubicacionDAO.obtenerNombrePorId(newE.getIdUbicacion()));
        diff(sb, "Ubicacion", oldUbic, newUbic);

        String oldEst = s(estatusDAO.obtenerNombrePorId(oldE.getIdEstatus()));
        String newEst = s(estatusDAO.obtenerNombrePorId(newE.getIdEstatus()));
        diff(sb, "Estatus", oldEst, newEst);

        // Campos directos
        diff(sb, "NumeroSerie", s(oldE.getNumeroSerie()), s(newE.getNumeroSerie()));
        diff(sb, "IPFija", s(oldE.getIpFija()), s(newE.getIpFija()));
        diff(sb, "PuertoEth", s(oldE.getPuertoEthernet()), s(newE.getPuertoEthernet()));
        diff(sb, "Notas", s(oldE.getNotas()), s(newE.getNotas()));

        // === Subentidades ===
        var simA = equipoSimDAO.obtenerPorIdEquipo(oldE.getIdEquipo());
        var simB = equipoSimDAO.obtenerPorIdEquipo(newE.getIdEquipo());
        diff(sb, "SIM.NumeroAsignado", s(simA==null?null:simA.getNumeroAsignado()), s(simB==null?null:simB.getNumeroAsignado()));
        diff(sb, "SIM.IMEI", s(simA==null?null:simA.getImei()), s(simB==null?null:simB.getImei()));

        var consA = equipoConsumibleDAO.obtenerPorIdEquipo(oldE.getIdEquipo());
        var consB = equipoConsumibleDAO.obtenerPorIdEquipo(newE.getIdEquipo());
        String oldColor = (consA==null) ? "-" : s(colorDAO.obtenerNombrePorId(consA.getIdColor()));
        String newColor = (consB==null) ? "-" : s(colorDAO.obtenerNombrePorId(consB.getIdColor()));
        diff(sb, "Consumible.Color", oldColor, newColor);

        String out = sb.toString().trim();
        return out.equals("Cambios:") ? "Sin diferencias detectadas" : out;
    }

    private void diff(StringBuilder sb, String tag, String a, String b) {
        String A = s(a), B = s(b);
        if (!A.equals(B)) sb.append("[").append(tag).append(": '").append(A).append("' → '").append(B).append("'] ");
    }
}


