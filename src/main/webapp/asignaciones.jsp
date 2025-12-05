<%@ page isELIgnored="false" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8" />
                <title>Asignaciones</title>
                <link rel="stylesheet" href="css/style.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css"
                    integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ=="
                    crossorigin="anonymous" referrerpolicy="no-referrer" />
            </head>

            <body>
                <div id="loadingOverlay">
                    <div class="loader" aria-label="Cargando"></div>
                </div>

                <!-- MENU FLOANTE (mismo diseño que equipos.jsp) -->
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
                        <a href="sims"><span class="icon fa-solid fa-sim-card"></span><span class="text">Sims</span></a>
                        <a href="consumibles"><span class="icon fa-solid fa-boxes-stacked"></span><span
                                class="text">Consumibles</span></a>
                        <a href="asignaciones.jsp" class="active">
                            <span class="icon fa-solid fa-arrow-right-arrow-left"></span><span
                                class="text">Asignaciones</span>
                        </a>
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
                        <h1>Asignaciones</h1>
                        <p class="subtitle" style="color:#6b7280;margin-bottom:1rem;">
                            Selecciona cómo deseas visualizar y gestionar las asignaciones de equipos TIC.
                        </p>

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

                        <!-- Opciones principales -->
                        <div class="options-grid"
                            style="display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:1rem;">
                            <!-- Asignaciones por usuario -->
                            <article class="option-card"
                                style="border:1px solid #e5e7eb;border-radius:.75rem;padding:1rem;display:flex;flex-direction:column;gap:.5rem;">
                                <div style="display:flex;align-items:center;gap:.6rem;">
                                    <span class="icon-circle"
                                        style="width:36px;height:36px;border-radius:999px;display:inline-flex;align-items:center;justify-content:center;background:#eff6ff;color:#1d4ed8;">
                                        <i class="fa-solid fa-user-check"></i>
                                    </span>
                                    <div>
                                        <h2 style="font-size:1rem;margin:0;">Ver asignaciones en usuarios</h2>
                                        <p style="margin:0;font-size:.85rem;color:#6b7280;">
                                            Lista de empleados con su información y número de equipos asignados.
                                        </p>
                                    </div>
                                </div>
                                <div style="margin-top:.75rem;display:flex;justify-content:flex-end;">
                                    <a class="btn btn-primary" href="asignaciones_usuarios?accion=lista">
                                        Abrir asignaciones por usuario
                                    </a>
                                </div>
                            </article>

                            <!-- Asignaciones por equipo -->
                            <article class="option-card"
                                style="border:1px solid #e5e7eb;border-radius:.75rem;padding:1rem;display:flex;flex-direction:column;gap:.5rem;">
                                <div style="display:flex;align-items:center;gap:.6rem;">
                                    <span class="icon-circle"
                                        style="width:36px;height:36px;border-radius:999px;display:inline-flex;align-items:center;justify-content:center;background:#f5f3ff;color:#6d28d9;">
                                        <i class="fa-solid fa-desktop"></i>
                                    </span>
                                    <div>
                                        <h2 style="font-size:1rem;margin:0;">Ver asignaciones en equipos</h2>
                                        <p style="margin:0;font-size:.85rem;color:#6b7280;">
                                            Lista de equipos con su estado de asignación actual e historial.
                                        </p>
                                    </div>
                                </div>
                                <div style="margin-top:.75rem;display:flex;justify-content:flex-end;">
                                    <a class="btn btn-primary" href="asignaciones_equipos?accion=lista">
                                        Abrir asignaciones por equipo
                                    </a>
                                </div>
                            </article>
                        </div>
                    </div>
                </main>

                <script type="text/javascript">
                    // Quitar overlay al cargar
                    window.addEventListener('DOMContentLoaded', () => {
                        document.body.classList.remove('loading');
                    });

                    // Navbar sin jQuery + arrastrar box-menu (mismo patrón que en equipos.jsp)
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
                            // Restaurar posición previa
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
                            // Usamos el link ya marcado como active en el HTML (Asignaciones)
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

            </body>

            </html>