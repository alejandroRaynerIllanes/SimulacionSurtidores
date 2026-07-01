# Manual de Instalación y Despliegue
## Simulador de Red de Surtidores de Combustible — Bolivia
**Versión:** 1.0-SNAPSHOT  
**Artefacto Maven:** `com.simulacion.bolivia:SimulacionSurtidores`

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#1-requisitos-previos)
2. [Descarga y Preparación del Proyecto](#2-descarga-y-preparación-del-proyecto)
3. [Compilación del Proyecto](#3-compilación-del-proyecto)
4. [Ejecución del Simulador](#4-ejecución-del-simulador)
5. [Estructura del Proyecto](#5-estructura-del-proyecto)
6. [Solución de Errores Comunes](#6-solución-de-errores-comunes)

---

## 1. Requisitos Previos

Antes de instalar y ejecutar el simulador, asegúrese de tener los siguientes componentes instalados y configurados correctamente en su sistema.

### 1.1 Java Development Kit (JDK) 21 o superior

El proyecto está compilado con **Java 21** (configurado en `pom.xml` mediante `maven.compiler.source` y `maven.compiler.target`).

| Parámetro | Valor requerido |
|-----------|----------------|
| Versión mínima | **JDK 21** |
| Distribución recomendada | OpenJDK 21 / Eclipse Temurin 21 |

**Verificar la instalación:**
```bash
java -version
```
Salida esperada:
```
openjdk version "21.x.x" ...
```

**Descargar JDK 21:**
- [Eclipse Temurin (Adoptium)](https://adoptium.net/temurin/releases/?version=21)
- [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)

---

### 1.2 Apache Maven

Maven gestiona las dependencias del proyecto y automatiza la compilación y ejecución.

| Parámetro | Valor |
|-----------|-------|
| Versión mínima | **Maven 3.8+** |
| Plugin de ejecución | `exec-maven-plugin 3.1.0` |

**Verificar la instalación:**
```bash
mvn -version
```
Salida esperada:
```
Apache Maven 3.x.x ...
Java version: 21.x.x ...
```

**Descargar Maven:**
- [Apache Maven — Descarga oficial](https://maven.apache.org/download.cgi)

---

### 1.3 Configuración de Variables de Entorno

Es obligatorio que las siguientes variables de entorno estén correctamente definidas en el sistema operativo.

#### En Windows

| Variable | Descripción | Ejemplo de valor |
|----------|-------------|-----------------|
| `JAVA_HOME` | Ruta raíz de la instalación del JDK | `C:\Program Files\Java\jdk-21` |
| `MAVEN_HOME` | Ruta raíz de la instalación de Maven | `C:\Program Files\Apache\maven` |
| `PATH` | Debe incluir `%JAVA_HOME%\bin` y `%MAVEN_HOME%\bin` | — |

**Verificar `JAVA_HOME`:**
```powershell
echo $env:JAVA_HOME
```

**Configurar `JAVA_HOME` en PowerShell (sesión actual):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

#### En Linux / macOS

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

> [!IMPORTANT]
> Si `JAVA_HOME` no está definido correctamente, Maven no podrá compilar el proyecto y lanzará el error `JAVA_HOME is not set`.

---

## 2. Descarga y Preparación del Proyecto

### 2.1 Mediante archivo ZIP

1. Descargue el archivo comprimido del proyecto (`SimulacionSurtidores.zip`).
2. Descomprimalo en la ubicación deseada:

   **En Windows (PowerShell):**
   ```powershell
   Expand-Archive -Path "SimulacionSurtidores.zip" -DestinationPath "C:\proyectos\simulacion"
   ```

   **En Linux / macOS:**
   ```bash
   unzip SimulacionSurtidores.zip -d ~/proyectos/simulacion
   ```

3. Navegue al directorio raíz del proyecto (donde se encuentra `pom.xml`):
   ```bash
   cd SimulacionSurtidores
   ```

### 2.2 Mediante Git (si aplica)

```bash
git clone <url-del-repositorio>
cd SimulacionSurtidores
```

> [!NOTE]
> El directorio raíz del proyecto debe contener el archivo `pom.xml`. Todos los comandos de Maven deben ejecutarse desde esta ubicación.

---

## 3. Compilación del Proyecto

### 3.1 Comando de compilación

Desde el directorio raíz del proyecto, ejecute:

```bash
mvn clean compile
```

Este comando realiza las siguientes acciones en orden:

| Fase | Descripción |
|------|-------------|
| `clean` | Elimina el directorio `target/` con compilaciones anteriores |
| `compile` | Descarga las dependencias (si no están en el repositorio local) y compila el código fuente a `target/classes/` |

---

### 3.2 Dependencias descargadas automáticamente

Maven descargará automáticamente las siguientes dependencias declaradas en `pom.xml` desde el repositorio central de Maven:

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `org.apache.commons:commons-math3` | **3.6.1** | Generación de variables aleatorias (distribuciones exponenciales y normales) para el motor de simulación estocástica |
| `org.apache.poi:poi` | **5.2.3** | Núcleo de Apache POI para manipulación de archivos Excel (`.xlsx`) |
| `org.apache.poi:poi-ooxml` | **5.2.3** | Módulo OOXML de POI: soporte para gráficos nativos (`XDDFChart`), hojas de cálculo y estilos avanzados |
| `org.apache.logging.log4j:log4j-core` | **2.19.0** | Sistema de logging requerido internamente por Apache POI para evitar advertencias en tiempo de ejecución |

Las dependencias se almacenan en el repositorio local de Maven (por defecto en `~/.m2/repository/`). En ejecuciones posteriores, Maven las reutilizará sin necesidad de descargarlas nuevamente.

---

### 3.3 Salida esperada de una compilación exitosa

```
[INFO] --- compiler:3.13.0:compile (default-compile) @ SimulacionSurtidores ---
[INFO] Compiling 21 source files with javac [debug target 21] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.x s
```

---

## 4. Ejecución del Simulador

### 4.1 Lanzar la interfaz gráfica

Una vez compilado el proyecto, ejecute el siguiente comando para iniciar el simulador con su interfaz gráfica (GUI):

```bash
mvn exec:java
```

Este comando utiliza el plugin `exec-maven-plugin 3.1.0` configurado en `pom.xml`, el cual lanza automáticamente la clase principal:

```
com.simulacion.bolivia.gui.AppGrafica
```

> [!TIP]
> No es necesario ejecutar `mvn compile` antes de `mvn exec:java` en una sesión ya compilada. Sin embargo, si realizó cambios en el código fuente, ejecute primero `mvn clean compile` para asegurarse de que los cambios estén incluidos.

---

### 4.2 Flujo de uso del simulador

Una vez abierta la ventana principal, el flujo recomendado es:

```
1. Configurar la Red de Estaciones
   └── Botón "Gestionar Red" → DialogGestorRed
       ├── Crear / editar estaciones de servicio
       └── Configurar surtidores por estación (DialogGestorSurtidores)

2. Configurar la Estación Internacional
   └── Botón "Configurar Internacional" → DialogConfiguracionInternacional

3. Iniciar la Simulación
   └── Botón "Iniciar Simulación" (panel inferior)
       └── Seleccionar días a simular con el deslizador

4. Exportar Reporte Excel (automático al finalizar)
   └── Se genera reporte_simulacion.xlsx en el directorio raíz del proyecto
       ├── Hoja "Dashboard"         → KPIs globales con fórmulas dinámicas
       ├── Hoja "Detalle de Operaciones" → Historial granular por vehículo
       └── Hoja "Análisis Gráfico"  → Gráfico de pastel y de barras nativos
```

---

## 5. Estructura del Proyecto

```
SimulacionSurtidores/
│
├── pom.xml                          ← Configuración Maven (dependencias y plugins)
├── Manual_Instalacion.md            ← Este documento
├── reporte_simulacion.xlsx          ← Reporte generado tras cada simulación
│
└── src/
    └── main/
        └── java/
            └── com/simulacion/bolivia/
                ├── app/             ← Clase Main (punto de entrada CLI)
                ├── engine/          ← Motor de simulación de eventos discretos
                ├── gui/             ← Interfaz gráfica Swing (ventanas y diálogos)
                ├── logic/           ← Motor de enrutamiento (MotorRuteo)
                ├── models/          ← Entidades del dominio (Vehiculo, Surtidor, etc.)
                ├── reports/         ← Generador de reportes Excel (GeneradorExcel)
                └── utils/           ← Generador estocástico (distribuciones)
```

---

## 6. Solución de Errores Comunes

### ❌ Error: `JAVA_HOME` no definido

**Mensaje:**
```
The JAVA_HOME environment variable is not defined correctly.
```
**Solución:**
Defina la variable `JAVA_HOME` apuntando al directorio raíz del JDK 21 (no al directorio `bin`):
```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

---

### ❌ Error: Versión de Java incompatible

**Mensaje:**
```
error: Source option 21 is not supported. Use 17 or lower.
```
**Solución:**
Instale JDK 21 y asegúrese de que `JAVA_HOME` apunte a esa versión. Verifique con:
```bash
java -version
```

---

### ❌ Error: `BUILD FAILURE` por dependencias no descargadas

**Mensaje:**
```
Could not resolve dependencies for project ...
```
**Solución:**
Verifique su conexión a Internet. Maven descarga dependencias desde Maven Central. Si está detrás de un proxy corporativo, configure `~/.m2/settings.xml` con las credenciales del proxy.

---

### ❌ Error al generar el reporte Excel (archivo bloqueado)

**Mensaje:**
```
Error al generar el reporte Excel: reporte_simulacion.xlsx
(El proceso no tiene acceso al archivo porque está siendo utilizado por otro proceso)
```
**Causa:** El archivo `reporte_simulacion.xlsx` está abierto en Microsoft Excel u otro programa.

**Solución:** El simulador detecta automáticamente este escenario y guarda el reporte con un nombre alternativo numerado:
```
reporte_simulacion (1).xlsx
reporte_simulacion (2).xlsx
...
```
Al finalizar, aparece un diálogo informando la ruta exacta donde se guardó el nuevo archivo. No es necesario cerrar el simulador ni repetir la simulación.

---

### ❌ Error: No se abre la ventana gráfica

**Causa posible:** El sistema no tiene entorno gráfico disponible (servidor headless).

**Solución:** Asegúrese de ejecutar el simulador en un entorno de escritorio con soporte para Java Swing (Windows, Linux con X11/Wayland, macOS).

---

*Documento generado automáticamente a partir del análisis de `pom.xml` y la estructura del proyecto `SimulacionSurtidores v1.0-SNAPSHOT`.*
