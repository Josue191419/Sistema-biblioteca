# Sistema Biblioteca

Proyecto Java con Maven para gestion de biblioteca por consola, con arquitectura por capas y soporte de persistencia en MySQL.

## Tecnologias

- Java 17
- Maven
- MySQL 8+
- JDBC (`mysql-connector-j`)
- Gson (consumo de API de Open Library)

## Arquitectura del proyecto

La aplicacion esta organizada por capas:

- `model`: entidades de dominio (`Libro`, `Usuario`, `Prestamo`, `Autor`, `Bibliotecario`, `Biblioteca`).
- `repository`: contratos e implementaciones de persistencia (memoria/MySQL).
- `service`: reglas de negocio y validaciones.
- `controller`: flujo de cada modulo del sistema.
- `view`: menus y entrada/salida en consola.
- `misc`: utilidades (por ejemplo, conexion JDBC).

Ruta base del codigo:

- `src/main/java/org/example`

## Configuracion de base de datos (MySQL)

El archivo de conexion esta en:

- `src/main/java/org/example/misc/Conexion.java`

Ejemplo de valores esperados:

```java
private static final String URL =
		"jdbc:mysql://127.0.0.1:3306/biblioteca?useSSL=false&serverTimezone=UTC";
private static final String USUARIO = "root";
private static final String PASSWORD = "191419";
```

## Script de base de datos

Ejecuta este script en MySQL Workbench:

```sql
CREATE DATABASE IF NOT EXISTS biblioteca
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE biblioteca;

CREATE TABLE IF NOT EXISTS autor (
  id INT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  nacionalidad VARCHAR(100),
  fecha_nacimiento DATE
);

CREATE TABLE IF NOT EXISTS bibliotecario (
  id INT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  turno VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS biblioteca_entidad (
  id INT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  direccion VARCHAR(255),
  telefono VARCHAR(100),
  capacidad INT
);

CREATE TABLE IF NOT EXISTS usuario (
  id INT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  identificacion VARCHAR(100),
  tipo VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS libro (
  id INT PRIMARY KEY,
  titulo VARCHAR(255) NOT NULL,
  autor VARCHAR(255) NOT NULL,
  editora VARCHAR(255),
  fecha_publicacion DATE,
  disponible BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS prestamo (
  id INT PRIMARY KEY,
  libro_id INT NOT NULL,
  usuario_id INT NOT NULL,
  autor_id INT,
  bibliotecario_id INT,
  biblioteca_id INT,
  fecha_prestamo DATE NOT NULL,
  fecha_devolucion DATE,
  CONSTRAINT fk_prestamo_libro FOREIGN KEY (libro_id) REFERENCES libro(id)
	ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_prestamo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
	ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_prestamo_autor FOREIGN KEY (autor_id) REFERENCES autor(id)
	ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_prestamo_bibliotecario FOREIGN KEY (bibliotecario_id) REFERENCES bibliotecario(id)
	ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_prestamo_biblioteca FOREIGN KEY (biblioteca_id) REFERENCES biblioteca_entidad(id)
	ON UPDATE CASCADE ON DELETE SET NULL
);
```

## Compilar y ejecutar

Desde la raiz del proyecto (`Biblioteca01`):

```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Si tu equipo no reconoce `mvn`, instala Maven y agrega su carpeta `bin` al `PATH` del sistema.

## Interfaz JavaFX

Se agrego una interfaz grafica con pestañas para `Libros`, `Usuarios`, `Prestamos`, `Biblioteca` y `Bibliotecarios`,
incluyendo busqueda de libros en Open Library desde el modulo de libros.

Para ejecutar JavaFX:

```powershell
mvn clean javafx:run
```

La app JavaFX inicia en `org.example.MainFx`.

## Flujo funcional general

1. Registrar libros.
2. Registrar usuarios.
3. Registrar autores y bibliotecarios.
4. Crear prestamos (incluye fechas y relaciones).
5. Consultar listados desde cada modulo.

## Open Library API

El proyecto incluye soporte para buscar libros desde Open Library y usar esos datos en el registro.

## Notas importantes

- En base de datos, las fechas `DATE` se guardan como `AAAA-MM-DD`.
- Para mostrar solo el ano, se formatea en la vista/controlador, no en la columna SQL.
- Evita subir credenciales reales a repositorios publicos.

## Autor

Proyecto academico de gestion bibliotecaria 

