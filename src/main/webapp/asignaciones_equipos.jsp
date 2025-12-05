<%@ page isELIgnored="false" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8" />
                <title>Asignaciones en equipos</title>
                <link rel="stylesheet" href="css/style.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css"
                    integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ=="
                    crossorigin="anonymous" referrerpolicy="no-referrer" />
                <style>
                    /* Estilos para modales más grandes */
                    #historialModal,
                    #gestionModal {
                        width: 90vw;
                        max-width: 900px;
                        max-height: calc(100vh - 3rem);
                        padding: 0;
                        border-radius: .75rem;
                        border: 1px solid #1d4ed8;
                    }

                    #historialModal[open],
                    #gestionModal[open] {
                        display: flex;
                        flex-direction: column;
                    }

                    #historialModal .modal-b,
                    #gestionModal .modal-b {
                        flex: 1 1 auto;
                        overflow: auto;
                        padding: 1rem 1.5rem;
                    }

                    /* Tabla de historial con scroll */
                    .historial-table-wrapper {
                        max-height: 400px;
                        overflow-y: auto;
                        border: 1px solid #e5e7eb;
                        border-radius: .5rem;
                    }

                    .historial-table-wrapper table {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    .historial-table-wrapper thead th {
                        position: sticky;
                        top: 0;
                        z-index: 2;
                        background: #f8fafc;
                        border-bottom: 2px solid #e5e7eb;
                    }

                    .historial-table-wrapper tbody td {
                        border-bottom: 1px solid #f1f5f9;
                    }

                </style>
            </head>

            <body>
                <div id="loadingOverlay">
                    <div class="loader" aria-label="Cargando"></div>
                </div>

                <!-- MENU FLOTANTE -->
                <div class="box-menu">
                    <div class="wrapper">
                        <div class="hamburguer">
                            <span></span><span></span><span></span>
                            <span></span><span></span><span></span><span></span>
                        </div>
                    </div>
                    <div class="menu">
                        <a href="equipos.jsp"><span class="icon fa-solid fa-desktop"></span><span
                                class="text">Equipos</span></a>
                        <a href="sims"><span class="icon fa-solid fa-sim-card"></span><span class="text">Sims</span>
                        </a>
                        <a href="consumibles"><span class="icon fa-solid fa-boxes-stacked"></span><span
                                class="text">Consumibles</span> </a>
                        <a href="asignaciones.jsp" class="active"><span
                                class="icon fa-solid fa-arrow-right-arrow-left"></span><span
                                class="text">Asignaciones</span></a>
                        <a href="catalogos.jsp"><span class="icon fa-solid fa-folder-open"></span><span
                                class="text">Catalogos</span></a>
                        <a href="usuarios.jsp"><span class="icon fa-solid fa-users"></span><span
                                class="text">Usuarios</span></a>
                        <a href="dashboard.jsp"><span class="icon fa-solid fa-chart-line"></span><span
                                class="text">Dashboard</span></a>
                    </div>
                </div>

                <main class="container">
                    <div class="card card-compact">
                        <div style="display:flex;justify-content:space-between;align-items:center;gap:.75rem;">
                            <div>
                                <h1>Asignaciones en equipos</h1>
                                <p style="margin:0;color:#6b7280;font-size:.9rem;">
                                    Lista de equipos y su estado de asignación. Usa los botones
                                    <strong>Historial</strong> y <strong>Gestionar</strong> para ver y modificar las
                                    asignaciones de cada equipo.
                                </p>
                            </div>
                            <a href="asignaciones.jsp" class="btn btn-secondary">
                                <i class="fa-solid fa-arrow-left"></i>&nbsp; Regresar
                            </a>
                        </div>

                        <c:if test="${not empty sessionScope.flashOk}">
                            <div class="alert alert-ok">
                                ${sessionScope.flashOk}
                            </div>
                            <c:remove var="flashOk" scope="session" />
                        </c:if>
                        <c:if test="${not empty sessionScope.flashError}">
                            <div class="alert alert-err">
                                ${sessionScope.flashError}
                            </div>
                            <c:remove var="flashError" scope="session" />
                        </c:if>

                        <!-- Tabla de equipos -->
                        <div class="table-wrapper" style="margin-top:1rem;">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tipo</th>
                                        <th>Núm. Serie</th>
                                        <th>Marca</th>
                                        <th>Modelo</th>
                                        <th>Ubicación</th>
                                        <th>Estatus</th>
                                        <th>Asignado a</th>
                                        <th style="width:240px;text-align:center;">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="equipo" items="${listaEquipos}">
                                        <tr>
                                            <td>${equipo.idEquipo}</td>
                                            <td>${equipo.tipoNombre}</td>
                                            <td>${equipo.numeroSerie}</td>
                                            <td>
                                                <c:out value="${empty equipo.marcaNombre ? '—' : equipo.marcaNombre}" />
                                            </td>
                                            <td>
                                                <c:out
                                                    value="${empty equipo.modeloNombre ? '—' : equipo.modeloNombre}" />
                                            </td>
                                            <td>
                                                <c:out
                                                    value="${empty equipo.ubicacionNombre ? '—' : equipo.ubicacionNombre}" />
                                            </td>
                                            <td>${equipo.estatusNombre}</td>
                                            <td>
                                                <c:out
                                                    value="${empty asignacionActual[equipo.idEquipo] ? '—' : asignacionActual[equipo.idEquipo]}" />
                                            </td>
                                            <td style="text-align:center;">
                                                <button type="button" class="btn btn-secondary btn-historial"
                                                    data-id-equipo="${equipo.idEquipo}"
                                                    data-equipo="${equipo.tipoNombre} - ${equipo.numeroSerie}">
                                                    Historial
                                                </button>
                                                <button type="button" class="btn btn-primary btn-gestionar"
                                                    data-id-equipo="${equipo.idEquipo}"
                                                    data-equipo="${equipo.tipoNombre} - ${equipo.numeroSerie}">
                                                    Gestionar
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty listaEquipos}">
                                        <tr>
                                            <td colspan="9" style="text-align:center;color:#6b7280;padding:.75rem;">
                                                No hay equipos para mostrar.
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </main>

                <!-- MODAL HISTORIAL DE ASIGNACIONES -->
                <dialog id="historialModal">
                    <div class="modal-h">
                        <h3 id="hist_title">Historial de asignaciones</h3>
                        <button type="button" class="btn" id="hist_btnClose">✕</button>
                    </div>
                    <div class="modal-b">
                        <div class="field">
                            <label>Equipo</label>
                            <div id="hist_equipoNombre">—</div>
                        </div>

                        <div class="field" style="grid-column:1/-1;margin-top:.5rem;">
                            <label>Historial completo</label>
                            <div id="hist_mensajes" style="margin-bottom:.6rem;display:none;">
                                <!-- Mensajes de error -->
                            </div>
                            <div class="historial-table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th style="padding:.5rem;text-align:left;">Usuario</th>
                                            <th style="padding:.5rem;text-align:left;">Asignado por</th>
                                            <th style="padding:.5rem;text-align:left;">Asignado en</th>
                                            <th style="padding:.5rem;text-align:left;">Devuelto en</th>
                                            <th style="padding:.5rem;text-align:left;">Estado</th>
                                        </tr>
                                    </thead>
                                    <tbody id="hist_tbody">
                                        <!-- Se llena dinámicamente -->
                                    </tbody>
                                </table>
                            </div>
                            <div id="hist_empty" style="margin-top:.5rem;color:#6b7280;font-size:.85rem;display:none;">
                                Este equipo no tiene historial de asignaciones.
                            </div>
                        </div>
                    </div>
                    <div class="modal-f">
                        <button type="button" class="btn" id="hist_btnClose2">Cerrar</button>
                    </div>
                </dialog>

                <!-- MODAL GESTIONAR ASIGNACIÓN -->
                <dialog id="gestionModal">
                    <div class="modal-h">
                        <h3 id="gest_title">Gestionar asignación</h3>
                        <button type="button" class="btn" id="gest_btnClose">✕</button>
                    </div>
                    <div class="modal-b" style="grid-template-columns:1fr;">
                        <div class="field">
                            <label>Equipo</label>
                            <div id="gest_equipoNombre">—</div>
                        </div>

                        <div id="gest_mensajes" style="margin-bottom:.6rem;display:none;">
                            <!-- Mensajes de éxito/error -->
                        </div>

                        <!-- Sección cuando NO está asignado -->
                        <div id="gest_noAsignado" style="display:none;">
                            <div class="field">
                                <label>Este equipo está disponible</label>
                                <p style="margin:.25rem 0;color:#6b7280;font-size:.85rem;">
                                    Selecciona un usuario para asignar el equipo:
                                </p>
                            </div>
                            <div class="field">
                                <label>Usuario</label>
                                <select id="gest_selectUsuario" style="width:100%;">
                                    <option value="">— Selecciona un usuario —</option>
                                </select>
                            </div>
                            <div class="field">
                                <button type="button" class="btn btn-primary" id="gest_btnAsignar">
                                    <i class="fa-solid fa-check"></i>&nbsp; Asignar equipo
                                </button>
                            </div>
                        </div>

                        <!-- Sección cuando SÍ está asignado -->
                        <div id="gest_siAsignado" style="display:none;">
                            <div class="field">
                                <label>Actualmente asignado a</label>
                                <div id="gest_usuarioActual" style="font-weight:600;color:#0f172a;">—</div>
                            </div>
                            <div class="field">
                                <label>Asignado desde</label>
                                <div id="gest_fechaAsignacion">—</div>
                            </div>
                            <div class="field" style="margin-top:.5rem;">
                                <button type="button" class="btn btn-danger" id="gest_btnDevolver">
                                    <i class="fa-solid fa-undo"></i>&nbsp; Devolver equipo
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="modal-f">
                        <button type="button" class="btn" id="gest_btnClose2">Cancelar</button>
                    </div>
                </dialog>

                <script type="text/javascript">
                    // Quitar overlay al cargar y animar tabla
                    window.addEventListener('DOMContentLoaded', () => {
                        document.body.classList.remove('loading');
                        const tw = document.querySelector('.table-wrapper');
                        if (tw) tw.classList.add('table-appear');
                    });
                </script>

                <script type="text/javascript">
                    // Navbar flotante (patrón estándar)
                    (function () {
                        const menuBox = document.querySelector('.box-menu');
                        const wrapper = document.querySelector('.box-menu .wrapper');
                        const burger = document.querySelector('.hamburguer');
                        const menuLinks = document.querySelectorAll('.box-menu .menu a');
                        const SAVE_KEY = 'boxMenuPos';
                        let isDragging = false;
                        let moved = false;
                        let startX = 0, startY = 0, offX = 0, offY = 0;

                        function getPoint(e) {
                            if (e.touches && e.touches[0]) return { x: e.touches[0].clientX, y: e.touches[0].clientY };
                            return { x: e.clientX, y: e.clientY };
                        }
                        function setPos(x, y) {
                            if (!menuBox) return;
                            const w = menuBox.offsetWidth || 60;
                            const h = menuBox.offsetHeight || 60;
                            const maxX = Math.max(0, (window.innerWidth || 0) - w);
                            const maxY = Math.max(0, (window.innerHeight || 0) - h);
                            const nx = Math.max(0, Math.min(x, maxX));
                            const ny = Math.max(0, Math.min(y, maxY));
                            menuBox.style.left = nx + 'px';
                            menuBox.style.top = ny + 'px';
                        }
                        function onDown(e) {
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
                            document.addEventListener('touchmove', onMove, { passive: false });
                            document.addEventListener('touchend', onUp);
                        }
                        function onMove(e) {
                            if (!isDragging || !menuBox) return;
                            const p = getPoint(e);
                            const nx = p.x - offX;
                            const ny = p.y - offY;
                            if (Math.abs(p.x - startX) > 4 || Math.abs(p.y - startY) > 4) moved = true;
                            setPos(nx, ny);
                            if (e.cancelable) e.preventDefault();
                        }
                        function onUp() {
                            if (!isDragging || !menuBox) return;
                            isDragging = false;
                            menuBox.classList.remove('dragging');
                            document.removeEventListener('mousemove', onMove);
                            document.removeEventListener('mouseup', onUp);
                            document.removeEventListener('touchmove', onMove);
                            document.removeEventListener('touchend', onUp);
                            const left = parseInt(menuBox.style.left || menuBox.offsetLeft || 0, 10);
                            const top = parseInt(menuBox.style.top || menuBox.offsetTop || 0, 10);
                            try { localStorage.setItem(SAVE_KEY, JSON.stringify({ left, top })); } catch (_) { }
                            setTimeout(() => { moved = false; }, 50);
                        }

                        if (menuBox) {
                            try {
                                const s = localStorage.getItem(SAVE_KEY);
                                if (s) {
                                    const p = JSON.parse(s);
                                    if (typeof p.left === 'number' && typeof p.top === 'number') setPos(p.left, p.top);
                                }
                            } catch (_) { }
                            menuBox.addEventListener('mousedown', onDown);
                            menuBox.addEventListener('touchstart', onDown, { passive: true });
                        }

                        if (wrapper) {
                            wrapper.addEventListener('click', function (ev) {
                                if (isDragging || moved) return;
                                if (menuBox) menuBox.classList.toggle('full-menu');
                                if (burger) burger.classList.toggle('active');
                            });
                        }

                        function updateArrow(currentLink) {
                            document.querySelectorAll('.box-menu .menu a .text i.fa-arrow-left')
                                .forEach(function (i) { i.remove(); });
                            if (!currentLink) return;
                            const txt = currentLink.querySelector('.text');
                            if (txt) {
                                const i = document.createElement('i');
                                i.className = 'fa-solid fa-arrow-left';
                                i.style.marginLeft = '6px';
                                txt.appendChild(i);
                            }
                        }

                        (function setActiveFromLocation() {
                            const current = document.querySelector('.box-menu .menu a.active');
                            if (current) updateArrow(current);
                        })();

                        menuLinks.forEach(function (link) {
                            link.addEventListener('click', function () {
                                menuLinks.forEach(function (l) { l.classList.remove('active'); });
                                link.classList.add('active');
                                updateArrow(link);
                            });
                        });
                    })();
                </script>

                <script>
                    const ctx = '${pageContext.request.contextPath}';
                </script>

                <script type="text/javascript">
                    // MODAL DE HISTORIAL
                    (function () {
                        const dlg = document.getElementById('historialModal');
                        const title = document.getElementById('hist_title');
                        const equipoNombre = document.getElementById('hist_equipoNombre');
                        const tbody = document.getElementById('hist_tbody');
                        const empty = document.getElementById('hist_empty');
                        const mensajes = document.getElementById('hist_mensajes');
                        const btnClose = document.getElementById('hist_btnClose');
                        const btnClose2 = document.getElementById('hist_btnClose2');

                        let currentEquipoId = null;

                        const showBox = (el) => { if (el) el.style.display = ''; };
                        const hideBox = (el) => { if (el) el.style.display = 'none'; };

                        function animateClose(d) {
                            if (!d) return;
                            d.classList.add('closing');
                            d.addEventListener('animationend', () => {
                                d.classList.remove('closing');
                                d.close();
                            }, { once: true });
                        }

                        function formatDate(val) {
                            if (!val) return '—';
                            try {
                                const d = new Date(val);
                                if (!isNaN(d)) return d.toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' });
                            } catch (_) { }
                            return String(val);
                        }

                        async function loadHistorial(idEquipo, nombreEquipo) {
                            currentEquipoId = idEquipo;
                            if (title) title.textContent = 'Historial de ' + (nombreEquipo || ('Equipo ' + idEquipo));
                            if (equipoNombre) equipoNombre.textContent = nombreEquipo || ('Equipo ID: ' + idEquipo);

                            hideBox(mensajes);
                            if (mensajes) mensajes.innerHTML = '';
                            if (tbody) tbody.innerHTML = '';
                            hideBox(empty);

                            try {
                                const url = ctx + '/asignaciones_equipos?accion=historial&idEquipo=' + encodeURIComponent(idEquipo);
                                const r = await fetch(url, { headers: { 'Accept': 'application/json' } });
                                if (!r.ok) {
                                    throw new Error('HTTP ' + r.status);
                                }
                                const data = await r.json();
                                const asignaciones = Array.isArray(data.asignaciones) ? data.asignaciones : [];

                                if (asignaciones.length === 0) {
                                    showBox(empty);
                                } else {
                                    asignaciones.forEach(a => {
                                        const tr = document.createElement('tr');

                                        const td1 = document.createElement('td');
                                        td1.style.padding = '.5rem';
                                        td1.textContent = a.usuarioNombre || '—';
                                        tr.appendChild(td1);

                                        const td2 = document.createElement('td');
                                        td2.style.padding = '.5rem';
                                        td2.textContent = a.asignadorNombre || '—';
                                        tr.appendChild(td2);

                                        const td3 = document.createElement('td');
                                        td3.style.padding = '.5rem';
                                        td3.textContent = formatDate(a.asignadoEn);
                                        tr.appendChild(td3);

                                        const td4 = document.createElement('td');
                                        td4.style.padding = '.5rem';
                                        td4.textContent = formatDate(a.devueltoEn);
                                        tr.appendChild(td4);

                                        const td5 = document.createElement('td');
                                        td5.style.padding = '.5rem';
                                        td5.textContent = a.devueltoEn ? 'Devuelta' : 'Activa';
                                        td5.style.fontWeight = a.devueltoEn ? 'normal' : '600';
                                        td5.style.color = a.devueltoEn ? '#6b7280' : '#0f172a';
                                        tr.appendChild(td5);

                                        tbody.appendChild(tr);
                                    });
                                }

                            } catch (err) {
                                console.error(err);
                                if (mensajes) {
                                    mensajes.innerHTML = '<div class="alert alert-err">No fue posible cargar el historial.</div>';
                                    showBox(mensajes);
                                }
                            }

                            if (typeof dlg?.showModal === 'function') {
                                dlg.showModal();
                            } else {
                                alert('Tu navegador no soporta el componente de diálogo.');
                            }
                        }

                        // Delegación: clic en Historial
                        document.addEventListener('click', (ev) => {
                            const btn = ev.target.closest('.btn-historial');
                            if (!btn) return;
                            ev.preventDefault();
                            const id = btn.getAttribute('data-id-equipo');
                            const nom = btn.getAttribute('data-equipo') || 'Equipo';
                            if (!id) return;
                            loadHistorial(id, nom);
                        });

                        if (btnClose) btnClose.onclick = () => animateClose(dlg);
                        if (btnClose2) btnClose2.onclick = () => animateClose(dlg);
                        dlg?.addEventListener('cancel', (e) => {
                            e.preventDefault();
                            animateClose(dlg);
                        });
                    })();
                </script>

                <script type="text/javascript">
                    // MODAL DE GESTIÓN
                    (function () {
                        const dlg = document.getElementById('gestionModal');
                        const title = document.getElementById('gest_title');
                        const equipoNombre = document.getElementById('gest_equipoNombre');
                        const mensajes = document.getElementById('gest_mensajes');
                        const noAsignado = document.getElementById('gest_noAsignado');
                        const siAsignado = document.getElementById('gest_siAsignado');
                        const selectUsuario = document.getElementById('gest_selectUsuario');
                        const usuarioActual = document.getElementById('gest_usuarioActual');
                        const fechaAsignacion = document.getElementById('gest_fechaAsignacion');
                        const btnAsignar = document.getElementById('gest_btnAsignar');
                        const btnDevolver = document.getElementById('gest_btnDevolver');
                        const btnClose = document.getElementById('gest_btnClose');
                        const btnClose2 = document.getElementById('gest_btnClose2');

                        let currentEquipoId = null;
                        let currentAsignacionId = null;

                        const showBox = (el) => { if (el) el.style.display = ''; };
                        const hideBox = (el) => { if (el) el.style.display = 'none'; };

                        function animateClose(d) {
                            if (!d) return;
                            d.classList.add('closing');
                            d.addEventListener('animationend', () => {
                                d.classList.remove('closing');
                                d.close();
                            }, { once: true });
                        }

                        function formatDate(val) {
                            if (!val) return '—';
                            try {
                                const d = new Date(val);
                                if (!isNaN(d)) return d.toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' });
                            } catch (_) { }
                            return String(val);
                        }

                        async function loadGestion(idEquipo, nombreEquipo) {
                            currentEquipoId = idEquipo;
                            currentAsignacionId = null;

                            if (title) title.textContent = 'Gestionar asignación';
                            if (equipoNombre) equipoNombre.textContent = nombreEquipo || ('Equipo ID: ' + idEquipo);

                            hideBox(mensajes);
                            if (mensajes) mensajes.innerHTML = '';
                            hideBox(noAsignado);
                            hideBox(siAsignado);
                            if (selectUsuario) selectUsuario.innerHTML = '<option value="">— Selecciona un usuario —</option>';

                            try {
                                const url = ctx + '/asignaciones_equipos?accion=asignacion_actual&idEquipo=' + encodeURIComponent(idEquipo);
                                const r = await fetch(url, { headers: { 'Accept': 'application/json' } });
                                if (!r.ok) {
                                    throw new Error('HTTP ' + r.status);
                                }
                                const data = await r.json();

                                const asignacion = data.asignacionActual;
                                const usuarios = Array.isArray(data.usuariosDisponibles) ? data.usuariosDisponibles : [];

                                if (asignacion) {
                                    // Equipo está asignado
                                    currentAsignacionId = asignacion.idAsignacion;
                                    if (usuarioActual) usuarioActual.textContent = asignacion.usuarioNombre || '—';
                                    if (fechaAsignacion) fechaAsignacion.textContent = formatDate(asignacion.asignadoEn);
                                    showBox(siAsignado);
                                } else {
                                    // Equipo disponible
                                    usuarios.forEach(u => {
                                        const opt = document.createElement('option');
                                        opt.value = u.idUsuario;
                                        opt.textContent = u.nombreCompleto;
                                        selectUsuario.appendChild(opt);
                                    });
                                    showBox(noAsignado);
                                }

                            } catch (err) {
                                console.error(err);
                                if (mensajes) {
                                    mensajes.innerHTML = '<div class="alert alert-err">No fue posible cargar los datos de asignación.</div>';
                                    showBox(mensajes);
                                }
                            }

                            if (typeof dlg?.showModal === 'function') {
                                dlg.showModal();
                            } else {
                                alert('Tu navegador no soporta el componente de diálogo.');
                            }
                        }

                        // Delegación: clic en Gestionar
                        document.addEventListener('click', (ev) => {
                            const btn = ev.target.closest('.btn-gestionar');
                            if (!btn) return;
                            ev.preventDefault();
                            const id = btn.getAttribute('data-id-equipo');
                            const nom = btn.getAttribute('data-equipo') || 'Equipo';
                            if (!id) return;
                            loadGestion(id, nom);
                        });

                        // Asignar equipo
                        btnAsignar?.addEventListener('click', async () => {
                            hideBox(mensajes);
                            if (mensajes) mensajes.innerHTML = '';

                            const idUsuario = selectUsuario.value;
                            if (!idUsuario) {
                                mensajes.innerHTML = '<div class="alert alert-err">Selecciona un usuario.</div>';
                                showBox(mensajes);
                                return;
                            }

                            try {
                                const payload = {
                                    idEquipo: parseInt(currentEquipoId),
                                    idUsuario: parseInt(idUsuario)
                                };

                                const r = await fetch(ctx + '/asignaciones_equipos?accion=asignar', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json',
                                        'Accept': 'application/json'
                                    },
                                    body: JSON.stringify(payload)
                                });

                                if (!r.ok) {
                                    throw new Error('HTTP ' + r.status);
                                }

                                const data = await r.json();

                                if (data.ok) {
                                    mensajes.innerHTML = '<div class="alert alert-ok">Equipo asignado correctamente.</div>';
                                    showBox(mensajes);
                                    // Recargar página después de 1 segundo
                                    setTimeout(() => {
                                        window.location.reload();
                                    }, 1000);
                                } else {
                                    mensajes.innerHTML = '<div class="alert alert-err">' + (data.error || 'Error al asignar') + '</div>';
                                    showBox(mensajes);
                                }

                            } catch (err) {
                                console.error(err);
                                mensajes.innerHTML = '<div class="alert alert-err">Ocurrió un error al asignar el equipo. Intenta de nuevo.</div>';
                                showBox(mensajes);
                            }
                        });

                        // Devolver equipo
                        btnDevolver?.addEventListener('click', async () => {
                            if (!confirm('¿Confirmas que deseas devolver este equipo?')) {
                                return;
                            }

                            hideBox(mensajes);
                            if (mensajes) mensajes.innerHTML = '';

                            try {
                                const payload = {
                                    idEquipo: parseInt(currentEquipoId)
                                };

                                const r = await fetch(ctx + '/asignaciones_equipos?accion=devolver', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json',
                                        'Accept': 'application/json'
                                    },
                                    body: JSON.stringify(payload)
                                });

                                if (!r.ok) {
                                    throw new Error('HTTP ' + r.status);
                                }

                                const data = await r.json();

                                if (data.ok) {
                                    mensajes.innerHTML = '<div class="alert alert-ok">Equipo devuelto correctamente.</div>';
                                    showBox(mensajes);
                                    // Recargar página después de 1 segundo
                                    setTimeout(() => {
                                        window.location.reload();
                                    }, 1000);
                                } else {
                                    mensajes.innerHTML = '<div class="alert alert-err">' + (data.error || 'Error al devolver') + '</div>';
                                    showBox(mensajes);
                                }

                            } catch (err) {
                                console.error(err);
                                mensajes.innerHTML = '<div class="alert alert-err">Ocurrió un error al devolver el equipo. Intenta de nuevo.</div>';
                                showBox(mensajes);
                            }
                        });

                        if (btnClose) btnClose.onclick = () => animateClose(dlg);
                        if (btnClose2) btnClose2.onclick = () => animateClose(dlg);
                        dlg?.addEventListener('cancel', (e) => {
                            e.preventDefault();
                            animateClose(dlg);
                        });
                    })();
                </script>

            </body>

            </html>