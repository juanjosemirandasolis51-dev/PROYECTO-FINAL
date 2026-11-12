DROP TABLE IF EXISTS ganadores;
DROP TABLE IF EXISTS pujas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id               BIGSERIAL     PRIMARY KEY,
    nombre           VARCHAR(100)  NOT NULL,
    correo           VARCHAR(150)  NOT NULL UNIQUE,
    contrasena_hash  VARCHAR(255)  NOT NULL,
    rol              VARCHAR(10)   NOT NULL DEFAULT 'USUARIO'
                     CHECK (rol IN ('USUARIO', 'ADMIN')),
    fecha_registro   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE productos (
    id            BIGSERIAL      PRIMARY KEY,
    vendedor_id   BIGINT         NOT NULL REFERENCES usuarios(id),
    nombre        VARCHAR(150)   NOT NULL,
    descripcion   TEXT           NOT NULL,
    precio_base   NUMERIC(12,2)  NOT NULL CHECK (precio_base > 0),
    fecha_inicio  TIMESTAMP      NOT NULL DEFAULT NOW(),
    fecha_fin     TIMESTAMP      NOT NULL,
    estado        VARCHAR(10)    NOT NULL DEFAULT 'ACTIVA'
                  CHECK (estado IN ('ACTIVA', 'CERRADA', 'CANCELADA')),

    CHECK (fecha_fin > fecha_inicio)
);

CREATE TABLE pujas (
    id           BIGSERIAL      PRIMARY KEY,
    producto_id  BIGINT         NOT NULL REFERENCES productos(id),
    usuario_id   BIGINT         NOT NULL REFERENCES usuarios(id),
    monto        NUMERIC(12,2)  NOT NULL CHECK (monto > 0),
    fecha_hora   TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE ganadores (
    producto_id   BIGINT         PRIMARY KEY REFERENCES productos(id),
    usuario_id    BIGINT         NOT NULL REFERENCES usuarios(id),
    precio_final  NUMERIC(12,2)  NOT NULL CHECK (precio_final > 0),
    fecha_cierre  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pujas_producto_fecha ON pujas (producto_id, fecha_hora, id);
CREATE INDEX idx_productos_estado_fin ON productos (estado, fecha_fin);
CREATE INDEX idx_productos_vendedor ON productos (vendedor_id);
CREATE INDEX idx_ganadores_usuario  ON ganadores (usuario_id);
