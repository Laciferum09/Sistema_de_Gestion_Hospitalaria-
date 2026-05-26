/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Expediente - Lista enlazada de consultas médicas de un paciente.
 * Se guarda en: expediente_[cedula].txt
 */
public class Expediente {

    private EntradaExpediente cabeza = null;
    private int               total  = 0;
    private final String      cedula;

    public Expediente(String cedula) {
        this.cedula = cedula;
        cargarDesdeArchivo();
    }

    // -------------------------------------------------------
    // AGREGAR (más reciente al inicio)
    // -------------------------------------------------------
    public void agregar(EntradaExpediente nueva) {
        nueva.siguiente = cabeza;
        cabeza          = nueva;
        total++;
        guardarEnArchivo();
    }

    // -------------------------------------------------------
    // ELIMINAR POR NÚMERO (1-based)
    // -------------------------------------------------------
    public boolean eliminar(int numero) {
        if (cabeza == null || numero < 1 || numero > total) return false;

        if (numero == 1) {
            cabeza = cabeza.siguiente;
            total--;
            guardarEnArchivo();
            return true;
        }
        EntradaExpediente aux = cabeza;
        for (int i = 1; i < numero - 1; i++) {
            if (aux.siguiente == null) return false;
            aux = aux.siguiente;
        }
        aux.siguiente = aux.siguiente.siguiente;
        total--;
        guardarEnArchivo();
        return true;
    }

    // -------------------------------------------------------
    // LISTAR TODAS
    // -------------------------------------------------------
    public String listar() {
        if (cabeza == null) return null;
        StringBuilder     sb  = new StringBuilder();
        EntradaExpediente aux = cabeza;
        int               num = 1;
        while (aux != null) {
            sb.append("──────────────────────────────\n");
            sb.append("  Consulta #").append(num++).append("\n");
            sb.append(aux.toString()).append("\n");
            aux = aux.siguiente;
        }
        sb.append("──────────────────────────────");
        return sb.toString();
    }

    public boolean estaVacio() { return cabeza == null; }
    public int     getTotal()  { return total; }

    // -------------------------------------------------------
    // GUARDAR EN ARCHIVO
    // -------------------------------------------------------
    private void guardarEnArchivo() {
        String archivo = "expediente_" + cedula + ".txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            EntradaExpediente aux = cabeza;
            while (aux != null) {
                bw.write(aux.toArchivo());
                bw.newLine();
                aux = aux.siguiente;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar expediente: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CARGAR DESDE ARCHIVO
    // -------------------------------------------------------
    private void cargarDesdeArchivo() {
        String archivo = "expediente_" + cedula + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            ArrayList<String> lineas = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null)
                if (!linea.trim().isEmpty()) lineas.add(linea);

            // Insertar al revés para que el más reciente quede primero
            for (int i = lineas.size() - 1; i >= 0; i--) {
                String[] p = lineas.get(i).split("\\|", 4);
                if (p.length == 4) {
                    EntradaExpediente e = new EntradaExpediente(p[0], p[1], p[2], p[3]);
                    e.siguiente = cabeza;
                    cabeza      = e;
                    total++;
                }
            }
        } catch (IOException e) {
            // Archivo no existe aún
        }
    }
}
