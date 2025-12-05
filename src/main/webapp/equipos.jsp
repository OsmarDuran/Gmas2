<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Equipos</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css"
          integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div id="loadingOverlay"><div class="loader" aria-label="Cargando"></div></div>

<!-- MENU FLOTANTE -->
<div class="box-menu">
    <div class="wrapper">
        <div class="hamburguer">
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            <span></span>
        </div>
    </div>
    <div class="menu">
        <a href="equipos.jsp" class="active"><span class="icon fa-solid fa-desktop"></span><span class="text">Equipos</span></a>
        <a href="sims"><span class="icon fa-solid fa-sim-card"></span><span class="text">Sims</span></a>
        <a href="consumibles"><span class="icon fa-solid fa-boxes-stacked"></span><span class="text">Consumibles</span></a>
        <a href="asignaciones.jsp"><span class="icon fa-solid fa-arrow-right-arrow-left"></span><span class="text">Asignaciones</span></a>
        <a href="catalogos.jsp"><span class="icon fa-solid fa-folder-open"></span><span class="text">Catálogos</span></a>
        <a href="usuarios.jsp"><span class="icon fa-solid fa-users"></span><span class="text">Usuarios</span></a>
        <a href="dashboard.jsp"><span class="icon fa-solid fa-chart-line"></span><span class="text">Dashboard</span></a>
    </div>
</div>

<main class="container">
    <div class="card card-compact">
        <h1>Equipos</h1>

        <!-- MENSAJES FLASH -->
        <c:if test="${not empty sessionScope.flashOk}">
            <div class="alert alert-ok">
                    ${sessionScope.flashOk}
            </div>
            <c:remove var="flashOk" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-err">
                    ${sessionScope.flashError}
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <!-- FILTROS -->
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/equipos">
            <div class="filters-row">
                <input type="search" name="q" value="${q}" placeholder="Buscar..." />
                <select name="idTipo">
                    <option value="">Tipo (todos)</option>
                    <c:forEach var="t" items="${tipos}">
                        <option value="${t.idTipo}" <c:if test="${idTipo==t.idTipo}">selected</c:if>>${t.nombre}</option>
                    </c:forEach>
                </select>
                <select name="idMarca">
                    <option value="">Marca</option>
                    <c:forEach var="m" items="${marcas}">
                        <option value="${m.idMarca}" <c:if test="${idMarca==m.idMarca}">selected</c:if>>${m.nombre}</option>
                    </c:forEach>
                </select>
                <select name="idModelo">
                    <option value="">Modelo</option>
                    <c:forEach var="m" items="${modelos}">
                        <option value="${m.idModelo}" <c:if test="${idModelo==m.idModelo}">selected</c:if>>${m.nombre}</option>
                    </c:forEach>
                </select>
                <select name="idUbicacion">
                    <option value="">Ubicación</option>
                    <c:forEach var="u" items="${ubicaciones}">
                        <option value="${u.idUbicacion}" <c:if test="${idUbicacion==u.idUbicacion}">selected</c:if>>${u.nombre}</option>
                    </c:forEach>
                </select>
                <select name="idEstatus">
                    <option value="">Estatus</option>
                    <c:forEach var="e" items="${estatuses}">
                        <option value="${e.idEstatus}" <c:if test="${idEstatus==e.idEstatus}">selected</c:if>>${e.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <input type="hidden" name="page" id="pageInput" value="1"/>
            <a class="btn btn-primary add-btn" href="${pageContext.request.contextPath}/equipos-nuevo">Agregar Equipo</a>
        </form>

        <!-- TABLA -->
        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Tipo</th>
                    <th>Núm. Serie</th>
                    <th>Marca</th>
                    <th>Modelo</th>
                    <th>Ubicación</th>
                    <th>Estatus</th>
                    <th style="width:170px">Acciones</th>
                </tr>
                </thead>
                <tbody id="equiposBody">
                <!-- Se llenará dinámicamente con fetch -->
                </tbody>
            </table>
        </div>
    </div>

    <!-- PAGINACIÓN -->
    <div class="pagination" id="pagination"
         style="display:flex;gap:.4rem;flex-wrap:wrap;align-items:center;margin:.75rem 0;"></div>
</main>

<!-- MODAL EDICIÓN / CREACIÓN -->
<dialog id="editModal">
    <form id="editForm" method="post" action="${pageContext.request.contextPath}/equipos">
        <input type="hidden" name="action" id="formAction" value="save"/>
        <input type="hidden" name="idEquipo" id="idEquipo"/>
        <input type="hidden" name="tipoNombre" id="tipoNombre"/>
        <input type="hidden" name="returnQuery" id="returnQuery"/>

        <div class="modal-h">
            <h3 id="modalTitle">Editar equipo</h3>
            <button type="button" class="btn" id="btnClose">✕</button>
        </div>

        <div class="modal-b">
            <div class="field">
                <label>Tipo</label>
                <select name="idTipo" id="idTipo" required>
                    <c:forEach var="t" items="${tipos}">
                        <option value="${t.idTipo}">${t.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Número de serie</label>
                <input type="text" name="numeroSerie" id="numeroSerie"/>
            </div>
            <div class="field">
                <label>Marca</label>
                <select name="idMarca" id="idMarca">
                    <option value="">—</option>
                    <c:forEach var="m" items="${marcas}">
                        <option value="${m.idMarca}">${m.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Modelo</label>
                <select name="idModelo" id="idModelo">
                    <option value="">—</option>
                    <c:forEach var="m" items="${modelos}">
                        <option value="${m.idModelo}">${m.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Ubicación</label>
                <select name="idUbicacion" id="idUbicacion">
                    <option value="">—</option>
                    <c:forEach var="u" items="${ubicaciones}">
                        <option value="${u.idUbicacion}">${u.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Estatus</label>
                <select name="idEstatus" id="idEstatus" required>
                    <option value="">— Selecciona estatus —</option>
                    <c:forEach var="s" items="${estatuses}">
                        <c:if test="${s.idEstatus != 2}">
                            <option value="${s.idEstatus}">${s.nombre}</option>
                        </c:if>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>IP fija</label>
                <input type="text" name="ipFija" id="ipFija" placeholder="192.168.1.x"/>
            </div>
            <div class="field">
                <label>Puerto Ethernet</label>
                <input type="text" name="puertoEthernet" id="puertoEthernet" placeholder="Ej. Eth1/0/24"/>
            </div>
            <div class="field" style="grid-column:1/-1">
                <label>Notas</label>
                <textarea name="notas" id="notas" rows="2"></textarea>
            </div>

            <!-- Datos SIM -->
            <div id="simFields" class="field" style="display:none;grid-column:1/-1">
                <fieldset style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.6rem">
                    <legend style="padding:0 .35rem;color:#334155">Datos SIM</legend>
                    <div style="display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:.8rem">
                        <div class="field">
                            <label>Número asignado</label>
                            <input type="text" name="simNumeroAsignado" id="simNumeroAsignado" placeholder="Ej. 555-123-4567"/>
                        </div>
                        <div class="field">
                            <label>IMEI</label>
                            <input type="text" name="simImei" id="simImei" placeholder="Ej. 356938035643809"/>
                        </div>
                    </div>
                </fieldset>
            </div>

            <!-- Datos Consumible -->
            <div id="consumibleFields" class="field" style="display:none;grid-column:1/-1">
                <fieldset style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.6rem">
                    <legend style="padding:0 .35rem;color:#334155">Datos Consumible</legend>
                    <div class="field">
                        <label>Color</label>
                        <select name="idColorConsumible" id="idColorConsumible">
                            <option value="">— Selecciona color —</option>
                            <c:forEach var="c" items="${colores}">
                                <option value="${c.idColor}">${c.nombre}</option>
                            </c:forEach>
                        </select>
                    </div>
                </fieldset>
            </div>

            <!-- Asignaciones del equipo -->
            <div class="field" style="grid-column:1/-1">
                <label>Asignaciones</label>
                <div id="asignacionesPanel"
                     style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.5rem;max-height:240px;overflow:auto">
                    <div id="asignacionesEmpty" style="color:#6b7280">No hay asignaciones.</div>
                    <table id="asignacionesTable"
                           style="width:100%;border-collapse:collapse;display:none">
                        <thead>
                        <tr>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Usuario</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado por</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado en</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Devuelto en</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Estado</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Acciones</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal-f">
            <button type="button" class="btn" id="btnCancel">Cancelar</button>
            <button type="submit" class="btn btn-primary">Guardar</button>
        </div>
    </form>
</dialog>

<!-- MODAL DETALLES -->
<dialog id="detailsModal">
    <div class="modal-h">
        <h3 id="detailsTitle">Detalles del equipo</h3>
        <button type="button" class="btn" id="btnCloseDetails">✕</button>
    </div>
    <div class="modal-b" id="detailsBody" style="grid-template-columns:repeat(2,minmax(0,1fr))">
        <div class="field"><label>Tipo</label><div id="d_tipo">—</div></div>
        <div class="field"><label>Número de serie</label><div id="d_numeroSerie">—</div></div>
        <div class="field"><label>Marca</label><div id="d_marca">—</div></div>
        <div class="field"><label>Modelo</label><div id="d_modelo">—</div></div>
        <div class="field"><label>Ubicación</label><div id="d_ubicacion">—</div></div>
        <div class="field"><label>Estatus</label><div id="d_estatus">—</div></div>
        <div class="field"><label>IP fija</label><div id="d_ip">—</div></div>
        <div class="field"><label>Puerto Ethernet</label><div id="d_puerto">—</div></div>
        <div class="field" style="grid-column:1/-1"><label>Notas</label><div id="d_notas">—</div></div>

        <!-- Sección SIM -->
        <div id="detailsSim" style="display:none;grid-column:1/-1">
            <fieldset style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.6rem">
                <legend style="padding:0 .35rem;color:#334155">Datos SIM</legend>
                <div style="display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:.8rem">
                    <div class="field"><label>Número asignado</label><div id="d_simNumero">—</div></div>
                    <div class="field"><label>IMEI</label><div id="d_simImei">—</div></div>
                </div>
            </fieldset>
        </div>

        <!-- Sección Consumible -->
        <div id="detailsConsumible" style="display:none;grid-column:1/-1">
            <fieldset style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.6rem">
                <legend style="padding:0 .35rem;color:#334155">Datos Consumible</legend>
                <div class="field"><label>Color</label><div id="d_colorConsumible">—</div></div>
            </fieldset>
        </div>

        <!-- Asignaciones -->
        <div class="field" style="grid-column:1/-1">
            <label>Asignaciones</label>
            <div id="detailsAsignacionesPanel"
                 style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.5rem;max-height:240px;overflow:auto">
                <div id="detailsAsignacionesEmpty" style="color:#6b7280">No hay asignaciones.</div>
                <table id="detailsAsignacionesTable"
                       style="width:100%;border-collapse:collapse;display:none">
                    <thead>
                    <tr>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Usuario</th>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado por</th>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado en</th>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Devuelto en</th>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Estado</th>
                        <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Acciones</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="modal-f">
        <button type="button" class="btn" id="btnCloseDetails2">Cerrar</button>
    </div>
</dialog>

<!-- MODAL USUARIO -->
<dialog id="userModal">
    <div class="modal-h">
        <h3>Detalles del usuario</h3>
        <button type="button" class="btn" id="btnCloseUser">✕</button>
    </div>
    <div class="modal-b" style="grid-template-columns:repeat(2,minmax(0,1fr))">
        <div class="field"><label>Nombre</label><div id="u_nombre">—</div></div>
        <div class="field"><label>Email</label><div id="u_email">—</div></div>
        <div class="field"><label>Teléfono</label><div id="u_tel">—</div></div>
        <div class="field"><label>Rol</label><div id="u_rol">—</div></div>
        <div class="field"><label>Puesto</label><div id="u_puesto">—</div></div>
        <div class="field"><label>Centro</label><div id="u_centro">—</div></div>
        <div class="field"><label>Líder</label><div id="u_lider">—</div></div>
        <div class="field"><label>Activo</label><div id="u_activo">—</div></div>
        <div class="field"><label>Último login</label><div id="u_login">—</div></div>
        <div class="field"><label>Creado en</label><div id="u_creado">—</div></div>
    </div>
    <div class="modal-f">
        <button type="button" class="btn" id="btnCloseUser2">Cerrar</button>
    </div>
</dialog>

<!-- ====================== JS GENERAL LISTADO + FETCH ====================== -->
<script>
    // Context path y endpoint
    const ctx = '${pageContext.request.contextPath}';
    const API_EQUIPOS = ctx + '/equipos';

    function showLoading(){ document.body.classList.add('loading'); }
    function hideLoading(){ document.body.classList.remove('loading'); }

    // Colorear filas según estatus
    function colorizeStatusRows(){
        const tbody = document.getElementById('equiposBody');
        if (!tbody) return;
        const norm = (s) => (s||'').toString().trim()
            .toLowerCase()
            .normalize('NFD').replace(/[\u0300-\u036f]/g,'');

        const map = {
            'disponible': 'status-disponible',
            'asignado': 'status-asignado',
            'asigando': 'status-asignado',
            'en reparacion': 'status-reparacion',
            'desuso': 'status-desuso',
        };

        Array.from(tbody.rows).forEach(tr => {
            const estText = norm(tr.cells[5] ? tr.cells[5].textContent : '');
            tr.classList.remove('status-disponible','status-asignado','status-reparacion','status-desuso');
            const cls = map[estText];
            if (cls) tr.classList.add(cls);
        });
    }

    // NUEVO: obtener id directamente (idEquipo / id_equipo / id)
    function getEquipoId(obj){
        if (!obj || typeof obj !== 'object') return null;

        if (obj.idEquipo != null)    return obj.idEquipo;
        if (obj.id_equipo != null)   return obj.id_equipo;
        if (obj.id != null)          return obj.id;

        // Fallback por si el campo viene con otro nombre raro
        for (const [k,v] of Object.entries(obj)) {
            const lk = k.toLowerCase();
            if (lk.includes('idequipo') || (lk.includes('id') && lk.includes('equipo'))) {
                return v;
            }
        }
        return null;
    }

    // Render de filas
    function renderEquiposTable(equipos){
        const tbody = document.getElementById('equiposBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        // 🔹 Mapa global: id -> objeto del listado
        window.EQUIPOS_BY_ID = {};

        if (!equipos || equipos.length === 0) {
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 7;
            td.textContent = 'No se encontraron equipos con los filtros actuales.';
            td.style.padding = '.5rem';
            tr.appendChild(td);
            tbody.appendChild(tr);
            colorizeStatusRows();
            return;
        }

        equipos.forEach(e => {
            const id = getEquipoId(e);

            // Si NO hay id, no dibujamos la fila (evitamos botones rotos)
            if (!id) {
                console.warn('Equipo sin id en JSON:', e);
                return;
            }

            // 🔹 Guardar en cache
            window.EQUIPOS_BY_ID[String(id)] = e;

            const tr = document.createElement('tr');

            const tdTipo   = document.createElement('td');
            const tdSerie  = document.createElement('td');
            const tdMarca  = document.createElement('td');
            const tdModelo = document.createElement('td');
            const tdUbic   = document.createElement('td');
            const tdEst    = document.createElement('td');
            const tdAcc    = document.createElement('td');

            tdTipo.textContent   = e.tipoNombre        || '';
            tdSerie.textContent  = e.numeroSerie       || '—';
            tdMarca.textContent  = e.marcaNombre       || '—';
            tdModelo.textContent = e.modeloNombre      || '—';
            tdUbic.textContent   = e.ubicacionNombre   || '—';
            tdEst.textContent    = e.estatusNombre     || '';

            tdAcc.style.width = '170px';
            tdAcc.innerHTML =
                '<button type="button" class="btn btn-secondary btn-detalles" ' +
                'data-id="' + id + '">Detalles</button>' +
                '<button type="button" class="btn btn-primary btn-editar" ' +
                'data-id="' + id + '">Editar</button>' +
                '<form style="display:inline" method="post" action="' + ctx + '/equipos">' +
                '<input type="hidden" name="action" value="delete"/>' +
                '<input type="hidden" name="idEquipo" value="' + id + '"/>' +
                '<button class="btn btn-danger" ' +
                'onclick="return confirm(\'¿Eliminar este equipo?\')">Eliminar</button>' +
                '</form>';

            tr.appendChild(tdTipo);
            tr.appendChild(tdSerie);
            tr.appendChild(tdMarca);
            tr.appendChild(tdModelo);
            tr.appendChild(tdUbic);
            tr.appendChild(tdEst);
            tr.appendChild(tdAcc);

            tbody.appendChild(tr);
        });

        // Reaplicar colores por estatus
        colorizeStatusRows();
    }




    // Render de paginación
    function renderPagination(page, totalPages){
        const cont = document.getElementById('pagination');
        if (!cont) return;
        cont.innerHTML = '';
        if (!totalPages || totalPages <= 1) return;

        function createBtn(label, targetPage, disabled, extraClass){
            const a = document.createElement('button');
            a.type = 'button';
            a.textContent = label;
            a.className = 'btn ' + (extraClass || '');
            if (disabled) {
                a.disabled = true;
            } else {
                a.dataset.page = String(targetPage);
            }
            return a;
        }

        // Prev
        cont.appendChild(createBtn('«', page - 1, page <= 1, ''));

        const pages = [];
        pages.push(1);
        if (page - 1 > 2) pages.push('...');
        for (let p = page - 1; p <= page + 1; p++) {
            if (p > 1 && p < totalPages) pages.push(p);
        }
        if (page + 1 < totalPages - 1) pages.push('...');
        if (totalPages > 1) pages.push(totalPages);

        pages.forEach(p => {
            if (p === '...') {
                const span = document.createElement('span');
                span.textContent = '…';
                span.style.padding = '0 .25rem';
                span.style.color = '#64748b';
                cont.appendChild(span);
            } else {
                const isCurrent = (p === page);
                const btn = createBtn(String(p), p, false, isCurrent ? 'btn-secondary' : '');
                cont.appendChild(btn);
            }
        });

        // Next
        cont.appendChild(createBtn('»', page + 1, page >= totalPages, ''));
    }

    // Carga por fetch
    async function loadEquipos(page = 1){
        const form = document.querySelector('form.toolbar');
        if (!form) return;

        const params = new URLSearchParams();
        params.set('action', 'listJson');
        params.set('page', String(page));

        const fd = new FormData(form);
        for (const [k,v] of fd.entries()) {
            if (k === 'page') continue; // ignoramos el hidden original
            if (v != null && String(v).trim() !== '') {
                params.set(k, String(v));
            }
        }

        showLoading();
        try {
            const res = await fetch(API_EQUIPOS + '?' + params.toString(), {
                headers: { 'Accept': 'application/json' }
            });
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const data = await res.json();
            renderEquiposTable(data.equipos || []);
            renderPagination(data.page || 1, data.totalPages || 1);
        } catch (err) {
            console.error(err);
            alert('No fue posible cargar el listado de equipos.');
        } finally {
            hideLoading();
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        const form = document.querySelector('form.toolbar');
        if (form) {
            // Evitar submit normal y usar fetch
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                loadEquipos(1);
            });

            // Cambios en selects -> recargar
            form.querySelectorAll('select').forEach(sel => {
                sel.addEventListener('change', () => loadEquipos(1));
            });

            // Búsqueda con debounce
            const inputQ = form.querySelector('input[name="q"]');
            if (inputQ) {
                let t;
                inputQ.addEventListener('input', () => {
                    clearTimeout(t);
                    t = setTimeout(() => loadEquipos(1), 400);
                });
            }
        }

        // Click en paginación
        const pag = document.getElementById('pagination');
        if (pag) {
            pag.addEventListener('click', (e) => {
                const btn = e.target.closest('button[data-page]');
                if (!btn) return;
                const p = parseInt(btn.dataset.page, 10);
                if (!isNaN(p)) loadEquipos(p);
            });
        }

        const tw = document.querySelector('.table-wrapper');
        if (tw) tw.classList.add('table-appear');

        // Primera carga
        loadEquipos(1);
    });
</script>

<!-- ====================== CATÁLOGOS JS ====================== -->
<script>
    // Modelos disponibles
    window.ALL_MODELOS = [
        <c:forEach var="m" items="${modelos}" varStatus="s">
        { idModelo: ${m.idModelo}, idMarca: ${m.idMarca}, nombre: '<c:out value="${m.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
        </c:forEach>
    ];
    window.ALL_TIPOS = [
        <c:forEach var="t" items="${tipos}" varStatus="s">
        { idTipo: ${t.idTipo}, nombre: '<c:out value="${t.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
        </c:forEach>
    ];
    window.ALL_MARCAS = [
        <c:forEach var="m" items="${marcas}" varStatus="s">
        { idMarca: ${m.idMarca}, nombre: '<c:out value="${m.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
        </c:forEach>
    ];
    window.ALL_UBICS = [
        <c:forEach var="u" items="${ubicaciones}" varStatus="s">
        { idUbicacion: ${u.idUbicacion}, nombre: '<c:out value="${u.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
        </c:forEach>
    ];
    window.ALL_ESTATUS = [
        <c:forEach var="s" items="${estatuses}" varStatus="st">
        { idEstatus: ${s.idEstatus}, nombre: '<c:out value="${s.nombre}"/>' }<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    ];
    window.ALL_COLORES = [
        <c:forEach var="c" items="${colores}" varStatus="st">
        { idColor: ${c.idColor}, nombre: '<c:out value="${c.nombre}"/>' }<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    ];
</script>

<!-- ====================== MODAL EDICIÓN (EDITAR) ====================== -->
<script>
    (() => {
        const dlg  = document.getElementById('editModal');
        const form = document.getElementById('editForm');
        const modalTitle = document.getElementById('modalTitle');
        const formAction = document.getElementById('formAction');

        const selTipoModal   = document.getElementById('idTipo');
        const selMarcaModal  = document.getElementById('idMarca');
        const selModeloModal = document.getElementById('idModelo');
        const selEstatusModal= document.getElementById('idEstatus');
        const tipoNombreInp  = document.getElementById('tipoNombre');
        const numeroSerieInp = document.getElementById('numeroSerie');
        const idUbicacionSel = document.getElementById('idUbicacion');
        const ipFijaInp      = document.getElementById('ipFija');
        const puertoInp      = document.getElementById('puertoEthernet');
        const notasInp       = document.getElementById('notas');
        const returnQueryInp = document.getElementById('returnQuery');

        const simFields = document.getElementById('simFields');
        const simNumeroInp = document.getElementById('simNumeroAsignado');
        const simImeiInp   = document.getElementById('simImei');

        const consumibleFields = document.getElementById('consumibleFields');
        const colorConsumibleSel = document.getElementById('idColorConsumible');

        const asignacionesPanel = document.getElementById('asignacionesPanel');
        const asignacionesEmpty = document.getElementById('asignacionesEmpty');
        const asignacionesTable = document.getElementById('asignacionesTable');
        const asignacionesTbody = asignacionesTable ? asignacionesTable.querySelector('tbody') : null;

        const STATUS_ASIGNADO = 2;
        let estatusOriginal = null;
        let estatusSeleccionado = null;
        let asignacionesActuales = [];

        function buildModeloOptionsModal(idMarca, selectedIdModelo) {
            selModeloModal.innerHTML = '';
            const def = document.createElement('option');
            def.value = '';
            def.textContent = '—';
            selModeloModal.appendChild(def);

            if (!idMarca) {
                selModeloModal.value = '';
                return;
            }
            const modelos = (window.ALL_MODELOS || []).filter(m => String(m.idMarca) === String(idMarca));
            for (const m of modelos) {
                const opt = document.createElement('option');
                opt.value = String(m.idModelo);
                opt.textContent = m.nombre;
                selModeloModal.appendChild(opt);
            }
            if (selectedIdModelo && modelos.some(m => String(m.idModelo) === String(selectedIdModelo))) {
                selModeloModal.value = String(selectedIdModelo);
            } else {
                selModeloModal.value = '';
            }
        }

        function toggleSubtypeFields() {
            const opt = selTipoModal.options[selTipoModal.selectedIndex];
            const nombreTipo = (opt && opt.textContent ? opt.textContent.trim().toUpperCase() : '');
            tipoNombreInp.value = nombreTipo;

            const isSIM  = nombreTipo.includes('SIM');
            const isCONS = nombreTipo.includes('CONSUMIBLE') || nombreTipo.includes('CONSUM');

            simFields.style.display = isSIM ? '' : 'none';
            consumibleFields.style.display = isCONS ? '' : 'none';

            if (!isSIM) {
                if (simNumeroInp) simNumeroInp.value = '';
                if (simImeiInp)   simImeiInp.value   = '';
            }
            if (!isCONS && colorConsumibleSel) {
                colorConsumibleSel.value = '';
            }

            if (numeroSerieInp) numeroSerieInp.required = !(isSIM || isCONS);
        }

        if (selTipoModal) {
            selTipoModal.addEventListener('change', toggleSubtypeFields);
        }
        if (selMarcaModal) {
            selMarcaModal.addEventListener('change', () => buildModeloOptionsModal(selMarcaModal.value, ''));
        }

        function renderAsignaciones(list) {
            if (!asignacionesTable || !asignacionesTbody) return;
            asignacionesTbody.innerHTML = '';
            const has = Array.isArray(list) && list.length > 0;
            asignacionesEmpty.style.display = has ? 'none' : 'block';
            asignacionesTable.style.display = has ? 'table' : 'none';
            if (!has) return;

            const fmtDate = (val) => {
                if (!val && val !== 0) return '';
                try {
                    if (typeof val === 'number') {
                        const dnum = new Date(val);
                        if (!isNaN(dnum)) return dnum.toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' });
                    }
                    let s = String(val).trim();
                    if (/^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}(:\d{2})?$/.test(s)) s = s.replace(' ', 'T');
                    const d = new Date(s);
                    if (!isNaN(d)) return d.toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' });
                } catch(_){}
                return String(val);
            };

            const makeTd = (text) => {
                const td = document.createElement('td');
                td.style.padding = '.35rem';
                td.style.borderBottom = '1px solid #f1f5f9';
                td.textContent = (text !== null && text !== undefined) ? String(text) : '';
                return td;
            };

            for (const a of list) {
                const tr = document.createElement('tr');
                tr.appendChild(makeTd(a.usuarioNombre || ''));
                tr.appendChild(makeTd(a.asignadorNombre || ''));
                tr.appendChild(makeTd(fmtDate(a.asignadoEn)));
                tr.appendChild(makeTd(fmtDate(a.devueltoEn)));
                tr.appendChild(makeTd(a && a.devueltoEn ? 'Devuelto' : 'Activa'));

                const tdAcc = document.createElement('td');
                tdAcc.style.padding = '.35rem';
                tdAcc.style.borderBottom = '1px solid #f1f5f9';
                const b = document.createElement('button');
                b.type = 'button';
                b.className = 'btn btn-secondary btn-user-details';
                b.textContent = 'Ver usuario';
                b.dataset.userId = String(a.idUsuario || '');
                tdAcc.appendChild(b);
                tr.appendChild(tdAcc);

                asignacionesTbody.appendChild(tr);
            }
        }

        function ensureAsignadoOption(currentStatus) {
            if (!selEstatusModal) return;

            const tempOpt = selEstatusModal.querySelector('option[data-temp-asignado="1"]');
            if (tempOpt) tempOpt.remove();

            const currentVal = parseInt(currentStatus, 10);
            if (currentVal === STATUS_ASIGNADO) {
                const exists = Array.from(selEstatusModal.options).some(o => o.value === String(STATUS_ASIGNADO));
                if (!exists) {
                    const opt = document.createElement('option');
                    opt.value = String(STATUS_ASIGNADO);
                    opt.textContent = 'Asignado';
                    opt.setAttribute('data-temp-asignado', '1');
                    selEstatusModal.insertBefore(opt, selEstatusModal.firstChild);
                }
            }
        }

        if (selEstatusModal) {
            selEstatusModal.addEventListener('change', () => {
                const next = parseInt(selEstatusModal.value || '0', 10);
                if (estatusSeleccionado === STATUS_ASIGNADO && next !== STATUS_ASIGNADO) {
                    const total = Array.isArray(asignacionesActuales) ? asignacionesActuales.length : 0;
                    let msg = 'Cambiar el estatus eliminará todas las asignaciones del equipo.';
                    if (total > 0) msg += ' Total a eliminar: ' + total + '.';
                    msg += ' ¿Deseas continuar?';
                    if (!confirm(msg)) {
                        selEstatusModal.value = String(estatusSeleccionado);
                        return;
                    }
                    const tempOpt = selEstatusModal.querySelector('option[data-temp-asignado="1"]');
                    if (tempOpt) selEstatusModal.removeChild(tempOpt);
                }
                estatusSeleccionado = next;
            });

            form.addEventListener('submit', (ev) => {
                const next = parseInt(selEstatusModal.value || '0', 10);
                if (estatusOriginal === STATUS_ASIGNADO && next !== STATUS_ASIGNADO) {
                    const total = Array.isArray(asignacionesActuales) ? asignacionesActuales.length : 0;
                    let msg = 'Se eliminarán todas las asignaciones del equipo al guardar.';
                    if (total > 0) msg += ' Total: ' + total + '.';
                    msg += ' ¿Deseas continuar?';
                    if (!confirm(msg)) {
                        ev.preventDefault();
                    }
                }
            });
        }

        // 🔹 Rellena campos básicos a partir de un objeto (del listado o del GET)
        function fillBasicFields(obj) {
            if (!obj) return;

            if (selTipoModal) {
                selTipoModal.value = obj.idTipo != null ? String(obj.idTipo) : '';
                toggleSubtypeFields();
            }
            if (selMarcaModal) selMarcaModal.value = obj.idMarca != null ? String(obj.idMarca) : '';
            buildModeloOptionsModal(obj.idMarca, obj.idModelo);

            if (idUbicacionSel) idUbicacionSel.value = obj.idUbicacion != null ? String(obj.idUbicacion) : '';

            if (typeof obj.idEstatus !== 'undefined' && obj.idEstatus !== null) {
                estatusOriginal = obj.idEstatus;
                estatusSeleccionado = obj.idEstatus;
                ensureAsignadoOption(obj.idEstatus);
                if (selEstatusModal) selEstatusModal.value = String(obj.idEstatus);
            }

            if (numeroSerieInp) numeroSerieInp.value = obj.numeroSerie || '';
            if (ipFijaInp)      ipFijaInp.value      = obj.ipFija || '';
            if (puertoInp)      puertoInp.value      = obj.puertoEthernet || '';
            if (notasInp)       notasInp.value       = obj.notas || '';
        }

        // Click en botón Editar (delegado)
        document.addEventListener('click', async (ev) => {
            const btn = ev.target.closest('.btn-editar');
            if (!btn) return;
            ev.preventDefault();
            const id = btn.dataset.id;
            if(!id){ alert('No se encontró el ID del equipo'); return; }

            modalTitle.textContent = 'Editar equipo';
            formAction.value = 'save';
            document.getElementById('idEquipo').value = id;
            if (returnQueryInp) returnQueryInp.value = (window.location.search || '').replace(/^\?/, '');

            // 🔹 Primero rellenamos con lo que viene del listado (EQUIPOS_BY_ID)
            const cacheMap = window.EQUIPOS_BY_ID || {};
            const base = cacheMap[String(id)] || null;
            fillBasicFields(base);

            // Panel de asignaciones visible
            if (asignacionesPanel) asignacionesPanel.style.display = '';

            try {
                // Luego traemos datos extra (SIM / Consumible) desde action=get
                const url = ctx + '/equipos?action=get&id=' + encodeURIComponent(id);
                const r = await fetch(url, { headers: { 'Accept': 'application/json' } });

                if (r.ok) {
                    const e = await r.json();

                    // Si por alguna razón el cache no traía algo, lo completamos
                    fillBasicFields(e);

                    // Subtipo SIM
                    if (simNumeroInp) simNumeroInp.value = e.simNumeroAsignado || '';
                    if (simImeiInp)   simImeiInp.value   = e.simImei || '';

                    // Subtipo Consumible
                    if (colorConsumibleSel) {
                        const idColor = e.idColorConsumible != null ? e.idColorConsumible : e.idColor;
                        if (idColor != null) colorConsumibleSel.value = String(idColor);
                    }
                } else {
                    console.error('Error al cargar equipo', r.status);
                }

                // Cargar asignaciones
                try {
                    const r2 = await fetch(ctx + '/equipos?action=asignaciones&id=' + encodeURIComponent(id), {
                        headers: { 'Accept': 'application/json' }
                    });
                    if (r2.ok) {
                        const list = await r2.json();
                        asignacionesActuales = Array.isArray(list) ? list : [];
                        renderAsignaciones(asignacionesActuales);
                    } else {
                        asignacionesActuales = [];
                        renderAsignaciones([]);
                    }
                } catch(_) {
                    asignacionesActuales = [];
                    renderAsignaciones([]);
                }

                if (typeof dlg?.showModal === 'function') {
                    dlg.showModal();
                } else {
                    alert('Tu navegador no soporta el componente de diálogo.');
                }

            } catch (err) {
                console.error(err);
                alert('Ocurrió un error al cargar el equipo para edición.');
            }
        });

        // Cierre con animación
        function animateCloseDialog(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=>{
                d.classList.remove('closing');
                d.close();
            }, { once:true });
        }

        const btnClose  = document.getElementById('btnClose');
        const btnCancel = document.getElementById('btnCancel');
        if (btnClose)  btnClose.onclick  = () => animateCloseDialog(dlg);
        if (btnCancel) btnCancel.onclick = () => animateCloseDialog(dlg);

        if (dlg) {
            dlg.addEventListener('cancel', (ev)=>{
                ev.preventDefault();
                animateCloseDialog(dlg);
            });
        }
    })();
</script>


<!-- ====================== MODAL DETALLES ====================== -->
<script>
    (() => {
        const dDlg = document.getElementById('detailsModal');
        const btnClose = document.getElementById('btnCloseDetails');
        const btnClose2= document.getElementById('btnCloseDetails2');
        const el = (id) => document.getElementById(id);
        const show = (v) => (v==null || v==='' ? '—' : v);

        const asgPanel = document.getElementById('detailsAsignacionesPanel');
        const asgEmpty = document.getElementById('detailsAsignacionesEmpty');
        const asgTable = document.getElementById('detailsAsignacionesTable');
        const asgTbody = asgTable ? asgTable.querySelector('tbody') : null;

        const fmtDate = (val) => {
            if (!val && val !== 0) return '';
            try{
                if (typeof val === 'number') {
                    const d = new Date(val);
                    if (!isNaN(d)) return d.toLocaleString('es-MX',{dateStyle:'medium',timeStyle:'short'});
                }
                let s = String(val).trim();
                if (/^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}(:\d{2})?$/.test(s)) s = s.replace(' ', 'T');
                const d2 = new Date(s);
                if (!isNaN(d2)) return d2.toLocaleString('es-MX',{dateStyle:'medium',timeStyle:'short'});
            }catch(_){}
            return String(val);
        };

        function resetDetails(){
            ['d_tipo','d_numeroSerie','d_marca','d_modelo','d_ubicacion','d_estatus','d_ip','d_puerto','d_notas','d_simNumero','d_simImei','d_colorConsumible']
                .forEach(id => { const n = el(id); if (n) n.textContent = '—'; });
            const sim = document.getElementById('detailsSim'); if (sim) sim.style.display = 'none';
            const cons= document.getElementById('detailsConsumible'); if (cons) cons.style.display = 'none';

            if (asgTbody) asgTbody.innerHTML = '';
            if (asgEmpty) asgEmpty.style.display = 'block';
            if (asgTable) asgTable.style.display = 'none';
        }

        function renderAsg(list){
            if (!asgTbody || !asgTable || !asgEmpty) return;
            asgTbody.innerHTML = '';
            const has = Array.isArray(list) && list.length > 0;
            asgEmpty.style.display = has ? 'none' : 'block';
            asgTable.style.display = has ? 'table' : 'none';
            if (!has) return;

            const makeTd = (txt)=>{
                const td = document.createElement('td');
                td.style.padding = '.35rem';
                td.style.borderBottom = '1px solid #f1f5f9';
                td.textContent = txt || '';
                return td;
            };

            list.forEach(a=>{
                const tr = document.createElement('tr');
                tr.appendChild(makeTd(a.usuarioNombre || ''));
                tr.appendChild(makeTd(a.asignadorNombre || ''));
                tr.appendChild(makeTd(fmtDate(a.asignadoEn)));
                tr.appendChild(makeTd(a.devueltoEn ? fmtDate(a.devueltoEn) : ''));
                tr.appendChild(makeTd(a && a.devueltoEn ? 'Devuelto' : 'Activa'));
                const tdAcc = makeTd('');
                const b = document.createElement('button');
                b.type = 'button';
                b.className = 'btn btn-secondary btn-user-details';
                b.textContent = 'Ver usuario';
                b.dataset.userId = String(a.idUsuario || '');
                tdAcc.textContent = '';
                tdAcc.appendChild(b);
                tr.appendChild(tdAcc);
                asgTbody.appendChild(tr);
            });
        }

        // 🔹 Rellena los datos generales desde un objeto del listado o del GET
        function fillBasicDetails(obj){
            if (!obj) return;

            const findName = (arr, key, id) => {
                try { return (arr || []).find(x => String(x[key]) === String(id))?.nombre || ''; } catch(_) { return ''; }
            };

            const tipoNombre      = obj.tipoNombre      || findName(window.ALL_TIPOS, 'idTipo', obj.idTipo);
            const marcaNombre     = obj.marcaNombre     || findName(window.ALL_MARCAS, 'idMarca', obj.idMarca);
            const modeloNombre    = obj.modeloNombre    || findName(window.ALL_MODELOS, 'idModelo', obj.idModelo);
            const ubicacionNombre = obj.ubicacionNombre || findName(window.ALL_UBICS, 'idUbicacion', obj.idUbicacion);
            const estatusNombre   = obj.estatusNombre   || findName(window.ALL_ESTATUS, 'idEstatus', obj.idEstatus);

            el('d_tipo').textContent        = show(tipoNombre);
            el('d_numeroSerie').textContent = show(obj.numeroSerie);
            el('d_marca').textContent       = show(marcaNombre);
            el('d_modelo').textContent      = show(modeloNombre);
            el('d_ubicacion').textContent   = show(ubicacionNombre);
            el('d_estatus').textContent     = show(estatusNombre);
            el('d_ip').textContent          = show(obj.ipFija);
            el('d_puerto').textContent      = show(obj.puertoEthernet);
            el('d_notas').textContent       = show(obj.notas);
        }

        async function openDetails(id){
            resetDetails();

            // 🔹 Primero usamos lo que viene del listado
            const cacheMap = window.EQUIPOS_BY_ID || {};
            const base = cacheMap[String(id)] || null;
            fillBasicDetails(base);

            try{
                // Datos extra (SIM / Consumible, notas más recientes, etc.)
                const r = await fetch(ctx + '/equipos?action=get&id=' + encodeURIComponent(id), {
                    headers: { 'Accept':'application/json' }
                });
                if (r.ok) {
                    const e = await r.json();

                    // Si no hubo cache, rellenamos todo desde este objeto
                    if (!base) fillBasicDetails(e);

                    // Sobrescribir cadenas si vienen en el GET
                    if (e.numeroSerie)   el('d_numeroSerie').textContent = show(e.numeroSerie);
                    if (e.ipFija)        el('d_ip').textContent          = show(e.ipFija);
                    if (e.puertoEthernet)el('d_puerto').textContent      = show(e.puertoEthernet);
                    if (e.notas)         el('d_notas').textContent       = show(e.notas);

                    const tipoTexto = (el('d_tipo').textContent || '').toUpperCase();
                    const simBlock  = document.getElementById('detailsSim');
                    const consBlock = document.getElementById('detailsConsumible');

                    // SIM
                    if (tipoTexto.includes('SIM')) {
                        if (simBlock) simBlock.style.display='';
                        el('d_simNumero').textContent = show(e.simNumeroAsignado);
                        el('d_simImei').textContent   = show(e.simImei);
                    }

                    // Consumible
                    if (tipoTexto.includes('CONSUMIBLE') || tipoTexto.includes('CONSUM')) {
                        if (consBlock) consBlock.style.display='';
                        const colorId = e.idColorConsumible != null ? e.idColorConsumible : e.idColor;
                        const findColor = (arr, id) => {
                            try { return (arr || []).find(c => String(c.idColor) === String(id))?.nombre || ''; } catch(_) { return ''; }
                        };
                        const colorNom = colorId != null ? findColor(window.ALL_COLORES, colorId) : '';
                        el('d_colorConsumible').textContent = show(colorNom);
                    }
                }

                // Asignaciones
                try{
                    const r2 = await fetch(ctx + '/equipos?action=asignaciones&id=' + encodeURIComponent(id), {
                        headers: { 'Accept':'application/json' }
                    });
                    if (r2.ok) {
                        const list = await r2.json();
                        renderAsg(Array.isArray(list) ? list : []);
                    } else {
                        renderAsg([]);
                    }
                }catch(_){
                    renderAsg([]);
                }

                if (typeof dDlg?.showModal === 'function') dDlg.showModal();
                else alert('Tu navegador no soporta el componente de diálogo.');

            }catch(err){
                console.error(err);
                alert('No fue posible cargar los detalles del equipo.');
            }
        }

        document.addEventListener('click', (ev) => {
            const btn = ev.target.closest('.btn-detalles');
            if (!btn) return;
            ev.preventDefault();
            const id = btn.getAttribute('data-id');
            if (id) openDetails(id);
        });

        function animateClose(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=>{
                d.classList.remove('closing');
                d.close();
            }, {once:true});
        }
        if (btnClose)  btnClose.onclick  = () => animateClose(dDlg);
        if (btnClose2) btnClose2.onclick = () => animateClose(dDlg);
        if (dDlg) dDlg.addEventListener('cancel', (e)=>{ e.preventDefault(); animateClose(dDlg); });
    })();
</script>


<!-- ====================== MODO CREACIÓN (AGREGAR EQUIPO) ====================== -->
<script>
    (() => {
        const dlg  = document.getElementById('editModal');
        const form = document.getElementById('editForm');
        const modalTitle = document.getElementById('modalTitle');
        const formAction = document.getElementById('formAction');

        const selTipoModal   = document.getElementById('idTipo');
        const selMarcaModal  = document.getElementById('idMarca');
        const selModeloModal = document.getElementById('idModelo');
        const selEstatusModal= document.getElementById('idEstatus');
        const tipoNombreInp  = document.getElementById('tipoNombre');
        const numeroSerieInp = document.getElementById('numeroSerie');
        const selUbic        = document.getElementById('idUbicacion');
        const ipFija         = document.getElementById('ipFija');
        const puerto         = document.getElementById('puertoEthernet');
        const notas          = document.getElementById('notas');
        const returnQueryInp = document.getElementById('returnQuery');

        const simFields = document.getElementById('simFields');
        const simNumero = document.getElementById('simNumeroAsignado');
        const simImei   = document.getElementById('simImei');

        const consumibleFields = document.getElementById('consumibleFields');
        const idColorConsumible= document.getElementById('idColorConsumible');

        const asignacionesPanel = document.getElementById('asignacionesPanel');
        const asignacionesEmpty = document.getElementById('asignacionesEmpty');
        const asignacionesTable = document.getElementById('asignacionesTable');
        const asignacionesTbody = asignacionesTable ? asignacionesTable.querySelector('tbody') : null;

        function buildModeloOptionsModal(idMarca, selectedIdModelo) {
            selModeloModal.innerHTML = '';
            const def = document.createElement('option');
            def.value = '';
            def.textContent = '—';
            selModeloModal.appendChild(def);

            if (!idMarca) {
                selModeloModal.value = '';
                return;
            }
            const modelos = (window.ALL_MODELOS || []).filter(m => String(m.idMarca) === String(idMarca));
            for (const m of modelos) {
                const opt = document.createElement('option');
                opt.value = String(m.idModelo);
                opt.textContent = m.nombre;
                selModeloModal.appendChild(opt);
            }
            if (selectedIdModelo && modelos.some(m => String(m.idModelo) === String(selectedIdModelo))) {
                selModeloModal.value = String(selectedIdModelo);
            } else {
                selModeloModal.value = '';
            }
        }

        function toggleSubtypeFields() {
            const opt = selTipoModal.options[selTipoModal.selectedIndex];
            const nombreTipo = (opt && opt.textContent ? opt.textContent.trim().toUpperCase() : '');
            tipoNombreInp.value = nombreTipo;

            const isSIM  = nombreTipo.includes('SIM');
            const isCONS = nombreTipo.includes('CONSUMIBLE') || nombreTipo.includes('CONSUM');

            simFields.style.display = isSIM ? '' : 'none';
            consumibleFields.style.display = isCONS ? '' : 'none';

            if (!isSIM) {
                if (simNumero) simNumero.value = '';
                if (simImei)   simImei.value   = '';
            }
            if (!isCONS && idColorConsumible) {
                idColorConsumible.value = '';
            }

            if (numeroSerieInp) numeroSerieInp.required = !(isSIM || isCONS);
        }

        if (selTipoModal) selTipoModal.addEventListener('change', toggleSubtypeFields);
        if (selMarcaModal) selMarcaModal.addEventListener('change', () => buildModeloOptionsModal(selMarcaModal.value, ''));

        function resetFormForCreate() {
            modalTitle.textContent = 'Agregar equipo';
            formAction.value = 'create';

            document.getElementById('idEquipo').value = '';
            if (numeroSerieInp) numeroSerieInp.value = '';
            if (selTipoModal) selTipoModal.selectedIndex = 0;
            if (selMarcaModal) selMarcaModal.value = '';
            buildModeloOptionsModal('', '');
            if (selUbic) selUbic.value = '';
            if (selEstatusModal) selEstatusModal.value = '';
            if (ipFija) ipFija.value = '';
            if (puerto) puerto.value = '';
            if (notas)  notas.value  = '';

            if (numeroSerieInp) numeroSerieInp.required = true;

            if (returnQueryInp) returnQueryInp.value = (window.location.search || '').replace(/^\?/, '');

            if (asignacionesPanel) asignacionesPanel.style.display = 'none';
            if (asignacionesTbody) asignacionesTbody.innerHTML = '';
            if (asignacionesEmpty) asignacionesEmpty.style.display = 'block';
            if (asignacionesTable) asignacionesTable.style.display = 'none';

            const tempOpt = selEstatusModal && selEstatusModal.querySelector('option[data-temp-asignado="1"]');
            if (tempOpt && selEstatusModal) tempOpt.remove();

            toggleSubtypeFields();
        }

        const addBtn = document.querySelector('a.btn.btn-primary[href$="/equipos-nuevo"]');
        if (addBtn) {
            addBtn.addEventListener('click', (ev) => {
                ev.preventDefault();
                resetFormForCreate();
                if (typeof dlg?.showModal === 'function') {
                    dlg.showModal();
                } else {
                    alert('Tu navegador no soporta el componente de diálogo.');
                }
            });
        }
    })();
</script>

<!-- ====================== NAVBAR / BOX-MENU DRAG ====================== -->
<script>
    (function(){
        const menuBox   = document.querySelector('.box-menu');
        const wrapper   = document.querySelector('.box-menu .wrapper');
        const burger    = document.querySelector('.hamburguer');
        const menuLinks = document.querySelectorAll('.box-menu .menu a');

        const SAVE_KEY = 'boxMenuPos';
        let isDragging = false;
        let moved = false;
        let startX = 0, startY = 0, offX = 0, offY = 0;

        function getPoint(e){
            if (e.touches && e.touches[0]) return {x:e.touches[0].clientX, y:e.touches[0].clientY};
            return {x:e.clientX, y:e.clientY};
        }
        function setPos(x,y){
            if (!menuBox) return;
            const w = menuBox.offsetWidth  || 60;
            const h = menuBox.offsetHeight || 60;
            const maxX = Math.max(0, (window.innerWidth  || 0) - w);
            const maxY = Math.max(0, (window.innerHeight || 0) - h);
            const nx = Math.max(0, Math.min(x, maxX));
            const ny = Math.max(0, Math.min(y, maxY));
            menuBox.style.left = nx + 'px';
            menuBox.style.top  = ny + 'px';
        }
        function onDown(e){
            if (!menuBox) return;
            const p = getPoint(e);
            isDragging = true;
            moved = false;
            startX = p.x; startY = p.y;
            offX = p.x - menuBox.offsetLeft;
            offY = p.y - menuBox.offsetTop;
            menuBox.classList.add('dragging');
            document.addEventListener('mousemove', onMove);
            document.addEventListener('mouseup', onUp);
            document.addEventListener('touchmove', onMove, {passive:false});
            document.addEventListener('touchend', onUp);
        }
        function onMove(e){
            if (!isDragging || !menuBox) return;
            const p = getPoint(e);
            const nx = p.x - offX;
            const ny = p.y - offY;
            if (Math.abs(p.x - startX) > 4 || Math.abs(p.y - startY) > 4) moved = true;
            setPos(nx, ny);
            if (e.cancelable) e.preventDefault();
        }
        function onUp(){
            if (!isDragging || !menuBox) return;
            isDragging = false;
            menuBox.classList.remove('dragging');
            document.removeEventListener('mousemove', onMove);
            document.removeEventListener('mouseup', onUp);
            document.removeEventListener('touchmove', onMove);
            document.removeEventListener('touchend', onUp);
            const left = parseInt(menuBox.style.left || menuBox.offsetLeft || 0, 10);
            const top  = parseInt(menuBox.style.top  || menuBox.offsetTop  || 0, 10);
            try{ localStorage.setItem(SAVE_KEY, JSON.stringify({left, top})); }catch(_){}
            setTimeout(()=>{ moved = false; }, 50);
        }

        if (menuBox) {
            try{
                const s = localStorage.getItem(SAVE_KEY);
                if (s) {
                    const p = JSON.parse(s);
                    if (typeof p.left === 'number' && typeof p.top === 'number') setPos(p.left, p.top);
                }
            }catch(_){}
            menuBox.addEventListener('mousedown', onDown);
            menuBox.addEventListener('touchstart', onDown, {passive:true});
        }

        if (wrapper) {
            wrapper.addEventListener('click', function(ev){
                if (isDragging || moved) return;
                if (menuBox) menuBox.classList.toggle('full-menu');
                if (burger)  burger.classList.toggle('active');
            });
        }

        function updateArrow(currentLink){
            document.querySelectorAll('.box-menu .menu a .text i.fa-arrow-left')
                .forEach(function(i){ i.remove(); });
            if (!currentLink) return;
            const txt = currentLink.querySelector('.text');
            if (txt) {
                const i = document.createElement('i');
                i.className = 'fa-solid fa-arrow-left';
                i.style.marginLeft = '6px';
                txt.appendChild(i);
            }
        }

        (function setActiveFromLocation(){
            const file = (window.location.pathname || '').split('/').pop().toLowerCase();
            let current = null;
            menuLinks.forEach(function(a){
                const href = (a.getAttribute('href') || '').toLowerCase();
                if (!current && href && file && href.endsWith(file)) current = a;
            });
            if (!current) current = document.querySelector('.box-menu .menu a.active');
            if (current) {
                menuLinks.forEach(function(l){ l.classList.remove('active'); });
                current.classList.add('active');
                updateArrow(current);
            }
        })();

        menuLinks.forEach(function(link){
            link.addEventListener('click', function(){
                menuLinks.forEach(function(l){ l.classList.remove('active'); });
                link.classList.add('active');
                updateArrow(link);
            });
        });
    })();
</script>

<!-- ====================== MODAL USUARIO ====================== -->
<script>
    (() => {
        const uDlg = document.getElementById('userModal');
        const u = (id) => document.getElementById(id);
        const fmt = (d) => {
            if (!d) return '—';
            try {
                const x = new Date(d);
                if (!isNaN(x)) return x.toLocaleString('es-MX',{dateStyle:'medium',timeStyle:'short'});
            } catch(_) {}
            return String(d);
        };
        function fillUser(data){
            u('u_nombre').textContent = [data.nombre, data.apellidoPaterno, data.apellidoMaterno].filter(Boolean).join(' ').trim() || '—';
            u('u_email').textContent  = data.email || '—';
            u('u_tel').textContent    = data.telefono || '—';
            u('u_rol').textContent    = data.nombreRol || '—';
            u('u_puesto').textContent = data.nombrePuesto || '—';
            u('u_centro').textContent = data.nombreCentro || '—';
            u('u_lider').textContent  = data.nombreLider || '—';
            u('u_activo').textContent = (data.activo === true ? 'Sí' : (data.activo === false ? 'No' : '—'));
            u('u_login').textContent  = fmt(data.ultimoLogin);
            u('u_creado').textContent = fmt(data.creadoEn);
        }
        async function openUserDetails(idUsuario){
            if (!idUsuario) return;
            try{
                const r = await fetch(ctx + '/equipos?action=usuario&idUsuario=' + encodeURIComponent(idUsuario), {
                    headers: { 'Accept':'application/json' }
                });
                if (!r.ok) throw new Error('HTTP ' + r.status);
                const data = await r.json();
                fillUser(data);
                if (typeof uDlg?.showModal === 'function') uDlg.showModal();
            } catch(err){
                console.error(err);
                alert('No fue posible cargar el usuario.');
            }
        }
        document.addEventListener('click', (ev) => {
            const b = ev.target.closest('.btn-user-details');
            if (!b) return;
            ev.preventDefault();
            const idUsuario = b.getAttribute('data-user-id');
            openUserDetails(idUsuario);
        });

        const btnCloseUser  = document.getElementById('btnCloseUser');
        const btnCloseUser2 = document.getElementById('btnCloseUser2');
        function animateClose(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=>{
                d.classList.remove('closing');
                d.close();
            }, {once:true});
        }
        if (btnCloseUser)  btnCloseUser.onclick  = () => animateClose(uDlg);
        if (btnCloseUser2) btnCloseUser2.onclick = () => animateClose(uDlg);
        if (uDlg) uDlg.addEventListener('cancel', (e)=>{ e.preventDefault(); animateClose(uDlg); });
    })();
</script>

</body>
</html>
