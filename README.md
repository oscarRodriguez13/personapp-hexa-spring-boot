# PersonApp

Este proyecto es una implementación de una **aplicación de gestión de personas** desarrollada con **arquitectura hexagonal** (puertos y adaptadores), usando **Spring Boot** y desplegada con **Docker**. Cuenta con adaptadores de entrada vía **REST API** y **CLI**, y persistencia dual en **MariaDB** y **MongoDB**.

Los desarrolladores de este sistema son:

- Oscar Alejandro Rodriguez Gómez
- Juan Felipe Gonzalez Quintero
- Andres Felipe Ruge Passito

---

## 💡 Stack tecnológico

- Java JDK 11
- SpringBoot
- Docker
- REST
- CLI
- MongoDB
- MariaBD

## 📦 Características principales

- Arquitectura hexagonal desacoplada (Domain, Application, Ports, Adapters)
- Adaptadores de entrada: API REST y CLI interactivo
- Adaptadores de salida: MariaDB (relacional), MongoDB (NoSQL)
- Contenedores Docker para API REST y BDs
- Uso de Swagger UI para documentación y prueba de endpoints REST

---

## 🚀 Ejecución del Proyecto (Docker Desktop para Windows)

Debe tenerse instalado y en funcionamiento:

- Docker Desktop
- Git
- Puertos `3000`, `3307` y `27017` libres

### 1️⃣ Levantar todos los servicios (REST, DBs, Swagger, etc.)

Desde la raíz del proyecto, ejecutar:

```bash
docker-compose up --build
