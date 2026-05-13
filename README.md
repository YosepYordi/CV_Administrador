# CV_Administrador

Sistema web para la gestion de curriculum vitae de egresados y busqueda de talento empresarial.

## Tecnologias

- Java / Jakarta EE con Servlets y JSP
- Arquitectura MVC con patron DAO
- Maven
- MySQL 8 con Docker o instalacion local
- GlassFish 8 / Jakarta EE 11
- Bootstrap 5
- JUnit 5

## Requisitos

- JDK 25. En NetBeans puede usarse `C:\Program Files\Apache NetBeans\jdk`.
- MySQL 8. Puede levantarse con Docker Desktop o instalarse localmente con MySQL Server.
- MySQL Workbench es opcional y sirve como cliente grafico; por si solo no reemplaza a MySQL Server.
- Maven incluido en `apache-maven/` o una instalacion local compatible.

## Configuracion local

1. Copiar `src/main/resources/database.example.properties` como `src/main/resources/database.properties`.
2. Ajustar usuario, contrasena, correo administrador, contrasena administrador y dominio institucional.
3. Configurar un `admin.password` fuerte. No usar `admin123`, `change_me`, `password` ni otros valores por defecto; la aplicacion rechazara esos valores al arrancar.

### Opcion A: MySQL con Docker

Levantar la base de datos:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

### Opcion B: MySQL local con Workbench, sin Docker

Workbench no levanta la base de datos por si solo. Primero debe estar instalado y corriendo MySQL Server.

1. Instalar MySQL Server 8 y MySQL Workbench.
2. Crear un usuario o usar el usuario local de MySQL que tenga permisos para crear bases de datos.
3. En `src/main/resources/database.properties`, configurar algo parecido a:

```properties
db.url=jdbc:mysql://localhost:3306/CV_Administrador?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8
db.username=TU_USUARIO_MYSQL
db.password=TU_PASSWORD_MYSQL
admin.email=admin@instituto.edu.pe
admin.password=AdminClave123!
```

4. Si el usuario MySQL tiene permiso `CREATE DATABASE`, la aplicacion puede crear la base y tablas al arrancar.
5. Si el usuario no tiene permiso para crear la base desde la app, crear la base `CV_Administrador` en Workbench y ejecutar el script `src/main/resources/schema.sql`.

## Credenciales de administrador

No hay una contrasena admin fija en el repositorio. La contrasena del admin inicial es el valor configurado en:

- `admin.password` dentro de `src/main/resources/database.properties`, o
- la variable de entorno `ADMIN_PASSWORD`.

Para desarrollo local del equipo, pueden usar:

```properties
admin.email=admin@instituto.edu.pe
admin.password=AdminClave123!
```

El archivo `database.properties` no se sube a Git, asi que cada integrante debe copiar `database.example.properties`, poner esos valores localmente y arrancar la app. El correo admin inicial sale de `admin.email` o `ADMIN_EMAIL`. Si ya existia un admin creado con una clave insegura conocida, al arrancar con un `admin.password` fuerte la aplicacion rota esa clave al nuevo valor configurado.

## Comandos utiles

Ejecutar pruebas:

```powershell
$env:JAVA_HOME='C:\Program Files\Apache NetBeans\jdk'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\apache-maven\bin\mvn.cmd test
```

Generar el WAR:

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
