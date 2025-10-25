DELETE FROM descuentos;
ALTER TABLE descuentos AUTO_INCREMENT = 1;
DELETE FROM clientes;
ALTER TABLE clientes AUTO_INCREMENT = 1;
DELETE FROM productos;
ALTER TABLE productos AUTO_INCREMENT = 1;
DELETE FROM proveedores;
ALTER TABLE proveedores AUTO_INCREMENT = 1;
DELETE FROM categorias;
ALTER TABLE categorias AUTO_INCREMENT = 1;
DELETE FROM medios_pago;
ALTER TABLE medios_pago AUTO_INCREMENT = 1;
DELETE FROM cajas;
ALTER TABLE cajas AUTO_INCREMENT = 1;
DELETE FROM locales;
ALTER TABLE locales AUTO_INCREMENT = 1;
DELETE FROM users_roles;
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;
DELETE FROM roles;
ALTER TABLE roles AUTO_INCREMENT = 1;
DELETE FROM descuentos;
DELETE FROM clientes;
DELETE FROM productos;
DELETE FROM proveedores;
DELETE FROM categorias;
DELETE FROM medios_pago;
DELETE FROM cajas;
DELETE FROM locales;
DELETE FROM users_roles;
DELETE FROM users;
DELETE FROM roles;

INSERT INTO roles (nombre) VALUES
('USUARIOS'),
('ROLES'),
('PRODUCTOS'),
('CATEGORIAS'),
('LOCALES'),
('COMPRAS'),
('MEDIOS_DE_PAGO'),
('PROVEEDORES'),
('CLIENTES'),
('CAJA'),
('ADMIN');

INSERT INTO users (enabled, password, username) VALUES
(1,'$2a$10$DHmKRktydQQlVnrRwVI65edNihJKmsgTwVdsoJVofv2e6rjcpDGri', 'administrador'),
(1,'$2a$10$DHmKRktydQQlVnrRwVI65edNihJKmsgTwVdsoJVofv2e6rjcpDGri', 'supervisor'),
(1,'$2a$10$DHmKRktydQQlVnrRwVI65edNihJKmsgTwVdsoJVofv2e6rjcpDGri', 'cajero'),
(1,'$2a$10$DHmKRktydQQlVnrRwVI65edNihJKmsgTwVdsoJVofv2e6rjcpDGri', 'soporte');

INSERT INTO users_roles (user_id, role_id) VALUES
(1,11), -- administrador: ADMIN
(2,10), -- supervisor: CAJA
(3,10), -- cajero: CAJA
(4,10); -- soporte: CAJA


INSERT INTO locales (nombre, direccion, telefono, email, horario, activo) VALUES
('Beauty First Callao', 'Av. Sáenz Peña 150, Callao', '987654321', 'callao@beautyfirst.com', 'L-D 9am-8pm', TRUE),
('Beauty First  Piura', 'Av. Grau 300, Piura', '912345678', 'piura@beautyfirst.com', 'L-D 10am-7pm', TRUE),
('Beauty First  Talara', 'Av. Bolognesi 450, Talara, Piura', '935678123', 'talara@beautyfirst.com', 'L-D 8am-9pm', TRUE),
('Beauty First  Salaverry', 'Av. Salaverry 1200, Jesús María, Lima', '932165498', 'salayverry@beautyfirst.com', 'L-D 9am-6pm', TRUE),
('Beauty First  Miraflores', 'Av. Benavides 1135, Miraflores, Lima', '934567890', 'miraflores@beautyfirst.com', 'L-D 7am-11pm', TRUE),
('Beauty First  Arequipa', 'Av. Ejército 709, Cayma, Arequipa', '936789012', 'arequipa@beautyfirst.com', 'L-D 8am-9pm', TRUE);

INSERT INTO cajas (codigo, nombre, fecha_creacion, local_id, usuario_id) VALUES
('CA01', 'Caja 01', NOW(), 1, 1),
('PI01', 'Caja 01', NOW(), 2, 1),
('TA01', 'Caja 01', NOW(), 3, 1),
('SA01', 'Caja 01', NOW(), 4, 1),
('MI01', 'Caja 01', NOW(), 5, 1),
('AR01', 'Caja 01', NOW(), 6, 1);


INSERT INTO medios_pago (nombre, descripcion, tipo, activo) VALUES
('Efectivo', 'Pago en efectivo', 'EFECTIVO', 1),
('Tarjeta', 'Pago con tarjeta de crédito o débito', 'TARJETA', 1);


INSERT INTO categorias (nombre, descripcion, activo) VALUES
('Maquillaje', 'Productos para maquillaje facial y de ojos', true),
('Cuidado de la piel', 'Cremas, serums y tratamientos para la piel', true),
('Cabello', 'Shampoo, acondicionadores y tratamientos capilares', true),
('Uñas', 'Esmaltes, tratamientos y accesorios para uñas', true),
('Fragancias', 'Perfumes y colonias', true);


INSERT INTO proveedores (ruc, razon_social, direccion, telefono, email, contacto, activo) VALUES
('20123456789', 'Maybelline', 'Av. Lima 123', '999111222', 'proveedora@email.com', 'Juan Pérez', true),
('20234567890', 'Loreal', 'Jr. Arequipa 456', '988222333', 'distribuidorab@email.com', 'María López', true),
('20345678901', 'MAC', 'Calle Cusco 789', '977333444', 'importadorac@email.com', 'Carlos Ruiz', true),
('20456789012', 'Kiko', 'Av. Piura 321', '966444555', 'mayoristad@email.com', 'Ana Torres', true),
('20567890123', 'Isdin', 'Jr. Tacna 654', '955555666', 'proveedore@email.com', 'Luis Gómez', true);

INSERT INTO productos (codigo, nombre, descripcion, precio, imagen, activo, stock_actual, categoria_id, proveedor_id) VALUES
('MAQ001', 'Base Líquida', 'Base líquida para rostro', 45.00, NULL, true, 30, 1, 1),
('MAQ002', 'Labial Mate', 'Labial de larga duración', 35.00, NULL, true, 40, 1, 2),
('MAQ003', 'Máscara de Pestañas', 'Volumen extremo', 38.00, NULL, true, 25, 1, 3),
('MAQ004', 'Rubor Compacto', 'Rubor en polvo', 32.00, NULL, true, 20, 1, 4),
('MAQ005', 'Delineador Líquido', 'Delineador resistente al agua', 28.00, NULL, true, 35, 1, 1),
('MAQ006', 'Paleta de Sombras', '12 tonos nude', 65.00, NULL, true, 15, 1, 2),
('MAQ007', 'Corrector', 'Corrector líquido', 30.00, NULL, true, 18, 1, 3),
('MAQ008', 'Polvo Compacto', 'Polvo matificante', 33.00, NULL, true, 22, 1, 4),
('MAQ009', 'Iluminador', 'Iluminador en barra', 36.00, NULL, true, 12, 1, 1),
('MAQ010', 'Primer', 'Prebase facial', 40.00, NULL, true, 10, 1, 2),
('PIE001', 'Crema Hidratante', 'Hidratación profunda', 55.00, NULL, true, 25, 2, 5),
('PIE002', 'Serum Facial', 'Antioxidante y revitalizante', 70.00, NULL, true, 18, 2, 5),
('PIE003', 'Tónico Facial', 'Equilibra el pH', 38.00, NULL, true, 20, 2, 2),
('PIE004', 'Gel Limpiador', 'Limpieza suave', 42.00, NULL, true, 30, 2, 1),
('PIE005', 'Protector Solar', 'FPS 50', 60.00, NULL, true, 22, 2, 5),
('PIE006', 'Mascarilla Facial', 'Purificante', 35.00, NULL, true, 15, 2, 3),
('PIE007', 'Exfoliante', 'Exfoliante suave', 40.00, NULL, true, 12, 2, 4),
('PIE008', 'Contorno de Ojos', 'Reduce ojeras', 48.00, NULL, true, 10, 2, 1),
('PIE009', 'Agua Micelar', 'Desmaquillante', 32.00, NULL, true, 20, 2, 2),
('PIE010', 'Crema Antiarrugas', 'Reafirmante', 75.00, NULL, true, 8, 2, 5),
('CAB001', 'Shampoo Reparador', 'Para cabello dañado', 28.00, NULL, true, 30, 3, 2),
('CAB002', 'Acondicionador', 'Suavidad y brillo', 30.00, NULL, true, 25, 3, 3),
('CAB003', 'Mascarilla Capilar', 'Nutrición intensa', 45.00, NULL, true, 18, 3, 4),
('CAB004', 'Aceite Capilar', 'Argán y coco', 50.00, NULL, true, 12, 3, 5),
('CAB005', 'Spray Fijador', 'Fijación fuerte', 35.00, NULL, true, 20, 3, 1),
('CAB006', 'Crema para Peinar', 'Control de frizz', 32.00, NULL, true, 15, 3, 2),
('CAB007', 'Tónico Capilar', 'Estimula el crecimiento', 38.00, NULL, true, 10, 3, 3),
('CAB008', 'Shampoo Anticaspa', 'Elimina caspa', 29.00, NULL, true, 22, 3, 4),
('CAB009', 'Ampollas Capilares', 'Tratamiento intensivo', 60.00, NULL, true, 8, 3, 5),
('CAB010', 'Serum Capilar', 'Brillo instantáneo', 55.00, NULL, true, 6, 3, 1),
('UNA001', 'Esmalte Rojo', 'Color intenso', 18.00, NULL, true, 40, 4, 2),
('UNA002', 'Esmalte Nude', 'Color natural', 18.00, NULL, true, 35, 4, 3),
('UNA003', 'Removedor de Esmalte', 'Sin acetona', 15.00, NULL, true, 30, 4, 4),
('UNA004', 'Tratamiento Fortalecedor', 'Fortalece uñas', 22.00, NULL, true, 25, 4, 5),
('UNA005', 'Top Coat', 'Brillo y protección', 20.00, NULL, true, 20, 4, 1),
('UNA006', 'Base Coat', 'Protege la uña', 20.00, NULL, true, 18, 4, 2),
('UNA007', 'Lima de Uñas', 'Lima profesional', 10.00, NULL, true, 15, 4, 3),
('UNA008', 'Aceite para Cutículas', 'Nutre cutículas', 25.00, NULL, true, 12, 4, 4),
('UNA009', 'Kit de Manicure', 'Completo', 50.00, NULL, true, 10, 4, 5),
('UNA010', 'Pegamento para Uñas', 'Fijación segura', 12.00, NULL, true, 8, 4, 1),
('FRA001', 'Perfume Floral', 'Aroma fresco y floral', 120.00, NULL, true, 15, 5, 1),
('FRA002', 'Colonia Cítrica', 'Aroma cítrico', 85.00, NULL, true, 12, 5, 2),
('FRA003', 'Body Mist', 'Spray corporal', 60.00, NULL, true, 18, 5, 3),
('FRA004', 'Perfume Dulce', 'Aroma dulce y juvenil', 130.00, NULL, true, 10, 5, 4),
('FRA005', 'Colonia para Hombre', 'Aroma masculino', 95.00, NULL, true, 8, 5, 5),
('FRA006', 'Perfume Intenso', 'Aroma intenso y duradero', 140.00, NULL, true, 6, 5, 1),
('FRA007', 'Colonia Fresca', 'Aroma fresco', 80.00, NULL, true, 14, 5, 2),
('FRA008', 'Set de Fragancias', 'Miniaturas', 200.00, NULL, true, 5, 5, 3),
('FRA009', 'Perfume Frutal', 'Aroma frutal', 110.00, NULL, true, 9, 5, 4),
('FRA010', 'Colonia Infantil', 'Aroma suave', 70.00, NULL, true, 11, 5, 5);

INSERT INTO clientes (dni, nombre, apellido, direccion, telefono, email, activo, tipo, puntos_acumulados) VALUES
('12345678', 'Juan', 'Pérez', 'Av. Lima 123', '999111222', 'juan.perez@email.com', 1, 'regular', 10),
('87654321', 'María', 'García', 'Calle Sol 456', '988222333', 'maria.garcia@email.com', 1, 'vip', 50),
('11223344', 'Carlos', 'Ramírez', 'Jr. Luna 789', '977333444', 'carlos.ramirez@email.com', 1, 'regular', 5),
('44332211', 'Ana', 'Torres', 'Av. Mar 321', '966444555', 'ana.torres@email.com', 1, 'vip', 80),
('55667788', 'Luis', 'Soto', 'Calle Río 654', '955555666', 'luis.soto@email.com', 1, 'regular', 0),
('88776655', 'Elena', 'Mendoza', 'Jr. Sol 987', '944666777', 'elena.mendoza@email.com', 1, 'vip', 120),
('99887766', 'Pedro', 'Vargas', 'Av. Paz 159', '933777888', 'pedro.vargas@email.com', 1, 'regular', 15),
('66778899', 'Lucía', 'Flores', 'Calle Luna 753', '922888999', 'lucia.flores@email.com', 1, 'vip', 200),
('33445566', 'Miguel', 'Castro', 'Jr. Estrella 852', '911999000', 'miguel.castro@email.com', 1, 'regular', 7),
('66554433', 'Rosa', 'Reyes', 'Av. Sol 951', '900000111', 'rosa.reyes@email.com', 1, 'vip', 60);


INSERT INTO descuentos (nombre, tipo, valor, activo, fecha_inicio, fecha_fin, condicion_minima_precio, categoria_id, producto_id) VALUES
('Descuento 15% en Fragancias', 'PORCENTAJE', 15.00, true, '2024-12-01', '2025-12-31', NULL, 5, NULL),
('S/5 OFF en Cabello', 'FIJO', 5.00, true, '2024-12-01', '2025-12-31', NULL, 3, NULL),
('10% OFF en Maquillaje', 'PORCENTAJE', 10.00, true, '2024-12-01', '2025-12-31', NULL, 1, NULL),
('Black Friday - 30% OFF UÑAS', 'PORCENTAJE', 30.00, true, '2024-12-15', '2025-12-31', NULL, 4, NULL);
