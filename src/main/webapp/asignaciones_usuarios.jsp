<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Asignaciones en usuarios</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css"
          integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <style>
        /* Que el dialog ocupe casi toda la pantalla */
        #gestionAsignacionesModal {
            width: 96vw;
            max-width: 1100px;
            max-height: calc(100vh - 3rem);
            padding: 0;
            border-radius: .75rem;
            border: 1px solid #1d4ed8;
        }

        /* SOLO cuando esté abierto se muestra como flex */
        #gestionAsignacionesModal[open] {
            display: flex;
            flex-direction: column;
        }

        /* Cabecera y pie fijos dentro del modal */
        #gestionAsignacionesModal .modal-h,
        #gestionAsignacionesModal .modal-f {
            flex-shrink: 0;
        }

        /* Cuerpo del modal: ocupa el espacio restante y scrollea si hace falta */
        #gestionAsignacionesModal .modal-b {
            padding: 1rem 1.5rem 1rem 1.5rem;
            flex: 1 1 auto;
            max-height: none;
            overflow: auto;
        }

        /* La grid interna no fuerza más tamaño del necesario */
        #gestionAsignacionesModal .ga-grid {
            min-height: 0;
        }

        /* El cuerpo del modal ocupa el alto disponible, pero no deja que las
   tablas se "salgan" por arriba de su wrapper */
        #gestionAsignacionesModal .modal-b {
            padding: 1rem 1.5rem 1rem 1.5rem;
            flex: 1 1 auto;
            overflow: hidden; /* el scroll principal se hará en cada tabla */
        }

        /* Cada tabla tiene su propio scroll vertical.
           Así, las filas se recortan cuando llegan al encabezado. */
        #gestionAsignacionesModal .ga-table-wrapper {
            max-height: 340px;        /* ajusta el alto al gusto */
            overflow-y: auto;
            overflow-x: hidden;
        }

        /* Encabezados pegados a la parte superior del wrapper de la tabla */
        #gestionAsignacionesModal .ga-table-wrapper thead th {
            position: sticky;
            top: 0;
            z-index: 2;
        }


        /* Un poco más pequeño el texto de las tablas del modal */
        #gestionAsignacionesModal table {
            font-size: .85rem;
        }

        /* Backdrop un poco oscurecido para que se note el modal */
        #gestionAsignacionesModal::backdrop {
            background-color: rgba(15, 23, 42, .45);
        }
    </style>


</head>
<body>
<div id="loadingOverlay"><div class="loader" aria-label="Cargando"></div></div>

<!-- MENU FLOANTE -->
<div class="box-menu">
    <div class="wrapper">
        <div class="hamburguer">
            <span></span><span></span><span></span>
            <span></span><span></span><span></span><span></span>
        </div>
    </div>
    <div class="menu">
        <a href="equipos.jsp"><span class="icon fa-solid fa-desktop"></span><span class="text">Equipos</span></a>
        <a href="sims"><span class="icon fa-solid fa-sim-card"></span><span class="text">Sims</span></a>
        <a href="consumibles"><span class="icon fa-solid fa-boxes-stacked"></span><span class="text">Consumibles</span></a>
        <a href="asignaciones.jsp" class="active">
            <span class="icon fa-solid fa-arrow-right-arrow-left"></span><span class="text">Asignaciones</span>
        </a>
        <a href="catalogos.jsp"><span class="icon fa-solid fa-folder-open"></span><span class="text">Catalogos</span></a>
        <a href="usuarios.jsp"><span class="icon fa-solid fa-users"></span><span class="text">Usuarios</span></a>
        <a href="dashboard.jsp"><span class="icon fa-solid fa-chart-line"></span><span class="text">Dashboard</span></a>
    </div>
</div>

<main class="container">
    <div class="card card-compact">
        <div style="display:flex;justify-content:space-between;align-items:center;gap:.75rem;">
            <div>
                <h1>Asignaciones en usuarios</h1>
                <p style="margin:0;color:#6b7280;font-size:.9rem;">
                    Lista de empleados y número de equipos asignados. Usa el botón
                    <strong>Gestionar asignaciones</strong> para ver y modificar el detalle de cada usuario.
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
            <c:remove var="flashOk" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-err">
                    ${sessionScope.flashError}
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <!-- Tabla de usuarios -->
        <div class="table-wrapper" style="margin-top:1rem;">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre completo</th>
                    <th>Centro</th>
                    <th>Puesto</th>
                    <th>Líder</th>
                    <th style="text-align:center;"># Asignaciones</th>
                    <th style="width:180px;text-align:center;">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${listaUsuarios}">
                    <tr>
                        <td>${u.idUsuario}</td>
                        <td>
                                ${u.nombre} ${u.apellidoPaterno} ${u.apellidoMaterno}
                        </td>
                        <td><c:out value="${empty u.nombreCentro ? '—' : u.nombreCentro}"/></td>
                        <td><c:out value="${empty u.nombrePuesto ? '—' : u.nombrePuesto}"/></td>
                        <td><c:out value="${empty u.nombreLider ? '—' : u.nombreLider}"/></td>
                        <td style="text-align:center;">
                            <c:out value="${conteoAsignaciones[u.idUsuario]}" default="0"/>
                        </td>
                        <td style="text-align:center;">
                            <button type="button"
                                    class="btn btn-primary btn-gestion-asignaciones"
                                    data-id-usuario="${u.idUsuario}"
                                    data-nombre="${u.nombre} ${u.apellidoPaterno} ${u.apellidoMaterno}">
                                Gestionar asignaciones
                            </button>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty listaUsuarios}">
                    <tr>
                        <td colspan="7" style="text-align:center;color:#6b7280;padding:.75rem;">
                            No hay usuarios activos para mostrar.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>

<!-- MODAL GESTIÓN ASIGNACIONES POR USUARIO -->
<dialog id="gestionAsignacionesModal">
    <div class="modal-h">
        <h3 id="ga_title">Asignaciones de usuario</h3>
        <button type="button" class="btn" id="ga_btnClose">✕</button>
    </div>
    <div class="modal-b" style="grid-template-columns:1fr;">
        <div class="field">
            <label>Usuario</label>
            <div id="ga_usuarioNombre">—</div>
        </div>

        <div class="field" style="grid-column:1/-1;">
            <label>Equipos</label>

            <!-- Mensajes (éxito / error al guardar) -->
            <div id="ga_mensajes" style="margin-bottom:.6rem;display:none;">
                <!-- Se llena dinámicamente -->
            </div>

            <!-- Grid con las dos tablas -->
            <div class="ga-grid"
                 style="display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:.75rem;align-items:flex-start;">
                <!-- Columna izquierda: equipos asignados -->
                <section class="ga-col">
                    <h4 style="margin:0 0 .4rem;font-size:.9rem;color:#0f172a;">
                        Equipos asignados al usuario
                    </h4>
                    <div class="table-wrapper ga-table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Tipo</th>
                                <th>Núm. Serie</th>
                                <th>Ubicación</th>
                                <th style="width:110px;">Acción</th>
                            </tr>
                            </thead>
                            <tbody id="ga_tbodyAsignados">
                            <!-- Filas asignadas se pintan aquí -->
                            </tbody>
                        </table>
                    </div>
                    <div id="ga_emptyAsignados"
                         style="margin-top:.25rem;font-size:.8rem;color:#6b7280;display:none;">
                        El usuario no tiene equipos asignados.
                    </div>
                </section>

                <!-- Columna derecha: equipos disponibles -->
                <section class="ga-col">
                    <h4 style="margin:0 0 .4rem;font-size:.9rem;color:#0f172a;">
                        Equipos disponibles
                    </h4>
                    <div class="table-wrapper ga-table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Tipo</th>
                                <th>Núm. Serie</th>
                                <th>Ubicación</th>
                                <th style="width:110px;">Acción</th>
                            </tr>
                            </thead>
                            <tbody id="ga_tbodyDisponibles">
                            <!-- Filas disponibles se pintan aquí -->
                            </tbody>
                        </table>
                    </div>
                    <div id="ga_emptyDisponibles"
                         style="margin-top:.25rem;font-size:.8rem;color:#6b7280;display:none;">
                        No hay equipos disponibles por el momento.
                    </div>
                </section>
            </div>
        </div>
    </div>
    <div class="modal-f">
        <button type="button" class="btn" id="ga_btnClose2">Cancelar</button>
        <button type="button" class="btn btn-primary" id="ga_btnGuardar">
            Guardar cambios
        </button>
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
    // Navbar flotante (igual patrón que en equipos.jsp, pero simplificado)
    (function(){
        const menuBox   = document.querySelector('.box-menu');
        const wrapper   = document.querySelector('.box-menu .wrapper');
        const burger    = document.querySelector('.hamburguer');
        const menuLinks = document.querySelectorAll('.box-menu .menu a');
        const SAVE_KEY  = 'boxMenuPos';
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
            const current = document.querySelector('.box-menu .menu a.active');
            if (current) updateArrow(current);
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


<script>
    const ctx = '${pageContext.request.contextPath}';
</script>

<script type="text/javascript">
    // Modal: Gestionar asignaciones (dos tablas + guardar cambios)
    (function(){
        const dlg          = document.getElementById('gestionAsignacionesModal');
        const title        = document.getElementById('ga_title');
        const labelNombre  = document.getElementById('ga_usuarioNombre');
        const tbodyAsig    = document.getElementById('ga_tbodyAsignados');
        const tbodyDisp    = document.getElementById('ga_tbodyDisponibles');
        const emptyAsig    = document.getElementById('ga_emptyAsignados');
        const emptyDisp    = document.getElementById('ga_emptyDisponibles');
        const boxMensajes  = document.getElementById('ga_mensajes');
        const btnClose     = document.getElementById('ga_btnClose');
        const btnClose2    = document.getElementById('ga_btnClose2');
        const btnGuardar   = document.getElementById('ga_btnGuardar');

        let gaUsuarioId = null;

        // Sets para saber qué estaba asignado al inicio y qué hay ahora
        let originalAssignedIds = new Set();
        let currentAssignedIds  = new Set();

        // Índice para obtener info de un equipo por id (para mensajes bonitos)
        const equiposIndex = {};

        const showBox = (el) => { if (el) el.style.display = ''; };
        const hideBox = (el) => { if (el) el.style.display = 'none'; };

        function animateClose(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=> {
                d.classList.remove('closing');
                d.close();
            }, { once:true });
        }

        function resetModalState(){
            gaUsuarioId = null;
            originalAssignedIds = new Set();
            currentAssignedIds  = new Set();
            for (const k in equiposIndex) {
                delete equiposIndex[k];
            }
            if (tbodyAsig) tbodyAsig.innerHTML = '';
            if (tbodyDisp) tbodyDisp.innerHTML = '';
            hideBox(boxMensajes);
            if (boxMensajes) boxMensajes.innerHTML = '';
            hideBox(emptyAsig);
            hideBox(emptyDisp);
        }

        // Helper para crear una fila de tabla
        function createRowEquipo(equipo, tipo){
            // tipo: 'asignado' o 'disponible'
            const tr = document.createElement('tr');
            tr.dataset.equipoId = String(equipo.idEquipo);

            const tdTipo  = document.createElement('td');
            const tdSerie = document.createElement('td');
            const tdUbic  = document.createElement('td');
            const tdAcc   = document.createElement('td');

            tdTipo.textContent  = equipo.tipoNombre      || '—';
            tdSerie.textContent = equipo.numeroSerie     || '—';
            tdUbic.textContent  = equipo.ubicacionNombre || '—';

            tdAcc.style.textAlign = 'center';

            const btn = document.createElement('button');
            btn.type = 'button';

            if (tipo === 'asignado') {
                btn.className = 'btn btn-secondary ga-btn-desasignar';
                btn.textContent = 'Desasignar';
            } else {
                btn.className = 'btn btn-primary ga-btn-asignar';
                btn.textContent = 'Asignar';
            }

            tdAcc.appendChild(btn);

            tr.appendChild(tdTipo);
            tr.appendChild(tdSerie);
            tr.appendChild(tdUbic);
            tr.appendChild(tdAcc);

            return tr;
        }

        function updateEmptyLabels(){
            if (emptyAsig) {
                const has = tbodyAsig && tbodyAsig.children.length > 0;
                emptyAsig.style.display = has ? 'none' : 'block';
            }
            if (emptyDisp) {
                const has = tbodyDisp && tbodyDisp.children.length > 0;
                emptyDisp.style.display = has ? 'none' : 'block';
            }
        }

        // Cargar datos del usuario (equipos asignados y disponibles)
        async function loadUsuarioEquipos(idUsuario, nombre){
            resetModalState();
            gaUsuarioId = idUsuario;

            if (title)       title.textContent = 'Asignaciones de ' + (nombre || ('Usuario ' + idUsuario));
            if (labelNombre) labelNombre.textContent = (nombre || 'Usuario') + ' (ID: ' + idUsuario + ')';

            try{
                // Backend a implementar: debe devolver JSON con {asignados:[], disponibles:[]}
                const url = ctx + '/asignaciones_usuarios?accion=datos&idUsuario='
                    + encodeURIComponent(idUsuario);
                const r = await fetch(url, { headers: { 'Accept':'application/json' } });
                if (!r.ok) {
                    throw new Error('HTTP ' + r.status);
                }
                const data = await r.json();

                const asignados   = Array.isArray(data.asignados)   ? data.asignados   : [];
                const disponibles = Array.isArray(data.disponibles) ? data.disponibles : [];

                // Rellenar índice y sets
                asignados.forEach(eq => {
                    equiposIndex[eq.idEquipo] = eq;
                    originalAssignedIds.add(eq.idEquipo);
                    currentAssignedIds.add(eq.idEquipo);
                });
                disponibles.forEach(eq => {
                    equiposIndex[eq.idEquipo] = eq;
                });

                // Pintar tablas
                if (tbodyAsig) {
                    tbodyAsig.innerHTML = '';
                    asignados.forEach(eq => {
                        const row = createRowEquipo(eq, 'asignado');
                        tbodyAsig.appendChild(row);
                    });
                }
                if (tbodyDisp) {
                    tbodyDisp.innerHTML = '';
                    disponibles.forEach(eq => {
                        const row = createRowEquipo(eq, 'disponible');
                        tbodyDisp.appendChild(row);
                    });
                }

                updateEmptyLabels();

            } catch(err){
                console.error(err);
                if (boxMensajes) {
                    boxMensajes.innerHTML =
                        '<div class="alert alert-err">No fue posible cargar las asignaciones del usuario.</div>';
                    showBox(boxMensajes);
                }
            }

            if (typeof dlg?.showModal === 'function') {
                dlg.showModal();
            } else {
                alert('Tu navegador no soporta el componente de diálogo.');
            }
        }

        // Delegación: clic en Gestionar asignaciones de la tabla principal
        document.addEventListener('click', (ev) => {
            const btn = ev.target.closest('.btn-gestion-asignaciones');
            if (!btn) return;
            ev.preventDefault();
            const id  = btn.getAttribute('data-id-usuario');
            const nom = btn.getAttribute('data-nombre') || 'Usuario';
            if (!id) return;
            loadUsuarioEquipos(id, nom);
        });

        // Delegación dentro del modal para mover filas entre tablas
        dlg?.addEventListener('click', (ev) => {
            const btnAsig = ev.target.closest('.ga-btn-asignar');
            const btnDes  = ev.target.closest('.ga-btn-desasignar');

            if (!btnAsig && !btnDes) return;

            const tr = ev.target.closest('tr');
            if (!tr) return;
            const idEq = parseInt(tr.dataset.equipoId || '0', 10);
            if (!idEq) return;

            if (btnDes && tbodyDisp) {
                // Desasignar: mover de asignados -> disponibles
                currentAssignedIds.delete(idEq);
                // Botón cambia a "Asignar"
                const newRow = createRowEquipo(equiposIndex[idEq], 'disponible');
                tbodyDisp.appendChild(newRow);
                tr.remove();
                updateEmptyLabels();
            } else if (btnAsig && tbodyAsig) {
                // Asignar: mover de disponibles -> asignados
                currentAssignedIds.add(idEq);
                const newRow = createRowEquipo(equiposIndex[idEq], 'asignado');
                tbodyAsig.appendChild(newRow);
                tr.remove();
                updateEmptyLabels();
            }
        });

        // Guardar cambios: calcular diferencias y mandar al backend
        btnGuardar?.addEventListener('click', async () => {
            hideBox(boxMensajes);
            if (boxMensajes) boxMensajes.innerHTML = '';

            if (!gaUsuarioId) {
                if (boxMensajes) {
                    boxMensajes.innerHTML =
                        '<div class="alert alert-err">No se encontró el usuario seleccionado.</div>';
                    showBox(boxMensajes);
                }
                return;
            }

            // Calcular cambios: qué se debe asignar y qué desasignar
            const asignar   = [];
            const desasignar = [];

            originalAssignedIds.forEach(id => {
                if (!currentAssignedIds.has(id)) {
                    desasignar.push(id);
                }
            });
            currentAssignedIds.forEach(id => {
                if (!originalAssignedIds.has(id)) {
                    asignar.push(id);
                }
            });

            if (asignar.length === 0 && desasignar.length === 0) {
                if (boxMensajes) {
                    boxMensajes.innerHTML =
                        '<div class="alert alert-ok">No hay cambios por guardar.</div>';
                    showBox(boxMensajes);
                }
                return;
            }

            // Petición al backend para verificar estado actual y aplicar cambios
            try {
                const payload = {
                    idUsuario: gaUsuarioId,
                    asignar: asignar,
                    desasignar: desasignar
                };

                // Backend a implementar: debe verificar estados y devolver per-equipo si se pudo o no
                const r = await fetch(ctx + '/asignaciones_usuarios?accion=guardar', {
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
                const resultados = Array.isArray(data.resultados) ? data.resultados : [];

                const okList = [];
                const errList = [];

                resultados.forEach(res => {
                    const idEq = res.idEquipo;
                    const info = equiposIndex[idEq] || {};
                    const etiqueta = (info.tipoNombre || 'Equipo') + ' - ' + (info.numeroSerie || ('ID ' + idEq));
                    if (res.ok) {
                        okList.push(etiqueta);
                        // Sincronizar estado original con el actual para futuras ediciones
                        if (currentAssignedIds.has(idEq)) {
                            originalAssignedIds.add(idEq);
                        } else {
                            originalAssignedIds.delete(idEq);
                        }
                    } else {
                        errList.push({
                            etiqueta,
                            mensaje: res.mensaje || 'Error desconocido.'
                        });
                    }
                });

                // Pintar mensajes
                let html = '';
                if (okList.length > 0) {
                    html += '<div class="alert alert-ok"><strong>Cambios aplicados correctamente en:</strong><ul style="margin:.35rem 0 0 .9rem;font-size:.85rem;">';
                    okList.forEach(t => {
                        html += '<li>' + t + '</li>';
                    });
                    html += '</ul></div>';
                }

                if (errList.length > 0 || data.errorGeneral) {
                    html += '<div class="alert alert-err"><strong>Hubo problemas con algunos equipos:</strong><ul style="margin:.35rem 0 0 .9rem;font-size:.85rem;">';
                    errList.forEach(e => {
                        html += '<li>' + e.etiqueta + ' — ' + e.mensaje + '</li>';
                    });
                    if (data.errorGeneral) {
                        html += '<li>' + data.errorGeneral + '</li>';
                    }
                    html += '</ul></div>';
                }

                if (!html) {
                    html = '<div class="alert alert-ok">Operación completada.</div>';
                }

                if (boxMensajes) {
                    boxMensajes.innerHTML = html;
                    showBox(boxMensajes);
                }

            } catch(err) {
                console.error(err);
                if (boxMensajes) {
                    boxMensajes.innerHTML =
                        '<div class="alert alert-err">Ocurrió un error al guardar los cambios. Intenta de nuevo.</div>';
                    showBox(boxMensajes);
                }
            }
        });

        // Cierre del modal
        if (btnClose)  btnClose.onclick  = () => animateClose(dlg);
        if (btnClose2) btnClose2.onclick = () => animateClose(dlg);
        dlg?.addEventListener('cancel', (e) => {
            e.preventDefault();
            animateClose(dlg);
        });
    })();
</script>


</body>
</html>
