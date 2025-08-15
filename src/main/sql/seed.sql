-- =========================================================
-- SEED DE DATOS PARA ESQUEMA gmas2 (estatus unificado)
-- Ejecutar DESPUÉS de crear las tablas (tu DDL).
-- =========================================================
USE gmas2;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------
-- ROLES
-- -----------------------
INSERT INTO rol (id_rol, nombre) VALUES
                                     (1,'employee'),(2,'admin'),(3,'master')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- -----------------------
-- ESTATUS (unificado)
-- Nota: tu DDL ya inserta EQUIPO: Disponible/Asignado/En reparación.
-- Aquí aseguramos su existencia e incorporamos UBICACION y PERSONAL.
-- -----------------------
INSERT INTO estatus (tipo_estatus, nombre) VALUES
                                               ('EQUIPO','Disponible'),
                                               ('EQUIPO','Asignado'),
                                               ('EQUIPO','En reparación')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Estatus para Ubicaciones
INSERT INTO estatus (tipo_estatus, nombre) VALUES
                                               ('UBICACION','Activa'),
                                               ('UBICACION','Inactiva')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Estatus para Personal/Líder
INSERT INTO estatus (tipo_estatus, nombre) VALUES
                                               ('PERSONAL','Activo'),
                                               ('PERSONAL','Baja')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Variables para usar los IDs correctos sin asumir valores
SET @EST_DISP     := (SELECT id_estatus FROM estatus WHERE tipo_estatus='EQUIPO'    AND nombre='Disponible');
SET @EST_ASIG     := (SELECT id_estatus FROM estatus WHERE tipo_estatus='EQUIPO'    AND nombre='Asignado');
SET @EST_REP      := (SELECT id_estatus FROM estatus WHERE tipo_estatus='EQUIPO'    AND nombre='En reparación');
SET @EST_UBI_ACT  := (SELECT id_estatus FROM estatus WHERE tipo_estatus='UBICACION' AND nombre='Activa');
SET @EST_UBI_INA  := (SELECT id_estatus FROM estatus WHERE tipo_estatus='UBICACION' AND nombre='Inactiva');
SET @EST_PER_ACT  := (SELECT id_estatus FROM estatus WHERE tipo_estatus='PERSONAL'  AND nombre='Activo');
SET @EST_PER_BAJA := (SELECT id_estatus FROM estatus WHERE tipo_estatus='PERSONAL'  AND nombre='Baja');

-- -----------------------
-- UBICACIONES
-- -----------------------
INSERT INTO ubicacion (id_ubicacion, nombre, id_estatus, notas) VALUES
                                                                    (1,'CDMX - Corporativo', @EST_UBI_ACT, 'Sede principal'),
                                                                    (2,'Monterrey - Planta 1', @EST_UBI_ACT, 'Producción'),
                                                                    (3,'Guadalajara - Centro TI', @EST_UBI_ACT, 'Soporte y desarrollo')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), id_estatus=VALUES(id_estatus), notas=VALUES(notas);

-- -----------------------
-- PUESTO
-- -----------------------
INSERT INTO puesto (id_puesto, nombre, notas) VALUES
                                                  (1,'Analista de Soporte','N1/N2'),
                                                  (2,'Administrador de Sistemas','Infraestructura'),
                                                  (3,'Gerente de TI','Lidera el área'),
                                                  (4,'Operador de Planta','Producción'),
                                                  (5,'Desarrollador','Aplicaciones internas')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), notas=VALUES(notas);

-- -----------------------
-- CENTRO (depende de ubicacion)
-- -----------------------
INSERT INTO centro (id_centro, nombre, id_ubicacion, notas) VALUES
                                                                (1,'CTI-CORP', 1, 'Centro de costo de TI en CDMX'),
                                                                (2,'PL1-OPS',  2, 'Operaciones Planta 1'),
                                                                (3,'GDL-DES',  3, 'Desarrollo Guadalajara')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), id_ubicacion=VALUES(id_ubicacion), notas=VALUES(notas);

-- -----------------------
-- LÍDER (depende de centro, puesto, estatus PERSONAL)
-- -----------------------
INSERT INTO lider (id_lider, nombre, apellido_paterno, apellido_materno, email, telefono, id_centro, id_puesto, id_estatus) VALUES
                                                                                                                                (1,'María','García','Luna','maria.garcia@empresa.com','555-100-0001',1,3,@EST_PER_ACT),
                                                                                                                                (2,'Carlos','Rivas','Mora','carlos.rivas@empresa.com','818-200-0002',2,3,@EST_PER_ACT),
                                                                                                                                (3,'Ana','Torres','Ibarra','ana.torres@empresa.com','333-300-0003',3,3,@EST_PER_ACT)
    ON DUPLICATE KEY UPDATE
                         nombre=VALUES(nombre), apellido_paterno=VALUES(apellido_paterno), apellido_materno=VALUES(apellido_materno),
                         telefono=VALUES(telefono), id_centro=VALUES(id_centro), id_puesto=VALUES(id_puesto), id_estatus=VALUES(id_estatus);

-- -----------------------
-- USUARIO
-- -----------------------
INSERT INTO usuario
(id_usuario, nombre, apellido_paterno, apellido_materno, email, telefono, id_lider, id_puesto, id_centro, id_rol, hash_password, activo, ultimo_login)
VALUES
    (1001,'Luis','Pérez','Soto','luis.perez@empresa.com','555-110-0001',1,1,1,1,'$2y$10$2bExAMPLE1u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',TRUE,NULL),
    (1002,'Elena','Ruiz','Campos','elena.ruiz@empresa.com','555-110-0002',1,5,1,1,'$2y$10$2bExAMPLE2u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',TRUE,NULL),
    (1003,'Javier','López','Nieto','javier.lopez@empresa.com','818-220-0003',2,4,2,1,'$2y$10$2bExAMPLE3u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',TRUE,NULL),
    (1004,'Sofía','Martínez','Ortega','sofia.martinez@empresa.com','333-330-0004',3,5,3,1,'$2y$10$2bExAMPLE4u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',TRUE,NULL),
    (1005,'Admin','TI','', 'admin.ti@empresa.com','555-000-9000',1,2,1,2,'$2y$10$AdMiNPaSSWORDEXAMPLEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',TRUE,NULL),
    (1006,'Master','System','', 'master.sys@empresa.com','555-000-9999',1,3,1,3,'$2y$10$MaSTERPaSSWORDEXAMPLEbbbbbbbbbbbbbbbbbbbbbbbbbbbb',TRUE,NULL)
    ON DUPLICATE KEY UPDATE
                         telefono=VALUES(telefono), id_lider=VALUES(id_lider), id_puesto=VALUES(id_puesto), id_centro=VALUES(id_centro), id_rol=VALUES(id_rol), activo=VALUES(activo);

-- -----------------------
-- CATÁLOGOS (marca/modelo/color)
-- -----------------------
INSERT INTO marca (id_marca, nombre, activo, notas) VALUES
                                                        (1,'HP', TRUE, NULL),
                                                        (2,'Dell', TRUE, NULL),
                                                        (3,'Cisco', TRUE, NULL),
                                                        (4,'Apple', TRUE, NULL),
                                                        (5,'Samsung', TRUE, NULL),
                                                        (6,'Epson', TRUE, NULL),
                                                        (7,'Motorola', TRUE, NULL)
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=VALUES(activo), notas=VALUES(notas);

-- Modelos (UNIQUE por (id_marca, nombre))
INSERT INTO modelo (id_modelo, id_marca, nombre, activo, notas) VALUES
                                                                    (1,1,'LaserJet Pro M404', TRUE, NULL),   -- HP
                                                                    (2,2,'Latitude 5420', TRUE, NULL),       -- Dell
                                                                    (3,3,'Aironet 1850', TRUE, NULL),        -- Cisco
                                                                    (4,4,'MacBook Pro 14', TRUE, NULL),      -- Apple
                                                                    (5,5,'Galaxy Tab S7', TRUE, NULL),       -- Samsung
                                                                    (6,6,'EcoTank L3250', TRUE, NULL),       -- Epson
                                                                    (7,2,'OptiPlex 7090', TRUE, NULL),       -- Dell
                                                                    (8,1,'EliteBook 840 G8', TRUE, NULL),    -- HP
                                                                    (9,7,'Moto G54', TRUE, NULL),            -- Motorola
                                                                    (10,5,'Galaxy Tab 3', TRUE, NULL),       -- Samsung
                                                                    (11,5,'Galaxy Tab 5', TRUE, NULL),       -- Samsung
                                                                    (12,4,'iPad Pro 11', TRUE, NULL)         -- Apple
    ON DUPLICATE KEY UPDATE id_marca=VALUES(id_marca), activo=VALUES(activo), notas=VALUES(notas);

INSERT INTO color (id_color, nombre) VALUES
                                         (1,'Negro'), (2,'Cian'), (3,'Magenta'), (4,'Amarillo')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- -----------------------
-- TIPOS DE EQUIPO
-- -----------------------
INSERT INTO tipo_equipo (id_tipo, nombre) VALUES
                                              (1,'Impresora'), (2,'Access Point'), (3,'Workstation'), (4,'Laptop'), (5,'Monitor'),
                                              (6,'Teléfono fijo'), (7,'Celular'), (8,'Tablet'), (9,'Computadora móvil'),
                                              (10,'Antena'), (11,'Consumible'), (12,'SIM')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- =========================================================
-- EQUIPOS (usa @EST_DISP para status inicial)
-- =========================================================
INSERT INTO equipo
(id_equipo, id_tipo, id_modelo, numero_serie, id_marca, id_ubicacion, id_estatus, ip_fija, puerto_ethernet, notas)
VALUES
    -- Impresoras
    (2001, 1, 1,  'HP-M404-0001',       1, 1, @EST_DISP, '10.10.1.21', NULL, 'Impresora piso 2'),
    (2002, 1, 6,  'EPS-L3250-0001',     6, 2, @EST_DISP, '10.20.1.15', NULL, 'Recepción PL1'),

    -- Access Points
    (2101, 2, 3,  'CISC-AP1850-0001',   3, 1, @EST_DISP, '10.10.2.31', NULL, NULL),

    -- Workstations
    (2201, 3, 7,  'DELL-WS7090-0001',   2, 2, @EST_DISP, NULL, NULL, 'Control de línea A'),

    -- Laptops
    (2301, 4, 2,  'DELL-LAT5420-0001',  2, 1, @EST_DISP, NULL, NULL, 'SSD 512GB, 16GB RAM'),
    (2302, 4, 8,  'HP-ELITE840-0001',   1, 3, @EST_DISP, NULL, NULL, NULL),
    (2303, 4, 4,  'APL-MBP14-0001',     4, 3, @EST_DISP, NULL, NULL, 'Equipo para desarrollo'),

    -- Monitores
    (2401, 5, NULL,'MON-GEN-0001',      2, 1, @EST_DISP, NULL, NULL, '24 pulgadas'),

    -- Celulares
    (2701, 7, 9,  'MOT-G54-0001',       7, 2, @EST_DISP, NULL, NULL, NULL),

    -- Tablets (incluye modelos Samsung)
    (2801, 8, 5,  'SMS-TABS7-0001',     5, 1, @EST_DISP, NULL, NULL, NULL),
    (2802, 8, 10, 'SMS-TAB3-0001',      5, 1, @EST_DISP, NULL, NULL, NULL),
    (2803, 8, 11, 'SMS-TAB5-0001',      5, 1, @EST_DISP, NULL, NULL, NULL),

    -- Consumibles (cartuchos)
    (2901, 11, 1, 'CN-HPM404-NEG-0001', 1, 1, @EST_DISP, NULL, NULL, 'Tóner Negro'),
    (2902, 11, 1, 'CN-HPM404-CIA-0001', 1, 1, @EST_DISP, NULL, NULL, 'Tóner Cian'),

    -- SIM cards
    (3001, 12, NULL,'SIM-ATTMX-0001',   NULL, 1, @EST_DISP, NULL, NULL, 'Plan corporativo'),
    (3002, 12, NULL,'SIM-ATTMX-0002',   NULL, 2, @EST_DISP, NULL, NULL, 'Plan corporativo')
    ON DUPLICATE KEY UPDATE
                         id_tipo=VALUES(id_tipo), id_modelo=VALUES(id_modelo), id_marca=VALUES(id_marca),
                         id_ubicacion=VALUES(id_ubicacion), id_estatus=VALUES(id_estatus),
                         ip_fija=VALUES(ip_fija), puerto_ethernet=VALUES(puerto_ethernet), notas=VALUES(notas);

-- -----------------------
-- SUBTIPOS
-- -----------------------
INSERT INTO equipo_sim (id_equipo, numero_asignado, imei) VALUES
                                                              (3001, '554000001', '356789012345671'),
                                                              (3002, '554000002', '356789012345689')
    ON DUPLICATE KEY UPDATE numero_asignado=VALUES(numero_asignado), imei=VALUES(imei);

INSERT INTO equipo_consumible (id_equipo, id_color) VALUES
                                                        (2901, 1),  -- Negro
                                                        (2902, 2)   -- Cian
    ON DUPLICATE KEY UPDATE id_color=VALUES(id_color);

-- =========================================================
-- ASIGNACIONES (disparan triggers definidos en tu DDL)
-- =========================================================
-- Asignar laptop DELL-LAT5420-0001 a Luis Pérez
INSERT INTO asignacion (id_asignacion, id_equipo, id_usuario, asignado_por, asignado_en, ruta_pdf)
VALUES (4001, 2301, 1001, 1005, NOW(), '/docs/asignaciones/4001_luisperez_lat5420.pdf')
    ON DUPLICATE KEY UPDATE ruta_pdf=VALUES(ruta_pdf);

-- Asignar SIM 3001 a Javier López
INSERT INTO asignacion (id_asignacion, id_equipo, id_usuario, asignado_por, asignado_en, ruta_pdf)
VALUES (4002, 3001, 1003, 1005, NOW(), '/docs/asignaciones/4002_javierlopez_sim3001.pdf')
    ON DUPLICATE KEY UPDATE ruta_pdf=VALUES(ruta_pdf);

-- (Opcional) Marcar equipo en reparación para dashboard
UPDATE equipo SET id_estatus = @EST_REP, notas = 'Falla de tarjeta lógica'
WHERE id_equipo = 2002;

SET FOREIGN_KEY_CHECKS = 1;

-- PRUEBAS
-- SELECT * FROM vw_dashboard_resumen;
-- SELECT * FROM vw_equipos_disponibles;
-- SELECT * FROM vw_equipos_asignados;
-- SELECT * FROM vw_equipos_reparacion;
-- SELECT e.id_equipo, e.id_estatus FROM equipo e WHERE e.id_equipo IN (2301,3001);
