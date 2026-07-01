# Manual de Usuario
## Simulador de Red de Surtidores de Combustible — Bolivia
**Versión:** 1.0 | **Interfaz:** Java Swing (Escritorio)

> [!NOTE]
> Este manual está escrito para el usuario final. No se requieren conocimientos de programación para operar el simulador.

---

## 📋 Tabla de Contenidos

1. [Pantalla Principal (Dashboard)](#1-pantalla-principal-dashboard)
2. [Configuración de la Red de Estaciones](#2-configuración-de-la-red-de-estaciones)
3. [Panel What-If — Análisis de Escenarios](#3-panel-what-if--análisis-de-escenarios)
4. [Ejecución y Visualización en Tiempo Real](#4-ejecución-y-visualización-en-tiempo-real)
5. [Módulo de Reportes — Inteligencia de Negocios](#5-módulo-de-reportes--inteligencia-de-negocios)

---

## 1. Pantalla Principal (Dashboard)

Al ejecutar el simulador, se abre la **ventana principal** dividida en cuatro zonas claramente diferenciadas:

```
┌─────────────────────────────────────────────────────────────┐
│                    BARRA DE ESTADO (NORTE)                  │
│  Reloj: Día 1 (00:00)   Gasolina: 30000 L   ESTADO: OK     │
├────────────────┬────────────────────────────────────────────┤
│                │                                            │
│  PANEL         │         LIENZO 2D DEL MAPA DE RED          │
│  WHAT-IF       │         (CENTRO — Zona de Visualización)   │
│  (OESTE)       │                                            │
│                │                                            │
├────────────────┴────────────────────────────────────────────┤
│        KPIs EN TIEMPO REAL    │  [Retardo ms]  [Iniciar]   │
│  Subv: 0 | Int: 0 | Bs: 0.00 │    ───────────  ─────────  │
└─────────────────────────────────────────────────────────────┘
```

---

### 1.1 Barra de Estado (parte superior)

La franja oscura en la parte superior muestra tres indicadores que se actualizan en tiempo real durante la simulación:

| Indicador | Descripción |
|-----------|-------------|
| **Reloj** | Tiempo virtual transcurrido. Muestra el día, la hora y los minutos. Ej: `Reloj: Día 3 (14:30)` |
| **Gasolina / Diésel** | Nivel actual (en litros) del tanque subvencionado de la primera estación registrada |
| **Estado del Tanque** | `ESTADO T. SUBVENCIONADO: OK` en **verde** cuando hay stock suficiente. Cambia a `!!! DESABASTECIDO - SOLICITADO CISTERNA !!!` en **rojo** cuando el tanque se agota y se ha pedido un reabastecimiento |

---

### 1.2 Lienzo 2D del Mapa de Red (centro)

El **área central** es el mapa visual donde se representan todas las estaciones de la red. Aquí podrá observar en tiempo real:

- **Cajas rectangulares con bordes grises**: cada una representa una **estación de servicio**. El nombre aparece en la parte superior de la caja.
- **Rectángulos pequeños de colores** dentro de cada estación: son los **surtidores (bombas)**:
  - 🟢 **Verde**: el surtidor está **libre**, listo para atender.
  - 🔴 **Rojo**: el surtidor está **ocupado** atendiendo un vehículo.
- **Pequeñas figuras azules** debajo de cada surtidor: representan los **vehículos en cola** esperando ser atendidos. Se muestran hasta 7 autos por surtidor.
- **Líneas punteadas** entre estaciones: muestran las **conexiones de red** con la distancia en kilómetros.
- **La caja en la esquina superior derecha** (fondo gris claro) es siempre la **Estación Internacional**, que atiende a vehículos extranjeros al precio internacional.

> [!TIP]
> El lienzo tiene scroll vertical. Si ha creado varias estaciones subvencionadas, desplace la barra de scroll hacia abajo para ver todas.

---

### 1.3 Panel de KPIs (parte inferior izquierda)

Muestra cuatro métricas clave que se actualizan automáticamente con cada evento de la simulación:

| Métrica | Significado |
|---------|------------|
| **Vehículos Subvencionados** | Cantidad de vehículos nacionales atendidos en estaciones subvencionadas |
| **Vehículos Internacionales** | Cantidad de vehículos extranjeros atendidos en la estación internacional |
| **Ingresos totales (Bs.)** | Suma de todos los ingresos generados hasta el momento |
| **Tiempo medio de espera** | Promedio en minutos que los vehículos esperaron en cola antes de ser atendidos |

---

## 2. Configuración de la Red de Estaciones

Antes de iniciar la simulación, debe definir cuántas estaciones subvencionadas operarán en la red. Para ello, use el botón **⚙️ Configurar Red de Estaciones...** del Panel What-If (columna izquierda).

Se abrirá la ventana **Gestor de Red de Estaciones**, dividida en dos secciones:

---

### 2.1 Lista de Estaciones (columna izquierda)

Muestra todas las estaciones subvencionadas creadas hasta el momento. Al hacer clic en una, el formulario de la derecha se rellena automáticamente con sus datos.

#### ➕ Crear una nueva estación

1. Haga clic en el botón azul **➕ Nueva**.
2. Se añade automáticamente una estación con valores por defecto (tanques de 30,000 L, abierta 0–24 h, conectada a "Internacional", con 2 surtidores).
3. La nueva estación queda seleccionada en la lista y sus datos aparecen en el formulario.
4. Modifique los campos según sus necesidades y pulse **💾 Guardar Cambios en Estación Seleccionada**.

#### 🗑️ Eliminar una estación

1. Haga clic sobre la estación que desea eliminar en la lista.
2. Pulse el botón rojo **🗑️ Eliminar**.
3. La estación desaparece de la lista inmediatamente. No se pide confirmación.

---

### 2.2 Formulario de Edición (columna derecha)

Al seleccionar una estación de la lista, se habilita el formulario con los siguientes campos editables:

| Campo | Descripción | Ejemplo |
|-------|-------------|---------|
| **Nombre Estación** | Nombre identificador de la estación | `Estación Central Norte` |
| **Conectar a (Estación)** | A qué punto de la red se conecta esta estación. El menú muestra todas las demás estaciones y siempre incluye "Internacional" | `Internacional` |
| **Distancia a Conector (Km)** | Kilómetros de distancia al punto de conexión. Aparece como etiqueta sobre la línea punteada del mapa | `5.5` |
| **Hora Apertura (0–24)** | Hora del día (en formato decimal) en que la estación empieza a operar | `6.0` (= 6:00 AM) |
| **Hora Cierre (0–24)** | Hora del día en que la estación deja de operar | `22.0` (= 10:00 PM) |
| **Gasolina** ☑ | Marque si la estación vende gasolina especial | ✅ habilitado |
| **Capacidad Gasolina (L)** | Tamaño del tanque de gasolina en litros | `30000` |
| **Diésel** ☑ | Marque si la estación vende diésel | ✅ habilitado |
| **Capacidad Diésel (L)** | Tamaño del tanque de diésel en litros | `30000` |
| **Número de Bombas (Surtidores)** | Cantidad de surtidores activos en la estación | `3` |

> [!IMPORTANT]
> Al menos uno de los dos tipos de combustible (Gasolina o Diésel) debe estar habilitado. El formulario mostrará un error si intenta guardar con ambos deshabilitados.

#### 💾 Guardar cambios

Una vez completado el formulario, haga clic en el botón verde **💾 Guardar Cambios en Estación Seleccionada**. El sistema confirmará el guardado con un mensaje y cerrará el diálogo automáticamente.

---

### 2.3 Personalizar Surtidores Individuales

Si necesita ajustar los parámetros de cada surtidor por separado (por ejemplo, cambiar el caudal o el precio individual), use el botón gris **⚙️ Personalizar Surtidores...** que aparece junto al campo de número de bombas.

Se abrirá el **Diálogo de Gestión de Surtidores** con las siguientes funciones:

- Ver la lista de todos los surtidores de la estación seleccionada.
- **Agregar** un nuevo surtidor con el botón **➕ Agregar Surtidor**.
- **Seleccionar** un surtidor de la lista para editar sus propiedades:
  - **ID del surtidor** (nombre identificador, ej: `S1_Centro`)
  - **Caudal (L/min)**: velocidad de despacho de combustible (por defecto: 60 L/min)
  - **Precio por litro (Bs.)**: precio aplicado por este surtidor específico
- **Eliminar** el surtidor seleccionado con el botón 🗑️ **Eliminar**.
- **Guardar** los cambios del surtidor con el botón verde **💾 Guardar Cambios...**.

> [!NOTE]
> El campo **Número de Bombas** del formulario principal se actualiza automáticamente al regresar desde el diálogo de surtidores para reflejar la cantidad final configurada.

---

### 2.4 Configurar la Estación Internacional

El botón gris **⚙️ Estación Internacional...** (en la parte inferior de la lista) abre un diálogo dedicado para editar los parámetros de la única estación internacional del sistema:

- **Nombre** de la estación internacional
- **Capacidad** del tanque de gasolina internacional (en litros)
- **Número de surtidores** internacionales (bombas disponibles)

> [!IMPORTANT]
> La Estación Internacional siempre está activa. Los vehículos extranjeros (aproximadamente el 5% del tráfico) se dirigen automáticamente a ella y pagan el **Precio Internacional** configurado en el Panel What-If.

---

## 3. Panel What-If — Análisis de Escenarios

El **Panel What-If** (columna izquierda de la ventana principal) es la zona de configuración de parámetros económicos y operativos antes de iniciar la simulación.

Todos sus campos son editables hasta el momento de pulsar **Iniciar Simulación**.

---

### 3.1 Precios de Combustible

| Campo | Valor por defecto | Descripción |
|-------|:-----------------:|-------------|
| **Precio Gasolina (Bs./L)** | `6.96` | Precio subvencionado por litro de gasolina especial que pagan los vehículos nacionales particulares |
| **Precio Diésel (Bs./L)** | `9.80` | Precio subvencionado por litro de diésel que pagan los vehículos de transporte pesado nacionales |
| **Precio Int. (Bs./L)** | `12.50` | Precio internacional por litro que pagan los vehículos extranjeros en la estación internacional |

> [!TIP]
> Pruebe escenarios de ajuste de precios cambiando estos valores. Por ejemplo, suba el **Precio Int.** a `15.00` y compare los ingresos internacionales al finalizar la simulación.

---

### 3.2 Cuotas de Entrega YPFB (Déficit del 60%)

Estos campos simulan la política de abastecimiento de YPFB, donde las estaciones no siempre reciben el 100% de su capacidad cuando llega una cisterna.

| Campo | Valor por defecto | Descripción |
|-------|:-----------------:|-------------|
| **Cuota Gasolina (%)** | `100` | Porcentaje del tanque que se llena cuando llega la cisterna de gasolina. Si se pone `60`, cada cisterna solo repone el 60% de la capacidad máxima, simulando el déficit de abastecimiento |
| **Cuota Diésel (%)** | `60` | Porcentaje de llenado para el diésel al llegar la cisterna |

> [!TIP]
> Para simular el escenario de **déficit real boliviano**, deje ambas cuotas en valores entre 50 y 70. Esto hará que las estaciones se desabastezcan más frecuentemente y podrá observar el impacto en los KPIs de espera e ingresos.

---

### 3.3 Días a Simular

| Campo | Valor por defecto | Descripción |
|-------|:-----------------:|-------------|
| **Días a Simular** | `90` | Número de días del período de simulación. El motor convierte este valor a minutos internamente (`días × 1440`). |

**Recomendaciones según el objetivo:**

| Objetivo | Días sugeridos |
|----------|:--------------:|
| Prueba rápida / revisión de configuración | `1` a `5` |
| Análisis semanal | `7` |
| Análisis mensual | `30` |
| Análisis trimestral (por defecto) | `90` |

> [!IMPORTANT]
> Una vez pulsado el botón **Iniciar Simulación**, todos los campos del Panel What-If se deshabilitan y no pueden ser modificados. Para cambiar parámetros debe reiniciar el programa.

---

## 4. Ejecución y Visualización en Tiempo Real

### 4.1 Botón "Iniciar Simulación"

El botón azul **Iniciar Simulación** (parte inferior derecha) pone en marcha el motor de simulación de eventos discretos con todos los parámetros configurados.

**Al presionarlo:**
1. El botón se deshabilita y su texto cambia a `"Simulación en curso..."`.
2. Todos los campos del Panel What-If se bloquean.
3. El motor comienza a procesar eventos (arribos de vehículos, servicios, cisternas) en un hilo separado para no congelar la interfaz.
4. La barra de estado, el mapa, y los KPIs se actualizan en tiempo real con cada evento procesado.
5. Al finalizar el período simulado, el botón muestra `"Simulación Finalizada"` y se genera automáticamente el reporte Excel.

> [!CAUTION]
> Si ingresó texto no numérico en alguno de los campos del Panel What-If, el simulador mostrará un mensaje de error **"Error de Formato"** y no iniciará. Corrija los valores y vuelva a intentarlo.

---

### 4.2 Control de Velocidad — Slider "Retardo (ms)"

El **slider horizontal** ubicado a la izquierda del botón Iniciar controla la velocidad de visualización de la simulación.

```
0 ms ──────────────────── 500 ms
│                            │
Muy rápido              Muy lento
(sin pausa)         (500ms por evento)
```

| Posición | Valor | Efecto |
|----------|:-----:|--------|
| Extremo izquierdo | `0 ms` | La simulación corre a máxima velocidad. El mapa se actualiza pero los eventos ocurren tan rápido que puede parecer que "salta" directamente al final |
| Centro | `~50 ms` | Velocidad recomendada para observar el movimiento de colas sin que sea demasiado lento |
| Extremo derecho | `500 ms` | Cada evento tarda medio segundo. Permite seguir cada llegada y salida de vehículo individualmente |

> [!TIP]
> Para simulaciones largas (90 días), se recomienda dejar el slider en `0` o valores bajos para que termine rápidamente. Para demostraciones o análisis visual, use valores de `50–100 ms`.

---

### 4.3 Interpretar el Mapa durante la Simulación

Mientras la simulación está en curso, el lienzo 2D se actualiza con cada evento:

#### Surtidores

| Color | Significado |
|-------|-------------|
| 🟢 **Verde** | El surtidor está **libre**. No hay ningún vehículo siendo atendido |
| 🔴 **Rojo** | El surtidor está **ocupado**. Hay un vehículo cargando combustible |

#### Vehículos en cola (figuras azules)

Cada pequeña figura azul con llantas debajo de un surtidor representa un vehículo esperando en la fila. El simulador dibuja hasta **7 vehículos** visibles por surtidor.

#### Marcador de desbordamiento `+N` (badge rojo)

Cuando la cola de un surtidor **supera los 7 vehículos**, aparece una etiqueta roja con el número de vehículos adicionales que no caben en pantalla:

```
  ┌──────────┐
  │   S1     │  ← Surtidor (rojo = ocupado)
  └──────────┘
    🚗 🚗 🚗
    🚗 🚗 🚗
    🚗           ← 7 vehículos visibles
  ┌──────────┐
  │  +150    │  ← 150 vehículos adicionales en espera (desbordamiento)
  └──────────┘
```

> [!WARNING]
> Un badge de desbordamiento alto (por ejemplo `+50` o más) indica que ese surtidor está **saturado**. Considere agregar más surtidores a esa estación o reducir los días simulados para revisar la configuración.

#### Alarma de desabastecimiento (barra superior)

Cuando un tanque se agota, la barra de estado cambia de **verde** a **rojo**:

- 🟥 `!!! DESABASTECIDO - SOLICITADO CISTERNA !!!` → Se ha pedido una cisterna. La cisterna tarda **24 horas** simuladas en llegar.
- 🟩 `ESTADO T. SUBVENCIONADO: OK` → Vuelve al verde cuando la cisterna llega y recarga los tanques.

---

## 5. Módulo de Reportes — Inteligencia de Negocios

### 5.1 Generación automática del reporte

**Al finalizar** cada simulación, el sistema genera automáticamente un archivo Excel profesional en el directorio raíz del proyecto:

```
📁 SimulacionSurtidores/
└── 📊 reporte_simulacion.xlsx    ← Se crea/actualiza aquí
```

Aparece también un **cuadro de diálogo informativo** confirmando la ruta exacta del archivo generado.

> [!NOTE]
> Si el archivo `reporte_simulacion.xlsx` está abierto en Excel al momento de finalizar la simulación, el sistema detecta el bloqueo automáticamente y guarda el nuevo reporte con un nombre alternativo numerado, por ejemplo: `reporte_simulacion (1).xlsx`. El diálogo informará la ruta exacta.

---

### 5.2 Estructura del archivo Excel

El reporte contiene **tres hojas** con información complementaria:

#### 📋 Hoja 1: Dashboard

Resumen ejecutivo con los KPIs globales de toda la simulación:

| Métrica incluida | Descripción |
|-----------------|-------------|
| Tiempo total simulado | En minutos y en días |
| Vehículos atendidos (Subvencionados) | Conteo de nacionales atendidos |
| Vehículos atendidos (Internacionales) | Conteo de extranjeros atendidos |
| Total vehículos atendidos | Suma de ambos tipos |
| Tiempo medio de espera | Promedio en minutos |
| Litros vendidos (Subvencionado) | Volumen total en litros |
| Litros vendidos (Internacional) | Volumen total en litros |
| Ingresos brutos (Subvencionado) | En bolivianos (Bs.) |
| Ingresos brutos (Internacional) | En bolivianos (Bs.) |
| Ingresos brutos totales | Suma total en bolivianos |
| Veces desabastecido | Número de veces que se solicitó cisterna |

#### 📋 Hoja 2: Detalle de Operaciones

Registro granular de **cada vehículo atendido** durante toda la simulación, con columnas:

`Tiempo de Salida` | `Tipo de Vehículo` | `Estación` | `Surtidor` | `Espera (min)` | `Litros` | `Monto (Bs.)`

Útil para auditoría, análisis estadístico externo o importación a otras herramientas.

#### 📊 Hoja 3: Análisis Gráfico

Contiene **dos gráficos nativos de Excel** (no imágenes, sino gráficos dinámicos que se actualizan):

| Gráfico | Tipo | Datos |
|---------|------|-------|
| **Distribución de Ingresos (Bs.)** | 🥧 Pastel | Porcentaje de ingresos de estaciones Subvencionadas vs. Internacional |
| **Volumen de Vehículos Atendidos** | 📊 Barras verticales | Cantidad de vehículos Subvencionados vs. Internacionales |

> [!TIP]
> Al abrir el Excel, haga clic derecho sobre cualquier gráfico y seleccione **"Actualizar datos"** si desea refrescar los valores. Los gráficos son completamente nativos de Excel y pueden ser copiados, modificados o incluidos en informes de PowerPoint directamente.

---

*Manual generado a partir del análisis del código fuente de la interfaz gráfica (`VentanaPrincipal`, `DialogGestorRed`, `PanelMapaRed`) del proyecto `SimulacionSurtidores v1.0-SNAPSHOT`.*
