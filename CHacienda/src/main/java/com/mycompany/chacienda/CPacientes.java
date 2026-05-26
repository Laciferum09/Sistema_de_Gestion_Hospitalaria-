/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;

/**
 * Clase CPacientes - Cola de Prioridad para turnos médicos.
 * Los pacientes con prioridad 1 (CRÍTICO) son atendidos primero.
 * Implementada con lista enlazada ordenada por prioridad.
 * El registro en archivo lo maneja CConexion.
 */
public class CPacientes {

    private Paciente frente  = null;
    private int      tamanio = 0;

    // -------------------------------------------------------
    // ENCOLAR: inserta ordenado por prioridad (1 = mayor prioridad)
    // -------------------------------------------------------
    public void encolar(Paciente nuevo) {
        if (frente == null || nuevo.prioridad < frente.prioridad) {
            nuevo.siguiente = frente;
            frente = nuevo;
        } else {
            Paciente aux = frente;
            while (aux.siguiente != null && aux.siguiente.prioridad <= nuevo.prioridad) {
                aux = aux.siguiente;
            }
            nuevo.siguiente = aux.siguiente;
            aux.siguiente   = nuevo;
        }
        tamanio++;
    }

    // -------------------------------------------------------
    // DESENCOLAR: atiende al paciente con mayor prioridad
    // -------------------------------------------------------
    public Paciente desencolar() {
        if (frente == null) return null;
        Paciente atendido  = frente;
        frente             = frente.siguiente;
        atendido.siguiente = null;
        tamanio--;
        return atendido;
    }

    // -------------------------------------------------------
    // VER EL SIGUIENTE sin desencolar
    // -------------------------------------------------------
    public Paciente verFrente() {
        return frente;
    }

    // -------------------------------------------------------
    // LISTAR COLA COMPLETA
    // -------------------------------------------------------
    public String listarCola() {
        if (frente == null) return null;
        StringBuilder sb  = new StringBuilder();
        Paciente      aux = frente;
        int           pos = 1;
        while (aux != null) {
            sb.append(pos++).append(". ").append(aux.toString()).append("\n\n");
            aux = aux.siguiente;
        }
        return sb.toString();
    }

    public boolean estaVacia()  { return frente == null; }
    public int     getTamanio() { return tamanio; }
}
