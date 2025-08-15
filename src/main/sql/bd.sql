-- Esquema
CREATE DATABASE IF NOT EXISTS gmas2 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE gmas2;

-- -----------------------
-- Seguridad / Usuarios
-- -----------------------
CREATE TABLE rol (
                     id_rol int PRIMARY KEY,
                     nombre VARCHAR(20) UNIQUE NOT NULL  -- 'employee','admin','master'
);

INSERT INTO rol (id_rol, nombre) VALUES (1,'employee'),(2,'admin'),(3,'master');

CREATE TABLE estatus (
                         id_estatus int auto_increment PRIMARY KEY,
                         tipo_estatus enum('EQUIPO','PERSONAL','UBICACION'),
                         nombre VARCHAR(20) NOT NULL,  -- Disponible, Asignado, En reparación
                         unique (tipo_estatus, nombre)
);

INSERT INTO estatus(tipo_estatus, nombre) VALUES ("EQUIPO",'Disponible'),("EQUIPO",'Asignado'),("EQUIPO",'En reparación');

CREATE TABLE ubicacion (
                           id_ubicacion INT PRIMARY KEY AUTO_INCREMENT,
                           nombre VARCHAR(120) NOT NULL,
                           id_estatus int not null,
                           notas text,
                           UNIQUE(nombre),
                           FOREIGN KEY (id_estatus) REFERENCES estatus(id_estatus)
);

create table puesto(
                       id_puesto int PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(120) NOT NULL,
                       notas text
);

create table centro(
                       id_centro int PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(50) NOT NULL,
                       id_ubicacion int not null,
                       notas text,
                       FOREIGN KEY (id_ubicacion) REFERENCES ubicacion(id_ubicacion)
);

create table lider(
                      id_lider int PRIMARY KEY AUTO_INCREMENT,
                      nombre VARCHAR(50) NOT NULL,
                      apellido_paterno varchar(50),
                      apellido_materno varchar(50),
                      email VARCHAR(120) NOT NULL UNIQUE,
                      telefono VARCHAR(25),
                      id_centro int not null,
                      id_puesto int not null,
                      id_estatus int not null,
                      FOREIGN KEY (id_centro) REFERENCES centro(id_centro),
                      FOREIGN KEY (id_puesto) REFERENCES puesto(id_puesto),
                      FOREIGN KEY (id_estatus) REFERENCES estatus(id_estatus)
);

CREATE TABLE usuario (
                         id_usuario int PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(50) NOT NULL,
                         apellido_paterno varchar(50),
                         apellido_materno varchar(50),
                         email VARCHAR(120) NOT NULL UNIQUE,
                         telefono VARCHAR(25),
                         id_lider int not null,
                         id_puesto int not null,
                         id_centro int not null,
                         id_rol int NOT NULL,
                         hash_password VARCHAR(255) NOT NULL,
                         activo BOOLEAN NOT NULL DEFAULT TRUE,
                         ultimo_login DATETIME,
                         creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (id_lider) REFERENCES lider(id_lider),
                         FOREIGN KEY (id_puesto) REFERENCES puesto(id_puesto),
                         FOREIGN KEY (id_centro) REFERENCES centro(id_centro),
                         FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- -----------------------
-- Catálogos base
-- -----------------------
CREATE TABLE marca (
                       id_marca INT PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(100) NOT NULL,
                       activo BOOLEAN NOT NULL DEFAULT TRUE,
                       notas text,
                       UNIQUE(nombre)
);


CREATE TABLE modelo (
                        id_modelo INT PRIMARY KEY AUTO_INCREMENT,
                        id_marca INT NOT NULL,
                        nombre VARCHAR(120) NOT NULL,
                        activo BOOLEAN NOT NULL DEFAULT TRUE,
                        UNIQUE(id_marca, nombre),
                        notas text,
                        FOREIGN KEY (id_marca) REFERENCES marca(id_marca)
);

-- Colores (para consumibles)
CREATE TABLE color (
                       id_color INT PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(60) NOT NULL UNIQUE
);

-- -----------------------
-- Tipos y Estatus de equipo
-- -----------------------
CREATE TABLE tipo_equipo (
                             id_tipo int PRIMARY KEY,
                             nombre VARCHAR(40) UNIQUE NOT NULL  -- Impresora, AP, Workstation, Laptop, Monitor, Teléfono fijo, Celular, Tablet, Computadora móvil, Antena, Consumible, SIM
);




-- -----------------------
-- Equipos (base + subtipos)
-- -----------------------
CREATE TABLE equipo (
                        id_equipo int PRIMARY KEY AUTO_INCREMENT,
                        id_tipo int NOT NULL,
                        id_modelo INT,
                        numero_serie VARCHAR(120),
                        id_marca INT,                 -- redundante si usas modelo, pero útil cuando no exista modelo en catálogo
                        id_ubicacion INT,
                        id_estatus int NOT NULL DEFAULT 1,
                        ip_fija VARCHAR(45),
                        puerto_ethernet VARCHAR(20),
                        notas TEXT,
                        UNIQUE(numero_serie),
                        INDEX idx_equipo_estatus (id_estatus),
                        INDEX idx_equipo_tipo (id_tipo),
                        INDEX idx_equipo_ubicacion (id_ubicacion),
                        FOREIGN KEY (id_tipo) REFERENCES tipo_equipo(id_tipo),
                        FOREIGN KEY (id_modelo) REFERENCES modelo(id_modelo),
                        FOREIGN KEY (id_marca) REFERENCES marca(id_marca),
                        FOREIGN KEY (id_ubicacion) REFERENCES ubicacion(id_ubicacion),
                        FOREIGN KEY (id_estatus) REFERENCES estatus(id_estatus)
);

-- Subtipo SIM (campos extra: número asignado, IMEI)
CREATE TABLE equipo_sim (
                            id_equipo int PRIMARY KEY,
                            numero_asignado VARCHAR(30) NOT NULL UNIQUE,
                            imei VARCHAR(20) NOT NULL UNIQUE,
                            FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo) ON DELETE CASCADE
);

-- Subtipo Consumible (campo extra: color)
CREATE TABLE equipo_consumible (
                                   id_equipo int PRIMARY KEY,
                                   id_color INT NOT NULL,
                                   FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo) ON DELETE CASCADE,
                                   FOREIGN KEY (id_color) REFERENCES color(id_color)
);

-- -----------------------
-- Asignaciones
-- -----------------------
CREATE TABLE asignacion (
                            id_asignacion int PRIMARY KEY AUTO_INCREMENT,
                            id_equipo int NOT NULL,
                            id_usuario int NOT NULL,            -- a quién se asigna
                            asignado_por int NOT NULL,          -- admin/master que realiza la operación
                            asignado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            devuelto_en DATETIME NULL,
                            ruta_pdf VARCHAR(300),                  -- para "descargar PDF para firma"
                            CONSTRAINT uq_equipo_asignado_activo UNIQUE (id_equipo, asignado_en),
                            INDEX idx_asignacion_usuario (id_usuario),
                            FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo),
                            FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
                            FOREIGN KEY (asignado_por) REFERENCES usuario(id_usuario),
                            CHECK (devuelto_en IS NULL OR devuelto_en >= asignado_en)
);

-- -----------------------
-- Bitácora de movimientos
-- -----------------------
CREATE TABLE bitacora_movimiento (
                                     id_mov int PRIMARY KEY AUTO_INCREMENT,
                                     id_equipo int NOT NULL,
                                     id_usuario int NULL,          -- involucrado (si aplica)
                                     accion ENUM('ASIGNAR','DEVOLVER','CAMBIO_ESTATUS','REPARACION_IN','REPARACION_OUT') NOT NULL,
                                     estatus_origen int NULL,
                                     estatus_destino int NULL,
                                     realizado_por int NOT NULL,   -- usuario del sistema que ejecuta
                                     realizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     notas text,
                                     INDEX idx_bitacora_equipo (id_equipo),
                                     FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo),
                                     FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
                                     FOREIGN KEY (estatus_origen) REFERENCES estatus(id_estatus),
                                     FOREIGN KEY (estatus_destino) REFERENCES estatus(id_estatus),
                                     FOREIGN KEY (realizado_por) REFERENCES usuario(id_usuario)
);

-- -----------------------
-- Vistas para inventario / dashboard
-- -----------------------
CREATE VIEW vw_equipos_disponibles AS
SELECT e.* FROM equipo e WHERE e.id_estatus = 1;

CREATE VIEW vw_equipos_asignados AS
SELECT e.* FROM equipo e WHERE e.id_estatus = 2;

CREATE VIEW vw_equipos_reparacion AS
SELECT e.* FROM equipo e WHERE e.id_estatus = 3;

CREATE VIEW vw_sim_disponibles AS
SELECT es.* FROM equipo e
                     JOIN equipo_sim es ON es.id_equipo = e.id_equipo
WHERE e.id_estatus = 1;

CREATE VIEW vw_dashboard_resumen AS
SELECT
    (SELECT COUNT(*) FROM usuario WHERE activo=TRUE) AS total_usuarios,
    (SELECT COUNT(*) FROM equipo WHERE id_estatus=2) AS total_equipos_asignados,
    (SELECT COUNT(*) FROM equipo WHERE id_estatus=1) AS equipos_disponibles,
    (SELECT COUNT(*) FROM equipo WHERE id_estatus=3) AS equipos_en_reparacion;

-- -----------------------
-- Reglas con triggers (integridad de negocio)
-- -----------------------

-- Al crear una asignación: validar que el equipo esté 'Disponible' y cambiar a 'Asignado'
DELIMITER $$
CREATE TRIGGER trg_asignacion_before_insert
    BEFORE INSERT ON asignacion
    FOR EACH ROW
BEGIN
    DECLARE est_actual int;
    SELECT id_estatus INTO est_actual FROM equipo WHERE id_equipo = NEW.id_equipo FOR UPDATE;
    IF est_actual IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Equipo inexistente';
END IF;
IF est_actual <> 1 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Solo se pueden asignar equipos con estatus Disponible';
END IF;
END$$

CREATE TRIGGER trg_asignacion_after_insert
    AFTER INSERT ON asignacion
    FOR EACH ROW
BEGIN
    UPDATE equipo SET id_estatus = 2 WHERE id_equipo = NEW.id_equipo;
    INSERT INTO bitacora_movimiento (id_equipo,id_usuario,accion,estatus_origen,estatus_destino,realizado_por,notas)
    VALUES (NEW.id_equipo, NEW.id_usuario, 'ASIGNAR', 1, 2, NEW.asignado_por, 'Asignación creada');
    END$$

    -- Al devolver (cuando se fija devuelto_en): regresar a Disponible
    CREATE TRIGGER trg_asignacion_after_update
        AFTER UPDATE ON asignacion
        FOR EACH ROW
    BEGIN
        IF NEW.devuelto_en IS NOT NULL AND OLD.devuelto_en IS NULL THEN
        UPDATE equipo SET id_estatus = 1 WHERE id_equipo = NEW.id_equipo;
        INSERT INTO bitacora_movimiento (id_equipo,id_usuario,accion,estatus_origen,estatus_destino,realizado_por,notas)
        VALUES (NEW.id_equipo, NEW.id_usuario, 'DEVOLVER', 2, 1, NEW.asignado_por, 'Equipo devuelto');
    END IF;
    END$$
    DELIMITER ;
