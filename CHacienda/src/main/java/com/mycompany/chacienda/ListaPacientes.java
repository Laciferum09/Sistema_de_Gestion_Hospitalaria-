/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;

/**
 * Clase ListaPacientes - Árbol Binario de Búsqueda (BST) de pacientes.
 * Permite insertar, buscar, eliminar y listar pacientes.
 * La persistencia en archivo la maneja CConexion.
 */
public class ListaPacientes {

    private Paciente raiz = null;

    // -------------------------------------------------------
    // INSERTAR
    // -------------------------------------------------------
    public void insertar(Paciente nuevo) {
        raiz = insertarRec(raiz, nuevo);
    }

    private Paciente insertarRec(Paciente nodo, Paciente nuevo) {
        if (nodo == null) return nuevo;
        int cmp = nuevo.cedula.compareTo(nodo.cedula);
        if (cmp < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, nuevo);
        } else if (cmp > 0) {
            nodo.derecho = insertarRec(nodo.derecho, nuevo);
        } else {
            // Cédula duplicada: actualizar datos
            nodo.nombre      = nuevo.nombre;
            nodo.edad        = nuevo.edad;
            nodo.diagnostico = nuevo.diagnostico;
            nodo.prioridad   = nuevo.prioridad;
        }
        return nodo;
    }

    // -------------------------------------------------------
    // BUSCAR POR CÉDULA
    // -------------------------------------------------------
    public Paciente buscarPorCedula(String cedula) {
        return buscarCedRec(raiz, cedula);
    }

    private Paciente buscarCedRec(Paciente nodo, String cedula) {
        if (nodo == null) return null;
        int cmp = cedula.compareTo(nodo.cedula);
        if (cmp == 0) return nodo;
        if (cmp < 0)  return buscarCedRec(nodo.izquierdo, cedula);
        return buscarCedRec(nodo.derecho, cedula);
    }

    // -------------------------------------------------------
    // BUSCAR POR NOMBRE
    // -------------------------------------------------------
    public String buscarPorNombre(String nombre) {
        StringBuilder sb = new StringBuilder();
        buscarNomRec(raiz, nombre.toLowerCase(), sb);
        return sb.length() > 0 ? sb.toString() : null;
    }

    private void buscarNomRec(Paciente nodo, String nombre, StringBuilder sb) {
        if (nodo == null) return;
        if (nodo.nombre.toLowerCase().contains(nombre))
            sb.append(nodo.toString()).append("\n");
        buscarNomRec(nodo.izquierdo, nombre, sb);
        buscarNomRec(nodo.derecho,   nombre, sb);
    }

    // -------------------------------------------------------
    // BUSCAR POR DIAGNÓSTICO
    // -------------------------------------------------------
    public String buscarPorDiagnostico(String diagnostico) {
        StringBuilder sb = new StringBuilder();
        buscarDiagRec(raiz, diagnostico.toLowerCase(), sb);
        return sb.length() > 0 ? sb.toString() : null;
    }

    private void buscarDiagRec(Paciente nodo, String diag, StringBuilder sb) {
        if (nodo == null) return;
        if (nodo.diagnostico.toLowerCase().contains(diag))
            sb.append(nodo.toString()).append("\n");
        buscarDiagRec(nodo.izquierdo, diag, sb);
        buscarDiagRec(nodo.derecho,   diag, sb);
    }

    // -------------------------------------------------------
    // ELIMINAR POR CÉDULA
    // -------------------------------------------------------
    public boolean eliminar(String cedula) {
        if (buscarPorCedula(cedula) == null) return false;
        raiz = eliminarRec(raiz, cedula);
        return true;
    }

    private Paciente eliminarRec(Paciente nodo, String cedula) {
        if (nodo == null) return null;
        int cmp = cedula.compareTo(nodo.cedula);
        if (cmp < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, cedula);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, cedula);
        } else {
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;
            Paciente sucesor = minimoNodo(nodo.derecho);
            nodo.cedula      = sucesor.cedula;
            nodo.nombre      = sucesor.nombre;
            nodo.edad        = sucesor.edad;
            nodo.diagnostico = sucesor.diagnostico;
            nodo.prioridad   = sucesor.prioridad;
            nodo.derecho     = eliminarRec(nodo.derecho, sucesor.cedula);
        }
        return nodo;
    }

    private Paciente minimoNodo(Paciente nodo) {
        while (nodo.izquierdo != null) nodo = nodo.izquierdo;
        return nodo;
    }

    // -------------------------------------------------------
    // LISTAR TODOS (en orden por cédula)
    // -------------------------------------------------------
    public String listarTodos() {
        StringBuilder sb = new StringBuilder();
        listarRec(raiz, sb);
        return sb.length() > 0 ? sb.toString() : null;
    }

    private void listarRec(Paciente nodo, StringBuilder sb) {
        if (nodo == null) return;
        listarRec(nodo.izquierdo, sb);
        sb.append(nodo.toString()).append("\n\n");
        listarRec(nodo.derecho, sb);
    }

    public boolean  estaVacio() { return raiz == null; }
    public Paciente getRaiz()   { return raiz; }
}
