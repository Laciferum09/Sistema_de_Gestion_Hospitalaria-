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

/**
 * Clase GrafoHospital - Grafo dirigido para modelar áreas del hospital.
 * Permite agregar áreas, conectarlas y buscar rutas con BFS.
 */
public class GrafoHospital {

    private static final int MAX_NODOS  = 20;
    private static final int INFINITO   = 999;
    private static final String ARCHIVO = "grafo.txt";

    private String[] areas    = new String[MAX_NODOS];
    private int[][]  matriz   = new int[MAX_NODOS][MAX_NODOS];
    private int[]    posX     = new int[MAX_NODOS];
    private int[]    posY     = new int[MAX_NODOS];
    private int      numAreas = 0;

    public GrafoHospital() {
        // Inicializar matriz con INFINITO (sin conexión)
        for (int i = 0; i < MAX_NODOS; i++)
            for (int j = 0; j < MAX_NODOS; j++)
                matriz[i][j] = (i == j) ? 0 : INFINITO;
    }

    // -------------------------------------------------------
    // AGREGAR ÁREA
    // -------------------------------------------------------
    public boolean agregarArea(String nombre) {
        return agregarArea(nombre, -1, -1); // sin posición manual
    }

    public boolean agregarArea(String nombre, int x, int y) {
        if (numAreas >= MAX_NODOS) return false;
        if (buscarIndice(nombre) != -1) return false; // ya existe
        areas[numAreas] = nombre;
        posX[numAreas]  = x;
        posY[numAreas]  = y;
        numAreas++;
        return true;
    }

    public int getPosX(int i) { return posX[i]; }
    public int getPosY(int i) { return posY[i]; }

    // -------------------------------------------------------
    // CONECTAR DOS ÁREAS (con peso = distancia/tiempo)
    // -------------------------------------------------------
    public boolean conectar(String origen, String destino, int peso) {
        int i = buscarIndice(origen);
        int j = buscarIndice(destino);
        if (i == -1 || j == -1) return false;
        matriz[i][j] = peso;
        return true;
    }

    // -------------------------------------------------------
    // BUSCAR RUTA ÓPTIMA: Dijkstra simplificado
    // -------------------------------------------------------
    public String buscarRuta(String origen, String destino) {
        int ini = buscarIndice(origen);
        int fin = buscarIndice(destino);
        if (ini == -1) return "Área de origen no encontrada: " + origen;
        if (fin == -1) return "Área de destino no encontrada: " + destino;

        int[]     dist    = new int[numAreas];
        int[]     prev    = new int[numAreas];
        boolean[] visit   = new boolean[numAreas];

        for (int i = 0; i < numAreas; i++) { dist[i] = INFINITO; prev[i] = -1; }
        dist[ini] = 0;

        for (int iter = 0; iter < numAreas; iter++) {
            // Nodo no visitado con menor distancia
            int u = -1;
            for (int i = 0; i < numAreas; i++)
                if (!visit[i] && (u == -1 || dist[i] < dist[u])) u = i;
            if (u == -1 || dist[u] == INFINITO) break;
            visit[u] = true;

            for (int v = 0; v < numAreas; v++) {
                if (matriz[u][v] != INFINITO && dist[u] + matriz[u][v] < dist[v]) {
                    dist[v] = dist[u] + matriz[u][v];
                    prev[v] = u;
                }
            }
        }

        if (dist[fin] == INFINITO)
            return "No existe ruta entre " + origen + " y " + destino;

        // Reconstruir camino
        StringBuilder camino = new StringBuilder();
        int actual = fin;
        String[] ruta = new String[numAreas];
        int len = 0;
        while (actual != -1) { ruta[len++] = areas[actual]; actual = prev[actual]; }
        // Invertir
        for (int i = len - 1; i >= 0; i--) {
            camino.append(ruta[i]);
            if (i > 0) camino.append(" → ");
        }
        camino.append("\nDistancia total: ").append(dist[fin]).append(" unidades");
        return camino.toString();
    }

    // -------------------------------------------------------
    // LISTAR ÁREAS Y CONEXIONES
    // -------------------------------------------------------
    public String listarAreas() {
        if (numAreas == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numAreas; i++) {
            sb.append("• ").append(areas[i]).append("\n");
        }
        return sb.toString();
    }

    public String listarConexiones() {
        if (numAreas == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numAreas; i++) {
            for (int j = 0; j < numAreas; j++) {
                if (matriz[i][j] != INFINITO && matriz[i][j] != 0) {
                    sb.append(areas[i]).append(" → ").append(areas[j])
                      .append(" (distancia: ").append(matriz[i][j]).append(")\n");
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "No hay conexiones registradas.";
    }

    // -------------------------------------------------------
    // CARGAR: desde archivo si existe, si no carga predefinidas
    // -------------------------------------------------------
    public void cargarAreasPredefinidas() {
        if (new java.io.File(ARCHIVO).exists()) {
            cargarDesdeArchivo();
        } else {
            cargarPredefinidas();
            guardarEnArchivo();
        }
    }

    private void cargarPredefinidas() {
        agregarArea("Emergencias",  420,  90);
        agregarArea("Recepción",    210,  90);
        agregarArea("Radiología",   630,  90);
        agregarArea("Cirugía",      530, 270);
        agregarArea("UCI",          420, 270);
        agregarArea("Farmacia",     210, 270);
        agregarArea("Laboratorio",  630, 270);
        agregarArea("Pediatría",    210, 430);

        conectar("Recepción",    "Emergencias", 2);
        conectar("Recepción",    "Farmacia",    3);
        conectar("Recepción",    "Radiología",  4);
        conectar("Emergencias",  "UCI",         1);
        conectar("Emergencias",  "Cirugía",     2);
        conectar("Radiología",   "Laboratorio", 2);
        conectar("Cirugía",      "UCI",         1);
        conectar("UCI",          "Farmacia",    3);
        conectar("Farmacia",     "Pediatría",   2);
        conectar("Laboratorio",  "Cirugía",     3);
    }

    // -------------------------------------------------------
    // GUARDAR EN ARCHIVO
    // Formato: AREA|nombre
    //          CONEXION|origen|destino|peso
    // -------------------------------------------------------
    public void guardarEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            // Guardar áreas
            for (int i = 0; i < numAreas; i++) {
                bw.write("AREA|" + areas[i] + "|" + posX[i] + "|" + posY[i]);
                bw.newLine();
            }
            // Guardar conexiones
            for (int i = 0; i < numAreas; i++) {
                for (int j = 0; j < numAreas; j++) {
                    if (matriz[i][j] != INFINITO && matriz[i][j] != 0) {
                        bw.write("CONEXION|" + areas[i] + "|" + areas[j] + "|" + matriz[i][j]);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar grafo: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CARGAR DESDE ARCHIVO
    // -------------------------------------------------------
    private void cargarDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|");
                if (p.length >= 4 && p[0].equals("AREA")) {
                    agregarArea(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]));
                } else if (p.length == 2 && p[0].equals("AREA")) {
                    agregarArea(p[1]); // compatibilidad archivos viejos sin posición
                } else if (p.length == 4 && p[0].equals("CONEXION")) {
                    conectar(p[1], p[2], Integer.parseInt(p[3]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar grafo: " + e.getMessage());
        }
    }

    private int buscarIndice(String nombre) {
        for (int i = 0; i < numAreas; i++)
            if (areas[i].equalsIgnoreCase(nombre)) return i;
        return -1;
    }

    // -------------------------------------------------------
    // ELIMINAR ÁREA (y todas sus conexiones)
    // -------------------------------------------------------
    public boolean eliminarArea(String nombre) {
        int idx = buscarIndice(nombre);
        if (idx == -1) return false;

        // Desplazar áreas hacia arriba
        for (int i = idx; i < numAreas - 1; i++) {
            areas[i] = areas[i + 1];
            posX[i]  = posX[i + 1];
            posY[i]  = posY[i + 1];
        }
        areas[numAreas - 1] = null;
        posX[numAreas - 1]  = -1;
        posY[numAreas - 1]  = -1;

        // Reconstruir matriz sin esa fila y columna
        for (int i = idx; i < numAreas - 1; i++) {
            for (int j = 0; j < numAreas; j++) {
                matriz[i][j] = matriz[i + 1][j];
            }
        }
        for (int j = idx; j < numAreas - 1; j++) {
            for (int i = 0; i < numAreas; i++) {
                matriz[i][j] = matriz[i][j + 1];
            }
        }
        // Limpiar última fila y columna
        for (int i = 0; i < MAX_NODOS; i++) {
            matriz[numAreas - 1][i] = INFINITO;
            matriz[i][numAreas - 1] = INFINITO;
        }
        matriz[numAreas - 1][numAreas - 1] = 0;
        numAreas--;
        return true;
    }

    public int getNumAreas() { return numAreas; }
    public String getArea(int i) { return areas[i]; }

    // Devuelve el peso entre dos nodos por índice (para MapaHospital)
    public int getPeso(int i, int j) {
        if (i < 0 || j < 0 || i >= numAreas || j >= numAreas) return INFINITO;
        return matriz[i][j];
    }

    // Devuelve el índice de un área por nombre (para MapaHospital)
    public int getIndice(String nombre) {
        return buscarIndice(nombre);
    }
}
