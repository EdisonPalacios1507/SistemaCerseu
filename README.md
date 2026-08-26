# Sistema CERSEU Arguedas - Gestión Académica

Aplicación de escritorio en Java (Swing + FlatLaf) que consume íntegramente la
base de datos `arguedas_cerseu`: ODS, Docente, Estudiante, Curso, Matrícula,
Asistencia, Acta_Notas y Pago, incluyendo el procedimiento almacenado
`sp_matricular_estudiante` y los dos triggers de notas.

## 1. Requisitos

- NetBeans 17+ (con soporte Maven, viene integrado).
- JDK 17 o superior.
- MySQL Server 8.x encendido y accesible.
- Haber ejecutado, en este orden, los scripts SQL que ya tienes:
  1. Creación de tablas (`CREATE DATABASE arguedas_cerseu; ...`).
  2. Creación de usuarios y privilegios (`admin_cerseu`, `app_arguedas`, `reporte_cerseu`).
  3. Procedimientos y triggers (`sp_matricular_estudiante`, `trg_calcular_estado_final`, `trg_bloquear_acta_cerrada`).
  4. Datos de prueba (INSERTs de ODS, Docente, Estudiante, Curso, etc.).

## 2. Abrir el proyecto en NetBeans

1. `File → Open Project...` y selecciona la carpeta `SistemaCerseu` (contiene el `pom.xml`).
2. NetBeans lo reconocerá como proyecto Maven y descargará automáticamente la
   única dependencia externa declarada en `pom.xml`:
   - `com.mysql:mysql-connector-j` (driver JDBC oficial de MySQL).

   La interfaz usa Nimbus como Look & Feel, que viene incluido en el JDK, así
   que no depende de ninguna librería gráfica externa.
3. Si prefieres compilar por consola: `mvn clean compile exec:java`.

## 3. Configurar la conexión

Abre `src/main/java/com/cerseu/app/conexion/ConexionBD.java` y ajusta si es
necesario:

```java
private static final String HOST = "localhost";
private static final String PUERTO = "3306";
private static final String USUARIO = "app_arguedas";
private static final String CLAVE = "AppArguedas_2026*";
```

Se usa por defecto el usuario `app_arguedas` (creado en tu script de
privilegios), que solo tiene permisos de SELECT/INSERT/UPDATE/DELETE/EXECUTE,
siguiendo el principio de menor privilegio que ya definiste en la base de
datos. Si prefieres usar `admin_cerseu`, solo cambia usuario y clave.

## 4. Ejecutar

Botón derecho sobre el proyecto → `Run`, o `Main.java` → `Run File`.

## 5. Qué hace cada pestaña

| Pestaña         | Tabla(s)                    | Detalle especial |
|-----------------|-----------------------------|-------------------|
| Estudiantes     | Estudiante                   | CRUD completo |
| Docentes        | Docente                       | CRUD completo |
| ODS             | ODS                            | CRUD completo |
| Cursos          | Curso (+ ODS, Docente)         | Combos de ODS y Docente, docente opcional |
| Matrícula       | Matricula (+ Estudiante/Curso) | El botón "Matricular" llama al procedimiento `sp_matricular_estudiante`, que valida cupos y estado del curso dentro de MySQL. Si el curso está lleno o no habilitado, verás el mensaje `SIGNAL` tal cual lo define el procedimiento |
| Asistencia      | Asistencia (+ Matricula)       | Incluye cálculo del % de asistencia por matrícula (sesiones "Presente" / total) |
| Actas de Notas  | Acta_Notas (+ Matricula)       | El "Estado final" **no se envía desde Java**: lo calcula el trigger `trg_calcular_estado_final` (asistencia < 70% → Inhabilitado; nota ≥ 11 → Aprobado; si no, Desaprobado). El botón "Cerrar acta" activa `esta_cerrada`; después de cerrada, cualquier intento de modificar la nota es bloqueado por `trg_bloquear_acta_cerrada` (Regla 13) y el mensaje de error se muestra en pantalla |
| Pagos           | Pago (+ Matricula)             | La moneda siempre se guarda como `PEN` (regla `CHECK` de la tabla); incluye selector de archivo para el nombre del voucher y botón para marcar un pago como verificado |

## 6. Notas de diseño

- Arquitectura en 3 capas: `modelo` (POJOs) → `dao` (JDBC/SQL) → `vista` (Swing).
- Todas las consultas usan `PreparedStatement` para evitar inyección SQL.
- Los errores de MySQL (claves foráneas, únicos, `SIGNAL`, triggers) se
  capturan como `SQLException` y se muestran en un diálogo con el mensaje
  original de la base de datos, para que sea evidente qué regla de negocio
  se disparó.
