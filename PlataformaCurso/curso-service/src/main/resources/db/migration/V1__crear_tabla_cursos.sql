CREATE TABLE cursos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        titulo VARCHAR(255) NOT NULL,
                        descripcion TEXT,
                        categoria VARCHAR(255),
                        instructor VARCHAR(255),
                        fecha_inicio DATE,
                        fecha_fin DATE,
                        cupo_maximo INT,
                        cupos_disponibles INT,
                        modalidad VARCHAR(100),
                        estado VARCHAR(100)
);