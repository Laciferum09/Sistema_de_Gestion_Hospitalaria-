# Sistema de Gestión Hospitalaria 
Proyecto de una base de datos de un hospital manejo de pacientes, mapeado con grafos y conexiones de api de hacienda para busqueda por cedula 

# 🏥 Sistema de Gestión Hospitalaria — CHacienda

Sistema de gestión de pacientes desarrollado en **Java con Swing**, que integra tres módulos principales: administración de pacientes, cola de turnos médicos y mapa visual del hospital. Proyecto académico desarrollado de forma individual.

---

## 📋 Descripción

El sistema permite a un hospital gestionar el flujo de pacientes desde su registro hasta su atención, con visualización gráfica del layout del edificio. Toda la información se persiste automáticamente en archivos `.txt`, lo que permite continuar desde donde se dejó en cada sesión.

La interfaz corre sobre **JOptionPane** (Swing), sin necesidad de instalar nada más allá del JDK. Al iniciar, el sistema carga los datos guardados automáticamente.

### Características principales

- **Registro de pacientes** con búsqueda automática de nombre por cédula costarricense vía API del TSE (GoMeta) y Hacienda como respaldo.
- **Tres búsquedas**: por cédula (BST O(log n)), por nombre y por diagnóstico (recorrido inorden).
- **Expediente médico por paciente**: historial de consultas persistido en `expediente_[cedula].txt`.
- **Cola de turnos** con prioridad: CRÍTICO → URGENTE → NORMAL, usando lista enlazada ordenada.
- **Historial de atendidos** tabulado con fecha y hora guardado en `atendidos.txt`.
- **Mapa visual del hospital** en ventana Swing con nodos, flechas dirigidas y ruta óptima resaltada.
- **Algoritmo de Dijkstra** para encontrar la ruta más corta entre dos áreas del hospital.

---

## 🧱 Estructuras de Datos implementadas

Todas las estructuras están implementadas **desde cero**, sin usar `ArrayList`, `LinkedList`, `TreeMap` ni ninguna colección de Java para la lógica principal.

### 1. Árbol Binario de Búsqueda — `ListaPacientes`

Almacena pacientes ordenados por cédula. Permite inserción, búsqueda O(log n), eliminación con sucesor inorden y listado en orden ascendente.

```
         119500608
        /          \
   119200100     119900553
```

### 2. Cola de Prioridad — `CPacientes`

Lista enlazada ordenada que garantiza que el paciente más crítico (prioridad 1) siempre quede al frente. Inserción O(n) ordenada, extracción O(1).

```
[CRÍTICO] → [URGENTE] → [URGENTE] → [NORMAL] → null
```

### 3. Lista Enlazada — `Expediente` + `EntradaExpediente`

Historial de consultas médicas de un paciente. Inserta al inicio (más reciente primero), permite eliminar por número y se persiste en archivo automáticamente.

### 4. Grafo Dirigido con Pesos — `GrafoHospital`

Representado mediante **matriz de adyacencia** de 20×20. Soporta agregar/eliminar áreas, conectar con peso y ejecutar Dijkstra para ruta óptima. Las posiciones X/Y de los nodos se guardan para el mapa visual.

---

## 🗺️ Módulos del sistema

### Módulo 1 — Gestión de Pacientes

| Función | Descripción |
|---|---|
| Agregar paciente | Busca el nombre por cédula en el padrón TSE automáticamente |
| Buscar por cédula | Búsqueda O(log n) en el BST |
| Buscar por nombre | Recorrido completo del árbol con coincidencia parcial |
| Buscar por diagnóstico | Igual al anterior, sobre el campo diagnóstico |
| Listar todos | Inorden — muestra pacientes en orden de cédula |
| Eliminar | BST con reemplazo por sucesor inorden |
| Expediente médico | Lista enlazada de consultas por paciente |

### Módulo 2 — Cola de Turnos Médicos

| Función | Descripción |
|---|---|
| Agregar a cola | Encola buscando al paciente en el BST; ofrece registrarlo si no existe |
| Atender siguiente | Desencola el de mayor prioridad y registra en `atendidos.txt` |
| Ver frente | Muestra el próximo sin desencolar |
| Ver cola completa | Lista todos los pacientes en espera con su posición y prioridad |
| Historial de atendidos | Lee y muestra `atendidos.txt` con formato tabular |

### Módulo 3 — Mapa del Hospital

| Función | Descripción |
|---|---|
| Agregar área | Nuevo nodo con posición X/Y en el mapa |
| Eliminar área | Remueve nodo y reconstruye la matriz de adyacencia |
| Conectar áreas | Define una arista dirigida con peso (distancia/tiempo) |
| Buscar ruta óptima | Dijkstra; muestra resultado en texto y en el mapa visual |
| Ver mapa | Ventana Swing con nodos circulares, flechas y leyenda |

---

## 📁 Estructura del proyecto

```
CHacienda/
├── src/main/java/com/mycompany/chacienda/
│   │
│   ├── FormHospital.java        # Menú principal y flujo de la aplicación (main)
│   │
│   ├── Paciente.java            # Nodo base: datos + punteros BST y lista enlazada
│   ├── ListaPacientes.java      # Árbol BST: insertar, buscar, eliminar, listar
│   ├── CPacientes.java          # Cola de prioridad con lista enlazada ordenada
│   │
│   ├── EntradaExpediente.java   # Nodo de lista enlazada para consultas médicas
│   ├── Expediente.java          # Lista enlazada de consultas + persistencia en archivo
│   │
│   ├── GrafoHospital.java       # Grafo dirigido (matriz de adyacencia) + Dijkstra
│   ├── MapaHospital.java        # Ventana Swing: dibujo del grafo con flechas y ruta
│   │
│   ├── CHacienda.java           # Cliente HTTP: busca nombre por cédula en TSE/Hacienda
│   └── CConexion.java           # Persistencia: leer/guardar pacientes y atendidos
│
├── pacientes.txt                # Base de datos de pacientes (BST serializado inorden)
├── atendidos.txt                # Historial tabulado de pacientes atendidos
├── expediente_[cedula].txt      # Historial de consultas por paciente
├── grafo.txt                    # Áreas y conexiones del mapa del hospital
└── pom.xml                      # Maven — sin dependencias externas
```

---

## 🔌 Integración con APIs externas

`CHacienda.java` consulta automáticamente el nombre del paciente por su número de cédula costarricense:

1. **GoMeta** (`apis.gometa.org/cedulas/{cedula}`) — padrón del TSE, primera opción.
2. **Hacienda** (`api.hacienda.go.cr/fe/ae?identificacion={cedula}`) — respaldo si GoMeta falla.

Si ambas APIs fallan o la cédula no existe, el sistema pide el nombre manualmente sin mostrar un error al usuario. El parseo del JSON se hace manualmente con búsqueda de substrings, sin librerías externas.

---

## ▶️ Cómo ejecutar

### Requisitos

- Java JDK 8 o superior
- Maven (o abrir directamente en NetBeans/IntelliJ)

### Con Maven

```bash
cd CHacienda
mvn compile
mvn exec:java -Dexec.mainClass="com.mycompany.chacienda.FormHospital"
```

### Con NetBeans

Abrir el proyecto (`CHacienda/`) y ejecutar con `Shift+F6` sobre `FormHospital.java`.

> Los archivos `.txt` de datos se crean automáticamente en el directorio de trabajo al primer uso.

---

## 💾 Persistencia de datos

| Archivo | Contenido | Formato |
|---|---|---|
| `pacientes.txt` | Árbol BST serializado inorden | `cedula;nombre;edad;diagnostico;prioridad` |
| `atendidos.txt` | Historial con fecha/hora | Tabla de texto con columnas fijas |
| `expediente_[cedula].txt` | Consultas médicas del paciente | `fecha\|medico\|motivo\|notas` |
| `grafo.txt` | Áreas y conexiones del hospital | `AREA\|nombre\|x\|y` y `CONEXION\|origen\|destino\|peso` |

---

## 👤 Créditos

Proyecto académico desarrollado individualmente para el curso de **Estructuras de Datos y Algoritmo**.

Todas las estructuras de datos (BST, cola de prioridad, lista enlazada, grafo con Dijkstra) están implementadas desde cero sin usar las colecciones de Java (`ArrayList`, `LinkedList`, etc.).
