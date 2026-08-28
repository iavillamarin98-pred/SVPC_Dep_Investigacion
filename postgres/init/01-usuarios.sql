-- ==========================================
-- USUARIOS INICIALES SVPC
-- ==========================================

-- ==========================================
-- ADMINISTRADOR MAESTRO
-- Correo: admin@uteq.edu.ec
-- Contraseña: 123456
-- ==========================================

INSERT INTO usuarios (
    nombre,
    correo,
    contrasena,
    rol,
    estado,
    fecha_creacion
)
VALUES (
    'Administrador',
    'admin@uteq.edu.ec',
    '$2a$10$aHoW.yOkhdtVSJFoor82UOKUW58xQnKd6FR6WWF71zm0J5R4zC.sq',
    'ADMINISTRADOR_MASTER',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (correo) DO NOTHING;


-- ==========================================
-- USUARIO DE INVESTIGACIÓN
-- Correo: jpatinou@uteq.edu.ec
-- Contraseña: investigacion1
-- ==========================================

INSERT INTO usuarios (
    nombre,
    correo,
    contrasena,
    rol,
    estado,
    fecha_creacion
)
VALUES (
    'Javier Patino',
    'jpatinou@uteq.edu.ec',
    '$2a$10$U6BKEt2xcZ2aRCc7ecKf9.vPN3sZimJDfbeyFzE3EunsfkHFtMVHW',
    'USUARIO',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (correo) DO NOTHING;