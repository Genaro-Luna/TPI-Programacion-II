# Sistema de Gestión de Usurios y Credenciales de acceso

## Trabajo Práctico Integrador - Programación 2

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de Programación 2. El proyecto consiste en desarrollar un sistema completo de gestión de usurios y credenciales de acceso que permita realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta y profesional.

### Objetivos Académicos

El desarrollo de este sistema permite aplicar y consolidar los siguientes conceptos clave de la materia:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en 4 capas diferenciadas
- Capa de Presentación (Main/UI): Interacción con el usuario mediante consola
- Capa de Lógica de Negocio (Service): Validaciones y reglas de negocio
- Capa de Acceso a Datos (DAO): Operaciones de persistencia
- Capa de Modelo (Models): Representación de entidades del dominio

**2. Programación Orientada a Objetos**
- Uso de herencia mediante clase abstracta Base
- Implementación de interfaces genéricas (GenericDAO, GenericService)
- Encapsulamiento con atributos privados y métodos de acceso
- Sobrescritura de métodos (toString)

**3. Persistencia de Datos con JDBC**
- Conexión a base de datos MySQL mediante JDBC
- Implementación del patrón DAO (Data Access Object)
- Uso de PreparedStatements para prevenir SQL Injection
- Gestión de transacciones con commit y rollback
- Manejo de claves autogeneradas (AUTO_INCREMENT)
- Consultas con LEFT JOIN para relaciones entre entidades

**4. Manejo de Recursos y Excepciones**
- Uso del patrón try-with-resources para gestión automática de recursos JDBC
- Implementación de AutoCloseable en TransactionManager
- Manejo apropiado de excepciones con propagación controlada
- Validación multi-nivel: base de datos y aplicación

**5. Patrones de Diseño**
- Factory Pattern (DatabaseConnection)
- Service Layer Pattern (separación lógica de negocio)
- DAO Pattern (abstracción del acceso a datos)
- Soft Delete Pattern (eliminación lógica de registros)

**6. Validación de Integridad de Datos**
- Validación de unicidad (id único por entidades)
- Validación de campos obligatorios en múltiples niveles
- Validación de integridad referencial (Foreign Keys)
- Implementación de eliminación segura para prevenir referencias huérfanas


## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 21 o superior |
| MySQL | 8.0 o superior |
| DBeaver | 25.4.0 |
| Sistema Operativo | Windows, Linux o macOS |

## Instalación

### 1. Configurar Base de Datos

Ejecutar el siguiente script SQL en MySQL:

CREATE DATABASE IF NOT EXISTS tpi;

USE tpi;

CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP NOT NULL,
    credencial_id BIGINT NULL UNIQUE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE credencialAcceso (
    id BIGINT PRIMARY KEY,
    hashPassword VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    ultimoCambio TIMESTAMP NOT NULL,
    requiereReset BOOLEAN NOT NULL DEFAULT FALSE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_credencial_a_usuario
        FOREIGN KEY (id) REFERENCES usuario(id)
        ON DELETE CASCADE
);

ALTER TABLE usuario
ADD CONSTRAINT fk_usuario_a_credencial
    FOREIGN KEY (credencial_id) REFERENCES credencialAcceso(id)
    ON DELETE SET NULL;


### 2. Configurar Conexión (Opcional)
Abre el DBeaver y conecta la base de datos
Por defecto conecta a:
- **Host**: localhost:3306
- **Base de datos**: dbtpi3
- **Usuario**: root
- **Contraseña**: (vacía) o la que pusiste vos

Para cambiar la configuración, usar propiedades del sistema:

```bash
java -Ddb.url=jdbc:mysql://localhost:3306/dbtpi3 \
     -Ddb.user=usuario \
     -Ddb.password=clave \
     -cp ...
```

## Ejecución

### Netbeans
1. Abrir proyecto en Netbeans TPI_ProgramacionII
2. Cambias los valores en la clase DatabaseConnection y pon tu contraseña
3. Verifica primero en la clase TestConexion si la conexion a la base de datos esta conectada
4. Ejecutar clase `Main.Main`


### Verificar Conexión

Salida esperada:
```
Conexion exitosa a la base de datos
Usuario conectado: root@localhost
Base de datos: tpi
URL: jdbc:mysql://localhost:3306/tpi
Driver: MySQL Connector/J v8.4.0
```

## Uso del Sistema

### Menú Principal

```
--- MENU PRINCIPAL (TFI) ---
--- Gestion de Usuarios ---
1. Crear Usuario (con Credencial)
2. Consultar Usuario por ID
3. Listar todos los Usuarios
4. Actualizar datos de Usuario
5. Eliminar Usuario (con Credencial)
--- Gestion de Credenciales ---
6. Actualizar Credencial (Resetear Password)
--- Busquedas ---
7. Buscar Usuario por Username
0. Salir
Seleccione una opcion: 
```
## Arquitectura

### Estructura en Capas

```
┌─────────────────────────────────────┐
│     Main / UI Layer                 │
│  (Interacción con usuario)          │
│  AppMenu                            │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Service Layer                   │
│  (Lógica de negocio y validación)   │
│  UsuarioService                     │
│  CredencialAccesoService            │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     DAO Layer                       │
│  (Acceso a datos)                   │
│  UsuarioDAO, CredencialAcceosDAO    │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Models Layer                    │
│  (Entidades de dominio)             │
│  Usuario, CredencialAcceso, Base    │
└─────────────────────────────────────┘
```

### Componentes Principales

**Config/**
- `DatabaseConnection.java`: Gestión de conexiones JDBC con validación en inicialización estática

**Models/**
- `Base.java`: Clase abstracta con campos id y eliminado
- `Usuario.java`: Entidad Usuario (email, username, activo, fechaRegistro, credencial)
- `CredencialAcceso.java`: Entidad CredencialAcceso (hashPassword, salt, ultimoCambio, requiereReset)

**Dao/**
- `GenericDAO<T>`: Interface genérica con operaciones CRUD
- `UsuarioDAO`
- `CredencialAccesoDAO`

**Service/**
- `GenericService<T>`: Interface genérica para servicios
- `UsuarioService`: Validaciones de usuario y coordinación con credenciales
- `CredencialAccesoService`: Validaciones de credenciales

**Main/**
- `Main.java`: Punto de entrada
- `AppMenu.java`: Orquestador del ciclo de menú
- `TestConexion.java`: Utilidad para verificar conexión a BD

**utils/**
- `OperacionTransaccion.java`: Manejo de las transacciones

## Documentación Adicional
- PDF "Trabajo Final Integrador - Programación 2 - Comision 3...": Informacion adicional del trabajo integrador.
- Consultas-Utilies-SQL-Progra2-TPI.sql: archivo sql donde se realizan consultas utiles de las tablas.
- datos_prueba_progra2TPI.sql: archivo sql donde se realizan distintas pruebas.
- db_tpi.sql: archivo sql donde se crean las tablas a utilizar.
- UML-PROGRA2-TPIF.png: imagen UML del programa ejecutado.
- message.txt: archivo de texto donde se muestra las diferentes fases del trabajo integrador

## Tecnologías Utilizadas

- **Lenguaje**: Java 21+
- **Base de Datos**: MySQL 8.x
- **JDBC Driver**: mysql-connector-j 8.4.0
- **DBeaver**: DBeaver 25.4

## Contexto Académico

**Materia**: Programación 2
**Tipo de Evaluación**: Trabajo Práctico Integrador (TPI)
**Modalidad**: Desarrollo de sistema CRUD con persistencia en base de datos
**Objetivo**: Aplicar conceptos de POO, JDBC, arquitectura en capas y patrones de diseño

Este proyecto representa la integración de todos los conceptos vistos durante el cuatrimestre, demostrando capacidad para:
- Diseñar sistemas con arquitectura profesional
- Implementar persistencia de datos con JDBC
- Aplicar patrones de diseño apropiados
- Manejar recursos y excepciones correctamente
- Validar integridad de datos en múltiples niveles
- Documentar código de forma profesional
---

**Java**: 21+
**MySQL**: 8.x
**DBeaver**: DBeaver 25.4
**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2
**Grupo**: 
- Tahiel Noé Heinze – tahielnoeheinze@gmail.com - 45237386
- Tobias Leiva tobiasleivaa@gmail.com - 47622125
- Genaro Luna - genaroluna2808@gmail.com - 44369231
