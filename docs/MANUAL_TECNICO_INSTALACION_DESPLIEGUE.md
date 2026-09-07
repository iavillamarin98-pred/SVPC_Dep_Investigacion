# Manual Técnico de Instalación y Despliegue — SVPC

## Sistema de Valoración de Producción Científica

**Proyecto:** SVPC  
**Repositorio:** `iavillamarin98-pred/SVPC_Dep_Investigacion`  
**Rama documentada:** `main`  
**Versión del manual:** Septiembre de 2026

---

## 1. Objetivo

Este documento describe la arquitectura técnica, requisitos, compilación, instalación, configuración, despliegue, inicialización de PostgreSQL, verificación y mantenimiento del SVPC.

---

## 2. Arquitectura general

El SVPC está implementado como una aplicación web monolítica.

### Frontend

- HTML.
- CSS.
- JavaScript.
- Recursos estáticos servidos por Spring Boot.

### Backend

- Java 21.
- Spring Boot.
- Spring MVC.
- Spring Data JPA.
- Spring Security.
- Bean Validation.

### Persistencia

- PostgreSQL.

### Procesamiento de archivos

- Apache POI para archivos Excel.

### Contenedores

- Docker.
- Docker Compose.

---

## 3. Estructura principal del repositorio

La estructura general del proyecto es:

```text
SVPC_Dep_Investigacion/
├── .mvn/
├── postgres/
│   └── init/
├── src/
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── svpc_db_limpia.sql
```

El archivo `svpc_db_limpia.sql` contiene la estructura necesaria para reconstruir la base de datos utilizada por el sistema.

---

## 4. Requisitos previos

Para ejecutar el sistema mediante Docker se recomienda disponer de:

- Git.
- Docker Engine o Docker Desktop.
- Docker Compose v2.
- JDK 21.
- Acceso a Internet para descargar dependencias e imágenes.
- Puerto `8080` disponible.
- Puerto `5432` disponible si PostgreSQL será publicado en el host.

No es obligatorio instalar Maven de forma global porque el proyecto incluye Maven Wrapper.

---

## 5. Clonar el repositorio

Ejecutar:

```bash
git clone https://github.com/iavillamarin98-pred/SVPC_Dep_Investigacion.git
cd SVPC_Dep_Investigacion
```

Comprobar:

```bash
git status
```

---

## 6. Compilar el proyecto

Antes de construir la imagen Docker debe generarse el archivo JAR.

### Linux

```bash
./mvnw clean package
```

### Windows PowerShell

```powershell
.\mvnw.cmd clean package
```

Al finalizar debe existir un archivo similar a:

```text
target/svpc-0.0.1-SNAPSHOT.jar
```

Si la compilación falla, no continuar hasta solucionar el error.

---

## 7. Construcción de la imagen Docker

Ejecutar desde la raíz:

```bash
docker build -t svpc-svpc:latest .
```

Comprobar:

```bash
docker images
```

Debe existir una imagen similar a:

```text
svpc-svpc   latest
```

> El `docker-compose.yml` utiliza la imagen `svpc-svpc:latest`, por lo que la imagen debe construirse previamente si no existe una sección `build:` en el servicio de la aplicación.

---

## 8. Servicios Docker

El entorno define principalmente dos servicios.

### PostgreSQL

Responsable de almacenar la información persistente del sistema.

Características habituales:

- Contenedor PostgreSQL.
- Base de datos `svpc_dbv1`.
- Puerto interno `5432`.
- Volumen persistente.
- Verificación de salud mediante `pg_isready`.

### Aplicación SVPC

Responsable de ejecutar Spring Boot.

Características:

- Imagen `svpc-svpc:latest`.
- Puerto interno `8080`.
- Dependencia de PostgreSQL.

---

## 9. Inicialización de la base de datos

La instalación debe garantizar que la base de datos tenga previamente todas las tablas requeridas.

El proyecto utiliza configuración equivalente a:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Esto significa que Hibernate no debe considerarse responsable de crear automáticamente el esquema.

Por esta razón se debe restaurar explícitamente:

```text
svpc_db_limpia.sql
```

---

## 10. Levantar PostgreSQL

Ejecutar:

```bash
docker compose up -d postgres
```

Verificar:

```bash
docker compose ps
```

Revisar logs:

```bash
docker logs svpc-postgres
```

Esperar hasta que PostgreSQL se encuentre disponible.

---

## 11. Restaurar la base de datos

### Linux o Git Bash

```bash
docker exec -i svpc-postgres \
  psql -U postgres -d svpc_dbv1 < svpc_db_limpia.sql
```

### Windows PowerShell

```powershell
cmd /c "docker exec -i svpc-postgres psql -U postgres -d svpc_dbv1 < svpc_db_limpia.sql"
```

Verificar las tablas:

```bash
docker exec -it svpc-postgres \
  psql -U postgres -d svpc_dbv1 -c "\dt"
```

---

## 12. Usuarios iniciales

El proyecto puede incluir scripts dentro de:

```text
postgres/init/
```

para insertar usuarios iniciales o información de bootstrap.

Si fuera necesario ejecutar uno:

```bash
docker exec -i svpc-postgres \
  psql -U postgres -d svpc_dbv1 < postgres/init/01-usuarios.sql
```

### Recomendaciones

- No documentar contraseñas reales.
- Cambiar credenciales predeterminadas.
- No dejar contraseñas en texto plano.
- Utilizar hashes seguros.
- Evitar publicar credenciales operativas en GitHub.

---

## 13. Iniciar la aplicación

Una vez restaurada la base:

```bash
docker compose up -d svpc
```

También se puede iniciar todo el entorno con:

```bash
docker compose up -d
```

Verificar:

```bash
docker compose ps
```

Resultado esperado:

```text
svpc-postgres   Up / Healthy
svpc-app        Up
```

---

## 14. Revisar logs

### Aplicación

```bash
docker logs -f svpc-app
```

### PostgreSQL

```bash
docker logs -f svpc-postgres
```

Para salir de la vista continua:

```text
Ctrl + C
```

Esto no detiene los contenedores.

---

## 15. Verificar el despliegue

Abrir en el navegador:

```text
http://localhost:8080/login.html
```

También puede utilizarse:

```bash
curl -I http://localhost:8080/login.html
```

Si la aplicación responde correctamente, el despliegue básico está operativo.

---

## 16. Persistencia

PostgreSQL debe utilizar un volumen persistente.

Esto permite conservar los datos aunque los contenedores sean detenidos o recreados.

El comando:

```bash
docker compose down
```

detiene y elimina los contenedores, pero normalmente mantiene los volúmenes.

---

## 17. Detener el sistema

```bash
docker compose down
```

---

## 18. Reiniciar servicios

### Reiniciar la aplicación

```bash
docker compose restart svpc
```

### Reiniciar todo el entorno

```bash
docker compose restart
```

---

## 19. Actualizar el sistema

Después de modificar el código:

```bash
git pull
```

Compilar nuevamente.

### Linux

```bash
./mvnw clean package
```

### Windows

```powershell
.\mvnw.cmd clean package
```

Reconstruir la imagen:

```bash
docker build -t svpc-svpc:latest .
```

Recrear el contenedor:

```bash
docker compose up -d --force-recreate svpc
```

Revisar logs:

```bash
docker logs -f svpc-app
```

---

## 20. Copia de seguridad de PostgreSQL

Antes de actualizaciones importantes se recomienda generar una copia.

### Linux

```bash
docker exec svpc-postgres \
  pg_dump -U postgres -d svpc_dbv1 > backup_svpc.sql
```

### Windows PowerShell

```powershell
cmd /c "docker exec svpc-postgres pg_dump -U postgres -d svpc_dbv1 > backup_svpc.sql"
```

Guardar el archivo en una ubicación segura.

---

## 21. Eliminación completa del entorno

El siguiente comando elimina también los volúmenes:

```bash
docker compose down -v
```

> **Advertencia:** esta operación puede eliminar los datos persistentes de PostgreSQL.

Utilizarla únicamente cuando:

- se desea reconstruir completamente el entorno; o
- existe una copia de seguridad válida; o
- los datos actuales pueden descartarse.

---

## 22. Configuración de conexión

La aplicación puede utilizar una configuración similar a:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://postgres:5432/svpc_dbv1
spring.jpa.hibernate.ddl-auto=none
```

Dentro de Docker Compose, `postgres` corresponde al nombre del servicio de base de datos.

---

## 23. Variables de entorno

Para un entorno productivo se recomienda no almacenar credenciales directamente en archivos versionados.

Ejemplo:

```properties
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

Variables equivalentes pueden definirse en Docker Compose.

Ejemplo:

```yaml
environment:
  DB_USER: ${DB_USER}
  DB_PASSWORD: ${DB_PASSWORD}
```

Los valores reales pueden almacenarse en un archivo `.env` excluido mediante `.gitignore`.

---

## 24. Seguridad

Antes de publicar el sistema en producción se recomienda revisar los siguientes puntos.

### 24.1 Credenciales

- No incluir contraseñas reales en GitHub.
- Rotar cualquier credencial previamente publicada.
- Utilizar variables de entorno o secretos.

### 24.2 Autorización por roles

Las funciones administrativas deben restringirse en el backend.

No es suficiente ocultar opciones en el frontend.

Ejemplos de mecanismos de Spring Security:

```java
hasRole("ADMIN")
```

o:

```java
hasAuthority("GESTIONAR_PROCESOS")
```

según el modelo de permisos definido.

### 24.3 Protección CSRF

Si la aplicación utiliza sesiones y formularios, debe evaluarse mantener habilitada la protección CSRF en producción.

### 24.4 Información de errores

En producción no se recomienda exponer:

- Stack traces.
- Consultas SQL.
- Detalles internos de excepciones.
- Información sensible de configuración.

### 24.5 PostgreSQL

Si solo la aplicación necesita conectarse a PostgreSQL, no es obligatorio publicar el puerto `5432` hacia Internet.

### 24.6 HTTPS

Para producción se recomienda utilizar un proxy inverso:

```text
Internet / Red institucional
          ↓
       HTTPS :443
          ↓
   Nginx / Apache
          ↓
      SVPC :8080
          ↓
     PostgreSQL
```

---

## 25. CI/CD

Si el repositorio no dispone todavía de workflows de CI/CD, el despliegue descrito en este documento corresponde a un procedimiento manual.

Una futura automatización puede utilizar un flujo como:

```text
Push
 ↓
Compilación Maven
 ↓
Pruebas
 ↓
Construcción de imagen Docker
 ↓
Publicación en registry
 ↓
Despliegue
```

---

## 26. Diagnóstico rápido

### La aplicación no inicia

Ejecutar:

```bash
docker logs svpc-app
```

Comprobar:

- El JAR fue generado.
- La imagen Docker existe.
- PostgreSQL está disponible.
- La base de datos contiene las tablas requeridas.

### Error `relation ... does not exist`

La base probablemente no fue restaurada.

Restaurar:

```text
svpc_db_limpia.sql
```

### No existe la imagen `svpc-svpc:latest`

Construir:

```bash
docker build -t svpc-svpc:latest .
```

### Puerto `8080` ocupado

Identificar el proceso que utiliza el puerto o modificar el mapeo Docker.

### Puerto `5432` ocupado

Puede existir una instancia local de PostgreSQL.

Si no se requiere acceso desde el host, puede evitarse publicar ese puerto.

---

## 27. Checklist de despliegue

### Instalación

- [ ] Repositorio clonado.
- [ ] Java 21 disponible.
- [ ] Docker operativo.
- [ ] Docker Compose operativo.
- [ ] Proyecto compilado.
- [ ] JAR generado.
- [ ] Imagen `svpc-svpc:latest` construida.
- [ ] PostgreSQL iniciado.
- [ ] Base `svpc_dbv1` restaurada.
- [ ] Tablas verificadas.
- [ ] Usuarios iniciales configurados.
- [ ] Aplicación iniciada.
- [ ] PostgreSQL saludable.
- [ ] Login accesible.

### Producción

- [ ] Credenciales fuera del repositorio.
- [ ] Contraseñas predeterminadas rotadas.
- [ ] Autorización por roles revisada.
- [ ] Protección CSRF revisada.
- [ ] Stack traces desactivados.
- [ ] Logs configurados para producción.
- [ ] PostgreSQL no expuesto innecesariamente.
- [ ] HTTPS habilitado.
- [ ] Backups periódicos configurados.

---

## 28. Conclusión

El SVPC puede desplegarse mediante Java 21, PostgreSQL y Docker.

La instalación requiere tres pasos principales:

```text
Compilar aplicación
       ↓
Construir imagen Docker
       ↓
Restaurar PostgreSQL y levantar servicios
```

Antes de utilizar el sistema en un entorno institucional o público se recomienda reforzar la gestión de credenciales, autorización por roles, protección de formularios, manejo de errores, logs y acceso a la base de datos.
