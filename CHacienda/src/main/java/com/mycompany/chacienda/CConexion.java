/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chacienda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase CConexion - Maneja toda la persistencia del sistema.
 * Guarda y carga pacientes desde archivos .txt
 * También registra el historial de pacientes atendidos de forma tabulada.
 */
public class CConexion {

    private static final String ARCHIVO_PACIENTES  = "pacientes.txt";
    private static final String ARCHIVO_ATENDIDOS  = "atendidos.txt";
    private static final String SEPARADOR          = ";";

    // -------------------------------------------------------
    // GUARDAR ÁRBOL DE PACIENTES EN ARCHIVO
    // -------------------------------------------------------
    /**
     * Guarda un arreglo de pacientes al archivo pacientes.txt
     * Se llama desde ListaPacientes cada vez que hay un cambio.
     */
    public boolean guardarPacientes(ListaPacientes arbol) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PACIENTES))) {
            escribirNodo(arbol.getRaiz(), bw);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar pacientes: " + e.getMessage());
            return false;
        }
    }

    private void escribirNodo(Paciente nodo, BufferedWriter bw) throws IOException {
        if (nodo == null) return;
        escribirNodo(nodo.izquierdo, bw);
        bw.write(nodo.cedula    + SEPARADOR
               + nodo.nombre   + SEPARADOR
               + nodo.edad     + SEPARADOR
               + nodo.diagnostico + SEPARADOR
               + nodo.prioridad);
        bw.newLine();
        escribirNodo(nodo.derecho, bw);
    }

    // -------------------------------------------------------
    // CARGAR PACIENTES DESDE ARCHIVO AL ÁRBOL
    // -------------------------------------------------------
    /**
     * Lee pacientes.txt e inserta cada registro en el árbol BST.
     * Se llama al iniciar el programa.
     */
    public void cargarPacientes(ListaPacientes arbol) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_PACIENTES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR);
                if (campos.length == 5) {
                    try {
                        Paciente p = new Paciente(
                            campos[0],
                            campos[1],
                            Integer.parseInt(campos[2]),
                            campos[3],
                            Integer.parseInt(campos[4])
                        );
                        arbol.insertar(p);
                    } catch (NumberFormatException ignored) {
                        // Línea mal formada, se ignora
                    }
                }
            }
        } catch (IOException e) {
            // El archivo aún no existe, se creará al primer guardado
        }
    }

    // -------------------------------------------------------
    // REGISTRAR PACIENTE ATENDIDO
    // -------------------------------------------------------
    /**
     * Agrega al archivo atendidos.txt el registro del paciente
     * que acaba de ser atendido, con fecha y hora tabulados.
     */
    public boolean registrarAtendido(Paciente p) {
        boolean archivoNuevo = !existeArchivo(ARCHIVO_ATENDIDOS);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ATENDIDOS, true))) {
            // Escribir encabezado solo si el archivo es nuevo
            if (archivoNuevo) {
                bw.write(encabezadoAtendidos());
                bw.newLine();
                bw.write("-".repeat(120));
                bw.newLine();
            }

            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            bw.write(String.format("%-15s %-30s %-6s %-30s %-10s %-22s%n",
                p.cedula,
                p.nombre,
                p.edad,
                p.diagnostico,
                p.getPrioridadTexto(),
                ahora.format(fmt)
            ));
            return true;
        } catch (IOException e) {
            System.err.println("Error al registrar atendido: " + e.getMessage());
            return false;
        }
    }

    private String encabezadoAtendidos() {
        return String.format("%-15s %-30s %-6s %-30s %-10s %-22s",
            "CÉDULA", "NOMBRE", "EDAD", "DIAGNÓSTICO", "PRIORIDAD", "FECHA/HORA ATENCIÓN");
    }

    // -------------------------------------------------------
    // LEER HISTORIAL DE ATENDIDOS
    // -------------------------------------------------------
    /**
     * Retorna el contenido completo del archivo atendidos.txt como String.
     * Útil para mostrarlo en pantalla con JOptionPane.
     */
    public String leerHistorialAtendidos() {
        if (!existeArchivo(ARCHIVO_ATENDIDOS)) {
            return "No hay registros de pacientes atendidos aún.";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ATENDIDOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            return "Error al leer el historial: " + e.getMessage();
        }
        return sb.length() > 0 ? sb.toString() : "El historial está vacío.";
    }

    // -------------------------------------------------------
    // UTILIDADES
    // -------------------------------------------------------
    private boolean existeArchivo(String ruta) {
        return new java.io.File(ruta).exists();
    }

    public String getNombreArchivoPacientes() { return ARCHIVO_PACIENTES; }
    public String getNombreArchivoAtendidos() { return ARCHIVO_ATENDIDOS; }
}
