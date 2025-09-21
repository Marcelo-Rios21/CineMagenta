-- Crecion de DB --
CREATE DATABASE IF NOT EXISTS Cine_DB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE Cine_DB;

-- Tabla principal --
DROP TABLE IF EXISTS Cartelera;
CREATE TABLE Cartelera (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo       VARCHAR(150) NOT NULL,
  director     VARCHAR(50)  NOT NULL,
  anio         INT          NOT NULL,
  duracion_min INT          NOT NULL,
  genero       ENUM('ACCION','DRAMA','COMEDIA','ANIMACION','TERROR','CIENCIA_FICCION') NOT NULL,
  CONSTRAINT chk_anio CHECK (anio BETWEEN 1900 AND 2100),
  CONSTRAINT chk_duracion CHECK (duracion_min > 0)
);

-- Índices útiles para futuras búsquedas
CREATE INDEX ix_cartelera_titulo  ON Cartelera(titulo);
CREATE INDEX ix_cartelera_genero  ON Cartelera(genero);
