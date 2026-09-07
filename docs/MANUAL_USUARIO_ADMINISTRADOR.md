# Manual de Usuario y Administrador — SVPC

## Sistema de Valoración de Producción Científica

**Proyecto:** SVPC — Sistema de Valoración de Producción Científica  
**Repositorio:** `iavillamarin98-pred/SVPC_Dep_Investigacion`  
**Rama documentada:** `main`  
**Versión del manual:** Septiembre de 2026

---

## 1. Introducción

El Sistema de Valoración de Producción Científica (SVPC) es una aplicación web orientada a la gestión, valoración y consulta de la producción científica de los docentes.

El sistema permite administrar procesos de valoración, actualizar docentes, importar producción científica, registrar proyectos, configurar puntajes y consultar rankings consolidados.

---

## 2. Objetivo del manual

Este manual describe el uso funcional del SVPC desde la perspectiva de un usuario autorizado o administrador.

Las principales actividades cubiertas son:

1. Iniciar y cerrar sesión.
2. Gestionar procesos de valoración.
3. Actualizar docentes.
4. Configurar puntajes y reglas.
5. Importar producción científica.
6. Administrar proyectos de investigación.
7. Registrar bonificaciones por propiedad intelectual.
8. Calcular puntajes.
9. Consultar y exportar rankings.

---

## 3. Acceso al sistema

El sistema se utiliza mediante un navegador web.

En una instalación local, la dirección predeterminada es:

```text
http://localhost:8080/login.html
```

En un servidor institucional se debe utilizar el dominio o dirección configurada por el administrador.

### Inicio de sesión

La pantalla de acceso solicita:

- Correo institucional.
- Contraseña.

Después de ingresar las credenciales, seleccionar **Iniciar sesión**.

Si las credenciales son incorrectas, el sistema mostrará el mensaje correspondiente.

> Las credenciales iniciales deben ser proporcionadas por el responsable técnico. No se deben almacenar contraseñas reales en este manual.

---

## 4. Pantalla principal

Después de iniciar sesión se presenta la interfaz principal del SVPC.

En la parte superior se muestra información como:

- Nombre del sistema.
- Institución.
- Nombre del usuario autenticado.
- Rol del usuario.
- Opción para cerrar sesión.

El menú principal contiene las siguientes áreas.

### 4.1 Dashboard

Presenta información general del sistema y funciona como pantalla principal.

### 4.2 Procesos de Valoración

Permite crear y administrar los períodos en los que se realizará la valoración de la producción científica.

### 4.3 Producción Científica

Incluye módulos para:

- Artículos.
- Proceedings.
- Libros.
- Capítulos de libro.
- Proyectos.
- Propiedad Intelectual.

### 4.4 Resultados

Permite consultar:

- Ranking General.
- Ranking por Carrera o agrupación institucional, según la vista disponible.

### 4.5 Sistema

Incluye funciones como:

- Gestión de Docentes.
- Configuración de Puntajes.

---

## 5. Flujo recomendado para el administrador

Para mantener consistencia en la información, se recomienda trabajar en el siguiente orden:

```text
Proceso de valoración
        ↓
Gestión de docentes
        ↓
Configuración de puntajes
        ↓
Importación de producción científica
        ↓
Cálculo de puntajes
        ↓
Actualización y revisión del ranking
```

---

## 6. Crear un proceso de valoración

Ingresar a:

```text
Procesos de Valoración → Crear proceso
```

La pantalla permite registrar un nuevo proceso y revisar los procesos existentes.

Los datos principales son:

- Nombre.
- Período.
- Descripción.
- Estado.

El estado puede utilizarse para determinar qué proceso se encuentra activo.

### Procedimiento

1. Ingresar el nombre del proceso.
2. Especificar el período.
3. Añadir una descripción.
4. Seleccionar el estado correspondiente.
5. Guardar.
6. Verificar que el proceso requerido aparezca correctamente registrado.

Antes de importar o calcular información, se debe confirmar que se está trabajando con el proceso correcto.

---

## 7. Gestión de docentes

Ingresar a:

```text
Sistema → Gestión de Docentes
```

Este módulo permite actualizar el listado de docentes mediante archivos Excel.

### Actualizar docentes

1. Seleccionar **Gestión de Docentes**.
2. Seleccionar el archivo Excel.
3. Ejecutar la opción de importación o actualización.
4. Revisar el resultado mostrado por el sistema.
5. Consultar el listado actualizado.

La tabla de docentes puede mostrar información como:

- Cédula.
- Nombre del docente.
- Facultad.
- Carrera.
- Estado.

También pueden estar disponibles filtros por nombre, cédula o facultad.

---

## 8. Configuración de puntajes

Ingresar a:

```text
Sistema → Configuración de Puntajes
```

Esta sección define las reglas utilizadas para calcular la valoración de la producción científica.

La configuración debe estar asociada al proceso de valoración correspondiente.

Entre las configuraciones disponibles pueden incluirse:

- Ponderaciones.
- Categorías.
- Criterios.
- Roles.
- Reglas de distribución de puntaje entre autores.

### Recomendación

La configuración debe completarse antes de ejecutar cálculos definitivos.

Cambiar una regla después de calcular resultados puede requerir volver a ejecutar el proceso de cálculo.

---

## 9. Artículos científicos

Ingresar a:

```text
Producción Científica → Artículos
```

Este módulo permite importar información relacionada con publicaciones científicas.

### Flujo general

1. Confirmar el proceso de valoración.
2. Seleccionar el archivo correspondiente.
3. Ejecutar la importación.
4. Revisar los registros importados.
5. Ejecutar el cálculo cuando corresponda.
6. Actualizar el ranking.
7. Verificar los puntajes obtenidos.

---

## 10. Proceedings

Ingresar a:

```text
Producción Científica → Proceedings
```

Procedimiento general:

1. Seleccionar el proceso correspondiente.
2. Importar el archivo.
3. Validar los registros.
4. Ejecutar el cálculo.
5. Revisar resultados.

---

## 11. Libros

Ingresar a:

```text
Producción Científica → Libros
```

Este módulo permite registrar o importar información relacionada con libros.

Después de importar los datos se recomienda comprobar los registros antes de ejecutar el cálculo de puntaje.

---

## 12. Capítulos de libro

Ingresar a:

```text
Producción Científica → Capítulos de Libro
```

El flujo recomendado es:

```text
Importación → Validación → Cálculo → Actualización del ranking
```

---

## 13. Proyectos de investigación

Ingresar a:

```text
Producción Científica → Proyectos
```

El módulo permite gestionar proyectos de investigación.

Dependiendo de la pantalla disponible, pueden existir opciones para:

- Registro.
- Importación.
- Listado.
- Ranking.

### Registro manual

Completar la información solicitada del proyecto, por ejemplo:

- Nombre.
- Tipo de financiamiento.
- Período.
- Estado.
- Descripción.
- Participantes.

Los participantes deben asociarse al proyecto con el rol correspondiente.

### Importación

También puede utilizarse un archivo Excel para cargar proyectos de forma masiva.

### Listado

Permite consultar los proyectos registrados o importados.

### Ranking

Permite consultar el puntaje de los docentes en esta categoría.

---

## 14. Propiedad intelectual

Ingresar a:

```text
Producción Científica → Propiedad Intelectual
```

Este módulo permite registrar bonificaciones o puntajes asociados con propiedad intelectual.

### Procedimiento general

1. Seleccionar al docente.
2. Verificar su información.
3. Seleccionar el criterio correspondiente.
4. Definir el puntaje permitido.
5. Guardar la bonificación.
6. Verificar posteriormente su incorporación al ranking.

---

## 15. Ranking General

Ingresar a:

```text
Resultados → Ranking General
```

Esta pantalla consolida los puntajes obtenidos por los docentes.

Puede incluir categorías como:

- Artículos.
- Proceedings.
- Libros.
- Capítulos de libro.
- Proyectos.
- Bonificaciones.
- Puntaje total.

### Uso recomendado

1. Completar las importaciones.
2. Ejecutar los cálculos requeridos.
3. Seleccionar **Actualizar ranking**.
4. Buscar al docente si se requiere una comprobación individual.
5. Revisar los valores por categoría.
6. Verificar el puntaje total.
7. Exportar los resultados cuando hayan sido validados.

---

## 16. Ranking por carrera o agrupación institucional

La segunda vista de resultados permite analizar la clasificación utilizando filtros o agrupaciones institucionales.

Se recomienda utilizar los filtros disponibles antes de comparar resultados entre unidades académicas diferentes.

---

## 17. Cierre de sesión

Para finalizar la sesión:

1. Ubicar el usuario en la parte superior de la interfaz.
2. Seleccionar la opción **Cerrar sesión**.
3. El sistema regresará a la pantalla de acceso.

---

## 18. Buenas prácticas para el administrador

- Verificar siempre el proceso activo antes de importar información.
- Realizar copias de seguridad antes de importaciones masivas.
- Revisar el formato de los archivos Excel.
- Configurar las ponderaciones antes de calcular.
- Evitar importar archivos duplicados sin verificar la información existente.
- Validar los resultados antes de generar el ranking definitivo.
- No compartir credenciales.
- Cerrar sesión al trabajar en equipos compartidos.

---

## 19. Problemas frecuentes

### No puedo iniciar sesión

Comprobar:

- Correo institucional.
- Contraseña.
- Disponibilidad del servidor.
- Estado del usuario en la base de datos.

### La importación Excel falla

Comprobar:

- Formato del archivo.
- Extensión permitida.
- Estructura de columnas.
- Integridad del archivo.
- Proceso de valoración seleccionado.
- Tamaño máximo permitido.

### El ranking no refleja nuevos datos

1. Verificar que la importación haya finalizado correctamente.
2. Ejecutar nuevamente el cálculo correspondiente.
3. Seleccionar **Actualizar ranking**.

### Una opción administrativa no aparece

Algunas funciones pueden encontrarse deshabilitadas o no expuestas en el menú en la versión actual.

---

## 20. Consideración sobre roles y permisos

El sistema contempla usuarios y roles, pero la seguridad de backend debe revisarse para asegurar que las funciones administrativas se encuentren restringidas de forma explícita.

Ocultar una opción en la interfaz no sustituye una autorización real en el servidor.

---

## 21. Fin del manual

Este documento debe actualizarse cuando se incorporen nuevos módulos, permisos, roles o cambios en los procesos de valoración.
