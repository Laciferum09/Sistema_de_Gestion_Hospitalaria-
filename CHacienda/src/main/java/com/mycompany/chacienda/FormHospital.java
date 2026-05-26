/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chacienda;

import javax.swing.JOptionPane;

/**
 * Clase FormHospital - Menú principal del Sistema de Gestión de Pacientes.
 * Interfaz basada en JOptionPane (librería SWING).
 * 
 * Módulos:
 *   1. Gestión de Pacientes (Árbol BST)
 *   2. Cola de Turnos Médicos (Cola de Prioridad)
 *   3. Mapa del Hospital (Grafo con Dijkstra)
 */
public class FormHospital {

    private ListaPacientes arbol    = new ListaPacientes();
    private CPacientes     cola     = new CPacientes();
    private GrafoHospital  grafo    = new GrafoHospital();
    private CHacienda      hacienda = new CHacienda();
    private CConexion      conexion = new CConexion();
    private MapaHospital   mapaAbierto = null; // referencia al mapa actual

    // -------------------------------------------------------
    // INICIO
    // -------------------------------------------------------
    public static void main(String[] args) {
        FormHospital app = new FormHospital();
        app.iniciar();
    }

    public void iniciar() {
        // Cargar datos previos usando CConexion
        conexion.cargarPacientes(arbol);
        grafo.cargarAreasPredefinidas();

        JOptionPane.showMessageDialog(null,
            "╔══════════════════════════════════╗\n" +
            "║  SISTEMA DE GESTIÓN HOSPITALARIA ║\n" +
            "║       Bienvenido al sistema      ║\n" +
            "╚══════════════════════════════════╝",
            "Hospital System", JOptionPane.INFORMATION_MESSAGE);

        menuPrincipal();
    }

    // -------------------------------------------------------
    // MENÚ PRINCIPAL
    // -------------------------------------------------------
    private void menuPrincipal() {
        String[] opciones = {
            "👤 Gestión de Pacientes",
            "🏥 Cola de Turnos Médicos",
            "🗺  Mapa del Hospital",
            "🚪 Salir"
        };
        while (true) {
            int op = JOptionPane.showOptionDialog(null,
                "Seleccione un módulo:",
                "Menú Principal",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]);

            switch (op) {
                case 0: menuPacientes(); break;
                case 1: menuCola();      break;
                case 2: menuGrafo();     break;
                case 3:
                case -1:
                    JOptionPane.showMessageDialog(null,
                        "Sistema cerrado. ¡Hasta pronto!",
                        "Salir", JOptionPane.INFORMATION_MESSAGE);
                    return;
            }
        }
    }

    // -------------------------------------------------------
    // MÓDULO 1: GESTIÓN DE PACIENTES (BST)
    // -------------------------------------------------------
    private void menuPacientes() {
        String[] opciones = {
            "➕ Agregar Paciente",
            "🔍 Buscar por Cédula",
            "🔍 Buscar por Nombre",
            "🔍 Buscar por Diagnóstico",
            "📋 Listar Todos",
            "❌ Eliminar Paciente",
            "🔙 Volver"
        };
        while (true) {
            int op = JOptionPane.showOptionDialog(null,
                "── Gestión de Pacientes ──",
                "Pacientes",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]);

            switch (op) {
                case 0: agregarPaciente();         break;
                case 1: buscarPorCedula();         break;
                case 2: buscarPorNombre();         break;
                case 3: buscarPorDiagnostico();    break;
                case 4: listarPacientes();         break;
                case 5: eliminarPaciente();        break;
                case 6: case -1: return;
            }
        }
    }

    private void agregarPaciente() {
        String cedula = pedirCedula("Agregar Paciente");
        if (cedula == null) return;

        // Buscar nombre automáticamente por cédula (TSE / Hacienda)
        JOptionPane.showMessageDialog(null,
            "⏳ Buscando nombre en el padrón electoral...",
            "Buscando", JOptionPane.INFORMATION_MESSAGE);
        String nombreAuto = hacienda.buscarNombre(cedula);
        String nombre;
        if (nombreAuto != null && !nombreAuto.equals("No encontrado")) {
            // Se encontró: confirmar o editar
            String[] opNombre = {"✅ Usar este nombre", "✏️ Escribirlo manualmente"};
            int confirmar = JOptionPane.showOptionDialog(null,
                "Se encontró en el padrón:\n\n" +
                "👤  " + nombreAuto + "\n\n¿Desea usar este nombre?",
                "Nombre encontrado", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, opNombre, opNombre[0]);
            if (confirmar == 0) {
                nombre = nombreAuto;
            } else {
                nombre = JOptionPane.showInputDialog(null,
                    "Ingrese el nombre del paciente:",
                    "Nombre manual", JOptionPane.QUESTION_MESSAGE);
            }
        } else {
            // No se encontró: pedir manualmente sin mostrar error
            nombre = JOptionPane.showInputDialog(null,
                "Ingrese el nombre del paciente:",
                "Agregar Paciente", JOptionPane.QUESTION_MESSAGE);
        }
        if (nombre == null || nombre.trim().isEmpty()) return;

        int edad = pedirEntero("Edad del paciente:", "Agregar Paciente", 0);
        if (edad == -1) return;

        String diagnostico = JOptionPane.showInputDialog(null,
            "Diagnóstico:", "Agregar Paciente", JOptionPane.QUESTION_MESSAGE);
        if (diagnostico == null || diagnostico.trim().isEmpty()) return;

        String[] prioridades = {"1 - CRÍTICO", "2 - URGENTE", "3 - NORMAL"};
        int selPrioridad = JOptionPane.showOptionDialog(null,
            "Seleccione la prioridad del caso:",
            "Prioridad", JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, prioridades, prioridades[2]);
        if (selPrioridad == -1) return;
        int prioridad = selPrioridad + 1;

        Paciente nuevo = new Paciente(cedula, nombre.trim(), edad, diagnostico.trim(), prioridad);
        arbol.insertar(nuevo);
        conexion.guardarPacientes(arbol);

        JOptionPane.showMessageDialog(null,
            "✅ Paciente registrado exitosamente:\n\n" + nuevo.toString(),
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarPorCedula() {
        String cedula = pedirCedula("Buscar Paciente");
        if (cedula == null) return;
        Paciente p = arbol.buscarPorCedula(cedula);
        if (p == null) {
            JOptionPane.showMessageDialog(null, "❌ Paciente no encontrado.", "Buscar", JOptionPane.WARNING_MESSAGE);
        } else {
            int op = JOptionPane.showConfirmDialog(null,
                "✅ Paciente encontrado:\n\n" + p.toString() + "\n\n¿Desea abrir su expediente médico?",
                "Paciente encontrado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (op == JOptionPane.YES_OPTION) menuExpediente(p);
        }
    }

    private void buscarPorNombre() {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre a buscar:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        String resultado = arbol.buscarPorNombre(nombre.trim());
        if (resultado == null) {
            JOptionPane.showMessageDialog(null, "❌ No se encontraron pacientes con ese nombre.", "Buscar", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "✅ Resultados:\n\n" + resultado, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void buscarPorDiagnostico() {
        String diag = JOptionPane.showInputDialog("Ingrese el diagnóstico a buscar:");
        if (diag == null || diag.trim().isEmpty()) return;
        String resultado = arbol.buscarPorDiagnostico(diag.trim());
        if (resultado == null) {
            JOptionPane.showMessageDialog(null, "❌ No se encontraron pacientes con ese diagnóstico.", "Buscar", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "✅ Resultados:\n\n" + resultado, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void listarPacientes() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(null, "⚠️ No hay pacientes registrados.", "Lista", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null,
            "📋 Lista de Pacientes (orden por cédula):\n\n" + arbol.listarTodos(),
            "Pacientes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarPaciente() {
        String cedula = pedirCedula("Eliminar Paciente");
        if (cedula == null) return;
        boolean eliminado = arbol.eliminar(cedula);
        if (eliminado) conexion.guardarPacientes(arbol);
        if (eliminado) {
            JOptionPane.showMessageDialog(null, "✅ Paciente eliminado correctamente.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "❌ No se encontró paciente con esa cédula.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------
    // MÓDULO 2: COLA DE TURNOS MÉDICOS
    // -------------------------------------------------------
    private void menuCola() {
        String[] opciones = {
            "➕ Agregar a la Cola",
            "✅ Atender Siguiente",
            "👁  Ver Siguiente en Cola",
            "📋 Ver Cola Completa",
            "📁 Ver Historial de Atendidos",
            "🔙 Volver"
        };
        while (true) {
            int op = JOptionPane.showOptionDialog(null,
                "── Cola de Turnos Médicos ──\nPacientes en espera: " + cola.getTamanio(),
                "Turnos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]);

            switch (op) {
                case 0: encolarPaciente();  break;
                case 1: atenderPaciente();  break;
                case 2: verFrente();        break;
                case 3: verCola();          break;
                case 4: verHistorial();     break;
                case 5: case -1: return;
            }
        }
    }

    private void encolarPaciente() {
        String cedula = pedirCedula("Agregar a Cola");
        if (cedula == null) return;

        Paciente p = arbol.buscarPorCedula(cedula.trim());
        if (p == null) {
            int crear = JOptionPane.showConfirmDialog(null,
                "Paciente no encontrado en el sistema.\n¿Desea registrarlo primero?",
                "No encontrado", JOptionPane.YES_NO_OPTION);
            if (crear == JOptionPane.YES_OPTION) agregarPaciente();
            return;
        }
        // Crear copia para la cola (sin afectar el árbol)
        Paciente enCola = new Paciente(p.cedula, p.nombre, p.edad, p.diagnostico, p.prioridad);
        cola.encolar(enCola);
        JOptionPane.showMessageDialog(null,
            "✅ Paciente agregado a la cola:\n" + p.nombre + " - " + p.getPrioridadTexto(),
            "Cola", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atenderPaciente() {
        if (cola.estaVacia()) {
            JOptionPane.showMessageDialog(null, "⚠️ La cola está vacía.", "Turnos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Paciente atendido = cola.desencolar();
        conexion.registrarAtendido(atendido);
        JOptionPane.showMessageDialog(null,
            "✅ Paciente atendido:\n\n" + atendido.toString() +
            "\n\nRegistro guardado en 'atendidos.txt'",
            "Atendido", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verFrente() {
        if (cola.estaVacia()) {
            JOptionPane.showMessageDialog(null, "⚠️ La cola está vacía.", "Turnos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Paciente p = cola.verFrente();
        JOptionPane.showMessageDialog(null,
            "👤 Siguiente paciente a atender:\n\n" + p.toString(),
            "Frente de Cola", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verCola() {
        if (cola.estaVacia()) {
            JOptionPane.showMessageDialog(null, "⚠️ La cola está vacía.", "Turnos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null,
            "📋 Cola de espera:\n\n" + cola.listarCola(),
            "Cola Completa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verHistorial() {
        String historial = conexion.leerHistorialAtendidos();
        JOptionPane.showMessageDialog(null,
            "📁 Historial de Pacientes Atendidos:\n\n" + historial,
            "Historial", JOptionPane.INFORMATION_MESSAGE);
    }

    // -------------------------------------------------------
    // MÓDULO 3: MAPA DEL HOSPITAL (GRAFO)
    // -------------------------------------------------------
    private void menuGrafo() {
        while (true) {
            boolean mapaVisible = mapaAbierto != null && mapaAbierto.isVisible();
            String[] opciones = {
                "➕ Agregar Área",
                "❌ Eliminar Área",
                "🔗 Conectar Áreas",
                "🗺  Buscar Ruta Óptima",
                "📋 Ver Mapa",
                "🔗 Ver Conexiones",
                mapaVisible ? "🗙 Cerrar Mapa" : "── (mapa cerrado) ──",
                "🔙 Volver"
            };
            int op = JOptionPane.showOptionDialog(null,
                "── Mapa del Hospital ──\n"
                + (mapaVisible ? "✅ Mapa abierto" : "⬜ Mapa cerrado"),
                "Grafo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]);

            switch (op) {
                case 0: agregarArea();   break;
                case 1: eliminarArea();  break;
                case 2: conectarAreas(); break;
                case 3: buscarRuta();    break;
                case 4: verAreas();      break;
                case 5: verConexiones(); break;
                case 6:
                    if (mapaVisible) { mapaAbierto.dispose(); mapaAbierto = null; }
                    break;
                case 7: case -1: return;
            }
        }
    }

    private void agregarArea() {
        String nombre = JOptionPane.showInputDialog(null,
            "Nombre del área a agregar:", "Agregar Área", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;

        // Mostrar guía de referencia del mapa
        JOptionPane.showMessageDialog(null,
            "Ingrese la posición del nodo en el mapa.\n\n" +
            "Referencia (el mapa mide 820 x 560 píxeles):\n" +
            "  X → horizontal :  50 = izq  |  410 = centro  |  770 = der\n" +
            "  Y → vertical   :  80 = arr  |  280 = centro  |  500 = abj\n\n" +
            "Posiciones de las áreas actuales:\n" +
            "  Emergencias (420, 90)    Recepción (210, 90)    Radiología (630, 90)\n" +
            "  Cirugía (530, 270)       UCI (420, 270)         Farmacia (210, 270)\n" +
            "  Laboratorio (630, 270)   Pediatría (210, 430)",
            "Posición en el Mapa", JOptionPane.INFORMATION_MESSAGE);

        int x = pedirEntero("Posición X del nodo (horizontal, ej: 360):", "Posición X", 10);
        if (x == -1) return;

        int y = pedirEntero("Posición Y del nodo (vertical, ej: 250):", "Posición Y", 10);
        if (y == -1) return;

        boolean ok = grafo.agregarArea(nombre.trim(), x, y);
        if (ok) {
            grafo.guardarEnArchivo();
            if (mapaAbierto != null && mapaAbierto.isVisible()) {
                mapaAbierto.dispose();
                mapaAbierto = new MapaHospital(grafo, null, null);
            }
        }
        JOptionPane.showMessageDialog(null,
            ok ? "✅ Área '" + nombre.trim() + "' agregada en (" + x + ", " + y + ")."
               : "⚠️ El área ya existe o se alcanzó el límite (máx 20).",
            "Agregar Área", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void eliminarArea() {
        String lista = grafo.listarAreas();
        if (lista == null) {
            JOptionPane.showMessageDialog(null, "No hay áreas registradas.", "Eliminar Área", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = JOptionPane.showInputDialog(null,
            "Áreas disponibles:\n\n" + lista + "\nEscriba el nombre del área a eliminar:",
            "Eliminar Área", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;

        boolean ok = grafo.eliminarArea(nombre.trim());
        if (ok) grafo.guardarEnArchivo();
        if (ok && mapaAbierto != null && mapaAbierto.isVisible()) {
            mapaAbierto.dispose();
            mapaAbierto = new MapaHospital(grafo, null, null);
        }
        JOptionPane.showMessageDialog(null,
            ok ? "✅ Área '" + nombre.trim() + "' eliminada correctamente."
               : "❌ No se encontró el área '" + nombre.trim() + "'.",
            "Eliminar Área", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void conectarAreas() {
        String origen  = JOptionPane.showInputDialog("Área de ORIGEN:");
        if (origen == null || origen.trim().isEmpty()) return;
        String destino = JOptionPane.showInputDialog("Área de DESTINO:");
        if (destino == null || destino.trim().isEmpty()) return;
        int peso = pedirEntero("Distancia/Tiempo entre áreas (número):", "Conectar Áreas", 1);
        if (peso == -1) return;
        boolean ok = grafo.conectar(origen.trim(), destino.trim(), peso);
        if (ok) grafo.guardarEnArchivo();
        JOptionPane.showMessageDialog(null,
            ok ? "✅ Áreas conectadas correctamente." : "❌ Una o ambas áreas no existen.",
            "Conexión", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void buscarRuta() {
        String origen  = JOptionPane.showInputDialog("Área de ORIGEN:");
        if (origen == null || origen.trim().isEmpty()) return;
        String destino = JOptionPane.showInputDialog("Área de DESTINO:");
        if (destino == null || destino.trim().isEmpty()) return;

        String rutaTexto = grafo.buscarRuta(origen.trim(), destino.trim());
        JOptionPane.showMessageDialog(null,
            "🗺 Ruta óptima:\n\n" + rutaTexto,
            "Ruta", JOptionPane.INFORMATION_MESSAGE);

        // Abrir mapa visual con la ruta resaltada
        if (!rutaTexto.startsWith("No existe") && !rutaTexto.startsWith("Área")) {
            // Extraer solo la línea de nodos (antes de "Distancia total")
            String lineaNodos = rutaTexto.split("\n")[0];
            String[] nodosRuta = lineaNodos.split(" → ");
            if (mapaAbierto != null && mapaAbierto.isVisible()) mapaAbierto.dispose();
            mapaAbierto = new MapaHospital(grafo, nodosRuta, rutaTexto);
        }
    }

    private void verAreas() {
        if (grafo.getNumAreas() == 0) {
            JOptionPane.showMessageDialog(null, "No hay áreas registradas.", "Áreas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (mapaAbierto != null && mapaAbierto.isVisible()) mapaAbierto.dispose();
        mapaAbierto = new MapaHospital(grafo, null, null);
    }

    private void verConexiones() {
        JOptionPane.showMessageDialog(null,
            "🔗 Conexiones del Hospital:\n\n" + grafo.listarConexiones(),
            "Conexiones", JOptionPane.INFORMATION_MESSAGE);
    }


    // -------------------------------------------------------
    // MÓDULO EXPEDIENTE MÉDICO (solo consultas)
    // -------------------------------------------------------
    private void menuExpediente(Paciente p) {
        Expediente exp = new Expediente(p.cedula);

        String[] opciones = {
            "📋 Ver Historial de Consultas",
            "➕ Agregar Consulta",
            "❌ Eliminar Consulta",
            "🔙 Volver"
        };

        while (true) {
            int op = JOptionPane.showOptionDialog(null,
                "── Expediente de: " + p.nombre + " ──\n"
                + "Cédula: " + p.cedula + "\n"
                + "Total de consultas: " + exp.getTotal(),
                "Expediente Médico",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]);

            switch (op) {
                case 0: verExpediente(p, exp);   break;
                case 1: agregarConsulta(exp);     break;
                case 2: eliminarConsulta(exp);    break;
                case 3: case -1: return;
            }
        }
    }

    private void verExpediente(Paciente p, Expediente exp) {
        if (exp.estaVacio()) {
            JOptionPane.showMessageDialog(null,
                "El expediente de " + p.nombre + " no tiene consultas aún.",
                "Expediente vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null,
            "📋 Expediente de: " + p.nombre + "  |  Cédula: " + p.cedula + "\n\n"
            + exp.listar(),
            "Expediente Médico", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarConsulta(Expediente exp) {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        String hoyStr = hoy.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String fecha = JOptionPane.showInputDialog(null,
            "Fecha de la consulta\n(Enter para usar hoy: " + hoyStr + "):",
            "Nueva Consulta", JOptionPane.QUESTION_MESSAGE);
        if (fecha == null) return;
        if (fecha.trim().isEmpty()) fecha = hoyStr;

        String medico = JOptionPane.showInputDialog(null,
            "Nombre del médico:", "Nueva Consulta", JOptionPane.QUESTION_MESSAGE);
        if (medico == null || medico.trim().isEmpty()) return;

        String motivo = JOptionPane.showInputDialog(null,
            "Motivo de la consulta:", "Nueva Consulta", JOptionPane.QUESTION_MESSAGE);
        if (motivo == null || motivo.trim().isEmpty()) return;

        String notas = JOptionPane.showInputDialog(null,
            "Notas / Diagnóstico (opcional):", "Nueva Consulta", JOptionPane.QUESTION_MESSAGE);
        if (notas == null || notas.trim().isEmpty()) notas = "-";

        EntradaExpediente entrada = new EntradaExpediente(
            fecha.trim(), medico.trim(), motivo.trim(), notas.trim());
        exp.agregar(entrada);

        JOptionPane.showMessageDialog(null,
            "✅ Consulta agregada correctamente:\n\n" + entrada.toString(),
            "Expediente actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarConsulta(Expediente exp) {
        if (exp.estaVacio()) {
            JOptionPane.showMessageDialog(null,
                "El expediente no tiene consultas.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numStr = JOptionPane.showInputDialog(null,
            "Hay " + exp.getTotal() + " consulta(s).\n"
            + "Ingrese el número de la consulta a eliminar\n"
            + "(ábralo primero para ver los números):",
            "Eliminar Consulta", JOptionPane.QUESTION_MESSAGE);
        if (numStr == null || numStr.trim().isEmpty()) return;
        if (numStr == null || !numStr.trim().matches("[0-9]+")) {
            if (numStr != null)
                JOptionPane.showMessageDialog(null, "⚠️ Solo se permiten números.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int num = Integer.parseInt(numStr.trim());
        boolean ok = exp.eliminar(num);
        JOptionPane.showMessageDialog(null,
            ok ? "✅ Consulta eliminada." : "❌ Número inválido. Debe ser entre 1 y " + (exp.getTotal() + 1),
            "Eliminar", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════
    // MÉTODOS DE VALIDACIÓN
    // ═══════════════════════════════════════════════════════

    /**
     * Pide una cédula con mínimo 7 dígitos, solo números.
     * Repite hasta que sea válida o el usuario cancele.
     */
    private String pedirCedula(String titulo) {
        while (true) {
            String valor = JOptionPane.showInputDialog(null,
                "Ingrese el número de cédula (mínimo 7 dígitos, solo números):",
                titulo, JOptionPane.QUESTION_MESSAGE);
            if (valor == null) return null;
            valor = valor.trim();
            if (valor.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ La cédula no puede estar vacía.", "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (!valor.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ La cédula solo puede contener números (0-9).", "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (valor.length() < 7) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ La cédula debe tener al menos 7 dígitos.\nIngresaste: " + valor.length() + " dígito(s).",
                    "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            return valor;
        }
    }

    /**
     * Pide un número entero positivo.
     * Repite hasta que sea válido o el usuario cancele.
     */
    private int pedirEntero(String mensaje, String titulo, int minimo) {
        while (true) {
            String valor = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
            if (valor == null) return -1;
            valor = valor.trim();
            if (valor.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ Este campo no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (!valor.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ Solo se permiten números (0-9). No ingrese letras ni símbolos.",
                    "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            int num = Integer.parseInt(valor);
            if (num < minimo) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ El valor mínimo permitido es " + minimo + ".",
                    "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            return num;
        }
    }

}