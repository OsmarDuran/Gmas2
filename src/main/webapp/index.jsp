<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.0/css/all.min.css" integrity="sha512-DxV+EoADOkOygM4IR9yXP8Sb2qwgidEmeqAEmDKIOfPRQZOWbXCzLC6vjbZyy0vPisbH2SyW27+ddLVCN+OMzQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<!-- Avisos globales -->
<c:if test="${not empty sessionScope.flashOk}">
    <div style="position:fixed;left:50%;top:18px;transform:translateX(-50%);z-index:9999;padding:.6rem .8rem;border-radius:.6rem;background:#d1fae5;color:#065f46;box-shadow:0 6px 20px rgba(0,0,0,.15)">
            ${sessionScope.flashOk}
    </div>
    <c:remove var="flashOk" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
    <div style="position:fixed;left:50%;top:18px;transform:translateX(-50%);z-index:9999;padding:.6rem .8rem;border-radius:.6rem;background:#fee2e2;color:#991b1b;box-shadow:0 6px 20px rgba(0,0,0,.15)">
            ${sessionScope.flashError}
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>

<div class="container">
    <div class="login-box">
        <h2>Inicio de sesión</h2>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="login"/>
            <div class="input-box">
                <input type="email" name="email" required>
                <label>Email</label>
            </div>
            <div class="input-box">
                <input type="password" name="password" required>
                <label>Contraseña</label>
            </div>
            <div class="forgot-password">
                <a href="#" id="btnForgot">Olvidé mi contraseña</a>
            </div>
            <button type="submit" class="btn">Iniciar sesión</button>
            <div class="signup-link">
                <a href="#" id="btnOpenRegister">Registrarme</a>
            </div>
        </form>

    </div>
    <span style="--i:0;"></span>
    <span style="--i:1;"></span>
    <span style="--i:2;"></span>
    <span style="--i:3;"></span>
    <span style="--i:4;"></span>
    <span style="--i:5;"></span>
    <span style="--i:6;"></span>
    <span style="--i:7;"></span>
    <span style="--i:8;"></span>
    <span style="--i:9;"></span>
    <span style="--i:10;"></span>
    <span style="--i:11;"></span>
    <span style="--i:12;"></span>
    <span style="--i:13;"></span>
    <span style="--i:14;"></span>
    <span style="--i:15;"></span>
    <span style="--i:16;"></span>
    <span style="--i:17;"></span>
    <span style="--i:18;"></span>
    <span style="--i:19;"></span>
    <span style="--i:20;"></span>
    <span style="--i:21;"></span>
    <span style="--i:22;"></span>
    <span style="--i:23;"></span>
    <span style="--i:24;"></span>
    <span style="--i:25;"></span>
    <span style="--i:26;"></span>
    <span style="--i:27;"></span>
    <span style="--i:28;"></span>
    <span style="--i:29;"></span>
    <span style="--i:30;"></span>
    <span style="--i:31;"></span>
    <span style="--i:32;"></span>
    <span style="--i:33;"></span>
    <span style="--i:34;"></span>
    <span style="--i:35;"></span>
    <span style="--i:36;"></span>
    <span style="--i:37;"></span>
    <span style="--i:38;"></span>
    <span style="--i:39;"></span>
    <span style="--i:40;"></span>
    <span style="--i:41;"></span>
    <span style="--i:42;"></span>
    <span style="--i:43;"></span>
    <span style="--i:44;"></span>
    <span style="--i:45;"></span>
    <span style="--i:46;"></span>
    <span style="--i:47;"></span>
    <span style="--i:48;"></span>
    <span style="--i:49;"></span>
</div>

<!-- Modal Registro -->
<dialog id="registerModal" class="reg-modal">
    <form id="registerForm" method="post" action="${pageContext.request.contextPath}/auth" class="reg-form">
        <input type="hidden" name="action" value="register"/>

        <div class="reg-h">
            <h3>Crear cuenta</h3>
            <button type="button" id="btnCloseRegister" class="icon-btn" aria-label="Cerrar">✕</button>
        </div>

        <!-- Aviso dentro del modal (errores de registro) -->
        <c:if test="${not empty sessionScope.regMsg}">
            <div style="margin:8px 16px 0 16px;padding:.5rem .7rem;border-radius:8px;background:#fff7ed;color:#9a3412;border:1px solid #fdba74;">
                    ${sessionScope.regMsg}
            </div>
        </c:if>

        <div class="reg-b">
            <div class="grid-1-1-1">
                <div class="col-12">
                    <label>Nombre</label>
                    <input type="text" name="nombre" required placeholder="Nombre(s)"
                           value="<c:out value='${sessionScope.reg_nombre}'/>"/>
                </div>
                <div>
                    <label>Apellido paterno</label>
                    <input type="text" name="apellidoPaterno"required placeholder=""
                           value="<c:out value='${sessionScope.reg_apellidoPaterno}'/>"/>
                </div>
                <div>
                    <label>Apellido materno</label>
                    <input type="text" name="apellidoMaterno"required placeholder=""
                           value="<c:out value='${sessionScope.reg_apellidoMaterno}'/>"/>
                </div>
                <div class="col-12">
                    <label>Email</label>
                    <input type="email" name="email" required placeholder="correo@empresa.com"
                           value="<c:out value='${sessionScope.reg_email}'/>"/>
                </div>
                <div>
                    <label>Contraseña</label>
                    <input type="password" name="password" id="regPass" required minlength="8" placeholder="Mínimo 8 caracteres"/>
                </div>
                <div>
                    <label>Confirmar contraseña</label>
                    <input type="password" name="password2" id="regPass2" required minlength="8" placeholder="Repite la contraseña"/>
                </div>
                <div class="col-12">
                    <label>Teléfono</label>
                    <input type="tel" name="telefono"required placeholder=""
                           value="<c:out value='${sessionScope.reg_telefono}'/>"/>
                </div>

                <!-- Nuevos: Centro, Puesto, Líder -->
                <div>
                    <label>Centro</label>
                    <select name="idCentro" id="r_idCentro" required data-selected="<c:out value='${sessionScope.reg_idCentro}'/>">
                        <option value="">— Selecciona centro —</option>
                    </select>
                </div>
                <div>
                    <label>Puesto</label>
                    <select name="idPuesto" id="r_idPuesto" required data-selected="<c:out value='${sessionScope.reg_idPuesto}'/>">
                        <option value="">— Selecciona puesto —</option>
                    </select>
                </div>
                <div>
                    <label>Líder</label>
                    <select name="idLider" id="r_idLider" required data-selected="<c:out value='${sessionScope.reg_idLider}'/>">
                        <option value="">— Selecciona líder —</option>
                    </select>
                </div>

                <div class="col-12 legal">Al registrarte aceptas los términos y condiciones.</div>
            </div>
        </div>

        <div class="reg-f">
            <button type="button" id="btnCancelRegister" class="btn-ghost">Cancelar</button>
            <button type="submit" class="btn-primary">Crear cuenta</button>
        </div>
    </form>
</dialog>

<!-- Modal: Olvidé mi contraseña -->
<dialog id="forgotModal" class="reg-modal">
    <form id="forgotForm" class="reg-form" method="dialog">
        <div class="reg-h">
            <h3>Recuperar contraseña</h3>
            <button type="button" id="btnCloseForgot" class="icon-btn" aria-label="Cerrar">✕</button>
        </div>

        <div id="forgotMsg" style="display:none;margin:8px 16px 0 16px;padding:.5rem .7rem;border-radius:8px;background:#fff7ed;color:#9a3412;border:1px solid #fdba74;"></div>

        <div class="reg-b" style="padding-top:8px">
            <!-- Paso 1: solicitar email -->
            <div id="step1">
                <div class="grid-1-1-1">
                    <div class="col-12">
                        <label>Email</label>
                        <input type="email" id="fpEmail" placeholder="tu@empresa.com" required/>
                    </div>
                </div>
            </div>

            <!-- Paso 2: confirmar código y nueva contraseña -->
            <div id="step2" style="display:none">
                <div class="grid-1-1-1">
                    <div class="col-12">
                        <label>Código de verificación</label>
                        <input type="text" id="fpCode" inputmode="numeric" maxlength="6" placeholder="6 dígitos" required/>
                    </div>
                    <div>
                        <label>Nueva contraseña</label>
                        <input type="password" id="fpPass1" minlength="8" placeholder="Mínimo 8 caracteres" required/>
                    </div>
                    <div>
                        <label>Confirmar contraseña</label>
                        <input type="password" id="fpPass2" minlength="8" placeholder="Repite la contraseña" required/>
                    </div>
                </div>
                <div id="devCodeHint" style="display:none;margin-top:.35rem;color:#475569;font-size:.9rem"></div>
            </div>
        </div>

        <div class="reg-f">
            <button type="button" id="btnCancelForgot" class="btn-ghost">Cancelar</button>
            <button type="button" id="btnNextForgot" class="btn-primary">Continuar</button>
        </div>
    </form>
</dialog>

<script type="text/javascript">
    (function(){
        const ctx = '${pageContext.request.contextPath}';
        const dlg = document.getElementById('forgotModal');
        const btnOpen = document.getElementById('btnForgot');
        const btnClose = document.getElementById('btnCloseForgot');
        const btnCancel = document.getElementById('btnCancelForgot');
        const btnNext = document.getElementById('btnNextForgot');
        const msg = document.getElementById('forgotMsg');
        const step1 = document.getElementById('step1');
        const step2 = document.getElementById('step2');
        const emailInp = document.getElementById('fpEmail');
        const codeInp = document.getElementById('fpCode');
        const p1 = document.getElementById('fpPass1');
        const p2 = document.getElementById('fpPass2');
        const devHint = document.getElementById('devCodeHint');

        function setMsg(text, ok){
            if (!msg) return;
            if (!text) { msg.style.display='none'; msg.textContent=''; return; }
            msg.textContent = text;
            msg.style.display = 'block';
            msg.style.background = ok ? '#d1fae5' : '#fff7ed';
            msg.style.color = ok ? '#065f46' : '#9a3412';
            msg.style.borderColor = ok ? '#6ee7b7' : '#fdba74';
        }

        function openDlg(e){
            if (e) e.preventDefault();
            setMsg('', false);
            step1.style.display = '';
            step2.style.display = 'none';
            devHint.style.display = 'none';
            if (typeof dlg?.showModal === 'function') dlg.showModal();
            setTimeout(()=> emailInp?.focus(), 50);
        }
        function closeDlg(e){
            if (e) e.preventDefault();
            dlg.close();
        }

        if (btnOpen) btnOpen.addEventListener('click', openDlg);
        if (btnClose) btnClose.addEventListener('click', closeDlg);
        if (btnCancel) btnCancel.addEventListener('click', closeDlg);
        if (dlg) dlg.addEventListener('cancel', (ev)=>{ ev.preventDefault(); closeDlg(); });

        btnNext?.addEventListener('click', async () => {
            try{
                if (step1.style.display !== 'none') {
                    const email = (emailInp.value||'').trim().toLowerCase();
                    if (!email) { setMsg('Ingresa tu email.', false); emailInp.focus(); return; }
                    setMsg('Enviando código...', true);
                    const r = await fetch(ctx + '/auth', {
                        method: 'POST',
                        headers: { 'Content-Type':'application/x-www-form-urlencoded' },
                        body: new URLSearchParams({ action:'forgot-start', email })
                    });
                    const data = await r.json().catch(()=> ({}));
                    if (!r.ok || data.ok !== true) {
                        setMsg(data.error || 'No se pudo iniciar el proceso.', false);
                        return;
                    }
                    setMsg('Código generado. Si tu correo está registrado, te llegará un correo con el código.', true);
                    step1.style.display = 'none';
                    step2.style.display = '';
                    // Modo dev: muestra el código devuelto (si viene)
                    if (data.devCode) {
                        devHint.textContent = 'Código (desarrollo): ' + data.devCode + ' | expira en ~10 minutos.';
                        devHint.style.display = 'block';
                    }
                    setTimeout(()=> codeInp?.focus(), 80);
                    return;
                }

                // Paso 2: validar y completar
                const code = (codeInp.value||'').trim();
                if (!/^\d{6}$/.test(code)) { setMsg('Ingresa el código de 6 dígitos.', false); codeInp.focus(); return; }
                const pass1 = p1.value||'', pass2 = p2.value||'';
                if (pass1.length < 8) { setMsg('La nueva contraseña debe tener al menos 8 caracteres.', false); p1.focus(); return; }
                if (pass1 !== pass2) { setMsg('Las contraseñas no coinciden.', false); p2.focus(); return; }

                setMsg('Actualizando contraseña...', true);
                const r2 = await fetch(ctx + '/auth', {
                    method: 'POST',
                    headers: { 'Content-Type':'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ action:'forgot-complete', code, password: pass1 })
                });
                const data2 = await r2.json().catch(()=> ({}));
                if (!r2.ok || data2.ok !== true) {
                    setMsg(data2.error || 'No se pudo actualizar la contraseña.', false);
                    return;
                }
                setMsg('Contraseña actualizada. Ya puedes iniciar sesión.', true);
                setTimeout(()=> closeDlg(), 1000);
            }catch(err){
                setMsg('Ocurrió un error. Intenta más tarde.', false);
            }
        });
    })();
</script>


<script type="text/javascript">
    (function(){
        const dlg = document.getElementById('registerModal');
        const openBtn = document.getElementById('btnOpenRegister');
        const closeBtn = document.getElementById('btnCloseRegister');
        const cancelBtn = document.getElementById('btnCancelRegister');
        const form = document.getElementById('registerForm');
        const pass = document.getElementById('regPass');
        const pass2 = document.getElementById('regPass2');

        const selCentro = document.getElementById('r_idCentro');
        const selPuesto = document.getElementById('r_idPuesto');
        const selLider  = document.getElementById('r_idLider');

        const ctx = '${pageContext.request.contextPath}';
        const SHOULD_OPEN = '${not empty sessionScope.regOpen ? "1" : ""}';
        const prevCentro = selCentro ? selCentro.dataset.selected : '';
        const prevPuesto = selPuesto ? selPuesto.dataset.selected : '';
        const prevLider  = selLider  ? selLider.dataset.selected  : '';

        async function loadCatalogsOnce(){
            if (!dlg || dlg.dataset.catalogsLoaded === '1') return;
            try{
                const r = await fetch(ctx + '/auth?action=catalogs', { headers: { 'Accept':'application/json' } });
                if (!r.ok) throw new Error('HTTP ' + r.status);
                const data = await r.json();
                const addOptions = (sel, list, idKey, textKey, textFn) => {
                    if (!sel || !Array.isArray(list)) return;
                    sel.innerHTML = `<option value="">— Selecciona —</option>`;
                    list.forEach(item => {
                        const opt = document.createElement('option');
                        opt.value = String(item[idKey]);
                        opt.textContent = textFn ? textFn(item) : String(item[textKey] || '');
                        sel.appendChild(opt);
                    });
                };
                addOptions(selCentro, data.centros, 'idCentro', 'nombre');
                addOptions(selPuesto, data.puestos, 'idPuesto', 'nombre');
                addOptions(selLider,  data.lideres, 'idLider', null, (l) => {
                    const ap = l.apellidoPaterno || '';
                    const am = l.apellidoMaterno || '';
                    return [l.nombre, ap, am].filter(Boolean).join(' ').trim();
                });

                // Preseleccionar si venía de error
                if (prevCentro) selCentro.value = String(prevCentro);
                if (prevPuesto) selPuesto.value = String(prevPuesto);
                if (prevLider)  selLider.value  = String(prevLider);

                dlg.dataset.catalogsLoaded = '1';
            }catch(e){
                console.error(e);
            }
        }

        function openDlg(e){
            if (e) e.preventDefault();
            if (!dlg) return;
            dlg.classList.remove('closing');
            dlg.classList.add('opening');
            if (typeof dlg.showModal === 'function') dlg.showModal();
            loadCatalogsOnce();
        }
        function closeDlg(e){
            if (e) e.preventDefault();
            if (!dlg || !dlg.open) return;
            dlg.classList.remove('opening');
            dlg.classList.add('closing');
            dlg.addEventListener('animationend', function onEnd(){
                dlg.classList.remove('closing');
                dlg.close();
                dlg.removeEventListener('animationend', onEnd);
            });
        }

        if (openBtn) openBtn.addEventListener('click', openDlg);
        if (closeBtn) closeBtn.addEventListener('click', closeDlg);
        if (cancelBtn) cancelBtn.addEventListener('click', closeDlg);
        if (dlg) dlg.addEventListener('cancel', function(ev){ ev.preventDefault(); closeDlg(); });

        function validatePasswords(){
            if (!pass || !pass2) return true;
            if (pass.value !== pass2.value) {
                pass2.setCustomValidity('Las contraseñas no coinciden');
                return false;
            }
            pass2.setCustomValidity('');
            return true;
        }
        if (pass && pass2) {
            pass.addEventListener('input', validatePasswords);
            pass2.addEventListener('input', validatePasswords);
        }

        if (form) {
            form.addEventListener('submit', function(e){
                if (!validatePasswords()) {
                    e.preventDefault();
                    pass2.reportValidity();
                    return;
                }
                if (!selCentro.value || !selPuesto.value || !selLider.value) {
                    e.preventDefault();
                    alert('Selecciona Centro, Puesto y Líder.');
                }
            });
        }

        // Si venimos de un fallo de registro: abre modal y precarga catálogos + selección
        window.addEventListener('DOMContentLoaded', () => {
            if (SHOULD_OPEN === '1') {
                openDlg();
            }
        });
    })();
</script>

<!-- Limpia flags de registro DESPUÉS de que el script las leyó -->
<c:if test="${not empty sessionScope.regOpen}">
    <c:remove var="regOpen" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.regMsg}">
    <c:remove var="regMsg" scope="session"/>
</c:if>


<script type="text/javascript">
    (function(){
        const dlg = document.getElementById('registerModal');
        const openBtn = document.getElementById('btnOpenRegister');
        const closeBtn = document.getElementById('btnCloseRegister');
        const cancelBtn = document.getElementById('btnCancelRegister');
        const form = document.getElementById('registerForm');
        const pass = document.getElementById('regPass');
        const pass2 = document.getElementById('regPass2');

        const selCentro = document.getElementById('r_idCentro');
        const selPuesto = document.getElementById('r_idPuesto');
        const selLider  = document.getElementById('r_idLider');

        const ctx = '${pageContext.request.contextPath}';
        const SHOULD_OPEN = '${not empty sessionScope.regOpen ? "1" : ""}';
        const prevCentro = selCentro ? selCentro.dataset.selected : '';
        const prevPuesto = selPuesto ? selPuesto.dataset.selected : '';
        const prevLider  = selLider  ? selLider.dataset.selected  : '';

        async function loadCatalogsOnce(){
            if (!dlg || dlg.dataset.catalogsLoaded === '1') return;
            try{
                const r = await fetch(ctx + '/auth?action=catalogs', { headers: { 'Accept':'application/json' } });
                if (!r.ok) throw new Error('HTTP ' + r.status);
                const data = await r.json();
                const addOptions = (sel, list, idKey, textKey, textFn) => {
                    if (!sel || !Array.isArray(list)) return;
                    sel.innerHTML = `<option value="">— Selecciona —</option>`;
                    list.forEach(item => {
                        const opt = document.createElement('option');
                        opt.value = String(item[idKey]);
                        opt.textContent = textFn ? textFn(item) : String(item[textKey] || '');
                        sel.appendChild(opt);
                    });
                };
                addOptions(selCentro, data.centros, 'idCentro', 'nombre');
                addOptions(selPuesto, data.puestos, 'idPuesto', 'nombre');
                addOptions(selLider,  data.lideres, 'idLider', null, (l) => {
                    const ap = l.apellidoPaterno || '';
                    const am = l.apellidoMaterno || '';
                    return [l.nombre, ap, am].filter(Boolean).join(' ').trim();
                });

                // Preseleccionar si venía de error
                if (prevCentro) selCentro.value = String(prevCentro);
                if (prevPuesto) selPuesto.value = String(prevPuesto);
                if (prevLider)  selLider.value  = String(prevLider);

                dlg.dataset.catalogsLoaded = '1';
            }catch(e){
                console.error(e);
            }
        }

        function openDlg(e){
            if (e) e.preventDefault();
            if (!dlg) return;
            dlg.classList.remove('closing');
            dlg.classList.add('opening');
            if (typeof dlg.showModal === 'function') dlg.showModal();
            loadCatalogsOnce();
        }
        function closeDlg(e){
            if (e) e.preventDefault();
            if (!dlg || !dlg.open) return;
            dlg.classList.remove('opening');
            dlg.classList.add('closing');
            dlg.addEventListener('animationend', function onEnd(){
                dlg.classList.remove('closing');
                dlg.close();
                dlg.removeEventListener('animationend', onEnd);
            });
        }

        if (openBtn) openBtn.addEventListener('click', openDlg);
        if (closeBtn) closeBtn.addEventListener('click', closeDlg);
        if (cancelBtn) cancelBtn.addEventListener('click', closeDlg);
        if (dlg) dlg.addEventListener('cancel', function(ev){ ev.preventDefault(); closeDlg(); });

        function validatePasswords(){
            if (!pass || !pass2) return true;
            if (pass.value !== pass2.value) {
                pass2.setCustomValidity('Las contraseñas no coinciden');
                return false;
            }
            pass2.setCustomValidity('');
            return true;
        }
        if (pass && pass2) {
            pass.addEventListener('input', validatePasswords);
            pass2.addEventListener('input', validatePasswords);
        }

        if (form) {
            form.addEventListener('submit', function(e){
                if (!validatePasswords()) {
                    e.preventDefault();
                    pass2.reportValidity();
                    return;
                }
                if (!selCentro.value || !selPuesto.value || !selLider.value) {
                    e.preventDefault();
                    alert('Selecciona Centro, Puesto y Líder.');
                }
            });
        }

        // Si venimos de un fallo de registro: abre modal y precarga catálogos + selección
        window.addEventListener('DOMContentLoaded', () => {
            if (SHOULD_OPEN === '1') {
                openDlg();
            }
        });
    })();
</script>


</body>
</html>
