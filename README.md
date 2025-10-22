
# Sistema POS - Punto de Venta

Este proyecto es un sistema de punto de venta (POS) para comercios, desarrollado en Java Spring Boot y Thymeleaf. Permite gestionar ventas, clientes, productos, descuentos, medios de pago, reportes y más. Incluye un sistema de puntos para fidelización de clientes.

---

## 🚀 Características principales

- Gestión de ventas y clientes (con roles ADMIN y CAJA)
- Registro y listado de productos, categorías, proveedores y locales
- Aplicación de descuentos y promociones
- Sistema de puntos para clientes frecuentes
- Reportes y control de caja
- Seguridad y control de acceso por roles

---

## ▶️ ¿Cómo ejecutar el proyecto?

1. **Requisitos previos:**
	- Java 21 o superior
	- Maven 3.8+ instalado

2. **Clona el repositorio y entra a la carpeta del proyecto:**
	```powershell
	git clone <url-del-repo>
	cd pos-system-feature-caja-descuentos-historialClientes
	```

3. **Configura la base de datos:**  
	El sistema usa H2/MySQL (ver `application.properties`).  
	Ajusta los datos de conexión si es necesario.

4. **Compila y ejecuta:**
	```powershell
	mvn clean spring-boot:run
	```

5. **Accede desde el navegador:**  
	[http://localhost:7000/](http://localhost:7000/)

6. **Usuarios de prueba:**  
	- ADMIN: administrador / password 
	- CAJA: cajero / password

---

## � Estructura de carpetas

- `src/main/java/com/cibertec/pos_system/controller/` — Controladores web
- `src/main/resources/templates/` — Vistas Thymeleaf
- `src/main/resources/application.properties` — Configuración
- `pom.xml` — Dependencias y plugins

---

## � Módulos y vistas principales

| Módulo o vista                      | Propósito principal                        |
|-------------------------------------|--------------------------------------------|
| `VentaController.java`              | Controla las rutas de ventas y vista general|
| `VentaService.java`                 | Lógica de puntos y registro de venta        |
| `ClienteEntity.java`                | Define tipo de cliente y puntos acumulados  |
| `ventas/venta-form.html`            | Formulario para registrar ventas            |
| `ventas/ventas-lista.html`          | Lista de ventas registradas                 |
| `cliente/lista.html`                | Vista de todos los clientes                 |
| `cliente/perfil.html`               | Perfil individual del cliente               |
| `ventas/preciosDescuentos.html`     | Pantalla de funciones del módulo            |

---

## 👥 ¿Para quién está hecho esto?

Este sistema está pensado para:

- Comercios que desean fidelizar clientes con un sistema de puntos y descuentos.
- Administradores que quieren tener control claro sobre promociones y beneficios.
- Equipos técnicos que necesitan integrar funciones sin afectar lo ya construido.

---

## ✨ Próximos pasos

El sistema está preparado para crecer:

- Integrar con el módulo de productos reales.
- Implementar descuentos automáticos según promociones.
- Exportar reportes de puntos y ventas.

---

🧑‍💻 Desarrollado con amor y lógica modular.  
Si tienes dudas o mejoras, no dudes en escribirme 💬.
