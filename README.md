# CV_Administrador

Sistema web para la gestion de curriculum vitae de egresados y busqueda de talento empresarial.

## Tecnologias

- Java / Jakarta EE con Servlets y JSP
- Arquitectura MVC con patron DAO
- Maven
- MySQL 8 en Docker
- GlassFish 8 / Jakarta EE 11
- Bootstrap 5
- JUnit 5

## Requisitos

- JDK 25. En NetBeans puede usarse `C:\Program Files\Apache NetBeans\jdk`.
- Docker Desktop para levantar MySQL.
- Maven incluido en `apache-maven/` o una instalacion local compatible.

## Configuracion local

1. Copiar `src/main/resources/database.example.properties` como `src/main/resources/database.properties`.
2. Ajustar usuario, contrasena, correo administrador y dominio institucional.
3. Levantar la base de datos:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

4. Ejecutar pruebas:

```powershell
$env:JAVA_HOME='C:\Program Files\Apache NetBeans\jdk'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\apache-maven\bin\mvn.cmd test
```

5. Generar el WAR:

```powershell
.\apache-maven\bin\mvn.cmd clean package
```

El artefacto queda en `target/cvmanager.war`.

## Modulos principales

- Autenticacion, registro, recuperacion de contrasena y filtros por rol.
- Perfil de egresado y configuracion de privacidad.
- Gestion de CV estructurado con educacion, experiencia, habilidades, idiomas y certificaciones.
- Busqueda empresarial con filtros por carrera, ciudad, habilidad, idioma, experiencia y palabra clave.
- Panel administrativo con usuarios, carreras, metricas, reportes y auditoria.
- Panel de empresas con favoritos y solicitudes de contacto.

## Documentacion

La monografia del proyecto esta incluida en `Monografia_CV_Manager_APA7.docx`.
