# Minimarket Plus - Backend

Sistema backend para la gestión de un minimarket, desarrollado con Spring Boot. Administra productos, inventario, ventas y usuarios con autenticación y control de acceso basado en roles.

## Tecnologías

- Java 17
- Spring Boot 3.4.1
- Spring Security + JWT
- Spring Data JPA
- H2 Database (en memoria)
- JUnit 5 + Mockito
- JaCoCo (cobertura de pruebas)
- Maven

## Funcionalidades

- Autenticación stateless mediante JWT
- Control de acceso por roles: `ROLE_ADMINISTRADOR`, `ROLE_EMPLEADO`, `ROLE_CLIENTE`
- Gestión de productos, categorías e inventario
- Gestión de ventas con detalle de productos
- Carrito de compras

## Seguridad

- Autenticación mediante JSON Web Tokens (JWT)
- Contraseñas hasheadas con BCrypt
- Clave secreta JWT externalizada mediante variable de entorno (`JWT_SECRET`)
- Reglas de acceso diferenciadas por rol en `SecurityConfig`

## Pruebas unitarias

El proyecto cuenta con 55 pruebas unitarias usando JUnit 5 y Mockito, cubriendo las entidades `Producto`, `Inventario`, `Venta`, `Usuario` y el componente `JwtUtil`.

## Informe JaCoCo

Cobertura total de 51% en instrucciones, destacando 100% en las clases `ProductoServiceImpl`, `InventarioServiceImpl` y `VentaServiceImpl`, y 92% en `JwtUtil`.

## Autor

Rafael Navarrete Morales - Estudiante Analista Programador Computacional, Duoc UC