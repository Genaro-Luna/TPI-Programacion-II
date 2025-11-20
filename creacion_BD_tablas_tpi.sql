CREATE DATABASE IF NOT EXISTS tpi;

USE tpi;

CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP NOT NULL,
    credencial_id BIGINT NULL UNIQUE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE credencialAcceso (
    id BIGINT PRIMARY KEY,
    hashPassword VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    ultimoCambio TIMESTAMP NOT NULL,
    requiereReset BOOLEAN NOT NULL DEFAULT FALSE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_credencial_a_usuario
        FOREIGN KEY (id) REFERENCES usuario(id)
        ON DELETE CASCADE
);

ALTER TABLE usuario
ADD CONSTRAINT fk_usuario_a_credencial
    FOREIGN KEY (credencial_id) REFERENCES credencialAcceso(id)
    ON DELETE SET NULL;