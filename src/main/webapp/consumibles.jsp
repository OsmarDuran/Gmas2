<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Consumibles</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css" integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div id="loadingOverlay"><div class="loader" aria-label="Cargando"></div></div>

<!-- Menú flotante -->
<div class="box-menu">
    <div class="wrapper">
        <div class="hamburguer"><span></span><span></span><span></span><span></span></div>
    </div>
    <div class="menu">
        <a href="equipos"><span class="icon fa-solid fa-desktop"></span><span class="text">Equipos</span></a>
        <a href="sims"><span class="icon fa-solid fa-sim-card"></span><span class="text">Sims</span></a>
        <a href="consumibles" class="active"><span class="icon fa-solid fa-boxes-stacked"></span><span class="text">Consumibles</span></a>
        <a href="asignaciones.jsp"><span class="icon fa-solid fa-arrow-right-arrow-left"></span><span class="text">Asignaciones</span></a>
        <a href="catalogos.jsp"><span class="icon fa-solid fa-folder-open"></span><span class="text">Catalogos</span></a>
        <a href="usuarios.jsp"><span class="icon fa-solid fa-users"></span><span class="text">Usuarios</span></a>
        <a href="dashboard.jsp"><span class="icon fa-solid fa-chart-line"></span><span class="text">Dashboard</span></a>
    </div>
</div>

<main class="container">
    <div class="card card-compact">
        <h1>Consumibles</h1>

        <c:if test="${not empty sessionScope.flashOk}">
            <div class="alert alert-ok">${sessionScope.flashOk}</div>
            <c:remove var="flashOk" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-err">${sessionScope.flashError}</div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <!-- Filtros -->
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/consumibles">
            <div class="filters-row">
                <input type="search" name="q" value="${q}" placeholder="Buscar (serie/marca/modelo)..." />
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
                <select name="idColor">
                    <option value="">Color</option>
                    <c:forEach var="c0" items="${colores}">
                        <option value="${c0.idColor}" <c:if test="${idColor==c0.idColor}">selected</c:if>>${c0.nombre}</option>
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
            <a class="btn btn-primary add-btn" href="${pageContext.request.contextPath}/consumibles-nuevo">Agregar Consumible</a>
        </form>

        <!-- Tabla -->
        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Núm. Serie</th>
                    <th>Marca</th>
                    <th>Modelo</th>
                    <th>Color</th>
                    <th>Estatus</th>
                    <th style="width:170px">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cns" items="${consumibles}">
                    <tr>
                        <td><c:out value="${empty cns.numeroSerie ? '—' : cns.numeroSerie}"/></td>
                        <td><c:out value="${empty cns.marcaNombre ? '—' : cns.marcaNombre}"/></td>
                        <td><c:out value="${empty cns.modeloNombre ? '—' : cns.modeloNombre}"/></td>
                        <td><c:out value="${empty cns.colorNombre ? '—' : cns.colorNombre}"/></td>
                        <td><c:out value="${empty cns.estatusNombre ? '—' : cns.estatusNombre}"/></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-detalles" data-id="${cns.idEquipo}">Detalles</button>
                            <button type="button" class="btn btn-primary btn-editar" data-id="${cns.idEquipo}">Editar</button>
                            <form style="display:inline" method="post" action="${pageContext.request.contextPath}/consumibles">
                                <input type="hidden" name="action" value="delete"/>
                                <input type="hidden" name="idEquipo" value="${cns.idEquipo}"/>
                                <button class="btn btn-danger" onclick="return confirm('¿Eliminar este consumible?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <script>
            // Colorear filas por estatus (igual que SIMs)
            (function(){
                const map = {
                    'disponible': 'status-disponible',
                    'asignado': 'status-asignado',
                    'en reparacion': 'status-reparacion',
                    'reparacion': 'status-reparacion',
                    'reparación': 'status-reparacion',
                    'desuso': 'status-desuso',
                    'baja': 'status-desuso',
                    'eliminado': 'status-desuso'
                };
                function norm(s){
                    if (!s) return '';
                    try {
                        return s.toString().trim().toLowerCase()
                            .normalize('NFD').replace(/[\u0300-\u036f]/g,'');
                    } catch(_) {
                        return s.toString().trim().toLowerCase();
                    }
                }
                function applyRowColors(){
                    const rows = document.querySelectorAll('.table-wrapper table tbody tr');
                    rows.forEach(tr => {
                        const tds = tr.querySelectorAll('td');
                        if (tds.length >= 5) {
                            const statusText = tds[4].textContent || '';
                            const cls = map[norm(statusText)];
                            if (cls) tr.classList.add(cls);
                        }
                    });
                }
                if (document.readyState === 'loading')
                    document.addEventListener('DOMContentLoaded', applyRowColors);
                else
                    applyRowColors();
            })();
        </script>
    </div>

    <!-- Paginación -->
    <c:if test="${totalPages gt 1}">
        <div class="pagination" style="display:flex;gap:.4rem;flex-wrap:wrap;align-items:center;margin:.75rem 0;">
            <c:if test="${page gt 1}">
                <c:url var="uPrev" value="/consumibles">
                    <c:param name="page" value="${page-1}"/>
                    <c:if test="${not empty q}"><c:param name="q" value="${q}"/></c:if>
                    <c:if test="${not empty idMarca}"><c:param name="idMarca" value="${idMarca}"/></c:if>
                    <c:if test="${not empty idModelo}"><c:param name="idModelo" value="${idModelo}"/></c:if>
                    <c:if test="${not empty idColor}"><c:param name="idColor" value="${idColor}"/></c:if>
                    <c:if test="${not empty idUbicacion}"><c:param name="idUbicacion" value="${idUbicacion}"/></c:if>
                    <c:if test="${not empty idEstatus}"><c:param name="idEstatus" value="${idEstatus}"/></c:if>
                </c:url>
                <a class="btn" href="${pageContext.request.contextPath}${uPrev}">«</a>
            </c:if>

            <c:url var="u1" value="/consumibles">
                <c:param name="page" value="1"/>
                <c:if test="${not empty q}"><c:param name="q" value="${q}"/></c:if>
                <c:if test="${not empty idMarca}"><c:param name="idMarca" value="${idMarca}"/></c:if>
                <c:if test="${not empty idModelo}"><c:param name="idModelo" value="${idModelo}"/></c:if>
                <c:if test="${not empty idColor}"><c:param name="idColor" value="${idColor}"/></c:if>
                <c:if test="${not empty idUbicacion}"><c:param name="idUbicacion" value="${idUbicacion}"/></c:if>
                <c:if test="${not empty idEstatus}"><c:param name="idEstatus" value="${idEstatus}"/></c:if>
            </c:url>
            <a class="btn ${page==1 ? 'btn-secondary' : ''}" href="${pageContext.request.contextPath}${u1}">1</a>

            <c:if test="${page gt 3}">
                <span style="padding:0 .25rem;color:#64748b">…</span>
            </c:if>

            <c:set var="startWin" value="${page-1 lt 2 ? 2 : page-1}"/>
            <c:set var="endWin"   value="${page+1 gt totalPages-1 ? totalPages-1 : page+1}"/>
            <c:forEach var="i" begin="${startWin}" end="${endWin}">
                <c:url var="uI" value="/consumibles">
                    <c:param name="page" value="${i}"/>
                    <c:if test="${not empty q}"><c:param name="q" value="${q}"/></c:if>
                    <c:if test="${not empty idMarca}"><c:param name="idMarca" value="${idMarca}"/></c:if>
                    <c:if test="${not empty idModelo}"><c:param name="idModelo" value="${idModelo}"/></c:if>
                    <c:if test="${not empty idColor}"><c:param name="idColor" value="${idColor}"/></c:if>
                    <c:if test="${not empty idUbicacion}"><c:param name="idUbicacion" value="${idUbicacion}"/></c:if>
                    <c:if test="${not empty idEstatus}"><c:param name="idEstatus" value="${idEstatus}"/></c:if>
                </c:url>
                <a class="btn ${page==i ? 'btn-secondary' : ''}" href="${pageContext.request.contextPath}${uI}">${i}</a>
            </c:forEach>

            <c:if test="${page lt totalPages-2}">
                <span style="padding:0 .25rem;color:#64748b">…</span>
            </c:if>

            <c:if test="${totalPages gt 1}">
                <c:url var="uLast" value="/consumibles">
                    <c:param name="page" value="${totalPages}"/>
                    <c:if test="${not empty q}"><c:param name="q" value="${q}"/></c:if>
                    <c:if test="${not empty idMarca}"><c:param name="idMarca" value="${idMarca}"/></c:if>
                    <c:if test="${not empty idModelo}"><c:param name="idModelo" value="${idModelo}"/></c:if>
                    <c:if test="${not empty idColor}"><c:param name="idColor" value="${idColor}"/></c:if>
                    <c:if test="${not empty idUbicacion}"><c:param name="idUbicacion" value="${idUbicacion}"/></c:if>
                    <c:if test="${not empty idEstatus}"><c:param name="idEstatus" value="${idEstatus}"/></c:if>
                </c:url>
                <a class="btn ${page==totalPages ? 'btn-secondary' : ''}" href="${pageContext.request.contextPath}${uLast}">${totalPages}</a>
            </c:if>

            <c:if test="${page lt totalPages}">
                <c:url var="uNext" value="/consumibles">
                    <c:param name="page" value="${page+1}"/>
                    <c:if test="${not empty q}"><c:param name="q" value="${q}"/></c:if>
                    <c:if test="${not empty idMarca}"><c:param name="idMarca" value="${idMarca}"/></c:if>
                    <c:if test="${not empty idModelo}"><c:param name="idModelo" value="${idModelo}"/></c:if>
                    <c:if test="${not empty idColor}"><c:param name="idColor" value="${idColor}"/></c:if>
                    <c:if test="${not empty idUbicacion}"><c:param name="idUbicacion" value="${idUbicacion}"/></c:if>
                    <c:if test="${not empty idEstatus}"><c:param name="idEstatus" value="${idEstatus}"/></c:if>
                </c:url>
                <a class="btn" href="${pageContext.request.contextPath}${uNext}">»</a>
            </c:if>
        </div>
    </c:if>
</main>

<!-- MODAL EDICIÓN/ALTA -->
<dialog id="editModal">
    <form id="editForm" method="post" action="${pageContext.request.contextPath}/consumibles">
        <input type="hidden" name="action" id="formAction" value="save"/>
        <input type="hidden" name="idEquipo" id="idEquipo"/>
        <input type="hidden" name="tipoNombre" id="tipoNombre" value="CONSUMIBLE"/>
        <input type="hidden" name="returnQuery" id="returnQuery"/>

        <div class="modal-h">
            <h3 id="modalTitle">Editar consumible</h3>
            <button type="button" class="btn" id="btnClose">✕</button>
        </div>
        <div class="modal-b">
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
                <label>Número de serie</label>
                <input type="text" name="numeroSerie" id="numeroSerie"/>
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
                        <option value="${s.idEstatus}">${s.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Color</label>
                <select name="idColor" id="idColor">
                    <option value="">—</option>
                    <c:forEach var="c0" items="${colores}">
                        <option value="${c0.idColor}">${c0.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field" style="grid-column:1/-1">
                <label>Notas</label>
                <textarea name="notas" id="notas" rows="2"></textarea>
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
        <h3>Detalles de consumible</h3>
        <button type="button" class="btn" id="btnCloseDetails">✕</button>
    </div>
    <div class="modal-b" id="detailsBody" style="grid-template-columns:repeat(2,minmax(0,1fr))">
        <div class="field"><label>Número de serie</label><div id="d_numeroSerie">—</div></div>
        <div class="field"><label>Marca</label><div id="d_marca">—</div></div>
        <div class="field"><label>Modelo</label><div id="d_modelo">—</div></div>
        <div class="field"><label>Ubicación</label><div id="d_ubicacion">—</div></div>
        <div class="field"><label>Estatus</label><div id="d_estatus">—</div></div>
        <div class="field"><label>Color</label><div id="d_color">—</div></div>
        <div class="field" style="grid-column:1/-1"><label>Notas</label><div id="d_notas">—</div></div>
    </div>
    <div class="modal-f">
        <button type="button" class="btn" id="btnCloseDetails2">Cerrar</button>
    </div>
</dialog>

<script>
    const ctx = '${pageContext.request.contextPath}';

    // Catálogos para dependencias en cliente
    window.ALL_MODELOS = [
        <c:forEach var="m" items="${modelos}" varStatus="s">
        { idModelo: ${m.idModelo}, idMarca: ${m.idMarca}, nombre: '<c:out value="${m.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
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
        <c:forEach var="c0" items="${colores}" varStatus="sc">
        { idColor: ${c0.idColor}, nombre: '<c:out value="${c0.nombre}"/>' }<c:if test="${!sc.last}">,</c:if>
        </c:forEach>
    ];
</script>

<!-- Filtros + overlay + paginación (idéntico a SIMs, con idColor incluido) -->
<script>
    (() => {
        const form = document.querySelector('form.toolbar');
        if (!form) return;
        const inputQ   = form.querySelector('input[name="q"]');
        const selMarca = form.querySelector('select[name="idMarca"]');
        const selModelo= form.querySelector('select[name="idModelo"]');
        const selColor = form.querySelector('select[name="idColor"]');
        const selUbic  = form.querySelector('select[name="idUbicacion"]');
        const selEst   = form.querySelector('select[name="idEstatus"]');
        const pageInp  = form.querySelector('#pageInput');

        function showLoading(){ document.body.classList.add('loading'); }
        function hideLoading(){ document.body.classList.remove('loading'); }
        let lastSubmittedQ = (inputQ ? (inputQ.value || '').trim() : '');

        function submitFilters(){
            if (pageInp) pageInp.value = '1';
            if (inputQ) lastSubmittedQ = (inputQ.value || '').trim();
            showLoading();
            setTimeout(()=> form.submit(), 10);
        }

        function rebuildModelosByMarca(){
            if (!selMarca || !selModelo || !window.ALL_MODELOS) return;
            const idMarca = selMarca.value || '';
            selModelo.innerHTML = '';
            const def = document.createElement('option'); def.value = ''; def.textContent = 'Modelo';
            selModelo.appendChild(def);
            if (idMarca) {
                (window.ALL_MODELOS || []).filter(m => String(m.idMarca) === String(idMarca))
                    .forEach(m => {
                        const opt = document.createElement('option');
                        opt.value = String(m.idModelo); opt.textContent = m.nombre;
                        selModelo.appendChild(opt);
                    });
            }
        }

        selMarca?.addEventListener('change', ()=>{ rebuildModelosByMarca(); submitFilters(); });
        [selModelo, selColor, selUbic, selEst].forEach(sel => sel?.addEventListener('change', submitFilters));

        if (inputQ) {
            inputQ.addEventListener('keydown', (e) => {
                if (e.key === 'Enter'){ e.preventDefault(); const v = (inputQ.value||'').trim(); if (v!==lastSubmittedQ) submitFilters(); }
            });
            inputQ.addEventListener('blur', ()=> {
                const v = (inputQ.value||'').trim();
                if (v === lastSubmittedQ) return;
                if (v === '' && lastSubmittedQ === '') return;
                submitFilters();
            });
        }

        window.addEventListener('pageshow', hideLoading);
        document.addEventListener('visibilitychange', ()=> { if (document.visibilityState==='visible') hideLoading(); });
        window.addEventListener('popstate', hideLoading);

        document.addEventListener('click', (e) => {
            const a = e.target.closest('.pagination a'); if (!a) return;
            if (e.metaKey || e.ctrlKey || e.shiftKey || e.button===1) return;
            document.body.classList.add('loading');
        });

        window.addEventListener('DOMContentLoaded', () => {
            document.querySelector('.table-wrapper')?.classList.add('table-appear');
            hideLoading();
        });
    })();
</script>

<!-- Modal: edición/alta -->
<script>
    (() => {
        const dlg = document.getElementById('editModal');
        const form= document.getElementById('editForm');
        const modalTitle = document.getElementById('modalTitle');
        const formAction = document.getElementById('formAction');
        const returnQueryInp = document.getElementById('returnQuery');

        const selMarca  = document.getElementById('idMarca');
        const selModelo = document.getElementById('idModelo');
        const selUbic   = document.getElementById('idUbicacion');
        const selEst    = document.getElementById('idEstatus');
        const selColor  = document.getElementById('idColor');

        const numeroSerie = document.getElementById('numeroSerie');

        function buildModeloOptions(idMarca, selected){
            selModelo.innerHTML = '';
            const def = document.createElement('option'); def.value=''; def.textContent='—';
            selModelo.appendChild(def);
            if (!idMarca) { selModelo.value = ''; return; }
            const modelos = (window.ALL_MODELOS||[]).filter(x=> String(x.idMarca)===String(idMarca));
            for (const m of modelos){
                const opt = document.createElement('option');
                opt.value = String(m.idModelo); opt.textContent = m.nombre;
                selModelo.appendChild(opt);
            }
            if (selected && modelos.some(m=>String(m.idModelo)===String(selected))) selModelo.value = String(selected);
            else selModelo.value = '';
        }
        selMarca?.addEventListener('change', ()=> buildModeloOptions(selMarca.value, ''));

        // Abrir edición
        document.querySelectorAll('.btn-editar').forEach(b=>{
            b.addEventListener('click', async (ev)=>{
                ev.preventDefault();
                const id = ev.currentTarget.dataset.id;
                if (!id) return alert('Sin ID');

                try{
                    const r = await fetch(ctx + '/consumibles?action=get&id=' + encodeURIComponent(id), { headers:{'Accept':'application/json'} });
                    if (!r.ok) throw new Error('HTTP ' + r.status);
                    const e = await r.json();

                    modalTitle.textContent = 'Editar consumible';
                    formAction.value = 'save';
                    document.getElementById('idEquipo').value = e.idEquipo;
                    (returnQueryInp) && (returnQueryInp.value = (window.location.search||'').replace(/^\?/, ''));

                    // Base
                    selMarca.value   = e.idMarca ?? '';
                    buildModeloOptions(selMarca.value, e.idModelo ?? '');
                    selUbic.value    = e.idUbicacion ?? '';
                    selEst.value     = e.idEstatus ?? '';
                    numeroSerie.value= e.numeroSerie ?? '';
                    selColor.value   = e.idColor ?? '';

                    if (typeof dlg?.showModal === 'function') dlg.showModal();
                }catch(err){
                    console.error(err);
                    alert('No fue posible cargar el consumible.');
                }
            });
        });

        // Alta
        const addBtn = document.querySelector('a.btn.btn-primary[href$="/consumibles-nuevo"]');
        function resetFormForCreate(){
            modalTitle.textContent='Agregar consumible';
            formAction.value='create';
            document.getElementById('idEquipo').value = '';
            selMarca.value=''; buildModeloOptions('', '');
            selUbic.value=''; selEst.value=''; selColor.value='';
            numeroSerie.value='';
            (returnQueryInp) && (returnQueryInp.value = (window.location.search||'').replace(/^\?/, ''));
        }
        addBtn?.addEventListener('click', (ev)=>{
            ev.preventDefault();
            resetFormForCreate();
            if (typeof dlg?.showModal === 'function') dlg.showModal();
            else alert('Tu navegador no soporta dialog.');
        });

        // Cierre animado
        function animateCloseDialog(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=>{ d.classList.remove('closing'); d.close(); }, {once:true});
        }
        document.getElementById('btnClose')?.addEventListener('click', ()=> animateCloseDialog(dlg));
        document.getElementById('btnCancel')?.addEventListener('click', ()=> animateCloseDialog(dlg));
        dlg?.addEventListener('cancel', (ev)=>{ ev.preventDefault(); animateCloseDialog(dlg); });
    })();
</script>

<!-- Modal: detalles -->
<script>
    (() => {
        const dDlg = document.getElementById('detailsModal');
        const el = (id)=> document.getElementById(id);
        function show(v){ return (v==null || v==='') ? '—' : v; }

        async function openDetails(id){
            try{
                const r = await fetch(ctx + '/consumibles?action=get&id=' + encodeURIComponent(id), { headers:{'Accept':'application/json'} });
                if (!r.ok) throw new Error('HTTP ' + r.status);
                const e = await r.json();

                const findName = (arr, key, id) => { try { return (arr||[]).find(x=>String(x[key])===String(id))?.nombre || ''; } catch(_) { return ''; } };
                const marcaNombre  = e.marcaNombre  || findName(window.ALL_MARCAS, 'idMarca', e.idMarca);
                const modeloNombre = e.modeloNombre || findName(window.ALL_MODELOS, 'idModelo', e.idModelo);
                const ubicNombre   = e.ubicacionNombre || findName(window.ALL_UBICS, 'idUbicacion', e.idUbicacion);
                const estNombre    = e.estatusNombre   || findName(window.ALL_ESTATUS,'idEstatus', e.idEstatus);
                const colorNombre  = e.colorNombre || (window.ALL_COLORES||[]).find(c=>String(c.idColor)===String(e.idColor))?.nombre;

                el('d_numeroSerie').textContent = show(e.numeroSerie);
                el('d_marca').textContent       = show(marcaNombre);
                el('d_modelo').textContent      = show(modeloNombre);
                el('d_ubicacion').textContent   = show(ubicNombre);
                el('d_estatus').textContent     = show(estNombre);
                el('d_color').textContent       = show(colorNombre);
                el('d_notas').textContent       = show(e.notas);

                if (typeof dDlg?.showModal === 'function') dDlg.showModal();
            }catch(err){
                alert('No fue posible cargar los detalles.');
            }
        }

        document.addEventListener('click', (ev)=>{
            const btn = ev.target.closest('.btn-detalles'); if (!btn) return;
            ev.preventDefault();
            const id = btn.getAttribute('data-id'); if (id) openDetails(id);
        });

        function animateClose(d){
            if(!d) return;
            d.classList.add('closing');
            d.addEventListener('animationend', ()=>{ d.classList.remove('closing'); d.close(); }, {once:true});
        }
        document.getElementById('btnCloseDetails')?.addEventListener('click', ()=> animateClose(dDlg));
        document.getElementById('btnCloseDetails2')?.addEventListener('click', ()=> animateClose(dDlg));
        dDlg?.addEventListener('cancel', (e)=>{ e.preventDefault(); animateClose(dDlg); });
    })();
</script>

<!-- Navbar drag + activos (igual a sims.jsp) -->
<script>
    (function(){
        const menuBox   = document.querySelector('.box-menu');
        const wrapper   = document.querySelector('.box-menu .wrapper');
        const burger    = document.querySelector('.hamburguer');
        const menuLinks = document.querySelectorAll('.box-menu .menu a');
        const SAVE_KEY = 'boxMenuPos';
        let isDragging=false, moved=false, startX=0,startY=0,offX=0,offY=0;
        function getPoint(e){ if (e.touches && e.touches[0]) return {x:e.touches[0].clientX,y:e.touches[0].clientY}; return {x:e.clientX,y:e.clientY}; }
        function setPos(x,y){
            if (!menuBox) return;
            const w = menuBox.offsetWidth||60, h=menuBox.offsetHeight||60;
            const maxX = Math.max(0,(window.innerWidth||0)-w);
            const maxY = Math.max(0,(window.innerHeight||0)-h);
            const nx=Math.max(0,Math.min(x,maxX)), ny=Math.max(0,Math.min(y,maxY));
            menuBox.style.left = nx+'px'; menuBox.style.top = ny+'px';
        }
        function onDown(e){
            if (!menuBox) return;
            const p = getPoint(e); isDragging=true; moved=false; startX=p.x; startY=p.y;
            offX=p.x-menuBox.offsetLeft; offY=p.y-menuBox.offsetTop; menuBox.classList.add('dragging');
            document.addEventListener('mousemove', onMove); document.addEventListener('mouseup', onUp);
            document.addEventListener('touchmove', onMove, {passive:false}); document.addEventListener('touchend', onUp);
        }
        function onMove(e){
            if (!isDragging || !menuBox) return;
            const p = getPoint(e); const nx=p.x-offX, ny=p.y-offY;
            if (Math.abs(p.x-startX)>4 || Math.abs(p.y-startY)>4) moved=true;
            setPos(nx,ny); if (e.cancelable) e.preventDefault();
        }
        function onUp(){
            if (!isDragging || !menuBox) return; isDragging=false; menuBox.classList.remove('dragging');
            document.removeEventListener('mousemove',onMove); document.removeEventListener('mouseup',onUp);
            document.removeEventListener('touchmove',onMove); document.removeEventListener('touchend',onUp);
            const left=parseInt(menuBox.style.left||menuBox.offsetLeft||0,10);
            const top =parseInt(menuBox.style.top ||menuBox.offsetTop ||0,10);
            try{ localStorage.setItem(SAVE_KEY, JSON.stringify({left, top})); }catch(_){}
            setTimeout(()=>{ moved=false; }, 50);
        }
        if (menuBox){
            try{ const s=localStorage.getItem(SAVE_KEY); if(s){ const p=JSON.parse(s); if(typeof p.left==='number'&&typeof p.top==='number') setPos(p.left,p.top); } }catch(_){}
            menuBox.addEventListener('mousedown', onDown);
            menuBox.addEventListener('touchstart', onDown, {passive:true});
        }
        if (wrapper) wrapper.addEventListener('click', function(){ if (isDragging || moved) return; menuBox?.classList.toggle('full-menu'); burger?.classList.toggle('active'); });
        function updateArrow(currentLink){
            document.querySelectorAll('.box-menu .menu a .text i.fa-arrow-left').forEach(i=> i.remove());
            if (!currentLink) return;
            const txt = currentLink.querySelector('.text');
            if (txt){ const i=document.createElement('i'); i.className='fa-solid fa-arrow-left'; i.style.marginLeft='6px'; txt.appendChild(i); }
        }
        (function setActiveFromLocation(){
            const file=(window.location.pathname||'').split('/').pop().toLowerCase();
            let current=null;
            menuLinks.forEach(a=>{
                const href=(a.getAttribute('href')||'').toLowerCase();
                if (!current && href && file && href.endsWith(file)) current=a;
            });
            if (!current) current=document.querySelector('.box-menu .menu a.active');
            if (current){ menuLinks.forEach(l=> l.classList.remove('active')); current.classList.add('active'); updateArrow(current); }
        })();
        menuLinks.forEach(link=> link.addEventListener('click', function(){ menuLinks.forEach(l=> l.classList.remove('active')); link.classList.add('active'); updateArrow(link); }));
    })();
</script>
</body>
</html>
