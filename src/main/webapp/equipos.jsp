<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Equipos</title>
    <style>
        .btn{padding:.45rem .8rem;border:1px solid #cbd5e1;border-radius:.5rem;background:#fff;cursor:pointer}
        .btn-primary{background:#2563eb;color:#fff;border-color:#1d4ed8}
        .btn-danger{background:#ef4444;color:#fff;border-color:#dc2626}
        table{width:100%;border-collapse:collapse}
        th,td{padding:.55rem;border-bottom:1px solid #e5e7eb;text-align:left}
        thead th{font-weight:600;color:#334155}
        .toolbar{display:flex;gap:.5rem;flex-wrap:wrap;align-items:center;justify-content:space-between;margin:1rem 0}
        dialog::backdrop{background:rgba(0,0,0,.3)}
        dialog{border:none;border-radius:.75rem;max-width:820px;width:95%}
        .modal-h{display:flex;align-items:center;justify-content:space-between;padding:1rem;border-bottom:1px solid #e5e7eb}
        .modal-b{padding:1rem;display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:.8rem}
        .modal-f{display:flex;justify-content:flex-end;gap:.5rem;padding:1rem;border-top:1px solid #e5e7eb}
        .field{display:flex;flex-direction:column;gap:.25rem}
    </style>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css" integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>

<div class="box-menu">
    <div class="wrapper">
        <div class="hamburguer">
            <span></span>
            <span></span>
            <span></span>
            <span></span>
        </div>
    </div>
    <div class="menu">
        <a href="#" class="active"><span class="fa-solid fa-desktop"></span><span class="text">Equipos</span></a>
        <a href="#"><span class="fa-solid fa-arrow-right-arrow-left"></span><span class="text">Asignaciones</span></a>
        <a href="#"><span class="fa-solid fa-folder-open"></span><span class="text">Catalogos</span></a>
        <a href="#"><span class="fa-solid fa-users"></span><span class="text">Usuarios</span></a>
        <a href="#"><span class="fa-solid fa-chart-line"></span><span class="text">Dashboard</span></a>
    </div>
</div>

<main class="container">
    <h1>Equipos</h1>

    <c:if test="${not empty sessionScope.flashOk}">
        <div style="margin:.5rem 0;color:#065f46;background:#d1fae5;padding:.4rem .6rem;border-radius:.4rem">
                ${sessionScope.flashOk}
        </div>
        <c:remove var="flashOk" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div style="margin:.5rem 0;color:#991b1b;background:#fee2e2;padding:.4rem .6rem;border-radius:.4rem">
                ${sessionScope.flashError}
        </div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <!-- Filtros -->
    <form class="toolbar" method="get" action="${pageContext.request.contextPath}/equipos">
        <div style="display:flex;gap:.5rem;flex-wrap:wrap">
            <input type="search" name="q" value="${q}" placeholder="Buscar serie/notas" />
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
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/equipos-nuevo">Agregar Equipo</a>
    </form>

    <!-- Tabla -->
    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>Tipo</th>
                <th>Modelo</th>
                <th>Núm. Serie</th>
                <th>Marca</th>
                <th>Ubicación</th>
                <th>IP Fija</th>
                <th>Estatus</th>
                <th style="width:170px">Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="e" items="${equipos}">
                <tr>
                    <td>${e.tipoNombre}</td>
                    <td><c:out value="${empty e.modeloNombre ? '—' : e.modeloNombre}"/></td>
                    <td>${e.numeroSerie}</td>
                    <td><c:out value="${empty e.marcaNombre ? '—' : e.marcaNombre}"/></td>
                    <td><c:out value="${empty e.ubicacionNombre ? '—' : e.ubicacionNombre}"/></td>
                    <td><c:out value="${empty e.ipFija ? '—' : e.ipFija}"/></td>
                    <td>${e.estatusNombre}</td>
                    <td>
                        <button type="button" class="btn btn-primary btn-editar"
                                data-id="${e.idEquipo}">Editar</button>


                        <form style="display:inline" method="post" action="${pageContext.request.contextPath}/equipos">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="idEquipo" value="${e.idEquipo}"/>
                            <button class="btn btn-danger"
                                    onclick="return confirm('¿Eliminar este equipo?')">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>

<!-- MODAL EDICIÓN -->
<dialog id="editModal">
    <form id="editForm" method="post" action="${pageContext.request.contextPath}/equipos">
        <input type="hidden" name="action" id="formAction" value="save"/>
        <input type="hidden" name="idEquipo" id="idEquipo"/>
        <input type="hidden" name="tipoNombre" id="tipoNombre"/>

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
                <input type="text" name="numeroSerie" id="numeroSerie" required/>
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

            <!-- Datos SIM (solo visible si el tipo contiene "SIM") -->
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

            <!-- Datos Consumible (solo visible si el tipo contiene "CONSUMIBLE") -->
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
                <div id="asignacionesPanel" style="border:1px solid #e5e7eb;border-radius:.5rem;padding:.5rem;max-height:240px;overflow:auto">
                    <div id="asignacionesEmpty" style="color:#6b7280">No hay asignaciones.</div>
                    <table id="asignacionesTable" style="width:100%;border-collapse:collapse;display:none">
                        <thead>
                        <tr>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Usuario</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado por</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Asignado en</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Devuelto en</th>
                            <th style="text-align:left;padding:.35rem;border-bottom:1px solid #e5e7eb">Estado</th>
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

<script>
  // Context path para peticiones
  const ctx = '${pageContext.request.contextPath}';

  // Modelos disponibles (compartido por filtros y modal)
  window.ALL_MODELOS = [
    <c:forEach var="m" items="${modelos}" varStatus="s">
      { idModelo: ${m.idModelo}, idMarca: ${m.idMarca}, nombre: '<c:out value="${m.nombre}"/>' }<c:if test="${!s.last}">,</c:if>
    </c:forEach>
  ];

  // =======================
  // Filtros (barra superior) - Filtrado en cliente (sin recargar)
  // =======================
  (() => {
    const form = document.querySelector('form.toolbar');
    const tableBody = document.querySelector('.table-wrapper table tbody');
    const selTipoFiltro      = form?.querySelector('select[name="idTipo"]');
    const selMarcaFiltro     = form?.querySelector('select[name="idMarca"]');
    const selModeloFiltro    = form?.querySelector('select[name="idModelo"]');
    const selUbicacionFiltro = form?.querySelector('select[name="idUbicacion"]');
    const selEstatusFiltro   = form?.querySelector('select[name="idEstatus"]');
    const inputQ             = form?.querySelector('input[name="q"]');

    const initialIdMarca  = '${idMarca}';
    const initialIdModelo = '${idModelo}';

    const norm = (s) => (s == null ? '' : String(s).trim().toLowerCase());
    const valOrEmpty = (s) => {
      const v = norm(s);
      return v === '—' ? '' : v;
    };
    const selectedText = (sel) => sel && sel.options && sel.selectedIndex >= 0
      ? norm(sel.options[sel.selectedIndex].textContent) : '';

    function buildModeloOptionsFilter(idMarca, selectedIdModelo) {
      if (!selModeloFiltro) return;
      selModeloFiltro.innerHTML = '';
      const def = document.createElement('option');
      def.value = '';
      def.textContent = 'Modelo';
      selModeloFiltro.appendChild(def);

      if (!idMarca) {
        selModeloFiltro.value = '';
        return;
      }

      const modelos = (window.ALL_MODELOS || []).filter(m => String(m.idMarca) === String(idMarca));
      for (const m of modelos) {
        const opt = document.createElement('option');
        opt.value = String(m.idModelo);
        opt.textContent = m.nombre;
        selModeloFiltro.appendChild(opt);
      }

      if (selectedIdModelo && modelos.some(m => String(m.idModelo) === String(selectedIdModelo))) {
        selModeloFiltro.value = String(selectedIdModelo);
      } else {
        selModeloFiltro.value = '';
      }
    }

    function filterRows() {
      if (!tableBody) return;
      const q = norm(inputQ ? inputQ.value : '');

      const tTipo = selectedText(selTipoFiltro);
      const tMarca = selectedText(selMarcaFiltro);
      const tModelo = selectedText(selModeloFiltro);
      const tUbic = selectedText(selUbicacionFiltro);
      const tEstatus = selectedText(selEstatusFiltro);

      const rows = Array.from(tableBody.querySelectorAll('tr'));
      for (const tr of rows) {
        const cTipo  = valOrEmpty(tr.cells[0]?.textContent);
        const cModelo= valOrEmpty(tr.cells[1]?.textContent);
        const cSerie = valOrEmpty(tr.cells[2]?.textContent);
        const cMarca = valOrEmpty(tr.cells[3]?.textContent);
        const cUbic  = valOrEmpty(tr.cells[4]?.textContent);
        const cEst   = valOrEmpty(tr.cells[6]?.textContent);

        const matchQ = !q || [cTipo, cModelo, cSerie, cMarca, cUbic, cEst].some(t => t.includes(q));
        const matchTipo = !selTipoFiltro?.value || cTipo === tTipo;
        const matchMarca = !selMarcaFiltro?.value || cMarca === tMarca;
        const matchModelo = !selModeloFiltro?.value || cModelo === tModelo;
        const matchUbic = !selUbicacionFiltro?.value || cUbic === tUbic;
        const matchEst = !selEstatusFiltro?.value || cEst === tEstatus;

        tr.style.display = (matchQ && matchTipo && matchMarca && matchModelo && matchUbic && matchEst) ? '' : 'none';
      }
    }

    const debounce = (fn, delay = 350) => {
      let t = null;
      return (...args) => {
        if (t) clearTimeout(t);
        t = setTimeout(() => fn.apply(null, args), delay);
      };
    };

    if (form) {
      // Dependencia Marca -> Modelo
      if (selMarcaFiltro) {
        selMarcaFiltro.addEventListener('change', () => {
          buildModeloOptionsFilter(selMarcaFiltro.value, '');
          filterRows();
        });
      }
      if (selModeloFiltro) selModeloFiltro.addEventListener('change', filterRows);
      if (selTipoFiltro) selTipoFiltro.addEventListener('change', filterRows);
      if (selUbicacionFiltro) selUbicacionFiltro.addEventListener('change', filterRows);
      if (selEstatusFiltro) selEstatusFiltro.addEventListener('change', filterRows);

      if (inputQ) {
        const debouncedFilter = debounce(filterRows, 350);
        inputQ.addEventListener('input', () => {
          const val = inputQ.value.trim();
          // Filtra sólo con 0 o >=3 caracteres para mejorar UX
          /*if (val.length === 0 || val.length >= 3)*/ debouncedFilter();
        });
        inputQ.addEventListener('blur', () => {
          const val = inputQ.value.trim();
          if (val.length === 0 || val.length >= 3) filterRows();
        });
        inputQ.addEventListener('keydown', (e) => {
          if (e.key === 'Enter') { e.preventDefault(); filterRows(); }
        });
      }

      // Inicializar opciones de modelo y aplicar filtro inicial (valores actuales)
      if (selMarcaFiltro && selModeloFiltro) {
        const idMarca = initialIdMarca && initialIdMarca !== 'null' ? initialIdMarca : selMarcaFiltro.value;
        const idModelo = initialIdModelo && initialIdModelo !== 'null' ? initialIdModelo : '';
        buildModeloOptionsFilter(idMarca, idModelo);
      }
      // Primer filtrado para respetar los valores seleccionados
      filterRows();
    }
  })();

  // =======================
  // Modal de edición
  // =======================
  (() => {
    const dlg  = document.getElementById('editModal');
    const form = document.getElementById('editForm');
    const modalTitle = document.getElementById('modalTitle');
    const formAction = document.getElementById('formAction');
    const selMarcaModal  = document.getElementById('idMarca');
    const selModeloModal = document.getElementById('idModelo');
    const selEstatusModal = document.getElementById('idEstatus');
    const selTipoModal = document.getElementById('idTipo');
    const tipoNombreInp = document.getElementById('tipoNombre');
    const simFields = document.getElementById('simFields');
    const consumibleFields = document.getElementById('consumibleFields');
    const asignacionesPanel = document.getElementById('asignacionesPanel');

    // Control de estatus y asignaciones
    const STATUS_ASIGNADO = 2;
    let estatusOriginal = null;
    let estatusSeleccionado = null;
    let asignacionesActuales = [];

    // Asegura que el select tenga opción "Asignado" SOLO si el equipo actual está en ese estatus.
    function ensureAsignadoOption(currentStatus) {
      if (!selEstatusModal) return;

      // Quitar cualquier opción temporal previa
      const tempOpt = selEstatusModal.querySelector('option[data-temp-asignado="1"]');
      if (tempOpt) tempOpt.remove();

      const currentVal = parseInt(currentStatus, 10);
      if (currentVal === STATUS_ASIGNADO) {
        // Si no existe ninguna opción con ese valor, insertamos la temporal
        const exists = Array.from(selEstatusModal.options).some(o => o.value === String(STATUS_ASIGNADO));
        if (!exists) {
          const opt = document.createElement('option');
          opt.value = String(STATUS_ASIGNADO);
          opt.textContent = 'Asignado';
          opt.setAttribute('data-temp-asignado', '1'); // identificador para poder quitarla
          // Insertar al inicio para que quede visible
          selEstatusModal.insertBefore(opt, selEstatusModal.firstChild);
        }
      }
    }

    // Panel de asignaciones
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

    function renderAsignaciones(list) {
      if (!asignacionesTable || !asignacionesTbody) return;
      asignacionesTbody.innerHTML = '';
      const has = Array.isArray(list) && list.length > 0;
      asignacionesEmpty.style.display = has ? 'none' : 'block';
      asignacionesTable.style.display = has ? 'table' : 'none';

      if (!has) return;

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
        tr.appendChild(makeTd(a.asignadoEn || ''));
        tr.appendChild(makeTd(a.devueltoEn || ''));
        tr.appendChild(makeTd(a && a.devueltoEn ? 'Devuelto' : 'Activa'));
        asignacionesTbody.appendChild(tr);
      }
    }

    if (selMarcaModal) {
      selMarcaModal.addEventListener('change', () => {
        buildModeloOptionsModal(selMarcaModal.value, '');
      });
    }

    // Advertencia al cambiar estatus desde Asignado
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
          // Usuario confirmó: quitar opción temporal "Asignado" para que no pueda re-seleccionarla
          const tempOpt = selEstatusModal.querySelector('option[data-temp-asignado="1"]');
          if (tempOpt) selEstatusModal.removeChild(tempOpt);
        }
        estatusSeleccionado = next;
      });

      // Confirmación final al guardar
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

    document.querySelectorAll('.btn-editar').forEach(b=>{
      b.addEventListener('click', async (ev)=>{
        ev.preventDefault();
        const id = ev.currentTarget.dataset.id;
        if(!id){ alert('No se encontró el ID del equipo'); return; }

        try {
          const url = ctx + '/equipos?action=get&id=' + encodeURIComponent(id);
          const r = await fetch(url, { headers: { 'Accept': 'application/json' } });

          if (!r.ok) {
            const text = await r.text().catch(()=> '');
            throw new Error('Error ' + r.status + ': ' + (text || 'No fue posible cargar el equipo'));
          }

          const e = await r.json();

          // Forzar modo edición
          if (modalTitle) modalTitle.textContent = 'Editar equipo';
          if (formAction) formAction.value = 'save';
          if (asignacionesPanel) asignacionesPanel.style.display = '';

          // Set campos base (excepto estatus, se maneja más abajo)
          document.getElementById('idEquipo').value        = e.idEquipo;
          document.getElementById('idTipo').value          = e.idTipo;
          document.getElementById('numeroSerie').value     = e.numeroSerie ?? '';
          document.getElementById('idUbicacion').value     = e.idUbicacion ?? '';
          document.getElementById('ipFija').value          = e.ipFija ?? '';
          document.getElementById('puertoEthernet').value  = e.puertoEthernet ?? '';
          document.getElementById('notas').value           = e.notas ?? '';

          // Mostrar/ocultar subcampos según tipo actual
          const opt = selTipoModal.options[selTipoModal.selectedIndex];
          const nombreTipo = (opt && opt.textContent ? opt.textContent.trim().toUpperCase() : '');
          if (tipoNombreInp) tipoNombreInp.value = nombreTipo;
          if (simFields) simFields.style.display = nombreTipo.includes('SIM') ? '' : 'none';
          if (consumibleFields) consumibleFields.style.display =
              (nombreTipo.includes('CONSUMIBLE') || nombreTipo.includes('CONSUM')) ? '' : 'none';

          // Estatus original y seleccionado
          estatusOriginal = e.idEstatus;
          estatusSeleccionado = e.idEstatus;
          // Asegura que la opción "Asignado" exista si el equipo está en ese estado y luego selecciona el valor
          ensureAsignadoOption(e.idEstatus);
          selEstatusModal.value = String(e.idEstatus);

          // Marca -> Modelos -> Modelo
          selMarcaModal.value = e.idMarca ?? '';
          buildModeloOptionsModal(selMarcaModal.value, e.idModelo ?? '');

          // Cargar asignaciones
          asignacionesActuales = [];
          renderAsignaciones([]); // reset UI
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
          } catch (_) {
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
    });

    document.getElementById('btnClose').onclick = () => dlg.close();
    document.getElementById('btnCancel').onclick = () => dlg.close();
  })();
</script>

<!-- Modal: modo creación y subtipos (SIM/Consumible) -->
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

    const simFields = document.getElementById('simFields');
    const simNumero = document.getElementById('simNumeroAsignado');
    const simImei   = document.getElementById('simImei');

    const consumibleFields = document.getElementById('consumibleFields');
    const idColorConsumible= document.getElementById('idColorConsumible');

    // Panel de asignaciones
    const asignacionesPanel = document.getElementById('asignacionesPanel');
    const asignacionesEmpty = document.getElementById('asignacionesEmpty');
    const asignacionesTable = document.getElementById('asignacionesTable');
    const asignacionesTbody = asignacionesTable ? asignacionesTable.querySelector('tbody') : null;

    function toggleSubtypeFields() {
      const opt = selTipoModal.options[selTipoModal.selectedIndex];
      const nombreTipo = (opt && opt.textContent ? opt.textContent.trim().toUpperCase() : '');
      tipoNombreInp.value = nombreTipo;
      const isSIM  = nombreTipo.includes('SIM');
      const isCONS = nombreTipo.includes('CONSUMIBLE') || nombreTipo.includes('CONSUM');

      simFields.style.display = isSIM ? '' : 'none';
      consumibleFields.style.display = isCONS ? '' : 'none';

      if (!isSIM) { if (simNumero) simNumero.value = ''; if (simImei) simImei.value = ''; }
      if (!isCONS) { if (idColorConsumible) idColorConsumible.value = ''; }
    }

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

    selTipoModal.addEventListener('change', toggleSubtypeFields);
    if (selMarcaModal) {
      selMarcaModal.addEventListener('change', () => buildModeloOptionsModal(selMarcaModal.value, ''));
    }

    function resetFormForCreate() {
      modalTitle.textContent = 'Agregar equipo';
      formAction.value = 'create';
      document.getElementById('idEquipo').value = '';
      document.getElementById('numeroSerie').value = '';
      selTipoModal.selectedIndex = 0;
      selMarcaModal.value = '';
      buildModeloOptionsModal('', '');
      document.getElementById('idUbicacion').value = '';
      selEstatusModal.value = '';
      document.getElementById('ipFija').value = '';
      document.getElementById('puertoEthernet').value = '';
      document.getElementById('notas').value = '';

      // Ocultar panel de asignaciones en nuevo
      if (asignacionesPanel) asignacionesPanel.style.display = 'none';
      if (asignacionesTbody) asignacionesTbody.innerHTML = '';
      if (asignacionesEmpty) asignacionesEmpty.style.display = 'block';
      if (asignacionesTable) asignacionesTable.style.display = 'none';

      // Quitar opción temporal "Asignado", si existe
      const tempOpt = selEstatusModal.querySelector('option[data-temp-asignado="1"]');
      if (tempOpt) tempOpt.remove();

      toggleSubtypeFields();
    }

    // Interceptar botón "Agregar Equipo"
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


<script type="text/javascript">

    // Navbar sin jQuery
    (function(){
        const menuBox   = document.querySelector('.box-menu');
        const wrapper   = document.querySelector('.box-menu .wrapper');
        const burger    = document.querySelector('.hamburguer');
        const menuLinks = document.querySelectorAll('.box-menu .menu a');

        if (wrapper) {
            wrapper.addEventListener('click', function(){
                if (menuBox) menuBox.classList.toggle('full-menu');
                if (burger)  burger.classList.toggle('active');
            });
        }

        // Activar el link pulsado dentro del menú
        menuLinks.forEach(function(link){
            link.addEventListener('click', function(ev){
                menuLinks.forEach(function(l){ l.classList.remove('active'); });
                link.classList.add('active');
            });
        });
    })();
</script>
</body>
</html>
